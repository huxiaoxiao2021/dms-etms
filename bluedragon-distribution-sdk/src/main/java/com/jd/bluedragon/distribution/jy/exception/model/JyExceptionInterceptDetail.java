package com.jd.bluedragon.distribution.jy.exception.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetail implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键  db_column: id
     */
    private Long id;
    /**
     * 异常任务业务主键  db_column: biz_id
     */
    private String bizId;
    /**
     * 拦截业务主键  db_column: intercept_biz_id
     */
    private String interceptBizId;
    /**
     * 异常条码  db_column: bar_code
     */
    private String barCode;
    /**
     * 业务数据来源  db_column: biz_source
     */
    private String bizSource;
    /**
     * 场地id  db_column: site_id
     */
    private Integer siteId;
    /**
     * 场地名称  db_column: site_name
     */
    private String siteName;
    /**
     * 省区ID  db_column: province_agency_code
     */
    private String provinceAgencyCode;
    /**
     * 省名称  db_column: province_agency_name
     */
    private String provinceAgencyName;
    /**
     * 枢纽ID  db_column: area_hub_code
     */
    private String areaHubCode;
    /**
     * 枢纽名称  db_column: area_hub_name
     */
    private String areaHubName;
    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;
    /**
     * 包裹总数  db_column: package_total
     */
    private Integer packageTotal;
    /**
     * 运单号  db_column: waybill_code
     */
    private String waybillCode;
    /**
     * 箱号  db_column: box_code
     */
    private String boxCode;
    /**
     * 操作节点  db_column: operate_node
     */
    private Integer operateNode;
    /**
     * 拦截生效时间  db_column: intercept_effect_time
     */
    private Long interceptEffectTime;
    /**
     * 拦截类型  db_column: intercept_type
     */
    private Integer interceptType;
    /**
     * 设备编码  db_column: device_code
     */
    private String deviceCode;
    /**
     * 设备类型  db_column: device_type
     */
    private Integer deviceType;
    /**
     * 设备子类型  db_column: device_sub_type
     */
    private Integer deviceSubType;
    /**
     * 操作人所在网格码  db_column: operate_position_code
     */
    private String operatePositionCode;
    /**
     * 操作网格工序  db_column: operate_work_station_grid_key
     */
    private String operateWorkStationGridKey;
    /**
     * 操作网格  db_column: operate_work_grid_key
     */
    private String operateWorkGridKey;
    /**
     * 作业区编码  db_column: operate_area_code
     */
    private String operateAreaCode;
    /**
     * 作业区名称  db_column: operate_area_name
     */
    private String operateAreaName;
    /**
     * 网格ID  db_column: operate_grid_code
     */
    private String operateGridCode;
    /**
     * 网格名称  db_column: operate_grid_name
     */
    private String operateGridName;
    /**
     * 工序编码  db_column: operate_work_code
     */
    private String operateWorkCode;
    /**
     * 工序名称  db_column: operate_work_name
     */
    private String operateWorkName;
    /**
     * 拦截消息编码  db_column: intercept_code
     */
    private Integer interceptCode;
    /**
     * 拦截消息  db_column: intercept_message
     */
    private String interceptMessage;
    /**
     * 拦截后处理节点  db_column: dispose_node
     */
    private Integer disposeNode;
    /**
     * 拦截处理人ID  db_column: dispose_user_id
     */
    private Long disposeUserId;
    /**
     * 拦截处理人  db_column: dispose_user_code
     */
    private String disposeUserCode;
    /**
     * 拦截处理人名称  db_column: dispose_user_name
     */
    private String disposeUserName;
    /**
     * 拦截处理时间  db_column: dispose_time
     */
    private Long disposeTime;
    /**
     * 录入包裹重量  db_column: input_weight
     */
    private BigDecimal inputWeight;
    /**
     * 录入包裹体积  db_column: input_volume
     */
    private BigDecimal inputVolume;
    /**
     * 录入包裹长度  db_column: input_length
     */
    private BigDecimal inputLength;
    /**
     * 录入包裹宽度  db_column: input_width
     */
    private BigDecimal inputWidth;
    /**
     * 录入包裹高度  db_column: input_height
     */
    private BigDecimal inputHeight;
    /**
     * 换单后新单号  db_column: waybill_code_new
     */
    private String waybillCodeNew;
    /**
     * 保存状态 0：暂存 1 ：保存   db_column: save_type
     */
    private Integer saveType;
    /**
     * 创建人ID  db_column: create_user_id
     */
    private Long createUserId;
    /**
     * 创建人  db_column: create_user_code
     */
    private String createUserCode;
    /**
     * 创建人名称  db_column: create_user_name
     */
    private String createUserName;
    /**
     * 修改人ID  db_column: update_user_id
     */
    private Long updateUserId;
    /**
     * 修改人  db_column: update_user_code
     */
    private String updateUserCode;
    /**
     * 修改人名称  db_column: update_user_name
     */
    private String updateUserName;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 修改时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 有效标志  db_column: yn
     */
    private Integer yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END

    public JyExceptionInterceptDetail(){
    }
    public JyExceptionInterceptDetail(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getInterceptBizId() {
        return interceptBizId;
    }

    public void setInterceptBizId(String interceptBizId) {
        this.interceptBizId = interceptBizId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
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

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Long getInterceptEffectTime() {
        return interceptEffectTime;
    }

    public void setInterceptEffectTime(Long interceptEffectTime) {
        this.interceptEffectTime = interceptEffectTime;
    }

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(Integer deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public String getOperatePositionCode() {
        return operatePositionCode;
    }

    public void setOperatePositionCode(String operatePositionCode) {
        this.operatePositionCode = operatePositionCode;
    }

    public String getOperateWorkStationGridKey() {
        return operateWorkStationGridKey;
    }

    public void setOperateWorkStationGridKey(String operateWorkStationGridKey) {
        this.operateWorkStationGridKey = operateWorkStationGridKey;
    }

    public String getOperateWorkGridKey() {
        return operateWorkGridKey;
    }

    public void setOperateWorkGridKey(String operateWorkGridKey) {
        this.operateWorkGridKey = operateWorkGridKey;
    }

    public String getOperateAreaCode() {
        return operateAreaCode;
    }

    public void setOperateAreaCode(String operateAreaCode) {
        this.operateAreaCode = operateAreaCode;
    }

    public String getOperateAreaName() {
        return operateAreaName;
    }

    public void setOperateAreaName(String operateAreaName) {
        this.operateAreaName = operateAreaName;
    }

    public String getOperateGridCode() {
        return operateGridCode;
    }

    public void setOperateGridCode(String operateGridCode) {
        this.operateGridCode = operateGridCode;
    }

    public String getOperateGridName() {
        return operateGridName;
    }

    public void setOperateGridName(String operateGridName) {
        this.operateGridName = operateGridName;
    }

    public String getOperateWorkCode() {
        return operateWorkCode;
    }

    public void setOperateWorkCode(String operateWorkCode) {
        this.operateWorkCode = operateWorkCode;
    }

    public String getOperateWorkName() {
        return operateWorkName;
    }

    public void setOperateWorkName(String operateWorkName) {
        this.operateWorkName = operateWorkName;
    }

    public Integer getInterceptCode() {
        return interceptCode;
    }

    public void setInterceptCode(Integer interceptCode) {
        this.interceptCode = interceptCode;
    }

    public String getInterceptMessage() {
        return interceptMessage;
    }

    public void setInterceptMessage(String interceptMessage) {
        this.interceptMessage = interceptMessage;
    }

    public Integer getDisposeNode() {
        return disposeNode;
    }

    public void setDisposeNode(Integer disposeNode) {
        this.disposeNode = disposeNode;
    }

    public Long getDisposeUserId() {
        return disposeUserId;
    }

    public void setDisposeUserId(Long disposeUserId) {
        this.disposeUserId = disposeUserId;
    }

    public String getDisposeUserCode() {
        return disposeUserCode;
    }

    public void setDisposeUserCode(String disposeUserCode) {
        this.disposeUserCode = disposeUserCode;
    }

    public String getDisposeUserName() {
        return disposeUserName;
    }

    public void setDisposeUserName(String disposeUserName) {
        this.disposeUserName = disposeUserName;
    }

    public Long getDisposeTime() {
        return disposeTime;
    }

    public void setDisposeTime(Long disposeTime) {
        this.disposeTime = disposeTime;
    }

    public BigDecimal getInputWeight() {
        return inputWeight;
    }

    public void setInputWeight(BigDecimal inputWeight) {
        this.inputWeight = inputWeight;
    }

    public BigDecimal getInputVolume() {
        return inputVolume;
    }

    public void setInputVolume(BigDecimal inputVolume) {
        this.inputVolume = inputVolume;
    }

    public BigDecimal getInputLength() {
        return inputLength;
    }

    public void setInputLength(BigDecimal inputLength) {
        this.inputLength = inputLength;
    }

    public BigDecimal getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(BigDecimal inputWidth) {
        this.inputWidth = inputWidth;
    }

    public BigDecimal getInputHeight() {
        return inputHeight;
    }

    public void setInputHeight(BigDecimal inputHeight) {
        this.inputHeight = inputHeight;
    }

    public String getWaybillCodeNew() {
        return waybillCodeNew;
    }

    public void setWaybillCodeNew(String waybillCodeNew) {
        this.waybillCodeNew = waybillCodeNew;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "JyExceptionInterceptDetail{" +
                "id=" + id +
                ", bizId='" + bizId + '\'' +
                ", interceptBizId='" + interceptBizId + '\'' +
                ", barCode='" + barCode + '\'' +
                ", bizSource='" + bizSource + '\'' +
                ", siteId=" + siteId +
                ", siteName='" + siteName + '\'' +
                ", provinceAgencyCode='" + provinceAgencyCode + '\'' +
                ", provinceAgencyName='" + provinceAgencyName + '\'' +
                ", areaHubCode='" + areaHubCode + '\'' +
                ", areaHubName='" + areaHubName + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", packageTotal=" + packageTotal +
                ", waybillCode='" + waybillCode + '\'' +
                ", boxCode='" + boxCode + '\'' +
                ", operateNode=" + operateNode +
                ", interceptEffectTime=" + interceptEffectTime +
                ", interceptType=" + interceptType +
                ", deviceCode='" + deviceCode + '\'' +
                ", deviceType=" + deviceType +
                ", deviceSubType=" + deviceSubType +
                ", operatePositionCode='" + operatePositionCode + '\'' +
                ", operateWorkStationGridKey='" + operateWorkStationGridKey + '\'' +
                ", operateWorkGridKey='" + operateWorkGridKey + '\'' +
                ", operateAreaCode='" + operateAreaCode + '\'' +
                ", operateAreaName='" + operateAreaName + '\'' +
                ", operateGridCode='" + operateGridCode + '\'' +
                ", operateGridName='" + operateGridName + '\'' +
                ", operateWorkCode='" + operateWorkCode + '\'' +
                ", operateWorkName='" + operateWorkName + '\'' +
                ", interceptCode=" + interceptCode +
                ", interceptMessage='" + interceptMessage + '\'' +
                ", disposeNode=" + disposeNode +
                ", disposeUserId=" + disposeUserId +
                ", disposeUserCode='" + disposeUserCode + '\'' +
                ", disposeUserName='" + disposeUserName + '\'' +
                ", disposeTime=" + disposeTime +
                ", inputWeight=" + inputWeight +
                ", inputVolume=" + inputVolume +
                ", inputLength=" + inputLength +
                ", inputWidth=" + inputWidth +
                ", inputHeight=" + inputHeight +
                ", waybillCodeNew=" + waybillCodeNew +
                ", saveType=" + saveType +
                ", createUserId=" + createUserId +
                ", createUserCode='" + createUserCode + '\'' +
                ", createUserName='" + createUserName + '\'' +
                ", updateUserId=" + updateUserId +
                ", updateUserCode='" + updateUserCode + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                ", ts=" + ts +
                '}';
    }
}
