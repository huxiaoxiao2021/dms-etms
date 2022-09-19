package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageCodeRequest;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.coldChain.service.IColdChainService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.Constants.KY_DELIVERY;
import static com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/2
 * @Description:
 */
@Service("coldChainExternalService")
public class ColdChainExternalServiceImpl implements IColdChainService {

    private static final Logger log = LoggerFactory.getLogger(ColdChainExternalServiceImpl.class);

    private static final String OPE_SITE_ERROR_MSG = "当前操作场地不属于冷链分拣中心，请检查！";

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;

    @Autowired
    @Qualifier("storagePackageMService")
    private StoragePackageMService storagePackageMService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private WaybillPackageBarcodeService waybillPackageBarcodeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private BaseMajorManager baseMajorManager;
    /**
     * 冷链验货校验
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.inspectionCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<InspectionCheckResult> inspectionCheck(InspectionCheckVO vo) {
        InvokeResult<InspectionCheckResult> result = new InvokeResult<>();
        InspectionCheckResult inspectionCheckResult = new InspectionCheckResult();
        result.success();
        result.setData(inspectionCheckResult);

        //检查barCode规则
        boolean isBox = BusinessUtil.isBoxcode(vo.getBarCode());
        boolean isWaybill = !BusinessUtil.isBoxcode(vo.getBarCode()) && WaybillUtil.isWaybillCode(vo.getBarCode());
        boolean isPack = WaybillUtil.isPackageCode(vo.getBarCode());
        //本次仅支持运单、包裹号
        if( !(isWaybill || isPack) ){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确的运单号|包裹号");
            result.getData().setForced(true);
            return result;
        }
        //冷链场地操作
        if(!checkOpeSite(vo.getOperateSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.getData().setForced(true);
            return result;
        }

        String waybillCode = WaybillUtil.getWaybillCode(vo.getBarCode());

        //扫描的是包裹或运单校验逻辑
        if(isWaybill || isPack){
            //加盟商余额校验
            if(!allianceBusiDeliveryDetailService.checkExist(waybillCode)) {
                if(!allianceBusiDeliveryDetailService.checkMoney(waybillCode)){
                    //校验失败
                    result.customMessage(JdResponse.CODE_FAIL,InspectionCheckResult.ALLIANCE_INTERCEPT_MESSAGE);
                    result.getData().setForced(true);
                    return result;
                }
            }
            //库位号等校验
            JdResponse storageResp = inspectionService.getStorageCode(vo.getBarCode(),vo.getOperateSiteCode());
            if(storageResp.isSucceed()){
                if(storageResp.getData()!= null){
                    BeanUtils.copyProperties(storageResp.getData(),inspectionCheckResult);
                }
            }else{
                result.customMessage(storageResp.getCode(),storageResp.getMessage());
                result.getData().setForced(true);
                return result;
            }

            //暂存校验
            com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> storagePResp =  storagePackageMService.checkIsNeedStorage(vo.getBarCode(), vo.getOperateSiteCode());
            if(!storagePResp.codeSuccess()){
                result.customMessage(storagePResp.getCode(),storagePResp.getMessage());
                if(storagePResp.getData()){
                    result.getData().setWeak(true);
                }else{
                    result.getData().setForced(true);
                    return result;
                }
            }

            //包装耗材
            com.jd.ql.dms.common.domain.JdResponse<Boolean> packingConsumableResp = dmsPackingConsumableService.getConfirmStatusByWaybillCode(waybillCode);
            //超级恶心的返回值 如果是400 和 500 就不需要处理了，200和 201提示
            if(!JdResponse.CODE_FAIL.equals(packingConsumableResp.getCode()) && !JdResponse.CODE_ERROR.equals(packingConsumableResp.getCode())){
                result.customMessage(JdResponse.CODE_FAIL,packingConsumableResp.getMessage());
                result.getData().setWeak(true);
                return result;
            }
        }


        return result;
    }

    /**
     * 冷链验货
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.inspection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Boolean> inspection(InspectionVO vo) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();

        if(CollectionUtils.isEmpty(vo.getBarCodes())){
            return result;
        }
        if(!checkOpeSite(vo.getSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.setData(Boolean.FALSE);
            return result;
        }


        return inspectionService.addInspection(vo, null);
    }



    /**
     * 冷链发货校验
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.sendCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<ColdCheckCommonResult> sendCheck(SendCheckVO vo) {
        InvokeResult<ColdCheckCommonResult> result = initCommonResult();

        //检查barCode规则
        boolean isBox = BusinessUtil.isBoxcode(vo.getBoxCode());
        boolean isWaybill = !BusinessUtil.isBoxcode(vo.getBoxCode()) && WaybillUtil.isWaybillCode(vo.getBoxCode());
        boolean isPack = WaybillUtil.isPackageCode(vo.getBoxCode());
        //本次仅支持运单
        if(!(isWaybill)){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确的运单号");
            result.getData().setForced(true);
            return result;
        }
        //冷链场地操作
        if(!checkOpeSite(vo.getSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.getData().setForced(true);
            return result;
        }
        //冷链发货校验
        SortingJsfResponse sortingJsfResponse = sortingCheckService.coldChainSendCheckAndReportIntercept(changeTo2(vo));
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(sortingJsfResponse.getCode())){
            result.customMessage(sortingJsfResponse.getCode(),sortingJsfResponse.getMessage());
            if(sortingJsfResponse.getCode()>=30000 && sortingJsfResponse.getCode()<=40000){
                //弱拦截
                result.getData().setWeak(true);
            }else{
                //强拦截
                result.getData().setForced(true);
                return result;
            }
        }
        //第二步 金鹏校验
        if(isWaybill || isPack ){
            String waybillCode = WaybillUtil.getWaybillCode(vo.getBoxCode());
            DeliveryResponse secResult =  deliveryService.dealJpWaybill(vo.getSiteCode(),waybillCode);
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(secResult.getCode())){
                result.customMessage(secResult.getCode(),secResult.getMessage());
                //强拦截
                result.getData().setForced(true);
                return result;
            }
        }

        //第三步路由校验
        DeliveryResponse thrResult =  deliveryService.checkThreeDelivery(changeTo(vo), Constants.DELIVERY_ROUTER_VERIFICATION_NEW);
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(thrResult.getCode())){
            result.customMessage(thrResult.getCode(),thrResult.getMessage());
            if(thrResult.getCode()>=30000 && thrResult.getCode()<=40000){
                //弱拦截
                result.getData().setWeak(true);
            }else{
                //强拦截
                result.getData().setForced(true);
                return result;
            }
        }else{
            if(!CollectionUtils.isEmpty(thrResult.getTipMessages())){
                //存在提示语
                result.getData().setWeak(true);
                StringBuilder msg = new StringBuilder();
                for(String tipMsg : thrResult.getTipMessages()){
                    msg.append(tipMsg);
                    msg.append(Constants.SEPARATOR_SEMICOLON);
                }
                result.customMessage(thrResult.getCode(),msg.toString());
            }
        }

        return result;
    }

    /**
     * 冷链发货
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.send", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Boolean> send(SendVO vo) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();

        if(!checkOpeSite(vo.getSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.setData(Boolean.FALSE);
            return result;
        }

        List<ColdChainDeliveryRequest> request = changeTo(vo);


        if(vo.getNeedCheck()){
            List<DeliveryRequest> request2 = changeTo2(vo);
            //不齐校验
            ThreeDeliveryResponse threeDeliveryResponse = deliveryService.checkThreePackageForKY(toSendDetailListInFirstIndex(request2));
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(threeDeliveryResponse.getCode())){
                result.setMessage(threeDeliveryResponse.getMessage());
                result.setData(Boolean.FALSE);
                return result;
            }

        }

        //发货
        DeliveryResponse response = deliveryService.coldChainSendDelivery(request,SendBizSourceEnum.COLD_LOAD_CAR_SEND,Boolean.FALSE);
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(response.getCode())){
            if(DeliveryResponse.CODE_DELIVERY_ALL_PROCESSING.equals(response.getCode())
                    ||DeliveryResponse.CODE_DELIVERY_EXIST_PROCESSING.equals(response.getCode())){
                //正在执行时返回成功
                log.warn("ColdChainExternalService.send warn! req:{}",JsonHelper.toJson(request));
            }else{
                result.setData(Boolean.FALSE);
            }
            result.setMessage(response.getMessage());
            return result;
        }

        return result;
    }

    /**
     * 冷链-快运发货校验
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.sendOfKYCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<ColdCheckCommonResult> sendOfKYCheck(SendOfKYCheckVO vo) {

        InvokeResult<ColdCheckCommonResult> result = initCommonResult();

        //检查barCode规则
        boolean isBox = BusinessUtil.isBoxcode(vo.getBoxCode());
        boolean isWaybill = !BusinessUtil.isBoxcode(vo.getBoxCode()) && WaybillUtil.isWaybillCode(vo.getBoxCode());
        boolean isPack = WaybillUtil.isPackageCode(vo.getBoxCode());
        //本次仅支持运单、包裹号
        if( !(isWaybill || isPack) ){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确的运单号|包裹号");
            result.getData().setForced(true);
            return result;
        }
        //冷链场地操作
        if(!checkOpeSite(vo.getSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.getData().setForced(true);
            return result;
        }
        //第一步校验
        JdResult<CheckBeforeSendResponse> firstResult =  deliveryService.checkBeforeSend(changeTo(vo));
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(firstResult.getCode())){
            result.customMessage(firstResult.getCode(),firstResult.getMessage());
            if(firstResult.getCode()>=30000 && firstResult.getCode()<=40000){
                //弱拦截
                result.getData().setWeak(true);
            }else{
                //强拦截
                result.getData().setForced(true);
                return result;
            }
        }


        //第二步 金鹏校验
        if(isWaybill || isPack ){
            String waybillCode = WaybillUtil.getWaybillCode(vo.getBoxCode());
            DeliveryResponse secResult =  deliveryService.dealJpWaybill(vo.getSiteCode(),waybillCode);
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(secResult.getCode())){
                result.customMessage(secResult.getCode(),secResult.getMessage());
                result.getData().setForced(true);
                return result;
            }
        }

        //第三步路由校验
        DeliveryResponse thrResult =  deliveryService.checkThreeDelivery(changeTo(vo), Constants.DELIVERY_ROUTER_VERIFICATION_NEW);
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(thrResult.getCode())){
            result.customMessage(thrResult.getCode(),thrResult.getMessage());
            if(thrResult.getCode()>=30000 && thrResult.getCode()<=40000){
                //弱拦截
                result.getData().setWeak(true);
            }else{
                //强拦截
                result.getData().setForced(true);
                return result;
            }
        }else{
            if(!CollectionUtils.isEmpty(thrResult.getTipMessages())){
                //存在提示语
                result.getData().setWeak(true);
                StringBuilder msg = new StringBuilder();
                for(String tipMsg : thrResult.getTipMessages()){
                    msg.append(tipMsg);
                    msg.append(Constants.SEPARATOR_SEMICOLON);
                }
                result.customMessage(thrResult.getCode(),msg.toString());
                return result;
            }
        }
        return result;
    }

    /**
     * 冷链-快运发货
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.sendOfKY", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Boolean> sendOfKY(SendOfKYVO vo) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();

        if(!checkOpeSite(vo.getSiteCode())){
            result.customMessage(JdResponse.CODE_FAIL,OPE_SITE_ERROR_MSG);
            result.setData(Boolean.FALSE);
            return result;
        }
        List<DeliveryRequest> request = changeTo(vo);

        if(vo.getNeedCheck()){

            //不齐校验
            ThreeDeliveryResponse threeDeliveryResponse = deliveryService.checkThreePackageForKY(toSendDetailListInFirstIndex(request));
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(threeDeliveryResponse.getCode())){
                result.setMessage(threeDeliveryResponse.getMessage());
                result.setData(Boolean.FALSE);
                return result;
            }

        }

        //发货
        DeliveryResponse response = deliveryService.sendDeliveryInfoForKY(request,SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND);
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(response.getCode())){
            if(DeliveryResponse.CODE_DELIVERY_ALL_PROCESSING.equals(response.getCode())
                    ||DeliveryResponse.CODE_DELIVERY_EXIST_PROCESSING.equals(response.getCode())){
                //正在执行时返回成功
                log.warn("ColdChainExternalService.sendOfKY warn! req:{}",JsonHelper.toJson(request));
            }else{
                result.setData(Boolean.FALSE);
            }
            result.setMessage(response.getMessage());
            return result;
        }

        return result;

    }

    /**
     * 检查批次号是否已封车
     *
     * @param vo
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkSendCodeOfSeal(SendVO vo) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();

        String sendCode = vo.getSendCode();
        String transCode = vo.getTransPlanCode();
        // 运输计划号存在时优先获取 对应的批次号
        if(StringUtils.isNotBlank(transCode)){
            ColdChainSend coldChainSend = coldChainSendService.getByTransCode(transCode);
            if (coldChainSend != null && org.apache.commons.lang.StringUtils.isNotEmpty(coldChainSend.getSendCode())) {
                sendCode = coldChainSend.getSendCode();
            }
        }

        if(StringUtils.isBlank(sendCode)){
            //此时仍未获取到批次号直接返回
            return result;
        }

        StringBuffer customMsg = new StringBuffer().append(DeliveryResponse.MESSAGE_SEND_CODE_ERROR);
        if (newSealVehicleService.newCheckSendCodeSealed(sendCode, customMsg)) {
            result.setData(Boolean.FALSE);
            result.setMessage(customMsg.toString());
        }

        return result;
    }

    /**
     * 发货并验货接口
     *
     * @param vo
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ColdChainExternalService.sendAndInspectionOfPack", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Boolean> sendAndInspectionOfPack(SendInspectionVO vo) {
        if (log.isInfoEnabled()) {
            log.info("自动发货接口入参，{}", JsonHelper.toJson(vo));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();
        if (!checkParam(vo,result)) {
            result.setData(Boolean.FALSE);
            return result;
        }
        try {
            // 验货
            InspectionVO inspectionVO = new InspectionVO();
            List<String> barCodes = new ArrayList<>();
            barCodes.add(vo.getBoxCode());
            inspectionVO.setBarCodes(barCodes);
            inspectionVO.setSiteCode(vo.getCreateSiteCode());
            inspectionVO.setUserCode(vo.getCreateUserCode());
            inspectionVO.setUserName(vo.getCreateUser());
            inspectionVO.setOperateTime(DateHelper.formatDateTime(vo.getOperateTime()));
            InvokeResult<Boolean> inspectionResult = inspectionService.addInspection(inspectionVO, InspectionBizSourceEnum.COLD_CHAIN_SEND_INSPECTION);
            if (!inspectionResult.codeSuccess()) {
                log.info("自动发货时验货任务失败，{}，{}", JsonHelper.toJson(inspectionVO), JsonHelper.toJson(inspectionResult));
                return inspectionResult;
            }
            // 发货且分拣
            SendM sendM = new SendM();
            BeanUtils.copyProperties(vo,sendM);
            Date time = new Date(vo.getOperateTime().getTime() + 2 * Constants.DELIVERY_DELAY_TIME);
            sendM.setCreateTime(time);
            sendM.setOperateTime(time);
            sendM.setUpdateTime(time);
            sendM.setYn(1);
            sendM.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
            deliveryService.packageSend(SendBizSourceEnum.COLD_CHAIN_AUTO_SEND, sendM);
        } catch (Exception e) {
            result.setData(Boolean.FALSE);
            result.error(e);
        }
        return result;
    }

    /**
     * 自动发货参数校验
     * @param vo
     * @param result
     * @return
     */
    private boolean checkParam(SendInspectionVO vo, InvokeResult result){
        if (vo.getCreateUserCode() == null) {
            result.parameterError("操作人不能为空！");
            return false;
        }
        if (StringUtils.isBlank(vo.getSendCode())) {
            result.parameterError("批次号不能为空！");
            return false;
        }
        if (vo.getReceiveSiteCode() == null) {
            result.parameterError("发货目的站点不能为空！");
            return false;
        }
        if (vo.getCreateSiteCode() == null) {
            result.parameterError("起始站点不能为空！");
            return false;
        }
        if (StringUtils.isBlank(vo.getBoxCode())) {
            result.parameterError("包裹号不能为空！");
            return false;
        }
        if (vo.getOperateTime() == null) {
            result.parameterError("操作时间不能为空！");
            return false;
        }
        // 验证发货起始和目的站点是否存在
        try {
            BaseStaffSiteOrgDto cbDto = baseMajorManager.getBaseSiteBySiteId(vo.getCreateSiteCode());
            BaseStaffSiteOrgDto rbDto = baseMajorManager.getBaseSiteBySiteId(vo.getReceiveSiteCode());
            if (cbDto == null) {
                result.parameterError(MessageFormat.format("起始站点编号[{0}]不合法，在基础资料未查到！", vo.getCreateSiteCode()));
                return false;
            }
            if (rbDto == null) {
                result.parameterError(MessageFormat.format("发货目的站点编号[{0}]不合法，在基础资料未查到！", vo.getReceiveSiteCode()));
                return false;
            }
        } catch (Exception e) {
            log.error("调分拣自动发货接口校验参数，检查站点信息，调用站点信息异常:{}", JsonHelper.toJson(vo), e);
        }
        // 校验包裹号是否符合规则
        if (!WaybillUtil.isPackageCode(vo.getBoxCode())) {
            result.parameterError(MessageFormat.format("包裹号[{0}]不合法,正则校验未通过！",vo.getBoxCode()));
            return false;
        }
        return true;
    }

