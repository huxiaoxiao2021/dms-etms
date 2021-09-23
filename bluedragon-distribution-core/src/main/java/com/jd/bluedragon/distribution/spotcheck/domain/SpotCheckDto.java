package com.jd.bluedragon.distribution.spotcheck.domain;

import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckHandlerTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * 抽检实体
 *
 * @author hujiping
 * @date 2021/8/10 11:16 上午
 */
public class SpotCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单号
     */
    private String barCode;
    /**
     * 抽检来源
     * @see SpotCheckSourceFromEnum
     */
    private String spotCheckSourceFrom;
    /**
     * 抽检业务类型
     * @see SpotCheckBusinessTypeEnum
     */
    private Integer spotCheckBusinessType;
    /**
     * 抽检处理类型
     * @see SpotCheckHandlerTypeEnum
     */
    private Integer spotCheckHandlerType;
    /**
     * 重量
     */
    private Double weight;
    /**
     * 长
     */
    private Double length;
    /**
     * 宽
     */
    private Double width;
    /**
     * 高
     */
    private Double height;
    /**
     * 体积
     */
    private Double volume;
    /**
     * 区域ID
     */
    private Integer orgId;
    /**
     * 区域名称
     */
    private String orgName;
    /**
     * 站点ID
     */
    private Integer siteCode;
    /**
     * 站点名称
     */
    private String siteName;
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
     * 抽检维度类型
     * @see SpotCheckDimensionEnum
     */
    private Integer dimensionType;
    /**
     * 超标状态
     */
    private Integer excessStatus;
    /**
     * 图片链接
     */
    private Map<String, String> pictureUrls;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSpotCheckSourceFrom() {
        return spotCheckSourceFrom;
    }

    public void setSpotCheckSourceFrom(String spotCheckSourceFrom) {
        this.spotCheckSourceFrom = spotCheckSourceFrom;
    }

    public Integer getSpotCheckBusinessType() {
        return spotCheckBusinessType;
    }

    public void setSpotCheckBusinessType(Integer spotCheckBusinessType) {
        this.spotCheckBusinessType = spotCheckBusinessType;
    }

    public Integer getSpotCheckHandlerType() {
        return spotCheckHandlerType;
    }

    public void setSpotCheckHandlerType(Integer spotCheckHandlerType) {
        this.spotCheckHandlerType = spotCheckHandlerType;
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

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Integer getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(Integer dimensionType) {
        this.dimensionType = dimensionType;
    }

    public Integer getExcessStatus() {
        return excessStatus;
    }

    public void setExcessStatus(Integer excessStatus) {
        this.excessStatus = excessStatus;
    }

    public Map<String, String> getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(Map<String, String> pictureUrls) {
        this.pictureUrls = pictureUrls;
    }
}
