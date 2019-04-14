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
import org.wintersleep.snmp.util.token.IdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmiModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmiModule.class);

    private SmiMib mib;
    private IdToken idToken;

    private List<SmiImports> imports = new ArrayList<SmiImports>();
    private List<SmiSymbol> symbols = new ArrayList<SmiSymbol>();

    Map<String, SmiType> typeMap = new LinkedHashMap<String, SmiType>();
    Map<String, SmiTextualConvention> textualConventionMap = new LinkedHashMap<String, SmiTextualConvention>();
    Map<String, SmiSymbol> symbolMap = new LinkedHashMap<String, SmiSymbol>();
    Map<String, SmiVariable> variableMap = new LinkedHashMap<String, SmiVariable>();
    Map<String, SmiVariable> scalarMap = new LinkedHashMap<String, SmiVariable>();
    Map<String, SmiTable> tableMap = new LinkedHashMap<String, SmiTable>();
    Map<String, SmiRow> rowMap = new LinkedHashMap<String, SmiRow>();
    Map<String, SmiVariable> columnMap = new LinkedHashMap<String, SmiVariable>();
    Map<String, SmiOidValue> oidValueMap = new LinkedHashMap<String, SmiOidValue>();
    Map<String, SmiObjectType> objectTypeMap = new LinkedHashMap<String, SmiObjectType>();
    Map<String, SmiNotificationType> notificationTypeMap = new LinkedHashMap<String, SmiNotificationType>();
    Map<String, SmiTrapType> trapTypeMap = new LinkedHashMap<String, SmiTrapType>();

    private int v1Features = 0;
    private int v2Features = 0;
    private SmiVersion version;
    private boolean isSmiDefinitionModule;

    private SmiModuleIdentity moduleIdentity;

    public SmiModule(SmiMib mib, IdToken idToken) {
        this.mib = mib;
        if (idToken == null) {
            throw new IllegalArgumentException();
        }
        setIdToken(idToken);
        isSmiDefinitionModule = SmiConstants.SMI_DEFINITION_MODULE_NAMES.contains(idToken.getId());
    }


    public int getV1Features() {
        return v1Features;
    }

    public void incV1Features() {
        v1Features++;
    }

    public int getV2Features() {
        return v2Features;
    }

    // TODO this needs to be applied in more cases, such as SNMPv2-CONF, where the V2 macro's are defined
    public void incV2Features() {
        v2Features++;
    }

    public SmiVersion getVersion() {
        if (version == null && (v1Features != 0 || v2Features != 0)) {
            version = determineVersion();
        }
        return version;
    }

    private SmiVersion determineVersion() {
        if (v1Features > v2Features) {
            return SmiVersion.V1;
        } else if (v1Features < v2Features) {
            return SmiVersion.V2;
        }
        LOGGER.info("interesting mib with equal amount of V1 and V2 features: " + v1Features + ": " + getIdToken());
        return null;
    }

    public SmiType findType(String id) {
        return typeMap.get(id);
    }

    public Collection<SmiType> getTypes() {
        return typeMap.values();
    }

    public SmiTextualConvention findTextualConvention(String id) {
        return textualConventionMap.get(id);
    }

    public Collection<SmiTextualConvention> getTextualConventions() {
        return textualConventionMap.values();
    }

    public Collection<SmiSymbol> getSymbols() {
        // TODO when the symbols have been resolved, set the symbols list to null?
        if (symbols != null) {
            return symbols;
        } else {
            return symbolMap.values();
        }
    }

    public SmiSymbol findSymbol(String id) {
        return symbolMap.get(id);
    }

    public SmiVariable findVariable(String id) {
        return variableMap.get(id);
    }

    public Collection<SmiVariable> getVariables() {
        return variableMap.values();
    }

    public SmiVariable findScalar(String id) {
        return scalarMap.get(id);
    }

    public Collection<SmiVariable> getScalars() {
        return scalarMap.values();
    }

    public SmiTable findTable(String id) {
        return tableMap.get(id);
    }

    public Collection<SmiTable> getTables() {
        return tableMap.values();
    }

    public SmiRow findRow(String id) {
        return rowMap.get(id);
    }

    public Collection<SmiRow> getRows() {
        return rowMap.values();
    }

    public SmiVariable findColumn(String id) {
        return columnMap.get(id);
    }

    public Collection<SmiVariable> getColumns() {
        return columnMap.values();
    }

    public SmiOidValue findOidValue(String id) {
        return oidValueMap.get(id);
    }

    public Collection<SmiOidValue> getOidValues() {
        return oidValueMap.values();
    }

    public SmiObjectType findObjectType(String id) {
        return objectTypeMap.get(id);
    }
    
    public SmiNotificationType findNotificationType(String id) {
    	return notificationTypeMap.get(id);
    }
    
    public SmiTrapType findTrapType(String id) {
    	return trapTypeMap.get(id);
    }

    public Collection<SmiObjectType> getObjectTypes() {
        return objectTypeMap.values();
    }
    
    public Collection<SmiNotificationType> getNotificationTypes() {
    	return notificationTypeMap.values();
    }
    
    public Collection<SmiTrapType> getTrapTypes() {
    	return trapTypeMap.values();
    }

    public void setIdToken(IdToken id) {
        assert (idToken == null);
        idToken = id;
        mib.addModule(id.getId(), this);
    }

    public IdToken getIdToken() {
        return idToken;
    }

    public String getId() {
        return idToken.getId();
    }

    public SmiMib getMib() {
        return mib;
    }

    public SmiModuleIdentity getModuleIdentity() {
        return moduleIdentity;
    }

    public void setModuleIdentity(SmiModuleIdentity identity) {
        moduleIdentity = identity;
    }

    public SmiType createType(IdToken idToken) {
        SmiType type = new SmiType(idToken, this);
        typeMap.put(idToken.getId(), type);
        return type;
    }

