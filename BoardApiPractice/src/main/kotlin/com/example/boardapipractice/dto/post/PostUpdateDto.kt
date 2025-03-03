package com.example.boardapipractice.dto.post

import com.example.boardapipractice.entity.Post

data class PostUpdateDto(
    val title: String?,
    val content: String?,
) {
    fun updateEntity(existingPost: Post): Post {
        title?.let { // title이 Null이 아닌 경우에만 수행
            // let : 객체를 인자로 받아 람다식 내에서 수행하고 결과를 반환
            existingPost.title = it // 이 경우 it은 title
        }
        content?.let {
            existingPost.content = it
        }

        return existingPost
    }

}
