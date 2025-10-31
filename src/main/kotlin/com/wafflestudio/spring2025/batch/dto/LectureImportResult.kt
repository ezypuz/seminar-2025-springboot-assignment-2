package com.wafflestudio.spring2025.batch.dto

/**
 * 강의 임포트 결과 DTO
 */
data class LectureImportResult(
    val totalCount: Int, // 전체 처리된 행 수
    val successCount: Int, // 성공한 행 수
    val failCount: Int, // 실패한 행 수
)
