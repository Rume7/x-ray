package com.abien.xray.business.hits.control;

import com.abien.xray.business.logging.boundary.XRayLogger;
import java.util.Map;
import java.util.function.Predicate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

/**
 *
 * @author airhacks.com
 */
public class FilterProviderTest {

    private FilterProvider cut;

    @Before
    public void init() {
        this.cut = new FilterProvider();
        this.cut.initEngine();
        this.cut.LOG = mock(XRayLogger.class);
    }

    @Test
    public void createPredicate() {
        Predicate<Map.Entry<String, String>> predicate = this.cut.createFromNashornScript("function test(i){return false;}");
        boolean result = predicate.test(null);
        assertFalse(result);
    }

    @Test
    public void predicateFromNullScript() {
        Predicate<Map.Entry<String, String>> predicate = this.cut.createFromNashornScript(null);
        boolean result = predicate.test(null);
        assertTrue(result);
    }

    @Test
    public void predicateFromEmptyScript() {
        Predicate<Map.Entry<String, String>> predicate = this.cut.createFromNashornScript("");
        boolean result = predicate.test(null);
        assertTrue(result);
    }

    @Test
    public void predicateFromIncorrectScript() {
        Predicate<Map.Entry<String, String>> predicate = this.cut.createFromNashornScript("false");
        boolean result = predicate.test(null);
        assertTrue(result);
    }

    @Test
    public void functionWithMapEntry() {
        Predicate<Map.Entry<String, String>> predicate = this.cut.createFromNashornScript("function test(i){return i.key.endsWith('duke');}");
        boolean result = predicate.test(new Map.Entry<String, String>() {

            @Override
            public String getKey() {
                return "duke";
            }

            @Override
            public String getValue() {
                return "";
            }

            @Override
            public String setValue(String value) {
                return "value";
            }
        });
        assertTrue(result);
    }

}
