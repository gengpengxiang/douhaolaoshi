package com.bj.eduteacher.community.utils;





import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class Base64Util {
    /**
     * BASE64字符串转为正常字符串
     * @param
     * @return
     */
    public static String decode(final String string) {
        try {
            return new String(Base64.decodeBase64(string.getBytes("UTF-8")), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 二进制数据编码为BASE64字符串
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String encode(final String string) {
        try {
            return new String(Base64.encodeBase64(string.getBytes("UTF-8")),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断是否进行base64加密
     * @param str
     * @return
     */

    public static boolean checkBase64(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
            if (str.length() % 4 != 0) {
                return false;
            }

            char[] strChars = str.toCharArray();
            for (char c:strChars) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                        || c == '+' || c == '/' || c == '=') {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }
}
