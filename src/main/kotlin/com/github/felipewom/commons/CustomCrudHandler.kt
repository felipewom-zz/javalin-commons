package com.github.felipewom.commons

import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.dsl.OpenApiCrudHandlerDocumentation
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import java.util.*


private val pathDeque = ArrayDeque<String>()

private fun prefixPath(path: String): String {
    return pathDeque.joinToString("") + if (path.startsWith("/") || path.isEmpty()) path else "/$path"
}


internal enum class CustomCrudHandlerLambdaKey(val value: String) {
    GET_ALL("getAll"),
    GET_ONE("getOne"),
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete")
}

/**
 * The CustomCrudHandler is an interface for handling the five most
 * common CRUD operations. It's only available through the ApiBuilder.
 *
 * @see ApiBuilder
 */
interface CustomCrudHandler {
    fun getAll(ctx: Context)
    fun getOne(ctx: Context, resourceId: String)
    fun create(ctx: Context)
    fun update(ctx: Context, resourceId: String)
    fun delete(ctx: Context, resourceId: String)
}


internal fun CustomCrudHandler.getCustomLambdas(resourceId: String): Map<CustomCrudHandlerLambdaKey, Handler> {
    val crudHandler = this
    return mapOf(
        CustomCrudHandlerLambdaKey.GET_ALL to Handler { ctx -> crudHandler.getAll(ctx) },
        CustomCrudHandlerLambdaKey.GET_ONE to Handler { ctx -> crudHandler.getOne(ctx, ctx.pathParam(resourceId)) },
        CustomCrudHandlerLambdaKey.CREATE to Handler { ctx -> crudHandler.create(ctx) },
        CustomCrudHandlerLambdaKey.UPDATE to Handler { ctx -> crudHandler.update(ctx, ctx.pathParam(resourceId)) },
        CustomCrudHandlerLambdaKey.DELETE to Handler { ctx -> crudHandler.delete(ctx, ctx.pathParam(resourceId)) }
    )
}

data class OpenApiCustomCrudHandlerDocumentation(
    var getAllDocumentation: OpenApiDocumentation = OpenApiDocumentation(),
    var getOneDocumentation: OpenApiDocumentation = OpenApiDocumentation(),
    var createDocumentation: OpenApiDocumentation = OpenApiDocumentation(),
    var updateDocumentation: OpenApiDocumentation = OpenApiDocumentation(),
    var deleteDocumentation: OpenApiDocumentation = OpenApiDocumentation()
) {
    fun getAll(doc: OpenApiDocumentation) = apply { this.getAllDocumentation = doc }
    fun getOne(doc: OpenApiDocumentation) = apply { this.getOneDocumentation = doc }
    fun create(doc: OpenApiDocumentation) = apply { this.createDocumentation = doc }
    fun update(doc: OpenApiDocumentation) = apply { this.updateDocumentation = doc }
    fun delete(doc: OpenApiDocumentation) = apply { this.deleteDocumentation = doc }
}

class DocumentedCustomCrudHandler(
    val crudHandlerDocumentation: OpenApiCustomCrudHandlerDocumentation,
    private val crudHandler: CustomCrudHandler
) : CustomCrudHandler {
    override fun getAll(ctx: Context) = crudHandler.getAll(ctx)
    override fun getOne(ctx: Context, resourceId: String) = crudHandler.getOne(ctx, resourceId)
    override fun create(ctx: Context) = crudHandler.create(ctx)
    override fun update(ctx: Context, resourceId: String) = crudHandler.update(ctx, resourceId)
    override fun delete(ctx: Context, resourceId: String) = crudHandler.delete(ctx, resourceId)
}

class DocumentedCustomCrudHandler2(
    val crudHandlerDocumentation: OpenApiCrudHandlerDocumentation,
    private val crudHandler: CustomCrudHandler
) : CustomCrudHandler {
    override fun getAll(ctx: Context) = crudHandler.getAll(ctx)
    override fun getOne(ctx: Context, resourceId: String) = crudHandler.getOne(ctx, resourceId)
    override fun create(ctx: Context) = crudHandler.create(ctx)
    override fun update(ctx: Context, resourceId: String) = crudHandler.update(ctx, resourceId)
    override fun delete(ctx: Context, resourceId: String) = crudHandler.delete(ctx, resourceId)
}

/** Creates a documented CrudHandler */
fun documented(documentation: OpenApiCrudHandlerDocumentation, handler: CustomCrudHandler) =
    DocumentedCustomCrudHandler2(documentation, handler)

/** Creates a documented CustomCrudHandler */
fun documented(documentation: OpenApiCustomCrudHandlerDocumentation, handler: CustomCrudHandler) =
    DocumentedCustomCrudHandler(documentation, handler)


internal fun documented(crudHandler: CustomCrudHandler, handlers: Map<CustomCrudHandlerLambdaKey, Handler>, resourceId: String): Map<CustomCrudHandlerLambdaKey, Handler> {
    return if (crudHandler is DocumentedCustomCrudHandler) {
        documented(crudHandler.crudHandlerDocumentation, crudHandler).getCustomLambdas(resourceId)
    } else {
        moveDocumentationFromAnnotationToHandler(crudHandler::class.java, handlers)
    }
}

internal fun moveDocumentationFromAnnotationToHandler(
    crudHandlerClass: Class<out CustomCrudHandler>,
    handlers: Map<CustomCrudHandlerLambdaKey, Handler>
): Map<CustomCrudHandlerLambdaKey, Handler> {
    return handlers.mapValues { (key, handler) ->
        io.javalin.plugin.openapi.dsl.moveDocumentationFromAnnotationToHandler(crudHandlerClass, key.value, handler)
    }
}

/**
 * Adds a CustomCrudHandler handler to the specified path with the given roles to the instance.
 * @see CustomCrudHandler
 */
fun registerCrud(path: String, crudHandler: CustomCrudHandler, permittedRoles: Set<Role>) {
    var currentPath = path
    currentPath = if (currentPath.startsWith("/")) currentPath else "/$currentPath"
    if (currentPath.startsWith("/:")) {
        throw IllegalArgumentException("CrudHandler requires a resource base at the beginning of the provided path e.g. '/users/:user-id'")
    }
    if (!currentPath.contains("/:") || currentPath.lastIndexOf("/") > currentPath.lastIndexOf("/:")) {
        throw IllegalArgumentException("CrudHandler requires a path-parameter at the end of the provided path e.g. '/users/:user-id'")
    }
    val SEPARATOR = "/:"
    val resourceBase = currentPath.substring(0, currentPath.lastIndexOf(SEPARATOR))
    val resourceId = currentPath.substring(currentPath.lastIndexOf(SEPARATOR) + SEPARATOR.length)
    var lambdas = crudHandler.getCustomLambdas(resourceId)
    lambdas = documented(crudHandler, lambdas, resourceId)
//    Application.javalin.get(prefixPath(currentPath), lambdas.getValue(CustomCrudHandlerLambdaKey.GET_ONE), permittedRoles)
//    Application.javalin.get(prefixPath(resourceBase), lambdas.getValue(CustomCrudHandlerLambdaKey.GET_ALL), permittedRoles)
//    Application.javalin.post(prefixPath(resourceBase), lambdas.getValue(CustomCrudHandlerLambdaKey.CREATE), permittedRoles)
//    Application.javalin.put(prefixPath(currentPath), lambdas.getValue(CustomCrudHandlerLambdaKey.UPDATE), permittedRoles)
//    Application.javalin.delete(prefixPath(currentPath), lambdas.getValue(CustomCrudHandlerLambdaKey.DELETE), permittedRoles)
}