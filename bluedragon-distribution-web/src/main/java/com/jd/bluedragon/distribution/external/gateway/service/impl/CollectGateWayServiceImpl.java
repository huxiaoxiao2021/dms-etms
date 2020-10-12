package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.collect.request.CollectGoodsRequest;
import com.jd.bluedragon.common.dto.collect.response.CollectGoodsResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDTO;
import com.jd.bluedragon.distribution.collect.service.impl.CollectGoodsCommonServiceImpl;
import com.jd.bluedragon.distribution.rest.collect.CollectGoodsResource;
import com.jd.bluedragon.external.gateway.service.CollectGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 安卓查询接口服务实现
 */
public class CollectGateWayServiceImpl implements CollectGateWayService {

    private final Logger logger = LoggerFactory.getLogger(CollectGateWayServiceImpl.class);

    @Autowired
    private CollectGoodsResource collectGoodsResource;

    /**
     * 获取某个分拣中心下的所有集货区
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectGateWayServiceImpl.findAreas",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<CollectGoodsResponse>> findAreas(CollectGoodsRequest request){
        JdCResponse<List<CollectGoodsResponse>> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        InvokeResult<List<CollectGoodsDTO>> response=collectGoodsResource.findAreas(request.getCurrentOperate().getSiteCode());
        if(InvokeResult.RESULT_SUCCESS_CODE==response.getCode()){
            res.toSucceed(response.getMessage());
            String datastr= JsonHelper.toJson(response.getData());
            res.setData(JsonHelper.jsonToList(datastr,CollectGoodsResponse.class));
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 集货  上架存放接口
     * 自动计算暂存位 并存放记录
     * 未验货时自动触发验货
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectGateWayServiceImpl.collectPut",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<CollectGoodsResponse> collectPut(CollectGoodsRequest request){
        JdVerifyResponse<CollectGoodsResponse> res = new JdVerifyResponse<>();
        res.toSuccess();

        JdCResponse<Void> checkRes=checkRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        if(StringUtils.isBlank(request.getPackageCode())){
            res.toFail("包裹号为空");
            return res;
        }

        if(StringUtils.isBlank(request.getCollectGoodsAreaCode())){
            res.toFail("集货区编号为空");
            return res;
        }

        CollectGoodsDTO sourceRequest=getSourceRequest(request);
        InvokeResult<CollectGoodsDTO> response =collectGoodsResource.put(sourceRequest);
        if(InvokeResult.RESULT_SUCCESS_CODE==response.getCode()){
            res.toSuccess(response.getMessage());
            String datastr= JsonHelper.toJson(response.getData());
            res.setData(JsonHelper.jsonToObject(datastr,CollectGoodsResponse.class));
            res.addBox(MsgBoxTypeEnum.PROMPT,300,response.getMessage());
        }else if(CollectGoodsCommonServiceImpl.COLLECT_ALL_TIP_CODE==response.getCode()){
            res.toSuccess(response.getMessage());
            String datastr= JsonHelper.toJson(response.getData());
            res.setData(JsonHelper.jsonToObject(datastr,CollectGoodsResponse.class));
            res.addBox(MsgBoxTypeEnum.CONFIRM,300,response.getMessage());
        }else if(CollectGoodsCommonServiceImpl.COLLECT_NOT_TIP_CODE==response.getCode()){
            res.toSuccess(response.getMessage());
            String datastr= JsonHelper.toJson(response.getData());
            res.setData(JsonHelper.jsonToObject(datastr,CollectGoodsResponse.class));
            res.addBox(MsgBoxTypeEnum.WARNING,300,response.getMessage());
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.CollectGateWayServiceImpl.findScanInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<CollectGoodsResponse> findScanInfo(CollectGoodsRequest request){
        JdCResponse<CollectGoodsResponse> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        if(StringUtils.isBlank(request.getPackageCode()) && StringUtils.isBlank(request.getCollectGoodsPlaceCode())){
            res.toFail("货位/包裹号不能都为空");
            return res;
        }

        CollectGoodsDTO sourceRequest=getSourceRequest(request);
        InvokeResult<CollectGoodsDTO> response=collectGoodsResource.find(sourceRequest);
        if(InvokeResult.RESULT_SUCCESS_CODE==response.getCode()){
            res.toSucceed(response.getMessage());
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

    /**
     * 集货  异常转移接口
     * 转移集货位 或 包裹 至异常集货位
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectGateWayServiceImpl.transfer",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> transfer(CollectGoodsRequest request){
        JdCResponse<Boolean> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        if(StringUtils.isBlank(request.getPackageCode()) && StringUtils.isBlank(request.getCollectGoodsPlaceCode())){
            res.toFail("货位/包裹号不能都为空");
            return res;
        }

        CollectGoodsDTO sourceRequest=getSourceRequest(request);
        InvokeResult<Boolean> response=collectGoodsResource.transfer(sourceRequest);
        if(InvokeResult.RESULT_SUCCESS_CODE==response.getCode()){
            res.toSucceed(response.getMessage());
            res.setData(response.getData());
        }else {
            res.toFail(response.getMessage());
            res.setData(response.getData());
        }

        return res;
    }

    /**
     * 集货  释放集货位接口
     * 释放集货位 或 集货区所有数据
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectGateWayServiceImpl.clean",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> clean(CollectGoodsRequest request){
        JdCResponse<Boolean> res=new JdCResponse<>();
        res.toSucceed();

        JdCResponse<Void> checkRes=checkRequest(request);
        if(!JdCResponse.CODE_SUCCESS.equals(checkRes.getCode())){
            res.toFail(checkRes.getMessage());
            return res;
        }

        if(StringUtils.isBlank(request.getCollectGoodsAreaCode()) && StringUtils.isBlank(request.getCollectGoodsPlaceCode())){
            res.toFail("集货区编码/货位编码不能都为空");
            return res;
        }

        CollectGoodsDTO sourceRequest=getSourceRequest(request);
        InvokeResult<Boolean> response=collectGoodsResource.clean(sourceRequest);
        if(InvokeResult.RESULT_SUCCESS_CODE==response.getCode()){
            res.toSucceed("释放成功");
            res.setData(response.getData());
        }else {
            res.toFail(response.getMessage());
            res.setData(response.getData());
        }

        return res;
    }

    /**
     * 由安卓入参转换为source入参
     * @param request
     * @return
     */
    private CollectGoodsDTO getSourceRequest(CollectGoodsRequest request){
        CollectGoodsDTO sourceRequest=new CollectGoodsDTO();
        sourceRequest.setPackageCode(request.getPackageCode());
        sourceRequest.setCollectGoodsAreaCode(request.getCollectGoodsAreaCode());
        sourceRequest.setCollectGoodsPlaceCode(request.getCollectGoodsPlaceCode());
        sourceRequest.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        sourceRequest.setOperateSiteName(request.getCurrentOperate().getSiteName());
        sourceRequest.setOperateTime(request.getCurrentOperate().getOperateTime().getTime());
        sourceRequest.setOperateUserId(request.getUser().getUserCode());
        sourceRequest.setOperateUserErp(request.getUser().getUserErp());
        sourceRequest.setOperateUserName(request.getUser().getUserName());
        return sourceRequest;
    }

    private JdCResponse<Void> checkRequest(CollectGoodsRequest request){
        JdCResponse<Void> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(null==request.getUser() || request.getUser().getUserCode()<=0){
            res.toFail("操作人信息为空");
            return res;
        }

        if(null==request.getCurrentOperate() || request.getCurrentOperate().getSiteCode()<=0){
            res.toFail("操作站点信息为空");
            return res;
        }

        return res;
    }

}
