package com.avalon.packer.cache;

public class KeyUtils {
    public static final String CONNECTOR = ":";
    public static final String PREFIX = "ContentSecurity";

    public static String getKey(String namespace, String key) {
        return namespace + CONNECTOR + key;
    }


    public static String getKey(String prefix, String namespace, String key) {
        return prefix + CONNECTOR + namespace + CONNECTOR + key;
    }

    public static class Namespace {
        public static final String EVENT = "event";
        public static final String COMMON_CONTENT = "common_content";
    }
}
