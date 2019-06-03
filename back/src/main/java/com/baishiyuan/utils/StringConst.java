package com.baishiyuan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 
 * @ClassName:  StringConst
 * @Description:字符串常量类
 *
 */
public class StringConst {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**当前是否为开发模式*/
	private static boolean devMode;
	
	/*系统名称*/
	public static final String SYS_APP_NAME = "pmnjy";
    
	/**路径通道:web*/
	public static final String PATH_CHANNEL_WEB = "web";
	/**路径通道:h5*/
    public static final String PATH_CHANNEL_H5 = "h5";
    /**路径通道:ih5，内嵌app*/
    public static final String PATH_CHANNEL_IH5 = "ih5";
    /**路径通道:app*/
    public static final String PATH_CHANNEL_APP = "app";
    /**路径通道:miniapp小程序*/
    public static final String PATH_CHANNEL_MINIAPP = "miniapp";
    
    /**行情服务接口地址*/
    public static String HQ_URL = "please set";//"http://appf5.mogucaifu.com/mnjy3.1_client/m/hq.jsp?c=%s&f=%s&r=%s";
    /**行情批量加载地址*/
    public static String HQ_LOADING_URL = "please set";//"http://appf5.mogucaifu.com/mnjy3.1_client/m/hq_loading.jsp?f=%s&r=%s";
    
	/**临时文件目录*/
	private static String tempDir = "../temp/";

	public static String MAIL_SMTP_SERVER = "smtp.126.com";
	public static String MAIL_FROM_EMAIL = "mogualert@126.com";
	public static String MAIL_TO_EMAIL = "xinglinc@mogucaifu.com";
	public static String MAIL_USERNAME = "mogualert@126.com";
	public static String MAIL_PASSWORD = "mogu07550831";
	
	public static final String EMPTY_STRING = "";
	
	public static final String SPILT_PARAMETERS = "\\|";//列参数分隔
	public static final String ERRCODE_KEY = "ret";//错误码key
	public static final String ERRCODE_MSG = "msg";//错误或提示内容
	
	public static final int ERRCODE_SUCCESS = 0;//成功
	public static final String ERRCODE_SUCCESS_MSG = "ok";//成功
	
	/**100000 系统具体提示内容*/
    public static final int ERRCODE_X = 100000;

    /**100000 系统具体提示内容(其它提示)*/
    public static final int ERRCODE_X2 = 200000;

    /**100001 系统维护中，请稍候…*/
    public static final int ERRCODE_MAINTENANCE = 100001;
    public static final String ERRCODE_MAINTENANCE_MSG = "系统维护中，请稍候…";  

    /**100002   hash一致,不用更新cache*/
    public static final int ERRCODE_HASH_EQUAL = 100002;
    public static final String ERRCODE_HASH_EQUAL_MSG = "hash一致,不用更新cache"; 
    
    /**100003  请等待*/
    public static final int ERRCODE_WAIT = 100003;
    public static final String ERRCODE_WAIT_MSG = "请等待"; 
    
    //-------------------------------------------------------------------------------
    /**101000 缺少参数不足*/
    public static final int ERRCODE_C_NOTENOUGH = 101000;
    public static final String ERRCODE_C_NOTENOUGH_MSG = "缺少公共参数";
    /**101005 缺少参数*/    
    public static final int ERRCODE_PARAMETER_NOTENOUGH = 101005;
    public static final String ERRCODE_PARAMETER_NOTENOUGH_MSG = "缺少参数";
    /**101006   缺少%s参数*/
    public static final int ERRCODE_PARAMETER_NOT_THEPARAM = 101006;
    public static final String ERRCODE_PARAMETER_NOT_THEPARAM_MSG = "缺少%s参数";
    
    /**101007   %s参数值不能为空!*/
    public static final int ERRCODE_PARAMETER_NOT_EMPTY = 101007;
    public static final String ERRCODE_PARAMETER_NOT_EMPTY_MSG = "%s参数值不能为空!";
    
    /**101008  %s已存在!*/
    public static final int ERRCODE_EXISTS = 101008;
    public static final String ERRCODE_EXISTS_MSG = "%s已存在!"; 
    
    /**101009  %s已满!*/
    public static final int ERRCODE_FULL = 101009;
    public static final String ERRCODE_FULL_MSG = "%s已满!"; 
    
    /**101010  %s不存在!*/
    public static final int ERRCODE_NOT_EXISTS = 101010;
    public static final String ERRCODE_NOT_EXISTS_MSG = "%s不存在!";   
    
    /**101011  验证码不正确!*/
    public static final int ERRCODE_RANDCODE_UNCHECKED = 101011;
    public static final String ERRCODE_RANDCODE_UNCHECKED_MSG = "验证码不正确!";
    
