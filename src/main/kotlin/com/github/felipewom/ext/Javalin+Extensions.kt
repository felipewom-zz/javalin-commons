package com.github.felipewom.ext

import com.github.felipewom.commons.*
import com.github.felipewom.i18n.I18nEnUSDefault
import com.github.felipewom.i18n.I18nKeys
import com.github.felipewom.i18n.I18nProvider
import com.github.felipewom.i18n.I18nPtBRDefault
import com.github.felipewom.security.Roles
import com.github.felipewom.springboot.HealthHandler
import com.github.felipewom.utils.GsonUtils
import com.github.felipewom.utils.gson.ListOfJson
import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.JavalinConfig
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.core.validation.JavalinValidation
import io.javalin.core.validation.Validator
import io.javalin.http.*
import io.javalin.plugin.json.FromJsonMapper
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.json.ToJsonMapper
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.javalin.plugin.openapi.jackson.JacksonModelConverterFactory
import io.javalin.plugin.openapi.jackson.JacksonToJsonMapper
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import java.rmi.activation.UnknownObjectException


@Throws(BadRequestResponse::class)
inline fun <reified T : Any?> Context.getParamIdValidator(): Validator<T> =
    this.pathParam(com.github.felipewom.commons.ApiConstants.ID, T::class.java)

inline fun <reified DTO : Any> Context.bodyAsList(): List<DTO>? {
    return try {
        val deserialized: List<DTO> = Gson().fromJson(this.body(), ListOfJson<DTO>(DTO::class.java))
        return deserialized
    } catch (e: Exception) {
        logger.error("[ERROR]GsonUtils::deserialize=>${e.message}")
        null
    }
}

inline fun <reified DTO : Any> Context.bodyAsListValidator() = try {
    Validator(this.bodyAsList<DTO>(), "Request body as ${DTO::class.simpleName}")
} catch (e: Exception) {
    throw BadRequestResponse("Couldn't deserialize body to ${DTO::class.simpleName}")
}

fun <T> Context.getPageable(): Pageable<T> = use(Pageable<T>()::class.java)

fun Context.getPageableExposed(): PageableExposed = use(PageableExposed()::class.java)

fun <T> Context.getPageableValidator(): Validator<Pageable<T>> {
    val pageable = this.use(Pageable<T>()::class.java)
    return Validator(pageable)
}

fun Context.getPageablePageableExposedValidator(): Validator<PageableExposed> {
    val pageable = this.use(PageableExposed::class.java)
    return Validator(pageable)
}

inline fun <reified T : Any> Context.deserializePageable(): Pageable<T> {
    val pageable = Pageable<T>()
    pageable.pageNumber = this.queryParam(PageableFields.PAGE_NUMBER)?.toInt() ?: 1
    pageable.pageSize = this.queryParam(PageableFields.PAGE_SIZE)?.toInt() ?: 20
    pageable.orderBy = this.queryParam(PageableFields.ORDER_BY)
    pageable.filter = this.queryParam(PageableFields.FILTER)
    pageable.objectFilter = this.queryParam(PageableFields.OBJECT_FILTER)
    return pageable
}

fun Context.deserializePageableExposed(): PageableExposed {
    val pageable = PageableExposed()
    pageable.pageNumber = this.queryParam(PageableFields.PAGE_NUMBER)?.toInt() ?: 1
    pageable.pageSize = this.queryParam(PageableFields.PAGE_SIZE)?.toInt() ?: 20
    pageable.orderBy = this.queryParam(PageableFields.ORDER_BY)
    pageable.filter = this.queryParam(PageableFields.FILTER)
    pageable.objectFilter = this.queryParam(PageableFields.OBJECT_FILTER)
    return pageable
}

fun Context.jsonOrNull(obj: Any?) = when {
    obj != null -> contentType(com.github.felipewom.commons.ApiConstants.JSON_MIME).result(JavalinJson.toJson(obj))
    else -> contentType(com.github.felipewom.commons.ApiConstants.JSON_MIME).result("")
}

fun Context.ok(body: Any? = null) = this.status(HttpStatus.OK_200).jsonOrNull(body)

fun Context.noContent() = this.status(HttpStatus.NO_CONTENT_204).result("")

fun Context.created(value: Any? = null) = this.status(HttpStatus.CREATED_201).jsonOrNull(value)

fun Context.badRequest(message: String = I18nKeys.error_bad_request) {
    val responseMessage = this.getI18n(message)
    this.json(
        ErrorResponse(
            errors = mapOf(com.github.felipewom.commons.ApiConstants.BAD_REQUEST_400 to listOf(responseMessage))
        )
    ).status(HttpStatus.BAD_REQUEST_400)
}

fun Context.badCredentials(message: String = I18nKeys.error_bad_credentials) {
    val responseMessage = this.getI18n(message)
    this.json(
        ErrorResponse(
            errors = mapOf(com.github.felipewom.commons.ApiConstants.UNAUTHORIZED_401 to listOf(responseMessage))
        )
    ).status(HttpStatus.UNAUTHORIZED_401)
}

