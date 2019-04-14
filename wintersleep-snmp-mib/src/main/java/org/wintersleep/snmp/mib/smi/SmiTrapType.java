/*
 * Copyright 2012 The OpenNMS Group, Inc.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmiTrapType extends SmiValue implements Notification {

    private IdToken enterpriseIdToken;
    private SmiOidValue enterpriseOid;
    private List<IdToken> variableTokens;
    private List<SmiVariable> variables = new ArrayList<SmiVariable>();
    private String description;
    private String reference;
    private IntegerToken specificTypeToken;
    
    public SmiTrapType(IdToken idToken, SmiModule module,
                       IdToken enterpriseIdToken, List<IdToken> variableTokens,
                       String description, String reference) {
        super(idToken, module);
        this.enterpriseIdToken = enterpriseIdToken;
        this.variableTokens = variableTokens;
        if (this.variableTokens == null) {
            this.variableTokens = Collections.emptyList();
        }
        this.description = description;
        this.reference = reference;
    }

    public String getCodeId() {
	    return getId();
	}

    public void resolveReferences(XRefProblemReporter reporter) {
    	enterpriseOid = getModule().resolveReference(enterpriseIdToken, SmiOidValue.class, reporter);
        for (IdToken variableToken : variableTokens) {
            SmiVariable variable = getModule().resolveReference(variableToken, SmiVariable.class, reporter);
            if (variable != null) {
                variables.add(variable);
            }
        }
    }

    public IdToken getEnterpriseIdToken() {
        return enterpriseIdToken;
    }

    public SmiOidValue getEnterpriseOid() {
        return enterpriseOid;
    }

    public List<IdToken> getVariableTokens() {
        return variableTokens;
    }

    public List<SmiVariable> getVariables() {
        return variables;
    }

    public List<SmiVariable> getObjects() {
        return variables;
    }

    public List<IdToken> getObjectTokens() {
        return variableTokens;
    }

    public String getDescription() {
        return description;
    }

    /**
     * This method is just here to implement Notification, for v1/v2 interoperability.
     *
     * Ideally we would return an SmiOidValue object here, but those would essentially always
     * cause clashes and "duplicate OID" errors. This is because traps essentially do not
     * define unique OIDs, and concatenating the trap enterprise OID with the trap specific type
     * integer, will essentially always clash with other OBJECT-TYPE definitions in the mib file.
     *
     * For instance:
     * <ul>
     *     <li>bgpVersion OBJECT-TYPE [snip] ::= { bgp 1 }</li>
     *     <li>bgpEstablished TRAP-TYPE ENTERPRISE bgp [snip] ::= 1</li>
     * </ul>
     *
     * @return The OID in decimal dotted notation.
     */

    public String getOidStr() {
        return getEnterpriseOid().getOidStr() + '.' + getSpecificType();
    }

    public String getReference() {
        return reference;
    }

    public IntegerToken getSpecificTypeToken() {
    	return specificTypeToken;
    }
    
    public void setSpecificTypeToken(IntegerToken specificTypeToken) {
    	this.specificTypeToken = specificTypeToken;
    }

    public int getSpecificType() {
        return specificTypeToken.getValue();
    }

}
