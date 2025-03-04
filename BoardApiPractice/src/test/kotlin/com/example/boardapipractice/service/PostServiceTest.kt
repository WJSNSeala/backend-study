package com.example.boardapipractice.service

import com.example.boardapipractice.dto.post.PostCreateDto
import com.example.boardapipractice.dto.post.PostUpdateDto
import com.example.boardapipractice.entity.Post
import com.example.boardapipractice.repository.PostRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

@ExtendWith(MockitoExtension::class)
class PostServiceTest {

    @Mock
    private lateinit var postRepository: PostRepository

    @InjectMocks
    private lateinit var postService: PostService

    private lateinit var testPost: Post
    private lateinit var testPostCreateDto: PostCreateDto
    private lateinit var testPostUpdateDto: PostUpdateDto
    private lateinit var testPosts: List<Post>
    private val totalPosts = 25
    private val pageSize = 10

    @BeforeEach
    fun setUp() {
        // 단일 테스트용 데이터 준비
        testPost = Post(
            postId = 1L,
            title = "Test Title",
            content = "Test Content",
            author = "Test Author",
            password = "password123"
        )

        testPostCreateDto = PostCreateDto(
            title = "New Post Title",
            content = "New Post Content",
            author = "New Author",
            password = "newpassword123"
        )

        testPostUpdateDto = PostUpdateDto(
            title = "Updated Title",
            content = "Updated Content"
        )

        // 페이지네이션 테스트용 다수의 데이터 준비
        testPosts = (1..totalPosts).map { index ->
            Post(
                postId = index.toLong(),
                title = "Test Title $index",
                content = "Test Content $index",
                author = "Test Author",
                password = "password123"
            )
        }
    }

    @Test
    @DisplayName("모든 게시물 조회 테스트 - 페이지네이션 검증")
    fun getAllPostsWithPaginationTest() {
        // Arrange

        // 첫 번째 페이지 테스트 (0번 페이지, 10개 항목)
        val firstPageable = PageRequest.of(0, pageSize)
        val firstPageContent = testPosts.subList(0, pageSize)
        val firstPage = PageImpl(firstPageContent, firstPageable, totalPosts.toLong())

        // 두 번째 페이지 테스트 (1번 페이지, 10개 항목)
        val secondPageable = PageRequest.of(1, pageSize)
        val secondPageContent = testPosts.subList(pageSize, pageSize * 2)
        val secondPage = PageImpl(secondPageContent, secondPageable, totalPosts.toLong())

        // 마지막 페이지 테스트 (2번 페이지, 5개 항목)
        val lastPageable = PageRequest.of(2, pageSize)
        val lastPageContent = testPosts.subList(pageSize * 2, totalPosts)
        val lastPage = PageImpl(lastPageContent, lastPageable, totalPosts.toLong())

        // Mock 설정
        `when`(postRepository.findAll(firstPageable)).thenReturn(firstPage)
        `when`(postRepository.findAll(secondPageable)).thenReturn(secondPage)
        `when`(postRepository.findAll(lastPageable)).thenReturn(lastPage)

        // Act & Assert - 첫 번째 페이지 테스트
        val firstResult = postService.getAllPosts(firstPageable)
        assertEquals(pageSize, firstResult.content.size)
        assertEquals(0, firstResult.number) // 페이지 번호
        assertEquals(pageSize, firstResult.size) // 페이지 크기
        assertEquals(totalPosts.toLong(), firstResult.totalElements) // 전체 항목 수
        assertEquals(3, firstResult.totalPages) // 전체 페이지 수
        assertEquals(testPosts[0].postId, firstResult.content[0].postId)
        assertEquals(testPosts[9].postId, firstResult.content[9].postId)

        // Act & Assert - 두 번째 페이지 테스트
        val secondResult = postService.getAllPosts(secondPageable)
        assertEquals(pageSize, secondResult.content.size)
        assertEquals(1, secondResult.number) // 페이지 번호
        assertEquals(testPosts[10].postId, secondResult.content[0].postId)
        assertEquals(testPosts[19].postId, secondResult.content[9].postId)

        // Act & Assert - 마지막 페이지 테스트 (항목이 더 적음)
        val lastResult = postService.getAllPosts(lastPageable)
        assertEquals(5, lastResult.content.size) // 마지막 페이지는 5개 항목만 있음
        assertEquals(2, lastResult.number) // 페이지 번호
        assertEquals(testPosts[20].postId, lastResult.content[0].postId)
        assertEquals(testPosts[24].postId, lastResult.content[4].postId)
        assertTrue(lastResult.isLast) // 마지막 페이지 여부

        // 각 페이지별로 findAll이 한 번씩만 호출되었는지 검증
        verify(postRepository, times(1)).findAll(firstPageable)
        verify(postRepository, times(1)).findAll(secondPageable)
        verify(postRepository, times(1)).findAll(lastPageable)
    }

