package com.wafflestudio.spring2025.post.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 생성 요청 DTO")
data class CreatePostRequest(
    @Schema(description = "게시글 제목", example = "Spring Boot 게시판 만들기")
    val title: String,
    @Schema(description = "게시글 내용", example = "Spring Boot와 Kotlin으로 게시판을 만들어보는 중입니다.")
    val content: String,
)
