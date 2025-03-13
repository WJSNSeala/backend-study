package com.example.boardapipractice.service

import com.example.boardapipractice.entity.Post
import com.example.boardapipractice.repository.PostRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class PostServiceConcurrencyTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var testPost: Post
    private lateinit var testPostForLock: Post

    private val threadCount = 100

    @BeforeEach
    fun setup() {
        // 일반 증가 테스트용 게시물
        testPost = postRepository.save(
            Post(
                title = "일반 증가 테스트 게시물",
                content = "동시성 테스트 내용",
                author = "테스터",
                password = "test123",
                likes = 0
            )
        )

        // 비관적 락 테스트용 게시물
        testPostForLock = postRepository.save(
            Post(
                title = "비관적 락 테스트 게시물",
                content = "동시성 테스트 내용",
                author = "테스터",
                password = "test123",
                likes = 0
            )
        )
    }

    @Test
    fun `일반 증가 방식으로 100개 스레드에서 좋아요 증가시키기 테스트`() {
        // 테스트 실행
        val finalLikes = runConcurrencyTest { postService.incrementLikes(testPost.postId) }

        // 결과 검증
        println("일반 방식 최종 좋아요 수: $finalLikes")
        assertEquals(threadCount, finalLikes, "일반 방식: 좋아요 수가 기대한 값과 다릅니다. 동시성 문제가 있을 수 있습니다.")
    }

    @Test
    fun `비관적 락 방식으로 100개 스레드에서 좋아요 증가시키기 테스트`() {
        // 테스트 실행
        val finalLikes = runConcurrencyTest { postService.incrementLikesWithPessimisticLock(testPostForLock.postId) }

        // 결과 검증
        println("비관적 락 방식 최종 좋아요 수: $finalLikes")
        assertEquals(threadCount, finalLikes, "비관적 락 방식: 좋아요 수가 기대한 값과 다릅니다.")
    }

    // 동시성 테스트를 실행하는 헬퍼 메서드
    private fun runConcurrencyTest(incrementFunction: () -> Unit): Int {
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 좋아요 증가 실행
        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    // 전달받은 함수 실행
                    incrementFunction()
                } finally {
                    latch.countDown()
                }
            }
        }

        // 모든 스레드가 작업을 마칠 때까지 대기
        latch.await()
        executorService.shutdown()

        // 테스트한 Post ID 확인
        val postId = if (incrementFunction.toString().contains("normal")) {
            testPost.postId
        } else {
            testPostForLock.postId
        }

        // 결과 확인을 위해 DB에서 최신 상태의 게시물 조회
        val updatedPost = postRepository.findById(postId).orElseThrow()
        return updatedPost.likes
    }
}