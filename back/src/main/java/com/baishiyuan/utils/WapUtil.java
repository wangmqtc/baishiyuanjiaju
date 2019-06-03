package com.baishiyuan.utils;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mogu on 2018/3/28.
 */
public class WapUtil {

    public static final Pattern MOBILENOCHECK = Pattern.compile("^1[2|3|4|5|6|7|8|9][\\d]{9}$");

    public static final Pattern PWD_PATTERN = Pattern.compile("^[a-zA-Z0-9_@.,]{6,40}$");

    /**数字pattern*/
    public static final Pattern NUM_CHECK = Pattern.compile("^\\d+$");

    /**字母pattern*/
    public static final Pattern LETTER_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    public static final Pattern EMOJI_PATTERN = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");

    /**
     * 用户昵称过滤，以便Wap显示正常
     * @param str
     * @return 安全内容
     */
    public static String getUserNickNameSafeFilter(String str) {
        if(str==null) return "";
        return str.replaceAll("\\|","｜").replaceAll("&","＆").replaceAll("'","’").replaceAll("\"","”").replaceAll("<","〈").replaceAll(">","〉").replaceAll("","").replaceAll("`", "’");
    }

    /**
     * 手机号码检查
     * @param str
     * @return
     */
    public static boolean isMobileno(String str){
        if(str==null) return false;
        str=str.trim();
        if(str.length()>11){
            str=str.substring(str.length()-11);
        }
        return MOBILENOCHECK.matcher(str).find();
    }

    /**密码检查
     * 不允许简重复密码，如111111、aaa、少于6位的密码
     * 需字母、数字混合
     * */
    public static Map<String,Object> checkPwd(String pwd) {
        if(pwd != null && pwd.length() > 30) {
            //已加过密
            return Utils.cloneReturnMap(StringConst.ERRCODE_SUCCESS, StringConst.ERRCODE_SUCCESS_MSG);
        }
        Map<String,Object> retMap = Utils.cloneReturnMap(StringConst.ERRCODE_X, "请输入至少6位长度的密码，需包含数字、字母！");
        if(pwd!=null && PWD_PATTERN.matcher(pwd).find() && ! isNumber(pwd) && ! LETTER_PATTERN.matcher(pwd).find()) {

            //重复的其它字符
            String regx = String.format("^[%s]{6,}$", pwd.substring(0, 1));
            Pattern pattern = Pattern.compile(regx);

            if(pattern.matcher(pwd).find()) {
                return Utils.putReturnMap(retMap, StringConst.ERRCODE_X, "密码不能完全重复！");
            }

            return Utils.putReturnMap(retMap, StringConst.ERRCODE_SUCCESS, StringConst.ERRCODE_SUCCESS_MSG);
        }

        return retMap;
    }

    /**是否数字*/
    public static boolean isNumber(String str){
        return str==null ? Boolean.FALSE : NUM_CHECK.matcher(str).find();
    }



    /**过滤emoji表情符*/
    public static String filterEmoji(String str) {
        if(StringUtils.isEmpty(str)){
            return str;
        }

        String reStr="?";
        Matcher emojiMatcher=EMOJI_PATTERN.matcher(str);
        str=emojiMatcher.replaceAll(reStr);

        return str;
    }

}
