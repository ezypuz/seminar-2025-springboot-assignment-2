package com.wafflestudio.spring2025.lecture.dto.core

import com.wafflestudio.spring2025.timeTable.model.Semester
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 강의 상세 정보 DTO
 * - 강의 검색, 시간표 조회 등에서 공통으로 사용
 */
@Schema(description = "강의 정보 DTO")
data class LectureDto(

    @Schema(description = "강의 ID", example = "12345")
    val id: Long,

    @Schema(description = "연도", example = "2025")
    val year: String?,

    @Schema(
        description = "학기 (SPRING, SUMMER, AUTUMN, WINTER)",
        example = "SPRING"
    )
    val semester: Semester?,

    @Schema(description = "이수 구분", example = "전선")
    val division: String?,

    @Schema(description = "대학명", example = "공과대학")
    val college: String?,

    @Schema(description = "학과명", example = "컴퓨터공학부")
    val department: String?,

    @Schema(description = "교과 구분", example = "전공필수")
    val courseType: String?,

    @Schema(description = "학년", example = "3")
    val grade: Int?,

    @Schema(description = "교과목 번호", example = "M1522.001000")
    val courseNumber: String?,

    @Schema(description = "분반 번호", example = "001")
    val lectureNumber: String?,

    @Schema(description = "강의명", example = "컴퓨터프로그래밍")
    val courseTitle: String?,

    @Schema(description = "부제", example = "고급 프로그래밍 기법")
    val subtitle: String?,

    @Schema(description = "학점", example = "3.0")
    val credits: Double?,

    @Schema(description = "강의 시간(시간 단위)", example = "3")
    val classTime: Int?,

    @Schema(description = "실험/실습 시간(시간 단위)", example = "1")
    val labTime: Int?,

    @Schema(description = "교수명", example = "홍길동")
    val professor: String?,

    @Schema(description = "예비 수강신청 인원 수", example = "50")
    val preRegistrationCount: Int?,

    @Schema(description = "예비 수강신청 인원 (재학생)", example = "30")
    val preRegistrationCountForNonFreshman: Int?,

    @Schema(description = "예비 수강신청 인원 (신입생)", example = "20")
    val preRegistrationCountForFreshman: Int?,

    @Schema(description = "정원", example = "60")
    val quota: Int,

    @Schema(description = "재학생 정원", example = "40")
    val nonfreshmanQuota: Int?,

    @Schema(description = "현재 수강신청 인원", example = "55")
    val registrationCount: Int?,

    @Schema(description = "비고", example = "수강편람 참고")
    val remark: String?,

    @Schema(description = "강의 언어", example = "Korean")
    val language: String?,

    @Schema(description = "강의 상태 (예: OPEN, CLOSED)", example = "OPEN")
    val status: String?,

    @Schema(description = "수업 시간/장소 정보 목록")
    val classSessions: List<ClassSessionDto>,
)

/**
 * 수업 시간/장소 정보 DTO
 */
@Schema(description = "수업 시간/장소 정보 DTO")
data class ClassSessionDto(

    @Schema(description = "요일 (1=월요일, 2=화요일, ... 7=일요일)", example = "1")
    val dayOfWeek: Int?,

    @Schema(description = "시작 시간 (24시간제, 예: 9 → 09:00)", example = "9")
    val startTime: Int?,

    @Schema(description = "종료 시간 (24시간제, 예: 10 → 10:00)", example = "10")
    val endTime: Int?,

    @Schema(description = "강의실 위치", example = "301동 105호")
    val location: String?,

    @Schema(description = "수업 형태 (예: 오프라인, 온라인, 블렌디드)", example = "오프라인")
    val courseFormat: String?,
)
