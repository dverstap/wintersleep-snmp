/*
 * Copyright 2016 Davy Verstappen.
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
import org.wintersleep.snmp.mib.parser.LibSmiParserFactory;
import org.wintersleep.snmp.mib.parser.SmiDefaultParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LibSmiDocTest extends AbstractMibTestCase{

    public LibSmiDocTest() {
        super(null);
    }

    @Override
    protected SmiDefaultParser createParser() throws Exception {
        URL mibsURL = getClass().getClassLoader().getResource(LIBSMI_MIBS_URL);
        if (mibsURL == null) {
            throw new IllegalStateException("Could not find resource: " + LIBSMI_MIBS_URL);
        }
        File mibsDir = new File(mibsURL.toURI());
        return new LibSmiParserFactory(mibsDir).create();
    }

    protected boolean mustParseSuccessfully() {
        return false;
    }

    @Test
    public void test() throws IOException, TemplateException {
        FreemarkerHtmlRenderer renderer = new FreemarkerHtmlRenderer(new ConfigurationBuilder().build(),
                getMib());
        renderer.render(new File("target/html"));

        //Runtime.getRuntime().exec("firefox target/html/index.html");

    }

}
