package com.hendraanggrian.rulebook.ktlint

import com.google.common.truth.Truth.assertThat
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import kotlin.test.Test

class RulebookRuleSetTest {
    @Test
    fun `All rules`() {
        assertThat(
            RulebookRuleSet()
                .getRuleProviders()
                .map { it.createNewRuleInstance().javaClass.kotlin },
        ).containsExactly(
            BlockCommentLineTrimmingRule::class,
            BlockCommentSpacingRule::class,
            BlockTagDescriptionPunctuationRule::class,
            BlockTagInitialLineSpacingRule::class,
            ClassFinalNameBlacklistingRule::class,
            ClassNameAcronymCapitalizationRule::class,
            ClassOrganizationRule::class,
            ConditionalBranchLineWrappingRule::class,
            EmptyBlockCommentLineJoiningRule::class,
            EmptyCodeBlockWrappingRule::class,
            EmptyCommentLineJoiningRule::class,
            ErrorSubclassThrowingRule::class,
            FileInitialLineTrimmingRule::class,
            FileSizeLimitationRule::class,
            GenericsNameWhitelistingRule::class,
            IdentifierNameBlacklistingRule::class,
            IfStatementFlatteningRule::class,
            InnerClassPositionRule::class,
            KotlinApiPriorityRule::class,
            OperandStructuralEqualityRule::class,
            QualifierConsistencyRule::class,
            SpecialFunctionPositionRule::class,
            TodoCommentStylingRule::class,
        )
    }

    @Test
    fun `No overlapping ID`() {
        val standardIds = StandardRuleSetProvider().getRuleProviders().ids
        RulebookRuleSet()
            .getRuleProviders()
            .ids
            .forEach { assertThat(it).isNotIn(standardIds) }
    }

    private val Set<RuleProvider>.ids
        get() =
            map {
                it
                    .createNewRuleInstance()
                    .ruleId.value
                    .substringAfterLast(':')
            }
}
