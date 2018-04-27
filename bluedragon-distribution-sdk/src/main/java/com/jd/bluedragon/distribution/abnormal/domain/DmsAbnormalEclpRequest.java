package com.jd.bluedragon.distribution.abnormal.domain;

/**
 * @author tangcq
 * @ClassName: DmsAbnormalEclpRequest
 * @Description: MQ用
 * @date 2018年4月11日18:30:18
 */
public class DmsAbnormalEclpRequest {

    //二级异常编码
    public static final String DMSABNORMALECLP_EXPTTWOLEVEL_CODE = "300400"; // 异常二级编码
    public static final String DMSABNORMALECLP_EXPTTWOLEVEL_NAME = "库房拒收"; // 库房拒收
    //三级异常编码
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_ABNORMAL_CODE = "300400001"; // 内物异常
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_ABNORMAL_NAME = "内物异常"; // 内物异常
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_DAMAGED_CODE = "300400002"; // 破损
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_DAMAGED_NAME = "破损"; // 破损
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_NONE_CODE = "300400003"; // 无理由
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_NONE_NAME = "无理由"; // 无理由
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_OTHER_CODE = "300400004"; // 其他原因
    public static final String DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_OTHER_ = "其他原因"; // 其他原因

    /**
     * 运单
     */
    private String waybillCode;
    /**
     * 责任中心code
     */
    private String deptCode;
    /**
     * 责任中心名称
     */
    private String deptName;
    /**
     * 异常名称 同异常三级名称
     */
    private String expName;
    /**
     * 异常时间
     */
    private String exptCreateTime;

    /**
     * 异常二级编码
     */
    private String exptTwoLevel;
    /**
     * 异常二级名称
     */
    private String exptTwoLevelName;
    /**
     * 异常三级编码
     */
    private String exptThreeLevel;
    /**
     * 异常三级名称
     */
    private String exptThreeLevelName;
    /**
     * 机构 对应区域信息
     */
    private String orgNo;


    /**
     * 待协商原因说明
     */
    private String consultMark;

    /**
     * 商家编码
     */
    private String busiId;
    /**
     * 商家名称
     */
    private String busiName;
    /**
     * 商家电话
     */
    private String telephone;

    /**
     * 创建人ERP
     */
    private String createUserCode;

    /**
     * 创建人名称
     */
    private String createUserName;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = expName;
    }

    public String getExptCreateTime() {
        return exptCreateTime;
    }

    public void setExptCreateTime(String exptCreateTime) {
        this.exptCreateTime = exptCreateTime;
    }

    public String getExptTwoLevel() {
        return exptTwoLevel;
    }

    public void setExptTwoLevel(String exptTwoLevel) {
        this.exptTwoLevel = exptTwoLevel;
    }

    public String getExptTwoLevelName() {
        return exptTwoLevelName;
    }

    public void setExptTwoLevelName(String exptTwoLevelName) {
        this.exptTwoLevelName = exptTwoLevelName;
    }

    public String getExptThreeLevel() {
        return exptThreeLevel;
    }

    public void setExptThreeLevel(String exptThreeLevel) {
        this.exptThreeLevel = exptThreeLevel;
    }

    public String getExptThreeLevelName() {
        return exptThreeLevelName;
    }

    public void setExptThreeLevelName(String exptThreeLevelName) {
        this.exptThreeLevelName = exptThreeLevelName;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getConsultMark() {
        return consultMark;
    }

    public void setConsultMark(String consultMark) {
        this.consultMark = consultMark;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
