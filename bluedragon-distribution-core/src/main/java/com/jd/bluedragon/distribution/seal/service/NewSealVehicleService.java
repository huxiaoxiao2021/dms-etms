package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.distribution.api.domain.TransAbnormalTypeDto;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.SealCodesResponse;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.tms.basic.dto.TransportResourceDto;

import java.util.List;
import java.util.Map;

public interface NewSealVehicleService {

    public static final String MESSAGE_OFFLINE_SEAL_SUCCESS = "离线封车成功!";
    public static final String MESSAGE_SEAL_SUCCESS = "封车成功!";
    public static final String MESSAGE_CANCEL_SEAL_SUCCESS = "取消封车成功!";
    public static final String MESSAGE_UNSEAL_SUCCESS = "解封车成功!";
    public static final String MESSAGE_ONE_CLICK_FERRY_SEAL_SUCCESS = "一键传摆封车成功!";

    /**
     * 封车
     *
     * @param sealCars
     * @return
     */
    public CommonDto<String> seal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars,Map<String, String> emptyBatchCode) throws Exception;

    /**
     * VOS封车业务同时生成车次任务
     * @param sealCars
     * @return
     */
    NewSealVehicleResponse doSealCarWithVehicleJob(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars,Map<String, String> emptyBatchCode);

    /*
    * 分拣工作台一键封车
    * */
    NewSealVehicleResponse doSealCarFromDmsWorkBench(List<SealCarDto> sealCarDtoList);
    /**
     * 取消封车
     * @param request
     * @return
     * @throws Exception
     */
    public NewSealVehicleResponse cancelSeal(cancelSealRequest request);

    /**
     * 查询待解任务
     *
     * @param request
     * @param pageDto
     * @return
     */
    public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request, PageDto<SealCarDto> pageDto) throws Exception;

    /**
     * 解封车
     *
     * @param sealCars
     * @return
     */
    public CommonDto<String> unseal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception;

    /**
     * VOS查询批次号是否已被封车接口
     *
     * @param batchCode
     * @return
     */
    public CommonDto<Boolean> isBatchCodeHasSealed(String batchCode);

    /**
     * 根据运力编码查询运力编码相关信息
     *
     * @param batchCode
     * @return
     */
    public com.jd.tms.basic.dto.CommonDto<TransportResourceDto> getTransportResourceByTransCode(String batchCode);

    /**
     * 校验批次的体积是否超标
     * @param sealCarDto
     * @return
     * @throws Exception
     */
    CommonDto<String> verifySealVehicleVolume(SealCarDto sealCarDto);

    /**
     * 校验车牌号能否生成车次任务
     * @param transportCode 运力编码
     * @param vehicleNumber 车牌号
     * @return
     */
    CommonDto<String> verifyVehicleJobByVehicleNumber(String transportCode,String vehicleNumber);


    /**
     * 校验车牌号能否生成车次任务（新）
     * @param sealCarPreRequest
     * @return
     */
    CommonDto<String> newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest);

    /**
     * 离线封车
     *
     * @param sealCars
     * @return
     */
    public CommonDto<String> offlineSeal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars);

    /**
     * 离线传摆封车
     *
     * @param sealCars
     * @return
     */
    public NewSealVehicleResponse oneClickFerrySeal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars);

    /**
     * 查询相关车辆是否在分拣中心的电子围栏内,返回不在围栏的车牌号
     *
     * @param sealCars 待解的封车任务
     * @return
     */
    public List<String> isSealCarInArea(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception;

    /**
     * 校验批次号是否封车:默认返回false
     *
     * @param sendCode
     * @return
     */
    boolean checkSendCodeIsSealed(String sendCode);

    /**
     * 校验批次号是否封车<br>
     * <b>包含西藏模式逻辑，调用ITMS系统判断批次号状态</b>
     *
     * @param sendCode 批次号
     * @param customMessage 自定义的提示语
     * @return 返回true不拦截，返回false需要拦截
     */
    boolean newCheckSendCodeSealed(String sendCode, StringBuffer customMessage);

    /**
     * 获取批次号的封车时间，若为null或者小于等于0则表示未封车
     *
     * @param sendCode
     * @return
     */
    Long getSealCarTimeBySendCode(String sendCode);

    /**
     * 查询全部的未封车批次号
     */
    List<String> getUnSealSendCodeList(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange);

    /**
     * 封车判断批次号内是否有发货记录
     * @param batchCode
     * @return
     */
    boolean checkBatchCodeIsNewSealVehicle(String batchCode);

    /**
     * 一键封车判断批次号内是否有发货记录
     * @param batchCode
     * @return
     */
    boolean checkBatchCodeIsSendPreSealVehicle(String batchCode);
    /**
     * 获取未封车批次号列表信息
     * @param request
     * @return
     */
	JdResult<List<String>> getUnSealSendCodes(NewSealVehicleRequest request);

    /**
     * 查询待解封签信息（无到货解封签专用）
     * @param request
     * @return
     */
	NewSealVehicleResponse<SealCodesResponse> querySealCodes(SealCodeRequest request);

    /**
     * 执行无到货解封签（无到货解封签专用）
     * @param request
     * @return
     */
	NewSealVehicleResponse<String> doDeSealCodes(DeSealCodeRequest request);

    /**
     * 运输异常提报接口
     * @param transAbnormalDto
     * @return
     */
    NewSealVehicleResponse<String> createTransAbnormalStandard(TransAbnormalDto transAbnormalDto);


    /**
     * 获取运输异常提报类型 1031,4,1323
     * @return
     */
    NewSealVehicleResponse<List<TransAbnormalTypeDto>> getTransAbnormalTypeCode();

    /**
     * 提报异常，并执行无到货解封签
     * @see #createTransAbnormalStandard(TransAbnormalDto)
     * @see #doDeSealCodes(DeSealCodeRequest) 
     * @return
     */
    NewSealVehicleResponse<String> createTransAbnormalAndDeSealCode(TransAbnormalAndDeSealRequest request);
    /**
     * 无货上封签
     * @param request
     * @return
     */
    NewSealVehicleResponse<String> doSealCodes(DoSealCodeRequest request);

}
