package com.wafflestudio.spring2025.lecture.controller

import com.wafflestudio.spring2025.lecture.service.LectureService
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/lectures") // 👈 API 경로
class LectureController(
    private val lectureService: LectureService,
) {
    /**
     * 강의 검색 API
     * - 특정 연도/학기의 강의를 키워드로 검색
     * - 키워드는 강의명 또는 교수명에 포함되는지 확인
     * - 페이지네이션 적용
     *
     * @param year 검색할 연도 (예: "2025")
     * @param semester 검색할 학기 (SPRING, SUMMER, FALL, WINTER)
     * @param keyword 검색 키워드 (강의명 또는 교수명)
     * @param pageable 페이지 정보 (page, size, sort)
     * @return 검색 결과 (페이지네이션 적용)
     */
    @GetMapping("/search")
    fun searchLectures(
        @RequestParam year: String,
        @RequestParam semester: Semester,
        @RequestParam keyword: String,
        @PageableDefault(size = 20, sort = ["courseTitle"]) pageable: Pageable,
    ): ResponseEntity<Page<LectureSearchResponse>> {
        val result = lectureService.searchLectures(year, semester, keyword, pageable)
        return ResponseEntity.ok(result)
    }
}
