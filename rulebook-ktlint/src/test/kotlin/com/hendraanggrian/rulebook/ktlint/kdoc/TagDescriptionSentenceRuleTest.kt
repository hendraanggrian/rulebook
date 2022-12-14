package com.hendraanggrian.rulebook.ktlint.kdoc

import com.hendraanggrian.rulebook.ktlint.kdoc.TagDescriptionSentenceRule.Companion.ERROR_MESSAGE
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import kotlin.test.Test

class TagDescriptionSentenceRuleTest {
    private val assertThatCode = assertThatRule { TagDescriptionSentenceRule() }

    @Test
    fun `No description`() = assertThatCode(
        """
        /**
         * @param input
         */
        fun add(input: Int) { }
        """.trimIndent()
    ).hasNoLintViolations()

    @Test
    fun `Description ends with punctuation`() = assertThatCode(
        """
        /**
         * @param input a number.
         * @param input a number!
         * @param input a number?
         * @param input some
         *   long number.
         * @param input some
         *   long number?
         * @param input some
         *   long number!
         */
        fun add(input: Int) { }
        """.trimIndent()
    ).hasNoLintViolations()

    @Test
    fun `Tag description has no end punctuation`() = assertThatCode(
        """
        /**
         * @param input a number
         * @param input some
         *   long number
         */
        fun add(input: Int) { }
        """.trimIndent()
    ).hasLintViolationsWithoutAutoCorrect(
        LintViolation(2, 10, ERROR_MESSAGE.format("@param")),
        LintViolation(3, 10, ERROR_MESSAGE.format("@param"))
    )

    @Test
    fun `Tag description ends with comments`() = assertThatCode(
        """
        /**
         * @param input a number // some comment
         * @param input a. // some comment
         * @param input // some comment
         */
        fun add(input: Int) { }
        """.trimIndent()
    ).hasLintViolationsWithoutAutoCorrect(
        LintViolation(2, 10, ERROR_MESSAGE.format("@param")),
        LintViolation(4, 10, ERROR_MESSAGE.format("@param"))
    )
}
