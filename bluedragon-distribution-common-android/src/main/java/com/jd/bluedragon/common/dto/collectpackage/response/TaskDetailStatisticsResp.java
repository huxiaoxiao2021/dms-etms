package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

/**
 * 集包任务相同统计信息相关数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-05-13 18:21:42 周一
 */
public class TaskDetailStatisticsResp implements Serializable {

    private static final long serialVersionUID = 7136699948346577549L;
    /**
     * 拦截件数
     */
    private Integer interceptCount = 0;

    /**
     * 扫描件数
     */
    private Integer scanCount = 0;

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getScanCount() {
        return scanCount;
    }

    public void setScanCount(Integer scanCount) {
        this.scanCount = scanCount;
    }
}
