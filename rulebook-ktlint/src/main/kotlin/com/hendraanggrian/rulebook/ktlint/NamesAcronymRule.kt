package com.hendraanggrian.rulebook.ktlint

import com.pinterest.ktlint.core.KtLint.FILE_PATH_USER_DATA_KEY
import com.pinterest.ktlint.core.Rule
import com.pinterest.ktlint.core.ast.ElementType.CLASS
import com.pinterest.ktlint.core.ast.ElementType.FILE
import com.pinterest.ktlint.core.ast.ElementType.FUN
import com.pinterest.ktlint.core.ast.ElementType.IDENTIFIER
import com.pinterest.ktlint.core.ast.ElementType.OBJECT_DECLARATION
import com.pinterest.ktlint.core.ast.ElementType.PROPERTY
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
import java.nio.file.Paths

/**
 * [See guide](https://github.com/hendraanggrian/rulebook/blob/main/rules.md#names-acronym).
 */
class NamesAcronymRule : Rule("names-acronym") {
    internal companion object {
        const val ERROR_MESSAGE = "Acronym of '%s' should be lowercase."
    }

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        // first line of filter
        when (node.elementType) {
            PROPERTY -> {
                // allow all uppercase, which usually is static property
                val identifier = node[IDENTIFIER]
                if (identifier.text.all { it.isUpperCase() || it.isDigit() || it == '_' }) {
                    return
                }

                // check for violation
                if (identifier.text.isViolation()) {
                    emit(identifier.startOffset, ERROR_MESSAGE.format("property"), false)
                }
            }
            FUN, CLASS, OBJECT_DECLARATION -> {
                // may be property, fun, class, interface, object or annotation
                val typeName = node.firstChildNode.text

                // skip companion object
                val identifier = node.findChildByType(IDENTIFIER) ?: return

                // check for violation
                if (identifier.text.isViolation()) {
                    emit(identifier.startOffset, ERROR_MESSAGE.format(typeName), false)
                }
            }
            FILE -> {
                // get filename, obtained from `com.pinterest.ktlint.ruleset.standard.FilenameRule`
                node as FileASTNode?
                    ?: error("node is not ${FileASTNode::class} but ${node::class}")
                val filePath = node.getUserData(FILE_PATH_USER_DATA_KEY)
                if (filePath?.endsWith(".kt") != true) {
                    stopTraversalOfAST() // ignore all non ".kt" files (including ".kts")
                    return
                }
                val fileName = Paths.get(filePath).fileName.toString().substringBefore(".")
                if (fileName == "package") {
                    stopTraversalOfAST() // ignore package.kt filename
                    return
                }

                // check for violation
                if (fileName.isViolation()) {
                    emit(node.startOffset, ERROR_MESSAGE.format("file"), false)
                }
            }
        }
    }

    private fun String.isViolation(): Boolean {
        // find 3 connecting uppercase letters
        for (i in 0 until length - 2) {
            if (get(i).isUpperCase() && get(i + 1).isUpperCase() && get(i + 2).isUpperCase()) {
                return true
            }
        }
        return false
    }
}
