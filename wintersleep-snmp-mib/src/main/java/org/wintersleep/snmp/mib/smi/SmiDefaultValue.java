package org.wintersleep.snmp.mib.smi;

import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.token.BigIntegerToken;
import org.wintersleep.snmp.util.token.BinaryStringToken;
import org.wintersleep.snmp.util.token.HexStringToken;
import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.util.token.QuotedStringToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
* Copyright 2007 Davy Verstappen.
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
public class SmiDefaultValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmiDefaultValue.class);

    private final SmiModule module;
    private final BigIntegerToken bigIntegerToken;
    private final List<IdToken> bitsIdTokenList;
    private final OidComponent lastOidComponent;
    private final BinaryStringToken binaryStringToken;
    private final HexStringToken hexStringToken;
    private final QuotedStringToken quotedStringToken;
    private final ScopedId scopedId;
    private final boolean isNullValue;

    SmiVariable variable;

    private ArrayList<SmiNamedNumber> bitsValue;
    private SmiNamedNumber enumValue;
    private SmiOidValue oidValue;
    private SmiOidNode oidNode;
    private SmiSymbol symbolValue;

    public SmiDefaultValue(SmiModule module, BigIntegerToken bigIntegerToken, List<IdToken> bitsIdTokenList, OidComponent lastOidComponent, BinaryStringToken binaryStringToken, HexStringToken hexStringToken, QuotedStringToken quotedStringToken, ScopedId scopedId, boolean isNullValue) {
        this.module = module;
        this.bigIntegerToken = bigIntegerToken;
        this.bitsIdTokenList = bitsIdTokenList;
        this.lastOidComponent = lastOidComponent;
        this.binaryStringToken = binaryStringToken;
        this.hexStringToken = hexStringToken;
        this.quotedStringToken = quotedStringToken;
        this.scopedId = scopedId;
        this.isNullValue = isNullValue;
    }

    public SmiVariable getVariable() {
        return variable;
    }

    public BigInteger getIntegerValue() {
        if (bigIntegerToken != null) {
            return bigIntegerToken.getValue();
        }
        return null;
    }

    public List<SmiNamedNumber> getBitsValue() {
        return bitsValue;
    }

    public SmiNamedNumber getEnumValue() {
        return enumValue;
    }

    public SmiOidValue getOidValue() {
        return oidValue;
    }

    public SmiOidNode getOidNode() {
        if (oidNode != null) {
            return oidNode;
        } else if (oidValue != null) {
            return oidValue.getNode();
        }
        return null;
    }

    public String getCStringValue() {
        if (quotedStringToken != null) {
            return quotedStringToken.getValue();
        }
        return null;
    }

    public String getBinaryStringValue() {
        if (binaryStringToken != null) {
            return binaryStringToken.getValue();
        }
        return null;
    }

    public String getHexStringValue() {
        if (hexStringToken != null) {
            return hexStringToken.getValue();
        }
        return null;
    }

    public SmiSymbol getSymbolValue() {
        return symbolValue;
    }

    public BigIntegerToken getBigIntegerToken() {
        return bigIntegerToken;
    }

    public List<IdToken> getBitsIdTokenList() {
        return bitsIdTokenList;
    }

    public OidComponent getLastOidComponents() {
        return lastOidComponent;
    }

    public BinaryStringToken getBinaryStringToken() {
        return binaryStringToken;
    }

    public HexStringToken getHexStringToken() {
        return hexStringToken;
    }

    public QuotedStringToken getQuotedStringToken() {
        return quotedStringToken;
    }

    public ScopedId getScopedId() {
        return scopedId;
    }

    public boolean isNullValue() {
        return isNullValue;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        if (bitsIdTokenList != null) {
            resolveBits(reporter);
        } else if (lastOidComponent != null) {
            oidNode = resolveOids(reporter);
        } else if (scopedId != null) {
            if (scopedId.getModuleToken() != null) {
                LOGGER.debug("Not yet implemented: " + scopedId.getModuleToken());
            } else {
                if (variable.getEnumValues() != null) {
                    enumValue = variable.resolveEnumConstant(scopedId.getSymbolToken(), reporter);
                } else {
                    SmiSymbol symbol = variable.getModule().resolveReference(scopedId.getSymbolToken(), reporter);
                    if (symbol != null) {
                        if (symbol instanceof SmiOidValue && variable.getPrimitiveType() == SmiPrimitiveType.OBJECT_IDENTIFIER) {
                            oidValue = (SmiOidValue) symbol;
                            oidNode = oidValue.getNode();
                        } else {
                            // some proprietary mibs define the default value for an integer as a reference
                            // to some other integer variable; 
                            symbolValue = symbol;
                            reporter.reportInvalidDefaultValue(scopedId.getSymbolToken());
                        }
                    }
                }
            }
        }
    }

    private SmiOidNode resolveOids(XRefProblemReporter reporter) {
        // TODO
        // reporter.reportOidDefaultValueMustBeSingleIdentifier(t);
        return lastOidComponent.resolveNode(module, reporter);
    }

    private void resolveBits(XRefProblemReporter reporter) {
        if (variable.getBitFields() != null) {
            bitsValue = new ArrayList<SmiNamedNumber>();
            for (IdToken idToken : bitsIdTokenList) {
                SmiNamedNumber nn = variable.resolveBitField(idToken, reporter);
                bitsValue.add(nn);
            }
        } else {
            reporter.reportBitsValueWithoutBitsType(bitsIdTokenList.get(0).getLocation());
        }
    }
}
