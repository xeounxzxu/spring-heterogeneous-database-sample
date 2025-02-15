package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
class JpaConfiguration {

    companion object {
        const val UNIT_NAME = "mysql"
        const val MYSQL_BASE = "io.xeounxzxu.springheterogeneousdatabasesample."
//        const val MYSQL_PLATFORM = "org.hibernate.dialect.MySQLDialect"
    }

    @Bean
    fun entityManagerFactory(
        @Qualifier("dataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactory = LocalContainerEntityManagerFactoryBean()
        entityManagerFactory.dataSource = dataSource
        entityManagerFactory.setPackagesToScan(MYSQL_BASE)
        entityManagerFactory.jpaVendorAdapter = jpaVendorAdapter()
        entityManagerFactory.persistenceUnitName = UNIT_NAME

        return entityManagerFactory
    }

    private fun jpaVendorAdapter(): JpaVendorAdapter {
        val hibernateJpaVendorAdapter = HibernateJpaVendorAdapter()
        hibernateJpaVendorAdapter.setGenerateDdl(false)
        hibernateJpaVendorAdapter.setShowSql(false)
//        hibernateJpaVendorAdapter.setDatabasePlatform(MYSQL_PLATFORM)
        return hibernateJpaVendorAdapter
    }

    @Bean
    fun transactionManager(
        @Qualifier("entityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.entityManagerFactory = entityManagerFactory.getObject()
        return jpaTransactionManager
    }
}
