package com.wafflestudio.spring2025.lecture.controller

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.service.LectureService
import com.wafflestudio.spring2025.timeTable.model.Semester
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/lectures")
@Tag(name = "Lecture", description = "강의 검색 API")
class LectureController(
    private val lectureService: LectureService,
) {
    @GetMapping("/search")
    @Operation(
        summary = "강의 검색",
        description = """
            특정 연도와 학기의 강의를 키워드로 검색합니다.  
            키워드는 강의명 또는 교수명에 포함되는지를 기준으로 검색하며,  
            결과는 페이지네이션 형태로 반환됩니다.
        """,
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "검색 성공"),
            ApiResponse(
                responseCode = "400",
                description = "요청 파라미터 오류",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = [Content()],
            ),
        ],
    )
    fun searchLectures(
        @Parameter(
            description = "검색할 연도 (예: 2025)",
            example = "2025",
            required = true,
        )
        @RequestParam year: String,
        @Parameter(
            description = "검색할 학기 (SPRING, SUMMER, AUTUMN, WINTER)",
            example = "SPRING",
            required = true,
        )
        @RequestParam semester: Semester,
        @Parameter(
            description = "검색 키워드 (강의명 또는 교수명 일부)",
            example = "컴퓨터프로그래밍",
            required = true,
        )
        @RequestParam keyword: String,
        @Parameter(
            description = "페이지 정보 (page, size, sort)",
            required = false,
            schema =
                Schema(
                    type = "object",
                    example = """{ "page": 0, "size": 20, "sort": ["courseTitle,asc"] }""",
                ),
        )
        @PageableDefault(size = 20, sort = ["courseTitle"]) pageable: Pageable,
    ): ResponseEntity<LectureSearchResponse> {
        val result = lectureService.searchLectures(year, semester, keyword, pageable)
        return ResponseEntity.ok(result)
    }
}
