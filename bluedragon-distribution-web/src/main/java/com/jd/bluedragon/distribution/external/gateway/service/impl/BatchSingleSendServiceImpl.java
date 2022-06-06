package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendCheckRequest;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendRequest;
import com.jd.bluedragon.common.dto.send.request.ThirdWaybillNoWyRequest;
import com.jd.bluedragon.common.dto.send.response.BatchSingleSendCheckDto;
import com.jd.bluedragon.common.dto.send.response.ThirdWaybillNoWyDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.request.ThirdWaybillNoRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.waybill.domain.ThirdWaybillNoResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.BatchSingleSendGatewayService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * BatchSingleSendServiceImpl
 * 处理批量一车一单发货校验
 * @author jiaowenqiang
 * @date 2019/6/11
 */
@Service("batchSingleSendService")
public class BatchSingleSendServiceImpl implements BatchSingleSendGatewayService {
    Logger logger = LoggerFactory.getLogger(BatchSingleSendServiceImpl.class);


    /**
     * 箱号resource
     */
    @Autowired
    @Qualifier("boxResource")
    private BoxResource boxResource;

    /**
     * 运单resource
     */
    @Autowired
    @Qualifier("waybillResource")
    private WaybillResource waybillResource;

    /**
     * 发货resource
     */
    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Autowired
    private UccPropertyConfiguration uccConfiguration;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 处理批量一车一单发货参数校验
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BatchSingleSendServiceImpl.batchSingleSendCheck", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<BatchSingleSendCheckDto> batchSingleSendCheck(BatchSingleSendCheckRequest request) {

        JdCResponse<BatchSingleSendCheckDto> jdResponse = new JdCResponse<>();
        jdResponse.toFail("非法的包裹号或箱号！");

        if(request == null){
            jdResponse.toFail("入参不能为空");
            return jdResponse;
        }

        if (request.getUser().getUserCode()<=0 || StringUtils.isBlank(request.getUser().getUserName()) || request.getCurrentOperate().getSiteCode()<=0 || StringUtils.isBlank(request.getCurrentOperate().getSiteName())){
            jdResponse.toFail("操作人信息和场地信息都不能为空");
            return jdResponse;
        }

        BatchSingleSendCheckDto checkVO = new BatchSingleSendCheckDto();
        //对批次号的处理,map的key为目的站点，值为批次号
        Map<Integer, String> batchCodeMap = dealSendCodes(request.getBatchCodeList());


        /* 1、如果参数中的boxCode分为箱号 */
        if (BusinessUtil.isBoxcode(request.getPackageOrBoxCode())) {

            return dealBox(jdResponse, request, checkVO, batchCodeMap);

        }

        /* 2、如果参数中的boxCode分为包裹号 */
        if (WaybillUtil.isPackageCode(request.getPackageOrBoxCode())) {

            return dealPackage(jdResponse, request, checkVO, batchCodeMap);
        }

