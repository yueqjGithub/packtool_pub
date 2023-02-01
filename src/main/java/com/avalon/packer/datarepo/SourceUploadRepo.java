package com.avalon.packer.datarepo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SourceUploadRepo {
    private static Map<String, SourceRepoItem> sourceMap = new HashMap<>();

    public static SourceRepoItem getValue (String key) {
        return sourceMap.get(key);
    }

    public static void setSourceMap (String key, SourceRepoItem source) {
        sourceMap.put(key, source);
    }

    public static void delSourceMapKey (String key) {
        sourceMap.remove(key);
    }
}
