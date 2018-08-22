package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年08月22日 16时:06分
 */
public class WaybillConsumableExportDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分拣中心编号 */
    private Integer dmsId;

    /** 分拣中心名称 */
    private String dmsName;

    /** 运单号 */
    private String waybillCode;

    /** 确认状态（0：未确认 1：已确认） */
    private Integer confirmStatus;

    /** 修改状态（0：未修改 1：已修改） */
    private Integer modifyStatus;

    /** 揽收人编号 */
    private String receiveUserCode;

    /** 揽收人erp */
    private String receiveUserErp;

    /** 揽收人Name */
    private String receiveUserName;

    /** 确认人编号 */
    private String confirmUserName;

    /** 确认人erp */
    private String confirmUserErp;

    /** 揽收时间 */
    private Date receiveTime;

    /** 确认时间 */
    private Date confirmTime;

    /** 揽收数量 */
    private Integer receiveQuantity;

    /** 确认数量 */
    private Integer confirmQuantity;

    /** 操作人编号 */
    private String operateUserCode;

    /** 操作人erp */
    private String operateUserErp;

    /** 耗材编号 */
    private String code;

    /** 耗材名称 */
    private String name;

    /** 类型 */
    private String type;

    /** 体积 */
    private BigDecimal volume;

    /** 体积系数 */
    private BigDecimal volumeCoefficient;

    /** 规格 */
    private String specification;

    /** 单位 */
    private String unit;

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Integer getModifyStatus() {
        return modifyStatus;
    }

    public void setModifyStatus(Integer modifyStatus) {
        this.modifyStatus = modifyStatus;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public String getReceiveUserErp() {
        return receiveUserErp;
    }

    public void setReceiveUserErp(String receiveUserErp) {
        this.receiveUserErp = receiveUserErp;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getConfirmUserName() {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName) {
        this.confirmUserName = confirmUserName;
    }

    public String getConfirmUserErp() {
        return confirmUserErp;
    }

    public void setConfirmUserErp(String confirmUserErp) {
        this.confirmUserErp = confirmUserErp;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(Integer receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public Integer getConfirmQuantity() {
        return confirmQuantity;
    }

    public void setConfirmQuantity(Integer confirmQuantity) {
        this.confirmQuantity = confirmQuantity;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getVolumeCoefficient() {
        return volumeCoefficient;
    }

    public void setVolumeCoefficient(BigDecimal volumeCoefficient) {
        this.volumeCoefficient = volumeCoefficient;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
