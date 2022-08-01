package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.dock.convert.DockInfoConverter;
import com.jd.bluedragon.distribution.dock.dao.DockBaseInfoDao;
import com.jd.bluedragon.distribution.dock.domain.DockBaseInfoPo;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.external.constants.TransportServiceConstants;
import com.jd.bluedragon.distribution.external.enums.AppVersionEnums;
import com.jd.bluedragon.distribution.external.service.FuncSwitchConfigApiService;
import com.jd.bluedragon.distribution.external.service.TransportCommonService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarCommonService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.common.util.StringUtils;
import com.jd.jim.cli.Cluster;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 转运依赖分拣相关服务
 */
@Slf4j
@Service("transportCommonService")
public class TransportCommonServiceImpl implements TransportCommonService {

    @Resource
    private BoardCommonManager boardCommonManager;

    @Resource
    private FuncSwitchConfigApiService funcSwitchConfigApiService;

    @Autowired
    private WaybillService waybillService;

    @Resource
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Resource
    private SendDatailDao sendDatailDao;

    @Resource
    private DockBaseInfoDao dockBaseInfoDao;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Resource
    private UnloadCarCommonService unloadCarCommonService;

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.interceptValidateUnloadCar", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Boolean> interceptValidateUnloadCar(TransportServiceRequest transportServiceRequest) {
        final String methodDesc = "TransportCommonServiceImpl.interceptValidateUnloadCar--装卸车通用校验begin--";
        InvokeResult<Boolean> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        if(StringUtils.isBlank(transportServiceRequest.getWaybillCode())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单号不能为空");
            return result;
        }
        if(StringUtils.isBlank(transportServiceRequest.getWaybillSign())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单waybillSign不能为空");
            return result;
        }

