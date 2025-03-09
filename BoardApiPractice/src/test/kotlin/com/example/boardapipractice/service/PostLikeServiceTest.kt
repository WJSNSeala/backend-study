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
    private lateinit var postService: PostService  // PostService에 incrementLikes 메서드가 있다고 가정

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var testPost: Post

    @BeforeEach
    fun setup() {
        // 테스트용 게시물 생성
        testPost = postRepository.save(
            Post(
                title = "테스트 게시물",
                content = "동시성 테스트 내용",
                author = "테스터",
                password = "test123",
                likes = 0
            )
        )
    }

    @Test
    fun `동시에 100개 스레드에서 좋아요 증가시키기 테스트`() {
        // 스레드 수 설정
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 좋아요 증가 실행
        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    // 좋아요 증가 메서드 호출
                    postService.incrementLikes(testPost.postId)
                } finally {
                    latch.countDown()
                }
            }
        }

        // 모든 스레드가 작업을 마칠 때까지 대기
        latch.await()
        executorService.shutdown()

        // 결과 검증: DB에서 최신 상태의 게시물 조회
        val updatedPost = postRepository.findById(testPost.postId).orElseThrow()

        // 모든 스레드가 각각 1씩 증가시켰다면 좋아요 수는 100이 되어야 함
        println("최종 좋아요 수: ${updatedPost.likes}")
        assertEquals(threadCount, updatedPost.likes, "좋아요 수가 기대한 값과 다릅니다. 동시성 문제가 있을 수 있습니다.")
    }
}