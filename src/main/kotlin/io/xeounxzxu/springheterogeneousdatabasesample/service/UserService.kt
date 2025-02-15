package io.xeounxzxu.springheterogeneousdatabasesample.service

import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory.UserEntity
import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true, transactionManager = "mysqlTransactionManager")
    fun getAll(): List<UserEntity> {

        val beforeIsReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        println("🔍 Transaction readOnly status: $beforeIsReadOnly")

        val data =  userRepository.findAll()

        val afterIsReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        println("🔍 Transaction readOnly status: $afterIsReadOnly")

        return data
    }
}
