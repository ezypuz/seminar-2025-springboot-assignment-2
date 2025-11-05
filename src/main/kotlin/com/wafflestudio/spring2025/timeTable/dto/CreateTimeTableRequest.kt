package com.wafflestudio.spring2025.timeTable.dto

import com.wafflestudio.spring2025.timeTable.model.Semester
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시간표 생성 요청 DTO")
data class CreateTimeTableRequest(
    @Schema(
        description = "시간표 이름",
        example = "2025년 1학기 시간표",
        required = true,
    )
    val name: String,
    @Schema(
        description = "시간표 연도",
        example = "2025",
        required = true,
    )
    val year: Int,
    @Schema(
        description = "학기 (SPRING, SUMMER, AUTUMN, WINTER)",
        example = "SPRING",
        required = true,
    )
    val semester: Semester,
)
