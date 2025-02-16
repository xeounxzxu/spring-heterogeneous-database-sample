package io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.config

import com.zaxxer.hikari.HikariDataSource
import io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.config.OracleDataSourceConfig.Companion.BASE_PACKAGES
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@EntityScan(BASE_PACKAGES)
@EnableJpaRepositories(
    basePackages = [BASE_PACKAGES],
    entityManagerFactoryRef = "oracleEntityManagerFactory",
    transactionManagerRef = "oracleTransactionManager",
)
class OracleDataSourceConfig {

    companion object {
        const val BASE_PACKAGES = "io.xeounxzxu.springheterogeneousdatabasesample.infra.oracle.repository"
        const val MASTER_ORACLE_DATASOURCE = "masterOracleDataSource"
        const val SLAVE_ORACLE_DATASOURCE = "slaveOracleDataSource"
    }

    @Bean(MASTER_ORACLE_DATASOURCE)
    @ConfigurationProperties(prefix = "oracle.datasource.hikari.master")
    fun masterDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(SLAVE_ORACLE_DATASOURCE)
    @ConfigurationProperties(prefix = "oracle.datasource.hikari.slave")
    fun slaveDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    fun oracleRoutingDataSource(
        @Qualifier(MASTER_ORACLE_DATASOURCE) masterDataSource: DataSource,
        @Qualifier(SLAVE_ORACLE_DATASOURCE) slaveDataSource: DataSource
    ): DataSource {

        val routingDataSource = RoutingDataSource()

        routingDataSource.setTargetDataSources(
            mapOf(
                "master" to masterDataSource,
                "slave" to slaveDataSource
            )
        )

        routingDataSource.setDefaultTargetDataSource(masterDataSource)

        return routingDataSource
    }

    @Bean
    fun oracleDataSource(@Qualifier("oracleRoutingDataSource") oracleRoutingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(oracleRoutingDataSource)
    }

    @Bean
    fun oracleEntityManagerFactory(
        @Qualifier("oracleRoutingDataSource") oracleRoutingDataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            dataSource = oracleRoutingDataSource
            setPackagesToScan(BASE_PACKAGES)
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            persistenceUnitName = "oracle"
            setJpaPropertyMap(
                mapOf(
                    "hibernate.dialect" to "org.hibernate.dialect.OracleDialect",
                    "hibernate.hbm2ddl.auto" to "update",
                    "hibernate.temp.use_jdbc_metadata_defaults" to false
                )
            )
        }
    }

    @Bean
    fun oracleTransactionManager(
        @Qualifier("oracleEntityManagerFactory") oracleEntityManagerFactory: LocalContainerEntityManagerFactoryBean,
    ): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            this.entityManagerFactory = oracleEntityManagerFactory.getObject()
        }
    }
}


class RoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        val isReadOnly: Boolean =
            TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        println("isReadOnly ========> $isReadOnly")
        return if (isReadOnly) "slave" else "master"
    }
}
