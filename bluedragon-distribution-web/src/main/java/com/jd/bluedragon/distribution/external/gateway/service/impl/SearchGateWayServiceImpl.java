package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.search.request.PackBoxRequest;
import com.jd.bluedragon.common.dto.search.request.PackWeightVORequest;
import com.jd.bluedragon.common.dto.search.response.BoxInfoResponse;
import com.jd.bluedragon.common.dto.search.response.ExpressReceivePackDiffResponse;
import com.jd.bluedragon.common.dto.search.response.PackBoxResponse;
import com.jd.bluedragon.common.dto.search.response.PackWeightVOResponse;
import com.jd.bluedragon.common.dto.search.response.PackageDifferentialResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxPackResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetail;
import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetailsResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressPackageDetailsResponse;
import com.jd.bluedragon.distribution.rest.box.BoxPackResource;
import com.jd.bluedragon.distribution.rest.express.ExpressCollectionResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillInterceptTipsResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.SearchGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓查询接口服务实现
 */
public class SearchGateWayServiceImpl implements SearchGateWayService {

    private final Logger logger = LoggerFactory.getLogger(SearchGateWayServiceImpl.class);

    @Autowired
    private BoxPackResource boxPackResource;

    @Autowired
    private DeliveryResource deliveryResource;

    @Autowired
    private ExpressCollectionResource expressCollectionResource;

    @Autowired
    private WaybillResource waybillResource;

    @Autowired
    private WaybillInterceptTipsResource waybillInterceptTipsResource;

    /**
     * 根据运单号或包裹号查询箱号包裹信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getBoxPackList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PackBoxResponse>> getBoxPackList(PackBoxRequest request){
        JdCResponse<List<PackBoxResponse>> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(null==request.getCreateSiteCode()){
            res.toFail("操作站点ID为空");
            return res;
        }

        if(StringUtils.isBlank(request.getWaybillNoOrPackNo())){
            res.toFail("包裹或者运单为空");
            return res;
        }

        if(null==request.getType()){
            res.toFail("分拣类型为空");
            return res;
        }

        BoxPackResponse response=boxPackResource.getBoxPackList(request.getCreateSiteCode(),request.getWaybillNoOrPackNo(),request.getType());
        if(JdResponse.CODE_OK.equals(response.getCode())){
            res.toSucceed(response.getMessage());
            String datastr= JsonHelper.toJson(response.getBoxPackList());
            res.setData(JsonHelper.jsonToList(datastr,PackBoxResponse.class));
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 根据箱号，获取箱内包裹数
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getPackSortByBoxCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoxInfoResponse> getPackSortByBoxCode(PackBoxRequest request){
        JdCResponse<BoxInfoResponse> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(null==request.getCreateSiteCode()){
            res.toFail("操作站点ID为空");
            return res;
        }

        if(StringUtils.isBlank(request.getWaybillNoOrPackNo())){
            res.toFail("箱号为空");
            return res;
        }

        BoxPackResponse response=boxPackResource.getBoxPack(request.getCreateSiteCode(),request.getWaybillNoOrPackNo());
        if(JdResponse.CODE_OK.equals(response.getCode())){
            res.toSucceed(response.getMessage());
            BoxInfoResponse data=new BoxInfoResponse();
            data.setTotalPack(response.getTotalPack());
            data.setReceiveSiteCode(response.getReceiveSiteCode());
            data.setReceiveSiteName(response.getReceiveSiteName());
            res.setData(data);
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getPackageDifferential",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PackageDifferentialResponse>> getPackageDifferential(DeliveryRequest request){
        JdCResponse<List<PackageDifferentialResponse>> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(StringUtils.isBlank(request.getBoxCode())){
            res.toFail("箱号为空");
            return res;
        }

        if(null==request.getCurrentOperate() || request.getCurrentOperate().getSiteCode()<=0){
            res.toFail("操作站点ID为空");
            return res;
        }

        if(null==request.getReceiveSiteCode()){
            res.toFail("目的地站点ID为空");
            return res;
        }

        com.jd.bluedragon.distribution.api.request.DeliveryRequest sourceRequest=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
        sourceRequest.setBoxCode(request.getBoxCode());
        sourceRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        sourceRequest.setReceiveSiteCode(request.getReceiveSiteCode());

        ThreeDeliveryResponse response=deliveryResource.checkSortingDiff(sourceRequest);
        if(JdResponse.CODE_OK.equals(response.getCode()) || DeliveryResponse.CODE_Delivery_SORTING_DIFF.equals(response.getCode())         ){
            res.setData(dataSourceToGateWay(response));
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 数据转换
     * @param data
     * @return
     */
    private List<PackageDifferentialResponse> dataSourceToGateWay(ThreeDeliveryResponse data){
        List<PackageDifferentialResponse> res=new ArrayList<>();
        if(null==data || null==data.getData() || data.getData().size()<=0){
            return res;
        }
        for (SendThreeDetail itme : data.getData()) {
            PackageDifferentialResponse info=new PackageDifferentialResponse();
            info.setBoxCode(itme.getBoxCode());
            info.setPackageBarcode(itme.getPackageBarcode());
            info.setScanStatusStr(itme.getMark());
            info.setIsWaybillFull(itme.getIsWaybillFull());

            res.add(info);
        }

        return res;
    }

