package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.SwitchStatementWrappingRule.Companion.MSG
import com.hendraanggrian.rulebook.ktlint.internals.Messages
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import kotlin.test.Test

class SwitchStatementWrappingRuleTest {
    private val assertThatCode = assertThatRule { SwitchStatementWrappingRule() }

    @Test
    fun `Joined switch case branches`() =
        assertThatCode(
            """
            fun method() {
                when (foo) {
                    true -> bar()
                    false -> { baz() }
                }
            }
            """.trimIndent(),
        ).hasNoLintViolations()

    @Test
    fun `Separated switch case branches`() =
        assertThatCode(
            """
            fun method() {
                when (foo) {
                    true -> bar()

                    false -> { baz() }
                }
            }
            """.trimIndent(),
        ).hasLintViolationWithoutAutoCorrect(5, 9, Messages[MSG])
}
