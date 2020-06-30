package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.common.dto.unloadCar.TaskHelpersReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskDto;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.*;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDistributionDao;
import com.jd.bluedragon.distribution.loadAndUnload.domain.*;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vos.ws.VosQueryWS;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:06
 */
@Service("unloadCarService")
public class UnloadCarServiceImpl implements UnloadCarService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarDao unloadCarDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private UnloadCarDistributionDao unloadCarDistributionDao;

    @Autowired
    private VosQueryWS vosQueryWS;

    @Override
    public InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if(unloadCar == null){
            result.error("封车编码【" + sealCarCode + "】的卸车任务不存在，请检查");
            return result;
        }
        UnloadCarScanResult unloadCarScanResult = new UnloadCarScanResult();
        unloadCarScanResult.setSealCarCode(unloadCar.getSealCarCode());
        unloadCarScanResult.setPackageTotalCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setPackageScanCount(0);
//        unloadCarScanResult.setPackageUnScanCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setSurplusPackageScanCount(0);
        result.setData(unloadCarScanResult);
        return result;
    }

    @Override
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request) {

        return null;
    }

    @Override
    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {

        List<UnloadCarTask> unloadCarTasks = new ArrayList<>();
        //查询卸车任务
        unloadCarTasks = unloadCarDao.queryByCondition(condition);
        for (UnloadCarTask unloadCarTask : unloadCarTasks) {
            String sealCarCode = unloadCarTask.getSealCarCode();
            //查询卸车任务的协助人
            List<String> helpers = unloadCarDistributionDao.selectHelperBySealCarCode(sealCarCode);
            unloadCarTask.setHelperErps(helpers.toString());
        }
        return unloadCarTasks;
    }

    @Override
    public boolean distributeTask(DistributeTaskRequest request) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp",request.getUnloadUserErp());
        params.put("railWayPlatForm",request.getRailWayPlatForm());
        params.put("sealCarCodes",request.getSealCarCodes());
        params.put("updateUserErp",request.getUpdateUserErp());
        params.put("updateUserName",request.getUpdateUserName());
        unloadCarDao.distributeTaskByParams(params);
        return true;
    }

    @Override
    public boolean insertUnloadCar(TmsSealCar tmsSealCar) {

        UnloadCar unloadCar = new UnloadCar();
        Integer waybillNum = sendDatailDao.queryWaybillNumBybatchCodes(tmsSealCar.getBatchCodes());
        Integer packageNum = sendDatailDao.queryPackageNumBybatchCodes(tmsSealCar.getBatchCodes());
        unloadCar.setWaybillNum(waybillNum);
        unloadCar.setPackageNum(packageNum);
        unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
        unloadCar.setVehicleNumber(tmsSealCar.getVehicleNumber());
        unloadCar.setSealTime(tmsSealCar.getOperateTime());
        unloadCar.setStartSiteCode(tmsSealCar.getOperateSiteId());
        unloadCar.setStartSiteName(tmsSealCar.getOperateSiteName());

        CommonDto<SealCarDto> sealCarDto = vosQueryWS.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
        if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
            unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
            unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
        }
        unloadCar.setBatchCode(tmsSealCar.getBatchCodes().toString());
        int result = unloadCarDao.add(unloadCar);
        if (result > 0) {
            logger.info("插入成功");
        }
        return true;
    }

    @Override
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq) {

        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<UnloadCarTaskDto> unloadCarTaskDtos = new ArrayList<>();
        result.setData(unloadCarTaskDtos);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp",unloadCarTaskReq.getUser().getUserErp());
        params.put("endSiteCode",unloadCarTaskReq.getCurrentOperate().getSiteCode());
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(params);
        int serialNumber = 1;
        for (UnloadCar unloadCar : unloadCars) {
            UnloadCarTaskDto unloadCarTaskDto = new UnloadCarTaskDto();
            unloadCarTaskDto.setSerialNumber(serialNumber);
            unloadCarTaskDto.setTaskCode(unloadCar.getSealCarCode());
            unloadCarTaskDto.setCarCode(unloadCar.getVehicleNumber());
            unloadCarTaskDto.setPlatformName(unloadCar.getRailWayPlatForm());
            unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode());
            unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
            unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum());
            unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum());
            unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
            unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());

            serialNumber++;
            unloadCarTaskDtos.add(unloadCarTaskDto);
        }

        return result;
    }

    @Override
    public InvokeResult<Boolean> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sealCarCode",unloadCarTaskReq.getTaskCode());
        params.put("status",unloadCarTaskReq.getTaskStatus());
        params.put("unloadUserErp",unloadCarTaskReq.getUser().getUserErp());
        params.put("updateUserErp",unloadCarTaskReq.getUser().getUserErp());
        params.put("updateUserName",unloadCarTaskReq.getUser().getUserName());
        params.put("endSiteCode",unloadCarTaskReq.getCurrentOperate().getSiteCode());
        Date updateTime = DateHelper.parseDate(unloadCarTaskReq.getOperateTime());
        params.put("updateTime",updateTime);
        int count = unloadCarDao.updateUnloadCarTaskStatus(params);
        if (count > 0) {
            result.setData(Boolean.TRUE);
        } else {
            result.setData(Boolean.FALSE);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    public InvokeResult<List<HelperDto>> getUnloadCarTaskHelpers(String sealCarCode) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<HelperDto> helperDtos = new ArrayList<HelperDto>();
        result.setData(helperDtos);

        try {
            List<UnloadCarDistribution> unloadCarDistributions = unloadCarDistributionDao.selectUnloadCarTaskHelpers(sealCarCode);
            if (CollectionUtils.isEmpty(unloadCarDistributions)) {
                result.setCode(InvokeResult.RESULT_NULL_CODE);
                result.setMessage("未查询到协助人");
                logger.warn("该任务：{}未查询到协助人",sealCarCode);
                return result;
            }
            for (UnloadCarDistribution unloadCarDistribution : unloadCarDistributions) {
                HelperDto helper = new HelperDto();
                helper.setHelperERP(unloadCarDistribution.getUnloadUserErp());
                helper.setHelperName(unloadCarDistribution.getUnloadUserName());
                helperDtos.add(helper);
            }
        } catch (Exception e) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("该任务：{}查询协助人异常",sealCarCode);
            return result;
        }

        return result;
    }

    @Override
    public InvokeResult<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        List<HelperDto> helperDtos = new ArrayList<HelperDto>();
        result.setData(helperDtos);

        try {
            if (taskHelpersReq.getOperateType() == 0) {
                //删除协助人
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("sealCarCode",taskHelpersReq.getTaskCode());
                params.put("unloadUserErp",taskHelpersReq.getHelperERP());
                Date updateTime = DateHelper.parseDate(taskHelpersReq.getOperateTime());
                params.put("updateTime",updateTime);

                if (!unloadCarDistributionDao.deleteUnloadCarTaskHelpers(params)) {
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage("删除协助人失败");
                    return result;
                }

            } else if (taskHelpersReq.getOperateType() == 1) {
                //添加协助人
                UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
                unloadCarDistribution.setSealCarCode(taskHelpersReq.getTaskCode());
                unloadCarDistribution.setUnloadUserErp(taskHelpersReq.getHelperERP());
                unloadCarDistribution.setUnloadUserName(taskHelpersReq.getHelperName());
                unloadCarDistribution.setUnloadUserType(1);
                unloadCarDistribution.setCreateTime(DateHelper.parseDate(taskHelpersReq.getOperateTime()));

                int count = unloadCarDistributionDao.add(unloadCarDistribution);
                if (count < 1) {
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage("添加协助人失败");
                    return result;
                }
            }

        } catch (Exception e) {
            logger.error("更新协助人发生异常：{}", JsonHelper.toJson(taskHelpersReq));
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

        return this.getUnloadCarTaskHelpers(taskHelpersReq.getTaskCode());
    }

    @Override
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        List<UnloadCarTaskDto> unloadCarTaskDtos = new ArrayList<>();

        try {
            //根据责任人/协助人查找任务编码
            List<String> sealCarCodes = unloadCarDistributionDao.selectTasksByUser(taskHelpersReq.getUser().getUserErp());
            if (CollectionUtils.isEmpty(sealCarCodes)) {
                result.setCode(InvokeResult.RESULT_NULL_CODE);
                result.setMessage("未查询到任务，请检查");
                return result;
            }
            //根据任务编码查询
            List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskScan(sealCarCodes);
            if (CollectionUtils.isEmpty(unloadCars)) {
                result.setCode(InvokeResult.RESULT_NULL_CODE);
                result.setMessage("未查询到任务，请检查");
                return result;
            }
            int serialNumber = 1;
            for (UnloadCar unloadCar : unloadCars) {
                UnloadCarTaskDto unloadCarTaskDto = new UnloadCarTaskDto();
                unloadCarTaskDto.setSerialNumber(serialNumber);
                unloadCarTaskDto.setTaskCode(unloadCar.getSealCarCode());
                unloadCarTaskDto.setPlatformName(unloadCar.getRailWayPlatForm());
                unloadCarTaskDto.setCarCode(unloadCar.getVehicleNumber());
                unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode());
                unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
                unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum());
                unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum());
                unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
                unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());

                serialNumber++;
                unloadCarTaskDtos.add(unloadCarTaskDto);
            }
        } catch (Exception e) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("根据责任人或协助人查询任务失败：{}",JsonHelper.toJson(taskHelpersReq));
            return result;
        }

        return result;
    }

    private int getBatchNumber(String batchCode) {
        String[] batchList = batchCode.split(",");
        return batchList.length;
    }
}
