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

/**
 * Indexes belong to a row and refer to a column.
 * Note that it is possible that the column belongs to another table!
 */
public class SmiIndex {

    private final ScopedId scopedId;
    private final SmiRow row;
    private final boolean implied;
	
	public SmiIndex(SmiRow row, ScopedId scopedId, boolean implied) {
        this.row = row;
        this.scopedId = scopedId;
        this.implied = implied;
	}

	public boolean isImplied() {
		return implied;
	}

	public SmiVariable getColumn() {
		return (SmiVariable) scopedId.getSymbol();
	}

	public SmiRow getRow() {
		return row;
	}

    public boolean isColumnFromOtherTable() {
        return row.getTable() != getColumn().getTable();
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        scopedId.resolveReferences(reporter);
    }

}
