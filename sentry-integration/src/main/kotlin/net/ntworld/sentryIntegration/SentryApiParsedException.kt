package net.ntworld.sentryIntegration

class SentryApiParsedException(val endpoint: String, val content: String, message: String?): Exception(message)
