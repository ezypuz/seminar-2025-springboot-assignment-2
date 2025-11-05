package com.wafflestudio.spring2025.board.controller

import com.wafflestudio.spring2025.board.dto.CreateBoardRequest
import com.wafflestudio.spring2025.board.dto.CreateBoardResponse
import com.wafflestudio.spring2025.board.dto.ListBoardResponse
import com.wafflestudio.spring2025.board.dto.core.BoardDto
import com.wafflestudio.spring2025.board.service.BoardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody

@RestController
@RequestMapping("/api/v1/boards")
@Tag(name = "Board", description = "게시판(Board) 관련 API")
class BoardController(
    private val boardService: BoardService,
) {
    @PostMapping
    @Operation(
        summary = "게시판 생성",
        description = "새로운 게시판을 생성합니다. 게시판 이름을 요청 본문으로 전달해야 합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "게시판 생성 성공",
                // CreateBoardResponse 가 typealias 라면 ::class 금지 → BoardDto 로 문서화
                content = [Content(schema = Schema(implementation = BoardDto::class))],
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            ApiResponse(responseCode = "409", description = "이미 존재하는 게시판 이름"),
        ],
    )
    fun create(
        @OasRequestBody(
            description = "생성할 게시판 정보",
            required = true,
            content = [Content(schema = Schema(implementation = CreateBoardRequest::class))],
        )
        @RequestBody createRequest: CreateBoardRequest,
    ): ResponseEntity<CreateBoardResponse> {
        val board = boardService.create(createRequest.name)
        return ResponseEntity.ok(board)
    }

    @GetMapping
    @Operation(
        summary = "게시판 목록 조회",
        description = "모든 게시판의 목록을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "게시판 목록 조회 성공",
                // ListBoardResponse 가 typealias List<BoardDto> 라면 배열 스키마로 문서화
                content = [
                    Content(
                        array =
                            ArraySchema(
                                schema = Schema(implementation = BoardDto::class),
                            ),
                    ),
                ],
            ),
        ],
    )
    fun list(): ResponseEntity<ListBoardResponse> {
        val boards = boardService.list()
        return ResponseEntity.ok(boards)
    }
}
