package com.jd.bluedragon.distribution.jy.dto.unload;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.io.Serializable;

/**
 * 卸车扫描上下文内容
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-07 09:34:19 周一
 */
public class UnloadScanContextDto implements Serializable {

    private static final long serialVersionUID = 5418914501044409289L;

    /**
     * 原始提交入参
     */
    private UnloadScanRequest unloadScanRequest;

    /**
     * 卸车任务详情
     */
    private JyBizTaskUnloadVehicleEntity taskUnloadVehicle;

    /**
     * 运单数据
     */
    private BigWaybillDto bigWaybillDto;

    /**
     * 是否更新卸车进度标识
     */
    private boolean updateUnloadProcessFlag;

    public UnloadScanRequest getUnloadScanRequest() {
        return unloadScanRequest;
    }

    public void setUnloadScanRequest(UnloadScanRequest unloadScanRequest) {
        this.unloadScanRequest = unloadScanRequest;
    }

    public JyBizTaskUnloadVehicleEntity getTaskUnloadVehicle() {
        return taskUnloadVehicle;
    }

    public void setTaskUnloadVehicle(JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        this.taskUnloadVehicle = taskUnloadVehicle;
    }

    public BigWaybillDto getBigWaybillDto() {
        return bigWaybillDto;
    }

    public void setBigWaybillDto(BigWaybillDto bigWaybillDto) {
        this.bigWaybillDto = bigWaybillDto;
    }

    public boolean getUpdateUnloadProcessFlag() {
        return updateUnloadProcessFlag;
    }

    public void setUpdateUnloadProcessFlag(boolean updateUnloadProcessFlag) {
        this.updateUnloadProcessFlag = updateUnloadProcessFlag;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
