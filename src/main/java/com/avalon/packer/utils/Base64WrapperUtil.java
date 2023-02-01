package com.avalon.packer.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64WrapperUtil {
    static byte[] encode(byte[] src, CodeType type) {
        Base64.Encoder encoder = Base64.getEncoder();
        switch (type) {
            case DEFAULT_CODER:
                encoder = Base64.getEncoder();
                break;
            case MIMECODER:
                encoder = Base64.getMimeEncoder();
                break;
            case URLCODER:
                encoder = Base64.getUrlEncoder();
                break;
        }
        return encoder.encode(src);
    }

    static String encodeToStr(byte[] src, CodeType type) throws UnsupportedEncodingException {
        return new String(encode(src, type), "UTF-8");
    }

    static String encodeToStr(String src, CodeType type) throws UnsupportedEncodingException {
        return encodeToStr(src.getBytes("UTF-8"), type);
    }

    static byte[] decode(byte[] src, CodeType type) {
        //Base64.Decoder decoder = Base64.getDecoder();
        Base64.Decoder decoder = Base64.getMimeDecoder();
        switch (type) {
            case DEFAULT_CODER:
                decoder = Base64.getDecoder();
                break;
            case MIMECODER:
                decoder = Base64.getMimeDecoder();
                break;
            case URLCODER:
                decoder = Base64.getUrlDecoder();
                break;
        }
        return decoder.decode(src);
    }

    static byte[] decode(String src, CodeType type) throws UnsupportedEncodingException {
        return decode(src.getBytes("UTF-8"), type);
    }

    static String decodeToStr(byte[] src, CodeType type) throws UnsupportedEncodingException {
        return new String(decode(src, type), "UTF-8");
    }

    static String decodeToStr(String src, CodeType type) throws UnsupportedEncodingException {
        return decodeToStr(src.getBytes("UTF-8"), type);
    }

    public enum CodeType {
        /**
         * 默认解码器
         */
        DEFAULT_CODER,
        /**
         * mime 分行解码器
         */
        MIMECODER,
        /**
         * url safe 解码器
         */
        URLCODER
    }
}
