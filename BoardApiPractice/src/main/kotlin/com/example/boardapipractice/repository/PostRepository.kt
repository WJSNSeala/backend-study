package com.example.boardapipractice.repository

import com.example.boardapipractice.entity.Post
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface PostRepository: JpaRepository<Post, Long> {
    // 비관적 락을 적용한 포스트 조회 메서드
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.postId = :postId")
    fun findByIdWithPessimisticLock(@Param("postId") postId: Long): Optional<Post>
}
