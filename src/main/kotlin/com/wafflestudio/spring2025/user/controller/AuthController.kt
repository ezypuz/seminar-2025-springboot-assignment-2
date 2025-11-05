package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.dto.LoginRequest
import com.wafflestudio.spring2025.user.dto.LoginResponse
import com.wafflestudio.spring2025.user.dto.RegisterRequest
import com.wafflestudio.spring2025.user.dto.RegisterResponse
import com.wafflestudio.spring2025.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody

// @SecurityRequirement(name = "bearerAuth")  // JWT 사용 시 활성화
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원가입 및 로그인 API")
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원가입 성공"),
            ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            ApiResponse(responseCode = "409", description = "이미 존재하는 사용자명"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun register(
        @RequestBody
        @OasRequestBody(
            description = "회원가입 요청 정보",
            required = true,
            content = [Content(schema = Schema(implementation = RegisterRequest::class))],
        )
        registerRequest: RegisterRequest,
    ): ResponseEntity<RegisterResponse> {
        val userDto =
            userService.register(
                username = registerRequest.username,
                password = registerRequest.password,
            )
        return ResponseEntity.ok(userDto)
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그인 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (아이디 또는 비밀번호 누락)"),
            ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 자격 증명)"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun login(
        @RequestBody
        @OasRequestBody(
            description = "로그인 요청 정보",
            required = true,
            content = [Content(schema = Schema(implementation = LoginRequest::class))],
        )
        loginRequest: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val token =
            userService.login(
                username = loginRequest.username,
                password = loginRequest.password,
            )
        return ResponseEntity.ok(LoginResponse(token))
    }
}
