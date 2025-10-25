package com.wafflestudio.spring2025.timeTable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("timeTableLecture")
data class TimeTableLecture(
    @Id var id: Long? = null,
    val timetableId: Long, // 👈 'TimeTable'을 가리킴
    val lectureId: Long, // 👈 'Lecture'를 가리킴
)
