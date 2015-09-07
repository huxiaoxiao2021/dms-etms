package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;

import java.util.List;
import java.util.Map;

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


}