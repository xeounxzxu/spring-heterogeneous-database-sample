package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory

import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.support.MysqlQuerydslSupport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, UserCustomRepository


interface UserCustomRepository {
    fun findAll2(): List<UserEntity>
}

class UserCustomRepositoryImpl : UserCustomRepository, MysqlQuerydslSupport(UserEntity::class.java) {

    val userQEntity = QUserEntity.userEntity

    override fun findAll2(): List<UserEntity> {
        return selectFrom(userQEntity).fetch()
    }
}
