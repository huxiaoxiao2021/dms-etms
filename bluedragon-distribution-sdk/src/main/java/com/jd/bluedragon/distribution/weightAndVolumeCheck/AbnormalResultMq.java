package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName: AbnormalResultMq
 * @Description: 发送异常结果数据
 * @author: hujiping
 * @date: 2019/4/22 14:00
 */
public class AbnormalResultMq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 来源系统
     * 1分拣抽检
     * 2终端抽检
     * 3计费
     */
    private String from;
    /**
     * 去向系统
     * 1判责
     * 2质控 (计费全发质控，其余系统可以为空，fxm根据dutyType判断发送到哪个系统)
     */
    private String to;
    /**
     * id
     */
    private Long id;
    /**
     * 数据来源
     */
    private Integer source;
    /**
     * 异常类型
     */
    private Integer errorType;
    /**
     * 运单号
     */
    private String billCode;
    /**
     * 商家id
     */
    private	Integer	 businessObjectId;
    /**
     * 商家名称
     */
    private	String	 businessObject;
    /**
     * 责任类型
     * @See com.jd.etms.finance.enums.DutyTypeEnum
     */
    private Integer dutyType;

    /**
     * 责任一级id
     */
    private String firstLevelId;

    /**
     * 责任一级名称
     */
    private String firstLevelName;
    /**
     * 责任二级id
     */
    private String secondLevelId;

    /**
     * 责任二级名称
     */
    private String secondLevelName;
    /**
     * 责任三级id
     */
    private String threeLevelId;

    /**
     * 责任三级名称
     */
    private String threeLevelName;
    /**
     * 责任人erp
     */
    private String dutyErp;
    /**
     * 计费重量
     */
    private BigDecimal weight;
    /**
     * 计费体积
     */
    private BigDecimal volume;

    /** 始发地城市id */
    private Integer deliverCityId;
    /** 始发地城市名称 */
    private String deliverCityName;
    /**
     * 目的省
     */
    private Integer provinceId;
    /**
     * 目的市
     */
    private Integer cityId;
    /**
     * 目的县
     */
    private Integer countyId;
    /**
     * 目的省名称
     */
    private String provinceName;
    /**
     * 目的市名称
     */
    private String cityName;
    /**
     * 目的县名称
     */
    private String countyName;
    /**
     * 异常id
     */
    private String abnormalId;
    /**
     * 复核日期
     * */
    private Date reviewDate;
    /**
     * 复核责任类型
     * 1仓库
     * 2分拣
     * 3站点
     * 4商家
     * 5空
     * */
    private Integer reviewDutyType;
    /**
     * 复核责任一级id
     * 1、2、3机构id
     */
    private Integer reviewFirstLevelId;
    /**
     * 复核责任一级名称1、2、3机构名称
     * */
    private String reviewFirstLevelName;
    /**
     * 复核责任二级id
     * 1配送中心id
     * 2分拣中心id
     * 3片区id
     * 4、5为空
     * */
    private Integer reviewSecondLevelId;
    /**
     * 复核责任二级名称
     * */
    private String reviewSecondLevelName;
    /**
     * 复核责任二级id
     * */
    private Integer reviewThreeLevelId;
    /**
     * 复核责任三级名称
     * */
    private String reviewThreeLevelName;
    /**
     * 复核责任人erp
     * */
    private String reviewDutyErp;
    /**
     * 复核机构类型
     * */
    private Integer reviewMechanismType;

    /**
     * 复核重量
     * */
    private Double reviewWeight;
    /**
     * 复核长cm
     * */
    private Double reviewLength;
    /**
     * 复核宽cm
     * */
    private Double reviewWidth;
    /**
     * 复核高cm
     * */
    private Double reviewHeight;
    /**
     * 复核体积cm3
     * */
    private Double reviewVolume;
    /**
     * 误差值重量
     * */
    private Double weightDiff;
    /**
     * 误差值体积
     * */
    private Double volumeDiff;
    /**
     * 误差标准值
     * */
    private String diffStandard;
    /**
     * 是否超标(0:未超标 1:超标)
     * */
    private Integer isExcess;
    /**
     * 图片链接
     * */
    private String pictureAddress;
    /**
     * 是否认责
     * */
    private Integer isAccusation;
    /**
     * 是否需要判责
     * */
    private Integer isNeedBlame;
    /**
     * 操作人
     */
    private String operateUser;
    /**
     * 操作时间
     */
    private Date operateTime;

    /** 始发地城市id */
    private Integer reviewDeliverCityId;
    /** 始发地城市名称 */
    private String reviewDeliverCityName;
    /**
     * 目的省
     */
    private Integer reviewProvinceId;
    /**
     * 目的市
     */
    private Integer reviewCityId;
    /**
     * 目的县
     */
    private Integer reviewCountyId;
    /**
     * 目的省名称
     */
    private String reviewProvinceName;
    /**
     * 目的市名称
     */
    private String reviewCityName;
    /**
     * 目的县名称
     */
    private String reviewCountyName;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Integer getBusinessObjectId() {
        return businessObjectId;
    }

    public void setBusinessObjectId(Integer businessObjectId) {
        this.businessObjectId = businessObjectId;
    }

    public String getBusinessObject() {
        return businessObject;
    }

    public void setBusinessObject(String businessObject) {
        this.businessObject = businessObject;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }

    public String getFirstLevelId() {
        return firstLevelId;
    }

    public void setFirstLevelId(String firstLevelId) {
        this.firstLevelId = firstLevelId;
    }

    public String getFirstLevelName() {
        return firstLevelName;
    }

    public void setFirstLevelName(String firstLevelName) {
        this.firstLevelName = firstLevelName;
    }

    public String getSecondLevelId() {
        return secondLevelId;
    }

    public void setSecondLevelId(String secondLevelId) {
        this.secondLevelId = secondLevelId;
    }

    public String getSecondLevelName() {
        return secondLevelName;
    }

    public void setSecondLevelName(String secondLevelName) {
        this.secondLevelName = secondLevelName;
    }

    public String getThreeLevelId() {
        return threeLevelId;
    }

    public void setThreeLevelId(String threeLevelId) {
        this.threeLevelId = threeLevelId;
    }

    public String getThreeLevelName() {
        return threeLevelName;
    }

    public void setThreeLevelName(String threeLevelName) {
        this.threeLevelName = threeLevelName;
    }

    public String getDutyErp() {
        return dutyErp;
    }

    public void setDutyErp(String dutyErp) {
        this.dutyErp = dutyErp;
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

    public Integer getDeliverCityId() {
        return deliverCityId;
    }

    public void setDeliverCityId(Integer deliverCityId) {
        this.deliverCityId = deliverCityId;
    }

    public String getDeliverCityName() {
        return deliverCityName;
    }

    public void setDeliverCityName(String deliverCityName) {
        this.deliverCityName = deliverCityName;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getAbnormalId() {
        return abnormalId;
    }

    public void setAbnormalId(String abnormalId) {
        this.abnormalId = abnormalId;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Integer getReviewDutyType() {
        return reviewDutyType;
    }

    public void setReviewDutyType(Integer reviewDutyType) {
        this.reviewDutyType = reviewDutyType;
    }

    public Integer getReviewFirstLevelId() {
        return reviewFirstLevelId;
    }

    public void setReviewFirstLevelId(Integer reviewFirstLevelId) {
        this.reviewFirstLevelId = reviewFirstLevelId;
    }

    public String getReviewFirstLevelName() {
        return reviewFirstLevelName;
    }

    public void setReviewFirstLevelName(String reviewFirstLevelName) {
        this.reviewFirstLevelName = reviewFirstLevelName;
    }

    public Integer getReviewSecondLevelId() {
        return reviewSecondLevelId;
    }

    public void setReviewSecondLevelId(Integer reviewSecondLevelId) {
        this.reviewSecondLevelId = reviewSecondLevelId;
    }

    public String getReviewSecondLevelName() {
        return reviewSecondLevelName;
    }

    public void setReviewSecondLevelName(String reviewSecondLevelName) {
        this.reviewSecondLevelName = reviewSecondLevelName;
    }

    public Integer getReviewThreeLevelId() {
        return reviewThreeLevelId;
    }

    public void setReviewThreeLevelId(Integer reviewThreeLevelId) {
        this.reviewThreeLevelId = reviewThreeLevelId;
    }

    public String getReviewThreeLevelName() {
        return reviewThreeLevelName;
    }

    public void setReviewThreeLevelName(String reviewThreeLevelName) {
        this.reviewThreeLevelName = reviewThreeLevelName;
    }

    public String getReviewDutyErp() {
        return reviewDutyErp;
    }

    public void setReviewDutyErp(String reviewDutyErp) {
        this.reviewDutyErp = reviewDutyErp;
    }

    public Integer getReviewMechanismType() {
        return reviewMechanismType;
    }

    public void setReviewMechanismType(Integer reviewMechanismType) {
        this.reviewMechanismType = reviewMechanismType;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public Double getReviewLength() {
        return reviewLength;
    }

    public void setReviewLength(Double reviewLength) {
        this.reviewLength = reviewLength;
    }

    public Double getReviewWidth() {
        return reviewWidth;
    }

    public void setReviewWidth(Double reviewWidth) {
        this.reviewWidth = reviewWidth;
    }

    public Double getReviewHeight() {
        return reviewHeight;
    }

    public void setReviewHeight(Double reviewHeight) {
        this.reviewHeight = reviewHeight;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public Double getWeightDiff() {
        return weightDiff;
    }

    public void setWeightDiff(Double weightDiff) {
        this.weightDiff = weightDiff;
    }

    public Double getVolumeDiff() {
        return volumeDiff;
    }

    public void setVolumeDiff(Double volumeDiff) {
        this.volumeDiff = volumeDiff;
    }

    public String getDiffStandard() {
        return diffStandard;
    }

    public void setDiffStandard(String diffStandard) {
        this.diffStandard = diffStandard;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public Integer getIsAccusation() {
        return isAccusation;
    }

    public void setIsAccusation(Integer isAccusation) {
        this.isAccusation = isAccusation;
    }

    public Integer getIsNeedBlame() {
        return isNeedBlame;
    }

    public void setIsNeedBlame(Integer isNeedBlame) {
        this.isNeedBlame = isNeedBlame;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getReviewDeliverCityId() {
        return reviewDeliverCityId;
    }

    public void setReviewDeliverCityId(Integer reviewDeliverCityId) {
        this.reviewDeliverCityId = reviewDeliverCityId;
    }

    public String getReviewDeliverCityName() {
        return reviewDeliverCityName;
    }

    public void setReviewDeliverCityName(String reviewDeliverCityName) {
        this.reviewDeliverCityName = reviewDeliverCityName;
    }

    public Integer getReviewProvinceId() {
        return reviewProvinceId;
    }

    public void setReviewProvinceId(Integer reviewProvinceId) {
        this.reviewProvinceId = reviewProvinceId;
    }

    public Integer getReviewCityId() {
        return reviewCityId;
    }

    public void setReviewCityId(Integer reviewCityId) {
        this.reviewCityId = reviewCityId;
    }

    public Integer getReviewCountyId() {
        return reviewCountyId;
    }

    public void setReviewCountyId(Integer reviewCountyId) {
        this.reviewCountyId = reviewCountyId;
    }

    public String getReviewProvinceName() {
        return reviewProvinceName;
    }

    public void setReviewProvinceName(String reviewProvinceName) {
        this.reviewProvinceName = reviewProvinceName;
    }

    public String getReviewCityName() {
        return reviewCityName;
    }

    public void setReviewCityName(String reviewCityName) {
        this.reviewCityName = reviewCityName;
    }

    public String getReviewCountyName() {
        return reviewCountyName;
    }

    public void setReviewCountyName(String reviewCountyName) {
        this.reviewCountyName = reviewCountyName;
    }
}
