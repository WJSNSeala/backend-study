package com.example.springmysqldemo.repository

import com.example.springmysqldemo.entity.DockerIncrease
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DockerIncreaseRepository : JpaRepository<DockerIncrease, Long>