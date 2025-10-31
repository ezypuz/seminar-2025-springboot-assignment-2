package com.wafflestudio.spring2025.timeTable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("timetable_lecture")
data class TimeTableLecture(
    @Id var id: Long? = null,
    @Column("timetable_id")
    val timeTableId: Long, // 👈 'TimeTable'을 가리킴
    @Column("lecture_id")
    val lectureId: Long, // 👈 'Lecture'를 가리킴
)
