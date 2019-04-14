/*
 * Copyright 2019 Davy Verstappen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wintersleep.snmp.mib2html

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.wintersleep.snmp.mib.smi.*
import java.io.File
import java.io.PrintWriter

enum class PageId {
    MODULES,
    TABLES,
    COLUMNS,
    SCALARS,
    VARIABLES,
    TRAPS,
    NOTIFICATIONS,
    TYPES,
    OIDS;

    val fileName = name.toLowerCase() + ".html"
    val link = fileName
    val title = name[0].toTitleCase() + name.substring(1).toLowerCase()
}

open class HtmlBasePage(val file: File,
                        val pageTitle: String,
                        val pageId: PageId? = null) {

    fun render() {
        file.printWriter().use {
            render(it)
        }
    }

    fun render(writer: PrintWriter) {
        writer.appendHTML().html {
            renderHeader()
            renderBody()
        }
    }

    private fun HTML.renderHeader() {
        head {
            title(pageTitle)
            // TODO local copy
            link {
                rel = "stylesheet"
                href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
                integrity = "sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
                attributes["crossorigin"] = "anonymous"
            }
            //link(rel = "stylesheet", href = "bootstrap.min.css")
        }
    }

    private fun HTML.renderBody() {
        body {
            renderNavigationBar()
            renderContent()
        }
    }

    private fun BODY.renderNavigationBar() {
        nav(classes = "navbar navbar-expand-lg navbar-light bg-light") {
            a(classes = "navbar-brand", href = "index.html") {
                +"Overview"
            }
            div(classes = "collapse navbar-collapse") {
                id = "navbarSupportedContent"
                ul(classes = "navbar-nav mr-auto") {
                    for (pageId in PageId.values()) {
                        li(classes = "nav-item" + activeClass(pageId)) {
                            // TODO active
                            a(classes = "nav-link", href = pageId.link) {
                                +pageId.title
                            }
                        }
                    }
                }
            }
        }
    }

    private fun activeClass(navPageId: PageId): String {
        return if (navPageId == pageId) {
            " active"
        } else {
            ""
        }
    }

    // TODO abstract
    protected open fun BODY.renderContent() {
        +"Not yet implemented"
    }

    protected fun HtmlBlockTag.moduleLink(module: SmiModule, text: Any? = null, anchor: String? = null) {
        val pageRef = "${module.id}.html"
        val href = if (anchor == null) pageRef else "$pageRef#$anchor"
        a(href = href) {
            +(text?.toString() ?: module.id)
        }
    }

    // TODO: the text parameter should be a body lambda
    protected fun HtmlBlockTag.symbolLink(symbol: SmiValue, text: Any? = null, anchor: String? = null, title: String? = null) {
        val href = symbol.module.id + ".html#" + (anchor ?: symbol.id)
        val myTitle = when {
            title != null -> title
            symbol is SmiObjectType -> symbol.description
            else -> ""
        }
        a(href = href) {
            this.title = myTitle
            +(text?.toString() ?: symbol.id)
        }
    }

    protected fun TABLE.variableRow(variable: SmiVariable) {
        tr {
            td { symbolLink(variable) }
            td { +variable.oidStr }
            td { +variable.type.toString() } // TODO link to type def
            td { +variable.accessAll.toString() }
        }
    }

    protected fun HtmlBlockTag.variable(level: Int, variable: SmiObjectType) {
        heading(level, variable)
        val parent = if (variable.node.parent.values.size > 1)
            variable.node.parent.oidStr
        else
            variable.node.parent.singleValue.id
        pre {
            // TODO: link to type def
            +"""
                ${variable.id} OBJECT-TYPE
                    SYNTAX      ${variable.type}
                    MAX-ACCESS  ${variable.accessAll}
                    STATUS      ${variable.status}
                    DESCRIPTION "${variable.description.replace("\n", "\n            ")}"
                ::= { ${parent} ${variable.lastOidComponent.valueToken.value} }
            """.trimIndent()
        }

    }

    @HtmlTagMarker
    private fun FlowOrHeadingContent.h(level: Int, classes: String? = null, block: H.() -> Unit = {}): Unit = H(level, attributesMapOf("class", classes), consumer).visit(block)

    protected fun HtmlBlockTag.heading(level: Int, oidValue: SmiOidValue) {
        h(level) {
            id = oidValue.id
            +"${oidValue.id} (${oidValue.oidStr})"
        }
    }


}

private class H(level: Int, initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>)
    : HTMLTag("h$level", consumer, initialAttributes, null, false, false), CommonAttributeGroupFacadeFlowHeadingPhrasingContent
