package com.abien.xray.business.store.control;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: blog.adam-bien.com Date: 17.02.11 Time: 21:11
 */
public class HitsCache {

    private ConcurrentMap<String, AtomicLong> hits = null;
    private ConcurrentSkipListSet<String> dirtyKeys;
    private ConcurrentSkipListSet<String> neverDirty;

    public HitsCache(ConcurrentMap hits) {
        this.hits = hits;
        this.dirtyKeys = new ConcurrentSkipListSet<>();
        this.neverDirty = new ConcurrentSkipListSet<>();
        initializeNeverDirty();
    }

    public long increase(String uniqueAction) {
        this.dirtyKeys.add(uniqueAction);
        hits.putIfAbsent(uniqueAction, new AtomicLong());
        this.neverDirty.remove(uniqueAction);
        AtomicLong hitCount = hits.get(uniqueAction);
        return hitCount.incrementAndGet();
    }

    public Map<String, AtomicLong> getCache() {
        return hits;
    }

    public Map<String, AtomicLong> getChangedEntriesAndClear() {
        Map<String, AtomicLong> changedValues = new HashMap<String, AtomicLong>();
        for (String dirtyKey : dirtyKeys) {
            dirtyKeys.remove(dirtyKey);
            changedValues.put(dirtyKey, hits.get(dirtyKey));
        }
        return changedValues;
    }

    public int getCacheSize() {
        return this.hits.size();
    }

    public int getDirtyEntriesCount() {
        return this.dirtyKeys.size();
    }

    public void clear() {
        hits.clear();
        dirtyKeys.clear();
    }

    public Set<String> getInactiveEntries() {
        return this.neverDirty;
    }

    public Set<String> getInactiveEntriesAndClear() {
        Set<String> staleEntries = new HashSet<String>();
        for (String key : this.neverDirty) {
            this.hits.remove(key);
            staleEntries.add(key);
        }
        this.neverDirty.clear();
        return staleEntries;
    }

    final void initializeNeverDirty() {
        Set<String> keySet = hits.keySet();
        this.neverDirty.addAll(keySet);
    }
}
