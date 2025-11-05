package com.wafflestudio.spring2025.post.controller

import com.wafflestudio.spring2025.post.dto.CreatePostRequest
import com.wafflestudio.spring2025.post.dto.CreatePostResponse
import com.wafflestudio.spring2025.post.dto.PostPagingResponse
import com.wafflestudio.spring2025.post.dto.UpdatePostRequest
import com.wafflestudio.spring2025.post.dto.UpdatePostResponse
import com.wafflestudio.spring2025.post.dto.core.PostDto
import com.wafflestudio.spring2025.post.service.PostService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody

// @SecurityRequirement(name = "bearerAuth")
@RestController
@Tag(name = "Post", description = "게시글 관리 API")
class PostController(
    private val postService: PostService,
) {
    @PostMapping("/api/v1/boards/{boardId}/posts")
    @Operation(
        summary = "게시글 생성",
        description = "특정 게시판(boardId)에 새 게시글을 생성합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "요청 바디가 잘못됨"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun create(
        @Parameter(hidden = true) // 문서에 노출하지 않음
        @LoggedInUser user: User,
        @Parameter(description = "게시판 ID", example = "1", required = true)
        @PathVariable boardId: Long,
        @RequestBody
        @OasRequestBody(
            description = "생성할 게시글 정보",
            required = true,
            content = [Content(schema = Schema(implementation = CreatePostRequest::class))],
        )
        createRequest: CreatePostRequest,
    ): ResponseEntity<CreatePostResponse> {
        val postDto =
            postService.create(
                title = createRequest.title,
                content = createRequest.content,
                user = user,
                boardId = boardId,
            )
        return ResponseEntity.ok(postDto)
    }

    @GetMapping("/api/v1/boards/{boardId}/posts")
    @Operation(
        summary = "게시글 페이지 조회(커서)",
        description = """
            특정 게시판의 게시글을 **커서 기반 페이지네이션**으로 조회합니다.  
            - 첫 페이지: `nextCreatedAt`, `nextId` 없이 호출  
            - 다음 페이지: 응답의 `nextCreatedAt`, `nextId` 값을 그대로 넘겨서 호출  
            - `limit`는 페이지 당 항목 수
        """,
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
            ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun page(
        @Parameter(description = "게시판 ID", example = "1", required = true)
        @PathVariable boardId: Long,
        @Parameter(
            description = "다음 페이지 커서: 생성시각(ms, epoch). 첫 페이지는 비움",
            example = "1730419200000",
            required = false,
        )
        @RequestParam(value = "nextCreatedAt", required = false) nextCreatedAt: Long?,
        @Parameter(
            description = "다음 페이지 커서: 마지막 항목의 ID. 첫 페이지는 비움",
            example = "12345",
            required = false,
        )
        @RequestParam(value = "nextId", required = false) nextId: Long?,
        @Parameter(description = "가져올 항목 수(기본 10)", example = "10")
        @RequestParam(value = "limit", defaultValue = "10") limit: Int,
    ): ResponseEntity<PostPagingResponse> {
        val postPagingResponse =
            postService.pageByBoardId(
                boardId,
                nextCreatedAt?.let { Instant.ofEpochMilli(it) },
                nextId,
                limit,
            )
        return ResponseEntity.ok(postPagingResponse)
    }

    @GetMapping("/api/v1/posts/{id}")
    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 게시글을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun get(
        @Parameter(description = "게시글 ID", example = "1001", required = true)
        @PathVariable id: Long,
    ): ResponseEntity<PostDto> {
        val postDto = postService.get(id)
        return ResponseEntity.ok(postDto)
    }

    @PatchMapping("/api/v1/posts/{id}")
    @Operation(summary = "게시글 수정", description = "게시글 제목/내용을 수정합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "요청 바디가 잘못됨"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun update(
        @Parameter(description = "게시글 ID", example = "1001", required = true)
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @LoggedInUser user: User,
        @RequestBody
        @OasRequestBody(
            description = "수정할 게시글 정보",
            required = true,
            content = [Content(schema = Schema(implementation = UpdatePostRequest::class))],
        )
        updateRequest: UpdatePostRequest,
    ): ResponseEntity<UpdatePostResponse> {
        val postDto =
            postService.update(
                postId = id,
                title = updateRequest.title,
                content = updateRequest.content,
                user = user,
            )
        return ResponseEntity.ok(postDto)
    }

    @DeleteMapping("/api/v1/posts/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공(응답 바디 없음)"),
            ApiResponse(responseCode = "401", description = "인증 필요"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류"),
        ],
    )
    fun delete(
        @Parameter(description = "게시글 ID", example = "1001", required = true)
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        postService.delete(id, user)
        return ResponseEntity.noContent().build()
    }
}
