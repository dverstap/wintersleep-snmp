/*
 * Copyright 2005 Davy Verstappen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wintersleep.snmp.mib.parser;

import org.wintersleep.snmp.mib.phase.Phase;
import org.wintersleep.snmp.mib.exception.SmiException;
import org.wintersleep.snmp.mib.phase.check.ErrorCheckPhase;
import org.wintersleep.snmp.mib.phase.file.FileParserPhase;
import org.wintersleep.snmp.mib.phase.file.FileParserProblemReporter;
import org.wintersleep.snmp.mib.phase.xref.XRefPhase;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.mib.smi.SmiJavaCodeNamingStrategy;
import org.wintersleep.snmp.mib.smi.SmiMib;
import org.wintersleep.snmp.mib.smi.SmiOptions;
import org.wintersleep.snmp.util.problem.DefaultProblemEventHandler;
import org.wintersleep.snmp.util.problem.DefaultProblemReporterFactory;
import org.wintersleep.snmp.util.problem.ProblemEventHandler;
import org.wintersleep.snmp.util.problem.ProblemReporterFactory;

public class SmiDefaultParser implements SmiParser {

    protected boolean failOnError = false;
    protected ProblemReporterFactory problemReporterFactory;
    protected FileParserPhase fileParserPhase;
    protected XRefPhase xRefPhase;
    protected ErrorCheckPhase errorCheckPhase;
    protected SmiOptions options = new SmiOptions();

    public SmiDefaultParser() {
        this(new DefaultProblemEventHandler());
    }

    public SmiDefaultParser(ProblemEventHandler problemEventHandler) {
        this(new DefaultProblemReporterFactory(problemEventHandler));
    }

    public SmiDefaultParser(ProblemReporterFactory problemReporterFactory) {
        this.problemReporterFactory = problemReporterFactory;
    }

    public SmiMib parse() throws SmiException {
        SmiMib mib = new SmiMib(options, new SmiJavaCodeNamingStrategy("org.wintersleep.snmp.mib")); // TODO
        
        Phase[] phases = new Phase[]{getFileParserPhase(), getXRefPhase(), getErrorCheckPhase()};
        for (Phase phase : phases) {
            phase.process(mib);
        }

        if (failOnError && getProblemReporterFactory().getProblemEventHandler().isNotOk()) {
            throw new SmiException();
        }
        return mib;
    }

    public SmiOptions getOptions() {
        return options;
    }

    public void setOptions(SmiOptions options) {
        this.options = options;
    }

    protected FileParserPhase createFileParserPhase() {
        return new FileParserPhase(getProblemReporterFactory().create(FileParserProblemReporter.class));
    }

    protected XRefPhase createXRefPhase() {
        return new XRefPhase(getProblemReporterFactory().create(XRefProblemReporter.class));
    }

    private ErrorCheckPhase createErrorCheckPhase() {
        return new ErrorCheckPhase();
    }

    public ProblemEventHandler getProblemEventHandler() {
        return problemReporterFactory.getProblemEventHandler();
    }
    
    public ProblemReporterFactory getProblemReporterFactory() {
        return problemReporterFactory;
    }

    public void setProblemReporterFactory(ProblemReporterFactory problemReporterFactory) {
        this.problemReporterFactory = problemReporterFactory;
    }

    public FileParserPhase getFileParserPhase() {
        if (fileParserPhase == null) {
            fileParserPhase = createFileParserPhase();
        }
        return fileParserPhase;
    }

    public void setFileParserPhase(FileParserPhase fileParserPhase) {
        this.fileParserPhase = fileParserPhase;
    }

    public XRefPhase getXRefPhase() {
        if (xRefPhase == null) {
            xRefPhase = createXRefPhase();
        }
        return xRefPhase;
    }

    public void setXRefPhase(XRefPhase xrefPhase) {
        xRefPhase = xrefPhase;
    }

    public ErrorCheckPhase getErrorCheckPhase() {
        if (errorCheckPhase == null) {
            errorCheckPhase = createErrorCheckPhase();
        }
        return errorCheckPhase;
    }

    public void setErrorCheckPhase(ErrorCheckPhase errorCheckPhase) {
        this.errorCheckPhase = errorCheckPhase;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}
