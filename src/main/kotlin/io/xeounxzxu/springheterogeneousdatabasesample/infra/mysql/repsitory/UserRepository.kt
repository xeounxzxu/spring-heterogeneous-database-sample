package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>
