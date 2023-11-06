package com.hendraanggrian.rulebook.ktlint

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class RulebookRuleSetTest {
    @Test
    fun `List of rules`() {
        assertThat(
            RulebookRuleSet()
                .getRuleProviders()
                .map { it.createNewRuleInstance().javaClass.kotlin },
        ).containsExactly(
            AddBlankLineBeforeTagsRule::class,
            AvoidMeaninglessWordRule::class,
            CapitalizeFirstAcronymLetterRule::class,
            EndTagWithPeriodRule::class,
            InvertIfConditionRule::class,
            RemoveBlankLineBetweenCasesRule::class,
            SpecifyTypeExplicitlyRule::class,
            ThrowExceptionSubclassRule::class,
            UseCommonGenericsRule::class,
            UseKotlinApiRule::class,
            UseStructuralEqualityRule::class,
        )
    }
}