    private InvokeResult<ColdCheckCommonResult> initCommonResult(){
        InvokeResult<ColdCheckCommonResult> result = new InvokeResult<>();
        ColdCheckCommonResult coldCheckCommonResult = new ColdCheckCommonResult();
        result.setData(coldCheckCommonResult);
        result.success();
        return result;
    }


    private List<DeliveryRequest> changeTo(SendOfKYVO vo){
        List<DeliveryRequest> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vo.getBarCodes())){
            for(String barCode : vo.getBarCodes()){
                DeliveryRequest target = new DeliveryRequest();
                //暂时用copy
                BeanUtils.copyProperties(vo,target);
                target.setOpType(Constants.KY_DELIVERY);
                target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
                target.setBoxCode(barCode);
                list.add(target);
            }
        }

        return list;
    }

    private List<ColdChainDeliveryRequest> changeTo(SendVO vo){
        List<ColdChainDeliveryRequest> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vo.getBarCodes())){
            for(String barCode : vo.getBarCodes()) {
                ColdChainDeliveryRequest target = new ColdChainDeliveryRequest();
                //暂时用copy
                BeanUtils.copyProperties(vo, target);
                target.setOpType(Constants.KY_DELIVERY);
                target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
                target.setBoxCode(barCode);
                list.add(target);
            }
        }

        return list;
    }

    private List<DeliveryRequest> changeTo2(SendVO vo){
        List<DeliveryRequest> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vo.getBarCodes())){
            for(String barCode : vo.getBarCodes()) {
                DeliveryRequest target = new DeliveryRequest();
                //暂时用copy
                BeanUtils.copyProperties(vo, target);
                target.setOpType(Constants.KY_DELIVERY);
                target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
                target.setBoxCode(barCode);
                list.add(target);
            }
        }

        return list;
    }


    private DeliveryRequest changeTo(SendOfKYCheckVO vo){
        DeliveryRequest target = new DeliveryRequest();

        //暂时用copy
        BeanUtils.copyProperties(vo,target);
        target.setOpType(Constants.KY_DELIVERY);
        target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        return target;
    }

    private DeliveryRequest changeTo(SendCheckVO vo){
        DeliveryRequest target = new DeliveryRequest();

        //暂时用copy
        BeanUtils.copyProperties(vo,target);
        target.setOpType(Constants.KY_DELIVERY);
        target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        return target;
    }

    private com.jd.bluedragon.common.dto.send.request.DeliveryRequest changeTo2(SendCheckVO vo){
        com.jd.bluedragon.common.dto.send.request.DeliveryRequest target = new com.jd.bluedragon.common.dto.send.request.DeliveryRequest();

        //暂时用copy
        BeanUtils.copyProperties(vo,target);
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(vo.getSiteCode());
        target.setCurrentOperate(currentOperate);
        User user = new User();
        user.setUserCode(vo.getUserCode());
        user.setUserName(vo.getUserName());
        target.setUser(user);
        target.setOpType(Constants.KY_DELIVERY);
        target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        return target;
    }

    /**
     * toSendDetailList(java.util.List) 的优化方法，主要优化isValidWaybillCode()的循环调用问题
     * @param request 发货列表
     * @return
     */
    private List<SendM> toSendDetailListInFirstIndex(List<DeliveryRequest> request) {
        List<SendM> sendMList = new ArrayList<SendM>();
        boolean ifBColdChain = CollectionUtils.isNotEmpty(request) && request.size() > 0
                && com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(isValidWaybillCode(request.get(0)).getCode());//是否B冷链快运发货
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || BusinessHelper.isBoxcode(deliveryRequest.getBoxCode())) {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                } else if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    //B冷链快运发货支持扫运单号发货
                    if (!ifBColdChain) {
                        log.warn("DeliveryResource--toSendDatailList出现运单号，但非冷链快运发货,siteCode:{},单号:{}",
                                deliveryRequest.getSiteCode() , deliveryRequest.getBoxCode());
                    } else {
                        sendMList.addAll(deliveryRequest2SendMList(deliveryRequest));
                    }
                } else {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    /**
     * B冷链转运中心--快运发货支持扫描运单号发货
     * 如果扫描的是运单号，判断是否符是B冷链转运中心 && 入口是快运发货
     * @param request
     * @return
     */
    private DeliveryResponse isValidWaybillCode(DeliveryRequest request){
        Integer opType = request.getOpType();
        DeliveryResponse response = new DeliveryResponse(com.jd.bluedragon.distribution.api.JdResponse.CODE_OK, com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_OK);

        //判断是否是正确的箱号/包裹号--仅B冷链转运中心6460快运发货支持扫运单
        //登录人机构不是冷链分拣中心
        BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(request.getSiteCode());
        if (siteOrgDto == null) {
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_NO_SITE);
            response.setMessage(MessageFormat.format(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_NO_SITE, request.getSiteCode()));
            return response;
        }
        if (!(Constants.B2B_CODE_SITE_TYPE.equals(siteOrgDto.getSubType())&& KY_DELIVERY.equals(opType)) ) {
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_INVALID_PACKAGECODE_BOXCODE);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_INVALID_PACKAGECODE_BOXCODE);
            return response;
        }
        return response;
    }
    /**
     * 根据DeliveryRequest对象转成SendM列表
     * 注意：DeliveryRequest中的boxCode对应运单号
     * @param deliveryRequest
     * @return
     */
    private List<SendM> deliveryRequest2SendMList(DeliveryRequest deliveryRequest){
        List<SendM> sendMList = new ArrayList<SendM>();
        if(WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || BusinessHelper.isBoxcode(deliveryRequest.getBoxCode())){
            sendMList.add(deliveryRequest2SendM(deliveryRequest));
        }else if(WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())){
            //生成包裹号
            List<String> packageCodes = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(deliveryRequest.getBoxCode());
            for(String packageCode: packageCodes){
                SendM sendM = new SendM();
                sendM.setBoxCode(packageCode);
                sendM.setCreateSiteCode(deliveryRequest.getSiteCode());
                sendM.setReceiveSiteCode(deliveryRequest.getReceiveSiteCode());
                sendM.setCreateUserCode(deliveryRequest.getUserCode());
                sendM.setSendType(deliveryRequest.getBusinessType());
                sendM.setCreateUser(deliveryRequest.getUserName());
                sendM.setSendCode(deliveryRequest.getSendCode());
                sendM.setCreateTime(new Date());
                sendM.setOperateTime(new Date());
                sendM.setYn(1);
                sendM.setTurnoverBoxCode(deliveryRequest.getTurnoverBoxCode());
                sendM.setTransporttype(deliveryRequest.getTransporttype());

                sendMList.add(sendM);
            }
        }
        return sendMList;
    }

    /**
     * DeliveryRequest对象转sendM
     * @param deliveryRequest
     * @return
     */
    protected SendM deliveryRequest2SendM(DeliveryRequest deliveryRequest){
        SendM sendM = new SendM();
        sendM.setBoxCode(deliveryRequest.getBoxCode());
        sendM.setCreateSiteCode(deliveryRequest.getSiteCode());
        sendM.setReceiveSiteCode(deliveryRequest.getReceiveSiteCode());
        sendM.setCreateUserCode(deliveryRequest.getUserCode());
        sendM.setSendType(deliveryRequest.getBusinessType());
        sendM.setCreateUser(deliveryRequest.getUserName());
        sendM.setSendCode(deliveryRequest.getSendCode());
        sendM.setCreateTime(new Date());
        sendM.setOperateTime(new Date());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(deliveryRequest.getTurnoverBoxCode());
        sendM.setTransporttype(deliveryRequest.getTransporttype());
        return sendM;
    }





    private boolean checkOpeSite(Integer opeSiteCode){
        //登录人机构不是冷链分拣中心
        BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(opeSiteCode);
        if (siteOrgDto == null) {
            return false;
        }
        if (!Constants.B2B_CODE_SITE_TYPE.equals(siteOrgDto.getSubType()) ) {
            return false;
        }
        return true;
    }

}
