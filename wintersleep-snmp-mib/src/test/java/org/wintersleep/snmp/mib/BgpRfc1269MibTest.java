/*
 * Copyright 2012 The OpenNMS Group, Inc..
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
package org.wintersleep.snmp.mib;

import org.wintersleep.snmp.mib.smi.SmiModule;
import org.wintersleep.snmp.mib.smi.SmiTrapType;
import org.wintersleep.snmp.mib.smi.SmiVariable;
import org.wintersleep.snmp.mib.smi.SmiVersion;
import org.wintersleep.snmp.util.url.DefaultURLListBuilder;

import java.io.File;

public class BgpRfc1269MibTest extends AbstractMibTestCase {

    public BgpRfc1269MibTest() {
        super(SmiVersion.V1);
    }

    @Override
    protected void addUrls(DefaultURLListBuilder builder) {
        super.addUrls(builder);
        builder.addDir(LIBSMI_IETF_DIR,
                "RFC-1212",
                "RFC1213-MIB",
                "RFC-1215",
                "RFC1269-MIB");
    }

    public void testSizes() {
        assertNotNull("MIB cannot be null", getMib());
        assertNotNull(getMib().getScalars());
        assertEquals(108, getMib().getScalars().size());

        assertNotNull(getMib().getColumns());
        assertEquals(89, getMib().getColumns().size());

        // { bgpEstablished, bgpBackwardTransition } from RFC1269-MIB 
        assertEquals(2, getMib().getTrapTypes().size());

        SmiModule bgpMib = getMib().findModule("RFC1269-MIB");
        assertNotNull(bgpMib);

        // { bgpEstablished, bgpBackwardTransition } from RFC1269-MIB
        assertEquals(2, bgpMib.getTrapTypes().size());

        assertEquals(3, bgpMib.getScalars().size());
        assertEquals(20, bgpMib.getColumns().size());

        assertNotNull(bgpMib.findScalar("bgpLocalAs"));
        assertNull(bgpMib.findColumn("bgpLocalAs"));

        assertNotNull(bgpMib.findColumn("bgpPeerIdentifier"));
        assertNull(bgpMib.findScalar("bgpPeerIdentifier"));

        assertNotNull(bgpMib.findTable("bgpPeerTable"));
        assertNull(bgpMib.findVariable("bgpPeerTable"));

        assertNotNull(bgpMib.findRow("bgpPeerEntry"));
        assertNull(bgpMib.findVariable("bgpPeerEntry"));
    }

    public void testTrapTypes() {
        SmiModule bgpMib = getMib().findModule("RFC1269-MIB");
        assertNotNull(bgpMib);

        SmiTrapType bgpEstablished = bgpMib.findTrapType("bgpEstablished");
        assertNotNull(bgpEstablished);

        SmiTrapType bgpBackwardTransition = bgpMib.findTrapType("bgpBackwardTransition");
        assertNotNull(bgpBackwardTransition);

        SmiVariable bgpPeerRemoteAddr = bgpMib.findVariable("bgpPeerRemoteAddr");
        assertNotNull(bgpPeerRemoteAddr);

        SmiVariable bgpPeerLastError = bgpMib.findVariable("bgpPeerLastError");
        assertNotNull(bgpPeerLastError);

        SmiVariable bgpPeerState = bgpMib.findVariable("bgpPeerState");
        assertNotNull(bgpPeerState);

        assertEquals("1.3.6.1.2.1.15", bgpEstablished.getEnterpriseOid().getOidStr());
        assertEquals(1, bgpEstablished.getSpecificType());
        assertEquals("1.3.6.1.2.1.15.1", bgpEstablished.getOidStr());

        assertEquals("The BGP Established event is generated when\n"
                + "          the BGP FSM enters the ESTABLISHED state. ", bgpEstablished.getDescription());
        assertNull(bgpEstablished.getReference());

        assertNotNull(bgpEstablished.getVariableTokens());
        assertEquals(3, bgpEstablished.getVariableTokens().size());
        assertEquals("bgpPeerRemoteAddr", bgpEstablished.getVariableTokens().get(0).getValue());
        assertEquals("bgpPeerLastError", bgpEstablished.getVariableTokens().get(1).getValue());
        assertEquals("bgpPeerState", bgpEstablished.getVariableTokens().get(2).getValue());
        assertEquals("bgpEstablished", bgpEstablished.getIdToken().getValue());

        assertNotNull(bgpEstablished.getVariables());
        assertEquals(3, bgpEstablished.getVariables().size());
        assertSame(bgpPeerRemoteAddr, bgpEstablished.getVariables().get(0));
        assertSame(bgpPeerLastError, bgpEstablished.getVariables().get(1));
        assertSame(bgpPeerState, bgpEstablished.getVariables().get(2));
    }

}
