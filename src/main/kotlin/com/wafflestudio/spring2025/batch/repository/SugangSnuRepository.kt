package com.wafflestudio.spring2025.batch.repository

import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

interface SugangSnuRepository {
    suspend fun downloadLectureXls(
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
    override suspend fun downloadLectureXls(
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

        return webClient
            .get()
            .uri("https://sugang.snu.ac.kr/sugang/cc/cc103excel.action") {
                it.queryParam("workType", "EX")
                it.queryParam("srchOpenSchyy", year)
                it.queryParam("srchOpenShtm", semesterCode)
                it.queryParam("lang", "ko")
                it.build()
            }.retrieve()
            .awaitBody()
    }
}
