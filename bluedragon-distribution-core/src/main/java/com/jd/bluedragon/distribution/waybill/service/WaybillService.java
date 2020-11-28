package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.response.DmsWaybillInfoResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;

public interface WaybillService {

    BigWaybillDto getWaybill(String waybillCode);

    BigWaybillDto getWaybill(String waybillCode, boolean isPackList);

    BigWaybillDto getWaybillProduct(String waybillCode);

    /**
     * 获取运单状态接口
     */
    BigWaybillDto getWaybillState(String waybillCode);

    /***
     * 处理task_waybill的任务
     * @param task
     * @return
     */
    Boolean doWaybillStatusTask(Task task);

    /***
     * 处理task_pop的任务
     * @param task
     * @return
     */
    Boolean doWaybillTraceTask(Task task);

    /**
     * 根据包裹号获取包裹体积重量信息
     *
     * @param packageCode
     * @return
     */
    public WaybillPackageDTO getWaybillPackage(String packageCode);

    /**
     * 查询运单是否可以进行逆向操作
     *
     * @param waybillCode 运单号
     * @param siteCode 操作站点
     * @return true:可以操作逆向操作 false:反之
     */
    public InvokeResult<Boolean> isReverseOperationAllowed(String waybillCode, Integer siteCode) throws Exception;

    /**
     * 获取商品明细和运单状态接口
     */
    BigWaybillDto getWaybillProductAndState(String waybillCode);

    /**
     * 根据waybillSign获取运单类型
     * @param waybillCode
     * @return
     */
    Integer getWaybillTypeByWaybillSign(String waybillCode);

    /**
     * 判断是否移动仓内配单
     * @param waybillCode
     * @return
     */
    boolean isMovingWareHouseInnerWaybill(String waybillCode);

    /**
     * 获取运单信息 并校验超区逻辑
     * @param packageCode
     * @return
     */
    DmsWaybillInfoResponse getDmsWaybillInfoAndCheck(String packageCode);

    /**
     * 本意是判断：是否是疫情超区 或者 春节禁售。此逻辑在预分拣侧控制。分拣只根据预分拣网点是-136做拦截限制
     * @param waybill
     * @return true 是，false 不是
     */
    boolean isOutZoneControl(Waybill waybill);

    /**
     * 获取运单信息
     * @param packageCode
     * @return
     */
    DmsWaybillInfoResponse getDmsWaybillInfoResponse(String packageCode);

    Waybill getWaybillByWayCode(String waybillCode);

    /**
     * 三方验货校验运单取消拦截
     *
     * @param pdaOperateRequest
     * @return
     */
    InvokeResult<Boolean> thirdCheckWaybillCancel(PdaOperateRequest pdaOperateRequest);

    /*
     * 是否是特安送服务的运单
     * 33位等于2，且增值服务中某个对象的vosNo=fr-a-0010
     * */
    boolean isSpecialRequirementTeAnSongService(String waybillCode, String waybillSign);

    JdCancelWaybillResponse dealCancelWaybill(String waybillCode);

    JdCancelWaybillResponse dealCancelWaybill(PdaOperateRequest pdaOperate);

    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    BlockResponse checkWaybillBlock(String waybillCode, Integer featureType);

    BlockResponse checkPackageBlock(String packageCode, Integer featureType);

    Integer getRouterFromMasterDb(String waybillCode, Integer createSiteCode);

}
