package com.jd.bluedragon.distribution.jy.dto.tms;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.tms.workbench.dto.TmsTransJobBillDto;

import java.io.Serializable;

/**
 * 同流向任务结果对象
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-09-18 11:05:51 周一
 */
public class SameDestinationSendTaskDto implements Serializable {
    private static final long serialVersionUID = -7345859904237072452L;

    // 同流向分拣TW任务
    private JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetailEntity;

    // 同流向运输TJ任务
    private TmsTransJobBillDto tmsTransJobBillDto;

    public SameDestinationSendTaskDto() {
    }

    public JyBizTaskSendVehicleDetailEntity getJyBizTaskSendVehicleDetailEntity() {
        return jyBizTaskSendVehicleDetailEntity;
    }

    public void setJyBizTaskSendVehicleDetailEntity(JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetailEntity) {
        this.jyBizTaskSendVehicleDetailEntity = jyBizTaskSendVehicleDetailEntity;
    }

    public TmsTransJobBillDto getTmsTransJobBillDto() {
        return tmsTransJobBillDto;
    }

    public void setTmsTransJobBillDto(TmsTransJobBillDto tmsTransJobBillDto) {
        this.tmsTransJobBillDto = tmsTransJobBillDto;
    }
}
