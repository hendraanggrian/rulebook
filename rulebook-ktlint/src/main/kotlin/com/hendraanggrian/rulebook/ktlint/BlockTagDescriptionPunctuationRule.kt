package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.internals.Emit
import com.hendraanggrian.rulebook.ktlint.internals.Messages
import com.hendraanggrian.rulebook.ktlint.internals.endOffset
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_TAG
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_TAG_NAME
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_TEXT
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.CommaSeparatedListValueParser
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import org.ec4j.core.model.PropertyType.LowerCasingPropertyType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * [See wiki](https://github.com/hendraanggrian/rulebook/wiki/Rules#block-tag-description-punctuation)
 */
public class BlockTagDescriptionPunctuationRule :
    Rule(
        "block-tag-description-punctuation",
        setOf(PUNCTUATED_BLOCK_TAGS_PROPERTY),
    ),
    RuleAutocorrectApproveHandler {
    private var punctuatedTags = PUNCTUATED_BLOCK_TAGS_PROPERTY.defaultValue

    override fun beforeFirstNode(editorConfig: EditorConfig) {
        punctuatedTags = editorConfig[PUNCTUATED_BLOCK_TAGS_PROPERTY]
    }

    override fun beforeVisitChildNodes(node: ASTNode, emit: Emit) {
        // first line of filter
        if (node.elementType != KDOC_TAG) {
            return
        }

        // only enforce certain tags
        node
            .findChildByType(KDOC_TAG_NAME)
            ?.takeIf { it.text in punctuatedTags }
            ?: return

        // long descriptions have multiple lines, take only the last one
        val kdocText =
            node
                .children()
                .findLast { it.elementType == KDOC_TEXT && it.text.isNotBlank() }
                ?: return

        // checks for violation
        kdocText.text
            .trimComment()
            .lastOrNull()
            ?.takeUnless { it in END_PUNCTUATIONS }
            ?: return
        emit(kdocText.endOffset, Messages.get(MSG, punctuatedTags.joinToString()), false)
    }

    internal companion object {
        const val MSG = "block.tag.description.punctuation"

        private val END_PUNCTUATIONS = setOf('.', ')')

        val PUNCTUATED_BLOCK_TAGS_PROPERTY =
            EditorConfigProperty(
                type =
                    LowerCasingPropertyType(
                        "rulebook_punctuated_block_tags",
                        "Block tags that have to end with a period.",
                        CommaSeparatedListValueParser(),
                    ),
                defaultValue = setOf("@param", "@return"),
                propertyWriter = { it.joinToString() },
            )

        private fun String.trimComment(): String {
            val index = indexOf("//")
            if (index == -1) {
                return this
            }
            return substring(0, index).trimEnd()
        }
    }
}
