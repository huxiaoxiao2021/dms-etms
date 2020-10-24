package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryResidentDto;
import com.jd.bluedragon.distribution.gantry.exception.GantryResidentException;
import com.jd.bluedragon.distribution.gantry.service.GantryResidentScanService;
import com.jd.bluedragon.distribution.popPrint.domain.ResidentTypeEnum;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 龙门架驻厂扫描实现
 *
 * @author: hujiping
 * @date: 2020/10/22 18:12
 */
@Service("gantryResidentScanService")
public class GantryResidentScanServiceImpl implements GantryResidentScanService {

    private static final Logger logger = LoggerFactory.getLogger(GantryResidentScanServiceImpl.class);

    private static final String PC_POP_INSPECTION = "PC-POP验货";

    @Autowired
    private TaskService taskService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("dmsPopPickupMQ")
    private DefaultJMQProducer dmsPopPickupMQ;

    @Autowired
    private PopPrintService popPrintService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.CORE.GantryResidentScanServiceImpl.dealLogic",
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealLogic(GantryResidentDto gantryResidentDto) throws Exception {

        String waybillCode = WaybillUtil.getWaybillCode(gantryResidentDto.getBarCode());
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if(waybill == null){
            logger.error("根据运单号【{}】获取运单信息失败!",waybillCode);
            throw new GantryResidentException(String.format(GantryResidentException.WAYBILL_NOT_EXIST,waybillCode));
        }
        // 1030-pop收货任务
        addPopReceiveTask(gantryResidentDto,waybill);
        // 上传称重数据
        dealWeightAndVolume(gantryResidentDto);
        // 发送pop上门接货消息
        pushPopPickup(gantryResidentDto,waybill);
        // 处理pop数据
        dealPopData(gantryResidentDto,waybill);

    }

