package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.request.*;
import com.jd.bluedragon.common.dto.blockcar.response.PreSealVehicleMeasureDto;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.distribution.api.domain.TransAbnormalTypeDto;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.NewUnsealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.SealCodesResponse;

import java.util.List;

/**
 * 封车发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/8
 */
public interface NewSealVehicleGatewayService {
    JdCResponse cancelSeal(CancelSealRequest gatewayRequest);

    JdVerifyResponse<SealCarTaskInfoDto> getTaskInfo(SealCarTaskInfoRequest request);

    JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request);

    JdCResponse<TransportInfoDto> getTransportInfoByCode(CapacityInfoRequest request);

    JdCResponse checkTransportCode(CheckTransportCodeRequest request);

    JdCResponse checkTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType);

    JdCResponse newCheckTranCodeAndBatchCode(SealCarPreRequest sealCarPreRequest);

    JdCResponse sealCar(SealCarRequest sealCarRequest);

    JdCResponse verifyVehicleJobByVehicleNumber(String transportCode, String vehicleNumber, Integer sealCarType);

    JdVerifyResponse<Void> newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest);

    JdCResponse verifySendVolume(SealVehicleVolumeVerifyRequest request);

    JdCResponse doSealCarWithVehicleJob(SealCarRequest sealCarRequest);

    /**
     * 传摆预封车接口
     * @param sealCarRequest
     * @return
     */
    JdVerifyResponse<Void> preSealFerry(SealCarRequest sealCarRequest);

    /**
     * 传摆预封车更新服务
     * @param sealCarRequest
     * @return
     */
    JdCResponse updatePreSealFerry(SealCarRequest sealCarRequest);

    /**
     * 根据运力编码获取车辆信息（车牌、重量体积）
     * @param transportCode
     * @return
     */
    JdCResponse<PreSealVehicleMeasureDto> getVehicleNumBySimpleCode(String transportCode);

    /**
     * 更新重量体积
     * @param request
     * @return
     */
    JdCResponse updatePreSealVehicleMeasureInfo(PreSealMeasureInfoRequest request);

    /**
     * 剔除没有发货记录的批次号。返回值为被剔除的批次列表
     * @param request
     * @return
     */
    JdCResponse<List<String>> removeEmptyBatchCode(List<String> request);


    /**
     * 取消预封车
     * @param request
     * @return
     */
    public JdCResponse<Boolean> cancelPreBlockCar(CancelPreBlockCarRequest request);
    /**
     * 获取预封车-待封车批次信息
     * @param request
     * @return
     */
    JdCResponse<List<String>> getUnSealSendCodes(SealCarPreRequest request);

    /**
     * 查询待解封的封签方法（无到货解封签专用）
     * @param param
     * @return
     */
    JdCResponse<SealCodesResponse> querySealCodes(SealCodeRequest param);

    /**
     * 无货解封签（无到货解封签专用）
     * @param request
     * @return
     */
    JdCResponse<String> doDeSealCodes(DeSealCodeRequest request);

    /**
     * 解封签异常提报
     * @param param
     * @return
     */
    JdCResponse<String> createTransAbnormalStandard (TransAbnormalDto param);

    /**
     * 获取解封签异常提报的类型枚举
     * @return
     */
    JdCResponse<List<TransAbnormalTypeDto>> getTransAbnormalTypeCode ();

    /**
     * 提报异常，并执行无到货解封签
     * @return
     */
    JdCResponse<String> createTransAbnormalAndDeSealCode(TransAbnormalAndDeSealRequest request);

    /**
     * 提报异常，并执行解封车校验 和 解封车
     * @return
     */
    NewUnsealVehicleResponse<Boolean> createTransAbnormalAndUnseal(TransAbnormalAndUnsealRequest request);
    /**
     * 无货上封签
     * @return
     */
    NewSealVehicleResponse<String> doSealCodes(DoSealCodeRequest request);
}
