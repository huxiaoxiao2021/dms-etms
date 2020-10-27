package com.jd.bluedragon.dms.utils;


/**
 * 
 * @author wuyoude
 *
 */
public interface DmsMessageConstants {
	/**
	 * 预售拦截消息码
	 */
    public static final Integer CODE_29419 = 29419;
    public static final String MESSAGE_29419 = "此单未付尾款，异常处理选24预售原因，换单回仓！";
    public static final Integer CODE_29420 = 29420;
    public static final String MESSAGE_29420 = "此单为预售未付全款，需要拦截暂存，等付全款后可继续操作！";
}
