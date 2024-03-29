package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.ObjectsComparisonRule.Companion.MSG_EQ
import com.hendraanggrian.rulebook.ktlint.ObjectsComparisonRule.Companion.MSG_NEQ
import com.hendraanggrian.rulebook.ktlint.internals.Messages
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import kotlin.test.Test

class ObjectsComparisonRuleTest {
    private val assertThatCode = assertThatRule { ObjectsComparisonRule() }

    @Test
    fun `Structural equalities`() =
        assertThatCode(
            """
            fun baz() {
                if (foo == bar) {
                    baz()
                } else if (foo != bar) {
                    baz()
                }
            }
            """.trimIndent(),
        ).hasNoLintViolations()

    @Test
    fun `Referential equalities`() =
        assertThatCode(
            """
            fun baz() {
                if (foo === bar) {
                    baz()
                } else if (foo !== bar) {
                    baz()
                }
            }
            """.trimIndent(),
        ).hasLintViolationsWithoutAutoCorrect(
            LintViolation(2, 13, Messages[MSG_EQ]),
            LintViolation(4, 20, Messages[MSG_NEQ]),
        )
}
