package io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource


@Configuration
class MysqlDataSourceConfig {

    companion object {
        const val MASTER_MYSQL_DATASOURCE = "masterMysqlDataSource"
        const val SLAVE_MYSQL_DATASOURCE = "slaveMysqlDataSource"
    }

    @Bean(MASTER_MYSQL_DATASOURCE)
    @ConfigurationProperties(prefix = "mysql.datasource.master.hikari")
    fun masterDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(SLAVE_MYSQL_DATASOURCE)
    @ConfigurationProperties(prefix = "mysql.datasource.slave.hikari")
    fun slaveDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    fun routingDataSource(
        @Qualifier(MASTER_MYSQL_DATASOURCE) masterDataSource: DataSource,
        @Qualifier(SLAVE_MYSQL_DATASOURCE) slaveDataSource: DataSource
    ): DataSource {
        val routingDataSource: RoutingDataSource = RoutingDataSource()

        val datasourceMap: Map<Any, Any> =
            mapOf(
                "master" to masterDataSource,
                "slave" to slaveDataSource
            )
        routingDataSource.setTargetDataSources(datasourceMap)

        routingDataSource.setDefaultTargetDataSource(masterDataSource)

        return routingDataSource
    }

    @Primary
    @Bean
    fun dataSource(@Qualifier("routingDataSource") routingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}


class RoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        val isReadOnly: Boolean = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        return if (isReadOnly) "slave" else "master"
    }
}
