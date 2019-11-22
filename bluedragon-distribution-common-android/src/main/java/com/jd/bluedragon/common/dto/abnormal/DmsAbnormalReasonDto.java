package com.jd.bluedragon.common.dto.abnormal;

import java.io.Serializable;
import java.util.List;

public class DmsAbnormalReasonDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * 异常原因id，对应基础资料的id
     * */
    private Integer reasonId;

    /*
     * 异常原因，对应基础资料的typeCode
     * */
    private Long reasonCode;

    /*
     * 异常原因名称
     * */
    private String reasonName;

    /*
     * 父原因id，对应基础资料的id
     * */
    private Integer parentId;

    /*
     * 父原因Code，对应基础资料的typeCode
     * */
    private Long parentCode;


    /*
     * 父原因id
     * */
    private String parentName;
    /*
     * 异常原因层级
     * */
    private Integer level;

    /*
     * 异常原因来源
     * */
    private Integer sourceType;

    /*
     * 是否外呼类型
     * */
    private Integer isOutCallType;

    /*
     * 是否上传类型
     * */
    private Integer isUploadImgType;

    /*
     * 是否上传视频类型
     * */
    private Integer isDeviceCodeType;

    /*
     * 原因说明
     * */
    private String remark;

    /*
     * 下一级别异常原因集合
     * */
    private List<DmsAbnormalReasonDto> childReasonList;

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getIsOutCallType() {
        return isOutCallType;
    }

    public void setIsOutCallType(Integer isOutCallType) {
        this.isOutCallType = isOutCallType;
    }

    public Integer getIsUploadImgType() {
        return isUploadImgType;
    }

    public void setIsUploadImgType(Integer isUploadImgType) {
        this.isUploadImgType = isUploadImgType;
    }

    public Integer getIsDeviceCodeType() {
        return isDeviceCodeType;
    }

    public void setIsDeviceCodeType(Integer isDeviceCodeType) {
        this.isDeviceCodeType = isDeviceCodeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<DmsAbnormalReasonDto> getChildReasonList() {
        return childReasonList;
    }

    public void setChildReasonList(List<DmsAbnormalReasonDto> childReasonList) {
        this.childReasonList = childReasonList;
    }


    public Long getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Long reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }
}
