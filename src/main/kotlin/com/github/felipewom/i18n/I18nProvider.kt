@file:JvmName("I18nProvider")

package com.github.felipewom.i18n

import io.javalin.http.Context

/*
* Classe provedora de resources baseado no header da requisicao
*
* */
object I18nProvider {
    const val PT_BR = "pt-br"
    const val EN_US = "en-US,en"
    const val ACCEPT_LANG = "Accept-Language"
    const val COMMA_SEPARATOR = ","
    const val SEMICOLON = ";"
    const val PT_BR_INTL = "com.github.felipewom.i18n.I18nPtBR"
    const val EN_US_INTL = "com.github.felipewom.i18n.I18nEnUS"
    fun get(str: String, ctx: Context): String {
        try {
            val lang = ctx.header(ACCEPT_LANG)?.let { it.split(SEMICOLON).firstOrNull() } ?: EN_US
            val langName = if (lang.contains(COMMA_SEPARATOR)) {
                lang.substring(0, lang.indexOf(COMMA_SEPARATOR))
            } else {
                lang
            }
            val translated = when (langName.toLowerCase()) {
                PT_BR -> getI18nPtBR(str)
                else -> {
                    getI18nEnUS(str)
                }
            }
            return translated ?: str
        } catch (e: Exception) {
            e.printStackTrace()
            return str
        }
    }

    private fun getI18nPtBR(str: String): String? {
        val default = I18nPtBRDefault.translate
        return try {
            val instance = Class.forName(PT_BR_INTL).kotlin.objectInstance as Internationalization
            val translations = mutableMapOf<String, String>()
            translations.putAll(instance.translate)
            translations.putAll(default)
            translations[str]
        } catch (e: Exception) {
            default[str]
        }
    }

    private fun getI18nEnUS(str: String): String? {
        val default = I18nPtBRDefault.translate
        return try {
            val instance = Class.forName(EN_US_INTL).kotlin.objectInstance as Internationalization
            val translations = mutableMapOf<String, String>()
            translations.putAll(instance.translate)
            translations.putAll(default)
            translations[str]
        } catch (e: Exception) {
            default[str]
        }
    }
}