package com.wafflestudio.spring2025.batch

import com.wafflestudio.spring2025.batch.dto.LectureImportResult
import com.wafflestudio.spring2025.timeTable.model.Semester
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/batch")
@Tag(name = "Admin Batch", description = "관리자 전용 배치 API (수강신청 데이터 수집 및 DB 저장)")
class SugangSnuBatchController(
    private val sugangSnuFetchService: SugangSnuFetchService,
) {

    @PostMapping("/import-lectures")
    @Operation(
        summary = "수강신청(Sugang SNU) 강의 데이터 가져오기",
        description = """
            수강신청(Sugang SNU) 사이트에서 지정된 연도(`year`)와 학기(`semester`)의 강의 데이터를 
            자동으로 가져와 데이터베이스에 저장합니다.
            
            ⚠️ **관리자 전용 API**이며, 실행 시 기존 데이터는 갱신될 수 있습니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "강의 데이터 가져오기 및 DB 저장 성공",
                content = [Content(schema = Schema(implementation = LectureImportResult::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            ApiResponse(responseCode = "401", description = "인증 실패 (관리자 권한 필요)"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류 (크롤링 실패 또는 DB 오류)")
        ]
    )
    fun importLectures(
        @Parameter(
            description = "가져올 학기의 연도 (예: 2025)",
            example = "2025",
            required = true
        )
        @RequestParam year: String,

        @Parameter(
            description = "학기 구분 (예: SPRING, SUMMER, FALL, WINTER)",
            example = "SPRING",
            required = true
        )
        @RequestParam semester: Semester,
    ): ResponseEntity<LectureImportResult> {
        val result = sugangSnuFetchService.fetchAndImportLectures(year, semester)
        return ResponseEntity.ok(result)
    }
}
