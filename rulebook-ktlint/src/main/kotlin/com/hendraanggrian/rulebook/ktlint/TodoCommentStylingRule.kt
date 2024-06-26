package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.internals.Emit
import com.hendraanggrian.rulebook.ktlint.internals.Messages
import com.hendraanggrian.rulebook.ktlint.internals.contains
import com.hendraanggrian.rulebook.ktlint.internals.siblingsUntil
import com.pinterest.ktlint.rule.engine.core.api.ElementType.EOL_COMMENT
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_LEADING_ASTERISK
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_SECTION
import com.pinterest.ktlint.rule.engine.core.api.ElementType.KDOC_TEXT
import com.pinterest.ktlint.rule.engine.core.api.ElementType.WHITE_SPACE
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import kotlin.text.RegexOption.IGNORE_CASE

/**
 * [See wiki](https://github.com/hendraanggrian/rulebook/wiki/Rules#todo-comment-styling)
 */
public class TodoCommentStylingRule :
    Rule("todo-comment-styling"),
    RuleAutocorrectApproveHandler {
    override fun beforeVisitChildNodes(node: ASTNode, emit: Emit) {
        // first line of filter
        val text =
            when (node.elementType) {
                EOL_COMMENT -> node.text
                KDOC_SECTION ->
                    node
                        .takeUnless { KDOC_LEADING_ASTERISK in node }
                        ?.findChildByType(KDOC_TEXT)
                        ?.text
                KDOC_LEADING_ASTERISK ->
                    node
                        .siblingsUntil(KDOC_LEADING_ASTERISK)
                        .takeUnless { n -> n.all { it.elementType == WHITE_SPACE } }
                        ?.joinToString("") { it.text }
                else -> null
            } ?: return

        // checks for violation
        if (KEYWORD_REGEX.containsMatchIn(text)) {
            emit(
                node.startOffset,
                Messages.get(MSG_KEYWORD, KEYWORD_REGEX.find(text)!!.value),
                false,
            )
        }
        if (SEPARATOR_REGEX.containsMatchIn(text)) {
            emit(
                node.startOffset,
                Messages.get(MSG_SEPARATOR, SEPARATOR_REGEX.find(text)!!.value.last()),
                false,
            )
        }
    }

    internal companion object {
        const val MSG_KEYWORD = "todo.comment.styling.keyword"
        const val MSG_SEPARATOR = "todo.comment.styling.separator"

        private val KEYWORD_REGEX = Regex("\\b(?i:fixme|todo)(?<!FIXME|TODO)\\b")
        private val SEPARATOR_REGEX = Regex("\\b(todo|fixme)\\S", IGNORE_CASE)
    }
}
