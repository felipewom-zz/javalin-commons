package com.github.felipewom.ext

import com.github.felipewom.commons.AppProperties
import com.github.felipewom.commons.DatabaseProperties
import com.github.felipewom.commons.EnvironmentProperties
import org.koin.core.KoinApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

fun KoinApplication.loadFileProperties(): KoinApplication {
    /*
    * load properties
    * if PRODUCTION /resources/koin.properties
    * if DEV /resources/koin-dev.properties if ENV STAGE=DEV file or given file name
    * */
    environmentProperties()
    val stagePermitted = listOf("dev", "development", "staging", "test")
    var stage = getEnvironmentProp("STAGE")
    if (stage.isNullOrBlank()) {
        stage = getEnvironmentProp("PROFILE")
    }
    val fileName = if (!stage.isNullOrBlank() && stagePermitted.contains(stage.toLowerCase()))
        "/koin-${stage.toLowerCase()}.properties"
    else
        "/koin.properties"
    return fileProperties(fileName)
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