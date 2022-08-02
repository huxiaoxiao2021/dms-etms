package com.jd.bluedragon.distribution.weightVolume.domain;

/**
 * 称重量方规则常量
 *
 * @author hujiping
 * @date 2021/3/23 10:16 上午
 */
public class WeightVolumeRuleConstant {

    /**
     * cm和m的转换值
     */
    public static final int CM_M_MAGNIFICATION = 100;
    /**
     * cm3和m3的转换值
     */
    public static final int CM3_M3_MAGNIFICATION = 1000000;
    /**
     * 边长最大限制-C网 单位cm
     */
    public static final int SIDE_MAX_LENGTH_C = 300;
    /**
     * 边长最大限制-B网 单位cm
     */
    public static final int SIDE_MAX_LENGTH_B = 150;
    /**
     * 包裹体积最大限制 单位cm3
     *  提示前台的最大限制
     */
    public static final int VOLUME_MAX_LIMIT_C = 400000;
    public static final int VOLUME_MAX_LIMIT_B = 5000000;
    /**
     * 包裹体积最大限制 单位cm3
     *  记录到es时的最大限制
     */
    public static final int VOLUME_MAX_LIMIT_RECORD_C = 4000000;
    /**
     * 包裹重量最大限制 单位kg
     */
    public static final int WEIGHT_MAX_LIMIT_C = 1000;

    public static final int WEIGHT_MAX_LIMIT_B = 5000;

    /**
     * 泡重比
     */
    public static final int FOAM_WEIGHT_RATIO_C = 8000;
    public static final int FOAM_WEIGHT_RATIO_B = 7800;
    public static final int FOAM_WEIGHT_RATIO_MULTIPLE = 5;

    /**
     * 基础校验
     */
    public static final String RESULT_BASIC_MESSAGE = "缺少必要参数!";
    public static final String RESULT_BASIC_MESSAGE_0 = "单号不符合规则!";
    public static final String RESULT_BASIC_MESSAGE_1 = "重量必须大于零!";
    public static final String RESULT_BASIC_MESSAGE_2 = "长必须大于零!";
    public static final String RESULT_BASIC_MESSAGE_3 = "宽必须大于零!";
    public static final String RESULT_BASIC_MESSAGE_4 = "高必须大于零!";
    public static final String RESULT_BASIC_MESSAGE_5 = "体积必须大于零!";

    public static final String RESULT_BASIC_MESSAGE_7 = "箱维度称重体积必须大于零!";

    /**
     * C网特殊校验-确认提示
     */
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_C_0 = "该包裹边长大于%s米！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_C_1 = "该包裹体积大于%s立方米！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_C_2 = "该包裹重量大于%s千克,系统即将记录为%s千克！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_C_3 = "计泡重量（长宽高相乘除以%s）已超过实际重量%s倍！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_C_4 = "该包裹重量大于%s千克,请确认包裹重量是否正确？";

    /**
     * B网特殊校验-强制提示
     */
    public static final String RESULT_SPECIAL_MESSAGE_FORCE_B_1 = "运单下单个包裹体积已超过%s立方米！";
    public static final String RESULT_SPECIAL_MESSAGE_FORCE_B_2 = "泡重比超过%s！";
    public static final String RESULT_SPECIAL_MESSAGE_FORCE_B_3 = "运单下单个包裹重量已超过%skg!";

    public static final String RESULT_SPECIAL_MESSAGE_FORCE_B = "请核实后重新录入!";

    /**
     * B网特殊校验-确认提示
     */
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_B_0 = "体积录入长宽高最大边已超过%s米！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_B_1 = "运单下平均单个包裹超过'%s千克/包裹'或'%s立方/包裹'为超规件！";
    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM_B_3 = "泡重比超过正常范围%s:1到%s:1！";

    public static final String RESULT_SPECIAL_MESSAGE_CONFIRM = "是否继续操作？";


    public static final String RESULT_BASIC_MESSAGE_CONFIRM_8 = "重量为零!";


    public static final String RESULT_SPECIAL_MESSAGE_RECORD= "系统将按%s记录！";

    public static final String RESULT_WEIGHT_INTERCEPT_AFTER_LL = "揽收后无法修改重量体积！";

