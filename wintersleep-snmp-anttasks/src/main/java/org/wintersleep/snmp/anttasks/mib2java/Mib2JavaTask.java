/*
 * Copyright 2012-2016 Davy Verstappen.
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
package org.wintersleep.snmp.anttasks.mib2java;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.wintersleep.snmp.mib.parser.SmiDefaultParser;
import org.wintersleep.snmp.mib.smi.SmiMib;
import org.wintersleep.snmp.mib.smi.SmiModule;
import org.wintersleep.snmp.mib2java.BuilderFactory;
import org.wintersleep.snmp.mib2java.CodeBuilder;
import org.wintersleep.snmp.mib2java.DefaultCodeBuilderSettings;

import java.io.File;
import java.io.PrintStream;

public class Mib2JavaTask extends Task {

    private final DefaultCodeBuilderSettings settings = new DefaultCodeBuilderSettings();
    private SmiMib mib;
    private FileSet fileset;
    private File statusFile;
    private boolean failOnMibError = true;
    private BuilderFactory builderFactory;

    public void setPackageName(String packageName) {
        settings.setPackageName(packageName);
    }

    public SmiMib getMib() {
        return mib;
    }

    public void setMib(SmiMib mib) {
        this.mib = mib;
    }

    public FileSet getFileset() {
        return fileset;
    }

    public void setFileset(FileSet fileset) {
        this.fileset = fileset;
    }

    public void addFileset(FileSet fileset) {
        this.fileset = fileset;
    }

    public File getOutputDir() {
//        if (settings.getOutputDir() == null) {
//            settings.setOutputDir(new File(getProject().getBaseDir(), "target/generated-sources/mib2java"));
//        }
        return settings.getOutputDir();
    }

    public void setOutputDir(File outputDir) {
        settings.setOutputDir(outputDir);
    }

    public File getStatusFile() {
        if (statusFile != null) {
            return statusFile;
        }
        return new File(getOutputDir(), "." + Mib2JavaTask.class.getName() + "." + getTaskName());
    }

    public void setStatusFile(File statusFile) {
        this.statusFile = statusFile;
    }

    public boolean isFailOnMibError() {
        return failOnMibError;
    }

    public void setFailOnMibError(boolean failOnMibError) {
        this.failOnMibError = failOnMibError;
    }

    public BuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    @Override
    public void execute() throws BuildException {
        try {
            if (isUptodate()) {
                log("Generated java files are up to date.");
            } else {
                compile();
            }
        } catch (Exception e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private boolean isUptodate() {
        if (getStatusFile().exists()) {
            long statusLastModified = getStatusFile().lastModified();
            for (File file : FileSetURLListFactory.listFiles(fileset)) {
                if (file.exists() && file.lastModified() > statusLastModified) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void compile() throws Exception {
        SmiMib mib = determineMib();
        log("Compiling " + mib.getModules().size() + " mibs to package " + settings.getPackageName() + " to " + settings.getOutputDir());
        for (SmiModule module : mib.getModules()) {
            log(module.getId(), Project.MSG_DEBUG);
        }

        CodeBuilder codeBuilder = determineBuilderFactory().createCodeBuilder(mib);
        codeBuilder.createOutputDir();
        try (PrintStream status = new PrintStream(getStatusFile())) {
            codeBuilder.write(status);
        }
    }

    protected BuilderFactory determineBuilderFactory() {
        if (builderFactory != null) {
            return builderFactory;
        }
        return new BuilderFactory(settings);
    }

    protected SmiMib determineMib() throws Exception {
        if (mib != null) {
            return mib;
        } else if (fileset != null) {
            return parseMib();
        }
        throw new BuildException("Either mib or inputFiles should be set.");
    }

    // TODO log and report errors ...
    protected SmiMib parseMib() throws Exception {
        SmiDefaultParser parser = createParser();
        SmiMib result = parser.parse();
        if (parser.getProblemEventHandler().isNotOk()) {
            int problemCount = parser.getProblemEventHandler().getTotalCount();
            if (failOnMibError) {
                throw new BuildException("Found " + problemCount + " problems.");
            } else {
                log("Found " + problemCount + " problems, but continuing anyway, because failOnMibError is set to false.", Project.MSG_DEBUG);
            }
        }
        return result;
    }

    protected SmiDefaultParser createParser() throws Exception {
        SmiDefaultParser parser = new SmiDefaultParser();
        parser.getFileParserPhase().setInputUrls(new FileSetURLListFactory(fileset).create());
        return parser;
    }

}
