package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;
import java.util.Map;

/**
 * 人工抽检请求对象
 *
 * @author hujiping
 * @date 2021/8/20 11:20 上午
 */
public class ArtificialSpotCheckRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 扫描条码
     *  运单号或包裹号
     */
    private String barCode;

    /**
     * 重量 kg
     */
    private Double weight;

    /**
     * 长 cm
     */
    private Double length;

    /**
     * 宽 cm
     */
    private Double width;

    /**
     * 高 cm
     */
    private Double height;

    /**
     * 体积 cm³
     */
    private Double volume;

    /**
     * 区域ID
     */
    private Integer operateOrgId;

    /**
     * 区域名称
     */
    private String operateOrgName;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人ID
     */
    private Integer operateUserId;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 是否运单维度抽检
     */
    private Boolean isWaybillSpotCheck = false;

    /**
     * 超标状态
     */
    private Integer excessStatus;

    /**
     * 图片url
     *  分为：重量、长、宽、高、面单
     */
    private Map<String, String> pictureUrlsMap;

    /**
     * 超标类型
     */
    private Integer excessType;
    /**
     * 是否改造后的抽检
     */
    private Boolean isReformedSpotCheck = false;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getOperateOrgId() {
        return operateOrgId;
    }

    public void setOperateOrgId(Integer operateOrgId) {
        this.operateOrgId = operateOrgId;
    }

    public String getOperateOrgName() {
        return operateOrgName;
    }

    public void setOperateOrgName(String operateOrgName) {
        this.operateOrgName = operateOrgName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Boolean getIsWaybillSpotCheck() {
        return isWaybillSpotCheck;
    }

    public void setIsWaybillSpotCheck(Boolean waybillSpotCheck) {
        isWaybillSpotCheck = waybillSpotCheck;
    }

    public Integer getExcessStatus() {
        return excessStatus;
    }

    public void setExcessStatus(Integer excessStatus) {
        this.excessStatus = excessStatus;
    }

    public Integer getExcessType() {
        return excessType;
    }

    public void setExcessType(Integer excessType) {
        this.excessType = excessType;
    }

    public Map<String, String> getPictureUrlsMap() {
        return pictureUrlsMap;
    }

    public void setPictureUrlsMap(Map<String, String> pictureUrlsMap) {
        this.pictureUrlsMap = pictureUrlsMap;
    }

    public Boolean getIsReformedSpotCheck() {
        return isReformedSpotCheck;
    }

    public void setIsReformedSpotCheck(Boolean reformedSpotCheck) {
        isReformedSpotCheck = reformedSpotCheck;
    }
}
