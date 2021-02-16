package net.ntworld.sentryIntegration.client.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import net.ntworld.sentryIntegration.client.parser.raw.EventRaw
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktraceContext
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktraceVariable
import net.ntworld.sentryIntegration.entity.LocalPath
import net.ntworld.sentryIntegration.entity.SentryEventTag

class SentryEventParser(private val linkedProject: LinkedProject) {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

    fun parseEventDetail(issueId: String, content: String): SentryEventDetail {
        val raw = json.parse(EventRaw.serializer(), content)

        return transformEventRaw(issueId, raw)
    }

    fun transformEventRaw(issueId: String, raw: EventRaw): SentryEventDetail {
        return SentryEventDetail(
            id = raw.id,
            issueId = issueId,
            exceptions = transformSentryEventExceptions(raw),
            request = null,
            tags = transformSentryEventTags(raw)
        )
    }

    private fun transformSentryEventExceptions(raw: EventRaw): List<SentryEventException> {
        val data = findEntryDataWithType(raw, "exception")
        if (null == data) {
            return listOf()
        }

        val values = data.jsonObject["values"]
        if (null === values) {
            return listOf()
        }

        val result = mutableListOf<SentryEventException>()
        for (item in values.jsonArray) {
            result.add(transformSentryEventException(item))
        }
        return result
    }

    private fun transformSentryEventException(element: JsonElement): SentryEventException
    {
        val typeElement = element.jsonObject["type"]
        val valueElement = element.jsonObject["value"]
        val stacktraceElement = element.jsonObject["stacktrace"]

        return SentryEventException(
            id = java.util.UUID.randomUUID().toString(),
            type = if (null !== typeElement) typeElement.primitive.content else "",
            value = if (null !== valueElement) valueElement.primitive.content else "",
            stacktrace = transformStacktraceElement(stacktraceElement)
        )
    }

    private fun transformStacktraceElement(element: JsonElement?): List<SentryEventExceptionStacktrace> {
        if (null === element) {
            return listOf()
        }
        val framesElement = element.jsonObject["frames"]
        if (null === framesElement) {
            return listOf()
        }

        val result = mutableListOf<SentryEventExceptionStacktrace>()
        for (frame in framesElement.jsonArray) {
            result.add(transformStacktraceFrameElement(frame))
        }
        return result
    }

    private fun transformStacktraceFrameElement(element: JsonElement): SentryEventExceptionStacktrace {
        val absPathElement = element.jsonObject["absPath"]
        val lineNoElement = element.jsonObject["lineNo"]
        val moduleElement = element.jsonObject["module"]
        val functionElement = element.jsonObject["function"]
        val absolutePath = if (null === absPathElement) "" else absPathElement.primitive.content
        val module = if (null === moduleElement) "" else moduleElement.primitive.content
        val function = if (null === functionElement) "" else functionElement.primitive.content
        val lineNumber = if (null === lineNoElement) 0 else {
            if (lineNoElement.isNull) 0 else lineNoElement.primitive.int
        }

        val context = mutableListOf<SentryEventExceptionStacktraceContext>()
        val contextElement = element.jsonObject["context"]
        if (null !== contextElement) {
            for (item in contextElement.jsonArray) {
                context.add(SentryEventExceptionStacktraceContext(
                    lineNumber = item.jsonArray[0]!!.primitive.int,
                    content = item.jsonArray[1]!!.primitive.content
                ))
            }
        }

        val varsElement = element.jsonObject["vars"]
        var variables = mutableListOf<SentryEventExceptionStacktraceVariable>()
        if (null !== varsElement && !varsElement.isNull) {
            for (item in varsElement.jsonObject) {
                variables.add(
                    SentryEventExceptionStacktraceVariable(
                        name = item.key,
                        value = if (null === item.value) "null" else item.value.toString()
                    )
                )
            }
        }

        return SentryEventExceptionStacktrace(
            absolutePath = LocalPath.create(absolutePath, linkedProject.sentryRootPath),
            module = module,
            function = function,
            lineNumber = lineNumber,
            context = context,
            variables = variables
        )
    }

    private fun findEntryDataWithType(raw: EventRaw, type: String): JsonElement? {
        for (entry in raw.entries) {
            val typeElement = entry.jsonObject["type"]
            if (null !== typeElement && typeElement.primitive.content == type) {
                return entry.jsonObject["data"]
            }
        }
        return null
    }

    private fun transformSentryEventTags(raw: EventRaw): List<SentryEventTag> {
        if (null === raw.tags) {
            return listOf()
        }
        val result = mutableListOf<SentryEventTag>()
        for (rawTagElement in raw.tags) {
            val key = rawTagElement.jsonObject["key"]
            val value = rawTagElement.jsonObject["value"]
            if (null !== key && null !== value) {
                result.add(SentryEventTag(
                    key = key.primitive.content,
                    value = value.primitive.content
                ))
            }
        }
        return result
    }
}