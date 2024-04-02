package com.jd.bluedragon.common.dto.router.dynamicLine.response;

import java.util.Date;

/**
 * 动态线路替换方案
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-02 10:41:47 周二
 */
public class RouterDynamicLineReplacePlanVo {

    /**
     * 主键
     */
    private Long id;
    /**
     * 版本ID
     */
    private Long versionId;
    /**
     * 当前场地id
     */
    private Integer startSiteId;
    /**
     * 当前场地id
     */
    private String startSiteName;
    /**
     * 原计划线路目的点ID
     */
    private Integer oldEndSiteId;
    /**
     * 原计划线路目的点名称
     */
    private String oldEndSiteName;
    /**
     * 原线路编码
     */
    private String oldPlanLineCode;
    /**
     * 原计划线路配载
     */
    private String oldPlanFlowCode;
    /**
     * 原计划线路发出时间
     */
    private Date oldPlanDepartureTime;
    /**
     * 新计划线路目的点ID
     */
    private Integer newEndSiteId;
    /**
     * 新计划线路目的点名称
     */
    private String newEndSiteName;
    /**
     * 新线路编码
     */
    private String newPlanLineCode;
    /**
     * 新计划线路配载
     */
    private String newPlanFlowCode;
    /**
     * 新计划线路发出时间
     */
    private Date newPlanDepartureTime;
    /**
     * 替换线路生效时间
     */
    private Date enableTime;
    /**
     * 替换线路生效时间
     */
    private Date disableTime;
    /**
     * 路由推送时间
     */
    private Long pushTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 有效标志
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;
}
