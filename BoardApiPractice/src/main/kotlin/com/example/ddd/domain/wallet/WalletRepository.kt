package com.example.ddd.domain.wallet

import org.springframework.data.jpa.repository.JpaRepository

interface WalletRepository: JpaRepository<Wallet, WalletId>