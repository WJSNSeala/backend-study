package com.example.springmysqldemo.entity

import jakarta.persistence.*

@Entity
@Table(name = "docker_increase_preserve")
class DockerIncrease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "val")
    var value: Int = 0
}