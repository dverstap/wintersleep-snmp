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

import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.token.BigIntegerToken;
import org.wintersleep.snmp.util.token.BinaryStringToken;
import org.wintersleep.snmp.util.token.HexStringToken;
import org.wintersleep.snmp.util.token.Token;

import java.math.BigInteger;

public class SmiRange {

    private Token beginToken;
    private Token endToken;

    public SmiRange(Token beginToken, Token endToken) {
        this.beginToken = beginToken;
        this.endToken = endToken;
    }

    public SmiRange(Token singleToken) {
        beginToken = singleToken;
        endToken = singleToken;
    }

    public Token getBeginToken() {
        return beginToken;
    }

    public Token getEndToken() {
        return endToken;
    }

    public boolean isSingle() {
        return beginToken == endToken;
    }

    public Location getLocation() {
        return beginToken.getLocation();
    }

    public BigInteger getMinValue() {
        return getValue(beginToken);
    }

    public BigInteger getMaxValue() {
        return getValue(endToken);
    }

    private static BigInteger getValue(Token token) {
        if (token instanceof BigIntegerToken) {
            return ((BigIntegerToken) token).getValue();
        } else if (token instanceof HexStringToken) {
            return ((HexStringToken) token).getIntegerValue();
        } else if (token instanceof BinaryStringToken) {
            return ((BinaryStringToken) token).getIntegerValue();
        }
        return null;
    }


    public String toString() {
        if (beginToken == endToken) {
            return beginToken.getObject().toString();
        } else {
            StringBuilder result = new StringBuilder("(");
            result.append(beginToken.getObject());
            result.append("..");
            result.append(endToken);
            result.append(")");
            return result.toString();
        }
    }
}
