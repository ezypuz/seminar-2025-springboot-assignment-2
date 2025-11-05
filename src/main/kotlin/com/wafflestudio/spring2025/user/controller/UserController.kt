package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.dto.GetMeResponse
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "사용자 정보 API")
@SecurityRequirement(name = "bearerAuth") // JWT 인증 필요 시 활성화
class UserController {
    @GetMapping("/me")
    @Operation(
        summary = "내 정보 조회",
        description = "현재 로그인한 사용자의 정보를 반환합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun me(
        @Parameter(hidden = true)
        @LoggedInUser user: User,
    ): ResponseEntity<GetMeResponse> = ResponseEntity.ok(UserDto(user))
}
