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
import org.wintersleep.snmp.util.token.IdToken;

import javax.annotation.Nonnull;

public class SmiMacro extends SmiSymbol {

    public SmiMacro(IdToken idToken, SmiModule module) {
        super(idToken, module);
        Preconditions.checkNotNull(idToken);
    }

    @Override
    public String getCodeId() {
        return null;
    }

    @Nonnull
    @Override
    public String getId() {
        String id = super.getId();
        Preconditions.checkNotNull(id);
        return id;
    }

    @Nonnull
    @Override
    public IdToken getIdToken() {
        IdToken idToken = super.getIdToken();
        Preconditions.checkNotNull(idToken);
        return idToken;
    }

}
