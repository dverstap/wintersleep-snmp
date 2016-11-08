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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wintersleep.snmp.mib.smi.SmiMib;
import org.wintersleep.snmp.mib.smi.SmiModule;
import org.wintersleep.snmp.mib.smi.SmiTable;

import java.io.*;
import java.util.Collections;

public class FreemarkerHtmlRenderer {

    private static final Logger log = LoggerFactory.getLogger(FreemarkerHtmlRenderer.class);

    private final Configuration configuration;
    private final SmiMib mib;

    public FreemarkerHtmlRenderer(Configuration configuration, SmiMib mib) {
        this.configuration = configuration;
        this.mib = mib;
    }

    public void render(File dir) throws IOException, TemplateException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Could not create directory: " + dir);
            }
        }
        copyBootstrap(dir);
        try (Writer w = new FileWriter(new File(dir, "index.html"))) {
            renderModules(w);
        }
        try (Writer w = new FileWriter(new File(dir, "tables.html"))) {
            renderTables(w);
        }
        for (SmiModule module : mib.getModules()) {
            try (Writer w = new FileWriter(new File(dir, module.getId() + ".html"))) {
                renderModule(module, w);
            }
            // TODO also copy the source file, and link to it
        }
        // TODO table names are not necessarily unique, so render in per-module directories:
        for (SmiTable table : mib.getTables()) {
            try (Writer w = new FileWriter(new File(dir, table.getId() + ".html"))) {
                log.info("Rendering table {}.", table.getId());
                // TODO this give an NPE
                //log.debug("Table {} has {} indexes.", table.getId(), table.getRow().getIndexes().size());
                renderTable(table, w);
            }
        }
    }

    private void copyBootstrap(File dir) throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.min.css")) {
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(dir, "bootstrap.min.css")))) {
                int b = is.read();
                while (b >= 0) {
                    os.write(b);
                    b = is.read();
                }
            }
        }
    }

    public void renderModules(Writer w) throws IOException, TemplateException {
        Template template = configuration.getTemplate("modules.html.ftl");
        template.process(mib, w);
    }

    public void renderTables(Writer w) throws IOException, TemplateException {
        Template template = configuration.getTemplate("tables.html.ftl");
        template.process(Collections.singletonMap("tables", mib.getTables().getAll()), w);
    }

    private void renderModule(SmiModule module, Writer w) throws IOException, TemplateException {
        Template template = configuration.getTemplate("module.html.ftl");
        template.process(Collections.singletonMap("module", module), w);
    }

    private void renderTable(SmiTable table, Writer w) throws IOException, TemplateException {
        Template template = configuration.getTemplate("table.html.ftl");
        template.process(Collections.singletonMap("table", table), w);
    }

}
