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

import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.token.BigIntegerToken;
import org.wintersleep.snmp.util.token.IdToken;

import java.math.BigInteger;

/**
 * Used to represent enum values and bit fields.
 */
public class SmiNamedNumber {

	private SmiType type;
	private IdToken idToken;
	private BigIntegerToken valueToken;

    public SmiNamedNumber(IdToken id, BigIntegerToken value) {
		super();
//		type = type;
		idToken = id;
		valueToken = value;
	}

    public Location getLocation() {
        return idToken.getLocation();
    }

	public String getId() {
		return idToken.getId();
	}

    public BigInteger getValue() {
		return valueToken.getValue();
	}

    public String getCodeId() {
		return type.getModule().getMib().getCodeNamingStrategy().getEnumValueId(this);
	}

    public SmiType getType() {
        return type;
    }

    public void setType(SmiType type) {
        this.type = type;
    }

    public IdToken getIdToken() {
        return idToken;
    }

    public BigIntegerToken getValueToken() {
        return valueToken;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (idToken != null) {
            sb.append(idToken.getId());
        }
        if (valueToken != null) {
            sb.append('(').append(valueToken.getValue()).append(')');
        }
        return sb.toString();
    }
}
