package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiFailDetailDto;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;

import java.util.List;
import java.util.Map;

/**
 * 发往物流网关的接口不要在此类中加方法
 */
public interface DmsExternalService {

    ////////////////////////////////PopAbnormalWssService//////////////////////

    /**
     * 商家更新abnormalorder包裹数量
     */
    public Boolean updatePopPackNum(String message);

    ////////////////////////////SealVehicleBoxService///////////////////

    /**
     * 批量增加封车信息
     *
     * @param sealVehicleList
     * @return
     */
    public BaseEntity<Map<String, Integer>> batchAddSealVehicle(
            List<SealVehicleDto> sealVehicleList);

    /**
     * 批量验证并保存解封车信息
     *
     * @param sealVehicleList
     * @return
     */
    public BaseEntity<Map<String, Integer>> batchUpdateSealVehicle(
            List<SealVehicleDto> sealVehicleList);

    /**
     * 批量保存封箱信息
     *
     * @param sealBoxList
     * @return
     */
    public BaseEntity<Map<String, Integer>> batchAddSealBox(
            List<SealBoxDto> sealBoxList);
    /**
     * 执行json格式的的指令
     * @param jsonCommand
     * @return
     */
    String executeJsonCommand(String jsonCommand);

    /**
     * 航空转陆运异常提交
     * @param arAbnormalRequest
     * @return
     */
    ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest);

    /**
     * 加盟商交接
     *
     * 优先判断交接池是否已校验过，如果校验过则不校验加盟商预付款余额
     * 未校验过则校验加盟商预付款余额是否充足，充足则直接记录称重信息和交接信息
     * 不充足则返回失败提示调用者
     * 如果校验池已校验过则直接记录称重信息和交接信息
     *
     * @param dto
     * @return  List<AllianceBusiFailDetailDto> 部分失败列表
     */
    BaseEntity<List<AllianceBusiFailDetailDto>> allianceBusiDelivery(AllianceBusiDeliveryDto dto);

}