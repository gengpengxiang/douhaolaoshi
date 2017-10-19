package com.bj.eduteacher.utils;

import android.util.Base64;

import java.net.URL;
import java.net.URLEncoder;

public class CharEncodeUtil {
    /**
     * 对网址路径进行编码，针对其中的汉字
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String changeURLEncode(String path) throws Exception {
        URL url = new URL(path);
        String protocol = url.getProtocol();
        String authority = url.getAuthority();
        String urlPath = url.getPath();
        String[] elements = urlPath.split("/");
        for (int i = 0; i < elements.length; i++) {
            elements[i] = URLEncoder.encode(elements[i], "UTF-8");
        }
        StringBuffer sb = new StringBuffer();
        for (String element : elements) {
            sb.append(element).append("/");
        }
        String urlPath2 = sb.substring(1, sb.length() - 1).toString();

        String urlPath3 = protocol + "://" + authority + "/" + urlPath2;

        return urlPath3;
    }

    /**
     * 判断是否为BASE加密
     */
    public static boolean ifBASE64Encode(String val) throws Exception {
        byte[] key = Base64.decode(val, Base64.DEFAULT);
        String strs = new String(key);
        String result = Base64.encodeToString(strs.getBytes(), Base64.DEFAULT);
        if (result.equals(val)) {
            return true;
        }
        return false;
    }

    /**
     * 获取可能被Base64加密过的字符串
     *
     * @param val
     * @return
     * @throws Exception
     */
    public static String getBase64DecodeContent(String val) throws Exception {
        byte[] key = Base64.decode(val, Base64.DEFAULT);
        String strs = new String(key);
        String result = Base64.encodeToString(strs.getBytes(), Base64.DEFAULT);
        if (result.equals(val)) {
            return strs;
        }
        return val;
    }
}
