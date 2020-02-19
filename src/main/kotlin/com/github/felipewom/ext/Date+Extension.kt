@file:JvmName("DateExtensions")

package com.github.felipewom.ext

import com.github.felipewom.commons.ApiConstants
import com.github.felipewom.commons.logger
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
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
    try {
        val tz = TimeZone.getTimeZone(ApiConstants.TZ_UTC)
        val df = SimpleDateFormat(ISO8601_PATTERN)
        df.timeZone = tz
        return df.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        logger.error("Date.toISO8601DefaultTimeZone:: CAN'T PARSE DATE ${this}")
    }
    return this.toString()
}


fun DateTime.toISO8601DefaultTimeZone(): String? {
    try {
        val tz = TimeZone.getTimeZone(ApiConstants.TZ_UTC)
        val df = SimpleDateFormat(ISO8601_PATTERN)
        df.timeZone = tz
        return df.format(this.toDate())
    } catch (e: Exception) {
        e.printStackTrace()
        logger.error("DateTime.toISO8601DefaultTimeZone:: CAN'T PARSE DATE ${this}")
    }
    return null
}


fun formatDateTime(str: String): String {
    val tz = TimeZone.getTimeZone(ApiConstants.TZ_UTC)
    val df = SimpleDateFormat(ISO8601_PATTERN)
    var dt: Date? = null
    df.timeZone = tz
    dt = df.parse(str)
    return df.format(dt)
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
    return Instant.ofEpochMilli(Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE)).timeInMillis)
        .toEpochMilli()
}

fun currentDate(timeZone: String? = DEFAULT_TIMEZONE): Date {
    return Calendar.getInstance(TimeZone.getTimeZone(timeZone)).time
}

fun currentDateTime(timeZone: String? = DEFAULT_TIMEZONE): DateTime {
    return DateTime(Calendar.getInstance(TimeZone.getTimeZone(timeZone)).time).toDateTimeISO()
}

fun currentCalendar(timeZone: String? = DEFAULT_TIMEZONE): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone(timeZone))
}

fun currentTimeISO() = currentDate().toISO8601UTC()


fun Date.atStartOfDay(): Date {
    val localDateTime = this.toLocalDateTime()
    val startOfDay = localDateTime.with(LocalTime.MIN)
    return Date.from((startOfDay.atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()))
}

fun Date.atEndOfDay(): Date {
    val localDateTime = this.toLocalDateTime()
    val endOfDay = localDateTime.with(LocalTime.MAX)
    return Date.from((endOfDay.atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()))
}

fun Date.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.of(DEFAULT_TIMEZONE))
}

private fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant())
}
fun currentEpochMillisUTC() : Long {
    return Instant.ofEpochMilli(Calendar.getInstance(TimeZone.getTimeZone(ApiConstants.TZ_UTC)).timeInMillis).toEpochMilli()
}

fun currentEpochSecondUTC() : Long {
    return Instant.ofEpochMilli(Calendar.getInstance(TimeZone.getTimeZone(ApiConstants.TZ_UTC)).timeInMillis).epochSecond
}