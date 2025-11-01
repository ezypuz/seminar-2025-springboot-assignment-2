package com.wafflestudio.spring2025.post.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 페이지네이션 정보 (커서 기반)")
data class PostPaging(

    @Schema(
        description = "다음 페이지 요청 시 사용할 마지막 게시글의 생성시각 (epoch milliseconds)",
        example = "1730419200000",
        nullable = true
    )
    val nextCreatedAt: Long?,

    @Schema(
        description = "다음 페이지 요청 시 사용할 마지막 게시글의 ID",
        example = "1050",
        nullable = true
    )
    val nextId: Long?,

    @Schema(
        description = "다음 페이지가 존재하는지 여부",
        example = "true"
    )
    val hasNext: Boolean,
)
