package com.wafflestudio.spring2025.timeTable.dto

import com.wafflestudio.spring2025.timeTable.model.Semester

data class CreateTimeTableRequest(
    val name: String,
    val year: Int,
    val semester: Semester,
)
