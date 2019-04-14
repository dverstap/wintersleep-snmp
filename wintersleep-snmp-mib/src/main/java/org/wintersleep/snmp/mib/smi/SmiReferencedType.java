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
package org.wintersleep.snmp.mib.smi;

import com.google.common.base.Preconditions;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.token.IdToken;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class is only used during parsing, to temporarily reference types that are defined
 * somewhere else, or that have not yet been defined. If the mib is correct, there will
 * never be instances of this class in the final SmiMib data structure.
 * <p>
 * TODO move class out of public API
 */
public class SmiReferencedType extends SmiType {

    private IdToken referencedModuleToken;
    private List<SmiNamedNumber> namedNumbers; // don't know yet whether this is enums or bitfields

    public SmiReferencedType(IdToken idToken, SmiModule module) {
        super(idToken, module);
        Preconditions.checkNotNull(idToken);

        // note that the idToken in this is case is the token where the reference
        // is made, not the token where the Type is defined.
    }

    @Nonnull
    @Override
    public String getId() {
        String id = super.getId();
        Preconditions.checkNotNull(id);
        return id;
    }

    @Nonnull
    @Override
    public IdToken getIdToken() {
        IdToken idToken = super.getIdToken();
        Preconditions.checkNotNull(idToken);
        return idToken;
    }

    public IdToken getReferencedModuleToken() {
        return referencedModuleToken;
    }

    public void setReferencedModuleToken(IdToken referencedModuleToken) {
        this.referencedModuleToken = referencedModuleToken;
    }

    public List<SmiNamedNumber> getNamedNumbers() {
        return namedNumbers;
    }

    public void setNamedNumbers(List<SmiNamedNumber> namedNumbers) {
        this.namedNumbers = namedNumbers;
    }

    @Override
    public SmiType resolveThis(XRefProblemReporter reporter, SmiType parentType) {
        SmiType result = this;

        SmiType type = getModule().resolveReference(getIdToken(), SmiType.class, reporter);
        if (type != null) {
            // TODO check compatibility
            // TODO verify
            if (getEnumValues() != null || getBitFields() != null || getRangeConstraints() != null || getSizeConstraints() != null) {
                if (parentType != null) {
                    parentType.setEnumValues(getEnumValues()); // TODO don't know this yet?
                    parentType.setBitFields(getBitFields());
                    parentType.setRangeConstraints(getRangeConstraints());
                    parentType.setSizeConstraints(getSizeConstraints());
                    result = type;
                } else {
                    result = new SmiType(null, getModule());
                    result.setEnumValues(getEnumValues()); // TODO don't know this yet?
                    result.setBitFields(getBitFields());
                    result.setRangeConstraints(getRangeConstraints());
                    result.setSizeConstraints(getSizeConstraints());
                    result.setBaseType(type);
                }
            } else {
                result = type;
            }
        } /*else {
            reporter.reportCannotFindSymbol(getIdToken());
        }*/
        return result;
    }

    @Override
    public String toString() {
        return "WARNING REFERENCED TYPE: " + super.toString();
    }

}

