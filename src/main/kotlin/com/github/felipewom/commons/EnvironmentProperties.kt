@file:JvmName("EnvironmentProperties")
package com.github.felipewom.commons

import com.github.felipewom.ext.getEnvProp
import org.koin.core.scope.Scope

data class EnvironmentProperties(
    var context: String = "/",
    var serverPort: Int = 7000,
    var stage: String = "production",
    var projectName: String = "Application Name",
    var projectVersion: String = "beta",
    var projectDescription: String = "Application Description",
    var swaggerContactName: String = "Softplan<g_equipe_cpa@softplan.com.br>",
    var swaggerContextPath: String = "/swagger",
    var swaggerJsonPath: String = "/swagger-json"
) {
    companion object {
        fun build(scope: Scope): EnvironmentProperties {
            return EnvironmentProperties(
                serverPort = getEnvProp(scope, 7000, "PORT", "env_server_port"),
                context = getEnvProp(scope, "api", "CONTEXT", "env_context"),
                stage = getEnvProp(
                    scope,
                    "production",
                    "ENV",
                    "STAGE",
                    "PROFILE",
                    "env_stage"
                ),
                projectName = getEnvProp(scope, "Application Name", "env_project_name"),
                projectVersion = getEnvProp(scope, "beta", "env_project_version"),
                projectDescription = getEnvProp(
                    scope,
                    "Application Description",
                    "env_project_description"
                ),
                swaggerContactName = getEnvProp(
                    scope,
                    "Application Description",
                    "env_swagger_contact_name"
                ),
                swaggerContextPath = getEnvProp(
                    scope,
                    "Application Description",
                    "env_swagger_context_path"
                ),
                swaggerJsonPath = getEnvProp(
                    scope,
                    "Application Description",
                    "env_swagger_json_path"
                )
            )
        }
    }

    fun isTest(): Boolean {
        return when (stage.toLowerCase()) {
            "testing" -> true
            "test" -> true
            else -> false
        }
    }

    fun isDev(): Boolean {
        return when (stage.toLowerCase()) {
            "development" -> true
            "dev" -> true
            else -> false
        }
    }

    override fun toString(): String {
        return "-> EnvironmentProperties:\ncontext='$context', \nserverPort=$serverPort, \nstage='$stage', \nprojectName='$projectName', \nprojectVersion='$projectVersion', \nprojectDescription='$projectDescription', \nswaggerContactName='$swaggerContactName', \nswaggerContextPath='$swaggerContextPath', \nswaggerJsonPath='$swaggerJsonPath'"
    }
}