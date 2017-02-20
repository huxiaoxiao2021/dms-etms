package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
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
    @Autowired
    private WaybillPackageApi waybillPackageApi;
    @Autowired
    private BoxService boxService;

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

        if(packageCode == null || packageCode.length() == 0){
            return null;
        }

        //判断是否为包裹号，如果不是包裹号，先从箱号里边取值
        if(!BusinessHelper.isPackageCode(packageCode)){
            Box box = boxService.findBoxByCode(packageCode);
            if(box == null){
                return null;
            }
            double length = box.getLength();
            double width = box.getWidth();
            double height = box.getHeight();
            WaybillPackageDTO waybillPackageDTOTemp = new WaybillPackageDTO();
            waybillPackageDTOTemp.setPackageCode(packageCode);
            //长宽高
            waybillPackageDTOTemp.setLength(length);
            waybillPackageDTOTemp.setWidth(width);
            waybillPackageDTOTemp.setHeight(height);
            waybillPackageDTOTemp.setOriginalVolume(length*width*height);
            waybillPackageDTOTemp.setVolume(length*width*height);
            return waybillPackageDTOTemp;
        }else{
            String waybillCode = SerialRuleUtil.getWaybillCode(packageCode);
            BaseEntity<List<PackOpeFlowDto>> dtoList= waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            if(dtoList!=null && dtoList.getResultCode()==1){
                List<PackOpeFlowDto> dto = dtoList.getData();
                if(dto!=null && !dto.isEmpty()) {
                    for(PackOpeFlowDto pack :dto){
                        if(packageCode.equals(pack.getPackageCode())){
                            WaybillPackageDTO waybillPackageDTOTemp = new WaybillPackageDTO();
                            waybillPackageDTOTemp.setWaybillCode(pack.getWaybillCode());
                            waybillPackageDTOTemp.setPackageCode(pack.getPackageCode());
                            waybillPackageDTOTemp.setWeight(pack.getpWeight());
                            //长宽高
                            waybillPackageDTOTemp.setLength(pack.getpLength());
                            waybillPackageDTOTemp.setWidth(pack.getpWidth());
                            waybillPackageDTOTemp.setHeight(pack.getpHigh());

                            waybillPackageDTOTemp.setOriginalVolume(pack.getpLength()*pack.getpWidth()*pack.getpHigh());
                            waybillPackageDTOTemp.setVolume(pack.getpLength()*pack.getpWidth()*pack.getpHigh());
                            waybillPackageDTOTemp.setCreateUserCode(pack.getWeighUserId());
                            waybillPackageDTOTemp.setCreateTime(pack.getWeighTime());
                            return waybillPackageDTOTemp;
                        }
                    }
                }
            }
        }

        return null;
    }
}
