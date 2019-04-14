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

public enum MacroType {

    OBJECT_TYPE_V1(SmiVersion.V1, "ACCESS"),
    OBJECT_TYPE_V2(SmiVersion.V2, "MAX-ACCESS"),
    OBJECT_IDENTITY(SmiVersion.V2, null),
    NOTIFICATION_TYPE(SmiVersion.V2, null),
    TEXTUAL_CONVENTION(SmiVersion.V2, null),
    OBJECT_GROUP(SmiVersion.V2, null),
    NOTIFICATION_GROUP(SmiVersion.V2, null),
    MODULE_COMPLIANCE(SmiVersion.V2, "MIN-ACCESS"),
    AGENT_CAPABILITIES(SmiVersion.V2, "ACCESS"),
    TRAP_TYPE(SmiVersion.V1, null);

    private final SmiVersion version;
    private final String accessFieldName;

    MacroType(SmiVersion version, String accessFieldName) {
        this.version = version;
        this.accessFieldName = accessFieldName;
    }

    public SmiVersion getVersion() {
        return version;
    }

    public String getAccessFieldName() {
        return accessFieldName;
    }
}
