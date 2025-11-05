package com.wafflestudio.spring2025.batch.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "수강신청(Sugang SNU) 강의 임포트 결과 DTO")
data class LectureImportResult(
    @Schema(description = "전체 처리된 강의 수", example = "1200")
    val totalCount: Int,
    @Schema(description = "DB에 성공적으로 저장된 강의 수", example = "1180")
    val successCount: Int,
    @Schema(description = "저장 실패(중복, 파싱오류 등)한 강의 수", example = "20")
    val failCount: Int,
)
