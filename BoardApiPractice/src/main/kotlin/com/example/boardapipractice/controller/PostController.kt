package com.example.boardapipractice.controller

import com.example.boardapipractice.dto.post.PostCreateDto
import com.example.boardapipractice.dto.post.PostUpdateDto
import com.example.boardapipractice.entity.Post
import com.example.boardapipractice.service.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    /**
     * 모든 게시물 조회
     *
     * @return 모든 게시물 목록
     */
    @GetMapping
    fun getAllPosts(
        @RequestParam(required = true) limit: Int,
        @RequestParam(required = false, defaultValue = "1") page: Int
    ): ResponseEntity<List<Post>> {
        if (limit < 1 || page < 1) {
            return ResponseEntity.badRequest().build()
        }

        val pageable = PageRequest.of(page-1, limit)
        val posts =  postService.getAllPosts(pageable)

        return ResponseEntity.ok(posts.content)
    }

    /**
     * ID로 게시물 조회
     *
     * @param postId 게시물 ID
     * @throws NoSuchElementException 게시물을 찾을 수 없는 경우
     */

    @GetMapping("/{postId}")
    fun getPostById(@PathVariable postId: Long): ResponseEntity<Post> {
        return try {
            val post = postService.getPostById(postId)
            ResponseEntity.ok(post)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * 새 게시물 저장
     *
     * @param post 저장할 게시물 객체
     * @return 저장된 게시물 객체 (ID가 할당됨)
     */
    @PostMapping
    fun createPost(@RequestBody postCreateDto: PostCreateDto ): ResponseEntity<Post> {
        val createdPost = postService.createPost(postCreateDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost)
    }

    /**
     * 게시물 수정
     *
     * @param postId 수정할 게시물의 ID
     * @param postUpdateDto 수정할 Post의 content 내용이 담긴 게시물 객체
     * PostUpdateDto class 참조
     *
     * @return 수정된 게시물 객체
     * @throws NoSuchElementException 게시물을 찾을 수 없는 경우
     */
    @PutMapping("/{postId}")
    fun updatePost(@PathVariable postId: Long, @RequestBody postUpdateDto: PostUpdateDto): ResponseEntity<Post> {
        return try {
            val updatedPost = postService.updatePost(postId, postUpdateDto)
            ResponseEntity.ok(updatedPost)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * 게시물 삭제
     *
     * @param postId 삭제할 게시물의 ID
     * @throws NoSuchElementException 게시물을 찾을 수 없는 경우
     */
    @DeleteMapping("/{postId}")
    fun deletePost(@PathVariable postId: Long): ResponseEntity<Unit> {
        return try {
            postService.deletePost(postId)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}