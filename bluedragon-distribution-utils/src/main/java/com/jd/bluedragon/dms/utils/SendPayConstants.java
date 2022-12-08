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
	/**
	 * 生鲜标识
	 */
	int POSITION_2 = 2;
	char CHAR_2_4 = '4';
	char CHAR_2_5 = '5';
	char CHAR_2_6 = '6';
	char CHAR_2_7 = '7';
	char CHAR_2_8 = '8';
	char CHAR_2_9 = '9';
	
	int POSITION_124 = 124;
	char CHAR_124_7 = '7';
	/**
	 * sendpay 第146位=1（城配网络订单）
	 */
	int POSITION_146 = 146;
	char CHAR_146_1 = '1';
	/**
	 * sendpay 第146位=4   面单产品打印【冷链卡班】占位符 jzdflag
	 */
	char CHAR_146_4 = '4';
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
	 * 预售未付款：
     *  1、退仓-297位为1 且 228位为1或2
     *  2、暂存分拣 297位1 且 228位为4
     * 预售已付款： 297位为2
	 */
	int POSITION_297 = 297;
	char CHAR_297_1 = '1';
	char CHAR_297_2 = '2';
    int POSITION_228 = 228;
    char CHAR_228_1 = '1';
    char CHAR_228_2 = '2';
    char CHAR_228_4 = '4';
	char CHAR_228_5 = '5';
	char CHAR_228_6 = '6';
	char CHAR_228_7 = '7';
	/**
	 * SendPay第293位,1-海运标识
	 */
	int POSITION_293 = 293;
	char CHAR_293_1 = '1';
	/**
	 * SendPay第314位,1-企配仓 2-企配仓
	 */
	int POSITION_314 = 314;
	char CHAR_314_1 = '1';
	char CHAR_314_2 = '2';
	/**
	 * SendPay第315位,CHAR_315_0 = '0' -非B2B运单
	 */
	int POSITION_315 = 315;
	char CHAR_315_0 = '0';
	/**
	 * Sendpay137位=1【京航达】
	 */
	int POSITION_137 = 137;
	char CHAR_137_1 = '1';
	/**
	 * 第307位,1-无人车配送
	 */
	int POSITION_307 = 307;
	char CHAR_307_1 = '1';
	/**
	 * 第292位,1-合约机业务
	 */
	int POSITION_292 = 292;
	char CHAR_292_1 = '1';
	/**
	 * 第327位,2-抖音标识
	 */
	int POSITION_327 = 327;
	char CHAR_327_2 = '2';
	/**
	 * 第596位，1-特殊品类（黄金珠宝等贵重物品）
	 */
	String POSITION_596 = "596";
	String STR_596_1 = "1";

	/**
	 * 第8位为6是全球购订单
	 */
	int POSITION_8 = 8;
	char CHAR_6 = '6';
}
