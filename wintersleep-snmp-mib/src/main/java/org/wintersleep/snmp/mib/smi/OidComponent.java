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

import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.util.token.IntegerToken;
import org.wintersleep.snmp.util.token.Token;

public class OidComponent {

    private final OidComponent parent;
    private OidComponent child;
    private final IdToken idToken;
    private final IntegerToken valueToken;
    private SmiOidNode node;
    private boolean isResolved = false;

    public OidComponent(OidComponent parent, IdToken idToken, IntegerToken intToken) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.child = this;
        }
        this.idToken = idToken;
        valueToken = intToken;
    }

    public IdToken getIdToken() {
        return idToken;
    }

    public IntegerToken getValueToken() {
        return valueToken;
    }

    public SmiOidNode getNode() {
        return node;
    }

    private Token getToken() {
        if (idToken != null) {
            return idToken;
        }
        return valueToken;
    }

    private boolean isFirst() {
        return parent == null;
    }

    private boolean isLast() {
        return child == null;
    }

    public SmiOidNode resolveNode(SmiModule module, XRefProblemReporter reporter) {
        assert (node == null);
        if (!isResolved) {
            SmiOidNode parent = null;
            if (this.parent != null) {
                parent = this.parent.resolveNode(module, reporter);
                if (parent == null) {
                    reporter.reportCannotFindParent(this.parent.getToken());
                }
            }
            node = doResolve(module, parent, reporter);
            if (node == null) {
                if (isLast()) {
                    if (parent != null) {
                        if (valueToken != null) {
                            node = new SmiOidNode(parent, valueToken.getValue());
                        } else {
                            reporter.reportValueTokenMissingForLastSubid(getToken());
                        }
                    } else {
                        reporter.reporParentMissingForLastSubid(getToken());
                    }
                } else {
                    reporter.reportCannotResolveNonLastSubid(getToken());
                }
            }
            isResolved = true;
        }
        return node;
    }

    private SmiOidNode doResolve(SmiModule module, SmiOidNode parent, XRefProblemReporter reporter) {
        SmiOidNode node;
        if (idToken != null && !isLast()) { // isLast check deals with jobmonMIB situation
            SmiSymbol symbol = module.resolveReference(idToken, null);
            if (symbol != null) {
                if (symbol instanceof SmiOidValue) {
                    SmiOidValue oidValue = (SmiOidValue) symbol;
                    node = oidValue.resolveOid(reporter);
                    if (node != null && valueToken != null) {
                        // TODO compare
                    }
                } else {
                    reporter.reportFoundSymbolButWrongType(idToken, SmiOidValue.class, symbol.getClass());
                    node = null;
                }
            } else if (parent != null && valueToken != null) {
                int value = valueToken.getValue();
                node = parent.childMap.get(value);
                if (node == null) {
                    node = new SmiOidNode(parent, value);
                }
            } else {
                node = null;
            }
        } else {
            if (isFirst()) {
                node = module.getMib().getRootNode().findChild(valueToken.getValue());
                if (node == null) {
                    node = new SmiOidNode(module.getMib().getRootNode(), valueToken.getValue());
                    //throw new IllegalStateException(valueToken.toString());
                }
            } else if (parent != null) {
                node = parent.findChild(valueToken.getValue());
                if (node == null) {
                    node = new SmiOidNode(parent, valueToken.getValue());
                }
            } else {
                //throw new IllegalStateException("Parent is null for: " + valueToken.toString());
                return null;
            }
        }
        return node;
    }

}
