package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.Rule;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigResponse;

import java.util.List;
import java.util.Map;

public interface JsfSortingResourceService {
	SortingJsfResponse check(SortingCheck sortingCheck);
    SortingJsfResponse isCancel(String packageCode);
    List<MixedPackageConfigResponse> getMixedConfigsBySitesAndTypes(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType);
    Integer getWaybillCancelByWaybillCode(String waybillCode);
    String getRouterByWaybillCode(String waybillCode);
    BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest request);
    /**
     * 批量查询路由
     * @param waybillCodes
     * @return
     */
    Map<String,String> getRouterByWaybillCodes(List<String> waybillCodes);

    /**
     * 校验滑道号
     * @return true 滑道号正确，false 不正确
     */
    Boolean checkPackageCrossCode(String waybillCode, String packageCode);
    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    BlockResponse checkWaybillBlock(String waybillCode, Integer featureType);
    /**
     * 查询包裹是否拦截完成
     * @param packageCode
     * @param featureType
     * @return
     */
    BlockResponse checkPackageBlock(String packageCode, Integer featureType);
    /**
     * 发货校验
     * @param request
     * @return
     */
    JdResult packageSendCheck(DeliveryRequest request);
    /**
     * 发货前扫单校验，适用于老发货、快运发货
     * @param request
     * @return
     */
    JdResult<CheckBeforeSendResponse> checkBeforeSend(DeliveryRequest request);

    /**
     * 校验包裹或订单是否有称重量方
     */
    Boolean weightVolumeValidate(String waybillCode, String packageCode);

    /**
     * 查询分拣规则
     * @param ruleType
     * @param createSiteCode
     * @return
     */
    Rule getSortingRule(Integer ruleType, Integer createSiteCode);

    /**
     * 查询某个分拣中心的分拣规则
     * @param createSiteCode
     * @return
     */
    Map<String, Rule> getSiteSortingRule(Integer createSiteCode);

    /**
     * 根据key获取UCC配置结果
     * @param configureKey
     * @return
     */
    Boolean getUccConfigurationByKey(String configureKey);

    /**
     * 根据key获取配置文件配置
     * @param configureKey
     * @return
     */
     String getFileConfigurationByKey(String configureKey) ;

    /**
     * 检查混装箱是否可以通过校验
     * @param createSiteCode    建包分拣中心编码
     * @param receiveSiteCode    目的分拣中心编码
     * @param mixedSiteCode     可混装地区编码
     * @param transportType     运输类型
     * @return 通过true ，不通过false
     */
    Boolean checkMixedPackageConfig(Integer createSiteCode,Integer receiveSiteCode,Integer mixedSiteCode,Integer transportType,Integer ruleType);


    /**
     * 获取运单拦截信息
     * @param waybillCode
     * @return
     */
    JdResponse dealCancelWaybill(String waybillCode);

    /**
     * 获取运单拦截并推送拦截信息
     * @param pdaOperateRequest
     * @return
     */
    JdResponse dealCancelWaybillByRequest(PdaOperateRequest pdaOperateRequest);
}
