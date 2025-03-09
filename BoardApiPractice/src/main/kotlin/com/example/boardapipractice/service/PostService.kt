package com.example.boardapipractice.service

import com.example.boardapipractice.dto.post.PostCreateDto
import com.example.boardapipractice.dto.post.PostUpdateDto
import com.example.boardapipractice.entity.Post
import com.example.boardapipractice.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(private val postRepository: PostRepository) {

    /**
     * 모든 게시물 조회
     *
     * @return 모든 게시물 목록
     */
    fun getAllPosts(pageable: Pageable): Page<Post> {
        return postRepository.findAll(pageable)
    }

    /**
     * ID로 게시물 조회
     * @throws NoSuchElementException 게시물을 찾을 수 없는 경우
     */
    fun getPostById(postId: Long): Post {
        return postRepository.findById(postId).orElseThrow {
            NoSuchElementException("ID가 " + postId + "인 게시물을 찾을 수 없습니다")
        }
    }

    /**
     * 새 게시물 저장
     *
     * @param post 저장할 게시물 객체
     * @return 저장된 게시물 객체 (ID가 할당됨)
     */
    @Transactional
    fun createPost(postCreateDto: PostCreateDto): Post {
        val post = postCreateDto.toEntity()
        return postRepository.save(post)
    }

    /**
     * 게시물 좋아요 수 증가
     */
    @Transactional
    fun incrementLikes(postId: Long): Post {
        val post = postRepository.findById(postId).orElseThrow {
            NoSuchElementException("ID가 " + postId + "인 게시물을 찾을 수 없습니다")
        }

        post.likes += 1
        return postRepository.save(post)
    }

    /**
     * Pessimistic lock based query를 이용한 like 증가
     */
    @Transactional
    fun incrementLikesWithPessimisticLock(postId: Long): Post {
        val post = postRepository.findByIdWithPessimisticLock(postId).orElseThrow {
            NoSuchElementException("ID가 " + postId + "인 게시물을 찾을 수 없습니다")
        }

        post.likes += 1
        return postRepository.save(post)
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
    @Transactional
    fun updatePost(postId: Long, postUpdateDto: PostUpdateDto): Post {

        val existingPost = postRepository.findById(postId).orElseThrow {
            NoSuchElementException("ID가 " + postId + "인 게시물을 찾을 수 없습니다")
        }

        // 기존 게시물 정보 업데이트
        postUpdateDto.updateEntity(existingPost)

        return postRepository.save(existingPost)
    }

    /**
     * 게시물 삭제
     *
     * @param postId 삭제할 게시물의 ID
     * @throws NoSuchElementException 게시물을 찾을 수 없는 경우
     */
    @Transactional
    fun deletePost(postId: Long) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId)
        } else {
            throw NoSuchElementException("ID가 " + postId + "인 게시물을 찾을 수 없습니다")
        }
    }

    /**
     * 게시물 존재 여부 확인
     *
     * @param postId 게시물 ID
     * @return 게시물 존재 여부
     */

    fun existsById(postId: Long): Boolean {
        return postRepository.existsById(postId)
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }
}