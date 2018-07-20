package com.bj.eduteacher.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelNumMatch {
    /*
     * 10. * 移动: 2G号段(GSM网络)有139,138,137,136,135,134,159,158,152,151,150, 11. *
     * 3G号段(TD-SCDMA网络)有157,182,183,188,187,181 147是移动TD上网卡专用号段. 联通: 12. *
     * 2G号段(GSM网络)有130,131,132,155,156 3G号段(WCDMA网络)有186,185 电信: 13. *
     * 2G号段(CDMA网络)有133,153 3G号段(CDMA网络)有189,180 14.
     */
    static String YD = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[12378]{1})|([4]{1}[7]{1}))[0-9]{8}$";
    static String LT = "^[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}$";
    static String DX = "^[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}$";

    String mobPhnNum;

    public TelNumMatch(String mobPhnNum) {
        this.mobPhnNum = mobPhnNum;
        Log.d("tool", mobPhnNum);
    }

    public int matchNum() {
        /**
         * 28. * flag = 1 YD 2 LT 3 DX 29.
         */
        int flag;//存储匹配结果
        // 判断手机号码是否是11位
        if (mobPhnNum.length() == 11) {
            // 判断手机号码是否符合中国移动的号码规则
            if (mobPhnNum.matches(YD)) {
                flag = 1;
            }
            // 判断手机号码是否符合中国联通的号码规则
            else if (mobPhnNum.matches(LT)) {
                flag = 2;
            }
            // 判断手机号码是否符合中国电信的号码规则
            else if (mobPhnNum.matches(DX)) {
                flag = 3;
            }
            // 都不适合，未知֪
            else {
                flag = 4;
            }
        }
        // 不是11位
        else {
            flag = 5;
        }
        Log.d("TelNumMatch", "flag" + flag);
        return flag;
    }

    //手机号码的有效性验证
    public static boolean isValidPhoneNumber(String number)
    {
        boolean flag=false;
        if(number.length()==11 && (number.matches(YD)||number.matches(LT)||number.matches(DX)))
        {
            flag=true;
        }
        return flag;
    }

    //判断手机号码是否存在
    public static boolean isExistPhoneNumber(String number)
    {
        return false;
    }

    //判断email地址是否有效
    public static boolean isEmail(String email)
    {
        String patternString="^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        return isMatcher(patternString,email);
    }

    //是否是数字和字母
    public static boolean isMatchCharOrNumber(String str)
    {
        String patternString="^[\\d|a-z|A-Z]+$";
        return isMatcher(patternString,str);
    }

    //是否匹配
    public static boolean isMatcher(String patternString,String str)
    {
        boolean isValid=false;
        CharSequence inputStr =str ;
        Pattern pattern =Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid =true;
        }
        return isValid;
    }
}
