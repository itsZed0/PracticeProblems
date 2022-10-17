package com.coding.lld;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class LRUCache {
    private int capacity = 4;
    private Set<Integer> cache = new LinkedHashSet<>(capacity);
    public static void main(String[] args) {
        LRUCache lru = new LRUCache();
        lru.refer(1);
        lru.refer(2);
        lru.refer(3);
        lru.refer(1);
        lru.refer(4);
        lru.refer(5);
        System.out.println(lru.cache);
    }

    private void refer(int i) {
        if(cache.contains(i)) {
            cache.remove(i);
            cache.add(i);
        } else {
            if(cache.size() >= capacity) {
                Iterator<Integer> it = cache.iterator();
                cache.remove(it.next());
                cache.add(i);
            } else {
                cache.add(i);
            }
        }
    }
}
