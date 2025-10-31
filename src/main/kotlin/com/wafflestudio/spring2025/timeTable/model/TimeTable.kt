package com.wafflestudio.spring2025.timeTable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("timetable")
data class TimeTable(
    @Id
    var id: Long? = null,
    var name: String,
    @Column("academic_year")
    var year: Int,
    var semester: Semester,
    @Column("user_id")
    var userId: Long,
)
