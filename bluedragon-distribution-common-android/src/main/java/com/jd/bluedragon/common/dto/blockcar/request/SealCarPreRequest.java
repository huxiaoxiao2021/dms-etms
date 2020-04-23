package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 封车前置校验请求对象
 *
 * @author: hujiping
 * @date: 2020/3/19 21:56
 */
public class SealCarPreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车功能
     *  10:封车
     *  20:解封车
     *  @see com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarFunctionEnum
     * */
    private Integer status;
    /**
     * 封车方式
     *  10：按运力
     *  20：按任务
     *  @see com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum
     * */
    private Integer sealCarType;
    /**
     * 封车来源：
     *  10：普通
     *  20：传摆
     *  @see com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum
     * */
    private Integer sealCarSource;
    /**
     * 任务简码
     */
    private String itemSimpleCode;
    /**
     * 运力编码
     * */
    private String transportCode;
    /**
     * 车牌号
     * */
    private String vehicleNumber;
    /**
     * 封车号
     * */
    private String sealCode;
    /**
     * 批次号
     * */
    private String sendCode;
    /**
     * 重量
     */
    private Double weight;
    /**
     * 体积
     */
    private Double volume;
    /**
     * 托盘数
     * */
    private Integer palletCount;
    /**
     * 操作人ERP
     * */
    private String operateErp;
    /**
     * 操作人姓名
     * */
    private String operateName;
    /**
     * 操作站点
     * */
    private Integer createSiteCode;
    /**
     * 操作站点名称
     * */
    private String createSiteName;
    /**
     * 操作时间
     * */
    private Date operateTime;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public Integer getSealCarSource() {
        return sealCarSource;
    }

    public void setSealCarSource(Integer sealCarSource) {
        this.sealCarSource = sealCarSource;
    }

    public String getItemSimpleCode() {
        return itemSimpleCode;
    }

    public void setItemSimpleCode(String itemSimpleCode) {
        this.itemSimpleCode = itemSimpleCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(Integer palletCount) {
        this.palletCount = palletCount;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