fun Context.failureWith(error: ResultHandler.Failure?) {
    if (error == null) {
        return this.badRequest()
    }
    return when (error.throwable) {
        is UnauthorizedResponse -> this.badCredentials()
        else -> this.badRequest()
    }
}


fun Context.getCookie(): String {
    val cookieMap = this.cookieMap()
    if(cookieMap.isNullOrEmpty()){
        return ""
    }
    return cookieMap.asCookieString()
}

fun Context.getTenantId(): String? = this.header(com.github.felipewom.commons.ApiConstants.TENANT_KEY_HEADER)

fun Context.getSSOFromJwt(): Map<String, String> {
    val principal = this.getJWTId()
    val ssoToken = this.getJWTPrincipal()
    val cookieToken =
        "${com.github.felipewom.commons.ApiConstants.J_COOKIE_NAME}=$ssoToken;${com.github.felipewom.commons.ApiConstants.SP_COOKIE_NAME}=$principal;"
    return mapOf(com.github.felipewom.commons.ApiConstants.COOKIE to cookieToken)
}

fun Context.getJWTPrincipal(): String =
    this.attribute<String>(com.github.felipewom.commons.ApiConstants.JWT_SUBJECT_ATTR) ?: throw UnauthorizedResponse()

fun Context.getJWTId(): String =
    this.attribute<String>(com.github.felipewom.commons.ApiConstants.JWT_CLAIM_ID_ATTR) ?: throw UnauthorizedResponse()

fun Context.getJWTEmail(): String =
    this.attribute<String>(com.github.felipewom.commons.ApiConstants.JWT_CLAIM_EMAIL_ATTR)
        ?: throw UnauthorizedResponse()

fun Context.getJWTPrincipalOrThrow(): String =
    this.attribute<String>(com.github.felipewom.commons.ApiConstants.JWT_CLAIM_EMAIL_ATTR)
        ?: throw UnauthorizedResponse()

fun Context.getJWT(): String {
    return this.header(com.github.felipewom.commons.ApiConstants.JWT_AUTHORIZATION_HEADER)?.let { it.split("${com.github.felipewom.commons.ApiConstants.JWT_BEARER_TOKEN} ")[1] }
        ?: throw UnauthorizedResponse()
}

fun Javalin.configureGsonMapper() {
    JavalinJson.fromJsonMapper = fromJsonMapper
    JavalinJson.toJsonMapper = toJsonMapper
}

val fromJsonMapper = object : FromJsonMapper {
    override fun <T> map(json: String, targetClass: Class<T>): T = GsonUtils.gson.fromJson(json, targetClass)
}
val toJsonMapper = object : ToJsonMapper {
    override fun map(obj: Any): String = GsonUtils.gson.toJson(obj)
}

fun configureJavalinServer(appDeclaration: JavalinConfig.() -> Unit): Javalin {
    val anonymousRoutes: Set<Role> = SecurityUtil.roles(Roles.ANYONE)
    val appProperties: com.github.felipewom.commons.AppProperties by GlobalContext.get().koin.inject()
    val javalin = Javalin.create { config ->
        config.registerPlugin(RouteOverviewPlugin(com.github.felipewom.commons.ApiConstants.OVERVIEW_PATH));
        config.registerPlugin(OpenApiPlugin(getOpenApiOptions(appProperties)));
        config.contextPath = appProperties.env.context
        config.enableCorsForAllOrigins()
        config.autogenerateEtags = true
        config.defaultContentType = com.github.felipewom.commons.ApiConstants.JSON_MIME
        // set debug logging if env variable ENV=development is present
        config.enableDevLogging()
        appDeclaration(config)
    }
    // configure json object mapper
    configureJsonMapper()
    javalin.registerExceptionHandlers()
    registerValidations()
    javalin.before { ctx ->
        ctx.register(Pageable::class.java, ctx.deserializePageable<Any>())
        ctx.register(PageableExposed::class.java, ctx.deserializePageableExposed())
    }
    javalin.routes {
        anonymousRoutes.also { route ->
            ApiBuilder.get(
                "ping",
                io.javalin.plugin.openapi.dsl.documented(
                    documentation = OpenApiDocumentation()
                        .result<String>(
                            "200",
                            applyUpdates = {
                                it.description(I18nEnUSDefault.translate.getValue(I18nKeys.application_version))
                            }
                        )
                ) { it.result(appProperties.env.projectVersion) },
                route
            )
            ApiBuilder.path("admin") {
                ApiBuilder.get("info", HealthHandler::info, route)
                ApiBuilder.get("health", HealthHandler::healthCheck, route)
                ApiBuilder.get("logfile", HealthHandler::logFile, route)
                ApiBuilder.head("logfile", HealthHandler::headLogFile, route)
            }
        }
    }
    javalin.after { ctx ->
        ctx.header("Server", "Powered by EMPREEND.ME")
        ctx.header(com.github.felipewom.commons.ApiConstants.API_VERSION_HEADER, appProperties.env.projectVersion)
    }
    javalin.events {
        it.serverStarting {
            logger.info("Server is starting in ${appProperties.env.stage.toUpperCase()}")
            logger.info("\n____________________________________________________")
            logger.info("\n${appProperties.env}")
            logger.info("\n____________________________________________________")
            logger.info("\n${appProperties.db}")
            logger.info("\n____________________________________________________")
        }
        it.serverStopping {
            stopKoin()
        }
    }
    return javalin
}

