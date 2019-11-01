@file:JvmName("I18nEnUSDefault")
package com.github.felipewom.i18n


object I18nEnUSDefault : Internationalization {
    override val translate: Map<String, String> = mapOf(
        I18nKeys.error_unknow_server_error to "Something went wrong, try again later.",
        I18nKeys.error_unknow_object_server_error to "Resource can't be found to fulfill the request.",
        I18nKeys.error_not_found_server_error to "Resource can't be found.",
        I18nKeys.error_bad_response_server_error to "Your request could not be completed at this time.",
        I18nKeys.error_bad_request to "Your request does not meet the needs to complete.",
        I18nKeys.error_bad_credentials to "Invalid credentials",
        I18nKeys.error_internal_server_error to "Oh no! Something bad happened. Please come back later when we fixed that problem. Thanks.",
        I18nKeys.error_user_not_authenticated to "User not authenticated!",
        I18nKeys.error_validator_id_required to "Id field can't be lower than zero.",
        I18nKeys.error_validator_item_not_found to "No itens found.",
        I18nKeys.error_validator_invalid_fields to "One or more fields are invalid.",
        I18nKeys.error_validator_could_not_update to "Item could not be updated.",
        I18nKeys.error_user_not_registered_for_notification to "User is not registered for notifications.",
        /*
        * Commons
        * */
        I18nKeys.application_version to "Application version"
    )
}