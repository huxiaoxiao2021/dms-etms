package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.Rule;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.external.service.DmsForMiddleEndService;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.jsf.service.CancelWaybillJsfService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.fastjson.JSON;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("dmsForMiddleEndService")
public class DmsForMiddleEndServiceImpl implements DmsForMiddleEndService {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    @Qualifier("storagePackageMService")
    private StoragePackageMService storagePackageMService;

    @Autowired
    private BoxService boxService;


    @Autowired
    private TransBillScheduleService transBillScheduleService;


    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    @Qualifier("cancelWaybillJsfService")
    private CancelWaybillJsfService cancelWaybillJsfService;


    /**
     * 校验金鹏订单是否已经发货
     *
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkJPWaybillIsSent(String waybillCode, String waybillSign) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if (StringUtils.isBlank(waybillCode)) {
            result.parameterError("参数错误--运单号为空");
            return result;
        }
        if (StringUtils.isBlank(waybillSign)) {
            result.parameterError("参数错误--waybillSign为空");
            return result;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkJPWaybillIsSent",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(storagePackageMService.checkWaybillCanSend(waybillCode, waybillSign));
        } catch (Exception e) {
            logger.error("checkJPWaybillIsSent执行异常.参数waybillCode:" + waybillCode + ",waybillSign:" + waybillSign + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return result;
    }

    /**
     * 判断箱号是否已经发货
     *
     * @param boxCode
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkBoxIsSent(String boxCode) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<Boolean>();
        invokeResult.success();

        if (StringUtils.isBlank(boxCode)) {
            invokeResult.parameterError("参数错误--箱号为空");
            return invokeResult;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkBoxIsSent",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            Box box = this.boxService.findBoxByCode(boxCode);
            if (box != null) {
                invokeResult.setData(boxService.checkBoxIsSent(boxCode, box.getCreateSiteCode()));
            } else {
                invokeResult.customMessage(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("checkBoxIsSent执行异常.参数boxCode:" + boxCode + ".", e);
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return invokeResult;
    }

    /**
     * 获取派车单信息
     *
     * @param boxCode
     * @param waybillCode
     * @return
     */
    public InvokeResult<TransBillScheduleResponse> checkScheduleBill(String boxCode, String waybillCode) {
        InvokeResult<TransBillScheduleResponse> invokeResult = new InvokeResult<TransBillScheduleResponse>();
        invokeResult.success();

        if (StringUtils.isBlank(boxCode)) {
            invokeResult.parameterError("参数错误--箱号为空");
            return invokeResult;
        }

        if (StringUtils.isBlank(waybillCode)) {
            invokeResult.parameterError("参数错误--运单号为空");
            return invokeResult;
        }

        TransBillScheduleRequest request = new TransBillScheduleRequest();
        request.setBoxCode(boxCode);
        request.setWaybillCode(waybillCode);

        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkScheduleBill",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            TransBillScheduleResponse response = transBillScheduleService.checkScheduleBill(request);
            invokeResult.setData(response);
        } catch (Exception e) {
            logger.error("checkScheduleBill执行异常.参数boxCode:" + boxCode + ",waybillCode:" + waybillCode + ".", e);
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return invokeResult;
    }

