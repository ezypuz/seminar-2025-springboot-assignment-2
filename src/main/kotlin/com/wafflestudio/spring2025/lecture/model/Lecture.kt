package com.wafflestudio.spring2025.lecture.model

import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 강의 마스터 데이터 엔티티 (MySQL/RDBMS 용)
 */
@Table("lecture") // V7 스크립트에서 정의한 'lecture' 테이블
data class Lecture(
    @Id
    var id: Long? = null,
    var year: String?, // 년도
    var semester: Semester?, // 학기
    // 모든 데이터를 null 가능 처리함
    val division: String?, // 교과구분 (A열)
    val college: String?, // 개설대학 (B열)
    val department: String?, // 개설학과 (C열)
    val courseType: String?, // 이수과정 (D열)
    val grade: Int?, // 학년 (E열)
    val courseNumber: String?, // 교과목번호 (F열)
    val lectureNumber: String?, // 강좌번호 (G열)
    val courseTitle: String?, // 교과목명 (H열)
    val subtitle: String?, // 부제명 (I열) (NULL 허용)
    val credits: Double?, // 학점 (J열)
    val classTime: Int?, // 강의 (K열)
    val labTime: Int?, // 실습 (L열)
    val professor: String?, // 주담당교수 (P열)
    val preRegistrationCount: Int?, // 장바구니 신청 인원 (전체) (Q열)
    val preRegistrationCountForNonFreshman: Int?, // 재학생 장바구니 신청 인원 (R열)
    val preRegistrationCountForFreshman: Int?, // 신입생 장바구니 신청 인원 (S열)
    val quota: Int, // 정원 (전체 정원) (T-1열)
    val nonfreshmanQuota: Int?, // 재학생 정원 (T-2열)
    val registrationCount: Int?, // 수강신청 인원 (실제 신청 인원) (U열)
    val remark: String?, // 비고 (V열)
    val language: String?, // 강의언어 (W열)
    val status: String?, // 개설상태 (X열)
    // 🔴 중요: class_time_info와 location, 수업형태는 별도 테이블로 분리합니다.
    // 이 엔티티는 '강의' 자체의 정보만 가집니다.
)
