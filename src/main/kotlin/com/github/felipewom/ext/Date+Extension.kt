@file:JvmName("DateExtensions")

package com.github.felipewom.ext

import com.github.felipewom.commons.ApiConstants
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
val DEFAULT_TIMEZONE = System.getenv("user.timezone") ?: com.github.felipewom.commons.ApiConstants.TZ_SAO_PAULO

fun SimpleDateFormat.defaultPatternString() = ISO8601_PATTERN

fun SimpleDateFormat.defaultPattern() = SimpleDateFormat(this.defaultPatternString())

fun getLocalDateTime() = LocalDateTime.now(ZoneId.of(DEFAULT_TIMEZONE))

fun getDateTimeZone() = DateTimeZone.forID(DEFAULT_TIMEZONE)

fun currentUtc() = DateTime.now(DateTimeZone.UTC)

fun Date.toISO8601UTC(): String {
    val tz = TimeZone.getTimeZone(com.github.felipewom.commons.ApiConstants.TZ_UTC)
    val df = SimpleDateFormat(ISO8601_PATTERN)
    df.timeZone = tz
    return df.format(this)
}

fun DateTime.toISO8601UTC(): String {
    val tz = TimeZone.getTimeZone(com.github.felipewom.commons.ApiConstants.TZ_UTC)
    val df = SimpleDateFormat(ISO8601_PATTERN)
    df.timeZone = tz
    return df.format(this.toString(ISO8601_PATTERN))
}


fun Date.toISO8601DefaultTimeZone(): String {
    val tz = TimeZone.getTimeZone(DEFAULT_TIMEZONE)
    val df = SimpleDateFormat(ISO8601_PATTERN)
    df.timeZone = tz
    return df.format(this)
}

fun DateTime.toISO8601DefaultTimeZone(): String {
    return this.toString(ISO8601_PATTERN)
}

fun String.fromISO8601UTC(): Date? {
    val tz = TimeZone.getTimeZone(com.github.felipewom.commons.ApiConstants.TZ_UTC)
    val df = SimpleDateFormat(ISO8601_PATTERN)
    df.timeZone = tz
    try {
        return df.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return null
}

fun currentTimeEpoch(): Long {
    return Instant.ofEpochMilli(Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE)).timeInMillis).toEpochMilli()
}

fun currentDate(timeZone: String? = DEFAULT_TIMEZONE): Date {
    return Calendar.getInstance(TimeZone.getTimeZone(timeZone)).time
}

fun currentCalendar(timeZone: String? = DEFAULT_TIMEZONE): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone(timeZone))
}

fun currentTimeISO() = currentDate().toISO8601UTC()