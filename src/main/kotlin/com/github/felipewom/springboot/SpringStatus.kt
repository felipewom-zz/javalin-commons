package com.github.felipewom.springboot

data class SpringStatus(val code: String, var description: String?) {
    constructor(code: String) : this(code, "No Database found")

    companion object {
        /**
         * [SpringStatus] indicating that the component or subsystem is functioning as
         * expected.
         */
        val UP = SpringStatus("UP")

        /**
         * [SpringStatus] indicating that the component or subsystem has suffered an
         * unexpected failure.
         */
        val DOWN = SpringStatus("DOWN")
    }
}