package com.wafflestudio.spring2025.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청 DTO")
data class LoginRequest(
    @Schema(
        description = "사용자 이름(아이디)",
        example = "waffle123",
        required = true,
    )
    val username: String,
    @Schema(
        description = "비밀번호",
        example = "1234abcd",
        required = true,
    )
    val password: String,
)
