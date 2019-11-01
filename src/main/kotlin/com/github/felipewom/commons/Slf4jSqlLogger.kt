@file:JvmName("Slf4jSqlLogger")
package com.github.felipewom.commons

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.slf4j.LoggerFactory

object Slf4jSqlLogger : SqlLogger {
    override fun log (context: StatementContext, transaction: Transaction) {
        LoggerFactory.getLogger(Slf4jSqlLogger::class.qualifiedName).info("SQL DEBBUGER: ${context.expandArgs(transaction)}")
    }
}