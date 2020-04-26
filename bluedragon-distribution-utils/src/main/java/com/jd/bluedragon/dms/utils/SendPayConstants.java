package com.jd.bluedragon.dms.utils;
/**
 * 
 * @ClassName: SendPayConstants
 * @Description: sendPay相关的常量定义
 * @author: wuyoude
 * @date: 2019年4月23日 上午10:19:10
 *
 */
public interface SendPayConstants {
	/**
	 * 默认打标-0
	 */
	char CHAR_0 = '0';

	int POSITION_124 = 124;
	char CHAR_124_7 = '7';
	/**
	 * sendpay 第146位=1（城配网络订单）
	 */
	int POSITION_146 = 146;
	char CHAR_146_1 = '1';
	/**
	 * SendPay第188位隐藏收件人信息标识
	 */
	int POSITION_188 = 188;
	/**
	 * SendPay第295位,1-物业代收 2-保安室代收 3-门口存放 4-用户指定
	 */
	int POSITION_295 = 295;
	char CHAR_295_1 = '1';
	char CHAR_295_2 = '2';
	char CHAR_295_3 = '3';
	char CHAR_295_4 = '4';
	/**
	 * SendPay第293位,1-海运标识
	 */
	int POSITION_293 = 293;
	char CHAR_293_1 = '1';
}
