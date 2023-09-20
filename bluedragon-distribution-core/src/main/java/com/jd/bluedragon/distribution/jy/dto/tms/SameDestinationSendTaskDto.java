package com.jd.bluedragon.distribution.jy.dto.tms;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
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
    private JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity;

    // 同流向运输TJ任务
    private TmsTransJobBillDto tmsTransJobBillDto;

    public SameDestinationSendTaskDto() {
    }

    public JyBizTaskSendVehicleEntity getJyBizTaskSendVehicleEntity() {
        return jyBizTaskSendVehicleEntity;
    }

    public void setJyBizTaskSendVehicleEntity(JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity) {
        this.jyBizTaskSendVehicleEntity = jyBizTaskSendVehicleEntity;
    }

    public TmsTransJobBillDto getTmsTransJobBillDto() {
        return tmsTransJobBillDto;
    }

    public void setTmsTransJobBillDto(TmsTransJobBillDto tmsTransJobBillDto) {
        this.tmsTransJobBillDto = tmsTransJobBillDto;
    }
}
