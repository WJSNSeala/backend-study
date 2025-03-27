package com.example.ddd.domain.wallet

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.util.*

// domain 객체, entity의 역할을 수행
@Entity
class Wallet(
    @Id val id: WalletId, // 유일함
    val userName: String,

    @Enumerated(EnumType.STRING) var creditStatus: CreditStatus,
) {
    var amount: Long = 0L;
    var debt: Long = 0L;


    fun transfer(walletTo: Wallet, transferAmount: Long) {
        // 핵심 로직만 수행
        check(amount <= transferAmount) {
            "잔고 부족"
        }

        this.amount -= transferAmount;
        walletTo.amount += transferAmount;
    }


    fun getNetAsset(): Long {
        return amount - debt;
    }

    fun rescoreCredit() {
        creditStatus =   when {
            debt == 0L -> CreditStatus.GOOD
            getNetAsset() < 10_000L -> CreditStatus.GOOD
            amount < 10_000L -> CreditStatus.BAD
            else -> CreditStatus.GOOD
        }
    }

    fun loan(loanAmount: Long) {
        // 요구사항 처리 시 require
        // 이 상태 자체가 있으면 안됨 check
        check(creditStatus == CreditStatus.GOOD) {
            "신용도가 낮아 대출이 불가능합니다."
        }

        amount += loanAmount;
        debt += loanAmount;
    }

    // entity는 value object와 다르게 동일성은 id로만 체크함
    // 이걸 정의 안하면 객체 동등성 비교로 바뀐다.
    // 객체를 proxy사용한다.
    // 프록싱된 월렛과 원래 원래 객체가 같다고 해줘야한다.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Wallet) return false // 이걸안하면 하위 클래스는 허용 안해줌

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        // toString 시 relation fetch 되지 않도록
        return "Wallet(userName='$userName', creditStatus=$creditStatus, amount=$amount, debt=$debt)"
    }


    companion object {
        fun createBadCreditWallet(userName: String): Wallet {
            val id = WalletId(UUID.randomUUID().toString())
            val wallet = Wallet(id, userName, creditStatus = CreditStatus.BAD);

            return wallet;
        }


    }
}

data  class WalletId(val id: String )