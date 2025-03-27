package com.example.ddd.application

import com.example.ddd.domain.wallet.WalletId
import com.example.ddd.service.TransferService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TransferController(
    private val transferService: TransferService
) {
    @PostMapping('/wallet/{userName}')
    fun createWallet(userName: String): WalletId {
        try {
            transferService.createWallet(userName)
        } catch (e: Exception) {
            throw RuntimeException("Could not create wallet")
        }
    }

    // 예외처리 + 입출력 & depend on service
}