package com.jd.bluedragon.distribution.spotcheck.domain;

/**
 * 抽检拦截常量
 *
 * @author hujiping
 * @date 2021/8/18 3:48 下午
 */
public class SpotCheckConstants {

    public static final String PICTURE_LOOK_URL = "%s/weightAndVolumeCheck/toSearchPicture4MultiplePackage/?waybillCode=%s&siteCode=%s&fromSource=%s&pageNo=1&pageSize=20";

    /**
     * C网默认泡重比：8000
     * */
    public static final int C_VOLUME_RATIO_DEFAULT = 8000;

    /**
     * C网快运使用的泡重比:6000
     */
    public static final int C_VOLUME_RATIO_KY = 6000;

    /**
     * B网特快重货重泡比
     * */
    public static final int B_VOLUME_RATIO_TKZH = 6000;
    /**
     * B网非特快重货重泡比
     * */
    public static final int B_VOLUME_RATIO_NOT_TKZH = 4800;

    /**
     * 重泡比标准值
     * */
    public static final int WEIGHT_VOLUME_RATIO = 7800;
    /**
     * 重量限额，5000KG
     * */
    public static final int WEIGHT_MAX_RATIO = 5000;
    /**
     * 体积限额,5cm³
     * */
    public static final int VOLUME_MAX_RATIO = 5;
    /**
     * cm3和m3的转换值
     */
    public static final long CM3_M3_MAGNIFICATION = 1000000;

    /**
     * 抽检全程跟踪术语
     */
    public static final String SPOT_CHECK_TRACE_REMARK = "重量体积抽检：重量%s公斤，体积%s立方厘米";

    public static final String SPOT_CHECK_ONLY_SUPPORT_ONE_PACK = "重量体积抽查只支持一单一件!";

    public static final String SPOT_CHECK_ONLY_SUPPORT_MORE_PACK = "dws只支持按包裹维度抽检!";

    public static final String SPOT_CHECK_ONLY_PACK = "请录入正确的包裹号!";

    public static final String SPOT_CHECK_ONLY_WAYBILL = "请录入正确的运单号!";

    public static final String SPOT_CHECK_ONLY_SUPPORT_C = "非C网订单!";

    public static final String SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH = "非纯配外单不计入抽检!";

    public static final String SPOT_CHECK_ONLY_SUPPORT_B = "此功能只支持B网运单抽检!";

    public static final String SPOT_CHECK_FORBID_FINISHED_PACK = "此运单已经妥投,禁止抽检!";

    public static final String SPOT_CHECK_ARTIFICIAL_PACK_FORBID_B = "人工包裹维度抽检不支持B网订单!";

    public static final String SPOT_CHECK_MUST_BEFORE_SEND = "此单已发货,禁止抽检!";

    public static final String SPOT_CHECK_PACK_SEND_TRANSFER = "运单下包裹%s已发货,请按包裹维度抽检!";

    public static final String SPOT_CHECK_PACK_SEND = "包裹%s已发货,禁止抽检!";

    public static final String SPOT_CHECK_HAS_SPOT_CHECK = "此单已操作抽检,禁止重复操作!";

    public static final String SPOT_CHECK_SAME_SITE = "同组织禁止上报!";

    public static final String SPOT_CHECK_RESULT_CHANGE = "超标结果发生变化，请重新操作!";

    public static final String SPOT_CHECK_VOLUME_RATE_LIMIT_B = "当前运单:%s重泡比超过%s,请核实后重新录入!";

    public static final String SPOT_CHECK_WEIGHT_LIMIT_B = "当前运单平均单个包裹重量超过%sKG，请核实后重新录入!";

    public static final String SPOT_CHECK_VOLUME_LIMIT_B = "当前运单平均单个包裹体积超过%sm³，请核实后重新录入!";

}
