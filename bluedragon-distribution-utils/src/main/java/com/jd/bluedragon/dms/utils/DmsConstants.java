package com.jd.bluedragon.dms.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static final String CODE_PREFIX_WORK_STATION = "GX";
    public static final String CODE_PREFIX_WORK_STATION_GRID = "CDGX";
    public static final String CODE_PREFIX_WORK_STATION_ATTEND_PLAN = "CDGXPL";
    public static final String CODE_PREFIX_POSITION = "GW";
    public static final String CODE_PREFIX_FM = "JDFM";

    public static final String CODE_PREFIX_JY_GROUP = "G";
    public static final String CODE_PREFIX_JY_GROUP_MEMBER = "GM";

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
     * 空占位符 0
     */
    public static final char FLG_CHAR_DEFAULT = '0';

    /**
     * 箱号正则表达式 旧版 废弃
     */
    @Deprecated
    public static final Pattern RULE_BOXCODE_REGEX_OLD = Pattern.compile("^[A-Z]{2}[A-Z0-9]{14,16}[0-9]{8}$");

    /**
     * 箱号正则表达式 新版
     */
    public static final Pattern RULE_BOXCODE_REGEX = Pattern.compile("^[A-Z]{2}10[0-9]{18}[0-3]{1}[0-9]{1}$");

    /**
     * 箱号正则表达式，新开放-德邦的箱号规则
     */
    public static final Pattern RULE_BOXCODE_REGEX_OPEN_DP = Pattern.compile("^B[0-9]{15}T99999$|^B[0-9]{15}T[0-9]{5}$|^B[0-9]{21,23}$");

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
     * 站点类型-仓
     */
    public static final Integer SITE_TYPE_WMS = 900;
    /**
     * 站点类型-分拣中心
     */
    public static final Integer SITE_TYPE_DMS = 64;
    /**
     * 分拣中心子类型-企配仓
     */
    public static final Integer SITE_SUB_TYPE_EDN = 6540;
    /**
     * 站点类型-站点
     */
    public static final Integer SITE_TYPE_SITE = 4;
    /**
     * 站点类型-车队
     */
    public static final Integer SITE_TYPE_FLEET = 96;

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
    public static final Pattern RULE_BOARD_CODE_REGEX = Pattern.compile("^[B|C][0-9]{14}$");

    /**
     * 逆向箱号正则表达式:
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     */
    @Deprecated
    public static final Pattern RULE_REVERSE_BOXCODE_REGEX_OLD = Pattern.compile("^(TC|TS|TW){1}[A-Z0-9]{14,16}[0-9]{8}$");

    /**
     * 逆向箱号正则表达式: 新版
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     */
    public static final Pattern RULE_REVERSE_BOXCODE_REGEX = Pattern.compile("^[T|G][C|S|X|W]10[0-9]{18}[0-3][0-9]$");

    /**
     * 分拣批次号正则（含亚一）
     */
    public static final String SEND_CODE_ALL_REG = "^[Y|y]?([1-9][0-9]{0,8})-([1-9][0-9]{0,8})-([0-9]{15,17})$";
    /**
     * 分拣批次号正则（含亚一）
     */
    public static final Pattern RULE_SEND_CODE_ALL_REGEX = Pattern.compile(SEND_CODE_ALL_REG);

    /**
     * 终端批次号
     */
    public static final Pattern RULE_TERMINAL_SEND_CODE_ALL_REGEX = Pattern.compile("^R+[0-9]{8,}$");

    /**
     * 任务简码
     */
    public  static final Pattern WORKITEM_SIMPLECODE_REGEX = Pattern.compile("^\\d{14}$");


    /**
     * 车牌号
     */
    public  static final Pattern CARCODE_REGEX = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");

    /**
     * 新批次号正则;
     * 批次号判断批次号是否是：站点（数字）+站点（数字）+时间串（14位数字）+序号（2位数字）+模7余数
     * 模7余数：对 站点第一位+站点第一位+时间串+序列号 取模
     * 必须是17位（时间14位+序号2位+模7余数1位）
     */
    public static final String SEND_CODE_NEW_REG = "^([1-9][0-9]{0,8})-([1-9][0-9]{0,8})-([0-9]{17})$";

    /**
     * 滑道号正则表达式
     */
    public static final Pattern PACKAGE_CODE_CROSSCODE_REGEX = Pattern.compile("^\\w+([-,N])([1-9][0-9]{0,5})([-,S])([0-9A-GI-MO-RT-Z]{1,6})([-,H])(\\w{1,8})$");

    /**
     * 封箱号正则
     */
    public static final String  SEAL_BOX_NO="^[0-9]{8,15}[A-Z]{1}$";

    /**
     * 集货区编码正则
     */
    public static final String RULE_COLLECT_AREA_CODE_REGEX ="^[A-Z]$";

    /**
     * 集货位编码正则
     */
    public static final String RULE_COLLECT_PLACE_CODE_REGEX ="^[A-Z][0-9]{3}$";

    /**
     * 冷链卡班
     */
    public static final String PRODUCT_TYPE_COLD_CHAIN_KB = "LL-KB-M";

    /**
     * 工种类型
     */
    public static final String JOB_TYPE_REGEX = "^[1-6]$";
    /**
     * 身份证正则
     */
    public static final String ID_CARD_NO_REGEX = "(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";

    /**
     * 京仓、非京仓、外仓
     */
    public static final String SPECIAL_MARK1_WAREHOUSE_JD = "京仓";
    public static final String SPECIAL_MARK1_WAREHOUSE_NOT_JD = "非京仓";
    public static final String SPECIAL_MARK1_WAREHOUSE_OUTER ="外仓";

    /**
     * remark特殊标识处理
     * */
    public static final String BAD_WAREHOURSE_FOR_PORT = "保税";
    public static final String PICKUP_CUSTOMER_COMMET = "服务单号：";
    public static final String BUSINESS_ORDER_CODE_REMARK = "商家订单号：";
    public static final String REL_WAYBILL_CODE = " 取件单：";

    /**
     * 保温箱号规则，MZ开头，总长度14到16位
     */
    public static final Pattern WARM_BOX_CODE_REGEX = Pattern.compile("^MZ[A-Z0-9]{12,14}$");

    /**
     * APP版本号正则.
     * 支持0.0.0，11.00.11，1.00.00，11.00.0，11.01.01，01.01.01，2.2
     */
    public static final Pattern APP_VERSION_REGEX = Pattern.compile("^([0-9]\\d|[0-9])(.([0-9]\\d|\\d)){1,2}$");

    /**
     * （微仓）大保温箱号规则，MZ开头，总长度14到16位
     */
    public static final Pattern BIG_WARM_BOX_CODE_REGEX = Pattern.compile("^WCDX[0-9]{10}$");

    public static final Pattern ICE_BOARD_BOX_CODE_REGEX = Pattern.compile("^WCBB[0-9]{10}$");

    /**
     * 预售异常原因-上级编码
     */
    public static final Integer QC_TYPE = 2;
    /**
     * 预售异常原因前缀
     */
    public static final String QC_TYPE_PRE_SELL_CODE_PRE = "24-";
    /**
     * 企配仓拣货单模板-名称
     */
    public static final String TEMPLATE_NAME_EDN_PICKING = "dmsEdnPickingList";
    /**
     * 企配仓拣货单-模板版本配置key
     */
    public static final String TEMPLATE_VERSION_KEY_EDN_PICKING = "print.template.version.dmsEdnPickingList";
    /**
     * 企配仓拣货单模板-默认版本号1
     */
    public static final String TEMPLATE_VERSION_DEFAULT_EDN_PICKING = "1";

    /**
     * 集包袋前缀
     */
    public static final String COLLECTION_BAG_PREFIX = "AD";

    /**
     * 医药直发围板箱前缀
     */
    public static final String COLLECTION_AY_PREFIX = "AY";

    /**
     * 分拣中心类型（数据字典-站点类型）
     */
    public static final List<Integer> SORTING_SITE_TYPE_LIST = Arrays.asList(64);
    /**
     * 退货地址类型6-二次换单退货地址
     */
    public static final int RETURN_BACK_ADDRESS_TYPE_6 = 6;
    /**
     * 微笑符号-^_^
     */
    public static final String  HIDE_SMILE = "^_^";
    /**
     * 微笑符号-****
     */
    public static final String  HIDE_STAR = "****";
    public static final int PHONE_FIRST_NUMBER = 3;//收件人联系方式前几位需要显示
    public static final int PHONE_HIGHLIGHT_NUMBER = 4;//收件人联系方式需要突出显示的位数(即手机尾数要保留的位数)
    public static final int PHONE_LEAST_NUMBER = PHONE_FIRST_NUMBER + PHONE_HIGHLIGHT_NUMBER;//隐藏处理的最小长度
    public static final int ADDRESS_SHOW_LENGTH = 9; //地址信息需要显示的前几位，超过部分用微笑符号替代
    public static final int ADDRESS_HIDE_LENGTH = 6; //地址信息需要隐藏的后几位，用微笑符号替代
    public static final int NAME_SHOW_LENGTH = 1; //姓名信息需要隐藏的位数

    /*车牌号正则表达式*/
    public static final String NUMBERPLATE_CODE = "^([\\u4e00-\\u9fa5]{1}|[0-9]{3,4})[0-9A-Za-z]{6,7}$";

    public static final String BC_BOX_PREFIX = "BC";

    public static final String WJ_BOX_PREFIX = "WJ";

    public static final int PAGE_SIZE_DEFAULT = 10;

    public static final String KEYS_SPLIT = ":";

    public static final String RULE_SAN_WU_CODE = "^(SW|sw)[0-9]{10}$";

    public static final String RULE_SITE_CODE = "[1-9][0-9]{0,8}";
    //站点操作的节点
    public static final Set<Integer> SITE_OPERAT_STATES = new HashSet<Integer>(){{
    	add(60);
    	add(80);
    	add(110);
    	add(150);
    	}};

    /**
     * 退分拣中心-目的名称标识
     */
    public static final String TEXT_SCRAP_DMS_NAME_MARK = "返分拣报废";
    /**
     * 消息类型
     */
    public static final String MESSAGE_TYPE_BAOFEI = "TYPE_BAOFEI";
    public static final String ORDER_TYPE_REVERSE = "20";

    /**
     * 抖音来源标识
     */
    public static final String SOURCE_CODE_DOUYIN  = "cn_jdl_ecp-bytedance";

    /**
     * 运单前缀-抖音-JDAZ
     */
    public static final String WAYBILL_CODE_PRE_DOUYIN  = "JDAZ";

    /**
     * 增值服务编码-京喜送达服务（ed-a-0073）
     */
    public static final String WAYBILL_VAS_JXD  = "ed-a-0073";
    /**
     * 增值服务编码-京喜送达服务-取值key1
     */
    public static final String WAYBILL_VAS_JXD_CARDINFOS  = "cardInfos";
    /**
     * 增值服务编码-京喜送达服务-取值key2-url
     */
    public static final String WAYBILL_VAS_JXD_ATTCHMENTURL  = "attachmentUrl";
    /**
     * 围栏-类型-WAYBILLAOI
     */
    public static final String WAYBILL_FENCE_TYPE_AOI  = "WAYBILLAOI";
    /**
     * 围栏-配送阶段（目前包括 1-揽收和2-派送）
     */
    public static final Integer WAYBILL_FENCE_DELIVERY_STAGE_1  = 1;
    /**
     * 围栏-配送阶段（目前包括 1-揽收和2-派送）
     */
    public static final Integer WAYBILL_FENCE_DELIVERY_STAGE_2  = 2;
    /**
     * 返调度-标识：1
     */
    public static final Integer LOCAL_SCHEDULE = 1;
    /**
     * 返调度-路区：0
     */
    public static final String LOCAL_SCHEDULE_ROAD_CODE = "0";

    public static final String CHINESE_PREFIX = "(^[\u4e00-\u9fa5])([0-9A-Z]+)"; // 汉字开头车牌号
    public static final String CODE_PREFIX = "(^[0-9]{3})[0-9A-Z]+"; 			  // 区号开头车牌号
    public static final String WHITE_SPACE = "\\s*";

    /**
     * 增值服务编码-分批派送（ed-a-0075）
     */
    public static final String WAYBILL_VAS_DELIVERY_MANY_BATCH  = "ed-a-0075";
    public static final String WAYBILL_VAS_DELIVERY_MANY_BATCH_EXECUTE_BATCH_DELIVERY  = "executeBatchDelivery";

    /**
     * 暂存类型枚举值
     */
    public enum PreSellTypeEnum {
        TOWAREHOUSE(1,"预售暂存到仓"),
        TODELIVERY(2,"预售暂存到配");
        //值
        private Integer value;
        //名
        private String name;

        PreSellTypeEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
