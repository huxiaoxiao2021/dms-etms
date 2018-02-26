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

}
