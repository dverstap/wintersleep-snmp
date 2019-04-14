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

package org.wintersleep.snmp.mib.smi;

import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;

public class SmiField {

    private SmiType parentType;
    private IdToken columnIdToken;
    private SmiVariable column;
    private SmiType type;

    public SmiField(SmiType parentType, IdToken columnIdToken, SmiType type) {
        this.parentType = parentType;
        this.columnIdToken = columnIdToken;
        this.type = type;
    }

    public SmiType getParentType() {
        return parentType;
    }

    public IdToken getColumnIdToken() {
        return columnIdToken;
    }

    public SmiVariable getColumn() {
        return column;
    }

    public SmiType getType() {
        return type;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        column = parentType.getModule().resolveReference(columnIdToken, SmiVariable.class, reporter);
    }

    // TODO resolve type?
}
