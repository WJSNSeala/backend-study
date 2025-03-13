package com.example.boardapipractice.entity

import jakarta.persistence.*

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Long = 0,

    @Column(name = "title", length = 50)
    var title: String,

    @Column(name = "content", columnDefinition = "text")
    var content: String,

    @Column(name = "author", length = 30)
    var author: String,

    @Column(name = "password", length=100)
    var password: String,

    @Column(name="likes", columnDefinition = "int default 0")
    var likes: Int = 0
)

