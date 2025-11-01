package com.wafflestudio.spring2025.user.dto.core

import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 DTO")
data class UserDto(

    @Schema(description = "사용자 ID", example = "1")
    val id: Long,

    @Schema(description = "사용자 이름(아이디)", example = "waffle123")
    val username: String,
) {
    constructor(user: User) : this(
        user.id!!,
        user.username,
    )
}
