package com.jd.bluedragon.dms.utils;

import java.util.regex.Pattern;

/**
 * @author tangchunqing
 * @Description: 静态变量
 * @date 2018年10月12日 16时:58分
 */
public class DmsConstants {

    /**
     * 业务类型
     **/
    public static final int BUSSINESS_TYPE_POSITIVE = 10;//正向
    public static final int BUSSINESS_TYPE_REVERSE = 20;//逆向
    public static final int BUSSINESS_TYPE_THIRD_PARTY = 30;
    public static final int BUSSINESS_TYPE_POP = 40;
    public static final int BUSSINESS_TYPE_InFactory = 41;
    public static final int BUSSINESS_TYPE_TRANSFER = 50;
    public static final int BUSSINESS_TYPE_SITE = 60;//配送员上门接货
    public static final int BUSSINESS_TYPE_BDB = 51;//夺宝岛
    public static final int BUSSINESS_TYPE_OEM = 52;//OEM
    public static final int BUSSINESS_TYPE_FC = 53;//返仓
    public static final int BUSSINESS_TYPE_RCD = 57;//反调度重打包裹
    public static final int BUSSINESS_TYPE_NEWTRANSFER = 50; //包裹交接类型合一 所有类型都是50

    public static final int BUSSINESS_TYPE_ZY_ORDERTYPE = 0;//自营的订单类型/协同仓
    public static final int BUSSINESS_TYPE_DBD_ORDERTYPE = 2;//夺宝岛的
    public static final int BUSSINESS_TYPE_OEM_52 = 52; //协同仓storeid
    public static final int BUSSINESS_TYPE_OEM_58 = 58; //协同仓storeid
    public static final int BUSSINESS_TYPE_OEM_59 = 59; //协同仓storeid

    public static final String PACKAGE_SEPARATOR = "-";
    public static final String PACKAGE_IDENTIFIER_SUM = "S";
    public static final String PACKAGE_IDENTIFIER_NUMBER = "N";
    public static final String PACKAGE_IDENTIFIER_PICKUP = "W";
    public static final String PACKAGE_WAIDAN = "V";

    public static final String AO_BATCH_CODE_PREFIX = "Y";
    public static final String PACKAGE_IDENTIFIER_REPAIR = "VY";
    public static final String SOURCE_CODE_ECLP = "ECLP";
    public static final String BUSI_ORDER_CODE_PRE_ECLP = "ESL";
    public static final String BUSI_ORDER_CODE_QWD = "QWD";
    public static final String SOURCE_CODE_CLPS = "CLPS";
    public static final String BUSI_ORDER_CODE_PRE_CLPS = "CSL";

    /**
     * 始发道口号类型-1-普通
     */
    public static final Integer ORIGINAL_CROSS_TYPE_GENERAL = 1;
    /**
     * 始发道口号类型-2-航空
     */
    public static final Integer ORIGINAL_CROSS_TYPE_AIR = 2;
    /**
     * 始发道口号类型-3-填仓
     */
    public static final Integer ORIGINAL_CROSS_TYPE_FILL = 3;

    /**
     * 字符类型yn标识,1-是
     */
    public static final char FLG_CHAR_YN_Y = '1';

    /**
     * 箱号正则表达式
     */
    public static final Pattern RULE_BOXCODE_REGEX = Pattern.compile("^[A-Z]{2}[A-Z0-9]{14,16}[0-9]{8}$");

    /**
     * 返单号正则表达式
     */
    public static final Pattern RULE_F_WAYBILL_CODE_REGEX = Pattern.compile("^F[0-9]{11}$");


    /**
     * 拍卖订单（多宝岛）
     */
    public static final Integer AUCTION = 2;
    /**
     * 站点类型-B商家
     */
    public static final Integer SITE_TYPE_BIZ = 1024;

    /**
     * 买卖宝 的标识
     */
    public static final char MMB_SELF_MARK = '9';
    public static final String MMB_V_MARK = "009";
    /**
     * 小米运单青龙商家编码
     * */
    public static final String busiCodeOfMillet = "010K258778";
    /**
     * 最多包裹数
     */
    public static int MAX_NUMBER = 20000;

    /**
     * 板号正则表达式
     */
    public static final Pattern RULE_BOARD_CODE_REGEX = Pattern.compile("^B[0-9]{14}$");

    /**
     * 逆向箱号正则表达式:
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     */
    public static final Pattern RULE_REVERSE_BOXCODE_REGEX = Pattern.compile("^(TC|TS|TW){1}[A-Z0-9]{14,16}[0-9]{8}$");


    /**
     * 批次号正则
     */
    public static final String SEND_CODE_REG = "^\\d+-\\d+-\\d{15,17}$"; //批次号正则
}
