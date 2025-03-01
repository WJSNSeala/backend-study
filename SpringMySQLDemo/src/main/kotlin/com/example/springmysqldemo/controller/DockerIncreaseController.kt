package com.example.springmysqldemo.controller

import com.example.springmysqldemo.service.DockerIncreaseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DockerIncreaseController(private val service: DockerIncreaseService) {

    @GetMapping("/increase")
    fun increaseValue(): Map<String, Int> {
        return service.increaseValue()
    }

    @GetMapping("/view")
    fun getValue(): Map<String, Int> {
        return service.getValue()
    }
}