package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.ClassSession
import org.springframework.data.repository.CrudRepository

interface ClassSessionRepository : CrudRepository<ClassSession, Long> {
    /**
     * 특정 강의의 모든 세션 조회
     */
    fun findByLectureId(lectureId: Long): List<ClassSession>
}
