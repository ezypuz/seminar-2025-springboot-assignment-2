package com.wafflestudio.spring2025.timeTable.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시간표 이름 수정 요청 DTO")
data class UpdateTimeTableNameRequest(
    @Schema(
        description = "새로운 시간표 이름",
        example = "2025년 1학기 수정본",
        required = true,
    )
    val name: String,
)
