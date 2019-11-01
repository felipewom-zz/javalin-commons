package com.github.felipewom.springboot

import com.github.felipewom.ext.getVariable
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.gson.jsonBody
import org.slf4j.LoggerFactory

internal data class SpringBootApplicationClient(
    val statusInfo: StatusInfo = StatusInfo(),
    val info: SpringInfo? = null,
    val name: String,
    var managementUrl: String?,
    var healthUrl: String?,
    var serviceUrl: String?
)

data class StatusInfo(
    val status: String = "UP"
)

object AdminClient {

    val logger = LoggerFactory.getLogger(AdminClient::class.java)

    var appName: String? = null
    var appVersion: String? = null
    var adminHost: String? = null
    var adminContext: String? = null
    var clientName: String? = null
    var clientHealthUrl: String? = null

    fun registerApplication() {
        try {
            val springBootAdminUrl = getVariable<String>("SPRING_BOOT_ADMIN_URL")
            if (springBootAdminUrl.isNullOrBlank()) {
                return
            }
            val username = getVariable<String>("SPRING_BOOT_ADMIN_USERNAME") ?: return
            val password = getVariable<String>("SPRING_BOOT_ADMIN_PASSWORD") ?: return
            appName = getVariable<String>("env_project_name")
            appVersion = getVariable<String>("env_project_version")
            adminHost = springBootAdminUrl
            adminContext = getVariable<String>("spring.boot.admin.client.api-path")
            clientName = getVariable<String>("spring.boot.admin.client.instance.name")
            clientHealthUrl = "${getVariable<String>("URL_SISTEMA")}${getVariable<String>("CONTEXT")}/admin/health"
            val app = SpringBootApplicationClient(
                info = SpringInfo(SpringApp(appName ?: ""), appVersion ?: ""),
                name = clientName ?: "",
                healthUrl = clientHealthUrl,
                serviceUrl = "${getVariable<String>("URL_SISTEMA")}${getVariable<String>("CONTEXT")}",
                managementUrl = "${getVariable<String>("URL_SISTEMA")}${getVariable<String>("CONTEXT")}/admin"
            )
            val result = Fuel.post(adminHost + adminContext)
                .authentication()
                .basic(username, password)
                .jsonBody(app)
            logger.info("Resultado registro springboot admin: ${result.body}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
