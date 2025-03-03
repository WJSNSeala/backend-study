package com.example.boardapipractice.dto.comment

import com.example.boardapipractice.entity.Comment

data class CommentUpdateDto(
    val content: String?
) {
    fun updateEntity(existingComment: Comment): Comment {
        content?.let {
            existingComment.content = it
        }

        return existingComment
    }
}
