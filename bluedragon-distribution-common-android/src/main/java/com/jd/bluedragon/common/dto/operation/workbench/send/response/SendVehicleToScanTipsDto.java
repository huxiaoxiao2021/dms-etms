package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.List;

/**
 * 待扫提示信息
 * @author fanggang7
 * @time 2023-08-15 16:16:04 周二
 */
public class SendVehicleToScanTipsDto implements Serializable {

    private static final long serialVersionUID = 1103646577453371688L;

    /**
     * 是否需要显示特殊产品类型待扫tips
     */
    private Boolean needShowSpecialProductTypeToScanTips;

    /**
     * 还需多少分钟才需要展示提示信息，前端取次值可以在该值之后启动定时任务调接口查询
     */
    private Integer needShowRemainTimeMinutes;

    /**
     * 剩余时间分钟数
     */
    private Integer toSendRemainTimeMinutes;

    /**
     * 待扫特殊产品类型统计
     */
    private List<SendVehicleProductTypeAgg> specialProductTypeToScanList;

    public SendVehicleToScanTipsDto() {
    }

    public Boolean getNeedShowSpecialProductTypeToScanTips() {
        return needShowSpecialProductTypeToScanTips;
    }

    public void setNeedShowSpecialProductTypeToScanTips(Boolean needShowSpecialProductTypeToScanTips) {
        this.needShowSpecialProductTypeToScanTips = needShowSpecialProductTypeToScanTips;
    }

    public Integer getNeedShowRemainTimeMinutes() {
        return needShowRemainTimeMinutes;
    }

    public void setNeedShowRemainTimeMinutes(Integer needShowRemainTimeMinutes) {
        this.needShowRemainTimeMinutes = needShowRemainTimeMinutes;
    }

    public Integer getToSendRemainTimeMinutes() {
        return toSendRemainTimeMinutes;
    }

    public void setToSendRemainTimeMinutes(Integer toSendRemainTimeMinutes) {
        this.toSendRemainTimeMinutes = toSendRemainTimeMinutes;
    }

    public List<SendVehicleProductTypeAgg> getSpecialProductTypeToScanList() {
        return specialProductTypeToScanList;
    }

    public void setSpecialProductTypeToScanList(List<SendVehicleProductTypeAgg> specialProductTypeToScanList) {
        this.specialProductTypeToScanList = specialProductTypeToScanList;
    }
}
