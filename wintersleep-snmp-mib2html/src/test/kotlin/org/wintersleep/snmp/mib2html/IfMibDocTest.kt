/*
 * Copyright 2015 Davy Verstappen.
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

import org.junit.Test
import org.wintersleep.snmp.mib.AbstractMibTestCase
import org.wintersleep.snmp.mib.smi.SmiVersion
import org.wintersleep.snmp.util.url.DefaultURLListBuilder
import java.io.File

class IfMibDocTest : AbstractMibTestCase(SmiVersion.V2) {

    override fun addUrls(builder: DefaultURLListBuilder) {
        super.addUrls(builder)
        builder.addDir(AbstractMibTestCase.LIBSMI_IANA_DIR,
                "IANAifType-MIB")
        builder.addDir(AbstractMibTestCase.LIBSMI_IETF_DIR,
                "IF-MIB")
    }

    @Test
    fun test() {
        WebSite(File("target/html/ifMib"), mib).render()
    }

}
