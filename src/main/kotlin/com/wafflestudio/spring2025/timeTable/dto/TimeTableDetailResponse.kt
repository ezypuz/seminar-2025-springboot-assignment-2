package com.wafflestudio.spring2025.timeTable.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.timeTable.model.Semester

/**
 * 시간표 상세 조회 API의 응답 DTO
 */
data class TimeTableDetailResponse(
    // 1. 시간표 기본 정보
    val id: Long,
    val name: String,
    val year: Int,
    val semester: Semester,
    // 2. 계산된 정보 (총 학점)
    val totalCredits: Double,
    // 3. 포함된 강의 목록
    val lectures: List<LectureDto>,
)
