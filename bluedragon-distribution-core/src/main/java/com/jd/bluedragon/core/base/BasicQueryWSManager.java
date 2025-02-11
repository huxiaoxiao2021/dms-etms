package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.BasicVehicleDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.ConfNodeCarrierDto;

import java.util.List;

/**
 * 运输基础字段接口包装
 *
 * @author : xumigen
 * @date : 2019/9/16
 */
public interface BasicQueryWSManager {

    /**
     * 根据网点编码查询承运商
     *
     * @param nodeCode 始发车站
     * @return
     */
    ConfNodeCarrierDto getCarrierByNodeCode(String nodeCode);

    /**
     * BASIC数据字典查询接口
     *
     * @param parentCode
     * @param dictLevel
     * @param dictGroup
     * @return
     */
    List<BasicDictDto> getDictList(String parentCode, int dictLevel, String dictGroup);

    /**
     * 获取车型查询接口
     *
     * @param owner 所有者：1-配运，2-仓运，Y-预约 空串""或nulll查询所有
     * @param type 所有：0 ，冷链：1 ，非冷链：2
     * @return
     */
    List<BasicDictDto> getVehicleTypeByType(String owner, int type);
    /**
     * 根据车牌号获取车辆信息
     * @param vehicleNumber
     * @return
     */
    public BasicVehicleDto getVehicleByVehicleNumber(String vehicleNumber);


    /**
     * 根据车牌号获取车型配置信息
     * @param vehicleNum
     * @return
     */
    BasicVehicleTypeDto getVehicleTypeByVehicleNum(String vehicleNum);

    /**
     * 根据车型获取车型配置信息
     *
     * @param vehicleType
     * @return
     */
    BasicVehicleTypeDto getVehicleTypeByVehicleType(Integer vehicleType);
}
