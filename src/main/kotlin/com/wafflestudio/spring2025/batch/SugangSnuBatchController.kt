package com.wafflestudio.spring2025.batch

import com.wafflestudio.spring2025.batch.dto.LectureImportResult
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/batch")
class SugangSnuBatchController(
    private val sugangSnuFetchService: SugangSnuFetchService,
) {
    /**
     * 수강신청 사이트에서 강의 데이터 가져와서 DB에 저장
     * (관리자 전용 API)
     */
    @PostMapping("/import-lectures")
    suspend fun importLectures(
        @RequestParam year: String,
        @RequestParam semester: Semester,
    ): ResponseEntity<LectureImportResult> {
        val result = sugangSnuFetchService.fetchAndImportLectures(year, semester)
        return ResponseEntity.ok(result)
    }
}
