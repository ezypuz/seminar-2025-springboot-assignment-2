package com.wafflestudio.spring2025.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청 DTO")
data class RegisterRequest(

    @Schema(
        description = "회원가입할 사용자 이름(아이디)",
        example = "waffle123",
        required = true
    )
    val username: String,

    @Schema(
        description = "비밀번호 (영문, 숫자 조합)",
        example = "1234abcd",
        required = true
    )
    val password: String,
)
