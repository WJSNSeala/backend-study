package com.example.springmysqldemo.service

import com.example.springmysqldemo.entity.DockerIncrease
import com.example.springmysqldemo.repository.DockerIncreaseRepository
import org.springframework.stereotype.Service

@Service
class DockerIncreaseService(private val repository: DockerIncreaseRepository) {

    fun getValue(): Map<String, Int> {
        // 첫 번째 값을 가져옴
        val dockerIncrease = repository.findAll().firstOrNull()
            ?: throw RuntimeException("No data found in docker_increase_preserve table")

        return mapOf("val" to dockerIncrease.value)
    }
}