package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsNewSealVehicleService {

    /**
     * 按任务封车时：通过扫描的任务简码获取任务详细信息
     *
     * @param simpleCode
     * @return
     */
    TransWorkItemResponse getVehicleNumBySimpleCode(String simpleCode);

    /**
     * 封车校验：批次号及运力编码|任务号的校验
     * 1. 批次号校验：是否符合批次号编码规范
     * 2. 批次号校验：是否已经封车
     * 3. 批次号校验：是否有发货数据
     * 4. 按运力封车：校验运力编码目的地是否和批次号目的地一致
     *
     * @param transportCode
     * @param batchCode
     * @param sealCarType
     * @return
     */
    NewSealVehicleResponse newCheckTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType);

    /**
     * 封车
     *
     * @param request
     * @return
     */
    NewSealVehicleResponse seal(NewSealVehicleRequest request);

    /**
     * 根据查询条件（批次号，始发站点ID，车牌号3个至少输入一个）查询当前7天内的待解任务
     *
     * @param request
     * @return
     */
    NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request);

    /**
     * 解封车时校验车辆是否在电子围栏
     *
     * @param request
     * @return
     */
    NewSealVehicleResponse unsealCheck(NewSealVehicleRequest request);

    /**
     * 对选中的带解任务执行解封车
     *
     * @param request
     * @return
     */
    NewSealVehicleResponse unseal(NewSealVehicleRequest request);

}
