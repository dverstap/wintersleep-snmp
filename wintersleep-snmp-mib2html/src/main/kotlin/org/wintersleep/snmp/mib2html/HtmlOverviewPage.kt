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
import org.wintersleep.snmp.mib.smi.SmiMib
import java.io.File

class HtmlOverviewPage(
        val mib: SmiMib,
        dir: File,
        pageId: PageId? = null
) : HtmlBasePage(File(dir, pageId?.fileName ?: "index.html"),
        pageId?.title ?: "Overview",
        pageId) {

    override fun BODY.renderContent() {
        table(classes = "table table-striped table-bordered") {
            tr {
                th { }
                th { +"${mib.types.size()}" }
                th { +"${mib.scalars.size()}" }
                th { +"${mib.tables.size()}" }
                th { +"${mib.columns.size()}" }
            }

            tr {
                th { +"Name" }
                th { +"#Types" }
                th { +"#Scalars" }
                th { +"#Tables" }
                th { +"#Columns" }
            }
            for (module in mib.modules.sortedBy { it.id }) {
                tr {
                    td { moduleLink(module) }
                    td { moduleLink(module, module.types.size, "types") }
                    td { moduleLink(module, module.scalars.size, "scalars") }
                    td { moduleLink(module, module.tables.size, "tables") }
                    td { moduleLink(module, module.columns.size, "tables") }
                }
            }
        }
    }

}