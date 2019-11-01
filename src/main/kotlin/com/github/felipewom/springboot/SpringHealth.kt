package com.github.felipewom.springboot

import com.github.felipewom.commons.AppProperties
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import org.eclipse.jetty.http.HttpStatus
import org.koin.core.KoinComponent
import org.koin.core.inject
import javax.sql.DataSource


data class SpringHealth(val status: String, val details: Map<String, Any>) {
    constructor(status: SpringStatus) : this(status.code, emptyMap())
}


object HealthHandler : KoinComponent {
    private val dataSource: DataSource by inject()
    private val appProps: com.github.felipewom.commons.AppProperties by inject()

    @OpenApi(
        ignore = true
    )
    fun healthCheck(ctx: Context) {
        ctx.json(SpringHealthEndPoint.getApplicationHealthStatus(dataSource))
    }

    @OpenApi(
        ignore = true
    )
    fun info(ctx: Context) {
        ctx.json(SpringInfo(SpringApp(appProps.env.projectName), appProps.env.projectVersion))
    }

    @OpenApi(
        ignore = true
    )
    fun headLogFile(ctx: Context) {
        ctx.status(HttpStatus.OK_200)
    }

    @OpenApi(
        ignore = true
    )
    fun logFile(ctx: Context) {
        try {
            SpringLogFile.getLogFile(ctx)
        } catch (e: Exception) {
            // if any I/O error occurs
            e.printStackTrace()
        }
    }
}
