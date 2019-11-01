package com.github.felipewom.springboot

import com.google.gson.annotations.SerializedName

data class SpringApp(
    @SerializedName("name") val name: String
)