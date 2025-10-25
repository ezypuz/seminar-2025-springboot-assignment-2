package com.wafflestudio.spring2025.timeTable.controller

import com.wafflestudio.spring2025.timeTable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timeTable.dto.CreateTimeTableRequest
import com.wafflestudio.spring2025.timeTable.dto.CreateTimeTableResponse
import com.wafflestudio.spring2025.timeTable.dto.ListTimeTableResponse
import com.wafflestudio.spring2025.timeTable.dto.TimeTableDetailResponse
import com.wafflestudio.spring2025.timeTable.dto.UpdateTimeTableNameRequest
import com.wafflestudio.spring2025.timeTable.dto.UpdateTimeTableResponse
import com.wafflestudio.spring2025.timeTable.service.TimeTableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/timetables") // API URL 경로 (버전 관리를 위해 /v1 추가)
class TimeTableController(
    private val timeTableService: TimeTableService,
) {
    /**
     * 1. 시간표 생성
     * POST /api/v1/timetables
     */
    @PostMapping
    fun createTimeTable(
        @LoggedInUser user: User, // 👈 로그인한 사용자 정보
        @RequestBody createRequest: CreateTimeTableRequest, // 👈 DTO로 요청 받기
    ): ResponseEntity<CreateTimeTableResponse> {
        val timeTableDto =
            timeTableService.createTimeTable(
                name = createRequest.name,
                year = createRequest.year,
                semester = createRequest.semester,
                user = user,
            )
        // 201 Created 상태 코드와 함께 생성된 시간표 정보 반환
        // TODO ok code created로 바꾸는 로직 구현
        return ResponseEntity.ok(timeTableDto)
    }

    /**
     * 2. 시간표 목록 조회
     * GET /api/v1/timetables
     */
    @GetMapping
    fun getTimeTableList(
        @LoggedInUser user: User,
    ): ResponseEntity<ListTimeTableResponse> {
        val timeTables = timeTableService.getTimeTables(user = user)
        return ResponseEntity.ok(timeTables)
    }

    /**
     * 3. 시간표 상세 조회 (강의 목록, 총 학점 포함)
     * GET /api/v1/timetables/{timeTableId}
     */
    @GetMapping("/{timeTableId}")
    fun getTimeTableDetail(
        @LoggedInUser user: User,
        @PathVariable("timeTableId") timeTableId: Long,
    ): ResponseEntity<TimeTableDetailResponse> {
        val timetableDetail =
            timeTableService.getTimeTableDetail(
                user = user,
                timeTableId = timeTableId,
            )
        return ResponseEntity.ok(timetableDetail)
    }

    /**
     * 4. 시간표 이름 수정
     * PATCH /api/v1/timetables/{timetable_id}
     */
    @PatchMapping("/{timeTableId}")
    fun updateTimeTableName(
        @LoggedInUser user: User,
        @PathVariable("timeTableId") timeTableId: Long,
        @RequestBody updateRequest: UpdateTimeTableNameRequest,
    ): ResponseEntity<UpdateTimeTableResponse> {
        val timeTableDto = timeTableService.updateTimeTableName(timeTableId, user, updateRequest.name)
        return ResponseEntity.ok(timeTableDto)
    }

    /**
     * 5. 시간표 삭제
     * DELETE /api/v1/timetables/{timetable_id}
     */
    @DeleteMapping("/{timeTableId}")
    fun deleteTimeTable(
        @LoggedInUser user: User,
        @PathVariable("timeTableId") timeTableId: Long,
    ): ResponseEntity<Unit> { // 👈 삭제 후에는 내용(Body) 없이 응답
        timeTableService.deleteTimeTable(
            timeTableId = timeTableId,
            user = user,
        )
        // 204 No Content 상태 코드 반환
        return ResponseEntity.noContent().build()
    }

    /**
     * 시간표에 강의 추가
     * (시간 중복 검증은 TimeTableService가 담당)
     */
    @PostMapping("/{timeTableId}/lectures")
    fun addLectureToTimeTable(
        @LoggedInUser user: User,
        @PathVariable("timeTableId") timeTableId: Long,
        @RequestBody request: AddLectureRequest,
    ): ResponseEntity<TimeTableDetailResponse> {
        val updatedTimeTable = timeTableService.addLecture(user, timeTableId, request.lectureId)
        return ResponseEntity.ok(updatedTimeTable)
    }

    /**
     * 시간표에서 강의 삭제
     */
    @DeleteMapping("/{timeTableId}/lectures/{lectureId}") // 👈 이 API
    fun removeLectureFromTimeTable(
        @LoggedInUser user: User,
        @PathVariable("timeTableId") timeTableId: Long,
        @PathVariable("lectureId") lectureId: Long,
    ): ResponseEntity<Unit> {
        timeTableService.removeLecture(user.id!!, timeTableId, lectureId)
        return ResponseEntity.noContent().build()
    }
}
