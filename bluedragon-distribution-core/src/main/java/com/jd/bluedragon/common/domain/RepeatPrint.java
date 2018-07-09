package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * Created by hujiping on 2018/6/12.
 */
public class RepeatPrint implements Serializable {

    /**
     *  换单打印前的单号
     * */
    private String oldWaybillCode;

    /**
     *  换单打印后的新单号
     * */
    private String newWaybillCode;

    /**
     *  取件单的创建时间是否超过15天
     * */
    private Boolean isOverTime;

    public String getNewWaybillCode() {
        return newWaybillCode;
    }

    public void setNewWaybillCode(String newWaybillCode) {
        this.newWaybillCode = newWaybillCode;
    }

    public Boolean getOverTime() {
        return isOverTime;
    }

    public void setOverTime(Boolean overTime) {
        isOverTime = overTime;
    }

    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }
}
