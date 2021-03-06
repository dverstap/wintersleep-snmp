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

import com.google.common.base.Preconditions;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.token.IdToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SmiOidValue extends SmiValue {

    private OidComponent lastOidComponent;

    private SmiOidNode node;

    public SmiOidValue(IdToken idToken, SmiModule module) {
        super(idToken, module);
    }

    public SmiOidValue(IdToken idToken, SmiModule internalModule, SmiOidNode node) {
        super(idToken, internalModule);
        Preconditions.checkNotNull(idToken);
        this.node = node;
    }

    public OidComponent getLastOidComponent() {
        return lastOidComponent;
    }

    public void setLastOidComponent(OidComponent lastOidComponent) {
        this.lastOidComponent = lastOidComponent;
    }

    public int[] getOid() {
        return node.getOid();
    }

    /**
     * @return null for the root node; the OID in decimal dotted notation for all other nodes
     */
    public String getOidStr() {
        if (node == null) {
            throw new NullPointerException("OidNode was not resolved for " + getIdToken());
        }
        return node.getOidStr();
    }

    public SmiOidNode resolveOid(XRefProblemReporter reporter) {
        if (node == null) {
            node = lastOidComponent.resolveNode(getModule(), reporter);
            if (node != null) {
                node.getValues().add(this);
            }
            // assumption is that another error has already been reported for this
        }
        return node;
    }

    public SmiOidNode getNode() {
        return node;
    }

    public String getCodeId() {
        return getModule().getMib().getCodeNamingStrategy().getOidValueId(this);
    }

}