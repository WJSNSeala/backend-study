package com.example.ddd.repository

import com.example.ddd.domain.wallet.Wallet
import com.example.ddd.domain.wallet.WalletId
import com.example.ddd.domain.wallet.WalletRepository
import com.example.ddd.repository.jpa.WalletJPAEntity
import com.example.ddd.repository.jpa.WalletJPARepository
import org.springframework.transaction.annotation.Transactional

class WalletRepositoryImpl (
private val walletJPARepository: WalletJPARepository
): WalletRepository
{
    @Transactional
    fun saveWallet(wallet: Wallet) {
        val walletJPAEntity = WalletJPAEntity(
            id = wallet.id.id,
            userName = wallet.userName,
            creditStatus = wallet.creditStatus.toString(),
            amount = wallet.amount,
            debt = wallet.debt
        )

        walletJPARepository.save(walletJPAEntity)
    }



    fun findWallet(walletId: WalletId): Wallet? {
        TODO()
    }
}