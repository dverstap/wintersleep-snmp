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

public class SmiOidMacro extends SmiOidValue {

    protected StatusAll status;

    public SmiOidMacro(IdToken idToken, SmiModule module) {
        super(idToken, module);
    }

    public StatusAll getStatus() {
        return status;
    }

    public StatusV1 getStatusV1() {
        return status.getStatusV1();
    }

    public StatusV2 getStatusV2() {
        return status.getStatusV2();
    }

}