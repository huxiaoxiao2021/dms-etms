package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.search.request.PackBoxRequest;
import com.jd.bluedragon.common.dto.search.response.BoxInfoResponse;
import com.jd.bluedragon.common.dto.search.response.PackBoxResponse;
import com.jd.bluedragon.common.dto.search.response.PackageDifferentialResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxPackResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.rest.box.BoxPackResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.external.gateway.service.SearchGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓查询接口服务实现
 */
public class SearchGateWayServiceImpl implements SearchGateWayService {

    private final Logger logger = LoggerFactory.getLogger(SearchGateWayServiceImpl.class);

    @Autowired
    BoxPackResource boxPackResource;

    @Autowired
    DeliveryResource deliveryResource;

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

}
