package com.wafflestudio.spring2025.post.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 수정 요청 DTO")
data class UpdatePostRequest(

    @Schema(
        description = "수정할 게시글 제목 (null일 경우 제목은 변경되지 않음)",
        example = "Spring Boot 게시판 기능 개선",
        nullable = true
    )
    val title: String?,

    @Schema(
        description = "수정할 게시글 내용 (null일 경우 내용은 변경되지 않음)",
        example = "게시판 페이징 로직을 커서 기반으로 개선했습니다.",
        nullable = true
    )
    val content: String?,
)
