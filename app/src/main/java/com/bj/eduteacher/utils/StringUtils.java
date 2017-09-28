package com.bj.eduteacher.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private StringUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str) || "Null".equals(str)
                || "NULL".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 检查用户名格式是否正确
     *
     * @param userName
     * @return
     */
    public static boolean checkUserName(String userName) {
//        String check = "^([\\w\\d_:]|[\\u4e00-\\u9fa5]){2,18}$";
        String check = "^[a-zA-z][a-zA-Z0-9_]{2,18}$";
        Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(userName);
        return m.matches();
    }

    public static boolean checkQRCode(String code) {
        String check = "^\\d{24}$";
        Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(code);
        return m.matches();
    }

    /**
     * 检查邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        String check = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9]" +
                "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检查手机号格式是否正确
     *
     * @param number
     * @return
     */
    public static boolean checkPhoneNumber(String number) {
        String check = "^1\\d{10}$";
        Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 检查密码格式是否正确
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        return true;
    }

    /**
     * 检验HTTP URL 是否正确
     *
     * @param url
     * @return
     */
    public static boolean checkHTTPURL(String url) {
        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        return m.matches();
    }
}
