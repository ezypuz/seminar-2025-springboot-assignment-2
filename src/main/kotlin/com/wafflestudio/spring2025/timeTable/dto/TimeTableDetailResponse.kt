package com.wafflestudio.spring2025.timeTable.dto

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
    val lectures: List<LectureResponse>,
)

/**
 * 시간표에 포함된 '강의'의 상세 정보를 담는 DTO
 * (Lecture 엔티티 + ClassSession 엔티티 목록)
 */
data class LectureResponse(
    // 1. 강의 고유 ID
    val id: Long,
    // 2. 강의 기본 정보
    val year: String?, // 년도
    val semester: Semester?, // 학기
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
    // 3. 수강신청 관련 정보
    val preRegistrationCount: Int?, // 장바구니 신청 인원 (전체)
    val preRegistrationCountForNonFreshman: Int?, // 재학생 장바구니 신청 인원
    val preRegistrationCountForFreshman: Int?, // 신입생 장바구니 신청 인원
    val quota: Int, // 정원 (전체)
    val nonfreshmanQuota: Int?, // 재학생 정원
    val registrationCount: Int?, // 수강신청 인원 (실제)
    // 4. 기타 정보
    val remark: String?, // 비고
    val language: String?, // 강의언어
    val status: String?, // 개설상태
    // 5. 수업 시간/장소 목록 (ClassSession 테이블)
    val classSessions: List<ClassSessionResponse>,
)

/**
 * '수업 시간/장소' (ClassSession)의 상세 정보를 담는 DTO
 */
data class ClassSessionResponse(
    //  val sessionId: Long?,
    //  필요없는 정보라 판단되어 response에서 제외
    val dayOfWeek: Int?, // 요일 (0=월, 1=화, 2=수, 3=목, 4=금, 5=토, 6=일)
    val startTime: Int?, // 시작 시간 (분 단위, 예: 600 = 10:00)
    val endTime: Int?, // 종료 시간 (분 단위, 예: 710 = 11:50)
    val location: String?, // 강의실
    val courseFormat: String?, // 수업 형태 (예: "대면", "비대면", "이론", "실습")
)
