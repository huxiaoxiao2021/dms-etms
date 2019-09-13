package com.jd.bluedragon.distribution.transport.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Created by lixin39 on 2018/1/3.
 */
public class ArPdaSendRegister {

    private Integer taskType;

    private String packageCode;

    private String waybillCode;

    /**
     * 目前用于存储预计起飞/发车日期
     */
    private String boxCode;

    private Integer receiveSiteCode;

    /**
     * 封箱号|封签号
     */
    private String sealBoxCode;

    /**
     * 封车号
     */
    private String shieldsCarCode;

    private String sendUser;

    /**
     * 周转箱号
     */
    private String turnoverBoxCode;

    private String exceptionType;

    private Long id;

    /**
     * 体积
     */
    private BigDecimal volume;

    /*********************************** 以下为空铁需求字段 ***********************************/

    /**
     * 航空单号
     */
    private String airNo;

    /**
     * 运力名称
     */
    private String transName;

    /**
     * 铁路站序
     */
    private String railwayNo;

    /**
     * 发货件数
     */
    private Integer num;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 备注
     */
    private String demo;

    /**
     * 车型
     */
    private Integer operateType;

    /**
     * 车号
     */
    private String carCode;

    /**
     * 发货批次
     */
    private String batchCode;

    private Integer userCode;

    private String userName;

    /**
     * 操作人ERP
     */
    private String sendUserCode;

    private Integer siteCode;

    private String siteName;

    private Date operateTime;

    /**
     * 货物类型
     */
    private String goodsType;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getSealBoxCode() {
        return sealBoxCode;
    }

    public void setSealBoxCode(String sealBoxCode) {
        this.sealBoxCode = sealBoxCode;
    }

    public String getShieldsCarCode() {
        return shieldsCarCode;
    }

    public void setShieldsCarCode(String shieldsCarCode) {
        this.shieldsCarCode = shieldsCarCode;
    }

    public String getSendUserCode() {
        return sendUserCode;
    }

    public void setSendUserCode(String sendUserCode) {
        this.sendUserCode = sendUserCode;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getAirNo() {
        return airNo;
    }

    public void setAirNo(String airNo) {
        this.airNo = airNo;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getRailwayNo() {
        return railwayNo;
    }

    public void setRailwayNo(String railwayNo) {
        this.railwayNo = railwayNo;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }
}
