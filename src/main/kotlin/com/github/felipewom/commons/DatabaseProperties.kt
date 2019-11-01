@file:JvmName("DatabaseProperties")
package com.github.felipewom.commons

import com.github.felipewom.ext.getEnvProp
import org.koin.core.scope.Scope
import java.util.concurrent.TimeUnit

data class DatabaseProperties(
    var jdbcUrl: String = "",
    var username: String = "",
    var password: String = "",
    var minimumIdle: Int = 0,
    var idleTimeout: Long = TimeUnit.MINUTES.toMillis(1),
    var maxLifeTime: Long = TimeUnit.MINUTES.toMillis(2),
    var isAutoCommit: Boolean = false,
    var driverClassName: String = "",
    var maximumPoolSize: Int = 10,
    var transactionIsolation: String = "",
    var connectionTestQuery: String = "SELECT 1"
) {
    companion object {
        fun build(scope: Scope): DatabaseProperties {
            return DatabaseProperties(
                jdbcUrl = getEnvProp(scope, "DB_URL", "DB_URL", "db_jdbc_url"),
                username = getEnvProp(scope, "SEDB_USER", "SEDB_USER", "db_username"),
                password = getEnvProp(scope, "SEDB_PASS", "SEDB_PASS", "db_password"),
                driverClassName = getEnvProp(
                    scope,
                    "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                    "DB_DRIVER",
                    "db_driver_class_name"
                ),
                idleTimeout = getEnvProp(scope, 1, "db_idle_timeout").toLong(),
                maxLifeTime = getEnvProp(
                    scope,
                    TimeUnit.MINUTES.toMillis(2).toInt(),
                    "db_max_life_time"
                ).toLong(),
                transactionIsolation = getEnvProp(
                    scope,
                    "TRANSACTION_REPEATABLE_READ",
                    "db_transaction_isolation"
                ),
                minimumIdle = getEnvProp(
                    scope,
                    TimeUnit.MINUTES.toMillis(1).toInt(),
                    "db_minimum_idle"
                ),
                maximumPoolSize = getEnvProp(
                    scope,
                    10,
                    "DB_MAXIMUM_POOL_SIZE",
                    "db_maximum_pool_size"
                ),
                isAutoCommit = getEnvProp(scope, false, "db_is_auto_commit")
            )
        }
    }
    override fun toString(): String {
        return "-> DatabaseProperties:\njdbcUrl='$jdbcUrl', \nusername='$username', \npassword='$password', \nminimumIdle=$minimumIdle, \nidleTimeout=$idleTimeout, \nmaxLifeTime=$maxLifeTime, \nisAutoCommit=$isAutoCommit, \ndriverClassName='$driverClassName', \nmaximumPoolSize=$maximumPoolSize, \ntransactionIsolation='$transactionIsolation', \nconnectionTestQuery='$connectionTestQuery'"
    }
}