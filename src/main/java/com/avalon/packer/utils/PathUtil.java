package com.avalon.packer.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class PathUtil {

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return null != os && os.indexOf("Windows") != -1;
    }

    public static String generateRelativeSaveDir() {
        String result = null;
        Date date = new Date();
        result = DateFormatUtils.format(date, "yyyyMMddHHmmssSSS");
        return result.endsWith("/") ? result : result + "/";
    }

    /**
     * 根据原始文件名生成新的文件名 ：即当前时间戳＋4位随机数+原始扩展名
     *
     * @param originalName
     * @return 生成的新文件名
     */
    public static String genearteNewFilename(String originalName) {
        int pos = -1;
        String extName = "";
        if (StringUtils.isNotEmpty(originalName)) {
            pos = originalName.lastIndexOf(".");
        }
        if (pos != -1) {
            extName = originalName.substring(pos);
        }
        String filenamePrefix = String.valueOf(new Date().getTime());
        String filename = filenamePrefix + RandomStringUtils.randomNumeric(4) + extName;
        return filename;
    }
}
