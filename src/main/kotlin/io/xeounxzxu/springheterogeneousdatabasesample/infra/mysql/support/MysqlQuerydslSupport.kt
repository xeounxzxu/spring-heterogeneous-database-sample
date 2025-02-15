package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.support

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

abstract class MysqlQuerydslSupport(
    domainClass: Class<*>
) : QuerydslRepositorySupport(domainClass) {

    // TODO(Kal): JPAQueryFactory 을 주입을 하도록 추후에 변경
// private lateinit var queryDslFactory: JPAQueryFactory

    @PersistenceContext
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }
}
