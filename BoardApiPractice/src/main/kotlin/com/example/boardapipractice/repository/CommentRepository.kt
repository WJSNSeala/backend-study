package com.example.boardapipractice.repository

import com.example.boardapipractice.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository()
interface CommentRepository: JpaRepository<Comment, Long> {
    /**
     * 게시물 ID로 댓글 조회
     *
     * parent_post_id 열을 통해 게시물 ID로 댓글을 조회합니다.
     *
     * postId - commentId의 index가 이미 생성되어 있다.
     *
     * @param postId 게시물 ID
     * @return 게시물 ID에 해당하는 댓글 목록
     */

    @Query("SELECT c FROM Comment c WHERE c.parentPostId = :postId")
    fun findByPostId(@Param("postId") postId: Long): List<Comment>
}