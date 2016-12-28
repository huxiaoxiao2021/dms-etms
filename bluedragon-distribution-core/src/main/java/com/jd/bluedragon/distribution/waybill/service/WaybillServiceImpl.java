package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.dao.FreshWaybillDao;
import com.jd.bluedragon.distribution.waybill.dao.WaybillPackageDao;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
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

//    @Autowired
//    private WaybillPackageDao waybillPackageDao;

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

    @Override
    public WaybillPackageDTO getWaybillPackage(String packageCode) {
        WaybillPackageDTO waybillPackageDTO = null;
//        try{
//            waybillPackageDTO = waybillPackageDao.get(packageCode);
//        }catch(Exception e){
//            this.logger.warn("获取总部运单包裹缓存表信息出现异常,包裹号：" + packageCode , e);
//            return getPackageByWaybillInterface(packageCode);
//        }

        if(waybillPackageDTO == null){
            return getPackageByWaybillInterface(packageCode);
        }else{
            return waybillPackageDTO;
        }
    }

    private WaybillPackageDTO getPackageByWaybillInterface(String packageCode){
        String waybillCode = SerialRuleUtil.getWaybillCode(packageCode);
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(true);

        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(waybillCode, wChoice);

        if(baseEntity == null || baseEntity.getData() == null){
            return null;
        }

        List<DeliveryPackageD> packageList = baseEntity.getData().getPackageList();

        if(packageList == null || packageList.size() < 1){
            return null;
        }

        for (DeliveryPackageD deliverPackageD : packageList) {
            if(packageCode.equals(deliverPackageD.getPackageBarcode())){
                WaybillPackageDTO waybillPackageDTOTemp = new WaybillPackageDTO();
                waybillPackageDTOTemp.setWaybillCode(deliverPackageD.getWaybillCode());
                waybillPackageDTOTemp.setPackageCode(deliverPackageD.getPackageBarcode());
                waybillPackageDTOTemp.setOriginalWeight(deliverPackageD.getGoodWeight());
                waybillPackageDTOTemp.setWeight(deliverPackageD.getAgainWeight());
                waybillPackageDTOTemp.setOriginalVolume(StringHelper.isDouble(deliverPackageD.getGoodVolume()) ? Double.parseDouble(deliverPackageD.getGoodVolume()) : 0);
                waybillPackageDTOTemp.setVolume(StringHelper.isDouble(deliverPackageD.getGoodVolume()) ? Double.parseDouble(deliverPackageD.getGoodVolume()) : 0);
                return waybillPackageDTOTemp;
            }
        }
        return null;
    }
}
