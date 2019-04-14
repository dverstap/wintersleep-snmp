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
package org.wintersleep.snmp.util.problem;

import junit.framework.TestCase;
import org.wintersleep.snmp.util.location.Location;

import java.util.ArrayList;
import java.util.List;

public class ProblemHandlerTest extends TestCase {
    private TestProblemEventHandler teh;
    private ExampleProblemReporter pr;

    protected void setUp() throws Exception {
        teh = new TestProblemEventHandler();

        ProblemReporterFactory factory = new DefaultProblemReporterFactory(this.getClass().getClassLoader(), teh);
        pr = factory.create(
                ExampleProblemReporter.class);
    }

    public void testSimpleMessage() {
        pr.simpleMessage();
        assertNotNull(teh.getLastProblemEvent());
        assertEquals("Simple message", teh.getLastProblemEvent().getLocalizedMessage());
        assertNull(teh.getLastProblemEvent().getLocation());
    }

    public void testListSize() {
        List<String> l = new ArrayList<String>();
        l.add("bla");
        pr.reportListSize(l);
        assertEquals("List size = 1", teh.getLastProblemEvent().getLocalizedMessage());
        assertNull(teh.getLastProblemEvent().getLocation());
    }

    public void testLocation() {
        Location location = new Location("/tmp/test", 77, 20);
        pr.simpleLocation(location);

        assertEquals("Simple location message", teh.getLastProblemEvent().getLocalizedMessage());
        assertSame(location, teh.getLastProblemEvent().getLocation());
    }
}
