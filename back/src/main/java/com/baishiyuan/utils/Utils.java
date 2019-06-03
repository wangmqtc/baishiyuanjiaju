package com.baishiyuan.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by mogu on 2018/3/28.
 */
public class Utils {

    /**数字pattern*/
    public static final Pattern NUM_CHECK = Pattern.compile("^\\d+$");

    /**复制空Map并填充返回值*/
    public static Map<String,Object> cloneReturnMap(int errKey, String errMsg){
        return putReturnMap(cloneEmptyMap(), errKey, errMsg);
    }

    /**填充返回值*/
    public static Map<String,Object> putReturnMap(Map<String,Object> retMap, int errKey, String errMsg){
        retMap.put(StringConst.ERRCODE_KEY, errKey);
        retMap.put(StringConst.ERRCODE_MSG, errMsg);
        return retMap;
    }

    /**复制空Map*/
    public static Map<String,Object> cloneEmptyMap(){
        //高效的copy map
        //return Collections.synchronizedMap(EMPTY_MAP);这个会一直引用
        //return (Map<String,Object>)EMPTY_MAP.clone();这个有点慢
        return new HashMap<String,Object>();
    }

    /**检查返回值是否成功*/
    public static boolean isReturnSuccess(Map<String,Object> retMap){
        return (Integer)retMap.get(StringConst.ERRCODE_KEY) == StringConst.ERRCODE_SUCCESS;
    }

    public static int formatIntDay(Calendar cal) {
        return cal.get(Calendar.YEAR)*10000+(cal.get(Calendar.MONTH)+1)*100+cal.get(Calendar.DAY_OF_MONTH);
    }

    /**一层加密：加密前*/
    public static String beforeMd5(String str) throws Exception
    {
        //重要，不要随意更改
        return DigestUtils.md5DigestAsHex((str+ "+mgcf").getBytes());
    }
    /**二层加密：加密后*/
    public static String afterMd5(String beforeMd5str) throws Exception
    {
        //重要，不要随意更改
        return DigestUtils.md5DigestAsHex((beforeMd5str+ ".mgcf").getBytes());
    }

    /**toJson字符串方法*/
    public static String toJSONString(Object obj){
        return JSONObject.toJSONString(obj);
    }

    /**是否数字*/
    public static boolean isNumber(String str){
        return str==null ? Boolean.FALSE : NUM_CHECK.matcher(str).find();
    }
}
