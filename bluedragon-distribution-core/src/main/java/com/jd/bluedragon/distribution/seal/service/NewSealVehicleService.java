package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.tms.tfc.dto.TransBookBillQueryDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.dto.TransWorkItemWsDto;

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
    public com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> getTransportResourceByTransCode(String batchCode);

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
     * 根据任务简码查询任务信息
     *
     * @param simpleCode
     * @return
     * @throws Exception
     */
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) throws Exception;

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
     * 获取批次号的封车时间，若为null或者小于等于0则表示未封车
     *
     * @param sendCode
     * @return
     */
    Long getSealCarTimeBySendCode(String sendCode);

    /**
     * 根据预约提货时间等条件获取委托书列表接口
     * @param transBookBillQueryDto
     * @param pageDto
     * @return
     * @throws Exception
     */
    com.jd.tms.tfc.dto.CommonDto<com.jd.tms.tfc.dto.PageDto<com.jd.tms.tfc.dto.TransBookBillResultDto>> getTransBookBill(com.jd.tms.tfc.dto.TransBookBillQueryDto transBookBillQueryDto, com.jd.tms.tfc.dto.PageDto<TransBookBillQueryDto> pageDto) throws Exception;

    /**
     * 根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新
     * @param simpleCode
     * @param transportCode
     * @return
     * @throws Exception
     */
    com.jd.tms.tfc.dto.CommonDto<String> checkTransportCode(String simpleCode, String transportCode) throws Exception;

    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     * @param transWorkItemWsDto
     * @return
     * @throws Exception
     */
    com.jd.tms.tfc.dto.CommonDto<TransWorkItemWsDto> getVehicleNumberOrItemCodeByParam(TransWorkItemWsDto transWorkItemWsDto) throws Exception;

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

}
