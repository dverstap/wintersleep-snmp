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
package org.wintersleep.snmp.mib.phase.file;

import antlr.Token;
import org.wintersleep.snmp.mib.smi.*;
import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.token.BigIntegerToken;
import org.wintersleep.snmp.util.token.BinaryStringToken;
import org.wintersleep.snmp.util.token.HexStringToken;
import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.util.token.IntegerToken;
import org.wintersleep.snmp.util.token.QuotedStringToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.wintersleep.snmp.mib.smi.SmiPrimitiveType.INTEGER;

public class ModuleParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleParser.class);

    private final SmiModule module;

    public ModuleParser(SmiModule module) {
        this.module = module;
    }

    public SmiModule getModule() {
        return module;
    }

    private Location makeLocation(Token token) {
        String source = module.getIdToken().getLocation().getSource();
        return new Location(source, token.getLine(), token.getColumn());
    }

    public IdToken idt(Token idToken) {
        return new IdToken(makeLocation(idToken), idToken.getText());
    }

    public IdToken idt(Token... tokens) {
        for (Token token : tokens) {
            if (token != null) {
                return idt(token);
            }
        }
        return null;
    }

    public IntKeywordToken intkt(Token idToken, SmiPrimitiveType primitiveType, SmiVersion version) {
        if (version != null) {
            if (version == SmiVersion.V1) {
                module.incV1Features();
            } else {
                module.incV2Features();
            }
        }
        return new IntKeywordToken(makeLocation(idToken), idToken.getText(), primitiveType);
    }

    public String getCStr(Token t) {
        String text = t.getText();
        if (!(text.startsWith("\"") && text.endsWith("\""))) {
            throw new IllegalArgumentException(t.getText());
        }
        return text.substring(1, text.length() - 1);
    }

    public String getOptCStr(Token t) {
        if (t != null) {
            return getCStr(t);
        }
        return null;
    }

    public IntegerToken intt(Token t) {
        int value = Integer.parseInt(t.getText());
        return new IntegerToken(makeLocation(t), value);
    }

    public BigIntegerToken bintt(Token t) {
        return new BigIntegerToken(makeLocation(t), false, t.getText());
    }

    public BigIntegerToken bintt(Token minusToken, Token t) {
        return new BigIntegerToken(makeLocation(t), minusToken != null, t.getText());
    }

    public List<IdToken> makeIdTokenList() {
        return new ArrayList<IdToken>();
    }

    public void addImports(IdToken moduleToken, List<IdToken> importedTokenList) {
        SmiImports result = new SmiImports(module, moduleToken, importedTokenList);
        module.getImports().add(result);
    }

    public OidComponent createOidComponent(OidComponent parent, Token id, Token value) {
        IdToken idToken = id != null ? idt(id) : null;
        IntegerToken valueToken = value != null ? intt(value) : null;
        return new OidComponent(parent, idToken, valueToken);
    }

    public SmiOidValue createOidValue(IdToken idToken, OidComponent lastOidComponent) {
        SmiOidValue result = new SmiOidValue(idToken, module);
        result.setLastOidComponent(lastOidComponent);
        return result;
    }

    public SmiMacro createMacro(IdToken idToken) {
        return new SmiMacro(idToken, module);
    }

    public SmiOidMacro createOidMacro(IdToken idToken) {
        return new SmiOidMacro(idToken, module);
    }

    public SmiVariable createVariable(IdToken idToken, SmiType t, Token units, SmiDefaultValue defaultValue) {
        final String methodWithParams = "createVariable(" + idToken.getId() + ")";
        LOGGER.debug(methodWithParams);

        QuotedStringToken unitsToken = null;
        if (units != null) {
            unitsToken = new QuotedStringToken(makeLocation(units), units.getText(), '\"');
        }
        return new SmiVariable(idToken, module, t, unitsToken, defaultValue);
    }

    public SmiNotificationType createNotification(IdToken idToken, List<IdToken> objectTokens,
                                                  StatusV2 status, String description, String reference) {
        final String methodWithParams = "createNotification(" + idToken.getId() + ")";
        LOGGER.debug(methodWithParams);

        return new SmiNotificationType(idToken, module, objectTokens, status, description, reference);
    }

    public SmiTrapType createTrap(IdToken idToken, IdToken enterpriseIdToken,
                                  List<IdToken> objectTokens, String description, String reference) {
        final String methodWithParams = "createTrap(" + idToken.getId() + ")";
        LOGGER.debug(methodWithParams);

        return new SmiTrapType(idToken, module, enterpriseIdToken, objectTokens,
                description, reference);
    }

    public SmiRow createRow(IdToken idToken, SmiType t) {
        final String methodWithParams = "createRow(" + idToken.getId() + ")";
        LOGGER.debug(methodWithParams);

        SmiRow result = new SmiRow(idToken, module);
        result.setType(t);
        return result;
    }

    public SmiTable createTable(IdToken idToken, SmiType t) {
        final String methodWithParams = "createTable(" + idToken.getId() + ")";
        LOGGER.debug(methodWithParams);

        SmiTable result = new SmiTable(idToken, module);
        result.setType(t);
        return result;
    }

    public SmiTextualConvention createTextualConvention(IdToken idToken, Token displayHint, StatusV2 status, Token description, Token reference, SmiType type) {
        SmiTextualConvention result = new SmiTextualConvention(idToken, module, getOptCStr(displayHint), status, getCStr(description), getOptCStr(reference));

        if (type.getBaseType() == null) {
            result.setBaseType(type);
        } else {
            result.setBaseType(type.getBaseType());
        }
        result.setEnumValues(type.getEnumValues());
        result.setBitFields(type.getBitFields());
        result.setRangeConstraints(type.getRangeConstraints());
        result.setSizeConstraints(type.getSizeConstraints());

        return result;
    }

    public SmiType createSequenceType(IdToken idToken) {
        return new SmiType(idToken, module);
    }

    public SmiType createType(IdToken idToken, SmiType baseType) {
        SmiType result;
        if (baseType == null) {
            throw new IllegalArgumentException();
        }
        if (idToken == null) {
            result = baseType;
        } else {
            result = new SmiType(idToken, module);
            result.setBaseType(baseType);
        }
        return result;
    }

    // TODO investigate idea: instead of using the hardcoded SmiConstants here, use SmiReferencedType for everything,
    // and resolve references to INTEGER, BITS, ... during the XRef phase
    public SmiType createIntegerType(IdToken idToken, IntKeywordToken intToken, Token applicationTagToken, List<SmiNamedNumber> namedNumbers, List<SmiRange> rangeConstraints) {
        if (idToken == null && intToken.getPrimitiveType() == INTEGER && namedNumbers == null && rangeConstraints == null) {
            return SmiConstants.INTEGER_TYPE;
        } else if (idToken != null || namedNumbers != null || rangeConstraints != null) {
            SmiType type = createPotentiallyTaggedType(idToken, applicationTagToken);
            if (intToken.getPrimitiveType() == INTEGER) {
                type.setBaseType(SmiConstants.INTEGER_TYPE);
            } else {
                type.setBaseType(new SmiReferencedType(intToken, module));
            }
            type.setEnumValues(namedNumbers);
            type.setRangeConstraints(rangeConstraints);
            return type;
        }
        return new SmiReferencedType(intToken, module);
    }

    private SmiType createPotentiallyTaggedType(IdToken idToken, Token applicationTagToken) {
        SmiType type;
        if (applicationTagToken != null) {
            int tag = Integer.parseInt(applicationTagToken.getText());
            type = new SmiType(idToken, module, tag);
        } else {
            type = new SmiType(idToken, module);
        }
        return type;
    }

    public SmiType createBitsType(IdToken idToken, List<SmiNamedNumber> namedNumbers) {
        module.incV2Features();
        if (idToken != null || namedNumbers != null) {
            SmiType type = new SmiType(idToken, module);
            type.setBaseType(SmiConstants.BITS_TYPE);
            type.setBitFields(namedNumbers);
            return type;
        }
        return SmiConstants.BITS_TYPE;
    }

    public SmiType createOctetStringType(IdToken idToken, Token applicationTagToken, List<SmiRange> sizeConstraints) {
        if (idToken != null || sizeConstraints != null) {
            SmiType type = createPotentiallyTaggedType(idToken, applicationTagToken);
            type.setBaseType(SmiConstants.OCTET_STRING_TYPE);
            type.setSizeConstraints(sizeConstraints);
            return type;
        }
        return SmiConstants.OCTET_STRING_TYPE;

    }

    public SmiType createDefinedType(IdToken idToken, Token moduleToken, Token referencedIdToken,
                                     List<SmiNamedNumber> namedNumbers,
                                     List<SmiRange> sizeConstraints,
                                     List<SmiRange> rangeConstraints) {
        SmiReferencedType referencedType = new SmiReferencedType(idt(referencedIdToken), module);
        if (moduleToken != null) {
            referencedType.setReferencedModuleToken(idt(moduleToken));
        }
        referencedType.setNamedNumbers(namedNumbers);
        referencedType.setSizeConstraints(sizeConstraints);
        referencedType.setRangeConstraints(rangeConstraints);

        SmiType result;
        if (idToken != null) {
            result = new SmiType(idToken, module);
            result.setBaseType(referencedType);
        } else {
            result = referencedType;
        }

        return result;
    }


    public SmiType createChoiceType(IdToken idToken) {
        return SmiProtocolType.createChoiceType(idToken, module);
    }

    public void addField(SmiType sequenceType, Token col, SmiType fieldType) {
        sequenceType.addField(idt(col), fieldType);
    }

    public SmiType createSequenceOfType(Token elementTypeNameToken) {
        SmiType sequenceOfType = new SmiType(null, module);
        sequenceOfType.setElementTypeToken(idt(elementTypeNameToken));
        return sequenceOfType;
    }

    public void addRange(List<SmiRange> rc, org.wintersleep.snmp.util.token.Token rv1, org.wintersleep.snmp.util.token.Token rv2) {
        SmiRange range;
        if (rv2 != null) {
            range = new SmiRange(rv1, rv2);
        } else {
            range = new SmiRange(rv1);
        }
        rc.add(range);
    }

    public BinaryStringToken bst(Token t) {
        return new BinaryStringToken(makeLocation(t), t.getText());
    }

    public HexStringToken hst(Token t) {
        return new HexStringToken(makeLocation(t), t.getText());
    }

    public QuotedStringToken dqst(Token t) {
        return new QuotedStringToken(makeLocation(t), t.getText(), '"');
    }

    public void addSymbol(SmiSymbol symbol) {
        if (symbol != null) {
            module.addSymbol(symbol);
        }
    }

    public StatusV2 findStatusV2(String text) {
        module.incV2Features();
        return StatusV2.find(text, true);
    }

    public ScopedId makeScopedId(Token moduleToken, Token symbolToken) {
        return new ScopedId(module, moduleToken != null ? idt(moduleToken) : null, idt(symbolToken));
    }

    public void setModuleIdentity(Token lastUpdated, Token organization, Token contactInfo, Token description, List<SmiModuleRevision> revisions) {
        module.setModuleIdentity(new SmiModuleIdentity(getOptCStr(lastUpdated), getOptCStr(organization), getOptCStr(contactInfo), getOptCStr(description), revisions));
    }

    public SmiModuleRevision createModuleRevision(Token revision, Token description) {
        return new SmiModuleRevision(getOptCStr(revision), getOptCStr(description));
    }
}

