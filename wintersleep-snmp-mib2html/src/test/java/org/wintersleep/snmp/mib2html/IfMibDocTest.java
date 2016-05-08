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
package org.wintersleep.snmp.mib2html;

import freemarker.template.TemplateException;
import org.junit.Test;
import org.wintersleep.snmp.mib.AbstractMibTestCase;
import org.wintersleep.snmp.mib.smi.SmiModule;
import org.wintersleep.snmp.mib.smi.SmiVersion;

import java.io.File;
import java.io.IOException;

public class IfMibDocTest extends AbstractMibTestCase {

    public IfMibDocTest() {
        super(SmiVersion.V2,
                LIBSMI_MIBS_URL + "/iana/IANAifType-MIB",
                LIBSMI_MIBS_URL + "/ietf/IF-MIB");
    }

    @Test
    public void testFreemarker() throws IOException, TemplateException {
        SmiModule ifMib = getMib().findModule("IF-MIB");
        assertNotNull(ifMib);

        FreemarkerHtmlRenderer renderer = new FreemarkerHtmlRenderer(new ConfigurationBuilder().build(),
                getMib());
        renderer.render(new File("target/html"));

        Runtime.getRuntime().exec("firefox target/html/index.html");
    }

}
