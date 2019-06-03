package com.baishiyuan.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * 
 * @ClassName:  SpringBeanUtil
 * @Description:SpringBeanUtil
 * @author:     zhiyongh
 * @date:       2015-4-25 下午5:05:37
 *
 */
public class SpringBeanUtil implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(SpringBeanUtil.class);
	protected static ApplicationContext context = null;
    
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	SpringBeanUtil.context = applicationContext;
    	logger.info(">>>>>>SpringBeanUtil.context setted ok!");
    }
    
	public static ApplicationContext getContext(){
		return context;
	}
	
	public static Object getBean(String beanId){
	    
	    Object obj = context.getBean(beanId);
	    
		return obj;
	}
    
}
