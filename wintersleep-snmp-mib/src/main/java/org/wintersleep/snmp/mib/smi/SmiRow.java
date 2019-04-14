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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmiRow extends SmiObjectType {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmiRow.class);

    // TODO remove?
    private List<SmiRow> parentRows = new ArrayList<SmiRow>();
    private List<SmiRow> childRows = new ArrayList<SmiRow>();

    private List<SmiIndex> indexes;
    private ScopedId augmentsId;

    public SmiRow(IdToken idToken, SmiModule module) {
        super(idToken, module);
    }

    public SmiTable getTable() {
        return getNode().getParent().getSingleValue(SmiTable.class, getModule());
    }

    public List<SmiVariable> getColumns() {
        List<SmiVariable> result = new ArrayList<SmiVariable>();
        for (SmiOidNode child : getNode().getChildren()) {
            SmiVariable column = child.getSingleValue(SmiVariable.class, getModule());
            if (column == null) {
                // This can happen when a new mib adds new columns to a table:
                // for example: kdiff3 RFC1269-MIB BGP4-MIB
                LOGGER.debug("{}: Could not find SmiVariable for {}", getIdToken(), child);
            } else {
                result.add(column);
            }
        }
        return result;
    }

    public SmiRow getAugments() {
        if (augmentsId != null) {
            // TODO type safety check when resolving
            return (SmiRow) augmentsId.getSymbol();
        } else {
            return null;
        }
    }

    public void setAugmentsId(ScopedId augmentsId) {
        this.augmentsId = augmentsId;
    }

    public List<SmiIndex> getIndexes() {
        return indexes;
    }

    public List<SmiRow> getChildRows() {
        return childRows;
    }

    public List<SmiRow> getParentRows() {
        return parentRows;
    }

    public SmiVariable findColumn(String id) {
        for (SmiVariable c : getColumns()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public SmiIndex addIndex(ScopedId scopedId, boolean isImplied) {
        if (indexes == null) {
            indexes = new ArrayList<SmiIndex>();
        }
        SmiIndex index = new SmiIndex(this, scopedId, isImplied);
        indexes.add(index);
        return index;
    }

    public boolean hasSameIndexes(SmiRow other) {
        boolean result = false;
        if (indexes.size() == other.indexes.size()) {
            boolean tmpResult = true;
            Iterator<SmiIndex> i = indexes.iterator();
            Iterator<SmiIndex> j = other.getIndexes().iterator();
            while (tmpResult && i.hasNext() && j.hasNext()) {
                SmiIndex i1 = i.next();
                SmiIndex i2 = j.next();
                if (i1.getColumn() != i2.getColumn()) {
                    tmpResult = false;
                }
                if (i1.isImplied() != i2.isImplied()) {
                    System.out.printf("implied index is not the same for %s and %s", getId(), other.getId())
                            .println();
                    tmpResult = false;
                }
            }
            result = tmpResult;
        }
        //System.out.printf("IndexCheck for %s and %s : %b%n", getId(), other.getId(), result);
        return result;
    }

    public void addParentRow(SmiRow row) {
        parentRows.add(row);
        row.childRows.add(this);
    }


    @Override
    public void resolveReferences(XRefProblemReporter reporter) {
        super.resolveReferences(reporter);
        if (indexes != null) {
            for (SmiIndex index : indexes) {
                index.resolveReferences(reporter);
            }
        } else {
            augmentsId.resolveReferences(reporter);
            SmiRow augmentedRow = getAugments();
            if (augmentedRow != null) {
                augmentedRow.childRows.add(this);
                parentRows.add(augmentedRow);
            }
        }
    }
}