    /**
     * C网-边长限制：300CM
     */
    private Integer sideMaxLengthC;
    /**
     * B网-边长限制：300CM
     */
    private Integer sideMaxLengthB;
    /**
     * C网-体积最大限制：400000CM3
     */
    private Integer volumeMaxLimitC;
    /**
     * B网-体积最大限制：5000000CM3
     */
    private Integer volumeMaxLimitB;
    /**
     * B网-体积确认值：1000000cm3(包裹维度)
     */
    private Integer volumeMaxLimitConfirmB;
    /**
     * C网-体积记录标准：4000000CM3
     */
    private Integer volumeMaxLimitRecord;
    /**
     * C网-重量最大限制：50KG
     */
    private Integer weightMaxLimitCF;
    /**
     * C网-重量最大限制：1000KG
     */
    private Integer weightMaxLimitCS;
    /**
     * B网-重量最大限制：5000KG(包裹维度)
     */
    private Integer weightMaxLimitB;
    /**
     * B网-重量确认值：200KG(包裹维度)
     */
    private Integer weightMaxLimitConfirmB;
    /**
     * C网-泡重比标准：8000
     */
    private Integer foamWeightRatioC;
    /**
     * B网-泡重比标准：7800
     */
    private Integer foamWeightRatioB;
    /**
     * B网-泡重比确认范围：168-330
     */
    private Integer foamWeightRatioConfirmFloorB;
    private Integer foamWeightRatioConfirmCeilingB;
    /**
     * C网-泡重比标准倍数：5
     */
    private Integer foamWeightRatioMultiple;

    public Integer getSideMaxLengthC() {
        return sideMaxLengthC;
    }

    public void setSideMaxLengthC(Integer sideMaxLengthC) {
        this.sideMaxLengthC = sideMaxLengthC;
    }

    public Integer getSideMaxLengthB() {
        return sideMaxLengthB;
    }

    public void setSideMaxLengthB(Integer sideMaxLengthB) {
        this.sideMaxLengthB = sideMaxLengthB;
    }

    public Integer getVolumeMaxLimitC() {
        return volumeMaxLimitC;
    }

    public void setVolumeMaxLimitC(Integer volumeMaxLimitC) {
        this.volumeMaxLimitC = volumeMaxLimitC;
    }

    public Integer getVolumeMaxLimitB() {
        return volumeMaxLimitB;
    }

    public void setVolumeMaxLimitB(Integer volumeMaxLimitB) {
        this.volumeMaxLimitB = volumeMaxLimitB;
    }

    public Integer getVolumeMaxLimitConfirmB() {
        return volumeMaxLimitConfirmB;
    }

    public void setVolumeMaxLimitConfirmB(Integer volumeMaxLimitConfirmB) {
        this.volumeMaxLimitConfirmB = volumeMaxLimitConfirmB;
    }

    public Integer getVolumeMaxLimitRecord() {
        return volumeMaxLimitRecord;
    }

    public void setVolumeMaxLimitRecord(Integer volumeMaxLimitRecord) {
        this.volumeMaxLimitRecord = volumeMaxLimitRecord;
    }

    public Integer getWeightMaxLimitCF() {
        return weightMaxLimitCF;
    }

    public void setWeightMaxLimitCF(Integer weightMaxLimitCF) {
        this.weightMaxLimitCF = weightMaxLimitCF;
    }

    public Integer getWeightMaxLimitCS() {
        return weightMaxLimitCS;
    }

    public void setWeightMaxLimitCS(Integer weightMaxLimitCS) {
        this.weightMaxLimitCS = weightMaxLimitCS;
    }

    public Integer getWeightMaxLimitB() {
        return weightMaxLimitB;
    }

    public void setWeightMaxLimitB(Integer weightMaxLimitB) {
        this.weightMaxLimitB = weightMaxLimitB;
    }

    public Integer getWeightMaxLimitConfirmB() {
        return weightMaxLimitConfirmB;
    }

    public void setWeightMaxLimitConfirmB(Integer weightMaxLimitConfirmB) {
        this.weightMaxLimitConfirmB = weightMaxLimitConfirmB;
    }

    public Integer getFoamWeightRatioC() {
        return foamWeightRatioC;
    }

    public void setFoamWeightRatioC(Integer foamWeightRatioC) {
        this.foamWeightRatioC = foamWeightRatioC;
    }

    public Integer getFoamWeightRatioB() {
        return foamWeightRatioB;
    }

    public void setFoamWeightRatioB(Integer foamWeightRatioB) {
        this.foamWeightRatioB = foamWeightRatioB;
    }

    public Integer getFoamWeightRatioConfirmFloorB() {
        return foamWeightRatioConfirmFloorB;
    }

    public void setFoamWeightRatioConfirmFloorB(Integer foamWeightRatioConfirmFloorB) {
        this.foamWeightRatioConfirmFloorB = foamWeightRatioConfirmFloorB;
    }

    public Integer getFoamWeightRatioConfirmCeilingB() {
        return foamWeightRatioConfirmCeilingB;
    }

    public void setFoamWeightRatioConfirmCeilingB(Integer foamWeightRatioConfirmCeilingB) {
        this.foamWeightRatioConfirmCeilingB = foamWeightRatioConfirmCeilingB;
    }

    public Integer getFoamWeightRatioMultiple() {
        return foamWeightRatioMultiple;
    }

    public void setFoamWeightRatioMultiple(Integer foamWeightRatioMultiple) {
        this.foamWeightRatioMultiple = foamWeightRatioMultiple;
    }
}