    /**
     * 获取快运包裹明细信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.queryPackageDetails",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<ExpressReceivePackDiffResponse> queryPackageDetails(PackBoxRequest request){
        JdCResponse<ExpressReceivePackDiffResponse> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkPackBoxRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        ExpressPackageDetailsResponse response=expressCollectionResource.queryPackageDetails(request.getCreateSiteCode(),request.getWaybillNoOrPackNo(),request.getStatusQueryCode());
        if(JdResponse.CODE_OK.equals(response.getCode())){
            ExpressReceivePackDiffResponse data=new ExpressReceivePackDiffResponse();
            BeanUtils.copyProperties(response, data);
            res.setData(data);
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 获取快运箱明细信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.queryBoxDetails",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PackBoxResponse>> queryBoxDetails(PackBoxRequest request){
        JdCResponse<List<PackBoxResponse>> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkPackBoxRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        ExpressBoxDetailsResponse response=expressCollectionResource.queryBoxDetails(request.getCreateSiteCode(),request.getWaybillNoOrPackNo(),request.getStatusQueryCode());
        if(JdResponse.CODE_OK.equals(response.getCode())){
            List<PackBoxResponse> data=new ArrayList<>();
            if(null!=response.getBoxDetails() && response.getBoxDetails().size()>0){
                for (ExpressBoxDetail itme : response.getBoxDetails()) {
                    PackBoxResponse packBox=new PackBoxResponse();
                    packBox.setBoxCode(itme.getBoxCode());
                    packBox.setPackageTotal(itme.getPackageSize());
                    data.add(packBox);
                }
            }

            res.setData(data);
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 根据包裹号获取重量信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getPackageWeight",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<PackWeightVOResponse> getPackageWeight(String request){
        JdCResponse<PackWeightVOResponse> res =new JdCResponse<>();
        res.toSucceed();

        InvokeResult<PackWeightVO> result =waybillResource.findPackageWeight("1",request);
        if(InvokeResult.RESULT_SUCCESS_CODE==result.getCode()){
            PackWeightVOResponse data=new PackWeightVOResponse();
            BeanUtils.copyProperties(result.getData(), data);
            res.setData(data);
        }else {
            res.toFail(result.getMessage());
        }

        return res;
    }

    /**
     * 上传包裹称重信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.savePackageWeight",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> savePackageWeight(PackWeightVORequest request){
        JdCResponse<Boolean> res =new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkPackageWeightRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        PackWeightVO resourceRequest=new PackWeightVO();
        resourceRequest.setCodeStr(request.getCodeStr());
        resourceRequest.setLength(request.getLength());
        resourceRequest.setWidth(request.getWidth());
        resourceRequest.setHigh(request.getHigh());
        resourceRequest.setWeight(request.getWeight());
        resourceRequest.setOperatorId(request.getUser().getUserCode());
        resourceRequest.setOperatorName(request.getUser().getUserName());
        resourceRequest.setOperatorSiteCode(request.getCurrentOperate().getSiteCode());
        resourceRequest.setOperatorSiteName(request.getCurrentOperate().getSiteName());
        resourceRequest.setOperateTimeMillis(request.getCurrentOperate().getOperateTime().getTime());

        InvokeResult<Boolean> result =waybillResource.savePackageWeight(resourceRequest);
        if(InvokeResult.RESULT_SUCCESS_CODE==result.getCode()){
            res.setData(true);
        }else {
            res.toFail(result.getMessage());
            res.setData(false);
        }

        return res;
    }

    /**
     * 分拣机一键排障获取拦截信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getSortMachineInterceptTips",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> getSortMachineInterceptTips(PackBoxRequest request){
        JdCResponse<List<String>> res =new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkPackBoxRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        PdaOperateRequest resourceRequest=new PdaOperateRequest();
        resourceRequest.setPackageCode(request.getWaybillNoOrPackNo());
        resourceRequest.setCreateSiteCode(request.getCreateSiteCode());

        JdResult<List<String>> result=waybillInterceptTipsResource.getWaybillAndPack(resourceRequest);
        if(JdResult.CODE_SUC.equals(result.getCode())){
            res.setData(result.getData());
        }else {
            res.toFail(result.getMessage());
        }

        return res;
    }

    private JdCResponse<Void> checkPackageWeightRequest(PackWeightVORequest request){
        JdCResponse<Void> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }
        if(!WaybillUtil.isPackageCode(request.getCodeStr())){
            res.toFail("包裹号格式不正确");
            return res;
        }

        if(!(((request.getLength()==null || request.getLength().equals(new Double(0)))
                && (request.getWidth() == null || request.getWidth().equals(new Double(0)))
                && (request.getHigh()==null || request.getHigh().equals(new Double(0))))
                || (((request.getLength()!=null && !request.getLength().equals(new Double(0)))
                && (request.getWidth() != null && !request.getWidth().equals(new Double(0)))
                && (request.getHigh()!=null && !request.getHigh().equals(new Double(0)))))
        )){
            res.toFail("长宽高必须同时录入");
            return res;
        }

        if(request.getWeight() == null){
            res.toFail("重量必须录入");
            return res;
        }

        return res;
    }

    private JdCResponse<Void> checkPackBoxRequest(PackBoxRequest request){
        JdCResponse<Void> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(null==request.getCreateSiteCode()){
            res.toFail("操作站点ID为空");
            return res;
        }

        if(StringUtils.isBlank(request.getWaybillNoOrPackNo())){
            res.toFail("包裹/运单号为空");
            return res;
        }

        if(StringUtils.isBlank(request.getStatusQueryCode())){
            res.toFail("查询状态码为空");
            return res;
        }

        return res;
    }

}
