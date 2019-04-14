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
import org.wintersleep.snmp.util.token.QuotedStringToken;

import java.util.List;

/**
 * Besides the OBJECT-TYPE fields that are specific to SNMP variable definitions,
 * this class also contains some methods that make it easier to deal with the recursive nature
 * of the SmiType definitions.
 */
public class SmiVariable extends SmiObjectType {

    private final QuotedStringToken unitsToken;
    private final SmiDefaultValue defaultValue;

    public SmiVariable(IdToken idToken, SmiModule module, SmiType type, QuotedStringToken unitsToken, SmiDefaultValue defaultValue) {
        super(idToken, module);
        setType(type);
        this.unitsToken = unitsToken;
        this.defaultValue = defaultValue;
        if (this.defaultValue != null) {
            this.defaultValue.variable = this;
        }
    }

    public String getCodeConstantId() {
        return getModule().getMib().getCodeNamingStrategy().getCodeConstantId(this);
    }

    public String getFullCodeConstantId() {
        return getModule().getMib().getCodeNamingStrategy().getFullCodeConstantId(this);
    }

    public String getCodeOid() {
        return getOidStr();
    }

    public String getRequestMethodId() {
        return getModule().getMib().getCodeNamingStrategy().getRequestMethodId(this);
    }

    public String getGetterMethodId() {
        return getModule().getMib().getCodeNamingStrategy().getGetterMethodId(this);
    }

    public String getSetterMethodId() {
        return getModule().getMib().getCodeNamingStrategy().getSetterMethodId(this);
    }

    public SmiRow getRow() {
        if (getNode() != null && getNode().getParent() != null) {
            SmiOidValue oidValue = getNode().getParent().getSingleValue(SmiOidValue.class, getModule());
            if (oidValue instanceof SmiRow) {
                return (SmiRow) oidValue;
            }
        }
        return null;
    }

    public SmiTable getTable() {
        SmiRow row = getRow();
        if (row != null) {
            return row.getTable();
        }
        return null;
    }

    public boolean isColumn() {
        return getRow() != null;
    }

    public boolean isScalar() {
        return getRow() == null;
    }

    public String getUnits() {
        return unitsToken != null ? unitsToken.getValue() : null;
    }

    public QuotedStringToken getUnitsToken() {
        return unitsToken;
    }

    public SmiTextualConvention getTextualConvention() {
        SmiType type = this.type;
        while (type != null) {
            if (type instanceof SmiTextualConvention) {
                return (SmiTextualConvention) type;
            }
            type = type.getBaseType();
        }
        return null;
    }

    public SmiPrimitiveType getPrimitiveType() {
        return type.getPrimitiveType();
    }

    public SmiType getEnumType() {
        SmiType type = this.type;
        while (type != null) {
            if (type.getEnumValues() != null) {
                return type;
            }
            type = type.getBaseType();
        }
        return null;
    }

    public List<SmiNamedNumber> getEnumValues() {
        SmiType type = getEnumType();
        if (type != null) {
            return type.getEnumValues();
        }
        return null;
    }

    public SmiType getBitFieldType() {
        SmiType type = this.type;
        while (type != null) {
            if (type.getBitFields() != null) {
                return type;
            }
            type = type.getBaseType();
        }
        return null;

    }

    public List<SmiNamedNumber> getBitFields() {
        SmiType type = getBitFieldType();
        if (type != null) {
            return type.getBitFields();
        }
        return null;
    }

    public SmiType getRangeConstraintType() {
        SmiType type = this.type;
        while (type != null) {
            if (type.getRangeConstraints() != null) {
                return type;
            }
            type = type.getBaseType();
        }
        return null;
    }

    public List<SmiRange> getRangeConstraints() {
        SmiType type = getRangeConstraintType();
        if (type != null) {
            return type.getRangeConstraints();
        }
        return null;
    }

    public SmiType getSizeConstraintType() {
        SmiType type = this.type;
        while (type != null) {
            if (type.getSizeConstraints() != null) {
                return type;
            }
            type = type.getBaseType();
        }
        return null;
    }

    public SmiType getSizeConstraints() {
        SmiType type = getSizeConstraintType();
        if (type != null) {
            return type;
        }
        return null;
    }

    public SmiDefaultValue getDefaultValue() {
        return defaultValue;
    }

    public SmiNamedNumber resolveBitField(IdToken idToken, XRefProblemReporter reporter) {
        for (SmiNamedNumber nn : getBitFields()) {
            if (nn.getId().equals(idToken.getId())) {
                return nn;
            }
        }
        reporter.reportCannotFindBitField(idToken);
        return null;
    }

    public SmiNamedNumber resolveEnumConstant(IdToken idToken, XRefProblemReporter reporter) {
        for (SmiNamedNumber nn : getEnumValues()) {
            if (nn.getId().equals(idToken.getId())) {
                return nn;
            }
        }
        reporter.reportCannotFindEnumConstant(idToken);
        return null;
    }
}
