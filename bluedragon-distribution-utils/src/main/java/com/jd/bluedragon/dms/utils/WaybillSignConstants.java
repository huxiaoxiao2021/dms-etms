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

	/**
	 * B网冷链
	 */
	int POSITION_54 = 54;
	char CHAR_54_2 = '2';

	/**
	 * 京仓/非京仓
	 */
	char CHAR_89_3 = '3';
	char CHAR_89_4 = '4';

	/**
	 * 是否代客下单(营业厅)
     */
	int REPLACE_ORDER_POSITION_62 = 62;
    /**
     * 代客下单
     */
	char REPLACE_ORDER_CHAR_62_1 = '1';

	/**
	 * C端收运费
     */
	int C_COLLECT_FEES_POSITION_25 = 25;
    /**
     *寄付运费
     */
	char C_COLLECT_FEES_CHAR_25_3 = '3';

	/**
	 * 逆向运单类型（外单）
     */
	int BACKWARD_TYPE_POSITION_61 = 61;
    /**
     * 正向
     */
	char BACKWARD_TYPE_NO_CHAR_61_0 = '0';


}
