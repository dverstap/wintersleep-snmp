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


/**
 * This identifies each of the primitive types as defined in the SNMPv2-SMI module.
 * Each one of them is also mapped to the field that should be used to send/receive
 * values of this type.
 *
 * There is no distinction made here between types such as Counter and Counter32.
 * That distinction can be made by comparing the SmiType definitions: they will have
 * the same SmiPrimitiveType, but they are different SmiTypes (they are also defined
 * in different modules, so that makes sense too).
 * 
 * @author davy
 *
 */
public enum SmiPrimitiveType {

	// Basic ASN.1 type:
	ENUM(SmiVarBindField.INTEGER_VALUE, "", true, true),     // TODO this probably needs to be removed
	
	// From the SimpleSyntax choice:
	INTEGER(SmiVarBindField.INTEGER_VALUE, "Int", true, true),
	OCTET_STRING(SmiVarBindField.STRING_VALUE, "Octs", false, true),
	OBJECT_IDENTIFIER(SmiVarBindField.OBJECTID_VALUE, "Oid", false, true),
	
	// Defined ASN.1 type:
	INTEGER_32(SmiVarBindField.INTEGER_VALUE, "Integer32", false, true),
	
	// From the ApplicationSyntax choice:
	IP_ADDRESS(SmiVarBindField.IPADDRESS_VALUE, "IpAddress", false, true),
	COUNTER_32(SmiVarBindField.COUNTER_VALUE, "Counter32", false, true),
	GAUGE_32(SmiVarBindField.UNSIGNED_INTEGER_VALUE, "Gauge32", false, true),
	UNSIGNED_32(SmiVarBindField.UNSIGNED_INTEGER_VALUE, "Unsigned32", false, true),
	TIME_TICKS(SmiVarBindField.TIMETICKS_VALUE, "TimeTicks", false, true),
	OPAQUE(SmiVarBindField.ARBITRARY_VALUE, "Opaque", false, true),
	COUNTER_64(SmiVarBindField.BIG_COUNTER_VALUE, "Counter64", false, true),
	
	// From the TextualConvention macro:
	BITS(SmiVarBindField.STRING_VALUE, "Bits", false, true)  // TODO this probably needs to be removed
	;
	
	//private static Map<String, SmiPrimitiveType> xmlValueMap_;
	private SmiVarBindField varBindField;
	private String xmlValue;

    // TODO use these for errors reporting
    private boolean namedNumbersSupported;
    private boolean rangesSupported;


    private SmiPrimitiveType(SmiVarBindField varBindField, String xmlValue, boolean allowsNamedNumbers, boolean allowsRanges)
	{
//		if (xmlValueMap_ == null) {
//			xmlValueMap_ = new HashMap<String, SmiPrimitiveType>();
//		}
		this.varBindField = varBindField;
		this.xmlValue = xmlValue;
		//xmlValueMap_.put(xmlValue_, this);

        namedNumbersSupported = allowsNamedNumbers;
        rangesSupported = allowsRanges;
    }
	
	public SmiVarBindField getVarBindField() {
		return varBindField;
	}
	
	public SmiPrimitiveType fromXmlValue(String xmlValue)
	{
		for (SmiPrimitiveType pt : values()) {
			if (pt.xmlValue.equals(xmlValue)) {
				return pt;
			}
		}
		return null;
	}


    public String getXmlValue() {
        return xmlValue;
    }

    public boolean isNamedNumbersSupported() {
        return namedNumbersSupported;
    }

    public boolean isRangesSupported() {
        return rangesSupported;
    }
}
