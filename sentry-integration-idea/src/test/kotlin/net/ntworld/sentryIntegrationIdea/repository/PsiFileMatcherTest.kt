package net.ntworld.sentryIntegrationIdea.repository

import org.junit.Test
import kotlin.test.assertEquals

class PsiFileMatcherTest {
    @Test
    fun testParseModuleInfo() {
        val dataset = mapOf(
            "ClassName" to PsiFileMatcher.ModuleInfo(
                namespace = "",
                className = "ClassName"
            ),
            "com.test.ClassName" to PsiFileMatcher.ModuleInfo(
                namespace = "com.test",
                className = "ClassName"
            ),
            "com.test.ClassName\$SubClassName" to PsiFileMatcher.ModuleInfo(
                namespace = "com.test",
                className = "ClassName"
            )
        )

        dataset.forEach { (input, expected) ->
            val result = PsiFileMatcher.parseModuleInfo(input)

            assertEquals(expected, result)
        }
    }
}