    /**
     * 校验包裹或订单是否有称重量方
     *
     * @param waybillCode
     * @param packageCode
     * @return
     */
    public InvokeResult<Boolean> weightVolumeValidate(String waybillCode, String packageCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();

        if (StringUtils.isBlank(waybillCode) && StringUtils.isBlank(packageCode)) {
            result.parameterError("参数错误--运单号或者包裹号同时为空");
            return result;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.weightVolumeValidate",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.weightVolumeValidate(waybillCode, packageCode));
        } catch (Exception e) {
            logger.error("weightVolumeValidate执行异常.参数waybillCode:" + waybillCode + ",packageCode:" + packageCode + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 获取分拣规则配置
     *
     * @param ruleType
     * @param createSiteCode
     * @return
     */
    public InvokeResult<Rule> getSortingRule(Integer ruleType, Integer createSiteCode) {
        InvokeResult<Rule> result = new InvokeResult<Rule>();
        result.success();

        if (ruleType == null) {
            result.parameterError("参数错误--ruleType为空");
            return result;
        }
        if (createSiteCode == null) {
            result.parameterError("参数错误--createSiteCode为空");
            return result;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.getSortingRule",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.getSortingRule(ruleType, createSiteCode));
        } catch (Exception e) {
            logger.error("getSortingRule执行异常.参数ruleType:" + ruleType + ",createSiteCode:" + createSiteCode + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 查询某个分拣中心的分拣规则
     *
     * @param createSiteCode
     * @return
     */
    public InvokeResult<Map<String, Rule>> getSiteSortingRule(Integer createSiteCode) {
        InvokeResult<Map<String, Rule>> result = new InvokeResult<Map<String, Rule>>();
        result.success();

        if (createSiteCode == null) {
            result.parameterError("参数错误--createSiteCode为空");
            return result;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.getSiteSortingRule",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.getSiteSortingRule(createSiteCode));
        } catch (Exception e) {
            logger.error("getSiteSortingRule执行异常.参数createSiteCode:" + createSiteCode + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 根据key获取UCC配置结果
     *
     * @param configureKey
     * @return
     */
    public InvokeResult<Boolean> getUccConfigurationByKey(String configureKey) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();

        if (StringUtils.isBlank(configureKey)) {
            result.parameterError("参数错误--configureKey为空");
            return result;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.getUccConfigurationByKey",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.getUccConfigurationByKey(configureKey));
        } catch (Exception e) {
            logger.error("getUccConfigurationByKey执行异常.参数configureKey:" + configureKey + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 根据key获取配置文件配置
     *
     * @param configureKey
     * @return
     */

    public InvokeResult<Object> getFileConfigurationByKey(String configureKey) {
        InvokeResult<Object> result = new InvokeResult<Object>();
        result.success();

        if (StringUtils.isBlank(configureKey)) {
            result.parameterError("参数错误--configureKey为空");
            return result;
        }

        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.getFileConfigurationByKey",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.getFileConfigurationByKey(configureKey));
        } catch (Exception e) {
            logger.error("getFileConfigurationByKey执行异常.参数configureKey:" + configureKey + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 检查混装箱是否可以通过校验
     *
     * @param createSiteCode  建包分拣中心编码
     * @param receiveSiteCode 目的分拣中心编码
     * @param mixedSiteCode   可混装地区编码
     * @param transportType   运输类型
     * @return 通过true ，不通过false
     */
    public InvokeResult<Boolean> checkMixedPackageConfig(Integer createSiteCode, Integer receiveSiteCode, Integer mixedSiteCode, Integer transportType, Integer ruleType) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();

        if (createSiteCode == null) {
            result.parameterError("参数错误--createSiteCode为空");
            return result;
        }
        if (receiveSiteCode == null) {
            result.parameterError("参数错误--receiveSiteCode为空");
            return result;
        }
        if (mixedSiteCode == null) {
            result.parameterError("参数错误--mixedSiteCode为空");
            return result;
        }
        if (transportType == null) {
            result.parameterError("参数错误--transportType为空");
            return result;
        }
        if (ruleType == null) {
            result.parameterError("参数错误--ruleType为空");
            return result;
        }

        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkMixedPackageConfig",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.checkMixedPackageConfig(createSiteCode, receiveSiteCode, mixedSiteCode, transportType, ruleType));
        } catch (Exception e) {
            logger.error("checkMixedPackageConfig执行异常.参数createSiteCode:" + createSiteCode + ",receiveSiteCode:" + receiveSiteCode +
                    ",mixedSiteCode:" + mixedSiteCode + ",transportType:" + transportType + ",ruleType:" + ruleType + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 查询运单是否拦截完成
     *
     * @param waybillCode
     * @param featureType
     * @return
     */
    public BlockResponse checkWaybillBlock(String waybillCode, Integer featureType) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkWaybillBlock",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return cancelWaybillJsfService.checkWaybillBlock(waybillCode, featureType);
        } catch (Exception e) {
            logger.error("checkWaybillBlock执行异常.参数waybillCode:" + waybillCode + ",featureType:" + featureType + ".", e);
            BlockResponse response = new BlockResponse();
            response.setCode(InvokeResult.SERVER_ERROR_CODE);
            response.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
            return response;
        } finally {
            Profiler.functionError(info);
        }
    }

    /**
     * 查询包裹是否拦截完成
     *
     * @param packageCode
     * @param featureType
     * @return
     */
    public BlockResponse checkPackageBlock(String packageCode, Integer featureType) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.checkPackageBlock",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return cancelWaybillJsfService.checkPackageBlock(packageCode, featureType);
        } catch (Exception e) {
            logger.error("checkPackageBlock执行异常.参数packageCode:" + packageCode + ",featureType:" + featureType + ".", e);
            BlockResponse response = new BlockResponse();
            response.setCode(InvokeResult.SERVER_ERROR_CODE);
            response.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
            return response;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 获取运单拦截信息
     *
     * @param waybillCode
     * @return
     */
    public JdResponse dealCancelWaybill(String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.dealCancelWaybill",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return jsfSortingResourceService.dealCancelWaybill(waybillCode);
        } catch (Exception e) {
            logger.error("checkPackageBlock执行异常.参数waybillCode:" + waybillCode + ".", e);
            JdResponse response = new BlockResponse();
            response.setCode(InvokeResult.SERVER_ERROR_CODE);
            response.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
            return response;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 获取运单拦截并推送拦截信息
     *
     * @param pdaOperateRequest
     * @return
     */
    public JdResponse dealCancelWaybillByRequest(PdaOperateRequest pdaOperateRequest) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.dealCancelWaybillByRequest",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return jsfSortingResourceService.dealCancelWaybillByRequest(pdaOperateRequest);
        }catch (Exception e){
            logger.error("dealCancelWaybillByRequest执行异常.参数pdaOperateRequest:" + JSON.toJSONString(pdaOperateRequest) + ".", e);
            JdResponse response = new BlockResponse();
            response.setCode(InvokeResult.SERVER_ERROR_CODE);
            response.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);

            Profiler.functionError(info);
            return response;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 根据运单号获取路由
     *
     * @param waybillCode
     * @return
     */
    public InvokeResult<String> getRouterByWaybillCode(String waybillCode) {
        InvokeResult<String> result = new InvokeResult<String>();
        result.success();

        if (StringUtils.isBlank(waybillCode)) {
            result.parameterError("参数错误--waybillCode为空");
            return result;
        }

        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsForMiddleEndServiceImpl.getRouterByWaybillCode",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(jsfSortingResourceService.getRouterByWaybillCode(waybillCode));
        } catch (Exception e) {
            logger.error("getRouterByWaybillCode执行异常.参数waybillCode:" + waybillCode + ".", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
