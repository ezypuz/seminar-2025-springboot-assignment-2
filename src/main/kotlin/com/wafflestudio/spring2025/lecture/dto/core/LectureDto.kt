// lecture/dto/LectureDto.kt (공통)
package com.wafflestudio.spring2025.lecture.dto.core

import com.wafflestudio.spring2025.timeTable.model.Semester

/**
 * 강의 상세 정보 DTO
 * - 강의 검색, 시간표 조회 등에서 공통으로 사용
 */
data class LectureDto(
    val id: Long,
    val year: String?,
    val semester: Semester?,
    val division: String?,
    val college: String?,
    val department: String?,
    val courseType: String?,
    val grade: Int?,
    val courseNumber: String?,
    val lectureNumber: String?,
    val courseTitle: String?,
    val subtitle: String?,
    val credits: Double?,
    val classTime: Int?,
    val labTime: Int?,
    val professor: String?,
    val preRegistrationCount: Int?,
    val preRegistrationCountForNonFreshman: Int?,
    val preRegistrationCountForFreshman: Int?,
    val quota: Int,
    val nonfreshmanQuota: Int?,
    val registrationCount: Int?,
    val remark: String?,
    val language: String?,
    val status: String?,
    val classSessions: List<ClassSessionDto>,
)

/**
 * 수업 시간/장소 정보 DTO
 */
data class ClassSessionDto(
    val dayOfWeek: Int?,
    val startTime: Int?,
    val endTime: Int?,
    val location: String?,
    val courseFormat: String?,
)
