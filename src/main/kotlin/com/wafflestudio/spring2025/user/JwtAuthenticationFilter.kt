package com.wafflestudio.spring2025.user

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // ✅ [수정됨] isPublicPath를 먼저 호출
        if (isPublicPath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val username = jwtTokenProvider.getUsername(token)
            request.setAttribute("username", username)
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    private fun isPublicPath(path: String): Boolean {
        val pathMatcher = AntPathMatcher()

        // 1. 기존 공개 경로
        val publicPaths =
            listOf(
                "/api/v1/auth/**", // 인증 관련 API
                "/api/v1/lectures/**", //  강의 검색 API (공개)
                "/admin/batch/**",
            )

        // 2. Swagger 경로 (추가)
        val swaggerPaths =
            listOf(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
            )

        // 3. 두 리스트를 합쳐서 한 번에 검사
        // (return 문 뒤에 코드를 쓴 것이 문제였습니다)
        return (publicPaths + swaggerPaths).any { pathMatcher.match(it, path) }
    }
}
