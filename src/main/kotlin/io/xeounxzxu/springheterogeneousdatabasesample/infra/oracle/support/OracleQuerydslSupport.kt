package io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.support

import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.properties.Delegates

abstract class OracleQuerydslSupport(
    domainClass: Class<*>
) : QuerydslRepositorySupport(domainClass) {

    private var queryDslFactory: JPAQueryFactory by Delegates.notNull()

    @PersistenceContext(
        unitName = ""
    )
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
        queryDslFactory = JPAQueryFactory(entityManager)
    }

    protected fun getDsl(): JPAQueryFactory {
        return queryDslFactory
    }

    protected fun <T> select(expr: Expression<T>): JPAQuery<T> {
        return queryDslFactory.select(expr)
    }

    protected fun <T> selectFrom(from: EntityPath<T>): JPAQuery<T> {
        return queryDslFactory.selectFrom(from)
    }

    protected fun <T> selectProjection(
        projections: ConstructorExpression<T>
    ): JPAQuery<T> {
        return queryDslFactory.select(projections)
    }
}

