package com.jd.bluedragon.distribution.jy.dto.task;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/2
 * @Description:
 */
public class JyBizTaskUnloadCountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务状态；0-等待初始，1-在途，2-待解，3-待卸，4-卸车，5-卸车完成，6-取消
     */
    private Integer vehicleStatus;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 数量
     */
    private Integer sum;

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
