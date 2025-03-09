package com.example.boardapipractice.entity

import jakarta.persistence.*

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var commentId: Long = 0,

    @Column(name = "content", columnDefinition = "text")
    var content: String,

    @Column(name = "author", length = 30)
    var author: String,

    @Column(name = "password", length=100)
    var password: String,

    // 직접 ID 값 접근용
    @Column(name = "parent_post_id")
    var parentPostId: Long,

    @Version
    var version: Long = 0,

    @Column(name="likes", columnDefinition = "int default 0")
    var likes: Int = 0
)