        try {
            log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
            return boardCommonManager.loadUnloadInterceptValidate(transportServiceRequest.getWaybillCode(), transportServiceRequest.getWaybillSign());
        } catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.boardCombinationCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        // ver组板拦截
        try {
            InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(request);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
            }
            return result;
        } catch (Exception e) {
            log.error("ver组板拦截出现异常，请求参数BoardCommonRequest={},error=", JsonHelper.toJson(request), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.hasInspectOrSendFunction", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Boolean> hasInspectOrSendFunction(TransportServiceRequest transportServiceRequest) {
        final String methodDesc = "TransportCommonServiceImpl.hasInspectOrSendFunction--获取发货验货白名单配置begin--";
        InvokeResult<Boolean> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        try{
            log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
            if(transportServiceRequest.getBusinessType() == null || transportServiceRequest.getCreateSiteId() == null
                        || StringUtils.isBlank(transportServiceRequest.getUserErp())) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }
            Integer menuCode = null;
            if(transportServiceRequest.getBusinessType().equals(1)) {
                menuCode = FuncSwitchConfigEnum.FUNCTION_SEND.getCode();
            }else if(transportServiceRequest.getBusinessType().equals(2)) {
                menuCode = FuncSwitchConfigEnum.FUNCTION_INSPECTION.getCode();
            }else {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage("参数businessType只允许为1（装车）或2（卸车）");
                return result;
            }
            return funcSwitchConfigApiService.hasInspectOrSendFunction(transportServiceRequest.getCreateSiteId(), menuCode,
                    transportServiceRequest.getUserErp());

        }catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.getRouterNextSiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Integer> getRouterNextSiteId(TransportServiceRequest transportServiceRequest){
        final String methodDesc = "TransportCommonServiceImpl.hasInspectOrSendFunction--获取发货验货白名单配置begin--";
        InvokeResult<Integer> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
        if(StringUtils.isBlank(transportServiceRequest.getWaybillCode())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单号不能为空");
            return result;
        }
        if(transportServiceRequest.getCreateSiteId() == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("操作场地编码不能为空");
            return result;
        }
        try{
           Integer nextSiteId = waybillService.getRouterFromMasterDb(transportServiceRequest.getWaybillCode(), transportServiceRequest.getCreateSiteId());
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(nextSiteId);
            return result;
        }catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.getRouterByWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<String> getRouterByWaybillCode(TransportServiceRequest transportServiceRequest) {
        InvokeResult<String> result = new InvokeResult<>();
        try {
            String router = waybillService.getRouterByWaybillCode(transportServiceRequest.getWaybillCode());
            result.setCode(com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(router);
        } catch (Exception e) {
            log.error("getRouterByWaybillCode|根据运单号【{}】查询waybill表路由字段异常", transportServiceRequest.getWaybillCode(), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.checkAllianceMoney", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Boolean> checkAllianceMoney(String waybillCode) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        boolean flag = false;
        try {
            // 加盟商余额校验
            if (allianceBusiDeliveryDetailService.checkExist(waybillCode)
                    && !allianceBusiDeliveryDetailService.checkMoney(waybillCode)) {
                flag = true;
            }
            result.setData(flag);
            return result;
        } catch (Exception e) {
            log.error("盟商余额校验出现异常，请求参数waybillCode={},error=", waybillCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.queryPackageCodeBySendAndWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<List<String>> queryPackageCodeBySendAndWaybillCode(Integer createSiteCode, String batchCode, String waybillCode) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        List<String> packageCodeList;
        try {
            SendDetailDto params = new SendDetailDto();
            params.setCreateSiteCode(createSiteCode);
            params.setSendCode(batchCode);
            params.setWaybillCode(waybillCode);
            params.setIsCancel(0);
            packageCodeList = sendDatailDao.queryPackageCodeBySendAndWaybillCode(params);
            result.setCode(com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(packageCodeList);
        } catch (Exception e) {
            log.error("queryPackageCodeBySendAndWaybillCode|查询批次【{}】,操作站点【{}】,运单号【{}】的包裹数异常", batchCode, createSiteCode, waybillCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.queryPackageCodeBySendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<List<String>> queryPackageCodeBySendCode(Integer createSiteCode, String batchCode) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        //从分拣获取批次下的包裹数据。
        try {
            SendDetailDto params = new SendDetailDto();
            params.setCreateSiteCode(createSiteCode);
            params.setSendCode(batchCode);
            params.setIsCancel(0);
            List<String> packageCodeList = sendDatailDao.queryPackageCodeBySendCode(params);
            result.setCode(com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(packageCodeList);
        } catch (Exception e) {
            log.error("queryPackageCodeBySendCode|查询批次【{}】,操作站点【{}】的包裹数异常", batchCode, createSiteCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.findByWaybillCodeOrPackageCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<String> findByWaybillCodeOrPackageCode(Integer createSiteCode, Integer receiveSiteCode, String barCode) {
        InvokeResult<String> result = new InvokeResult<>();
        try {
            SendDetail sendDetail = new SendDetail();
            sendDetail.setPackageBarcode(barCode);
            sendDetail.setCreateSiteCode(createSiteCode);
            sendDetail.setReceiveSiteCode(receiveSiteCode);
            List<SendDetail> sendDetailList = sendDatailDao.findByWaybillCodeOrPackageCode(sendDetail);
            result.setCode(com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE);
            if (CollectionUtils.isNotEmpty(sendDetailList)) {
                result.setData(sendDetailList.get(0).getSendCode());
            }
        } catch (Exception e) {
            log.error("findByWaybillCodeOrPackageCode|查询包裹号【{}】,操作站点【{}】,下一站点【{}】的包裹明细异常", barCode, createSiteCode, receiveSiteCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.queryPackageAndWaybillNumByBatchCodes", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Map<String, Integer>> queryPackageAndWaybillNumByBatchCodes(Integer createSiteCode, List<String> batchCodes) {
        InvokeResult<Map<String, Integer>> result = new InvokeResult<>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("createSiteCode", createSiteCode);
        params.put("sendCodes", batchCodes);
        try {
            Integer waybillNum = sendDatailDao.queryWaybillNumBybatchCodes(params);
            Integer packageNum = sendDatailDao.queryPackageNumBybatchCodes(params);
            result.setCode(com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE);
            Map<String, Integer> map = new HashMap<>(2);
            map.put("waybill", waybillNum);
            map.put("package", packageNum);
            result.setData(map);
        } catch (Exception e) {
            log.error("queryPackageAndWaybillNumByBatchCodes|查询批次【{}】,操作站点【{}】的包裹数异常", batchCodes, createSiteCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.TransportCommonServiceImpl.listAllDockInfoBySiteCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<DockInfoEntity>> listAllDockInfoBySiteCode(Integer siteCode) {
        InvokeResult<List<DockInfoEntity>> dockListRes = new InvokeResult<>();
        dockListRes.success();
        dockListRes.setData(new ArrayList<DockInfoEntity>());
        if (Objects.isNull(siteCode) || !NumberHelper.gt0(siteCode)) {
            dockListRes.parameterError("站点无效，请检查");
            return dockListRes;
        }

        List<DockBaseInfoPo> dockBaseInfoPos = dockBaseInfoDao.listAllDockInfoBySiteCode(siteCode);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(dockBaseInfoPos)) {
            if (log.isInfoEnabled()) {
                log.info("根据站点【{}】未查询到有效的月台信息，返回为空", siteCode);
            }
            return dockListRes;
        }
        for (DockBaseInfoPo dockBaseInfoPo : dockBaseInfoPos) {
            dockListRes.getData().add(DockInfoConverter.convertToEntity(dockBaseInfoPo));
        }

        return dockListRes;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.TransportCommonServiceImpl.findDockInfoByDockCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<DockInfoEntity> findDockInfoByDockCode(Integer siteCode, String dockCode) {
        InvokeResult<DockInfoEntity> result = new InvokeResult<>();
        result.success();
        if (Objects.isNull(siteCode) || Objects.isNull(dockCode)) {
            result.parameterError("缺少必要查询参数");
            return result;
        }
        DockBaseInfoPo dockBaseInfoPo = new DockBaseInfoPo();
        dockBaseInfoPo.setDockCode(dockCode);
        dockBaseInfoPo.setSiteCode(siteCode);
        DockBaseInfoPo dockBaseInfoPoRes = dockBaseInfoDao.findByDockCode(dockBaseInfoPo);

        if(dockBaseInfoPoRes == null) {
            result.error("场地【" + siteCode + "】内未查到月台【" + dockCode + "】信息");
            return result;
        }
        result.setData(DockInfoConverter.convertToEntity(dockBaseInfoPoRes));

        return result;
    }

    @Override
    public InvokeResult<Boolean> saveOperatePdaVersion(String sealCarCode, String pdaVersion) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode) || StringUtils.isBlank(pdaVersion)) {
            res.error("参数不能为空");
            return res;
        }
        if(!AppVersionEnums.existValidation(pdaVersion)) {
            res.error("版本暂不支持");
            return res;
        }
        try{
            //todo zcf 考虑加锁
            boolean resData = true;
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            if (redisClientOfJy.exists(key)) {
                resData = redisClientOfJy.get(key).equals(pdaVersion);
            }else {
                //兼容历史数据：
                UnloadCar uc = unloadCarCommonService.selectBySealCarCodeWithStatus(sealCarCode);
                if(uc.getStatus() == 1 || uc.getStatus() == 2 || uc.getStatus() == 3) {
                    //老PDA已经操作领取status=1或者已经开始扫描status=2或任务完成status=3，但是无redis
                    redisClientOfJy.setEx(key, AppVersionEnums.PDA_OLD.getVersion(), TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION_EXPIRE, TimeUnit.DAYS);
                    if(AppVersionEnums.PDA_OLD.getVersion().equals(pdaVersion)) {
                        resData = true;
                    }else {
                        resData = false;
                    }
                }else {
                    redisClientOfJy.setEx(key, pdaVersion, TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION_EXPIRE, TimeUnit.DAYS);
                }
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("TransportCommonServiceImpl.saveOperatePdaVersion--服务异常--sealCarCode={}，pdaVersion={}, srrMsg={}", sealCarCode, pdaVersion, e.getMessage(), e);
            res.error("保存PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }

    @Override
    public InvokeResult<Boolean> delOperatePdaVersion(String sealCarCode, String pdaVersion) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode) || StringUtils.isBlank(pdaVersion)) {
            res.error("参数不能为空");
            return res;
        }
        if(!AppVersionEnums.existValidation(pdaVersion)) {
            res.error("版本暂不支持");
            return res;
        }
        try{
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            if (redisClientOfJy.exists(key)) {
                if(!redisClientOfJy.get(key).equals(pdaVersion)) {
                    String msg = StringUtils.isBlank(AppVersionEnums.getDescByCode(pdaVersion)) ? "其他版本" : AppVersionEnums.getDescByCode(pdaVersion);
                    res.error(msg + "正在操作中");
                    return res;
                }else {
                    redisClientOfJy.del(key);
                }
            }
            res.setData(true);
            return res;
        }catch (Exception e) {
            log.error("TransportCommonServiceImpl.delOperatePdaVersion--服务异常--sealCarCode={}，pdaVersion={}, srrMsg={}", sealCarCode, pdaVersion, e.getMessage(), e);
            res.error("删除PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }



    @Override
    public InvokeResult<String> getOperatePdaVersion(String sealCarCode) {
        InvokeResult<String> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode)) {
            res.error("参数不能为空");
            return res;
        }
        try{
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            res.setData(redisClientOfJy.get(key));
            return res;
        }catch (Exception e) {
            log.error("TransportCommonServiceImpl.getOperatePdaVersion--服务异常--sealCarCode={}， srrMsg={}", sealCarCode, e.getMessage(), e);
            res.error("查询PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }

}
