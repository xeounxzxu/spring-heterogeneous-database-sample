package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,
    val name: String
)
