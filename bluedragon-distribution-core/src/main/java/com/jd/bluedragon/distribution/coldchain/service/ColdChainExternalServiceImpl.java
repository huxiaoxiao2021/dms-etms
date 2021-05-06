package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.coldChain.service.IColdChainService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.Constants.KY_DELIVERY;

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
        if(!(isBox || isWaybill || isPack)){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确条码");
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
                    result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,InspectionCheckResult.ALLIANCE_INTERCEPT_MESSAGE);
                    result.getData().setForced(true);
                    return result;
                }
            }
            //库位号等校验
            JdResponse storageResp = inspectionService.getStorageCode(vo.getBarCode(),vo.getOperateSiteCode());
            if(storageResp.isSucceed()){
                if(storageResp.getData()!= null){
                    BeanUtils.copyProperties(storageResp,inspectionCheckResult);
                }
            }else{
                result.customMessage(storageResp.getCode(),storageResp.getMessage());
                result.getData().setForced(true);
                return result;
            }

            //包装耗材
            com.jd.ql.dms.common.domain.JdResponse<Boolean> packingConsumableResp = dmsPackingConsumableService.getConfirmStatusByWaybillCode(waybillCode);
            if(!(packingConsumableResp.isSucceed() && packingConsumableResp.getData())){
                result.customMessage(packingConsumableResp.getCode(),packingConsumableResp.getMessage());
                result.getData().setForced(true);
                return result;
            }

            //暂存校验
            com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> storagePResp =  storagePackageMService.checkIsNeedStorage(vo.getBarCode(), vo.getOperateSiteCode());
            if(!(storagePResp.codeSuccess() && packingConsumableResp.getData())){
                result.customMessage(packingConsumableResp.getCode(),storagePResp.getMessage());
                result.getData().setForced(true);
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
        result.success();

        if(CollectionUtils.isEmpty(vo.getBarCodes())){
            return result;
        }

        //拆分运单包裹列表  拆分箱号列表
        List<TaskRequest> reqs =  changeToInspectionReq(vo);

        for(TaskRequest req : reqs){
            TaskResponse taskResponse = taskService.add(req);
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(taskResponse.getCode())){
                //失败阻断 允许重试幂等即可
                result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,taskResponse.getMessage());
                return result;
            }
        }

        return result;
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
        if(!(isBox || isWaybill || isPack)){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确条码");
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
            }
            return result;
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
            }
            return result;
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
        result.success();

        List<DeliveryRequest> request2 = changeTo2(vo);
        List<ColdChainDeliveryRequest> request = changeTo(vo);

        if(vo.getNeedCheck()){

            //不齐校验
            ThreeDeliveryResponse threeDeliveryResponse = deliveryService.checkThreePackageForKY(toSendDetailListInFirstIndex(request2));
            if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(threeDeliveryResponse.getCode())){
                result.setMessage(threeDeliveryResponse.getMessage());
                result.setData(Boolean.FALSE);
                return result;
            }

        }

        //发货
        DeliveryResponse response = deliveryService.coldChainSendDelivery(request,SendBizSourceEnum.COLD_LOAD_CAR_SEND);
        if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(response.getCode())){
            result.setMessage(response.getMessage());
            result.setData(Boolean.FALSE);
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
        if(!(isBox || isWaybill || isPack)){
            result.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR,"请扫描正确条码");
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
            }
            return result;
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
            }
            return result;
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
        result.success();

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
            result.setMessage(response.getMessage());
            result.setData(Boolean.FALSE);
            return result;
        }
        result.setData(Boolean.TRUE);
        return result;

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
            DeliveryRequest target = new DeliveryRequest();
            //暂时用copy
            BeanUtils.copyProperties(vo,target);
            target.setOpType(Constants.KY_DELIVERY);
            target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            list.add(target);
        }

        return list;
    }

    private List<ColdChainDeliveryRequest> changeTo(SendVO vo){
        List<ColdChainDeliveryRequest> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vo.getBarCodes())){
            ColdChainDeliveryRequest target = new ColdChainDeliveryRequest();
            //暂时用copy
            BeanUtils.copyProperties(vo,target);
            target.setOpType(Constants.KY_DELIVERY);
            target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            list.add(target);
        }

        return list;
    }

    private List<DeliveryRequest> changeTo2(SendVO vo){
        List<DeliveryRequest> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vo.getBarCodes())){
            DeliveryRequest target = new DeliveryRequest();
            //暂时用copy
            BeanUtils.copyProperties(vo,target);
            target.setOpType(Constants.KY_DELIVERY);
            target.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            list.add(target);
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

    /**
     * {"sealBoxCode":null,"boxCode":null,"packageBarOrWaybillCode":"JDVA00182404142-1-1-","exceptionType":null,"operateType":0,"receiveSiteCode":0,"bizSource":31,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 20:57:34.000"}
     * {"shieldsCarCode":null,"carCode":null,"sealBoxCode":null,"packOrBox":"BC1001210427200014348517","turnoverBoxCode":null,"queueNo":null,"departureCarId":null,"shieldsCarTime":null,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 21:00:00.642"}
     * @param vo
     * @return
     */
    private void makeToInspectionBody(InspectionVO vo,List<Map<String,String>> boxes,List<Map<String,String>> others){

        for(String barCode : vo.getBarCodes()){
            Map<String,String> map = new HashMap<>();
            map.put("userCode",String.valueOf(vo.getUserCode()));
            map.put("userName",vo.getUserName());
            map.put("siteCode",String.valueOf(vo.getSiteCode()));
            map.put("siteName",vo.getSiteName());
            map.put("operateTime",vo.getOperateTime());
            map.put("businessType",String.valueOf(Constants.BUSSINESS_TYPE_POSITIVE));

            if(BusinessUtil.isBoxcode(barCode)){
                //箱号
                map.put("packOrBox",barCode);
                boxes.add(map);
            }else{
                //非箱号
                map.put("packageBarOrWaybillCode",barCode);
                //map.put("bizSource","31");
                others.add(map);
            }
        }
    }

    /**
     * {"sealBoxCode":null,"boxCode":null,"packageBarOrWaybillCode":"JDVA00182404142-1-1-","exceptionType":null,"operateType":0,"receiveSiteCode":0,"bizSource":31,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 20:57:34.000"}
     * {"shieldsCarCode":null,"carCode":null,"sealBoxCode":null,"packOrBox":"BC1001210427200014348517","turnoverBoxCode":null,"queueNo":null,"departureCarId":null,"shieldsCarTime":null,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 21:00:00.642"}
     * @param vo
     * @return
     */
    private List<TaskRequest> changeToInspectionReq(InspectionVO vo){

        List<Map<String,String>> boxes = new ArrayList<>();
        List<Map<String,String>> others = new ArrayList<>();
        makeToInspectionBody( vo,boxes,others);

        List<TaskRequest> requests = new ArrayList<>();
        for(Map<String,String> boxBody : boxes){
            TaskRequest request = new TaskRequest();
            request.setType(Task.TASK_TYPE_RECEIVE);
            request.setBody(Constants.PUNCTUATION_OPEN_BRACKET
                    + JsonHelper.toJson(boxBody)
                    + Constants.PUNCTUATION_CLOSE_BRACKET);
            request.setKeyword1(String.valueOf(vo.getSiteCode()));
            request.setReceiveSiteCode(vo.getSiteCode());
            request.setSiteCode(vo.getSiteCode());
            request.setBoxCode(boxBody.get("packOrBox"));
            requests.add(request);
        }

        for(Map<String,String> otherBody : others){
            TaskRequest request = new TaskRequest();
            request.setType(Task.TASK_TYPE_INSPECTION);
            request.setBody(Constants.PUNCTUATION_OPEN_BRACKET
                    + JsonHelper.toJson(otherBody)
                    + Constants.PUNCTUATION_CLOSE_BRACKET);
            request.setKeyword1(String.valueOf(vo.getSiteCode()));
            request.setReceiveSiteCode(vo.getSiteCode());
            request.setSiteCode(vo.getSiteCode());
            requests.add(request);
        }

        return requests;

    }

}
