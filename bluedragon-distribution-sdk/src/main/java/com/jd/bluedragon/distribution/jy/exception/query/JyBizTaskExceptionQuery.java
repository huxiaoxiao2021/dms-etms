package com.jd.bluedragon.distribution.jy.exception.query;

import com.jd.dms.java.utils.sdk.base.BaseQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 异常任务查询入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-29 20:53:24 周一
 */
public class JyBizTaskExceptionQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -2138834926896326547L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 异常类型0：三无 1：报废 2：破损
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
     * 不在此场地范围条件
     */
    private Long excludeSiteCode;
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
     * 处理人用户ID
     */
    private Integer handlerUserId;
    /**
     * 异常状态:0：待取件 1：待处理 2：待打印 3：已完成
     */
    private Integer status;

    /**
     * 排除此状态参数
     */
    private List<Integer> excludeStatusList;

    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;

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

    public Long getExcludeSiteCode() {
        return excludeSiteCode;
    }

    public void setExcludeSiteCode(Long excludeSiteCode) {
        this.excludeSiteCode = excludeSiteCode;
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

    public Integer getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(Integer handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getExcludeStatusList() {
        return excludeStatusList;
    }

    public void setExcludeStatusList(List<Integer> excludeStatusList) {
        this.excludeStatusList = excludeStatusList;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
