package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.ExceptionAmbiguityRule.Companion.ERROR_MESSAGE
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import kotlin.test.Test

class ExceptionAmbiguityRuleTest {
    private val assertThatCode = assertThatRule { ExceptionAmbiguityRule() }

    @Test
    fun `Throw supertype without messages`() = assertThatCode(
        """
        fun main() {
            throw Exception()
            throw Error()
            throw Throwable()
        }
        """.trimIndent()
    ).hasLintViolationsWithoutAutoCorrect(
        LintViolation(2, 11, ERROR_MESSAGE.format("Exception")),
        LintViolation(3, 11, ERROR_MESSAGE.format("Error")),
        LintViolation(4, 11, ERROR_MESSAGE.format("Throwable"))
    )

    @Test
    fun `Throw supertype with messages`() = assertThatCode(
        """
        fun main() {
            throw Exception("Some message")
            throw Error("Some message")
            throw Throwable("Some message")
        }
        """.trimIndent()
    ).hasNoLintViolations()

    @Test
    fun `Throw subtype without messages`() = assertThatCode(
        """
        fun main() {
            throw IllegalStateException()
            throw StackOverflowError()
        }
        """.trimIndent()
    ).hasNoLintViolations()

    @Test
    fun `Throw reference instead of call expression`() = assertThatCode(
        """
        fun main() {
            val exception = Exception()
            throw exception

            val error = Error()
            throw error

            val throwable = Throwable()
            throw throwable
        }
        """.trimIndent()
    ).hasNoLintViolations()
}
