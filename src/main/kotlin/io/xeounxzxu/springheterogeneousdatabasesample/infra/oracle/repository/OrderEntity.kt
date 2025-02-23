package io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.repository

import jakarta.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    val id: Long? = null,
    val customerId: Long
)
