package com.avalon.packer.datarepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SourceFileRepo {
    private static Map<String, Calendar> repo = new HashMap<>();

    public static void updateFileInfo (String fileName, Calendar time) {
        Calendar target = repo.get(fileName);
        if (time != target) {
            repo.put(fileName, time);
        }
    }

    public static boolean needUpdate (String fileName, Calendar time) {
        Calendar target = repo.get(fileName);
        return time == target;
    }

    public static Calendar getTime (String fileName) {
        return repo.get(fileName);
    }
}
