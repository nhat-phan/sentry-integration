package net.ntworld.sentryIntegration.client

import net.ntworld.sentryIntegration.SentryApi

open class SentryApiDecorator(private val api: SentryApi): SentryApi by api