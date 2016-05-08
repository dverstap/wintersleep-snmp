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

import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;

public class ConfigurationBuilder {

    public Configuration build() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //result.setTemplateLoader(new ClassTemplateLoader(ConfigurationBuilder.class, "org.wintersleep.snmp.mib2doc.html"));
        cfg.setTemplateLoader(new ClassTemplateLoader(ConfigurationBuilder.class, ""));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build());
        //cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
        cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
//        Map<String, String> autoImports = new HashMap<>();
//        autoImports.put("layout", "layout.ftl");
//        cfg.setAutoImports(autoImports);
        cfg.addAutoInclude("layout.ftl");
        cfg.addAutoInclude("variablerow.html.ftl");
        cfg.addAutoInclude("variable.html.ftl");
        return cfg;
    }

}
