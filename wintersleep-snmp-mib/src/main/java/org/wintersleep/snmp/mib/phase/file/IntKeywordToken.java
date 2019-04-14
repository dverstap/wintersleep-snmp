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
package org.wintersleep.snmp.mib.phase.file;

import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.mib.smi.SmiPrimitiveType;

public class IntKeywordToken extends IdToken {

    private SmiPrimitiveType primitiveType;

    public IntKeywordToken(Location location, String value, SmiPrimitiveType primitiveType) {
        super(location, value);

        if (primitiveType == null) {
            throw new IllegalArgumentException("Primitive type is mandatory.");
        }

        this.primitiveType = primitiveType;
    }

    public SmiPrimitiveType getPrimitiveType() {
        return primitiveType;
    }
}
