package com.example.ddd.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface WalletJPARepository: JpaRepository<WalletJPAEntity, String> {}