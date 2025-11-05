package com.wafflestudio.spring2025.post.dto

import com.wafflestudio.spring2025.post.dto.core.PostDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 목록 조회 응답 (커서 기반 페이지네이션 포함)")
data class PostPagingResponse(
    @Schema(
        description = "게시글 목록",
        implementation = PostDto::class,
    )
    val data: List<PostDto>,
    @Schema(
        description = "다음 페이지 커서 정보",
    )
    val paging: PostPaging,
)
