package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillManageDomain;
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
    private WaybillPackageApi waybillPackageApi;
    @Autowired
    private BoxService boxService;
    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    AbnormalWayBillService abnormalWayBillService;

//    @Autowired
//    private WaybillPackageDao waybillPackageDao;

    public BigWaybillDto getWaybill(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    public BigWaybillDto getWaybillProduct(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryGoodList(true);
        wChoice.setQueryWaybillC(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    public BigWaybillDto getWaybillState(String waybillCode) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillM(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode,
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
            return getPackageByWaybillInterface(packageCode);
    }

    private WaybillPackageDTO getPackageByWaybillInterface(String packageCode){

        if(packageCode == null || packageCode.length() == 0){
            return null;
        }

        //判断是否为包裹号，如果不是包裹号，先从箱号里边取值
        if(!WaybillUtil.isPackageCode(packageCode)){
            Box box = boxService.findBoxByCode(packageCode);
            if(box == null){
                return null;
            }
            double length = box.getLength() == null ? 0 : box.getLength();
            double width = box.getWidth() == null ? 0 : box.getWidth();
            double height = box.getHeight() == null ? 0 : box.getWidth();
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
                            double volume = null==pack.getpLength()||null==pack.getpWidth()||null==pack.getpHigh()?
                                    0.00 : pack.getpLength()*pack.getpWidth()*pack.getpHigh();
                            waybillPackageDTOTemp.setOriginalVolume(volume);
                            waybillPackageDTOTemp.setVolume(volume);
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

    @Override
    public Boolean isReverseOperationAllowed(String waybillCode, Integer siteCode) throws Exception {
        //获取运单信息
        BigWaybillDto bigWaybillDto = this.getWaybillState(waybillCode);
        if(bigWaybillDto != null && bigWaybillDto.getWaybillState() != null) {
            WaybillManageDomain waybillManageDomain = bigWaybillDto.getWaybillState();
            //判断运单是否妥投
            if (Constants.WAYBILL_DELIVERED_CODE.equals(waybillManageDomain.getWaybillState())) {
                //查询运单是否操作异常处理
                AbnormalWayBill abnormalWaybill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(waybillCode, siteCode);
                //异常操作运单记录为空，不能进行逆向操作，需提示妥投订单逆向操作需提交异常处理记录
                if(abnormalWaybill == null) {
                    return false;
                }
            }
        } else {
            String log = "isReverseOperationAllowed方法获取运单状态失败，waybillCode：" + waybillCode + ", siteCode：" + siteCode;
            logger.error(log);
            throw new Exception(log);
        }

        return true;
    }

    @Override
    public BigWaybillDto getWaybillProductAndState(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryGoodList(true);
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillM(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode, wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }
}
