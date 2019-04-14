/*
 * Copyright 2005 Davy Verstappen.
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
package org.wintersleep.snmp.mib.smi;

import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.token.IdToken;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SmiMib {

    private Map<String, SmiModule> moduleMap = new LinkedHashMap<String, SmiModule>();
    private final SmiOptions options;
    private SmiCodeNamingStrategy codeNamingStrategy;
    private SmiOidNode rootNode;

    SmiSymbolMapImpl<SmiType> typeMap = new SmiSymbolMapImpl<SmiType>(SmiType.class, moduleMap);
    SmiSymbolMapImpl<SmiTextualConvention> textualConventionMap = new SmiSymbolMapImpl<SmiTextualConvention>(SmiTextualConvention.class, moduleMap);
    SmiSymbolMapImpl<SmiSymbol> symbolMap = new SmiSymbolMapImpl<SmiSymbol>(SmiSymbol.class, moduleMap);
    SmiSymbolMapImpl<SmiVariable> variableMap = new SmiSymbolMapImpl<SmiVariable>(SmiVariable.class, moduleMap);
    SmiSymbolMapImpl<SmiTable> tableMap = new SmiSymbolMapImpl<SmiTable>(SmiTable.class, moduleMap);
    SmiSymbolMapImpl<SmiRow> rowMap = new SmiSymbolMapImpl<SmiRow>(SmiRow.class, moduleMap);
    SmiSymbolMapImpl<SmiVariable> columnMap = new SmiSymbolMapImpl<SmiVariable>(SmiVariable.class, moduleMap);
    SmiSymbolMapImpl<SmiVariable> scalarMap = new SmiSymbolMapImpl<SmiVariable>(SmiVariable.class, moduleMap);
    SmiSymbolMapImpl<SmiOidValue> oidValueMap = new SmiSymbolMapImpl<SmiOidValue>(SmiOidValue.class, moduleMap);
    SmiSymbolMapImpl<SmiObjectType> objectTypesMap = new SmiSymbolMapImpl<SmiObjectType>(SmiObjectType.class, moduleMap);
    SmiSymbolMapImpl<SmiNotificationType> notificationTypesMap = new SmiSymbolMapImpl<SmiNotificationType>(SmiNotificationType.class, moduleMap);
    SmiSymbolMapImpl<SmiTrapType> trapTypesMap = new SmiSymbolMapImpl<SmiTrapType>(SmiTrapType.class, moduleMap);

    int dummyOidNodesCount;
    private SmiModule internalModule;

    public SmiMib(SmiOptions options, SmiCodeNamingStrategy codeNamingStrategy) {
        this.options = options;

        //assert(codeNamingStrategy != null);
        this.codeNamingStrategy = codeNamingStrategy;

        SmiModule internalMib = buildInternalMib();
        moduleMap.put(internalMib.getId(), internalMib);
    }

    private SmiModule buildInternalMib() {
        Location location = new Location("JSMI_INTERNAL_MIB");
        internalModule = new SmiModule(this, new IdToken(location, "JSMI_INTERNAL_MIB"));

        rootNode = SmiOidNode.createRootNode();
        //rootNode.setOidComponents(Collections.<OidComponent>emptyList());

        return internalModule;
    }

    public SmiOidNode getRootNode() {
        return rootNode;
    }

    public SmiModule getInternalModule() {
        return internalModule;
    }

    public SmiModule findModule(String id) {
        return moduleMap.get(id);
    }

    public Collection<SmiModule> getModules() {
        return moduleMap.values();
    }

    public SmiOptions getOptions() {
        return options;
    }

    public SmiCodeNamingStrategy getCodeNamingStrategy() {
        return codeNamingStrategy;
    }

    public void setCodeNamingStrategy(SmiCodeNamingStrategy codeNamingStrategy) {
        this.codeNamingStrategy = codeNamingStrategy;
    }

    public SmiModule createModule(IdToken idToken) {
        SmiModule oldModule = moduleMap.get(idToken.getId());
        if (oldModule != null) {
            // TODO error handling
            // should do this in the XRefPhase
            throw new IllegalStateException("Duplicate module: " + oldModule.getIdToken() + " is already defined when adding: " + idToken);
        }
        SmiModule module = new SmiModule(this, idToken);
        moduleMap.put(module.getId(), module);
        return module;
    }

    public void determineInheritanceRelations() {
        for (SmiRow row : rowMap.values()) {
            if (row.getAugments() != null) {
                row.addParentRow(row.getAugments());
            } else if (row.getIndexes().size() == 1) {
                SmiIndex index = row.getIndexes().get(0);
                SmiRow indexRow = index.getColumn().getRow();
                if (row != indexRow) {
                    row.addParentRow(indexRow);
                }
            } else if (row.getIndexes().size() > 1) {
                SmiIndex lastIndex = row.getIndexes().get(row.getIndexes().size() - 1);
                SmiRow lastIndexRow = lastIndex.getColumn().getRow();
                if (row != lastIndexRow && row.hasSameIndexes(lastIndexRow)) {
                    row.addParentRow(lastIndexRow);
                }
            }
        }
    }

    void addModule(String id, SmiModule module) {
        SmiModule oldModule = moduleMap.get(id);
        if (oldModule != null) {
            throw new IllegalArgumentException("Mib already contains a module: " + oldModule);
        }
        moduleMap.put(id, module);
    }

    public void fillTables() {
        // TODO deal with double defines
        for (SmiModule module : moduleMap.values()) {
            module.fillTables();
            typeMap.putAll(module.typeMap);
            textualConventionMap.putAll(module.textualConventionMap);
            variableMap.putAll(module.variableMap);
            rowMap.putAll(module.rowMap);
            tableMap.putAll(module.tableMap);
            symbolMap.putAll(module.symbolMap);
            oidValueMap.putAll(module.oidValueMap);
            objectTypesMap.putAll(module.objectTypeMap);
            notificationTypesMap.putAll(module.notificationTypeMap);
            trapTypesMap.putAll(module.trapTypeMap);
        }
    }

    public void fillExtraTables() {
        // TODO deal with double defines
        for (SmiModule module : moduleMap.values()) {
            module.fillExtraTables();
            scalarMap.putAll(module.scalarMap);
            columnMap.putAll(module.columnMap);
        }
    }


    public int getDummyOidNodesCount() {
        return dummyOidNodesCount;
    }

    public SmiSymbolMap<SmiType> getTypes() {
        return typeMap;
    }

    public SmiSymbolMap<SmiTextualConvention> getTextualConventions() {
        return textualConventionMap;
    }

    public SmiSymbolMap<SmiSymbol> getSymbols() {
        return symbolMap;
    }

    public SmiSymbolMap<SmiVariable> getVariables() {
        return variableMap;
    }

    public SmiSymbolMap<SmiTable> getTables() {
        return tableMap;
    }

    public SmiSymbolMap<SmiRow> getRows() {
        return rowMap;
    }

    public SmiSymbolMap<SmiVariable> getColumns() {
        return columnMap;
    }

    public SmiSymbolMap<SmiVariable> getScalars() {
        return scalarMap;
    }

    public SmiSymbolMap<SmiOidValue> getOidValues() {
        return oidValueMap;
    }

    public SmiSymbolMap<SmiObjectType> getObjectTypes() {
        return objectTypesMap;
    }
    
    public SmiSymbolMap<SmiNotificationType> getNotificationTypes() {
    	return notificationTypesMap;
    }
    
    public SmiSymbolMap<SmiTrapType> getTrapTypes() {
    	return trapTypesMap;
    }

    public SmiOidNode findByOid(int... oid) {
        SmiOidNode child = null;
        SmiOidNode parent = getRootNode();
        for (int oidPart : oid) {
            child = parent.findChild(oidPart);
            if (child == null) {
                return null;
            }
            parent = child;
        }
        return child;
    }

    /**
     * This method can be used to find the best match for an OID.
     * By comparing the length of the OID of the result and the input OID you can
     * determine how many and which subIds where not matched.
     *
     * @param oid For which the best match is searched.
     * @return Best matching SmiOidValue, or null if none is found.
     */
    public SmiOidNode findByOidPrefix(int... oid) {
        SmiOidNode parent = getRootNode();
        for (int subId : oid) {
            SmiOidNode result = parent.findChild(subId);
            if (result == null) {
                return parent;
            }
            parent = result;
        }
        return null;
    }

    public Set<SmiModule> findModules(SmiVersion version) {
        Set<SmiModule> result = new HashSet<SmiModule>();
        for (SmiModule module : moduleMap.values()) {
            if (module.getVersion() == null || module.getVersion() == version) {
                result.add(module);
            }
        }
        return result;
    }

    public void defineMissingStandardOids() {
        Location location = internalModule.getIdToken().getLocation();

        if (symbolMap.findAll("itu").isEmpty()) {
            SmiOidNode ituNode = new SmiOidNode(rootNode, 0);
            SmiOidValue itu = new SmiOidValue(new IdToken(location, "itu"), internalModule, ituNode);
            //itu.setLastOidComponent(new OidComponent(null, null, new IntegerToken(location, 0)));
            internalModule.addSymbol(itu);
            internalModule.symbolMap.put(itu.getId(), itu);
            symbolMap.put(itu.getId(), itu);
            oidValueMap.put(itu.getId(), itu);
        }

        if (symbolMap.findAll("iso").isEmpty()) {
            SmiOidNode isoNode = new SmiOidNode(rootNode, 1);
            SmiOidValue iso = new SmiOidValue(new IdToken(location, "iso"), internalModule, isoNode);
            internalModule.addSymbol(iso);
            internalModule.symbolMap.put(iso.getId(), iso);
            symbolMap.put(iso.getId(), iso);
            oidValueMap.put(iso.getId(), iso);
        }
    }

    public SmiModule resolveModule(IdToken moduleToken, XRefProblemReporter reporter) {
        SmiModule result = moduleMap.get(moduleToken.getId());
        if (result == null) {
            reporter.reportCannotFindModule(moduleToken);
        }
        return result;
    }

}
