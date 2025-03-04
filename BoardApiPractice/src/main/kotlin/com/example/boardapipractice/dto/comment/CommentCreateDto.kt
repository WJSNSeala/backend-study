package com.example.boardapipractice.dto.comment

import com.example.boardapipractice.entity.Comment

data class CommentCreateDto(
    val content: String,
    val author: String,
    val password: String,
    val parentPostId: Long
) {
    fun toEntity() = Comment(
        content = content,
        author = author,
        password = password,
        parentPostId = parentPostId
    )
}
