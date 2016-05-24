package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WaybillServiceImpl implements WaybillService {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WaybillStatusService waybillStatusService;
	@Autowired
	WaybillQueryApi waybillQueryApi;

	public BigWaybillDto getWaybill(String waybillCode) {
		String aWaybillCode = BusinessHelper.getWaybillCode(waybillCode);

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillE(true);
		wChoice.setQueryWaybillM(true);
		wChoice.setQueryPackList(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(aWaybillCode,
		        wChoice);

		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}

	public BigWaybillDto getWaybillProduct(String waybillCode) {
		String aWaybillCode = BusinessHelper.getWaybillCode(waybillCode);
		
		WChoice wChoice = new WChoice();
		wChoice.setQueryGoodList(true);

		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(aWaybillCode,
		        wChoice);
		
		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}

	public BigWaybillDto getWaybillState(String waybillCode) {

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillM(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(waybillCode,
		        wChoice);

		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}

    @Override
    public Boolean doWaybillStatusTask(Task task) {
        try {
            // 妥投任务
            if(null != task.getType() && task.getType().equals(Task.TASK_TYPE_WAYBILL_FINISHED)) {
                waybillStatusService.sendModifyWaybillStatusFinished(task);
                // 除妥投外需要改变运单状态的任务
            } else {
                List<Task> taskList = new ArrayList<Task>();
                taskList.add(task);
                waybillStatusService.sendModifyWaybillStatusNotify(taskList);
            }
        } catch (Exception e) {
            logger.error("调用运单接口[回传运单状态]或者[置妥投]失败 \n" + JsonHelper.toJson(task) + "\n", e);
            return false;
        }

        return true;
    }

    @Override
    public Boolean doWaybillTraceTask(Task task) {
        try {
            List<Task> taskList = new ArrayList<Task>();
            taskList.add(task);
            this.waybillStatusService.sendModifyWaybillTrackNotify(taskList);
        } catch (Exception e) {
            this.logger.warn("调用运单[回传全程跟踪]服务出现异常 \n" + JsonHelper.toJson(task) + "\n", e);
            return false;
        }
        return true;
    }
}
