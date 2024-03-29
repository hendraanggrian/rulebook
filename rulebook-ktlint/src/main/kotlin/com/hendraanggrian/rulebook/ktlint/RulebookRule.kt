package com.hendraanggrian.rulebook.ktlint

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty

public sealed class RulebookRule(
    id: String,
    override val usesEditorConfigProperties: Set<EditorConfigProperty<*>> = emptySet(),
    override val visitorModifiers: Set<VisitorModifier> = emptySet(),
) : Rule(
        RuleId("${ID.value}:$id"),
        About(
            maintainer = "Rulebook",
            repositoryUrl = "https://github.com/hendraanggrian/rulebook",
            issueTrackerUrl = "https://github.com/hendraanggrian/rulebook/issues",
        ),
        visitorModifiers,
        usesEditorConfigProperties,
    ) {
    public companion object {
        public val ID: RuleSetId = RuleSetId("rulebook")
    }
}
