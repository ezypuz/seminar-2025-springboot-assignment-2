package com.wafflestudio.spring2025.timeTable.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.timeTable.model.Semester
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시간표 상세 조회 응답 DTO")
data class TimeTableDetailResponse(

    @Schema(description = "시간표 ID", example = "1")
    val id: Long,

    @Schema(description = "시간표 이름", example = "2025년 1학기 시간표")
    val name: String,

    @Schema(description = "시간표 연도", example = "2025")
    val year: Int,

    @Schema(
        description = "학기 (SPRING, SUMMER, AUTUMN, WINTER)",
        example = "SPRING"
    )
    val semester: Semester,

    @Schema(description = "시간표 내 총 학점", example = "18.0")
    val totalCredits: Double,

    @Schema(description = "시간표에 포함된 강의 목록")
    val lectures: List<LectureDto>,
)