/*
    public SmiTextualConvention createTextualConvention(IdToken idToken) {
        SmiTextualConvention tc = new SmiTextualConvention(idToken, this);
        typeMap.put(idToken.getId(), tc);
        return tc;
    }
*/

    public String getCodeId() {
        return getMib().getCodeNamingStrategy().getModuleId(this);
    }

    public String getFullCodeId() {
        return getMib().getCodeNamingStrategy().getFullModuleId(this);
    }

    public SmiTable createTable(IdToken idToken) {
        SmiTable table = new SmiTable(idToken, this);
        tableMap.put(idToken.getId(), table);
        return table;
    }

    public SmiRow createRow(IdToken idToken) {
        SmiRow row = new SmiRow(idToken, this);
        rowMap.put(idToken.getId(), row);
        return row;
    }

    public String getFullVariableOidClassId() {
        return getMib().getCodeNamingStrategy().getFullVariableOidClassId(this);
    }

    public String getVariableOidClassId() {
        return getMib().getCodeNamingStrategy().getVariableOidClassId(this);
    }

    public boolean isSmiDefinitionModule() {
        return isSmiDefinitionModule;
    }

    /**
     * @return The list of IMPORTS statements. Note that there may be more than one IMPORTS statement per module,
     *         so this is not guaranteed to be unique.
     */
    public List<SmiImports> getImports() {
        return imports;
    }

    /**
     * @return Unique set of imported modules.
     */
    public Set<SmiModule> getImportedModules() {
        Set<SmiModule> result = new HashSet<SmiModule>();
        for (SmiImports anImport : imports) {
            result.add(anImport.getModule());
        }
        return result;
    }

    public void fillTables() {
        for (SmiSymbol symbol : symbols) {
            put(tableMap, SmiTable.class, symbol);
            put(variableMap, SmiVariable.class, symbol);
            put(typeMap, SmiType.class, symbol);
            put(textualConventionMap, SmiTextualConvention.class, symbol);
            put(rowMap, SmiRow.class, symbol);
            put(oidValueMap, SmiOidValue.class, symbol);
            put(objectTypeMap, SmiObjectType.class, symbol);
            put(notificationTypeMap, SmiNotificationType.class, symbol);
            put(trapTypeMap, SmiTrapType.class, symbol);
        }
    }

    public void fillExtraTables() {
        for (SmiVariable variable : variableMap.values()) {
            if (variable.isColumn()) {
                columnMap.put(variable.getId(), variable);
            } else {
                scalarMap.put(variable.getId(), variable);
            }
        }
    }

    private <T extends SmiSymbol> void put(Map<String, T> map, Class<T> clazz, SmiSymbol symbol) {
        if (clazz.isInstance(symbol)) {
            map.put(symbol.getId(), clazz.cast(symbol));
        }
    }

    public void addSymbol(SmiSymbol symbol) {
        symbols.add(symbol);
        symbolMap.put(symbol.getId(), symbol);
    }

    /**
     * Resolves a reference from within this module to a symbol in the same module, an imported module
     * or in the whole mib
     *
     * @param idToken  Token of the identifier that has to be resolved.
     * @param reporter If not null, the reporter will be used to reporter the not found error message.
     * @return The symbol that was found, or null.
     */
    public SmiSymbol resolveReference(IdToken idToken, XRefProblemReporter reporter) {
// doesn't work anymore with hardcoded missing symbols
//        if (!idToken.getLocation().getSource().equals(getIdToken().getLocation().getSource())) {
//            // note this check is not entirely fool-proof in case multiple modules are located in one file
//            throw new IllegalArgumentException("Resolving references is only allowed from inside the same module");
//        }

        SmiSymbol result = findSymbol(idToken.getId());
        if (result == null) {
            result = findImportedSymbol(idToken.getId());
        }
        if (result == null) {
            List<SmiSymbol> symbols = getMib().getSymbols().findAll(idToken.getId());
            if (symbols.size() == 1) {
                result = symbols.get(0);
            } else if (symbols.size() > 0) {
                result = determineBestMatch(idToken, symbols);
            }
        }
        if (result == null && reporter != null) {
            reporter.reportCannotFindSymbol(idToken);
        }

        return result;
    }

    public <T extends SmiSymbol> T resolveReference(IdToken idToken, Class<T> expectedClass, XRefProblemReporter reporter) {
        SmiSymbol result = resolveReference(idToken, reporter);
        if (result != null) {
            if (expectedClass.isInstance(result)) {
                return expectedClass.cast(result);
            } else {
                reporter.reportFoundSymbolButWrongType(idToken, expectedClass, result.getClass());
            }
        }
        return null;
    }

    private SmiSymbol determineBestMatch(IdToken idToken, List<SmiSymbol> symbols) {
        SmiSymbol result = determineBestMatchBasedOnSnmpVersion(symbols);
        if (result != null) {
            return result;
        }
        result = determineBestMatchBasedOnOtherImports(idToken, symbols);
        if (result != null) {
            return result;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Couldn't choose between " + symbols.size() + " choices for resolving: " + idToken + ":");
            for (SmiSymbol symbol : symbols) {
                LOGGER.debug(symbol.toString());
            }
        }
        return null;
    }

    private SmiSymbol determineBestMatchBasedOnOtherImports(IdToken idToken, List<SmiSymbol> symbols) {
        for (SmiSymbol symbol : symbols) {
            for (SmiImports imports : imports) {
                if (imports.getModule() == symbol.getModule()) {
                    LOGGER.debug("Determined best match for " + idToken + " based on other imports from " + symbol.getModule().getId());
                    return symbol;
                }
            }
        }
        return null;
    }

    private SmiSymbol determineBestMatchBasedOnSnmpVersion(List<SmiSymbol> symbols) {
        if (symbols.size() == 2) {
            SmiSymbol s0 = symbols.get(0);
            SmiSymbol s1 = symbols.get(1);
            SmiVersion version0 = s0.getModule().getVersion();
            SmiVersion version1 = s1.getModule().getVersion();
            if (version0 != null && version1 != null && version0 != version1) {
                if (getVersion() == version0) {
                    return s0;
                } else if (getVersion() == version1) {
                    return s1;
                }
            }
        }
        return null;
    }

    private SmiSymbol findImportedSymbol(String id) {
        for (SmiImports imports : imports) {
            SmiSymbol symbol = imports.find(id);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public void resolveImports(XRefProblemReporter reporter) {
        for (SmiImports imports : imports) {
            imports.resolveImports(reporter);
        }
        // TODO check for imports with the same id
    }


    public String toString() {
        return idToken.toString();
    }
}
