package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 11:28
 * @Description
 */
public class PickingGoodsRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 流向不一致发货二次确认
     */
    public static final Integer CODE_30001 = 30001;
    public static final String CODE_30001_MSG_1 = "该包裹[箱号]下一流向场地是[%s],与当前发货流向[%s]不一致";
    public static final String CODE_30001_MSG_2 = "该包裹[箱号]路由下一流向查询为空";


    //路由流向场地
    private Integer routerNextSiteId;
    private String routerNextSiteName;
    /**
     * 箱号确认流向场地的key[包裹号]
     */
    private String boxConfirmNextSiteKey;
    /**
     * 是否可以切换流向
     * 【1、错发的流向在发货流向维护过允许切换，如果没有维护该流向，只能操作强发，不允许切换】
     * 【2、没有找到路由，只能操作强发，不允许切换】
     */
    private Boolean nextSiteSupportSwitch;
    /**
     * 服务端提货时间
     */
    private Long operateTime;

    /**
     * 提货任务来源【PDA不需要关注】
     * 根据提货单据反查任务，确定任务多重规则，记录具体找任务的规则
     * com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum
     */
    private Integer taskSource;

    /**
     * 当前barCode提货任务维度统计数据
     */
    private AirRailTaskAggDto airRailTaskAggDto;
    /**
     * 实际发货的批次号
     */
    private String batchCode;
    /**
     * 发货流向维度的统计数据
     */
    private SendFlowDto sendFlowDto;

    public Integer getRouterNextSiteId() {
        return routerNextSiteId;
    }

    public void setRouterNextSiteId(Integer routerNextSiteId) {
        this.routerNextSiteId = routerNextSiteId;
    }

    public String getRouterNextSiteName() {
        return routerNextSiteName;
    }

    public void setRouterNextSiteName(String routerNextSiteName) {
        this.routerNextSiteName = routerNextSiteName;
    }

    public String getBoxConfirmNextSiteKey() {
        return boxConfirmNextSiteKey;
    }

    public void setBoxConfirmNextSiteKey(String boxConfirmNextSiteKey) {
        this.boxConfirmNextSiteKey = boxConfirmNextSiteKey;
    }

    public Boolean getNextSiteSupportSwitch() {
        return nextSiteSupportSwitch;
    }

    public void setNextSiteSupportSwitch(Boolean nextSiteSupportSwitch) {
        this.nextSiteSupportSwitch = nextSiteSupportSwitch;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(Integer taskSource) {
        this.taskSource = taskSource;
    }

    public AirRailTaskAggDto getAirRailTaskAggDto() {
        return airRailTaskAggDto;
    }

    public void setAirRailTaskAggDto(AirRailTaskAggDto airRailTaskAggDto) {
        this.airRailTaskAggDto = airRailTaskAggDto;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public SendFlowDto getSendFlowDto() {
        return sendFlowDto;
    }

    public void setSendFlowDto(SendFlowDto sendFlowDto) {
        this.sendFlowDto = sendFlowDto;
    }
}
