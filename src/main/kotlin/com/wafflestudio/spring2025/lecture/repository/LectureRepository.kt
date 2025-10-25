package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface LectureRepository : CrudRepository<Lecture, Long> {
    /**
     * 특정 시간표(timeTableId)에 포함된 모든 강의(Lecture)와
     * 그 강의에 속한 모든 세션(ClassSession) 정보를 한 번의 쿼리로 가져옵니다.
     * (N+1 문제 해결)
     */
    @Query(
        """
        SELECT
            l.id AS lecture_id,
            l.year,
            l.semester,
            l.division,
            l.college,
            l.department,
            l.course_type,
            l.grade,
            l.course_number,
            l.lecture_number,
            l.course_title,
            l.subtitle,
            l.credits,
            l.class_time,
            l.lab_time,
            l.professor,
            l.pre_registration_count,
            l.pre_registration_count_for_non_freshman,
            l.pre_registration_count_for_freshman,
            l.quota,
            l.nonfreshman_quota,
            l.registration_count,
            l.remark,
            l.language,
            l.status,
            
            cs.id AS session_id 
            cs.day_of_week,
            cs.start_time,
            cs.end_time,
            cs.location,
            cs.course_format
        FROM
            lecture l
        JOIN
            timetable_lecture tl ON l.id = tl.lecture_id
        LEFT JOIN
            class_session cs ON l.id = cs.lecture_id
        WHERE
            tl.timetable_id = :timeTableId
    """,
    )
    fun findLectureDetailsByTimeTableId(
        @Param("timeTableId") timeTableId: Long,
    ): List<LectureDetailRow>
}

// 2. 위 @Query의 결과를 매핑할 내부 DTO
data class LectureDetailRow(
    // Lecture 필드 (카멜 케이스)
    val lectureId: Long,
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
    // ClassSession 필드 (카멜 케이스)
    val sessionId: Long?, // session 정보는 없을 수도 있어서 null 허용
    val dayOfWeek: Int?,
    val startTime: Int?,
    val endTime: Int?,
    val location: String?,
    val courseFormat: String?,
)
