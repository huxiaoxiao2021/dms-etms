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
	char CHAR_1_2 = '2';
	char CHAR_1_4 = '4';
	char CHAR_1_5 = '5';
	char CHAR_1_7 = '7';
	char CHAR_1_8 = '8';
	char CHAR_1_A = 'A';
	/**
	 * 第一位等于T，判断为【自营逆向单】
	 */
	char CHAR_1_T = 'T';
	/**
	 * 运单标识第4位
	 */
	int POSITION_4 = 4;
	/**
	 * 签单返回，waybillSign第4位：1,2,3,4,9
	 */
	char CHAR_4_1 = '1';
	char CHAR_4_2 = '2';
	char CHAR_4_3 = '3';
	char CHAR_4_4 = '4';
	char CHAR_4_9 = '9';

	/**
	 * 改址标位 8 位  1 2 3
	 */
	int POSITION_8 = 8;
	char CHAR_8_1='1';
	char CHAR_8_2='2';
	char CHAR_8_3='3';

	/**
	 * B网标识 1、2、3、4、5
	 */
	int POSITION_40 = 40;
	char CHAR_40_0='0';
	char CHAR_40_1='1';
	char CHAR_40_2='2';
	char CHAR_40_3='3';
	char CHAR_40_6='6';
	char CHAR_40_7='7';
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
	 *
	 */
	char CHAR_89_0 = '0';
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
	char CHAR_80_8 = '8';
	/**
	 * 城配
	 */
	char CHAR_80_6 = '6';
	/*
	* 航空重货
	* */
	char CHAR_80_C = 'C';

	int POSITION_118 = 118;
	/**
	 * 共配
	 */
	char CHAR_118_1 = '1';
    char CHAR_118_0 = '0';

	/**
	 * 隐藏收件人
	 */
	int POSITION_37 = 37;
	char CHAR_37_0 = '0';
	/**
	 * 隐藏寄件人
	 */
	int POSITION_47 = 47;
	char CHAR_47_0 = '0';
	/**
	 * 特准包裹
	 */
	char CHAR_80_9 = '9';

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
	 * 逆向运单类型（外单）
     */
	int POSITION_61 = 61;



    /**
     * 正向
     */
	char CHAR_61_0 = '0';
	/**
	 * 逆向  1236
	 */
	char CHAR_61_1 = '1';
	char CHAR_61_2 = '2';
	char CHAR_61_3 = '3';
	char CHAR_61_6 = '6';

	/**
	 * 逆向类型(运单打标)
	 */
	int POSITION_15 = 15;
	/**
	 * 正向
	 */
	char CHAR_15_0 = '0';

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
	 * 时效
	 * 2 同城
	 * 3 次晨
	 */
	int POSITION_116 = 116;
	char CHAR_116_0 = '0';
	char CHAR_116_1 = '1';	
	char CHAR_116_2 = '2';
	char CHAR_116_3 = '3';
	char CHAR_116_4 = '4';
	
	/**
	 * waybillsign第31位
	 */
	int POSITION_31 = 31;
	/**
	 * 是否是特惠送
	 * */
	char CHAR_31_0 = '0';
	/**
	 * 是否是次晨达
	 * */
	char CHAR_31_1 = '1';
	/**
	 * 是否是同城当日达
	 * */
	char CHAR_31_2 = '2';
	char CHAR_31_3 = '3';	
	char CHAR_31_4 = '4';	
	char CHAR_31_5 = '5';
	char CHAR_31_6 = '6';
	char CHAR_31_7 = '7';
	char CHAR_31_8 = '8';
	char CHAR_31_9 = '9';	
	char CHAR_31_A = 'A';
	char CHAR_31_B = 'B';
	char CHAR_31_C = 'C';
	char CHAR_31_D = 'D';
	char CHAR_31_F = 'F';
	char CHAR_31_G = 'G';
	char CHAR_31_H = 'H';
	/**
	 * waybillsign第36位
	 */
	int POSITION_36 = 36;
	char CHAR_36_4 = '4';


	/**
	 * waybillsign第84位定义
	 */
	int POSITION_84 = 84;
	char CHAR_84_3 = '3';
	char CHAR_84_2 = '2';
	char CHAR_84_1 = '1';
	/**
	 * waybillsign第57位定义
	 */
	int POSITION_57 = 57;
	/**
	 * 2-代表“KA运营特殊保障”
	 */
	char CHAR_57_2 = '2';

	/**
	 * 信任商家 1
	 */
	int POSITION_56 = 56;
	char CHAR_56_1 = '1';

	/**
     * B2C纯配订单
     * */
	char CHAR_1_3 = '3';
	char CHAR_1_6 = '6';
	char CHAR_1_9 = '9';
	char CHAR_1_K = 'K';
	char CHAR_1_Y = 'Y';
	int POSITION_28 = 28;
	char CHAR_28_0 = '0';
	char CHAR_28_1 = '1';
	/**
	 * 是否是月结订单
	 * */
	int POSITION_25 = 25;
	char CHAR_25_0 = '0';
	char CHAR_25_1 = '1';
	char CHAR_25_2 = '2';
	char CHAR_25_3 = '3';
	char CHAR_25_4 = '4';
	char CHAR_25_5 = '5';
	
	int POSITION_16 = 16;
	char CHAR_16_1 = '1';
	char CHAR_16_2 = '2';
	char CHAR_16_3 = '3';	
	char CHAR_16_4 = '4';
	char CHAR_16_7 = '7';
	char CHAR_16_8 = '8';
	/**
	 * 寄付月结 waybillSign第14位为0
	 * 自提 waybillSign第79位为2
	 * */
	int POSITION_14 = 14;
	char CHAR_14_0 = '0';
	int POSITION_79 = 79;
	char CHAR_79_2 = '2';


	/**
	 * 经济网 62位为8
	 */
	int BUSINESS_ENET_POSITION_62 = 62;
	char BUSINESS_ENET_CHAR_62_8 = '8';

	/**
	 * 生鲜专送：55位为1
	 */
	int POSITION_55 = 55;
	char CHAR_55_0 = '0';	
	char CHAR_55_1 = '1';

	/**
	 * WaybillSign第33位,9-物业代收 A-保安室代收 B-门口存放 C-用户指定
	 */
	int POSITION_33 = 33;
	char CHAR_33_9 = '9';
	char CHAR_33_A = 'A';
	char CHAR_33_B = 'B';
	char CHAR_33_C = 'C';
	/**
	 * WaybillSign第128位,1-企配仓
	 */
	int POSITION_128 = 128;
	char CHAR_128_1 = '1';

	/**
	 * 直投自提柜提示
	 * 5-自营自提柜
	 * 6-深度合作自提柜（如丰巢
	 * 7-合作自提代收点（便民点
	 */
	int POSITION_23 = 23;
	char CHAR_23_5 = '5';
	char CHAR_23_6 = '6';
	char CHAR_23_7 = '7';
	/**
	 * 29
	 */
	int POSITION_29 = 29;
	char CHAR_29_8 = '8';
	char CHAR_29_2 = '2';
	char CHAR_29_B = 'B';
	char CHAR_29_C = 'C';
	char CHAR_29_D = 'D';
	char CHAR_29_E = 'E';
	/**
	 * 86
	 */
	int POSITION_86 = 86;
	char CHAR_86_2 = '2';
	char CHAR_86_3 = '3';
	char CHAR_86_0 = '0';

	/**
	 * 66
	 */
	int POSITION_66 = 66;
	char CHAR_66_0 = '0';
	char CHAR_66_1 = '1';
	char CHAR_66_2 = '2';
	char CHAR_66_3 = '3';

	/**
	 * 18
	 */
	int POSITION_18 = 18;
	char CHAR_18_C = 'C';
	

	
	/**
	 * 71
	 */
	int POSITION_71 = 71;
	char CHAR_71_2 = '2';

	/**
	 * 53
	 */
	int POSITION_53 = 53;
	char CHAR_53_1 = '1';

	/**
	 * 99
	 */
	int POSITION_99 = 99;
	char CHAR_99_1 = '1';


	/**
	 * 140
	 */
	int POSITION_140 = 140;
	char CHAR_140_2 = '2';
}
