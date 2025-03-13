package com.example.boardapipractice.service

import com.example.boardapipractice.entity.Comment
import com.example.boardapipractice.repository.CommentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class CommentServiceConcurrencyTest {

    @Autowired
    private lateinit var commentService: CommentService

    @Autowired
    private lateinit var commentRepository: CommentRepository

    private lateinit var testComment: Comment

    @BeforeEach
    fun setup() {
        // Create a test comment with 0 likes
        testComment = Comment(
            content = "Test comment for concurrency testing",
            author = "Tester",
            password = "password123",
            parentPostId = 3L,
            likes = 0
        )
        testComment = commentRepository.save(testComment)
    }

    @Test
    fun `test normal like increase with concurrency issues`() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // Before test
        val beforeLikes = testComment.likes
        println("Starting test with likes = $beforeLikes")

        // Launch 100 concurrent threads to like the comment
        for (i in 1..threadCount) {
            executor.submit {
                try {
                    commentService.increaseLikeCount(testComment.commentId)
                } catch (e: Exception) {
                    println("Error in normal like thread: ${e.message}")
                } finally {
                    latch.countDown()
                }
            }
        }

        // Wait for all threads to complete (timeout after 10 seconds)
        latch.await(10, TimeUnit.SECONDS)
        executor.shutdown()

        // Refresh comment from database
        val updatedComment = commentRepository.findById(testComment.commentId).get()

        // Calculate expected and actual likes
        val expectedLikes = beforeLikes + threadCount
        val actualLikes = updatedComment.likes

        println("Normal like increase test:")
        println("Expected likes: $expectedLikes")
        println("Actual likes: $actualLikes")
        println("Difference: ${expectedLikes - actualLikes}")

        // This assertion will likely fail due to race conditions
        assertEquals(expectedLikes, actualLikes,
            "Normal like increase should have race conditions, resulting in less than $threadCount increments")
    }

    @Test
    fun `test optimistic locking like increase with concurrency resolution`() {
        val threadCount = 50 // 100개는 실패, 50개는 성공
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // Before test
        val commentId = testComment.commentId
        commentRepository.findById(commentId).ifPresent {
            it.likes = 0
            commentRepository.save(it)
        }

        val beforeLikes = commentRepository.findById(commentId).get().likes
        println("Starting optimistic locking test with likes = $beforeLikes")

        // Launch 100 concurrent threads to like the comment
        for (i in 1..threadCount) {
            executor.submit {
                try {
                    commentService.increaseLikeCountWithOptimisticLocking(commentId)
                } catch (e: Exception) {
                    println("Error in optimistic locking thread: ${e.message}")
                } finally {
                    latch.countDown()
                }
            }
        }

        // Wait for all threads to complete (timeout after 20 seconds to allow for retries)
        latch.await(20, TimeUnit.SECONDS)
        executor.shutdown()

        // Refresh comment from database
        val updatedComment = commentRepository.findById(commentId).get()

        // Calculate expected and actual likes
        val expectedLikes = beforeLikes + threadCount
        val actualLikes = updatedComment.likes

        println("Optimistic locking test:")
        println("Expected likes: $expectedLikes")
        println("Actual likes: $actualLikes")
        println("Difference: ${expectedLikes - actualLikes}")

        // This assertion should pass with optimistic locking
        assertEquals(expectedLikes, actualLikes,
            "Optimistic locking should handle concurrency correctly, resulting in exactly $threadCount increments")
    }
}