package com.zeph.registry

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object Utils {
    fun formatDate(date: String): String {
        return LocalDate.parse(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    }
}

val String.capitalizeWords
    get() = this.lowercase().split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }