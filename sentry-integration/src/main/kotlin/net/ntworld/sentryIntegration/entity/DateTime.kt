package net.ntworld.sentryIntegration.entity

import net.ntworld.sentryIntegration.DateTimeUtil
import java.util.*

class DateTime(val value: String) {

    fun toDate(): Date = DateTimeUtil.toDate(value)

    fun pretty(): String = DateTimeUtil.toPretty(value)

    fun format(): String = DateTimeUtil.formatDate(DateTimeUtil.toDate(value))

}