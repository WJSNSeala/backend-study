package com.example.boardapipractice.service

import com.example.boardapipractice.dto.comment.CommentCreateDto
import com.example.boardapipractice.dto.comment.CommentUpdateDto
import com.example.boardapipractice.entity.Comment
import com.example.boardapipractice.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postService: PostService

) {

    /**
     * 모든 댓글 조회
     *
     * @return 모든 댓글 목록
     */
    fun getAllComments(pageable: Pageable): Page<Comment> {
        return commentRepository.findAll(pageable)
    }

    /**
     * ID로 댓글 조회
     * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
     */
    fun getCommentById(commentId: Long): Comment {
        return commentRepository.findById(commentId).orElseThrow {
            NoSuchElementException("ID가 " + commentId + "인 댓글을 찾을 수 없습니다")
        }
    }

    /**
     * 게시물 ID로 댓글 조회
     * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
     */
    fun getCommentsByPostId(postId: Long): List<Comment> {
        return commentRepository.findByPostId(postId)
    }


    /**
     * 새 댓글 저장
     *
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글 객체 (ID가 할당됨)
     */
    @Transactional
    fun createComment(commentCreateDto: CommentCreateDto): Comment {
      if (postService.existsById(commentCreateDto.parentPostId)) {
            val comment = commentCreateDto.toEntity()
            return commentRepository.save(comment)
      } else {
            throw NoSuchElementException("ID가 " + commentCreateDto.parentPostId + "인 게시물을 찾을 수 없습니다")
      }
    }

    /**
     *  좋아요 수 증가
     */
    @Transactional
    fun increaseLikeCount(commentId: Long): Comment {
        val existingComment = commentRepository.findById(commentId).orElseThrow {
            NoSuchElementException("ID가 " + commentId + "인 댓글을 찾을 수 없습니다")
        }

        existingComment.likes += 1

        return commentRepository.save(existingComment)
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
    @Transactional
    fun updateComment(commentId: Long, commentUpdateDto: CommentUpdateDto): Comment {
        val existingComment = commentRepository.findById(commentId).orElseThrow {
            NoSuchElementException("ID가 " + commentId + "인 댓글을 찾을 수 없습니다")
        }

        // 기존 댓글 정보 업데이트
        commentUpdateDto.updateEntity(existingComment)

        return commentRepository.save(existingComment)
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글의 ID
     * @throws NoSuchElementException 댓글을 찾을 수 없는 경우
     */
    @Transactional
    fun deleteComment(commentId: Long) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId)
        } else {
            throw NoSuchElementException("ID가 " + commentId + "인 댓글을 찾을 수 없습니다")
        }

    }

}