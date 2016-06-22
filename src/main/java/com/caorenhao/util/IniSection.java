package com.caorenhao.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IniSection {

    public IniSection(String name) {
        this.name = name;
    }

    public String get(String key) {
        return valueMap.get(key);
    }
    
    public String getName() {
        return name;
    }

    public void put(String key, String value) {
        valueMap.put(key, value);
    }
    
    public Set<Map.Entry<String, String>> entrySet() {
        return valueMap.entrySet();
    }

    private String name;

    private Map<String, String> valueMap = new HashMap<String, String>();
}