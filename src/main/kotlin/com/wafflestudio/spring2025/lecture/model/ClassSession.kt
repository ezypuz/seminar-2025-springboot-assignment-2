package com.wafflestudio.spring2025.lecture.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 강의 세션(시간/장소) 엔티티 (V10 스크립트)
 * (하나의 Lecture가 여러 개의 ClassSession을 가짐)
 */
@Table("class_session") // V10에서 정의한 'class_session' 테이블
data class ClassSession(
    @Id
    var id: Long? = null,
    /**
     * 이 세션이 속한 Lecture의 ID (FK)
     * V10에서 NOT NULL로 정의됨
     */
    val lectureId: Long,
    /**
     * 요일 (0=월, 1=화, 2=수...)
     * V10에서 INT NULL로 정의됨
     */
    val dayOfWeek: Int?,
    /**
     * 시작 시간 (분 단위, 예: 10:00 -> 600)
     * V10에서 INT NULL로 정의됨
     */
    val startTime: Int?,
    /**
     * 종료 시간 (분 단위, 예: 11:50 -> 710)
     * V10에서 INT NULL로 정의됨
     */
    val endTime: Int?,
    /**
     * 강의실
     * V10에서 VARCHAR NULL로 정의됨
     */
    val location: String?,
    /**
     * 수업 형태 (예: "이론", "실습", "온라인")
     * V10에서 VARCHAR NULL로 정의됨
     */
    val courseFormat: String?,
)
