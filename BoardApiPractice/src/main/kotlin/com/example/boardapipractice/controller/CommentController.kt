package com.example.boardapipractice.controller

import com.example.boardapipractice.dto.comment.CommentCreateDto
import com.example.boardapipractice.dto.comment.CommentUpdateDto
import com.example.boardapipractice.entity.Comment
import com.example.boardapipractice.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService
) {

        /**
        * 모든 댓글 조회
        *
        * @return 모든 댓글 목록
        */
        @GetMapping
        fun getAllComments(
            @RequestParam(required = true) limit: Int,
            @RequestParam(required = false,
                          defaultValue = "1") page: Int,
        ): ResponseEntity<List<Comment>> {
            if (limit < 1 || page < 1) {
                return ResponseEntity.badRequest().build()
            }

            val pageable = PageRequest.of(page-1, limit)

            val comments = commentService.getAllComments(pageable)
            return ResponseEntity.ok(comments.content)
        }

        /**
        * ID로 댓글 조회
        *
        * @param commentId 댓글 ID
        * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
        */
        @GetMapping("/{commentId}")
        fun getCommentById(@PathVariable commentId: Long): ResponseEntity<Comment> {
            return try {
                val comment = commentService.getCommentById(commentId)
                ResponseEntity.ok(comment)
            } catch (e: NoSuchElementException) {
                ResponseEntity.notFound().build()
            }
        }

        /**
        * 게시물 ID로 댓글 조회
        *
        * @param postId 게시물 ID
        * @return 게시물에 달린 모든 댓글 목록
        */
        @GetMapping("/by-post-id/{postId}")
        fun getCommentsByPostId(@PathVariable postId: Long): ResponseEntity<List<Comment>> {
            val comments = commentService.getCommentsByPostId(postId)
            return ResponseEntity.ok(comments)
        }

        /**
        * 새 댓글 저장
        *
        * @param comment 저장할 댓글 객체
        * @return 저장된 댓글 객체 (ID가 할당됨)
        */
        @PostMapping
        fun createComment(@RequestBody commentCreateDto: CommentCreateDto): ResponseEntity<Comment> {
            val createdComment = commentService.createComment(commentCreateDto)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment)
        }

        /**
        * 댓글 수정
        *
        * @param commentId 수정할 댓글의 ID
        * @param commentUpdateDto 수정할 Comment의 content 내용이 담긴 댓글 객체
        * CommentUpdateDto class 참조
        *
        * @return 수정된 댓글 객체
        * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
        */
        @PutMapping("/{commentId}")
        fun updateComment(
            @PathVariable commentId: Long,
            @RequestBody commentUpdateDto: CommentUpdateDto
        ): ResponseEntity<Comment> {
            return try {
                val updatedComment = commentService.updateComment(commentId, commentUpdateDto)
                ResponseEntity.ok(updatedComment)
            } catch (e: NoSuchElementException) {
                ResponseEntity.notFound().build()
            }
        }

    /**
     * 댓글 삭제
     * @param commentId 삭제할 댓글의 ID
     * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
     *
     */
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<Unit> {
        return try {
            commentService.deleteComment(commentId)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}