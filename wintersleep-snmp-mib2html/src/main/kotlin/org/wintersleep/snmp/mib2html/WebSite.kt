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

import org.wintersleep.snmp.mib.smi.SmiMib
import java.io.File

class WebSite(val dir: File,
              val mib: SmiMib) {

    fun render() {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw IllegalStateException("Could not create dir: $dir")
            }
        }
        HtmlOverviewPage(mib, dir).render()
        for (pageId in PageId.values()) {
            if (pageId == PageId.MODULES) {
                HtmlOverviewPage(mib, dir, pageId).render()
            } else {
                HtmlUnfinishedPage(dir, pageId).render()
            }
        }
        for (module in mib.modules) {
            HtmlModulePage(module, dir).render()
        }
    }

}