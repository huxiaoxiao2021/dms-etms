package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.api.response.DmsWaybillInfoResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.util.Date;
import java.util.List;

public interface WaybillService {

    BigWaybillDto getWaybill(String waybillCode);

    BigWaybillDto getWaybill(String waybillCode, boolean isPackList);
    
    BigWaybillDto getWaybill(String waybillCode, boolean isPackList, boolean isExtend);

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

    /**
     * 理赔破损拦截专用拦截判断方法
     * 1. 仅有破损拦截时，不允许换单
     * 1. 收到可换单消息后，可以换单
     * @param waybillCode 运单号
     */
    JdCancelWaybillResponse dealCancelWaybillWithClaimDamaged(String waybillCode);

    JdCancelWaybillResponse dealCancelWaybill(PdaOperateRequest pdaOperate);

    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    BlockResponse checkWaybillBlock(String waybillCode, Integer featureType);

    BlockResponse checkPackageBlock(String packageCode, Integer featureType);

    /**
     * 根据featureType查询拦截
     *
     * @param packageCode
     * @param featureTypes
     * @return
     */
    BlockResponse checkPackageBlockByFeatureTypes(String packageCode, List<Integer> featureTypes);
    BlockResponse checkWaybillBlockByFeatureTypes(String waybillCode, List<Integer> featureTypes);

    Integer getRouterFromMasterDb(String waybillCode, Integer createSiteCode);

    String getRouterByWaybillCode(String waybillCode);

    /**
     * 现场预分拣拦截校验
     * @param waybillForPreSortOnSiteRequest
     * @return
     */
    InvokeResult<String> checkWaybillForPreSortOnSite(WaybillForPreSortOnSiteRequest waybillForPreSortOnSiteRequest);

    /**
     * 判断满足文件类型的拦截条件
     * @param subType
     * @param waybillSign
     * @return
     */
    boolean allowFilePackFilter(Integer subType, String waybillSign);

    /**
     * 百川业务开关，判断omcOrderCode是否有值，有值代表开启百川业务
     * @param waybill
     * @return
     */
    String baiChuanEnableSwitch(Waybill waybill);


    /**
     * 获取运单路由最后一段或者第一段code值
     * @param waybillCode 运单编码
     * @param locationFlag 位置标识 0 代表第一节点路由信息 -1代表最后
     * @return
     */
    Integer getFinalOrFirstRouterFromDb(String waybillCode,int locationFlag);

    /**
     * 根据传入的站点code判断在路由第一节点 还是最后一个路由节点
     * @param operateSiteCode 传入站点code
     * @param waybillCode     运单单号
     * @param locationFlag -1最后一个节点 0第一个节点
     * @return
     */
    boolean isStartOrEndSite(Integer operateSiteCode,String waybillCode,int locationFlag);
    /**
     * 判断单号是否存在补打拦截
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    boolean hasPrintIntercept(String waybillCode,String waybillSign);

    /**
     * 仅获取存在理赔破损拦截，包含取消破损拦截场景
     * @param waybillCode
     * @return
     */
    CancelWaybill checkClaimDamagedCancelWaybill(String waybillCode);

    /**
     * 判断是否运单是否包含易冻品增值服务
     * @param waybillCode
     * @return
     */
    boolean isEasyFrozenVosWaybill(String waybillCode,String waybillSign);

    /**
     * 根据运单、操作时间、操作场地 检查易冻品
     * @param waybillCode
     * @param operateTime
     * @param siteCode
     * @return
     */
    InvokeResult<Boolean> checkEasyFreeze(String waybillCode, Date operateTime, Integer siteCode);

    /**
     * 判断运单是否包含特保单增值服务
     * @param waybillCode
     * @return
     */
    boolean isLuxurySecurityVosWaybill(String waybillCode);

    /**
     * 根据运单号检查是否属于 特保单
     * @param waybillCode
     * @return
     */
    InvokeResult<Boolean> checkLuxurySecurity(Integer siteCode,String waybillCode, String waybilSign);

    /**
     * 匹配是否为终端场地操作得物类型返调度操作条件
     * @param customerCode 商家编号
     * @param siteCode 场地编码
     * @return
     */
    boolean matchTerminalSiteReSortDewuCondition(String customerCode, Integer siteCode);

}
