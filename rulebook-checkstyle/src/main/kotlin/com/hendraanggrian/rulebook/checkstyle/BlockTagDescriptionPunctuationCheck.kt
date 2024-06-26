package com.hendraanggrian.rulebook.checkstyle

import com.hendraanggrian.rulebook.checkstyle.internals.Messages
import com.puppycrawl.tools.checkstyle.api.DetailNode
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.DESCRIPTION
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.JAVADOC_TAG
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes.TEXT

/**
 * [See wiki](https://github.com/hendraanggrian/rulebook/wiki/Rules#block-tag-description-punctuation)
 */
public class BlockTagDescriptionPunctuationCheck : JavadocCheck() {
    private var blockTags = setOf("@param", "@return")

    public fun setBlockTags(vararg blockTags: String) {
        this.blockTags = blockTags.toSet()
    }

    override fun getDefaultJavadocTokens(): IntArray = intArrayOf(JAVADOC_TAG)

    override fun visitJavadocToken(node: DetailNode) {
        // only enforce certain tags
        node.takeIf { it.children.first().text in blockTags } ?: return

        // long descriptions have multiple lines, take only the last one
        val text =
            node.children
                ?.firstOrNull { it.type == DESCRIPTION }
                ?.children
                ?.findLast { it.type == TEXT && it.text.isNotBlank() }
                ?: return

        // checks for violation
        text.text
            .trimComment()
            .trimEnd()
            .lastOrNull()
            ?.takeUnless { it in END_PUNCTUATIONS }
            ?: return
        log(text.lineNumber, text.columnNumber, Messages.get(MSG, blockTags.joinToString()))
    }

    internal companion object {
        const val MSG = "block.tag.description.punctuation"

        private val END_PUNCTUATIONS = setOf('.', ')')

        private fun String.trimComment(): String {
            val index = indexOf("//")
            if (index == -1) {
                return this
            }
            return substring(0, index).trimEnd()
        }
    }
}
