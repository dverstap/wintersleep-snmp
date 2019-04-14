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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmiNotificationType extends SmiOidMacro implements Notification {

    private List<IdToken> objectTokens;
    private List<SmiVariable> objects = new ArrayList<SmiVariable>();
    private StatusV2 statusV2;
    private String description;
    private String reference;

    public SmiNotificationType(IdToken idToken, SmiModule module, List<IdToken> objectTokens, StatusV2 statusV2, String description, String reference) {
        super(idToken, module);
        this.objectTokens = objectTokens;
        if (this.objectTokens == null) {
            this.objectTokens = Collections.emptyList();
        }
        this.statusV2 = statusV2;
        this.description = description;
        this.reference = reference;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        for (IdToken objectToken : objectTokens) {
            SmiVariable variable = getModule().resolveReference(objectToken, SmiVariable.class, reporter);
            if (variable != null) {
                objects.add(variable);
            }
        }
    }

    public List<SmiVariable> getObjects() {
        return objects;
    }

    public List<IdToken> getObjectTokens() {
    	return objectTokens;
    }

    public StatusV2 getStatusV2() {
        return statusV2;
    }

    public String getDescription() {
        return description;
    }

    public String getReference() {
        return reference;
    }

}
