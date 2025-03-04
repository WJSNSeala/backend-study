package com.example.boardapipractice.repository

import com.example.boardapipractice.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PostRepository: JpaRepository<Post, Long>
