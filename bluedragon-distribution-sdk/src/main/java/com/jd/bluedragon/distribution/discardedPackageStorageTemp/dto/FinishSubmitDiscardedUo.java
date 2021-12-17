package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 完成提交更新参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-06 00:23:50 周二
 */
public class FinishSubmitDiscardedUo implements Serializable {
    private static final long serialVersionUID = 4958039952135359839L;

    private Integer unSubmitStatus;
    private Integer submitStatus;
    private String operatorErp;
    private Date operateTime;

    public FinishSubmitDiscardedUo() {
    }

    public Integer getUnSubmitStatus() {
        return unSubmitStatus;
    }

    public FinishSubmitDiscardedUo setUnSubmitStatus(Integer unSubmitStatus) {
        this.unSubmitStatus = unSubmitStatus;
        return this;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public FinishSubmitDiscardedUo setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
        return this;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public FinishSubmitDiscardedUo setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
        return this;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public FinishSubmitDiscardedUo setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    @Override
    public String toString() {
        return "FinishSubmitDiscardedUo{" +
                "unSubmitStatus=" + unSubmitStatus +
                ", submitStatus=" + submitStatus +
                ", operatorErp='" + operatorErp + '\'' +
                ", operateTime=" + operateTime +
                '}';
    }
}
