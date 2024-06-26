package com.hendraanggrian.rulebook.checkstyle

import com.hendraanggrian.rulebook.checkstyle.internals.Messages
import com.puppycrawl.tools.checkstyle.api.DetailNode
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.DESCRIPTION
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.EOF
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.JAVADOC
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.JAVADOC_TAG
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.LEADING_ASTERISK
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.NEWLINE
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.TEXT
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.WS

/**
 * [See wiki](https://github.com/hendraanggrian/rulebook/wiki/Rules#block-comment-line-trimming)
 */
public class BlockCommentLineTrimmingCheck : JavadocCheck() {
    override fun getDefaultJavadocTokens(): IntArray = intArrayOf(JAVADOC)

    override fun visitJavadocToken(node: DetailNode) {
        // skip single-line block comment
        if (node.children.size < 3) {
            return
        }

        // initial node is always newline
        var children = node.filterEmpty()
        val firstChild = children.first().takeIf { it.type == NEWLINE }
        if (firstChild != null &&
            children.indexOf(firstChild).let {
                children.getOrNull(it + 1)?.type == LEADING_ASTERISK &&
                    children.getOrNull(it + 2)?.type == NEWLINE
            }
        ) {
            log(firstChild.lineNumber, firstChild.columnNumber, Messages[MSG_FIRST])
        }

        // final node may be newline or tag
        val lastChild =
            children.last { it.type != EOF }.let { child ->
                when (child.type) {
                    NEWLINE -> child
                    JAVADOC_TAG -> {
                        val description = child.children.firstOrNull { it.type == DESCRIPTION }
                        if (description != null) {
                            children = description.filterEmpty()
                            return@let children.lastOrNull { it.type == NEWLINE }
                        }
                        return@let null
                    }
                    else -> null
                }
            }
        if (lastChild != null &&
            children.indexOf(lastChild).let {
                children.getOrNull(it - 1)?.type == LEADING_ASTERISK &&
                    children.getOrNull(it - 2)?.type == NEWLINE
            }
        ) {
            log(lastChild.lineNumber, lastChild.columnNumber, Messages[MSG_LAST])
        }
    }

    /** Disregard whitespace between asterisk and newline. */
    private fun DetailNode.filterEmpty() =
        children.filter {
            when (it.type) {
                TEXT -> it.text.isNotBlank()
                WS -> false
                else -> true
            }
        }

    internal companion object {
        const val MSG_FIRST = "block.comment.line.trimming.first"
        const val MSG_LAST = "block.comment.line.trimming.last"
    }
}
