package com.wafflestudio.spring2025.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 응답 DTO (JWT 토큰 반환)")
data class LoginResponse(
    @Schema(
        description = "인증 성공 시 발급되는 JWT 토큰",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    )
    val token: String,
)
