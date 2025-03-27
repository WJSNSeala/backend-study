package com.example.ddd.service

import com.example.ddd.domain.wallet.Wallet
import com.example.ddd.domain.wallet.WalletId
import com.example.ddd.domain.wallet.WalletRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

open class TransferService(
    private val walletRepository: WalletRepository
) {

    // application : 가져오고 저장하기 신경 씀
    // 이걸 가져다 쓰고 싶은 사람은 id로만 가져다 쓰면 됨

    // transaction 처리

    // 데이터 변경 -> domain service에 위임

    @Transactional
    open fun createWallet(userName: String): WalletId {
        val newWallet = Wallet.createBadCreditWallet(userName)

        walletRepository.save(newWallet)

        return newWallet.id;
    }

    @Transactional
    // application : 가져오고 저장하기 신경 씀
    open fun transfer(walletIdFrom: WalletId, walletIdTo: WalletId, amount: Long) {
        val walletFrom = walletRepository.findByIdOrNull(walletIdFrom) ?: throw RuntimeException("from wallet 찾을 수 없음")
        val walletTo = walletRepository.findByIdOrNull(walletIdTo) ?: throw RuntimeException("to wallet 찾을 수 없음")


        walletFrom.transfer(walletTo, amount)

        // walletFrom.amount -= amount; 해버릴 수도 있다

        walletRepository.save(walletFrom)
        walletRepository.save(walletTo)
    }

    @Transactional
    // application : 가져오고 저장하기 신경 씀
    open fun rescoreWalletId(walletId: WalletId) {
        val wallet = walletRepository.findByIdOrNull(walletId) ?: throw RuntimeException("wallet 찾을 수 없음")

        wallet.rescoreCredit()

        walletRepository.save(wallet)
    }

    @Transactional
    // application : 가져오고 저장하기 신경 씀
    open fun loan(walletId: WalletId, amount: Long) {
        val wallet = walletRepository.findByIdOrNull(walletId) ?: throw RuntimeException("wallet 찾을 수 없음")

        wallet.loan(amount)

        walletRepository.save(wallet)
    }

    @Transactional(readOnly = true)
    // application : 가져오고 저장하기 신경 씀
    open fun getNetAsset(walletId: WalletId): Long {
        val wallet = walletRepository.findByIdOrNull(walletId) ?: throw RuntimeException("wallet 찾을 수 없음")

        return wallet.getNetAsset()
    }
}