        return jdResponse;
    }

    /**
     * 执行批量一车一单发货
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BatchSingleSendServiceImpl.batchSingleSend", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @BusinessLog(sourceSys = 1,bizType = 100,operateType = 1006)
    public JdCResponse batchSingleSend(BatchSingleSendRequest request) {
        JdCResponse jdResponse = new JdCResponse<>();
        if(request == null){
            jdResponse.toFail("入参不能为空");
            return jdResponse;
        }

        if (request.getUser().getUserCode()<=0 || StringUtils.isBlank(request.getUser().getUserName()) || request.getCurrentOperate().getSiteCode()<=0 || StringUtils.isBlank(request.getCurrentOperate().getSiteName())){
            jdResponse.toFail("操作人信息和场地信息都不能为空");
            return jdResponse;
        }

        PackageSendRequest param = convertToPackageSendRequest(request);
        param.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));

        InvokeResult<SendResult> result = deliveryResource.newPackageSend(param);

        if (result.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
            if (result.getData().getKey() == 1) {
                jdResponse.init(JdCResponse.CODE_SUCCESS, result.getData().getValue());
            }
            if (result.getData().getKey() == 2) {
                jdResponse.init(JdCResponse.CODE_FAIL, result.getData().getValue());
            }
            if (result.getData().getKey() == 4) {
                jdResponse.init(JdCResponse.CODE_CONFIRM, result.getData().getValue());
            }

            return jdResponse;
        }

        jdResponse.toFail(result.getData().getValue());
        return jdResponse;
    }

    /**
     * 发货参数转化
     */
    private PackageSendRequest convertToPackageSendRequest(BatchSingleSendRequest request) {
        PackageSendRequest packageSendRequest = new PackageSendRequest();

        packageSendRequest.setUserCode(request.getUser().getUserCode());
        packageSendRequest.setUserName(request.getUser().getUserName());
        packageSendRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        packageSendRequest.setSiteName(request.getCurrentOperate().getSiteName());

        packageSendRequest.setIsForceSend(request.isForceSend());
        packageSendRequest.setReceiveSiteCode(request.getReceiveSiteCode());
        packageSendRequest.setSendCode(request.getSendCode());
        packageSendRequest.setBoxCode(request.getBoxCode());
        packageSendRequest.setBusinessType(request.getBusinessType());

        return packageSendRequest;
    }


    /**
     * 如果是箱号
     */
    private JdCResponse<BatchSingleSendCheckDto> dealBox(JdCResponse<BatchSingleSendCheckDto> jdResponse,
                                                         BatchSingleSendCheckRequest request,
                                                         BatchSingleSendCheckDto checkVO,
                                                         Map<Integer, String> batchCodeMap) {

        BoxResponse boxResponse = boxResource.get(request.getPackageOrBoxCode());
        //未成功查出箱号,直接返回
        if (boxResponse != null) {
            if (Objects.equals(boxResponse.getCode(), JdCResponse.CODE_SUCCESS)) {
                checkVO.setReceiveSiteCode(boxResponse.getReceiveSiteCode());
                if (!batchCodeMap.containsKey(boxResponse.getReceiveSiteCode())) {
                    jdResponse.toFail(MessageFormat.format("没有单据{0}对应的批次，请先扫描批次！", boxResponse.getReceiveSiteCode()));
                    return jdResponse;
                } else {
                    checkVO.setSendCode(batchCodeMap.get(boxResponse.getReceiveSiteCode()));
                    jdResponse.setData(checkVO);
                    jdResponse.toSucceed(JdResponse.MESSAGE_SUCCESS);
                    return jdResponse;
                }

            } else {
                jdResponse.toFail(boxResponse.getMessage());
                return jdResponse;
            }
        } else {
            return jdResponse;
        }
    }

    /**
     * 如果是包裹号
     */
    private JdCResponse<BatchSingleSendCheckDto> dealPackage(JdCResponse<BatchSingleSendCheckDto> jdResponse,
                                                             BatchSingleSendCheckRequest request,
                                                             BatchSingleSendCheckDto checkVO,
                                                             Map<Integer, String> batchCodeMap) {
        //参数校验
        InvokeResult<List<Integer>> routerResult = null;
        if(!(routerResult =checkPackageCode(request)).codeSuccess()){
            jdResponse.toFail(routerResult.getMessage());
            return jdResponse;
        }

        //如果扫了目的地德邦分拣中心的批次号 而且改单子为一单一件德邦单，则该单匹配该批次号
        String dpSendCode = getSendCodeByDpWaybillCode(request.getPackageOrBoxCode(), batchCodeMap);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(dpSendCode)){
            checkVO.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(dpSendCode));
            checkVO.setSendCode(dpSendCode);
            jdResponse.setData(checkVO);
            jdResponse.toSucceed(JdResponse.MESSAGE_SUCCESS);
            return jdResponse;
        }

        List<Integer> siteCodes;
        routerResult = getPackageRouter(request);
        if (routerResult == null) {
            jdResponse.toFail("查询运单失败，没有该运单对应的站点");
            return jdResponse;
        } else if (routerResult.getCode() != JdCResponse.CODE_SUCCESS) {
            jdResponse.toFail(routerResult.getMessage());
            return jdResponse;
        } else {
            siteCodes = routerResult.getData();
        }

        // 2.2按路由发货未获取到路由信息时返回
        if (request.getOperateType() == 1 && siteCodes.size() == 1) {
            jdResponse.toFail("获取路由数据失败，请使用新发货功能操作发货");
            return jdResponse;
        }

        // 2.3校验包裹的预分拣站点是否在属于扫描的批次中的任何一个
        String sendCode = checkSiteCodeInBatch(siteCodes, batchCodeMap);
        if (sendCode != null) {
            checkVO.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
            checkVO.setSendCode(sendCode);
            jdResponse.setData(checkVO);
            jdResponse.toSucceed(JdResponse.MESSAGE_SUCCESS);
            return jdResponse;
        } else {
            jdResponse.toFail(MessageFormat.format("没有单据{0}对应的批次，请先扫描批次！", request.getPackageOrBoxCode()));
            return jdResponse;
        }
    }

    private String getSendCodeByDpWaybillCode(String packageCode, Map<Integer, String> batchCodeMap){
        String dpSendCode = null;
        try {
            if(!uccConfiguration.isDpWaybillMatchSendCodeSwitch()){
                logger.info("批量一单一件发货德邦运单匹配德邦批次号开关未开启，packageCode:{}", packageCode);
                return null;
            }
            List<Integer> dpSiteCodeList = uccConfiguration.getDpSiteCodeList();
            for(Integer siteCode : batchCodeMap.keySet()){
                if(BusinessHelper.isDPSiteCode(dpSiteCodeList, siteCode)){
                    dpSendCode = batchCodeMap.get(siteCode);
                    break;
                }
            }

            if(StringUtils.isBlank(dpSendCode)){
                return null;
            }

            String waybillCode = WaybillUtil.isWaybillCode(packageCode) ? packageCode : WaybillUtil.getWaybillCode(packageCode);
            //查询运单包裹数和 运单扩展属性
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillExtend(true);
            BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getDataByChoiceNoCache(waybillCode, wChoice);
            //为查到运单信息
            if(baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
                logger.warn("根据运单号获取BigWaybillDto返回data为null,waybillCode:{},baseEntity.code:{}", waybillCode, baseEntity.getResultCode());
                return null;
            }
            BigWaybillDto bigWaybillDto = baseEntity.getData();
            //运单包裹号为空，或非一单一件 ，或运单扩展对象为空
            if( bigWaybillDto.getWaybill().getGoodNumber() == null || bigWaybillDto.getWaybill().getGoodNumber() > 1 ||
            bigWaybillDto.getWaybill().getWaybillExt() == null){
                logger.info("运单包裹号为空，或非一单一件 ，或运单扩展对象为空,waybillCode:{},goodNumber:{}", waybillCode,
                        bigWaybillDto.getWaybill().getGoodNumber());
                return null;
            }
            //to
            //非德邦单
            if(!BusinessHelper.isDPWaybill(bigWaybillDto.getWaybill().getWaybillExt().getThirdCarrierFlag())){
                logger.info("批量发货该单非德邦单，不匹配德邦批次号,waybillCode:{},ThirdCarrierFlag:{}", waybillCode,
                        bigWaybillDto.getWaybill().getWaybillExt().getThirdCarrierFlag());
                return null;
            }
            logger.info("批量发货该单:{}为德邦单匹配德邦批次号:{}成功", waybillCode, dpSendCode);
        }catch (Exception e){
            logger.error("批量一单一件发货德邦运单匹配德邦批次号异常", e);
        }
        return dpSendCode;

    }

   private InvokeResult<List<Integer>> checkPackageCode(BatchSingleSendCheckRequest request){
       InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
       /* 检查参数的有效性 */
       String operateTime = DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME);
       if (StringHelper.isEmpty(request.getPackageOrBoxCode())
               || StringHelper.isEmpty(operateTime) || 0 == request.getCurrentOperate().getSiteCode()) {
           result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
           result.setMessage(InvokeResult.PARAM_ERROR);
           return result;
       }

       /* 校验单号是否是运单号或者是包裹号 */
       if (!WaybillUtil.isWaybillCode(request.getPackageOrBoxCode()) && !WaybillUtil.isPackageCode(request.getPackageOrBoxCode())) {
           logger.warn("WaybillResource.getBarCodeAllRouters-->参数错误：{}，单号不是京东单号", request.getPackageOrBoxCode());
           result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
           result.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_RULE_ILLEGAL));
           return result;
       }
       result.success();
       return result;
   }

    /**
     * 处理批次号
     */
    private Map<Integer, String> dealSendCodes(List<String> batchList) {
        //从批次号中获取目的站点,key为目的站点，value为批次号
        Map<Integer, String> receiveCodeMap = new HashMap<>(200);
        for (String s : batchList) {
            receiveCodeMap.put(BusinessUtil.getReceiveSiteCodeFromSendCode(s), s);
        }
        return receiveCodeMap;
    }


    /**
     * 查找包裹的路由信息
     */
    private InvokeResult<List<Integer>> getPackageRouter(BatchSingleSendCheckRequest request) {

        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setPackageCode(request.getPackageOrBoxCode());
        pdaOperateRequest.setOperateType(request.getOperateType());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setMachineCode(request.getMachineCode());

        return waybillResource.getBarCodeAllRouters(pdaOperateRequest);
    }

    /**
     * 校验包裹号的路由站点是否属于扫描的批次中,并返回批次号
     */
    private String checkSiteCodeInBatch(List<Integer> siteCodes, Map<Integer, String> batchCodeMap) {

        for (Integer siteCode : siteCodes) {
            if (batchCodeMap.get(siteCode) != null) {
                return batchCodeMap.get(siteCode);
            }
        }
        return null;
    }

    /**
     * 由运单号查包裹号,安卓入口
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BatchSingleSendServiceImpl.getPackageNoByThirdWaybillNo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<ThirdWaybillNoWyDto> getPackageNoByThirdWaybillNo(ThirdWaybillNoWyRequest request) {

        JdCResponse<ThirdWaybillNoWyDto> jdResponse = new JdCResponse<>();
        jdResponse.toFail("操作失败请联系IT");

        if (request == null) {
            jdResponse.toFail("入参不能为空");
            return jdResponse;
        }

        if ( StringUtils.isBlank(request.getThirdWaybill())) {
            jdResponse.toFail("运单号都不能为空");
            return jdResponse;
        }
        ThirdWaybillNoRequest param = new ThirdWaybillNoRequest();
        param.setThirdWaybill(request.getThirdWaybill());
        param.setUserErp(request.getUser().getUserErp());
        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setOperateTime(request.getCurrentOperate().getOperateTime()==null?DateHelper.formatDate(new Date()):DateHelper.formatDateTime(request.getCurrentOperate().getOperateTime()));
        param.setUserCode(request.getUser().getUserCode());
        param.setUserName(request.getUser().getUserName());
        InvokeResult<ThirdWaybillNoResult> response = waybillResource.getWyPackageNoByThirdWaybillNo(param);

        ThirdWaybillNoResult data;
        if (response == null) {
            jdResponse.toFail("由三方运单号查包裹号失败！");
            return jdResponse;
        } else if (!response.codeSuccess()) {
            jdResponse.toFail(response.getMessage());
            return jdResponse;
        } else {
            data = response.getData();
        }
        ThirdWaybillNoWyDto resultDto =new ThirdWaybillNoWyDto();
        resultDto.setWaybillCode(data.getWaybillCode());

        jdResponse.setData(resultDto);
        jdResponse.toSucceed(JdResponse.MESSAGE_SUCCESS);
        return jdResponse;
    }

}
