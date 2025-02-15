package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory

import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.support.MysqlQuerydslSupport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, UserCustomRepository


interface UserCustomRepository {
    fun findByUsername(username: String): UserEntity?
}

class UserCustomRepositoryImpl : UserCustomRepository, MysqlQuerydslSupport(UserEntity::class.java) {
    override fun findByUsername(username: String): UserEntity? {
        TODO("Not yet implemented")
    }
}
