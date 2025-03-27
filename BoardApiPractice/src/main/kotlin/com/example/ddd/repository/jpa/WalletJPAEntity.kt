package com.example.ddd.repository.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id

// JAP용 entity
// 저장용
@Entity
class WalletJPAEntity(
    @Id
    val id: String, // 유일함
    val userName: String,
    var creditStatus: String,
    var amount: Long,
    var debt: Long,
) {

}