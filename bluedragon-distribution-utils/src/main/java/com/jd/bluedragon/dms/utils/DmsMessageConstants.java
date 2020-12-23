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

    Integer CODE_50001 = 50001;
    String MESSAGE_50001 = "最多绑定{0}个WJ";

    Integer CODE_50002 = 50002;
    String MESSAGE_50002 = "BC箱号已发货";

    Integer CODE_50003 = 50003;
    String MESSAGE_50003 = "箱号目的地不一致";

    Integer CODE_50004 = 50004 ;
    String MESSAGE_50004 = "箱号{0}正在由他人操作绑定，请稍后再试!";
}
