package com.github.felipewom.ext

import com.github.felipewom.commons.DatabaseProperties
import com.github.felipewom.commons.EnvironmentProperties
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import java.util.*
import kotlin.reflect.KClass

fun KoinApplication.loadFileProperties(): KoinApplication {
    loadDefaultProperties()
    return environmentProperties()
}

/**
 * Load properties from Property file
 * @param fileName
 */
private fun KoinApplication.loadDefaultProperties() {
    val stagePermitted = listOf("dev", "development", "staging", "test")
    var stage = getEnvironmentProp("STAGE")
    if (stage.isNullOrBlank()) {
        stage = getEnvironmentProp("PROFILE")
    }
    var fileName = if (!stage.isNullOrBlank() && stagePermitted.contains(stage.toLowerCase()))
        "/application-${stage.toLowerCase()}.properties"
    else
        "/application.properties"
    if (KoinApplication.logger.isAt(Level.DEBUG)) {
        KoinApplication.logger.debug("load properties from $fileName")
    }
    val contentDefault = Koin::class.java.getResource(fileName)?.readText()
    if (contentDefault != null) {
        if (KoinApplication.logger.isAt(Level.INFO)) {
            KoinApplication.logger.info("loaded properties from file:'$fileName'")
        }
        val properties = readDataFromFile(contentDefault)
        propertyRegistry.saveProperties(properties)
    }
    // Koin default properties
    fileName = if (!stage.isNullOrBlank() && stagePermitted.contains(stage.toLowerCase()))
        "/koin-${stage.toLowerCase()}.properties"
    else
        "/koin.properties"
    if (KoinApplication.logger.isAt(Level.DEBUG)) {
        KoinApplication.logger.debug("load properties from $fileName")
    }
    val content = Koin::class.java.getResource(fileName)?.readText()
    if (content != null) {
        if (KoinApplication.logger.isAt(Level.INFO)) {
            KoinApplication.logger.info("loaded properties from file:'$fileName'")
        }
        val properties = readDataFromFile(content)
        propertyRegistry.saveProperties(properties)
    }
}

fun readDataFromFile(content: String): Properties {
    val properties = Properties()
    properties.load(content.byteInputStream())
    return properties
}

fun setupKoin(modules: List<Module>? = null) {
    startKoin {
        val allModules = defaultModules(modules)
        loadKoinModules(allModules)
        // enable PrintLogger with default Level.INFO can have Level & implementation equivalent to logger(Level.INFO, PrintLogger())
        printLogger(Level.INFO)
        // load properties
        loadFileProperties()
    }
}

fun defaultModules(modules: List<Module>?): List<Module> {
    val allModules =  mutableListOf(
        module {
            single { com.github.felipewom.commons.AppProperties(get(), get()) }
            single { DatabaseProperties.build(this) }
            single { EnvironmentProperties.build(this) }
        }
    )
    if (modules != null && modules.isNotEmpty() ) {
        // list all used modules as list or vararg
        allModules.addAll(modules)
    }
    return allModules
}


val rootScope by lazy {
    GlobalContext.get().koin.rootScope
}

val propertyRegistry by lazy {
    GlobalContext.get().koin.propertyRegistry
}

/**
 * Lazy inject a Koin instance
 * @param qualifier
 * @param scope
 * @param parameters
 *
 * @return Lazy instance of type T
 */
@JvmOverloads
inline fun <reified T> injectDependency(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = rootScope.inject(qualifier, parameters)

/**
 * Lazy inject a Koin instance if available
 * @param qualifier
 * @param scope
 * @param parameters
 *
 * @return Lazy instance of type T or null
 */
@JvmOverloads
inline fun <reified T> injectDependencyOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T?> = rootScope.injectOrNull(qualifier, parameters)

/**
 * Get a Koin instance
 * @param qualifier
 * @param scope
 * @param parameters
 */
@JvmOverloads
inline fun <reified T> getDependency(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T = rootScope.get(qualifier, parameters)

/**
 * Get a Koin instance if available
 * @param qualifier
 * @param scope
 * @param parameters
 *
 * @return instance of type T or null
 */
@JvmOverloads
inline fun <reified T> getDependencyOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T? = rootScope.getOrNull(qualifier, parameters)

/**
 * Get a Koin instance
 * @param clazz
 * @param qualifier
 * @param scope
 * @param parameters
 *
 * @return instance of type T
 */
fun <T> getDependency(
    clazz: KClass<*>,
    qualifier: Qualifier?,
    parameters: ParametersDefinition?
): T = rootScope.get(clazz, qualifier, parameters)


/**
 * Retrieve a property
 * @param key
 * @param defaultValue
 */
fun <T> getProperty(key: String, defaultValue: T): T {
    return propertyRegistry.getProperty<T>(key) ?: defaultValue
}

/**
 * Retrieve a property
 * @param key
 */
fun <T> getProperty(key: String): T? {
    return propertyRegistry.getProperty(key)
}

/**
 * Retrieve a property
 * @param key
 */
fun <T> getPropertyOrNull(key: String): T? = propertyRegistry.getProperty(key)

/**
 * Save a property
 * @param key
 * @param value
 */
fun <T : Any> setProperty(key: String, value: T) {
    propertyRegistry.saveProperties(mapOf(key to  value))
}