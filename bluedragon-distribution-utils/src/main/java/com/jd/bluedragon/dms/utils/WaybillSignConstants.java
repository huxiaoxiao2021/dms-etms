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
	 * 运单标识
	 */
	int POSITION_1 = 1;
	/**
	 * 第1位等于 1、4、5、7、8 、A，判断为【自营】运单
	 */
	char CHAR_1_1 = '1';
	char CHAR_1_4 = '4';
	char CHAR_1_5 = '5';
	char CHAR_1_7 = '7';
	char CHAR_1_8 = '8';
	char CHAR_1_A = 'A';
	/**
	 * B网标识 1、2、3、4、5
	 */
	int POSITION_40 = 40;
	char CHAR_40_2='2';
	char CHAR_40_3='3';
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
	 * 卡班
	 */
	int POSITION_80 = 80;
	char CHAR_80_7 = '7';

	/**
	 * B网冷链
	 */
	int POSITION_54 = 54;
	char CHAR_54_2 = '2';
	char CHAR_54_4 = '4';

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
	 * 医药冷链温层
	 */
	int POSITION_43=43;
	char CHAR_43_1 = '1';
	char CHAR_43_2 = '2';
	char CHAR_43_3 = '3';
	char CHAR_43_4 = '4';
	char CHAR_43_5 = '5';
	char CHAR_43_6 = '6';

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

	/**
	 * 毕业寄
	 */
	int POSITION_98 = 98;
	char CHAR_98_1 = '1';
	char CHAR_98_2 = '2';

	/**
	 * 加盟商
	 * 106等于2代表加盟商运单
	 */
	int POSITION_106 = 106;
	char CHAR_106_2 = '2';

    /**
     * 鸡毛信
     * 2或者3
     */
    int POSITION_92 = 92;
    char CHAR_92_2 = '2';
    char CHAR_92_3 = '3';
	/**
	 * waybillsign第31位
	 */
	int POSITION_31 = 31;
	char CHAR_31_9 = '9';
	/**
	 * waybillsign第84位定义
	 */
	int POSITION_84 = 84;
	char CHAR_84_3 = '3';
	/**
	 * waybillsign第57位定义
	 */
	int POSITION_57 = 57;
	/**
	 * 2-代表“KA运营特殊保障”
	 */
	char CHAR_57_2 = '2';
}
