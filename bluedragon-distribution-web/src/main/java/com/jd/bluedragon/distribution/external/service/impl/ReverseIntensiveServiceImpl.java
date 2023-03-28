package com.jd.bluedragon.distribution.external.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.warmbox.response.WarmBoxInOutDto;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.request.material.collectionbag.CollectionBagRequest;
import com.jd.bluedragon.distribution.api.request.material.recyclingbox.RecyclingBoxInOutboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxBoardRelationRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.StrandReportReasonsVO;
import com.jd.bluedragon.distribution.api.response.WarmBoxInOutVO;
import com.jd.bluedragon.distribution.api.response.WaybillTrackResVO;
import com.jd.bluedragon.distribution.api.response.material.recyclingbox.RecyclingBoxInOutResponse;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.base.domain.BaseDataDictVO;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.external.intensive.service.ReverseIntensiveService;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.rest.material.CollectionBagOperationResource;
import com.jd.bluedragon.distribution.rest.material.RecyclingBoxInOutResource;
import com.jd.bluedragon.distribution.rest.material.WarmBoxInOutResource;
import com.jd.bluedragon.distribution.track.WaybillTrackQueryService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供给逆向集约实现
 *
 * @author hujiping
 * @date 2023/3/2 6:18 PM
 */
@Service("reverseIntensiveService")
public class ReverseIntensiveServiceImpl implements ReverseIntensiveService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private StrandService strandService;
    
    @Autowired
    private WaybillTrackQueryService waybillTrackQueryService;

    @Autowired
    private QualityControlService qualityControlService;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("warmBoxInOutResource")
    private WarmBoxInOutResource warmBoxInOutResource;

    @Autowired
    @Qualifier("collectionBagOperationResource")
    private CollectionBagOperationResource operationResource;
    
    @Autowired
    private RecyclingBoxInOutResource recyclingBoxInOutResource;
    
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.queryPackageCodes",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<String>> queryPackageCodes(String waybillCode) {
        return waybillTrackQueryService.queryPackageCodes(waybillCode);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.queryWaybillTrackHistory",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<String>> queryWaybillTrackHistory(String erp) {
        return waybillTrackQueryService.queryWaybillTrackHistory(erp);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.queryWaybillTrack",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<WaybillTrackResVO>> queryWaybillTrack(WaybillTrackReqVO waybillTrackReqVO) {
        return waybillTrackQueryService.queryWaybillTrack(waybillTrackReqVO);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.queryStrandReportReasons",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<StrandReportReasonsVO>> queryStrandReportReasons() {
        InvokeResult<List<StrandReportReasonsVO>> result = new InvokeResult<>();
        InvokeResult<List<ConfigStrandReasonData>> listInvokeResult = strandService.queryReasonList();
        if(listInvokeResult == null || CollectionUtils.isEmpty(listInvokeResult.getData())){
            result.parameterError("未查询到滞留上报原因，请联系分拣小秘!");
            return result;
        }
        List<StrandReportReasonsVO> list = Lists.newArrayList();
        for (ConfigStrandReasonData data : listInvokeResult.getData()) {
            StrandReportReasonsVO strandReportReasonsVO = new StrandReportReasonsVO();
            BeanUtils.copyProperties(data, strandReportReasonsVO);
            list.add(strandReportReasonsVO);
        }
        result.setData(list);
        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.strandReport",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Boolean> strandReport(StrandReportRequest request) {
        request.setBusinessType(20);
        return strandService.report(request);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.getBaseDictByTypeGroups",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<BaseDataDictVO>> getBaseDictByTypeGroups(List<Integer> typeGroups) {
        InvokeResult<List<BaseDataDictVO>> result = new InvokeResult<>();
        if (CollectionUtils.isEmpty(typeGroups)) {
            result.parameterError();
            return result;
        }
        try {
            List<BaseDataDictVO> list = new ArrayList<>();
            for (Integer typeGroup : typeGroups) {
                List<BaseDataDictVO> dataDictList = convert2VO(baseService.getBaseDictionaryTree(typeGroup));
                if (dataDictList != null) {
                    list.addAll(dataDictList);
                }
            }
            result.setData(list);
        } catch (Exception e) {
            logger.error("查询基础字典信息时发生异常，request:{}", JsonHelper.toJson(typeGroups), e);
            result.error("未查询到基础字典信息,请联系分拣小秘!");
        }
        return result;
    }

    private List<BaseDataDictVO> convert2VO(List<BaseDataDict> tmpList) {
        if(CollectionUtils.isEmpty(tmpList)){
            return null;
        }
        List<BaseDataDictVO> list = Lists.newArrayList();
        for (BaseDataDict tmp : tmpList) {
            BaseDataDictVO baseDataDictVO = new BaseDataDictVO();
            BeanUtils.copyProperties(tmp, baseDataDictVO);
            list.add(baseDataDictVO);
        }
        return list;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.redeliveryCheck",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<RedeliveryMode> redeliveryCheck(RedeliveryCheckRequest request) {
        return qualityControlService.redeliveryCheck(request);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "ReverseIntensiveService.exceptionSubmit",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Boolean> exceptionSubmit(QualityControlRequest request) {
        return qualityControlService.exceptionSubmit(request);
    }

    @Override
    public InvokeResult<WarmBoxInOutVO> listBoxBoardRelations(WarmBoxBoardRelationReqVO request) {
        InvokeResult<WarmBoxInOutVO> response = new InvokeResult<>();
        response.success();
        if (null == request) {
            response.error("参数为空！");
            return response;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Gateway获取板和保温箱绑定关系. req:[{}]", JSON.toJSONString(request));
        }

        WarmBoxBoardRelationRequest queryParam = new WarmBoxBoardRelationRequest();
        queryParam.setBoardCode(request.getBoardCode() == null ? StringUtils.EMPTY : request.getBoardCode());
        queryParam.setUserCode(request.getUserCode());
        queryParam.setUserName(request.getUserName());
        queryParam.setSiteCode(request.getSiteCode());
        queryParam.setSiteName(request.getSiteName());
        JdResult<WarmBoxInOutResponse> result = warmBoxInOutResource.listBoxBoardRelation(queryParam);
        if (result.isFailed()) {
            response.error(result.getMessage());
            return response;
        }
        response.setData(JSON.parseObject(JSON.toJSONString(result.getData()), WarmBoxInOutVO.class));
        response.setMessage(result.getMessage());

        if (logger.isDebugEnabled()) {
            logger.debug("Gateway获取板和保温箱绑定关系. resp:[{}]", JSON.toJSONString(response));
        }

        return response;
    }

    @Override
    public InvokeResult<Void> warmBoxOutbound(WarmBoxOutboundReqVO request) {
        InvokeResult<Void> response = new InvokeResult<>();
        response.success();
        if (null == request) {
            response.error("参数为空！");
            return response;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Gateway保温箱出库. req:[{}]", JSON.toJSONString(request));
        }

        WarmBoxOutboundRequest body = new WarmBoxOutboundRequest();
        body.setBoardCode(request.getBoardCode() == null ? StringUtils.EMPTY : request.getBoardCode());
        body.setWarmBoxCodes(request.getWarmBoxCodes());
        body.setOutboundType(request.getOutboundType());
        body.setUserCode(request.getUserCode());
        body.setUserName(request.getUserName());
        body.setSiteCode(request.getSiteCode());
        body.setSiteName(request.getSiteName());

        JdResult<WarmBoxInOutResponse> result = warmBoxInOutResource.warmBoxOutbound(body);
        if (result.isFailed()) {
            response.error(result.getMessage());
            return response;
        }
        response.setMessage(result.getMessage());

        if (logger.isDebugEnabled()) {
            logger.debug("Gateway保温箱出库. resp:[{}]", JSON.toJSONString(response));
        }
        return response;
    }

    @Override
    public InvokeResult<Void> sendCollectionBag(CollectionBagOperationReqVO request) {
        InvokeResult<Void> response = new InvokeResult<>();
        response.success();
        if (null == request || null == request.getReceiveSiteCode() || org.apache.commons.collections.CollectionUtils.isEmpty(request.getBoxCodes())) {
            response.error("参数不全！");
            return response;
        }

        CollectionBagRequest body = new CollectionBagRequest();
        body.setCollectionBagCodes(request.getBoxCodes());
        body.setReceiveSiteCode(request.getReceiveSiteCode());
        body.setReceiveSiteName(request.getReceiveSiteName());
        body.setSiteCode(request.getSiteCode());
        body.setSiteName(request.getSiteName());
        body.setUserCode(request.getUserCode());
        body.setUserName(request.getUserName());

        JdResult<Boolean> result = operationResource.send(body);
        if (result.isFailed()) {
            response.error(result.getMessage());
            return response;
        }
        response.setMessage(result.getMessage());
        return response;
    }

    @Override
    public InvokeResult<RecyclingBoxInOutResponse> recyclingBoxOutbound(RecyclingBoxInOutboundRequest request) {
        InvokeResult<RecyclingBoxInOutResponse> response = new InvokeResult<>();
        response.success();

        JdResult<RecyclingBoxInOutResponse> jdResult = recyclingBoxInOutResource.recyclingBoxOutbound(request);

        response.setCode(jdResult.getCode());
        response.setMessage(jdResult.getMessage());
        response.setData(jdResult.getData());
        return response;
    }
}
