package com.hendraanggrian.rulebook.ktlint

import com.hendraanggrian.rulebook.ktlint.internals.Messages
import com.hendraanggrian.rulebook.ktlint.internals.qualifierName
import com.pinterest.ktlint.rule.engine.core.api.ElementType.DOT_QUALIFIED_EXPRESSION
import com.pinterest.ktlint.rule.engine.core.api.ElementType.IMPORT_DIRECTIVE
import com.pinterest.ktlint.rule.engine.core.api.ElementType.TYPE_REFERENCE
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * [See wiki](https://github.com/hendraanggrian/rulebook/wiki/Rules#kotlin-api-consistency).
 */
public class KotlinApiConsistencyRule : RulebookRule("kotlin-api-consistency") {
    private var isTestClass = false

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        // first line of filter
        if (node.elementType != IMPORT_DIRECTIVE &&
            node.elementType != TYPE_REFERENCE
        ) {
            return
        }

        // get import or reference
        val (api, ast) =
            when (node.elementType) {
                IMPORT_DIRECTIVE -> {
                    // get text after `import`
                    val path = (node.psi as KtImportDirective).importPath!!.pathStr

                    // check if running on test
                    if (!isTestClass) {
                        path.takeIf { s -> TEST_LIBRARIES.any { s.startsWith(it) } }
                            ?.let { isTestClass = true }
                    }

                    val dotQualifiedExpression =
                        node.findChildByType(DOT_QUALIFIED_EXPRESSION) ?: return
                    path to dotQualifiedExpression
                }
                else -> node.qualifierName to node
            }

        // checks for violation
        val replacement =
            when {
                api.startsWith("java.lang.") ->
                    try {
                        Class.forName(api).kotlin.qualifiedName
                            ?.takeIf { it.startsWith("kotlin.") }
                    } catch (e: ClassNotFoundException) {
                        null
                    }
                api.startsWith("java.util.") -> COLLECTIONS_REPLACEMENT[api]
                isTestClass && api.startsWith("org.junit") -> TEST_ANNOTATIONS_REPLACEMENT[api]
                else -> null
            } ?: return
        emit(ast.startOffset, Messages.get(MSG, replacement), false)
    }

    internal companion object {
        const val MSG = "kotlin.api.consistency"

        private val COLLECTIONS_REPLACEMENT =
            mapOf(
                "java.util.Iterator" to "kotlin.collections.Iterator",
                "java.util.Iterable" to "kotlin.collections.Iterable",
                "java.util.Collection" to "kotlin.collections.Collection",
                "java.util.Set" to "kotlin.collections.Set",
                "java.util.HashSet" to "kotlin.collections.HashSet",
                "java.util.LinkedHashSet" to "kotlin.collections.LinkedHashSet",
                "java.util.List" to "kotlin.collections.List",
                "java.util.ArrayList" to "kotlin.collections.ArrayList",
                "java.util.ListIterator" to "kotlin.collections.ListIterator",
                "java.util.Map" to "kotlin.collections.Map",
                "java.util.Map.Entry" to "kotlin.collections.Map.Entry",
            )

        private val TEST_LIBRARIES =
            setOf(
                "io.kotest",
                "junit.framework",
                "kotlin.test",
                "org.junit",
                "org.testng",
            )

        private val TEST_ANNOTATIONS_REPLACEMENT =
            mapOf(
                "org.junit.Test" to "kotlin.test.Test",
                "org.junit.Ignore" to "kotlin.test.Ignore",
                "org.junit.Before" to "kotlin.test.BeforeTest",
                "org.junit.After" to "kotlin.test.AfterTest",
            )
    }
}
