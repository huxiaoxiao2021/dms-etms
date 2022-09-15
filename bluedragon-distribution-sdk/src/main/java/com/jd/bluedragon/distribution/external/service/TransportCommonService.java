package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;

import java.util.List;
import java.util.Map;

/**
 * 转运依赖分拣相关服务
 */
public interface TransportCommonService {

    /**
     * 卸车的拦截校验
     * code=200接口调用成功，data=true  需要拦截，，拦截消息看msg
     */
    InvokeResult<Boolean> interceptValidateUnloadCar(TransportServiceRequest transportServiceRequest);

    /**
     * VER组板拦截校验
     * @param request 组板检查请求
     * @return 校验结果
     */
    InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request);

    /**
     * 判断PDA登录ERP或登录ERP所属场地是否有配置验货/发货白名单
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<Boolean> hasInspectOrSendFunction(TransportServiceRequest transportServiceRequest);

    /**
     * 获取路由下一场地编码
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<Integer> getRouterNextSiteId(TransportServiceRequest transportServiceRequest);

    /**
     * 根据运单号获取waybill表路由字段
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<String> getRouterByWaybillCode(TransportServiceRequest transportServiceRequest);

    /**
     * 加盟商余额校验
     * @param waybillCode 运单号
     * @return 校验结果
     */
    InvokeResult<Boolean> checkAllianceMoney(String waybillCode);

    /**
     * 获取已发货批次下和指定运单下的包裹号
     * @param createSiteCode 操作站点
     * @param batchCode 批次号
     * @param waybillCode 运单号
     * @return
     */
    InvokeResult<List<String>> queryPackageCodeBySendAndWaybillCode(Integer createSiteCode, String batchCode, String waybillCode);

    /**
     * 获取已发货批次下的包裹号
     * @param createSiteCode 操作站点
     * @param batchCode 批次号
     * @return
     */
    InvokeResult<List<String>> queryPackageCodeBySendCode(Integer createSiteCode, String batchCode);

    /**
     * 获取已发货批次下的包裹号
     * @param createSiteCode 操作站点
     * @param receiveSiteCode 下一站点
     * @param barCode 包裹号或运单号
     * @return
     */
    InvokeResult<String> findByWaybillCodeOrPackageCode(Integer createSiteCode, Integer receiveSiteCode, String barCode);

    /**
     * 获取已发货批次下的运单总数和包裹总数
     * @param createSiteCode 操作站点
     * @param batchCodes 批次号集合
     * @return
     */
    InvokeResult<Map<String, Integer>> queryPackageAndWaybillNumByBatchCodes(Integer createSiteCode, List<String> batchCodes);

    /**
     * 根据场地信息查询所有的月台列表
     * @param siteCode
     * @return
     */
    InvokeResult<List<DockInfoEntity>> listAllDockInfoBySiteCode(Integer siteCode);

    /**
     * 根据站点和月台号获取月台信息
     * @param siteCode
     * @param dockCode
     * @return
     */
    InvokeResult<DockInfoEntity> findDockInfoByDockCode(Integer siteCode, String dockCode);

    /**
     * 查询集包下的包裹总数
     * @param boxCode
     * @return
     */
    InvokeResult<Integer> getSumByBoxCode(String boxCode);

    /**
     * 查询集包下的包裹号
     * 分页
     * @param request
     * @return
     */
    InvokeResult<List<String>> getPagePackageNoByBoxCode(SortingPageRequest request);

    /**
     * 保存操作版本， 不成功给返回提示
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<Boolean> saveOperatePdaVersion(String sealCarCode, String pdaVersion);

    /**
     *
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<Boolean> delOperatePdaVersion(String sealCarCode, String pdaVersion);


    /**
     *
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<String> getOperatePdaVersion(String sealCarCode);


    /**
     * 根据组编码查询组成员
     * @param groupCode
     * @return
     */
    InvokeResult<List<JyGroupMemberEntity>> queryMemberListByGroup(String groupCode);

}
