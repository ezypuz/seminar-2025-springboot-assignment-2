package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface LectureRepository :
    CrudRepository<Lecture, Long>,
    PagingAndSortingRepository<Lecture, Long> {

    /**
     * 연도, 학기, 키워드로 강의 검색 (세션 포함 + 전체 개수를 한 번에 조회)
     * Window Function으로 전체 개수를 각 행에 포함
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
            cs.id AS session_id,
            cs.day_of_week,
            cs.start_time,
            cs.end_time,
            cs.location,
            cs.course_format,
            COUNT(DISTINCT l.id) OVER() AS total_count
        FROM lecture l
        LEFT JOIN class_session cs ON l.id = cs.lecture_id
        WHERE l.year = :year
          AND l.semester = :semester
          AND (
              l.course_title LIKE CONCAT('%', :keyword, '%')
              OR l.professor LIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY l.id
        LIMIT :limit OFFSET :offset
        """,
    )
    fun findLectureDetailsWithSessions(
        @Param("year") year: String,
        @Param("semester") semester: Semester,
        @Param("keyword") keyword: String,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int,
    ): List<LectureDetailRow>

    /**
     * 특정 시간표(timeTableId)에 포함된 모든 강의(Lecture)와
     * 그 강의에 속한 모든 세션(ClassSession) 정보를 한 번의 쿼리로 가져옵니다.
     * (N+1 문제 해결)
     *
     * @param timeTableId 조회할 시간표 ID
     * @return 강의와 세션 정보가 결합된 LectureDetailRow 리스트
     *         - 하나의 강의에 여러 세션이 있으면 해당 강의 정보가 세션 수만큼 중복되어 반환됨
     *         - 세션이 없는 강의는 session 관련 필드가 null로 반환됨 (LEFT JOIN)
     */
    @Query(
        """
        SELECT
            -- 강의 기본 정보
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
            
            -- 수강신청 관련 정보
            l.pre_registration_count,
            l.pre_registration_count_for_non_freshman,
            l.pre_registration_count_for_freshman,
            l.quota,
            l.nonfreshman_quota,
            l.registration_count,
            
            -- 기타 정보
            l.remark,
            l.language,
            l.status,
            
            -- 세션(수업 시간) 정보
            cs.id AS session_id,
            cs.day_of_week,
            cs.start_time,
            cs.end_time,
            cs.location,
            cs.course_format,
            0 AS total_count
        FROM
            lecture l
        JOIN
            timetable_lecture tl ON l.id = tl.lecture_id
        LEFT JOIN -- 세션이 없는 강의도 포함하기 위해 LEFT JOIN 사용
            class_session cs ON l.id = cs.lecture_id
        WHERE
            tl.timetable_id = :timeTableId
    """,
    )
    fun findLectureDetailsByTimeTableId(
        @Param("timeTableId") timeTableId: Long,
    ): List<LectureDetailRow>
}

/**
 * 강의와 세션 정보를 담는 평면화된 DTO
 * - SQL JOIN 결과를 매핑하기 위한 임시 데이터 클래스
 * - Spring Data JDBC가 snake_case 컬럼명을 camelCase 필드명으로 자동 매핑
 */
data class LectureDetailRow(
    // === 강의 기본 정보 ===
    val lectureId: Long,
    val year: String?,
    val semester: Semester?,
    val division: String?, // 교과구분
    val college: String?, // 개설대학
    val department: String?, // 개설학과
    val courseType: String?, // 이수과정
    val grade: Int?, // 학년
    val courseNumber: String?, // 교과목번호
    val lectureNumber: String?, // 강좌번호
    val courseTitle: String?, // 교과목명
    val subtitle: String?, // 부제명
    val credits: Double?, // 학점
    val classTime: Int?, // 강의 시간
    val labTime: Int?, // 실습 시간
    val professor: String?, // 주담당교수
    // === 수강신청 관련 정보 ===
    val preRegistrationCount: Int?, // 장바구니 신청 인원 (전체)
    val preRegistrationCountForNonFreshman: Int?, // 재학생 장바구니 신청 인원
    val preRegistrationCountForFreshman: Int?, // 신입생 장바구니 신청 인원
    val quota: Int, // 정원 (전체)
    val nonfreshmanQuota: Int?, // 재학생 정원
    val registrationCount: Int?, // 수강신청 인원 (실제)
    // === 기타 정보 ===
    val remark: String?, // 비고
    val language: String?, // 강의언어
    val status: String?, // 개설상태
    // === 세션(수업 시간) 정보 ===
    val sessionId: Long?, // 세션 ID (LEFT JOIN이므로 null 가능)
    val dayOfWeek: Int?, // 요일 (0: 월요일 ~ 6: 일요일)
    val startTime: Int?, // 시작 시간 (분 단위)
    val endTime: Int?, // 종료 시간 (분 단위)
    val location: String?, // 강의실
    val courseFormat: String?, // 수업 형태 (대면/비대면 등)
    val totalCount: Long,
)
