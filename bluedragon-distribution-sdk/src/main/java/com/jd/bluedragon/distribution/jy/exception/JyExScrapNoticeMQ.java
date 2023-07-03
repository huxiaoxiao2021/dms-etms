package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;
import java.util.Date;

/**
 * 异常-报废通知消息体
 *
 * @author hujiping
 * @date 2023/3/13 2:09 PM
 */
public class JyExScrapNoticeMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 领取人ERP
     */
    private String handlerErp;

    /**
     * 领取开始时间
     */
    private Date queryStartTime;

    /**
     * 领取结束时间
     */
    private Date queryEndTime;

    public String getHandlerErp() {
        return handlerErp;
    }

    public void setHandlerErp(String handlerErp) {
        this.handlerErp = handlerErp;
    }

    public Date getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(Date queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public Date getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Date queryEndTime) {
        this.queryEndTime = queryEndTime;
    }
}
