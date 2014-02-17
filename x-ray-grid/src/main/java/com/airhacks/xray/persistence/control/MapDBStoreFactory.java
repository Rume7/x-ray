/*
 */
package com.airhacks.xray.persistence.control;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import java.util.Properties;
import javax.enterprise.inject.Vetoed;

/**
 *
 * @author adam-bien.com
 */
@Vetoed
public class MapDBStoreFactory implements MapStoreFactory<String, String> {

    @Override
    public MapLoader<String, String> newMapStore(String storeName, Properties prprts) {
        return new MapDBStore(storeName);
    }

}
