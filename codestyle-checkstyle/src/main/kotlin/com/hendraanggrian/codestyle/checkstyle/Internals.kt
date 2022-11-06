package com.hendraanggrian.codestyle.checkstyle

import com.puppycrawl.tools.checkstyle.api.DetailNode

/**
 * Returns the whole text of this node. Calling `getText()` on Java node with children returns its
 * type, e.g.: (JAVADOC_TAG). This function search node's children recursively for a valid text.
 */
internal val DetailNode.actualText: String
    get() = if (children.isEmpty()) {
        text
    } else {
        buildString { children.forEach { append(it.actualText) } }
    }
