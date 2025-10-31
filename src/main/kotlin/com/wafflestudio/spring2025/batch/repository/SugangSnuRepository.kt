package com.wafflestudio.spring2025.batch.repository

import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

interface SugangSnuRepository {
    fun downloadLectureXls(
        year: String,
        semester: Semester,
    ): ByteArray
}

@Repository
class SugangSnuRepositoryImpl(
    private val webClient: WebClient,
) : SugangSnuRepository {
    /**
     * 수강신청 사이트에서 강의 목록 XLS 파일 다운로드
     */
    override fun downloadLectureXls(
        year: String,
        semester: Semester,
    ): ByteArray {
        val semesterCode =
            when (semester) {
                Semester.SPRING -> "U000200001U000300001" // 1학기
                Semester.SUMMER -> "U000200001U000300002" // 여름학기
                Semester.AUTUMN -> "U000200002U000300001" // 2학기
                Semester.WINTER -> "U000200002U000300002" // 겨울학기
            }

        // 실제 브라우저가 전송하는 모든 파라미터를 포함해야 합니다.
        val formData =
            LinkedMultiValueMap<String, String>().apply {
                add("workType", "EX")
                add("pageNo", "1")
                add("srchOpenSchyy", year)
                add("srchOpenShtm", semesterCode)
                add("srchSbjtNm", "")
                add("srchSbjtCd", "")
                add("seeMore", "")
                add("srchCptnCorsFg", "")
                add("srchOpenShyr", "")
                add("srchOpenUpSbjtFldCd", "")
                add("srchOpenSbjtFldCd", "")
                add("srchOpenUpDeptCd", "")
                add("srchOpenDeptCd", "")
                add("srchOpenMjCd", "")
                add("srchOpenSubmattCorsFg", "")
                add("srchOpenSubmattFgCd1", "")
                add("srchOpenSubmattFgCd2", "")
                add("srchOpenSubmattFgCd3", "")
                add("srchOpenSubmattFgCd4", "")
                add("srchOpenSubmattFgCd5", "")
                add("srchOpenSubmattFgCd6", "")
                add("srchOpenSubmattFgCd7", "")
                add("srchOpenSubmattFgCd8", "")
                add("srchOpenSubmattFgCd9", "")
                add("srchExcept", "")
                add("srchOpenPntMin", "")
                add("srchOpenPntMax", "")
                add("srchCamp", "")
                add("srchBdNo", "")
                add("srchProfNm", "")
                add("srchOpenSbjtTmNm", "")
                add("srchOpenSbjtDayNm", "")
                add("srchOpenSbjtTm", "")
                add("srchOpenSbjtNm", "")
                add("srchTlsnAplyCapaCntMin", "")
                add("srchTlsnAplyCapaCntMax", "")
                add("srchLsnProgType", "")
                add("srchTlsnRcntMin", "")
                add("srchTlsnRcntMax", "")
                add("srchMrksGvMthd", "")
                add("srchIsEngSbjt", "")
                add("srchMrksApprMthdChgPosbYn", "")
                add("srchIsPendingCourse", "")
                add("srchGenrlRemoteLtYn", "")
                add("srchLanguage", "ko")
                add("srchCurrPage", "1")
                add("srchPageSize", "9999")
            }

        // 요청 URL
        val targetUrl = "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceExcel.action"
        // Referer 헤더 값
        val refererUrl = "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceSrch.action"

        // .retrieve() 대신 .exchangeToMono()를 사용합니다.
        val responseMono: Mono<ByteArray> =
            webClient
                .post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                // User-Agent, Referer 헤더를 추가하여 일반 브라우저처럼 위장
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0",
                ).header("Referer", refererUrl)
                .bodyValue(formData)
                // ✅ [수정됨] .retrieve() 대신 .exchangeToMono() 사용
                .exchangeToMono { response ->

                    // 1. 2xx 성공 상태 코드 처리
                    if (response.statusCode().is2xxSuccessful) {
                        val contentType =
                            response
                                .headers()
                                .contentType()
                                .orElse(MediaType.APPLICATION_OCTET_STREAM) // 기본값

                        // 2. [오류 케이스] 2xx 성공이지만, 내용물이 HTML인 경우
                        if (contentType.isCompatibleWith(MediaType.TEXT_HTML)) {
                            // HTML 본문을 읽어서 예외 메시지에 포함
                            response
                                .bodyToMono(String::class.java)
                                .flatMap { htmlBody ->
                                    // 본문을 소모해야 함
                                    Mono.error<ByteArray>( // Mono<ByteArray> 타입과 맞춤
                                        RuntimeException("서버가 Excel이 아닌 HTML을 반환했습니다. 파라미터 오류일 수 있습니다. 응답: $htmlBody"),
                                    )
                                }
                        } else {
                            // 3. [정상 케이스] 2xx 성공이고, Excel 파일인 경우
                            response.bodyToMono(ByteArray::class.java)
                        }
                    } else {
                        // 4. [실패 케이스] 4xx 또는 5xx 오류인 경우
                        response.createException().flatMap { Mono.error(it) }
                    }
                }

        // ✅ [유지됨] 동기 방식의 반환 타입을 위해 .block() 호출
        return responseMono.block() ?: throw RuntimeException("강의 데이터 다운로드 실패 (null 반환)")
    }
}
