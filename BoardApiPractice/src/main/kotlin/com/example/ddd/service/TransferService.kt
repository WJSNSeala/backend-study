package com.example.ddd.service

import com.example.ddd.domain.wallet.Wallet
import com.example.ddd.domain.wallet.WalletId
import com.example.ddd.domain.wallet.WalletRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

open class TransferService(
    private val walletRepository: WalletRepository,
    @Qualifier("caffeineCacheManager") private val cacheManager: CacheManager,
) {
    private val walletCache = cacheManager.getCache("wallet")!!

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

    @Cacheable(
//        keyGenerator = "", // custom key generator
//        condition = "", // SpEL에 따라 언제 캐시할지를 결정

//        cacheManager = "redisCacheManager", // 이 함수의 캐시를 관리할 캐시매니저의 qualifier를 지정
//        value = "", // zo
    ) // in-memory cache
    @Transactional(readOnly = true)
    // application : 가져오고 저장하기 신경 씀
    open fun getNetAsset(walletId: WalletId): Long {
        val wallet = walletRepository.findByIdOrNull(walletId) ?: throw RuntimeException("wallet 찾을 수 없음")

        return wallet.getNetAsset()
    }
}