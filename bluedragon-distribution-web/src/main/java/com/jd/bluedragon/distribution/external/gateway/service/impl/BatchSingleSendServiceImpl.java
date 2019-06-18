package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendCheckRequest;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendRequest;
import com.jd.bluedragon.common.dto.send.response.BatchSingleSendCheckVO;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.external.gateway.service.BatchSingleSendGatewayService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理批量一车一单发货校验
 *
 * @author jiaowenqiang
 * @date 2019/6/11
 */
@Service("batchSingleSendService")
public class BatchSingleSendServiceImpl implements BatchSingleSendGatewayService {


    @Autowired
    @Qualifier("boxResource")
    BoxResource boxResource;

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    @Qualifier("waybillResource")
    WaybillResource waybillResource;

    @Autowired
    @Qualifier("deliveryResource")
    DeliveryResource deliveryResource;

    /**
     * 处理批量一车一单发货参数校验
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BatchSingleSendServiceImpl.batchSingleSendCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<BatchSingleSendCheckVO> batchSingleSendCheck(BatchSingleSendCheckRequest request) {

        JdResponse<BatchSingleSendCheckVO> jdResponse = new JdResponse<>();
        jdResponse.toFail("操作失败请联系IT");

        BatchSingleSendCheckVO checkVO = new BatchSingleSendCheckVO();
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
    @JProfiler(jKey = "DMSWEB.BatchSingleSendServiceImpl.batchSingleSend", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<SendResult> batchSingleSend(BatchSingleSendRequest request) {

        PackageSendRequest param = convertToPackageSendRequest(request);
        param.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));

        JdResponse<SendResult> jdResponse = new JdResponse<>();
        InvokeResult<SendResult> result = deliveryResource.newPackageSend(param);

        if (result.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
            jdResponse.setCode(result.getCode());
            jdResponse.setData(result.getData());
            jdResponse.setMessage(result.getMessage());

            return jdResponse;
        }

        jdResponse.toFail(result.getData().getValue());
        return jdResponse;
    }

    private PackageSendRequest convertToPackageSendRequest(BatchSingleSendRequest request){
        PackageSendRequest packageSendRequest=new PackageSendRequest();

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
    private JdResponse<BatchSingleSendCheckVO> dealBox(JdResponse<BatchSingleSendCheckVO> jdResponse,
                                                       BatchSingleSendCheckRequest request,
                                                       BatchSingleSendCheckVO checkVO,
                                                       Map<Integer, String> batchCodeMap) {

        BoxResponse boxResponse = boxResource.get(request.getPackageOrBoxCode());
        //未成功查出箱号,直接返回
        if (boxResponse != null) {
            if (boxResponse.getCode() == 200) {
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
    private JdResponse<BatchSingleSendCheckVO> dealPackage(JdResponse<BatchSingleSendCheckVO> jdResponse,
                                                           BatchSingleSendCheckRequest request,
                                                           BatchSingleSendCheckVO checkVO,
                                                           Map<Integer, String> batchCodeMap) {

        List<Integer> siteCodes;
        InvokeResult<List<Integer>> routerResult = getPackageRouter(request);
        if (routerResult == null) {
            jdResponse.toFail("查询运单失败，没有该运单对应的站点");
            return jdResponse;
        } else if (routerResult.getCode() != 200) {
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
            checkVO.setReceiveSiteCode(Integer.parseInt(BusinessUtil.getBatchReceiveNO(sendCode)));
            checkVO.setSendCode(sendCode);
            jdResponse.setData(checkVO);
            jdResponse.toSucceed(JdResponse.MESSAGE_SUCCESS);
            return jdResponse;
        } else {
            jdResponse.toFail(MessageFormat.format("没有单据{0}对应的批次，请先扫描批次！", request.getPackageOrBoxCode()));
            return jdResponse;
        }
    }


    /**
     * 处理批次号
     */
    private Map<Integer, String> dealSendCodes(List<String> batchList) {
        //从批次号中获取目的站点,key为目的站点，value为批次号
        Map<Integer, String> receiveCodeMap = new HashMap<>();
        for (String s : batchList) {
            receiveCodeMap.put(Integer.parseInt(BusinessUtil.getBatchReceiveNO(s)), s);
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
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().siteCode);
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));

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
}
