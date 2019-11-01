package com.github.felipewom.springboot

import javax.sql.DataSource

data class SpringDatabase(val status: String, val database: String)

object SpringHealthEndPoint {
    fun getApplicationHealthStatus(dataSource: DataSource?) : SpringHealth {
        lateinit var health: SpringHealth
        val conn = dataSource?.connection
        try {
            val map = hashMapOf<String, Any>()
            var status: SpringStatus = SpringStatus.DOWN
            map["database"] = SpringDatabase(status.code, "~")
            conn?.let {
                if (it.isValid(10_000)) {
                    status = SpringStatus.UP
                }
                map["database"] = SpringDatabase(SpringStatus.UP.code, it.metaData.driverName)
            }
            health = SpringHealth(status.code, map)
            return health
        } catch (e: Exception) {
            conn?.close()
            val status = SpringStatus.DOWN
            status.description = e.message
            health = SpringHealth(status)
            return health
        } finally {
            if (conn != null && !conn.isClosed) {
                conn.close()
            }
        }
    }
}