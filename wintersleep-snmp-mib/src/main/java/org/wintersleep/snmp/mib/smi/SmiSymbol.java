/*
 * Copyright 2005 Davy Verstappen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.token.IdToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@ParametersAreNonnullByDefault
public abstract class SmiSymbol implements Serializable, Comparable {

    private IdToken idToken;
    private SmiModule module;
    private Map<Object, Object> userData;

    public SmiSymbol(@Nullable IdToken idToken, SmiModule module) {
        Preconditions.checkNotNull(module);
        this.idToken = idToken;
        this.module = module;
    }

    public SmiSymbol(SmiModule module) {
        Preconditions.checkNotNull(module);
        this.module = module;
    }

    @Nullable
    public String getId() {
        return idToken != null ? idToken.getId() : null;
    }

    @Nullable
    public IdToken getIdToken() {
        return idToken;
    }

    public abstract String getCodeId();

    public String getFullCodeId() {
        return module.getMib().getCodeNamingStrategy().getFullCodeId(this);
    }

    @Nonnull
    public SmiModule getModule() {
        return module;
    }

    @Nullable
    public Location getLocation() {
        return idToken != null ? idToken.getLocation() : null;
    }

    @Nonnull
    public String getUcId() {
        String id = getId();
        Preconditions.checkNotNull(id);
        return SmiUtil.ucFirst(id);
    }


    @Override
    public String toString() {
        return module.getId() + ": " + getId();
    }

    @Override
    public int hashCode() {
        if (idToken != null) {
            return idToken.getId().hashCode();
        }
        return super.hashCode();
    }

    /**
     * @param obj the object to compare
     * @return equality by SmiSymbol identifier and SmiModule
     */
    @Override
    public boolean equals(Object obj) {
        if (idToken != null) {
            if (obj instanceof SmiSymbol) {
                SmiSymbol other = (SmiSymbol) obj;
                return this.module.equals(other.module) && other.getId().equals(this.getId());
            }
        }
        return super.equals(obj);
    }

    public int compareTo(Object o) {
        return compareTo((SmiSymbol) o);
    }

    public int compareTo(SmiSymbol other) {
        int result = getModule().getId().compareTo(other.getModule().getId());
        if (result == 0) {
            result = getId().compareTo(other.getId());
        }
        return result;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        // do nothing
    }

    /**
     * @return A non-modifiable non-null Map of all the associated user data.
     */
    public Map<Object, Object> getUserData() {
        if (userData == null) {
            return Collections.emptyMap();
        }
        return userData;
    }

    public void addUserData(Object key, Object value) {
        if (userData == null) {
            userData = new HashMap<Object, Object>();
        }
        userData.put(key, value);
    }

    public Object findUserData(Object key) {
        if (userData == null) {
            return null;
        }
        return userData.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T findUserData(Class<T> key) {
        if (userData == null) {
            return null;
        }
        return (T) userData.get(key);
    }
}
