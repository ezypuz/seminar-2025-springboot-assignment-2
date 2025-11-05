package com.wafflestudio.spring2025.post.dto.core

import com.wafflestudio.spring2025.board.dto.core.BoardDto
import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.model.PostWithUserAndBoard
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 정보 DTO")
data class PostDto(
    @Schema(description = "게시글 ID", example = "1001")
    val id: Long,
    @Schema(description = "게시글 제목", example = "Spring Boot 강의 후기")
    val title: String,
    @Schema(description = "게시글 내용", example = "Spring Boot를 배우면서 느낀 점을 공유합니다.")
    val content: String,
    @Schema(description = "게시글 작성자 정보")
    val user: UserDto,
    @Schema(description = "게시판 정보")
    val board: BoardDto,
    @Schema(description = "게시글 생성 시각 (epoch milliseconds)", example = "1730419200000")
    val createdAt: Long,
    @Schema(description = "게시글 수정 시각 (epoch milliseconds)", example = "1730505600000")
    val updatedAt: Long,
) {
    constructor(post: Post, user: User, board: Board) : this(
        id = post.id!!,
        title = post.title,
        content = post.content,
        user = UserDto(user),
        board = BoardDto(board),
        createdAt = post.createdAt!!.toEpochMilli(),
        updatedAt = post.updatedAt!!.toEpochMilli(),
    )

    constructor(postWithUserAndBoard: PostWithUserAndBoard) : this(
        id = postWithUserAndBoard.id,
        title = postWithUserAndBoard.title,
        content = postWithUserAndBoard.content,
        user =
            UserDto(
                id = postWithUserAndBoard.user!!.id,
                username = postWithUserAndBoard.user.username,
            ),
        board =
            BoardDto(
                id = postWithUserAndBoard.board!!.id,
                name = postWithUserAndBoard.board.name,
            ),
        createdAt = postWithUserAndBoard.createdAt.toEpochMilli(),
        updatedAt = postWithUserAndBoard.updatedAt.toEpochMilli(),
    )
}
