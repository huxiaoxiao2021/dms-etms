package com.jd.bluedragon.distribution.jy.exception;


import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public class JyBizTaskExceptionEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 异常类型0：三无 1：破损 2：报废
     */
    private Integer type;
    /**
     * 数据来源: 0:通用入口 1：卸车入口 2、发货入口
     */
    private Integer source;
    /**
     * 异常条码
     */
    private String barCode;
    /**
     * 标签
     */
    private String tags;
    /**
     * 场地id
     */
    private Long siteCode;
    /**
     * 场地名称
     */
    private String siteName;
    /**
     * 楼层
     */
    private Integer floor;
    /**
     * 网格编号
     */
    private String gridCode;
    /**
     * 网格号
     */
    private String gridNo;
    /**
     * 作业区编码
     */
    private String areaCode;
    /**
     * 作业区名称
     */
    private String areaName;
    /**
     * 分配类型; 1-场地，2-组，3-人员
     */
    private Integer DistributionType;
    /**
     * 分配目标
     */
    private String distributionTarget;
    /**
     * 分配时间
     */
    private Date distributionTime;
    /**
     * 处理人erp
     */
    private String handlerErp;
    /**
     * 异常状态:0：待取件 1：待处理 2：待打印 3：已完成
     */
    private Integer status;
    /**
     * 处理状态0:待录入 1：待匹配 2：暂存 3: 处理完成
     */
    private Integer processingStatus;
    /**
     * 开始处理时间
     */
    private Date processBeginTime;
    /**
     * 处理完成时间
     */
    private Date processEndTime;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 更新人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否超时：1- 超时，0-未超时
     */
    private Integer timeOut;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getDistributionType() {
        return DistributionType;
    }

    public void setDistributionType(Integer distributionType) {
        DistributionType = distributionType;
    }

    public String getDistributionTarget() {
        return distributionTarget;
    }

    public void setDistributionTarget(String distributionTarget) {
        this.distributionTarget = distributionTarget;
    }

    public Date getDistributionTime() {
        return distributionTime;
    }

    public void setDistributionTime(Date distributionTime) {
        this.distributionTime = distributionTime;
    }

    public String getHandlerErp() {
        return handlerErp;
    }

    public void setHandlerErp(String handlerErp) {
        this.handlerErp = handlerErp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }

    public Date getProcessBeginTime() {
        return processBeginTime;
    }

    public void setProcessBeginTime(Date processBeginTime) {
        this.processBeginTime = processBeginTime;
    }

    public Date getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(Date processEndTime) {
        this.processEndTime = processEndTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
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

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
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
}
