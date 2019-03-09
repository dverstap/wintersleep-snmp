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
import org.wintersleep.snmp.mib.smi.SmiModule
import org.wintersleep.snmp.mib.smi.SmiVariable
import java.io.File

class HtmlModulePage(
        val module: SmiModule,
        dir: File
) : HtmlBasePage(File(dir, module.id + ".html"),
        module.id) {

    override fun BODY.renderContent() {
        renderTop()
        renderTypes()
        renderScalars()
        renderTables()
    }


    private fun BODY.renderTop() {
        h1 { +module.id }
        p {
            +"TODO: description, revisions, contact (or rather put the administrative stuff at the back?)"
            // TODO copy and link to the original mib text file
        }
        ul {
            li { a(href = "#types") { +"Types (${module.types.size})" } }
            li { a(href = "#scalars") { +"Scalars (${module.scalars.size})" } }
            li { a(href = "#tables") { +"Tables (${module.tables.size})" } }
        }
    }

    private fun BODY.renderTypes() {
        h1 {
            id = "types"
            +"Types"
        }
        p {
            +"TODO"
        }
    }

    private fun BODY.renderScalars() {
        h1 {
            id = "scalars"
            +"Scalars"
        }
        variableTable(module.scalars)
        for (scalar in module.scalars) {
            variable(2, scalar)
        }
    }

    private fun BODY.variableTable(variableList: Collection<SmiVariable>) {
        table(classes = "table table-striped table-bordered") {
            tr {
                th { +"Name" }
                th { +"OID" }
                th { +"Type" }
                th { +"Access" }
            }
            for (scalar in variableList) {
                variableRow(scalar)
            }
        }
    }


    private fun BODY.renderTables() {
        h1 {
            id = "tables"
            +"Tables"
        }

        table(classes = "table table-striped table-bordered") {
            tr {
                th { +"Name" }
                th { +"OID" }
                th { +"Augments" }
                th { +"Index(es)" }
                th { +"#Columns" }
            }
            for (table in module.tables) {
                val augments = table.row.augments
                tr {
                    td { symbolLink(table) }
                    td { +table.oidStr }
                    td {
                        if (augments != null) {
                            symbolLink(augments)
                        }
                    }
                    td {
                        // TODO: indexes should not never return null, this is because the core library doesn't deal well with AUGMENTS
                        // TODO: handle IMPLIED
                        if (table.row.indexes != null) {
                            for (index in table.row.indexes) {
                                p { symbolLink(index.column) }
                            }
                        }
                        if (augments != null) {
                            for (index in augments.indexes) {
                                p { symbolLink(index.column) }
                            }
                        }
                    }
                    td {
                        symbolLink(table, table.row.columns.size,
                                title = table.row.columns.map { it.id }.joinToString("\n"))
                        // TODO ideally this is drop-down menu that allows you to directly go to the selected item
                    }
                }
            }
        }

        for (table in module.tables) {
            variable(2, table)
            variableTable(table.row.columns)
            variable(3, table.row)
            // TODO indexes, augments
            for (column in table.row.columns) {
                variable(4, column)
            }
        }
    }

}

