package com.example.boardapipractice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry


@SpringBootApplication
@EnableRetry

class BoardApiPracticeApplication

fun main(args: Array<String>) {
	runApplication<BoardApiPracticeApplication>(*args)
}
