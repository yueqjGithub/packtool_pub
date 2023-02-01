package com.avalon.packer.utils;


import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 签名工具类
 *
 * @author QINZH
 */
public abstract class ZzEncryptUtil {
    private final static String SIGN = "sign";

    /**
     * 对http请求参数作字典排序，拼接字符串,不包括签名
     *
     * @param params  请求参数
     * @param signKey 签名的KEY
     * @return 序列化后的待签名字符串
     */
    public static String generateSign(Map<String, String> params, String signKey) {
        String sigString = generateNormalizedString(params, SIGN);
        sigString = sigString.replaceAll("[\\t\\n\\r]", "");
        return MD5Util.MD5(signKey + sigString + signKey);
    }

    /**
     * 对http请求参数作字典排序，拼接字符串,不包括签名
     * @param paramMap    请求参数
     * @param sigParamKey 签名的KEY
     * @return 序列化后的待签名字符串
     */
    private static String generateNormalizedString(Map<String, String> paramMap, String sigParamKey) {
        Set<String> params = paramMap.keySet();
        List<String> sortedParams = new ArrayList<String>(params);
        Collections.sort(sortedParams);
        StringBuilder sb = new StringBuilder();
        for (String paramKey : sortedParams) {
            if (paramKey.equals(sigParamKey)) {
                continue;
            }
            String valueStr = String.valueOf(paramMap.get(paramKey));
            try {
                sb.append(paramKey).append('=').append(null == valueStr ? "" :
                        Base64WrapperUtil.encodeToStr(valueStr.getBytes("utf-8"), Base64WrapperUtil.CodeType.DEFAULT_CODER));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 检查签名
     *
     * @param params  请求参数
     * @param signKey 签名的KEY
     * @return boolean
     */
    public static boolean checkSign(Map<String, String> params, String signKey) {
        if (params.containsKey(SIGN)) {
            String sign = params.get(SIGN);
            String checkSign = generateSign(params, signKey);
            if (equalsStringIgnoreCase(sign, checkSign)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串对比(忽略大小写) 仅当str1,str2不为null,不为空字符串并且相同的情况下,返回true 否则返回false
     *
     * @param str1
     * @param str2
     * @return boolean
     */
    public static boolean equalsStringIgnoreCase(String str1, String str2) {
        return equalsString(str1, str2, true);
    }

    public static boolean equalsString(String str1, String str2, boolean ignoreCase) {
        if (null != str1 && !"".equals(str1) && null != str2 && !"".equals(str2))
            return ignoreCase ? str1.equalsIgnoreCase(str2) : str1.equals(str2);
        return false;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String a = "345app_id=REstVEVTVA==\n" +
                "    channel_id=QU5EMDAw\n" +
                "    345";
        String s = Base64WrapperUtil.encodeToStr(a.getBytes("utf-8"), Base64WrapperUtil.CodeType.DEFAULT_CODER);
       // System.out.println(s);
        String signKey = "345";
        String s1 = MD5Util.MD5("345app_id=REstVEVTVA==channel_id=QU5EMDAw345");
        System.out.println(s1);
    }
}
