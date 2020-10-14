package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.vos.dto.*;

/**
 * @author huangliang
 */
public interface VosManager {

    /**
     * 车辆电子围栏（VOS）: 测试分组：VOS-TEST，线上分组： VOS-JSF
     *
     * @param dto
     * @return
     */
    public abstract CommonDto<List<SendCarInfoDto>> getSendCar(SendCarParamDto dto);

    /***
     * 根据运输计划号获取运输计划详情
     * @param carriagePlanCode
     * @return
     */
    public abstract CommonDto<CarriagePlanDto> queryCarriagePlanDetails(String carriagePlanCode);

    /**
     * 根据批次号获取封车信息
     *
     * @param batchCode 批次号
     * @return
     */
    SealCarDto querySealCarByBatchCode(String batchCode) throws Exception;


    /**
     * VOS封车业务同时生成车次任务
     * @param sealCarDto
     * @return
     */
    CommonDto<String> doSealCarWithVehicleJob(SealCarDto sealCarDto);

    /**
     * VOS校验车牌号能否封车创建车次任务
     * @param verifyVehicleJobDto
     * @return
     */
    CommonDto<String> verifyVehicleJobByVehicleNumber(VerifyVehicleJobDto  verifyVehicleJobDto);

    /**
     * VOS创建预封车任务
     * @param preSealVehicleJobDto
     * @return
     */
    CommonDto<String> doPreSealVehicleJob(PreSealVehicleJobDto preSealVehicleJobDto);

    /**
     * VOS取消预封车任务
     * @param preSealVehicleJobDto
     * @return
     */
    CommonDto<String> cancelPreSealVehicleJob(PreSealVehicleJobDto preSealVehicleJobDto);

    /**
     * 根据封车编码获取封车信息
     * @param sealCarCode 封车编码
     * @return
     */
    CommonDto<SealCarDto> querySealCarInfoBySealCarCode(String sealCarCode);
}
