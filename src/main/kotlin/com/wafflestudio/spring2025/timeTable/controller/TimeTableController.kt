package com.wafflestudio.spring2025.timeTable.controller

import com.wafflestudio.spring2025.timeTable.dto.*
import com.wafflestudio.spring2025.timeTable.service.TimeTableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/timetables")
@Tag(name = "TimeTable", description = "시간표 관리 API")
// @SecurityRequirement(name = "bearerAuth")  // JWT 사용 시 활성화
class TimeTableController(
    private val timeTableService: TimeTableService,
) {

    @PostMapping
    @Operation(summary = "시간표 생성", description = "새로운 시간표를 생성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "시간표 생성 성공"),
            ApiResponse(responseCode = "400", description = "요청 바디 오류"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ]
    )
    fun createTimeTable(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @RequestBody
        @OasRequestBody(
            description = "생성할 시간표 정보",
            required = true,
            content = [Content(schema = Schema(implementation = CreateTimeTableRequest::class))]
        )
        createRequest: CreateTimeTableRequest,
    ): ResponseEntity<CreateTimeTableResponse> {
        val timeTableDto =
            timeTableService.createTimeTable(
                name = createRequest.name,
                year = createRequest.year,
                semester = createRequest.semester,
                user = user,
            )
        return ResponseEntity.ok(timeTableDto)
    }

    @GetMapping
    @Operation(summary = "시간표 목록 조회", description = "현재 로그인한 사용자의 모든 시간표 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ]
    )
    fun getTimeTableList(
        @Parameter(hidden = true)
        @LoggedInUser user: User,
    ): ResponseEntity<ListTimeTableResponse> {
        val timeTables = timeTableService.getTimeTables(user = user)
        return ResponseEntity.ok(timeTables)
    }

    @GetMapping("/{timeTableId}")
    @Operation(summary = "시간표 상세 조회", description = "특정 시간표의 강의 목록과 총 학점을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            ApiResponse(responseCode = "404", description = "시간표를 찾을 수 없음"),
        ]
    )
    fun getTimeTableDetail(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @Parameter(description = "조회할 시간표 ID", example = "1", required = true)
        @PathVariable("timeTableId") timeTableId: Long,
    ): ResponseEntity<TimeTableDetailResponse> {
        val timetableDetail =
            timeTableService.getTimeTableDetail(
                user = user,
                timeTableId = timeTableId,
            )
        return ResponseEntity.ok(timetableDetail)
    }

    @PatchMapping("/{timeTableId}")
    @Operation(summary = "시간표 이름 수정", description = "특정 시간표의 이름을 수정합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            ApiResponse(responseCode = "404", description = "시간표를 찾을 수 없음"),
        ]
    )
    fun updateTimeTableName(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @Parameter(description = "수정할 시간표 ID", example = "2", required = true)
        @PathVariable("timeTableId") timeTableId: Long,

        @RequestBody
        @OasRequestBody(
            description = "새로운 시간표 이름",
            required = true,
            content = [Content(schema = Schema(implementation = UpdateTimeTableNameRequest::class))]
        )
        updateRequest: UpdateTimeTableNameRequest,
    ): ResponseEntity<UpdateTimeTableNameResponse> {
        val timeTableDto = timeTableService.updateTimeTableName(timeTableId, user, updateRequest.name)
        return ResponseEntity.ok(timeTableDto)
    }

    @DeleteMapping("/{timeTableId}")
    @Operation(summary = "시간표 삭제", description = "특정 시간표를 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "시간표를 찾을 수 없음"),
        ]
    )
    fun deleteTimeTable(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @Parameter(description = "삭제할 시간표 ID", example = "3", required = true)
        @PathVariable("timeTableId") timeTableId: Long,
    ): ResponseEntity<Unit> {
        timeTableService.deleteTimeTable(timeTableId = timeTableId, user = user)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{timeTableId}/lectures")
    @Operation(summary = "시간표에 강의 추가", description = "시간표에 새 강의를 추가합니다. (중복 및 시간 중복 검증 포함)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "추가 성공"),
            ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "404", description = "시간표 또는 강의를 찾을 수 없음"),
            ApiResponse(responseCode = "409", description = "시간 중복 또는 이미 추가된 강의"),
        ]
    )
    fun addLectureToTimeTable(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @Parameter(description = "강의를 추가할 시간표 ID", example = "1", required = true)
        @PathVariable("timeTableId") timeTableId: Long,

        @RequestBody
        @OasRequestBody(
            description = "추가할 강의 ID",
            required = true,
            content = [Content(schema = Schema(implementation = AddLectureRequest::class))]
        )
        request: AddLectureRequest,
    ): ResponseEntity<TimeTableDetailResponse> {
        val updatedTimeTable = timeTableService.addLecture(user, timeTableId, request.lectureId)
        return ResponseEntity.ok(updatedTimeTable)
    }

    @DeleteMapping("/{timeTableId}/lectures/{lectureId}")
    @Operation(summary = "시간표에서 강의 삭제", description = "시간표에서 특정 강의를 제거합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "시간표 또는 강의를 찾을 수 없음"),
        ]
    )
    fun removeLectureFromTimeTable(
        @Parameter(hidden = true)
        @LoggedInUser user: User,

        @Parameter(description = "시간표 ID", example = "1", required = true)
        @PathVariable("timeTableId") timeTableId: Long,

        @Parameter(description = "삭제할 강의 ID", example = "1001", required = true)
        @PathVariable("lectureId") lectureId: Long,
    ): ResponseEntity<Unit> {
        timeTableService.removeLecture(user.id!!, timeTableId, lectureId)
        return ResponseEntity.noContent().build()
    }
}
