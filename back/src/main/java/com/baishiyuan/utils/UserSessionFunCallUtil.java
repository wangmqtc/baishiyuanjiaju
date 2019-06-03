package com.baishiyuan.utils;

import com.baishiyuan.domain.SessionInfo;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @ClassName:  UserSessionFunCallUtil
 * @Description:会话调用Util
 * @author:     zhiyongh
 * @date:       2016-1-11 下午14:01:16
 */
public class UserSessionFunCallUtil {
	private static final Logger logger = Logger.getLogger(UserSessionFunCallUtil.class);

    /**获取当前登录会话值*/
    public static SessionInfo getCurrentSession(HttpServletRequest request) {

        HttpSession httpSession = request.getSession(false);

        if(httpSession == null){
            return null;
        }

        return (SessionInfo)httpSession.getAttribute("userInfo");
    }

}
