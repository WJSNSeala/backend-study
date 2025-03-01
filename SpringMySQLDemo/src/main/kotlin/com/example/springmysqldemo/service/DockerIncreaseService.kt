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

    fun increaseValue(): Map<String, Int> {
        // 첫 번째 값을 가져옴
        val dockerIncrease = repository.findAll().firstOrNull()
            ?: DockerIncrease().apply { value = 0 } // 데이터가 없으면 새로 생성

        // 값을 1 증가
        dockerIncrease.value += 1

        // 저장하고 증가된 값 반환
        val savedIncrease = repository.save(dockerIncrease)
        return mapOf("val" to savedIncrease.value)
    }
}