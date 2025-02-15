package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.config

import com.zaxxer.hikari.HikariDataSource
import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.config.MysqlDataSourceConfig.Companion.BASE_PACKAGES
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
    entityManagerFactoryRef = "mysqlEntityManagerFactory",
    transactionManagerRef = "mysqlTransactionManager",
)
class MysqlDataSourceConfig {

    companion object {
        const val BASE_PACKAGES = "io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory"
        const val MASTER_MYSQL_DATASOURCE = "masterMysqlDataSource"
        const val SLAVE_MYSQL_DATASOURCE = "slaveMysqlDataSource"
    }

    @Bean(MASTER_MYSQL_DATASOURCE)
    @ConfigurationProperties(prefix = "mysql.datasource.hikari.master")
    fun masterDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(SLAVE_MYSQL_DATASOURCE)
    @ConfigurationProperties(prefix = "mysql.datasource.hikari.slave")
    fun slaveDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    fun mysqlRoutingDataSource(
        @Qualifier(MASTER_MYSQL_DATASOURCE) masterDataSource: DataSource,
        @Qualifier(SLAVE_MYSQL_DATASOURCE) slaveDataSource: DataSource
    ): DataSource {

        val routingDataSource = RoutingDataSource()

        val datasourceMap: Map<Any, Any> =
            mapOf(
                "master" to masterDataSource,
                "slave" to slaveDataSource
            )

        routingDataSource.setTargetDataSources(datasourceMap)

        routingDataSource.setDefaultTargetDataSource(masterDataSource)

        return routingDataSource
    }

    @Bean
    fun mysqlDataSource(@Qualifier("mysqlRoutingDataSource") mysqlRoutingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(mysqlRoutingDataSource)
    }

    @Bean
    fun mysqlEntityManagerFactory(
        @Qualifier("mysqlRoutingDataSource") mysqlRoutingDataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            dataSource = mysqlRoutingDataSource
            setPackagesToScan(BASE_PACKAGES)
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setJpaPropertyMap(
                mapOf(
                    "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect",
                    "hibernate.hbm2ddl.auto" to "update",
                    "hibernate.temp.use_jdbc_metadata_defaults" to false
                )
            )
        }
    }

    @Bean
    fun mysqlTransactionManager(
        @Qualifier("mysqlEntityManagerFactory") mysqlEntityManagerFactory: LocalContainerEntityManagerFactoryBean,
    ): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            this.entityManagerFactory = mysqlEntityManagerFactory.getObject()
        }
    }
}


class RoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        val isReadOnly: Boolean = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        return if (isReadOnly) "slave" else "master"
    }
}
