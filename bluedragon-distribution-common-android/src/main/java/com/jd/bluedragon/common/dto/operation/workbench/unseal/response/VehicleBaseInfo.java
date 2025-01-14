package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName VehicleBaseInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 19:59
 **/
public class VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -5679990549749395497L;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 业务编码
     */
    private String bizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 线路类型描述
     */
    private String lineTypeName;

    /**
     * 是否抽检
     */
    private Boolean spotCheck;

    /**
     * 任务号
     */
    private String billCode;

    /**
     * 始发站点ID
     */
    private Integer starSiteId;

    /**
     * 始发站点名称
     */
    private String startSiteName;
    
    /**
     * 抽检类型
     */
    private Integer spotCheckType;

    /**
     * 单号标签集合
     */
    private List<LabelOption> tags;

    /**
     * 排序
     */
    private Integer orderIndex;

    /**
     * 0-非特安；1-特安
     */
    private Integer teanFlag;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public Boolean getSpotCheck() {
        return spotCheck;
    }

    public void setSpotCheck(Boolean spotCheck) {
        this.spotCheck = spotCheck;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Integer getStarSiteId() {
        return starSiteId;
    }

    public void setStarSiteId(Integer starSiteId) {
        this.starSiteId = starSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

	public Integer getSpotCheckType() {
		return spotCheckType;
	}

	public void setSpotCheckType(Integer spotCheckType) {
		this.spotCheckType = spotCheckType;
	}

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getTeanFlag() {
        return teanFlag;
    }

    public void setTeanFlag(Integer teanFlag) {
        this.teanFlag = teanFlag;
    }
}
