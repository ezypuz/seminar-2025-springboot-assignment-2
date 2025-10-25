package com.wafflestudio.spring2025.batch

import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

class SugangSnuBatchController {
    /**
     * 2. [관리자용] 서울대 강의 정보 가져오기 (배치)
     * POST /v1/lectures/fetch-snu
     */
    @PostMapping("/fetch-snu")
    fun fetchSnuLectures(
        @RequestParam("year") year: Int,
        @RequestParam("semester") semester: Semester,
    ): ResponseEntity<String> { // 👈 간단한 문자열 응답

        // Service의 비동기(@Async) 메서드를 호출합니다.
        // 이 API는 즉시 응답하고, 실제 작업은 백그라운드에서 실행됩니다.
        lectureService.fetchSnuLectures(year, semester)

        return ResponseEntity.ok("[$year-$semester] 강의 정보 가져오기를 시작합니다. (비동기 처리)")
    }
}
