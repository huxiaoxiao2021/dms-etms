package com.jd.bluedragon.distribution.material.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName MaterialSendDto
 * @Description
 * @Author wyh
 * @Date 2020/3/16 14:05
 **/
public class MaterialSendDto implements Serializable {

    private static final long serialVersionUID = 1150785687798341845L;

    /**
     * 物资编号
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 发货数量
     */
    private Integer sendNum;

    /**
     * 总重量（KG）
     */
    private BigDecimal totalWeight;

    /**
     * 总体积（立方厘米）
     */
    private BigDecimal totalVolume;

    /**
     * 发货批次号
     */
    private String batchCode;

    /**
     * 操作人ERP
     */
    private String erpUserCode;

    /**
     * 操作人
     */
    private String erpUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 操作人所在分拣中心
     */
    private Long createSiteCode;

    /**
     * 操作人所在分拣中心名称
     */
    private String createSiteName;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getErpUserCode() {
        return erpUserCode;
    }

    public void setErpUserCode(String erpUserCode) {
        this.erpUserCode = erpUserCode;
    }

    public String getErpUserName() {
        return erpUserName;
    }

    public void setErpUserName(String erpUserName) {
        this.erpUserName = erpUserName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }
}
