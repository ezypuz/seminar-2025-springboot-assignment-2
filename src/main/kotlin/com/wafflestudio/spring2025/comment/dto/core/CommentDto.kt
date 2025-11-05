package com.wafflestudio.spring2025.comment.dto.core

import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.model.CommentWithUser
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "게시글에 대한 댓글 정보를 나타내는 DTO",
    example = """
        {
          "id": 1,
          "content": "좋은 글 감사합니다!",
          "postId": 10,
          "user": {
            "id": 3,
            "username": "cjwjeong"
          },
          "createdAt": 1730549021000,
          "updatedAt": 1730549021000
        }
    """,
)
data class CommentDto(
    @Schema(description = "댓글의 고유 ID", example = "1")
    val id: Long?,
    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    val content: String,
    @Schema(description = "해당 댓글이 속한 게시글 ID", example = "10")
    val postId: Long,
    @Schema(description = "댓글 작성자 정보 (UserDto)")
    val user: UserDto,
    @Schema(description = "댓글 작성 시각 (epoch millisecond)", example = "1730549021000")
    val createdAt: Long,
    @Schema(description = "댓글 수정 시각 (epoch millisecond)", example = "1730549021000")
    val updatedAt: Long,
) {
    constructor(comment: Comment, user: User) : this(
        id = comment.id,
        content = comment.content,
        postId = comment.postId,
        user = UserDto(user),
        createdAt = comment.createdAt!!.toEpochMilli(),
        updatedAt = comment.updatedAt!!.toEpochMilli(),
    )

    constructor(commentWithUser: CommentWithUser) : this(
        id = commentWithUser.id,
        content = commentWithUser.content,
        postId = commentWithUser.postId,
        user =
            UserDto(
                id = commentWithUser.user!!.id,
                username = commentWithUser.user.username,
            ),
        createdAt = commentWithUser.createdAt.toEpochMilli(),
        updatedAt = commentWithUser.updatedAt.toEpochMilli(),
    )
}
