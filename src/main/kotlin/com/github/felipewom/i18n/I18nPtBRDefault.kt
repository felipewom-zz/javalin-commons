@file:JvmName("I18nPtBRDefault")
package com.github.felipewom.i18n

object I18nPtBRDefault : Internationalization {
    override val translate: Map<String, String> = mapOf(
        I18nKeys.error_unknow_server_error to "Ops, algo aconteceu errado...\nTente novamente mais tarde.",
        I18nKeys.error_unknow_object_server_error to "Recurso desconhecido para completar requisição.",
        I18nKeys.error_not_found_server_error to "Recurso não foi encontrado.",
        I18nKeys.error_bad_response_server_error to "Sua requisição não pode ser concluída desta vez.",
        I18nKeys.error_bad_request to "Sua requisição não atende as necessidades para ser concluída.",
        I18nKeys.error_bad_credentials to "Credênciais inválidas.",
        I18nKeys.error_internal_server_error to "Oh não! Algo de ruim aconteceu. Por favor, volte mais tarde quando resolvermos o problema. Obrigado.",
        I18nKeys.error_user_not_authenticated to "Usuario não autenticado!",
        I18nKeys.error_validator_id_required to "Parametro id não pode ser nulo.",
        I18nKeys.error_validator_item_not_found to "Nenhum item foi encontrado.",
        I18nKeys.error_validator_invalid_fields to "Um ou mais campos estão inválidos.",
        I18nKeys.error_validator_could_not_update to "Item não pode ser atualizado.",
        I18nKeys.error_user_not_registered_for_notification to "Usuario não registrado para receber notificações.",
        /*
        * Commons
        * */
        I18nKeys.application_version to "Versão da aplicação"
    )
}