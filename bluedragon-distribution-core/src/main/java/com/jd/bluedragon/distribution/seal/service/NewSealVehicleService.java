package com.jd.bluedragon.distribution.seal.service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vos.dto.SealCarInAreaDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;

import java.util.List;

public interface NewSealVehicleService {

    public static final String MESSAGE_OFFLINE_SEAL_SUCCESS = "离线封车成功!";
    public static final String MESSAGE_SEAL_SUCCESS = "封车成功!";
    public static final String MESSAGE_UNSEAL_SUCCESS = "解封车成功!";

    /**
     * 封车
     *
     * @param sealCars
     * @return
     */
    public CommonDto<String> seal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception;

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
     * 检查批次是否存在
     *
     * @param sendCode
     * @return
     */
    public boolean checkSendIsExist(String sendCode);

    /**
     * 离线封车
     *
     * @param sealCars
     * @return
     */
    public CommonDto<String> offlineSeal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars);

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
     * 获取批次号的封车时间，若为null则表示未封车
     *
     * @param sendCode
     * @return
     */
    Long getSealCarTimeBySendCode(String sendCode);

}