    /**101012  %s不足!*/
    public static final int ERRCODE_NOT_ENOUGH = 101012;
    public static final String ERRCODE_NOT_ENOUGH_MSG = "%s不足!";
      

    
    /**101001   未找到功能号*/
    public static final int ERRCODE_NOTFOUND_FUNID = 101001;
    public static final String ERRCODE_NOTFOUND_FUNID_MSG = "未找到功能号";
    
    /**101002   缺少time参数*/
    public static final int ERRCODE_NOTIMESTAMP = 101002;
    public static final String ERRCODE_NOTIMESTAMP_MSG = "缺少time参数";
    
    /**101003   缺少sign参数*/
    public static final int ERRCODE_NOSIGN = 101003;
    public static final String ERRCODE_NOSIGN_MSG = "缺少sign参数";
    
    /**101004   签名sign非法*/
    public static final int ERRCODE_SIGNILLEGAL = 101004;
    public static final String ERRCODE_SIGNILLEGAL_MSG = "签名sign非法";

    //--------------------------------------------------------------------------------
    /**102000 必须先登录*/
    public static final int ERRCODE_MUSTLOGIN = 102000;
    public static final String ERRCODE_MUSTLOGIN_MSG = "请先登录";
    
    /**102001   第三方登录失败!*/
    public static final int ERRCODE_LOGIN_FAILURE_FORTHIRD = 102001;
    public static final String ERRCODE_LOGIN_FAILURE_FORTHIRD_MSG = "第三方登录失败!";
    /**102002   登录失败!请检查用户名和密码!*/
    public static final int ERRCODE_LOGIN_FAILURE = 102002;
    public static final String ERRCODE_LOGIN_FAILURE_MSG = "登录失败!请检查用户名和密码!";
    /**102003   未找到此用户*/
    public static final int ERRCODE_USER_NOTFOUND = 102003;
    public static final String ERRCODE_USER_NOTFOUND_MSG = "未找到此用户";
    /**102004   完善个人资料*/
    public static final int ERRCODE_USER_INFO_IMPROVE = 102004;
    public static final String ERRCODE_USER_INFO_IMPROVE_MSG = "请先完善个人资料";
    
    /**102005   手机号码不正确*/
    public static final int ERRCODE_MOBILE_ERR = 102005;
    public static final String ERRCODE_MOBILE_ERR_MSG = "手机号码不正确";
    
    /**102006   昵称不能为空*/
    public static final int ERRCODE_NICKNAME_ISEMPTY = 102006;
    public static final String ERRCODE_NICKNAME_ISEMPTY_MSG = "昵称不能为空";
    
    /**120006   清算中(模拟交易)*/
    public static final int ERRCODE_LIQUIDATING = 120006;
    public static final String ERRCODE_LIQUIDATING_MSG = "系统清算中，请于17:00后下单！";   

    
	public void setMAIL_SMTP_SERVER(String mAIL_SMTP_SERVER) {
		StringConst.MAIL_SMTP_SERVER = mAIL_SMTP_SERVER;
	}

	public void setMAIL_FROM_EMAIL(String mAIL_FROM_EMAIL) {
		StringConst.MAIL_FROM_EMAIL = mAIL_FROM_EMAIL;
	}

	public void setMAIL_TO_EMAIL(String mAIL_TO_EMAIL) {
		StringConst.MAIL_TO_EMAIL = mAIL_TO_EMAIL;
	}

	public void setMAIL_USERNAME(String mAIL_USERNAME) {
		StringConst.MAIL_USERNAME = mAIL_USERNAME;
	}

	public void setMAIL_PASSWORD(String mAIL_PASSWORD) {
		StringConst.MAIL_PASSWORD = mAIL_PASSWORD;
	}

	public static boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		StringConst.devMode = devMode;
	}
	
	public static String getTempDir() {
		return tempDir;
	}

	public  void setTempDir(String tempDir) {
		StringConst.tempDir = tempDir;
        logger.warn(String.format("配置 临时目录 StringConst.tempDir=%s", tempDir));
		File f=new File(tempDir);
		if(! f.exists()){
            logger.warn(String.format("临时目录 不存在，请设置(程序中止)！StringConst.tempDir=%s", tempDir));
			System.exit(0);
		}
	}

    public void setHQ_URL(String hQ_URL)
    {
        HQ_URL = hQ_URL;
        logger.warn(String.format("配置 行情接口地址 StringConst.HQ_URL=%s", HQ_URL));
    }

    public void setHQ_LOADING_URL(String hQ_LOADING_URL)
    {
        HQ_LOADING_URL = hQ_LOADING_URL;
        logger.warn(String.format("配置 行情批量加载地址 StringConst.HQ_LOADING_URL=%s", HQ_LOADING_URL));
    }

    public static void main(String[] args){
		
	}
}
