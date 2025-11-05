package com.wafflestudio.spring2025.timeTable.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시간표에 강의 추가 요청 DTO")
data class AddLectureRequest(
    @Schema(
        description = "추가할 강의의 ID",
        example = "12345",
        required = true,
    )
    val lectureId: Long,
)