    /**
     * 发送pop上门接货消息
     * @param dto
     * @param waybill
     */
    private void pushPopPickup(GantryResidentDto dto, Waybill waybill) {
        try {
            String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());
            PopPickupRequest popPickup = new PopPickupRequest();
            popPickup.setWaybillCode(waybillCode);
            popPickup.setPackageBarcode(dto.getBarCode());
            popPickup.setUserCode(dto.getOperatorId());
            popPickup.setUserName(dto.getOperatorName());
            popPickup.setSiteCode(dto.getOperateSiteCode());
            popPickup.setSiteName(dto.getOperateSiteName());
            popPickup.setOperateTime(dto.getOperateTime());
            popPickup.setBusinessType(0);
            popPickup.setIsCancel(0);
            popPickup.setPickupType(5);
            popPickup.setPopBusinessCode(waybill.getConsignerId() == null ? null : String.valueOf(waybill.getConsignerId()));
            popPickup.setPopBusinessName(waybill.getConsigner());
            popPickup.setPackageNumber(waybill.getGoodNumber());
            popPickup.setWaybillType(waybill.getWaybillType());
            dmsPopPickupMQ.sendOnFailPersistent(popPickup.getPackageBarcode(),JsonHelper.toJson(popPickup));
        }catch (Exception e){
            logger.error("发送pop上门接货消息异常,入参:【{}】",JsonHelper.toJsonMs(dto),e);
        }
    }

    /**
     * 上传称重数据
     * @param dto
     */
    private void dealWeightAndVolume(GantryResidentDto dto) {
        try {
            WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
            weightVolumeEntity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE);
            weightVolumeEntity.setBarCode(dto.getBarCode());
            weightVolumeEntity.setWaybillCode(WaybillUtil.getWaybillCode(dto.getBarCode()));
            weightVolumeEntity.setPackageCode(dto.getBarCode());
            weightVolumeEntity.setSourceCode(FromSourceEnum.DMS_AUTOMATIC_MEASURE);
            weightVolumeEntity.setWeight(dto.getWeight());
            weightVolumeEntity.setVolume(dto.getVolume());
            weightVolumeEntity.setLength(dto.getLength());
            weightVolumeEntity.setWidth(dto.getWidth());
            weightVolumeEntity.setHeight(dto.getHeight());
            weightVolumeEntity.setOperateSiteCode(dto.getOperateSiteCode());
            weightVolumeEntity.setOperateSiteName(dto.getOperateSiteName());
            weightVolumeEntity.setOperatorId(dto.getOperatorId());
            weightVolumeEntity.setOperatorCode(dto.getOperatorErp());
            weightVolumeEntity.setOperatorName(dto.getOperatorName());
            weightVolumeEntity.setOperateTime(DateHelper.parseDateTime(dto.getOperateTime()));
            InvokeResult<Boolean> result = dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity, Boolean.FALSE);
            if(result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()){
                logger.error("龙门架驻厂扫描上传称重数据异常,异常原因【{}】",result.getMessage());
            }
        }catch (Exception e){
            logger.error("龙门架驻厂扫描上传称重数据异常,入参【{}】", JsonHelper.toJsonMs(dto),e);
        }
    }

    /**
     * 创建pop收货任务
     * @param gantryResidentDto
     */
    private void addPopReceiveTask(GantryResidentDto gantryResidentDto, Waybill waybill) {
        try {
            Task task = new Task();
            task.setTableName(Task.getTableName(Task.TASK_TYPE_POP));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setType(Task.TASK_TYPE_POP);
            task.setCreateSiteCode(gantryResidentDto.getOperateSiteCode());
            task.setKeyword1(PC_POP_INSPECTION);
            task.setKeyword2(gantryResidentDto.getBarCode());
            task.setBoxCode(WaybillUtil.getWaybillCode(gantryResidentDto.getBarCode()));
            task.setReceiveSiteCode(0);
            task.setBusinessType(DmsConstants.BUSSINESS_TYPE_InFactory);
            task.setOperateTime(DateHelper.parseDateTime(gantryResidentDto.getOperateTime()));
            String ownSign = BusinessHelper.getOwnSign();
            task.setOwnSign(ownSign);

            String waybillCode = WaybillUtil.getWaybillCode(gantryResidentDto.getBarCode());
            Map<String,Object> bodyMap = new HashMap<>();
            bodyMap.put("boxCode","");
            bodyMap.put("boxCodeNew",waybillCode);
            bodyMap.put("packageBarcode",gantryResidentDto.getBarCode());
            bodyMap.put("popSupId",waybill.getConsignerId());
            bodyMap.put("popSupName",waybill.getConsigner());
            bodyMap.put("quantity",waybill.getGoodNumber());
            bodyMap.put("type",waybill.getWaybillType());
            bodyMap.put("queueNo","");
            bodyMap.put("queueType",5);
            bodyMap.put("busiId",waybill.getBusiId());
            bodyMap.put("busiName",waybill.getBusiName());
            bodyMap.put("siteCode",gantryResidentDto.getOperateSiteCode());
            bodyMap.put("siteName",gantryResidentDto.getOperateSiteName());
            bodyMap.put("userCode",gantryResidentDto.getOperatorId());
            bodyMap.put("userName",gantryResidentDto.getOperatorName());
            bodyMap.put("businessType",DmsConstants.BUSSINESS_TYPE_InFactory);
            bodyMap.put("operateTime",gantryResidentDto.getOperateTime());

            String body = Constants.PUNCTUATION_OPEN_BRACKET
                    + JsonHelper.toJson(bodyMap)
                    + Constants.PUNCTUATION_CLOSE_BRACKET;
            task.setBody(body);

            StringBuilder fingerprint = new StringBuilder("");
            fingerprint.append(task.getCreateSiteCode()).append(Constants.UNDER_LINE)
                    .append(task.getReceiveSiteCode()).append(Constants.UNDER_LINE).append(task.getBusinessType())
                    .append(Constants.UNDER_LINE).append(task.getBoxCode()).append(Constants.UNDER_LINE).append(task.getKeyword2())
                    .append(Constants.UNDER_LINE).append(DateHelper.formatDateTimeMs(task.getOperateTime()))
                    .append(Constants.UNDER_LINE).append(task.getOperateType());
            task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
            taskService.add(task, true);
        }catch (Exception e){
            logger.error("创建pop收货任务异常,入参:【{}】",JsonHelper.toJsonMs(gantryResidentDto),e);
        }
    }

    /**
     * 处理pop数据
     * @param dto
     */
    private void dealPopData(GantryResidentDto dto, Waybill waybill) {

        try {
            PopPrintRequest popPrintRequest = new PopPrintRequest();
            popPrintRequest.setWaybillCode(waybill.getWaybillCode());
            popPrintRequest.setQuantity(waybill.getGoodNumber());
            popPrintRequest.setPopSupId(waybill.getConsignerId());
            popPrintRequest.setPopSupName(waybill.getConsigner());
            popPrintRequest.setWaybillType(waybill.getWaybillType());
            popPrintRequest.setOperatorCode(dto.getOperatorId());
            popPrintRequest.setOperatorName(dto.getOperatorName());
            popPrintRequest.setOperateSiteCode(dto.getOperateSiteCode());
            popPrintRequest.setOperateSiteName(dto.getOperateSiteName());
            popPrintRequest.setOperateTime(dto.getOperateTime());
            popPrintRequest.setPopReceiveType(5);
            popPrintRequest.setBoxCode(dto.getBoxCode());
            popPrintRequest.setPackageBarcode(dto.getBarCode());
            popPrintRequest.setBusiId(waybill.getBusiId());
            popPrintRequest.setBusiName(waybill.getBusiName());
            popPrintRequest.setBusinessType(PopPrintRequest.BUS_TYPE_IN_FACTORY_PRINT);
            popPrintRequest.setCategoryName(dto.getConsignGood());
            popPrintRequest.setInterfaceType(WaybillPrintOperateTypeEnum.FIELD_PRINT.getType());
            popPrintRequest.setOperateType(PopPrintRequest.NOT_PRINT_PACK_TYPE);

            PopPrintResponse popPrintResponse
                    = popPrintService.dealPopPrintLogic(popPrintRequest, ResidentTypeEnum.RESIDENT_GANTRY.getType());
            if(popPrintResponse != null
                    && !PopPrintResponse.CODE_OK.equals(popPrintResponse.getCode())){
                logger.error("处理pop数据失败，异常原因：【{}】",popPrintResponse.getMessage());
            }
        }catch (Exception e){
            logger.error("处理pop数据异常,入参:【{}】!",JsonHelper.toJsonMs(dto),e);
        }

    }
}
