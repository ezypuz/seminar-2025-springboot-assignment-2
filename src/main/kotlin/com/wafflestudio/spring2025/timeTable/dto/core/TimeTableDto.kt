package com.wafflestudio.spring2025.timeTable.dto.core

import com.wafflestudio.spring2025.timeTable.model.Semester
import com.wafflestudio.spring2025.timeTable.model.TimeTable
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시간표 정보 DTO")
data class TimeTableDto(

    @Schema(description = "시간표 ID", example = "1")
    val id: Long,

    @Schema(description = "시간표 이름", example = "2025년 1학기 시간표")
    val name: String,

    @Schema(description = "연도", example = "2025")
    val year: Int,

    @Schema(
        description = "학기 (SPRING, SUMMER, AUTUMN, WINTER)",
        example = "SPRING"
    )
    val semester: Semester,

    @Schema(description = "시간표 소유자 정보")
    val user: UserDto,
) {
    constructor(timeTable: TimeTable, user: User) : this(
        id = timeTable.id!!,
        name = timeTable.name,
        year = timeTable.year,
        semester = timeTable.semester,
        user = UserDto(user),
    )
}
