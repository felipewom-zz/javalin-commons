package com.github.felipewom.springboot

import com.google.gson.annotations.SerializedName


data class SpringInfo(
    @SerializedName("app") val app: SpringApp,
    @SerializedName("version") val version: String
)