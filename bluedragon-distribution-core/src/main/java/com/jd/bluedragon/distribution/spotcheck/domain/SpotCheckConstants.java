package com.jd.bluedragon.distribution.spotcheck.domain;

/**
 * 抽检拦截常量
 *
 * @author hujiping
 * @date 2021/8/18 3:48 下午
 */
public class SpotCheckConstants {

    /**
     * 抽检已上传图片缓存前缀
     */
    public static final String UPLOADED_PIC_PREFIX = "spotCheck-uploadedPic:%s|%s";

    /**
     * 抽检下发来源：分拣 1
     */
    public static final Integer DMS_SPOT_CHECK_ISSUE = 1;
    /**
     * 抽检来源标识
     *  人工：DMS-MSI 设备：DMS-DWS
     */
    public static final String EQUIPMENT_SPOT_CHECK = "DMS-DWS";
    public static final String ARTIFICIAL_SPOT_CHECK = "DMS-MSI";

    public static final String PICTURE_LOOK_URL = "%s/weightAndVolumeCheck/toSearchPicture4MultiplePackage/?waybillCode=%s&siteCode=%s&fromSource=%s&pageNo=1&pageSize=20";

    public static final String SPOT_CHECK_PICTURE_LOOK_URL = "%s/spotCheckReport/toSearchPicture/?waybillCode=%s&reviewSiteCode=%s&reviewSource=%s";

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
     * 泡重比最大值
     * */
    public static final double VOLUME_WEIGHT_RATIO_MAX = 0.2;

    /**
     * 泡重比最小值
     */
    public static final double VOLUME_WEIGHT_RATIO_MIN = 0.0005;
    /**
     * 重量限额，62500KG(运单维度)
     * */
    public static final int WEIGHT_MAX_RATIO = 62500;
    /**
     * 体积限额,300m³（运单维度）
     * */
    public static final int VOLUME_MAX_RATIO = 300;
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

    public static final String SPOT_CHECK_FORBID = "未知业务类型不支持，禁止操作!";

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
    public static final String SPOT_CHECK_VOLUME_RATE_LIMIT_B_PACK = "当前包裹:%s重泡比超过%s,请核实后重新录入!";

    public static final String SPOT_CHECK_WEIGHT_LIMIT_B = "当前运单平均单个包裹重量超过%sKG，请核实后重新录入!";
    public static final String SPOT_CHECK_WEIGHT_LIMIT_B_PACK = "当前包裹重量超过%sKG，请核实后重新录入!";

    public static final String SPOT_CHECK_VOLUME_LIMIT_B = "当前运单平均单个包裹体积超过%sm³，请核实后重新录入!";
    public static final String SPOT_CHECK_VOLUME_LIMIT_B_PACK = "当前包裹体积超过%sm³，请核实后重新录入!";

    public static final String SPOT_CHECK_PACK_SEND_REFORM = "运单下包裹%s已发货,不支持人工抽检!";
    public static final String SPOT_CHECK_PACK_SPOT_CHECK_REFORM = "运单下包裹%s已抽检,不支持人工抽检!";
    public static final String SPOT_CHECK_PACK_SPOT_SEND_NOT_CHECK = "运单下有包裹已发货未抽检,禁止操作!!";

    public static final String SPOT_CHECK_EXCESS_LIMITATION = "货物超大超重，请注意重量体积是否有误！";
    public static final String SPOT_CHECK_NOT_MEET_THEORETICAL_VALUE = "您录入的重量或者体积可能错误，不符合货物理论大小，请仔细核对!";
    public static final String WOODEN_FRAME_NOT_SUPPORT_ARTIFICIAL_SPOT_CHECK = "该运单存在打木架服务，暂不支持称重举报!";

    /**
     * AI识别图片类型
     *  1：重量 2：面单
     */
    public static final Integer SPOT_CHECK_AI_TYPE_WEIGHT = 1;
    public static final Integer SPOT_CHECK_AI_TYPE_FACE = 2;
    public static final String SPOT_CHECK_AI_WEIGHT_HIT = "录入重量:%s和图片识别重量:%s不一致，请重新上传!";
    public static final String SPOT_CHECK_AI_FACE_HIT = "录入的运单号:%s和图片识别单号:%s不一致，请重新上传!";
    /**
     * 超标类型
     *  重量超标：1 体积超标：2
     */
    public static final Integer EXCESS_TYPE_WEIGHT = 1;
    public static final Integer EXCESS_TYPE_VOLUME = 2;
    /**
     * 图片识别异常编码
     */
    public static final Integer SPOT_CHECK_AI_EXC_CODE = 40001;

    /**
     * 打木架服务耗材编码
     */
    public static final String WOODEN_FRAME_TYPE_CODE = "TY003";

}
