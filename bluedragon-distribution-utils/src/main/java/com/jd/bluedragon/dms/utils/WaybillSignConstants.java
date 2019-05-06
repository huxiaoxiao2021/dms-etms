package com.jd.bluedragon.dms.utils;
/**
 * 
 * @ClassName: WaybillSignConstants
 * @Description: waybillSign相关的常量定义
 * @author: wuyoude
 * @date: 2019年4月23日 上午10:19:10
 *
 */
public interface WaybillSignConstants {
	/**
	 * 默认打标-0
	 */
	char CHAR_0 = '0';
	/**
	 * 默认打标-1
	 */
	char CHAR_1 = '1';
	/**
	 * B网标识 1、2、3、4、5
	 */
	int POSITION_40 = 40;
	/**
	 * B与C转网模式说明: 0：未转网
		1：C转B
		2：B转C
		3：C转B转C
		4：B转C转B
	 */
	int POSITION_97 = 97;
	/**
	 * 1：C转B
	 */
	char CHAR_97_1 = '1';
	/**
	 * 2:B转C
	 */
	char CHAR_97_2 = '2';
	/**
	 * 3:C转B转C
	 */
	char CHAR_97_3 = '3';
	/**
	 * 4：B转C转B
	 */
	char CHAR_97_4 = '4';
	/**
	 * TC标识
	 */
	int POSITION_89 = 89;
	/**
	 * TC-1
	 */
	char CHAR_89_1 = '1';
	/**
	 * TC-2
	 */
	char CHAR_89_2 = '2';
}
