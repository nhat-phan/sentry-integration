package net.ntworld.sentryIntegration

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object DateTimeUtil {
    private val timezone = TimeZone.getTimeZone("UTC")
    private val toStringDateFormat by lazy {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        df.timeZone = timezone
        df
    }
    private val convertDateFormat by lazy {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        df.timeZone = timezone
        df
    }
    private val parser = ISODateTimeFormat.dateTimeParser()
    private val localTimeZone = DateTimeZone.getDefault()
    private val prettyTime = PrettyTime()

    @Synchronized
    fun fromDate(date: Date): String = convertDateFormat.format(date)

    @Synchronized
    fun toDate(datetime: String): Date {
        return parser.parseDateTime(datetime).withZone(localTimeZone).toDate()
    }

    @Synchronized
    fun formatDate(date: Date): String = toStringDateFormat.format(date) ?: ""

    @Synchronized
    fun formatDate(date: Date, format: String): String {
        val df = SimpleDateFormat(format)
        df.timeZone = timezone
        return df.format(date) ?: ""
    }

    @Synchronized
    fun toPretty(date: Date): String {
        return prettyTime.format(date)
    }

    @Synchronized
    fun fromTimestamp(timestamp: Long): Date {
        return DateTime(timestamp, DateTimeZone.UTC).toDate()
    }

    @Synchronized
    fun toPretty(datetime: String): String {
        return prettyTime.format(toDate(datetime))
    }
}