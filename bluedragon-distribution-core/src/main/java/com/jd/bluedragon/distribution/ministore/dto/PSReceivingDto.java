package com.jd.bluedragon.distribution.ministore.dto;

public class PSReceivingDto {
    public static final int STATUS_TEMP_SAVE = 5;
    public static final int BUSINESS_TYPE_FORWARD = 10;
    public static final int BUSINESS_TYPE_REVERSE = 20;
    public static final int BUSINESS_TYPE_THIRD = 30;

    private Long id;
    //    // 业务类型:‘10’正向  "20' 逆向 '30' 三方
//    private int businessType;
    // 封车号
    private String shieldsCarCode;
    // 车号
    private String carCode;
    // 封箱号
    private String sealBoxCode;
    // 箱号
    private String packOrBox;
    // 司机ID-名字
    private String driver;
    // 状态 5：已存   9：提交成功
    private int stateCode;
    // 业务类型:‘10’正向  "20' 逆向 '30' 三方
    private int businessType;
    // 用户ID
    private int userCode;
    // 用户名
    private String userName;
    // 分拣中心ID
    private int siteCode;
    // 分拣中心名称
    private String siteName;
    // 周转箱号
    private String turnoverBoxCode;
    // 运单/包裹/**号
    private String queueNo;
    // 发车批次号
    private String departureCarId;
    // 发车批次号扫枪时间
    private String shieldsCarTime;
    // 操作时间
    private String operateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShieldsCarCode() {
        return shieldsCarCode;
    }

    public void setShieldsCarCode(String shieldsCarCode) {
        this.shieldsCarCode = shieldsCarCode;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getSealBoxCode() {
        return sealBoxCode;
    }

    public void setSealBoxCode(String sealBoxCode) {
        this.sealBoxCode = sealBoxCode;
    }

    public String getPackOrBox() {
        return packOrBox;
    }

    public void setPackOrBox(String packOrBox) {
        this.packOrBox = packOrBox;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(String queueNo) {
        this.queueNo = queueNo;
    }

    public String getDepartureCarId() {
        return departureCarId;
    }

    public void setDepartureCarId(String departureCarId) {
        this.departureCarId = departureCarId;
    }

    public String getShieldsCarTime() {
        return shieldsCarTime;
    }

    public void setShieldsCarTime(String shieldsCarTime) {
        this.shieldsCarTime = shieldsCarTime;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
