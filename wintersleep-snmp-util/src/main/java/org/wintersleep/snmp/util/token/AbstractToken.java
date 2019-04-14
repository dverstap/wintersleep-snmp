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
package org.wintersleep.snmp.util.token;

import org.wintersleep.snmp.util.location.Location;

public abstract class AbstractToken implements Token {

    private Location location;

    protected AbstractToken(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (getLocation() != null) {
            result.append(getLocation().toString());
        } else {
            result.append("<hardcoded>");
        }
        result.append(Location.SEPARATOR);
        result.append(getObject().toString());
        return result.toString();
    }
}
