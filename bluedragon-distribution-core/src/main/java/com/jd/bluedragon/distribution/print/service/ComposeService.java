package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * Created by wangtingwei on 2015/12/23.
 */
public interface ComposeService {

    Integer PREPARE_SITE_CODE_NOTHING = 0;
    String PREPARE_SITE_NAME_NOTHING = "未定位门店";

    Integer PREPARE_SITE_CODE_OVER_LINE = -100;
    String PREPARE_SITE_NAME_OVER_LINE = "超区分界线";

    Integer PREPARE_SITE_CODE_OVER_AREA = -2;
    String PREPARE_SITE_NAME_OVER_AREA = "超区";

    Integer PREPARE_SITE_CODE_EMS_DIRECT = 999999999;
    String PREPARE_SITE_NAME_EMS_DIRECT = "EMS全国直发";

    String PREPARE_SITE_NAME_FRESH_SEND = "生鲜专送";
    String PREPARE_SITE_NAME_SAMECITY_ARRIVE = "同城当日达";
    String PREPARE_SITE_NAME_SMALL_PACKAGE = "微小件";

    //时效
    String SPECIAL_MARK_SAME_CITY = "同城";
    String SPECIAL_MARK_NEXT_DAY = "次晨";
    String SPECIAL_MARK_LAND_TRANS = "陆运";

    String SPECIAL_MARK_C ="C";
    String SPECIAL_MARK_AIRTRANSPORT ="航";
    String SPECIAL_MARK_ROAD = "陆";
    String SPECIAL_MARK_RAIL = "高";
    String SPECIAL_MARK_SENIOR ="尊";
    String SPECIAL_MARK_ARAYACAK_CABINET = "柜";
    String SPECIAL_MARK_LOCAL_SCHEDULE = "调";
    String SPECIAL_MARK_ARAYACAK_SITE = "提";
    String SPECIAL_MARK_ARAYACAK_DIAN = "店";
    String SPECIAL_MARK_CROWD_SOURCING = "众";
    String SPECIAL_MARK_VALUABLE="安";
    String SPECIAL_MARK_PUBLIC_WELFARE="益";
    String SPECIAL_MARK_FRESH = "鲜";
    String SPECIAL_MARK_SAME = "同";
    String SPECIAL_MARK_AIRTRANSPORT_FILL = "航填";
    String SPECIAL_MARK_FIRST = "优";
    String SPECIAL_MARK_INTERCITY = "城际";
    String SPECIAL_MARK_HELP_DELIVERY="代";
    String SPECIAL_MARK_TRANSFER="传";
    String SPECIAL_MARK_PART_REVERSE = "半退";
    String SPECIAL_MARK_BOX = "鸡毛信箱";
    String SPECIAL_MARK_UTENSIL = "鸡毛信器";
    /**
     * 全球售合包订单标识
     * 当sendpay第108位为1或2或3且senpay第124位为4时，
     */
    String SPECIAL_MARK_SOLD_INTO_PACKAGE = "合";
    /**
     * 城市配送项目：标识
     * sendpay 第146位等于1，且sendpay第124位=3，则打印“集”;
     * sendpay 第146位等于1，但sendpay第124位不为3，则打印“城”，B2B二期把“城”字替换为“B”;
     */
    String CITY_DISTRIBUTION_JI = "集";
    String CITY_DISTRIBUTION_CHENG = "B";
    String CITY_DISTRIBUTION_ZHI = "直";

    /** 安利项目 waybillSign第27位为1的订单 打“半” **/
    String ALLOW_HALF_ACCEPT = "半";


    int AIR_TRANSPORT = 1;
    int ARAYACAK_CABINET = 1;
    int LOCAL_SCHEDULE = 1;

    static String ONLINE_PAYMENT="在线支付";
    static Integer ONLINE_PAYMENT_SIGN = 4;

    //自提点标示
    static int ARAYACAK_SIGN = 64;
    //运输模式 1-分拣集货 2-仓库直发
    Integer TRANS_MODE_JI = 1;
    Integer TRANS_MODE_ZHI = 2;
    
    /**
     * 字符标识
     */
    char CHAR_3 = '3';
    /**
     * 位置标识-36
     */
    int POSITION_36 = 36;
    /**
     * 位置标识-124
     */
    int POSITION_124 = 124;
    /**
     * 位置标识-146
     */
    int POSITION_146 = 146;

    /**
     * waybill_sign第54位等于4 且 第40位等于2或3
     */
    String  SPECIAL_MARK_MEDICINE ="医药";

    String SPECIAL_MARK_MEDICINE_COLD_CHAIN="医药冷链";

    /**
     * 合成器
     * @param waybill           运单信息
     * @param dmsCode           始发分拣中心ID
     * @param targetSiteCode    目的站点ID
     */
    void handle(final PrintWaybill waybill,Integer dmsCode,Integer targetSiteCode);
}
