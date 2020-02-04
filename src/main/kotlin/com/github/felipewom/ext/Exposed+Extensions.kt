package com.github.felipewom.ext

import com.github.felipewom.commons.PageableExposed
import com.github.felipewom.commons.PageableFields
import com.github.felipewom.commons.Slf4jSqlLogger
import com.github.felipewom.commons.logger
import com.github.felipewom.utils.GsonUtils
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Savepoint
import java.util.*
import javax.sql.DataSource

/*
* Executa raw sql e retorna map com o resultado
* ex:
* "select a.column, b.column from table_a a inner join table_b b where <expression>".execAndMap { rs ->
*    rs.getString("u.column") to rs.getString("c.column")
*  }
*
* */
fun <DTO : Any> String.execAndMap(transform: (ResultSet) -> DTO): List<DTO> {
    val result = arrayListOf<DTO>()
    TransactionManager.current().exec(this) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}

fun Transaction.exec(sql: String, body: PreparedStatement.() -> Unit): ResultSet? {
    return connection.prepareStatement(sql).apply(body).run {
        if (sql.toLowerCase().startsWith("select "))
            executeQuery()
        else {
            executeUpdate()
            resultSet
        }
    }
}

/**
 * *Query.addPagination* provides pagination functionality to Query DSL
 *
 * Adds pagination to current query SQL
 * @param pageable contains values to determine limite and order of query
 * @see <a href="https://github.com/JetBrains/Exposed/wiki/DSL">Query in docs</a>
 */
fun Query.addPagination(pageable: PageableExposed) {
    val copied = this.copy()
    try {
        pageable.totalSize = copied.toList().size
    } catch (e: Exception) {
        pageable.totalSize = 0
    }
    if (pageable.pageSize > 0) {
        this.limit(pageable.pageSize, pageable.offset)
    }
    pageable.orderByField?.let {
        this.orderBy(it)
    }
}

infix fun <T : Int?> ExpressionWithColumnType<T>.like(pattern: String): Op<Boolean> =
    LikeOp(this, QueryParameter(pattern, columnType))

fun SqlExpressionBuilder.buildLikeOp(column: Column<Int>, value: Int) = Op.build { column like value.toLike() }

fun SqlExpressionBuilder.buildLikeOp(column: Column<String>, value: String) = Op.build { column like value.toLike() }

fun Int?.toLike() =
    if (this != null) {
        "%${this}%"
    } else {
        "%%"
    }

fun String?.toLike() =
    if (this.isNotNullOrBlank()) {
        "%${this}%"
    } else {
        "%%"
    }


class InOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "IN")

infix fun <T, S1 : T?, S2 : T?> Expression<in S1>.inExpr(other: Expression<in S2>): Op<Boolean> = InOp(this, other)


fun <T> connectionTransactionNone(block: Transaction.() -> T): T {
    val dataSource by injectDependency<DataSource>()
    val database = Database.connect(dataSource)
    return transaction(Connection.TRANSACTION_NONE, 0, database) {
        addLogger(Slf4jSqlLogger)
        logTimeSpent("Request transactions statements") {
            return@transaction block()
        }
    }
}

private fun <T> connectionTransactionSafe(isolationLevel: Int, block: Transaction.() -> T): T {
    val dataSource by injectDependency<DataSource>()
    val database = Database.connect(dataSource)
    var transaction: Transaction? = null
    var connection: Connection? = null
    var savePoint: Savepoint? = null
    return try {
        transaction(isolationLevel, 0, database) {
            addLogger(Slf4jSqlLogger)
            logTimeSpent("Request transactions statements") {
                connection = this.connection
                transaction = this
                savePoint = connection?.setSavepoint()
                return@transaction block()
            }
        }
    } catch (e: Exception) {
        rollbackTransaction(connection, savePoint)
        e.printStackTrace()
        throw  e
    } finally {
        try {
            if (connection?.isClosed == false) {
                connection?.releaseSavepoint(savePoint)
                transaction?.let {
                    it.currentStatement?.closeOnCompletion()
                }
                connection?.close()
            }
        } catch (ex: Exception) {
            logger.error(ex.message ?: " -> Erro ao encerrar conexoes ativa na requisicao")
        }
    }
}

fun <T> connectionTransactionRepeatableRead(block: Transaction.() -> T) =
    connectionTransactionSafe(Connection.TRANSACTION_REPEATABLE_READ, block)

fun <T> connectionTransactionSerializable(block: Transaction.() -> T) =
    connectionTransactionSafe(Connection.TRANSACTION_SERIALIZABLE, block)

fun rollbackTransaction(connection: Connection?, savePoint: Savepoint?) {
    if (connection != null && !connection.isClosed) {
        connection.rollback(savePoint)
    }
}


abstract class BaseIntIdTable(name: String) : IntIdTable(name) {
    val createdAt = datetime("createdAt").clientDefault { currentUtc() }
    val updatedAt = datetime("updatedAt").nullable()
}

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable) : IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseIntEntityClass<E : BaseIntEntity>(table: BaseIntIdTable) : IntEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}

val BaseIntEntity.idValue: Int
    get() = this.id.value


abstract class BaseUUIDTable(name: String) : UUIDTable(name) {
    val createdAt = datetime(com.github.felipewom.commons.ApiConstants.CREATED_AT).clientDefault { currentUtc() }
    val updatedAt = datetime(com.github.felipewom.commons.ApiConstants.UPDATED_AT).nullable()
}

abstract class BaseUUIDEntity(id: EntityID<UUID>, table: BaseUUIDTable) : UUIDEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseUUIDEntityClass<E : BaseUUIDEntity>(table: BaseUUIDTable) : UUIDEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}

val BaseUUIDEntity.idValue: UUID
    get() = this.id.value



fun calculateOffset(pageNumber:Int, pageSize:Int): Int {
    if (pageNumber <= 0) {
        return 0
    }
    return (pageNumber-1) * pageSize
}

/**
 * Returns the DTO requested.
 * @return
 */
inline fun <reified DTO : Any> PageableExposed.getObjectFilter(): DTO? = tryOrNull {
    if (objectFilter != null) {
        return GsonUtils.deserialize(objectFilter!!, DTO::class.java)
    }
    return null
}


fun PageableExposed.toQueryMap(): Map<String, Any> {
    val queryMap = mutableMapOf<String, String>()
    try {
        queryMap[PageableFields.PAGE_NUMBER] = "${this.pageNumber}"
        queryMap[PageableFields.PAGE_SIZE] = "${this.pageSize}"
        this.orderBy?.let { orderBy ->
            queryMap[PageableFields.ORDER_BY] = orderBy
        }
        this.filter?.let { filter ->
            queryMap[PageableFields.FILTER] = filter
        }
        this.objectFilter?.let { filter ->
            queryMap[PageableFields.OBJECT_FILTER] = filter
        }
    } catch (e: Exception) {
        logger.error("[ERROR]Pageable::toQueryMap=>${e.message}")
    }
    return queryMap
}