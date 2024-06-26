package com.hendraanggrian.rulebook.checkstyle.internals

import com.puppycrawl.tools.checkstyle.api.DetailAST
import com.puppycrawl.tools.checkstyle.api.DetailNode
import com.puppycrawl.tools.checkstyle.api.TokenTypes.ANNOTATION
import com.puppycrawl.tools.checkstyle.api.TokenTypes.IDENT
import com.puppycrawl.tools.checkstyle.api.TokenTypes.MODIFIERS

internal operator fun DetailNode.contains(type: Int): Boolean = children.any { it.type == type }

internal operator fun DetailAST.contains(type: Int): Boolean = findFirstToken(type) != null

internal fun DetailAST.hasModifier(type: Int): Boolean =
    MODIFIERS in this &&
        type in findFirstToken(MODIFIERS)!!

internal fun DetailAST.hasAnnotation(name: String): Boolean =
    MODIFIERS in this &&
        findFirstToken(MODIFIERS)
            .children()
            .any { it.type == ANNOTATION && it.findFirstToken(IDENT)?.text.orEmpty() == name }

internal val DetailAST.firstmostChild: DetailAST
    get() {
        var last = this
        while (last.firstChild != null) {
            last = last.firstChild
        }
        return last
    }

internal val DetailAST.lastmostChild: DetailAST
    get() {
        var last = this
        while (last.lastChild != null) {
            last = last.lastChild
        }
        return last
    }

/** In Checkstyle, nodes with children do not have text. */
internal fun DetailAST.joinText(separator: String = "", excludeType: Int = 0): String {
    val children = children().toList()
    val result =
        when {
            children.isEmpty() -> text
            else ->
                buildString {
                    for (child in children) {
                        if (child.type == excludeType) {
                            continue
                        }
                        append(child.joinText(separator, excludeType))
                        append(separator)
                    }
                }
        }
    if (result.endsWith(separator)) {
        return result.substring(0, result.length - separator.length)
    }
    return result
}

internal fun DetailAST.children(): Sequence<DetailAST> =
    generateSequence(firstChild) { node -> node.nextSibling }

internal fun DetailAST.siblings(forward: Boolean = true): Sequence<DetailAST> =
    when {
        forward -> generateSequence(nextSibling) { it.nextSibling }
        else -> generateSequence(previousSibling) { it.previousSibling }
    }

internal val DetailNode.next: DetailNode?
    get() {
        val children = parent.children
        return children.getOrNull(children.indexOf(this) + 1)
    }
