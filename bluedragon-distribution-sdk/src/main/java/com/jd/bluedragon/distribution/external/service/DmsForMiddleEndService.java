package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.Rule;
import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;

import java.util.Map;

public interface DmsForMiddleEndService {
    /**
     * 校验金鹏订单是否已经发货
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    InvokeResult<Boolean> checkJPWaybillIsSent(String waybillCode, String waybillSign);

    /**
     * 判断箱号是否已经发货
     * @param boxCode
     * @return
     */
    InvokeResult<Boolean> checkBoxIsSent(String boxCode);

    /**
     * 获取派车单信息
     * @param boxCode
     * @param waybillCode
     * @return
     */
    InvokeResult<TransBillScheduleResponse> checkScheduleBill(String boxCode, String waybillCode);

    /**
     * 校验包裹或订单是否有称重量方
     * @param waybillCode
     * @param packageCode
     * @return
     */
    InvokeResult<Boolean> weightVolumeValidate(String waybillCode, String packageCode);

    /**
     * 获取分拣规则配置
     * @param ruleType
     * @param createSiteCode
     * @return
     */
    InvokeResult<Rule> getSortingRule(Integer ruleType, Integer createSiteCode);

    /**
     * 查询某个分拣中心的分拣规则
     * @param createSiteCode
     * @return
     */
    InvokeResult<Map<String, Rule>> getSiteSortingRule(Integer createSiteCode);

    /**
     * 根据key获取UCC配置结果
     * @param configureKey
     * @return
     */
    InvokeResult<Boolean> getUccConfigurationByKey(String configureKey);

    /**
     * 根据key获取配置文件配置
     * @param configureKey
     * @return
     */
    InvokeResult<Object> getFileConfigurationByKey(String configureKey);

    /**
     * 检查混装箱是否可以通过校验
     * @param createSiteCode    建包分拣中心编码
     * @param receiveSiteCode    目的分拣中心编码
     * @param mixedSiteCode     可混装地区编码
     * @param transportType     运输类型
     * @return 通过true ，不通过false
     */
    InvokeResult<Boolean> checkMixedPackageConfig(Integer createSiteCode,Integer receiveSiteCode,Integer mixedSiteCode,Integer transportType,Integer ruleType);

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

    /**
     * 根据运单号获取路由
     * @param waybillCode
     * @return
     */
    InvokeResult<String> getRouterByWaybillCode(String waybillCode);
}