fun Context.isPermittedRoute(permittedRoles: Set<Role>): Boolean {
    val appProperties: com.github.felipewom.commons.AppProperties by GlobalContext.get().koin.inject()
    val isSwaggerAvailable = appProperties.env.isDev() && (
            this.path().equals(
                appProperties.env.context + com.github.felipewom.commons.ApiConstants.OVERVIEW_PATH,
                true
            ) ||
                    this.path().equals(appProperties.env.swaggerContextPath, true) ||
                    this.path().equals(appProperties.env.swaggerJsonPath, true)
            )
    return isSwaggerAvailable || permittedRoles.contains(Roles.ANYONE)
}

private fun getOpenApiOptions(appProperties: com.github.felipewom.commons.AppProperties): OpenApiOptions {
    val applicationInfo = Info()
        .version(appProperties.env.projectVersion)
        .title(appProperties.env.projectName)
        .description(appProperties.env.projectDescription)
        .contact(Contact().name(appProperties.env.swaggerContactName))
    return OpenApiOptions(applicationInfo)
        .toJsonMapper(JacksonToJsonMapper)
        .modelConverterFactory(JacksonModelConverterFactory)
        .roles(SecurityUtil.roles(Roles.AUTHENTICATED))
        .activateAnnotationScanningFor(com.github.felipewom.commons.ApiConstants.ROOT_PACKAGE)
        .swagger(SwaggerOptions(appProperties.env.swaggerContextPath).title(appProperties.env.projectName))
        .defaultDocumentation { documentation ->
            documentation.json<ErrorResponse>(I18nPtBRDefault.translate.getValue(I18nKeys.error_unknow_server_error))
        }
        .path(appProperties.env.swaggerJsonPath)
}

private fun registerValidations() {
    JavalinValidation.register(Any::class.java) {
        try {
            GsonUtils.deserialize(it, Any::class.java)
        } catch (e: Exception) {
            it
        }
    }
}

fun configureJsonMapper() {
    JavalinJson.fromJsonMapper = fromJsonMapper
    JavalinJson.toJsonMapper = toJsonMapper
}

fun Javalin.registerExceptionHandlers() {
    this.exception(RuntimeException::class.java) { e, ctx ->
        logger.error("Exception occurred for req -> ${ctx.url()}", e)
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_internal_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("InternalServerError" to errorList))
        ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
    }
    this.exception(Exception::class.java) { e, ctx ->
        logger.error("Exception occurred for req -> ${ctx.url()}", e)
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_internal_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("InternalServerError" to errorList))
        ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
    }
    this.exception(ExposedSQLException::class.java) { e, ctx ->
        logger.error("Exception occurred for req -> ${ctx.url()}", e)
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_internal_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("InternalServerError" to errorList))
        ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
    }
    this.exception(SecurityException::class.java) { e, ctx ->
        logger.info("SecurityException occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_user_not_authenticated)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("SecurityException" to errorList))
        ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
    }
    this.exception(UnauthorizedResponse::class.java) { e, ctx ->
        logger.info("UnauthorizedResponse occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_user_not_authenticated)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("Unauthorized" to errorList))
        ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
    }
    this.exception(ForbiddenResponse::class.java) { e, ctx ->
        logger.info("ForbiddenResponse occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_user_not_authenticated)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("Forbidden" to errorList))
        ctx.json(error).status(HttpStatus.FORBIDDEN_403)
    }
    this.exception(BadRequestResponse::class.java) { e, ctx ->
        logger.info("BadRequestResponse occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_bad_request)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("BadRequest" to errorList))
        ctx.json(error).status(HttpStatus.BAD_REQUEST_400)
    }
    this.exception(UnknownObjectException::class.java) { e, ctx ->
        logger.info("UnknownObjectException occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_unknow_object_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("UnknownObject" to errorList))
        ctx.json(error).status(HttpStatus.UNPROCESSABLE_ENTITY_422)
    }
    this.exception(NotFoundResponse::class.java) { e, ctx ->
        logger.info("NotFoundResponse occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_not_found_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val error = ErrorResponse(mapOf("NotFound" to errorList))
        ctx.json(error).status(HttpStatus.NOT_FOUND_404)
    }
    this.exception(HttpResponseException::class.java) { e, ctx ->
        logger.info("HttpResponseException occurred for req -> ${ctx.url()}")
        val errorMessage = ctx.getI18n(e.message ?: I18nKeys.error_unknow_server_error)
        val errorList = mutableListOf(errorMessage)
        if (e.localizedMessage != errorMessage) {
            errorList.add(e.localizedMessage)
        }
        val errorMap = mutableMapOf(
            "ErrorResponse" to errorList,
            "Details" to e.details.map { it.key to listOf(it.value) }.flatMap { it.second })
        val error = ErrorResponse(errorMap)
        ctx.json(error).status(e.status)
    }
}

fun Context.getI18n(str: String) = I18nProvider.get(str, this)