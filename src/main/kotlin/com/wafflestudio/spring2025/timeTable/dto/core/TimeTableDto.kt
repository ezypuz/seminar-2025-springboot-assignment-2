package com.wafflestudio.spring2025.timeTable.dto.core

import com.wafflestudio.spring2025.timeTable.model.Semester
import com.wafflestudio.spring2025.timeTable.model.TimeTable
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User

data class TimeTableDto(
    val id: Long,
    val name: String,
    val year: Int,
    val semester: Semester,
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
