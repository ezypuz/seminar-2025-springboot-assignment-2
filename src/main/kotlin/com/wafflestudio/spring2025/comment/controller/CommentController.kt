package com.wafflestudio.spring2025.comment.controller

import com.wafflestudio.spring2025.comment.dto.CreateCommentRequest
import com.wafflestudio.spring2025.comment.dto.CreateCommentResponse
import com.wafflestudio.spring2025.comment.dto.UpdateCommentRequest
import com.wafflestudio.spring2025.comment.dto.UpdateCommentResponse
import com.wafflestudio.spring2025.comment.dto.core.CommentDto
import com.wafflestudio.spring2025.comment.service.CommentService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "Comment", description = "댓글 API")
class CommentController(
    private val commentService: CommentService,
) {

    @GetMapping
    @Operation(
        summary = "댓글 목록 조회",
        description = "특정 게시글(postId)에 달린 댓글 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [
                    Content(
                        array = ArraySchema(schema = Schema(implementation = CommentDto::class))
                    )
                ]
            ),
            ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음")
        ]
    )
    fun list(
        @Parameter(description = "게시글 ID", example = "10")
        @PathVariable postId: Long,
    ): ResponseEntity<List<CommentDto>> {
        val comments = commentService.list(postId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping
    @Operation(
        summary = "댓글 생성",
        description = "특정 게시글(postId)에 새로운 댓글을 작성합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "생성 성공",
                content = [Content(schema = Schema(implementation = CreateCommentResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "유효하지 않은 요청 본문"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음")
        ]
    )
    fun create(
        @Parameter(description = "게시글 ID", example = "10")
        @PathVariable postId: Long,
        @OasRequestBody(
            description = "생성할 댓글 내용",
            required = true,
            content = [Content(schema = Schema(implementation = CreateCommentRequest::class))]
        )
        @RequestBody createRequest: CreateCommentRequest,
        @Parameter(hidden = true) @LoggedInUser user: User,
    ): ResponseEntity<CreateCommentResponse> {
        val comment =
            commentService.create(
                postId = postId,
                content = createRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "댓글 수정",
        description = "특정 게시글의 특정 댓글(ID)을 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = [Content(schema = Schema(implementation = UpdateCommentResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "유효하지 않은 요청 본문"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            ApiResponse(responseCode = "404", description = "댓글/게시글이 존재하지 않음")
        ]
    )
    fun update(
        @Parameter(description = "게시글 ID", example = "10")
        @PathVariable postId: Long,
        @Parameter(description = "댓글 ID", example = "1")
        @PathVariable id: Long,
        @Parameter(hidden = true) @LoggedInUser user: User,
        @OasRequestBody(
            description = "수정할 댓글 내용",
            required = true,
            content = [Content(schema = Schema(implementation = UpdateCommentRequest::class))]
        )
        @RequestBody updateRequest: UpdateCommentRequest,
    ): ResponseEntity<UpdateCommentResponse> {
        val comment =
            commentService.update(
                commentId = id,
                postId = postId,
                content = updateRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "댓글 삭제",
        description = "특정 게시글의 특정 댓글(ID)을 삭제합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공(응답 바디 없음)"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "댓글/게시글이 존재하지 않음")
        ]
    )
    fun delete(
        @Parameter(description = "게시글 ID", example = "10")
        @PathVariable postId: Long,
        @Parameter(description = "댓글 ID", example = "1")
        @PathVariable id: Long,
        @Parameter(hidden = true) @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        commentService.delete(
            commentId = id,
            postId = postId,
            user = user,
        )
        return ResponseEntity.noContent().build()
    }
}
