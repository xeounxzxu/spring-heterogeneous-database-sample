package io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.repository

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {
}
