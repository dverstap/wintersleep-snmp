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

import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;
import org.wintersleep.snmp.util.token.IdToken;

public class SmiObjectType extends SmiOidMacro {

    protected SmiType type;
    private IdToken accessToken;
    private IdToken maxAccessToken;
    private String description;

    private ObjectTypeAccessV1 accessV1;
    private ObjectTypeAccessV2 accessV2;
    private AccessAll accessAll;

    public SmiObjectType(IdToken idToken, SmiModule module) {
        super(idToken, module);
    }

    public SmiType getType() {
        return type;
    }

    public void setType(SmiType type) {
        this.type = type;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        type = type.resolveThis(reporter, null);

        if (accessToken != null) {
            accessV1 = ObjectTypeAccessV1.find(accessToken.getId(), false);
            if (accessV1 != null) {
                accessAll = accessV1.getAccessAll();
            } else {
                reporter.reportInvalidAccess(accessToken);
                accessAll = AccessAll.find(accessToken.getId(), false);
            }
        } else {
            accessV2 = ObjectTypeAccessV2.find(maxAccessToken.getId(), false);
            if (accessV2 != null) {
                accessAll = accessV2.getAccessAll();
            } else {
                reporter.reportInvalidMaxAccess(maxAccessToken);
                accessAll = AccessAll.find(maxAccessToken.getId(), false);
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(StatusAll status) {
        this.status = status;
    }

    public IdToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(IdToken accessToken) {
        this.accessToken = accessToken;
    }

    public IdToken getMaxAccessToken() {
        return maxAccessToken;
    }

    public void setMaxAccessToken(IdToken maxAccessToken) {
        this.maxAccessToken = maxAccessToken;
    }

    public ObjectTypeAccessV1 getAccessV1() {
        return accessV1;
    }

    public ObjectTypeAccessV2 getAccessV2() {
        return accessV2;
    }

    public ObjectTypeAccessV2 getMaxAccess() {
        return accessV2;
    }

    public AccessAll getAccessAll() {
        return accessAll;
    }

}
