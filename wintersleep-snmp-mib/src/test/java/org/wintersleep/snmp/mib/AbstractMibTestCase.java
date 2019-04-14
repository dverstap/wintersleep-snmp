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
package org.wintersleep.snmp.mib;

import com.google.common.base.Stopwatch;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wintersleep.snmp.mib.parser.SmiDefaultParser;
import org.wintersleep.snmp.mib.parser.SmiParser;
import org.wintersleep.snmp.mib.smi.*;
import org.wintersleep.snmp.util.problem.annotations.ProblemSeverity;
import org.wintersleep.snmp.util.url.DefaultURLListBuilder;
import org.wintersleep.snmp.util.url.URLListFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMibTestCase extends TestCase {

    public static final File LIBSMI_DIR = new File(findMibs(), "libsmi");
    public static final File LIBSMI_IANA_DIR = new File(LIBSMI_DIR, "iana");
    public static final File LIBSMI_IETF_DIR = new File(LIBSMI_DIR, "ietf");

    public static final String LIBSMI_DIR_NAME = LIBSMI_DIR.getAbsolutePath();

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMibTestCase.class);

    private final SmiVersion version;

    private static ThreadLocal<Class<? extends AbstractMibTestCase>> testClass = new ThreadLocal<Class<? extends AbstractMibTestCase>>();
    private static ThreadLocal<SmiMib> mib = new ThreadLocal<SmiMib>();

    private SmiType integer32;
    private SmiType counter;
    private SmiDefaultParser parser;


    private static File findMibs() {
        File pwd = Paths.get("").toAbsolutePath().toFile();
        File parent = pwd.getParentFile();
        while (parent != null) {
            File mibs = new File(parent, "mibs");
            if (mibs.exists()) {
                return mibs;
            }
            parent = parent.getParentFile();
        }
        throw new IllegalStateException("Cannot find mibs directory.");
    }

    public AbstractMibTestCase() {
        this.version = null;
    }

    public AbstractMibTestCase(SmiVersion version) {
        this.version = version;
    }


    protected SmiDefaultParser getParser() {
        return parser;
    }

    protected SmiMib getMib() {
        // this is a rather ugly hack to mimic JUnit4 @BeforeClass, without having to annotate all test methods:
        if (mib.get() == null || testClass.get() != getClass()) {
            try {
                SmiParser parser = createParser();
                Stopwatch stopWatch = Stopwatch.createStarted();
                SmiMib mib = parser.parse();
                LOGGER.info("Parsing time: " + stopWatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
                if (mustParseSuccessfully()) {
                    assertTrue(((SmiDefaultParser) parser).getProblemEventHandler().isOk());
                    assertEquals(0, ((SmiDefaultParser) parser).getProblemEventHandler().getSeverityCount(ProblemSeverity.ERROR));
                }
                AbstractMibTestCase.mib.set(mib);
                testClass.set(getClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return mib.get();
    }

    protected boolean mustParseSuccessfully() {
        return true;
    }

    protected SmiParser createParser() throws Exception {
        URLListFactory urlListFactory = createURLListFactory();
        parser = new SmiDefaultParser();
        parser.getFileParserPhase().setInputUrls(urlListFactory.create());
        return parser;
    }

    public URLListFactory createURLListFactory() {
        DefaultURLListBuilder result = new DefaultURLListBuilder();
        addUrls(result);
        return result;
    }

    protected void addUrls(DefaultURLListBuilder builder) {
        if (version == null || version == SmiVersion.V1) {
            builder.addDir(LIBSMI_IETF_DIR,
                    "RFC1155-SMI");
        }
        if (version == null || version == SmiVersion.V2) {
            builder.addDir(LIBSMI_IETF_DIR,
                    "SNMPv2-SMI",
                    "SNMPv2-TC",
                    "SNMPv2-CONF",
                    "SNMPv2-MIB");
        }
    }

    public SmiType getInteger32() {
        if (integer32 == null) {
            integer32 = getMib().getTypes().find("Integer32");
            assertSame(SmiConstants.INTEGER_TYPE, integer32.getBaseType());
            assertSame(SmiPrimitiveType.INTEGER_32, integer32.getPrimitiveType());
            assertEquals(1, integer32.getRangeConstraints().size());
            assertEquals(-2147483648, integer32.getRangeConstraints().get(0).getMinValue().intValue());
            assertEquals(2147483647, integer32.getRangeConstraints().get(0).getMaxValue().intValue());
            assertNull(integer32.getSizeConstraints());
            assertNull(integer32.getEnumValues());
            assertNull(integer32.getBitFields());
            assertNull(integer32.getFields());
        }
        return integer32;
    }

    public SmiType getCounter() {
        if (counter == null) {
            counter = getMib().getTypes().find("Counter");
            assertSame(SmiConstants.INTEGER_TYPE, counter.getBaseType());
            assertSame(SmiPrimitiveType.COUNTER_32, counter.getPrimitiveType());
            assertEquals(1, counter.getRangeConstraints().size());
            assertEquals(0, counter.getRangeConstraints().get(0).getMinValue().intValue());
            assertEquals(0xFFFFFFFFL, counter.getRangeConstraints().get(0).getMaxValue().longValue());
            assertNull(counter.getSizeConstraints());
            assertNull(counter.getEnumValues());
            assertNull(counter.getBitFields());
            assertNull(counter.getFields());
        }
        return counter;
    }

    protected void showOverview() {
        for (SmiModule module : mib.get().getModules()) {
            for (SmiSymbol symbol : module.getSymbols()) {
                String msg = module.getId() + ": " + symbol.getId() + ": " + symbol.getClass().getSimpleName();
                if (symbol instanceof SmiOidValue) {
                    SmiOidValue oidValue = (SmiOidValue) symbol;
                    msg += ": " + oidValue.getOidStr();
                }
                System.out.println(msg);
            }
        }
    }

    protected void checkOidTree(SmiMib mib) {
        //mib.getRootNode().dumpTree(System.out, "");

        int count = 0;
        for (SmiSymbol symbol : mib.getSymbols()) {
            if (symbol instanceof SmiOidValue) {
                SmiOidValue oidValue = (SmiOidValue) symbol;
                //oidValue.dumpAncestors(System.out);
                if (oidValue.getNode() != mib.getRootNode()) {
                    String msg = oidValue.getIdToken().toString();
                    assertNotNull(msg, oidValue.getNode().getParent());

                    SmiOidNode foundOidNode = oidValue.getNode().getParent().findChild(oidValue.getNode().getValue());
                    assertNotNull(msg, foundOidNode);
                    assertSame(msg, oidValue.getNode(), foundOidNode);
                    assertTrue(msg, oidValue.getNode().getParent().contains(oidValue.getNode()));
                }
//                SmiOidValue foundSymbol = findOidSymbol(mib.getRootNode(), symbol.getId());
//                assertNotNull(symbol.getId(), foundSymbol);
//                assertSame(symbol.getId(), symbol, foundSymbol);
                count++;
            }
        }

        //mib.getRootNode().dumpTree(System.out, "");
        int totalChildCount = mib.getRootNode().getTotalChildCount();
        //assertTrue(count + " < " +  totalChildCount, count < totalChildCount);
        //System.out.println("totalChildCount: " + totalChildCount);

        // I don't think you can draw any conclusions based on the count:
        // - due to anonymous oid node, you can have more oid nodes than oid symbols
        // - due to duplicated symbols you can have more symbols than nodes:
        //assertEquals(count + mib.getDummyOidNodesCount(), totalChildCount);
    }

    protected void checkObjectTypeAccessAll(SmiMib mib) {
        for (SmiObjectType objectType : mib.getObjectTypes()) {
            assertNotNull(objectType.getId(), objectType.getAccessAll());
        }
    }

    protected void add(Set<String> paths, String dir, String name) {
        paths.add(dir + "/" + name);
    }
}
