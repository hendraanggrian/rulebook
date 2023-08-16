package com.hendraanggrian.rulebook.ktlint

import com.google.common.truth.Truth.assertThat
import com.hendraanggrian.rulebook.ktlint.docs.SummaryContinuationRule
import com.hendraanggrian.rulebook.ktlint.docs.TagDescriptionSentenceRule
import com.hendraanggrian.rulebook.ktlint.docs.TagsStartingWhitespaceRule
import kotlin.test.Test
import kotlin.test.assertFalse

class RulebookRuleSetTest {
    @Test
    fun `Rule set setup`() {
        assertFalse(RULEBOOK_ID.value.isBlank())
        assertFalse(RULEBOOK_ABOUT.maintainer.isBlank())
        assertFalse(RULEBOOK_ABOUT.repositoryUrl.isBlank())
        assertFalse(RULEBOOK_ABOUT.issueTrackerUrl.isBlank())
    }

    @Test
    fun `List of rules`() {
        assertThat(
            RulebookRuleSet()
                .getRuleProviders()
                .map { it.createNewRuleInstance().javaClass.kotlin }
        ).containsExactly(
            SummaryContinuationRule::class,
            TagDescriptionSentenceRule::class,
            TagsStartingWhitespaceRule::class,
            ClassBodyStartingWhitespaceRule::class,
            FunctionsReturnTypeRule::class,
            NamesAcronymRule::class,
            SwitchEntryWhitespaceRule::class,
            ThrowAmbiguityRule::class,
            UseKotlinApiRule::class
        )
    }
}
