package com.baishiyuan.utils;

import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 文 件 名:  JavaBeanUtils.java
 * 版    权:  Mogucaifu Technologies Co., Ltd. Copyright 2014-2016,  All rights reserved
 * 描    述:  javabean处理
 * 创建人  :  miaoqingw
 * 创建时间:  2015年12月10日
 */
public class JavaBeanUtils {
    private static final Logger logger = Logger.getLogger(JavaBeanUtils.class);

    public static <V> Map<String, Object> convertBeanToMap(V obejct)
    {

        if(obejct == null){
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obejct.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                Method getter = property.getReadMethod();
                if(getter != null){
                    Object value = getter.invoke(obejct);
                    if(value != null)
                    {
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("convert BeanToMap Error " + e);
        }
        return map;
    }
}
