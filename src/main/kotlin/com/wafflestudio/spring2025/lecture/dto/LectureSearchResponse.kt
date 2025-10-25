package com.wafflestudio.spring2025.lecture.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import org.springframework.data.domain.Page

/**
 * 강의 검색 결과 응답 (페이지네이션 포함)
 */
typealias LectureSearchResponse = Page<LectureDto>
