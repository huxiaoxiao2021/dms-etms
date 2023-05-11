package com.jd.bluedragon.distribution.jy.transport.dto;

import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 运输车辆靠台基础数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-09 10:41:54 周二
 */
public class VehicleArriveDockBaseDataDto implements Serializable {

    private static final long serialVersionUID = -5941136054594631023L;

    /**
     * 月台号列表
     */
    private List<DockInfoEntity> dockList;

    /**
     * 服务器时间，毫秒单位
     */
    private Long timeMillSeconds;

    /**
     * 服务器时间格式化样式字符
     */
    private String timeStr;

    /**
     * 服务器时间，格式化形式
     */
    private String timeFormatStr;

    public VehicleArriveDockBaseDataDto() {
    }

    public List<DockInfoEntity> getDockList() {
        return dockList;
    }

    public void setDockList(List<DockInfoEntity> dockList) {
        this.dockList = dockList;
    }

    public Long getTimeMillSeconds() {
        return timeMillSeconds;
    }

    public void setTimeMillSeconds(Long timeMillSeconds) {
        this.timeMillSeconds = timeMillSeconds;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getTimeFormatStr() {
        return timeFormatStr;
    }

    public void setTimeFormatStr(String timeFormatStr) {
        this.timeFormatStr = timeFormatStr;
    }
}
