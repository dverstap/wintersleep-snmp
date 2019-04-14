/*
 * Copyright 2006 Davy Verstappen.
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
package org.wintersleep.snmp.mib.phase.xref;

import org.wintersleep.snmp.mib.exception.SmiException;
import org.wintersleep.snmp.mib.phase.Phase;
import org.wintersleep.snmp.mib.smi.SmiDefaultValue;
import org.wintersleep.snmp.mib.smi.SmiMib;
import org.wintersleep.snmp.mib.smi.SmiModule;
import org.wintersleep.snmp.mib.smi.SmiOidNode;
import org.wintersleep.snmp.mib.smi.SmiOidValue;
import org.wintersleep.snmp.mib.smi.SmiSymbol;
import org.wintersleep.snmp.mib.smi.SmiVariable;
import org.wintersleep.snmp.util.problem.DefaultProblemReporterFactory;
import org.wintersleep.snmp.util.problem.ProblemEventHandler;
import org.wintersleep.snmp.util.problem.ProblemReporterFactory;
import org.wintersleep.snmp.util.token.IdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class XRefPhase implements Phase {

    private static final Logger LOGGER = LoggerFactory.getLogger(XRefPhase.class);

    private XRefProblemReporter reporter;
    private Map<String, SymbolDefiner> symbolDefinerMap = new LinkedHashMap<String, SymbolDefiner>();

    public XRefPhase(XRefProblemReporter reporter) {
        this.reporter = reporter;
    }

    public XRefPhase(ProblemReporterFactory reporterFactory) {
        reporter = reporterFactory.create(XRefProblemReporter.class);
    }

    public XRefPhase(ProblemEventHandler eventHandler) {
        DefaultProblemReporterFactory reporterFactory = new DefaultProblemReporterFactory(eventHandler);
        reporter = reporterFactory.create(XRefProblemReporter.class);
    }

    public Object getOptions() {
        return null;
    }

    public Map<String, SymbolDefiner> getSymbolDefinerMap() {
        return symbolDefinerMap;
    }

    public void setSymbolDefinerMap(Map<String, SymbolDefiner> symbolDefinerMap) {
        this.symbolDefinerMap = symbolDefinerMap;
    }

    public XRefPhase addSymbolDefiner(String moduleId, SymbolDefiner symbolDefiner) {
        symbolDefinerMap.put(moduleId, symbolDefiner);
        return this;
    }

    public XRefPhase addSymbolDefiner(SymbolDefiner symbolDefiner) {
        symbolDefinerMap.put(symbolDefiner.getModuleId(), symbolDefiner);
        return this;
    }

    public XRefPhase addSymbolDefiners(SymbolDefiner... symbolDefiners) {
        for (SymbolDefiner symbolDefiner : symbolDefiners) {
            symbolDefinerMap.put(symbolDefiner.getModuleId(), symbolDefiner);
        }
        return this;
    }

    public SmiMib process(SmiMib mib) throws SmiException {

        defineMissingSymbols(mib);

        mib.fillTables();
        mib.defineMissingStandardOids();

        for (SmiModule module : mib.getModules()) {
            module.resolveImports(reporter);
        }

        Collection<SmiModule> modules = mib.getModules();
        resolveReferences(modules);
        resolveOids(modules);
        mib.fillExtraTables();
        resolveDefaultValues(mib);

        return mib;
    }

    protected void defineMissingSymbols(SmiMib mib) {
        for (Map.Entry<String, SymbolDefiner> entry : symbolDefinerMap.entrySet()) {
            SmiModule module = mib.findModule(entry.getKey());
            if (module == null) {
                module = mib.createModule(new IdToken(null, entry.getKey()));
            }
            entry.getValue().defineSymbols(module);
        }
    }

    protected void resolveReferences(Collection<SmiModule> modules) {
        for (SmiModule module : modules) {
            for (SmiSymbol symbol : module.getSymbols()) {
                symbol.resolveReferences(reporter);
            }
        }
    }

    protected void resolveOids(Collection<SmiModule> modules) {
        for (SmiModule module : modules) {
            LOGGER.debug("Resolving oids in module: " + module.getId() + " hash=" + module.getId().hashCode());
            for (SmiOidValue oidValue : module.getOidValues()) {
                oidValue.resolveOid(reporter);
            }
        }
        for (SmiModule module : modules) {
            for (SmiOidValue oidValue : module.getOidValues()) {
                SmiOidNode node = oidValue.getNode();
                if (node != null) {
                    node.determineFullOid();
                }
            }
        }
    }

    protected void resolveDefaultValues(SmiMib mib) {
        for (SmiVariable variable : mib.getVariables()) {
            SmiDefaultValue defaultValue = variable.getDefaultValue();
            if (defaultValue != null) {
                defaultValue.resolveReferences(reporter);
            }
        }
    }

}