    @Test
    @DisplayName("ID로 게시물 조회 테스트 - 성공 케이스")
    fun getPostByIdSuccessTest() {
        // Arrange
        `when`(postRepository.findById(1L)).thenReturn(Optional.of(testPost))

        // Act
        val result = postService.getPostById(1L)

        // Assert
        assertEquals(testPost.postId, result.postId)
        assertEquals(testPost.title, result.title)
        assertEquals(testPost.content, result.content)
        verify(postRepository, times(1)).findById(1L)
    }

    @Test
    @DisplayName("ID로 게시물 조회 테스트 - 실패 케이스")
    fun getPostByIdFailureTest() {
        // Arrange
        `when`(postRepository.findById(999L)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(NoSuchElementException::class.java) {
            postService.getPostById(999L)
        }
        assertTrue(exception.message!!.contains("999"))
        verify(postRepository, times(1)).findById(999L)
    }

    @Test
    @DisplayName("새 게시물 저장 테스트")
    fun createPostTest() {
        // Arrange
        val newPost = testPostCreateDto.toEntity()
        val savedPost = Post(
            postId = 1L,
            title = newPost.title,
            content = newPost.content,
            author = newPost.author,
            password = newPost.password
        )

        `when`(postRepository.save(any(Post::class.java))).thenReturn(savedPost)

        // Act
        val result = postService.createPost(testPostCreateDto)

        // Assert
        assertEquals(savedPost.postId, result.postId)
        assertEquals(savedPost.title, result.title)
        assertEquals(savedPost.content, result.content)
        verify(postRepository, times(1)).save(any(Post::class.java))
    }

    @Test
    @DisplayName("게시물 수정 테스트 - 성공 케이스")
    fun updatePostSuccessTest() {
        // Arrange
        val existingPost = testPost.copy()
        val updatedPost = existingPost.copy().apply {
            title = testPostUpdateDto.title ?: title
            content = testPostUpdateDto.content ?: content
        }

        `when`(postRepository.findById(1L)).thenReturn(Optional.of(existingPost))
        `when`(postRepository.save(any(Post::class.java))).thenReturn(updatedPost)

        // Act
        val result = postService.updatePost(1L, testPostUpdateDto)

        // Assert
        assertEquals(updatedPost.postId, result.postId)
        assertEquals(updatedPost.title, result.title)
        assertEquals(updatedPost.content, result.content)
        verify(postRepository, times(1)).findById(1L)
        verify(postRepository, times(1)).save(any(Post::class.java))
    }

    @Test
    @DisplayName("게시물 수정 테스트 - 실패 케이스")
    fun updatePostFailureTest() {
        // Arrange
        `when`(postRepository.findById(999L)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(NoSuchElementException::class.java) {
            postService.updatePost(999L, testPostUpdateDto)
        }
        assertTrue(exception.message!!.contains("999"))
        verify(postRepository, times(1)).findById(999L)
        verify(postRepository, never()).save(any(Post::class.java))
    }

    @Test
    @DisplayName("게시물 삭제 테스트 - 성공 케이스")
    fun deletePostSuccessTest() {
        // Arrange
        `when`(postRepository.existsById(1L)).thenReturn(true)
        doNothing().`when`(postRepository).deleteById(1L)

        // Act
        postService.deletePost(1L)

        // Assert
        verify(postRepository, times(1)).existsById(1L)
        verify(postRepository, times(1)).deleteById(1L)
    }

    @Test
    @DisplayName("게시물 삭제 테스트 - 실패 케이스")
    fun deletePostFailureTest() {
        // Arrange
        `when`(postRepository.existsById(999L)).thenReturn(false)

        // Act & Assert
        val exception = assertThrows(NoSuchElementException::class.java) {
            postService.deletePost(999L)
        }
        assertTrue(exception.message!!.contains("999"))
        verify(postRepository, times(1)).existsById(999L)
        verify(postRepository, never()).deleteById(999L)
    }

    @Test
    @DisplayName("게시물 존재 여부 확인 테스트 - 존재하는 경우")
    fun existsByIdTrueTest() {
        // Arrange
        `when`(postRepository.existsById(1L)).thenReturn(true)

        // Act
        val result = postService.existsById(1L)

        // Assert
        assertTrue(result)
        verify(postRepository, times(1)).existsById(1L)
    }

    @Test
    @DisplayName("게시물 존재 여부 확인 테스트 - 존재하지 않는 경우")
    fun existsByIdFalseTest() {
        // Arrange
        `when`(postRepository.existsById(999L)).thenReturn(false)

        // Act
        val result = postService.existsById(999L)

        // Assert
        assertFalse(result)
        verify(postRepository, times(1)).existsById(999L)
    }
}