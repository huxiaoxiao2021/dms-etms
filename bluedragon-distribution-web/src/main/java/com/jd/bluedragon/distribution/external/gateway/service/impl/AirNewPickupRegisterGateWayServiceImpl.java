package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.air.request.AirDepartRequest;
import com.jd.bluedragon.common.dto.air.request.AirPortRequest;
import com.jd.bluedragon.common.dto.air.request.AirTplBillRequest;
import com.jd.bluedragon.common.dto.air.request.TmsDictRequest;
import com.jd.bluedragon.common.dto.air.response.AirContrabandReason;
import com.jd.bluedragon.common.dto.air.response.AirPortResponseDto;
import com.jd.bluedragon.common.dto.air.response.AirTplBillRespDto;
import com.jd.bluedragon.common.dto.air.response.TmsDictDto;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.EcpAirWSManager;
import com.jd.bluedragon.core.base.EcpQueryWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.arAbnormal.ArAbnormalResource;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReason;
import com.jd.bluedragon.external.gateway.service.AirNewPickupRegisterGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.ecp.dto.AirDepartInfoDto;
import com.jd.tms.ecp.dto.AirPortDto;
import com.jd.tms.ecp.dto.AirTplBillDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓-空铁-新发货登记 相关接口
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirNewPickupRegisterGateWayServiceImpl implements AirNewPickupRegisterGateWayService {

    @Autowired
    private EcpQueryWSManager ecpQueryWSManager;

    @Autowired
    private EcpAirWSManager ecpAirWSManager;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    @Autowired
    private ArAbnormalResource arAbnormalResource;

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.getGoodsTypeFromTms", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<TmsDictDto>> getGoodsTypeFromTms(TmsDictRequest tmsDictRequest) {
        JdCResponse<List<TmsDictDto>> jdCResponse = new JdCResponse<>();
        List<BasicDictDto> list = basicQueryWSManager.getDictList(Constants.PARENTCODE,Constants.DICTLEVEL,Constants.DICTGROUP);
        if(CollectionUtils.isEmpty(list)){
            jdCResponse.toError("查询数据为空！");
            return jdCResponse;
        }
        List<TmsDictDto> tmsDictDtoList = Lists.newArrayList();
        for(BasicDictDto item:list){
            TmsDictDto tmsDictDto = new TmsDictDto();
            tmsDictDto.setDictId(item.getDictId());
            tmsDictDto.setYn(item.getYn());
            tmsDictDto.setCreateTime(item.getCreateTime() != null ?item.getCreateTime().getTime():null);
            tmsDictDto.setSysTime(item.getSysTime() != null ?item.getSysTime().getTime():null);
            tmsDictDto.setUpdateTime(item.getUpdateTime() != null ?item.getUpdateTime().getTime():null);
            tmsDictDto.setOwner(item.getOwner());
            tmsDictDto.setRemark(item.getRemark());
            tmsDictDto.setDictSequence(item.getDictSequence());
            tmsDictDto.setDictGroup(item.getDictGroup());
            tmsDictDto.setDictLevel(item.getDictLevel());
            tmsDictDto.setParentCode(item.getParentCode());
            tmsDictDto.setDictCode(item.getDictCode());
            tmsDictDto.setDictName(item.getDictName());
            tmsDictDtoList.add(tmsDictDto);
        }
        jdCResponse.setData(tmsDictDtoList);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.getAirPortListByFlightNumber", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<AirPortResponseDto>> getAirPortListByFlightNumber(AirPortRequest airPortRequest) {
        JdCResponse<List<AirPortResponseDto>> jdCResponse = new JdCResponse<>();
        List<AirPortDto> list = ecpQueryWSManager.getAirPortListByFlightNumber(airPortRequest.getFlightNumber());
        if(CollectionUtils.isEmpty(list)){
            jdCResponse.toError("查询数据为空！");
            return jdCResponse;
        }
        List<AirPortResponseDto> dtoArrayList = Lists.newArrayList();
        for(AirPortDto item:list){
            AirPortResponseDto dto = new AirPortResponseDto();
            dto.setBeginNodeCode(item.getBeginNodeCode());
            dto.setBeginNodeName(item.getBeginNodeName());
            dto.setEndNodeCode(item.getEndNodeCode());
            dto.setEndNodeName(item.getEndNodeName());
            dtoArrayList.add(dto);
        }
        jdCResponse.setData(dtoArrayList);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.submitSortAirDepartInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<String> submitSortAirDepartInfo(AirDepartRequest airDepartRequest) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        AirDepartInfoDto param = new AirDepartInfoDto();
        param.setTplBillCode(airDepartRequest.getTplBillCode());
        param.setFlightNumber(airDepartRequest.getFlightNumber());
        param.setBeginNodeCode(airDepartRequest.getBeginNodeCode());
        param.setEndNodeCode(airDepartRequest.getEndNodeCode());
        param.setDepartCargoType(airDepartRequest.getDepartCargoType());
        param.setDepartCargoAmount(airDepartRequest.getDepartCargoAmount());
        param.setDepartCargoRealWeight(airDepartRequest.getDepartCargoRealWeight());
        param.setDepartCargoChargedWeight(airDepartRequest.getDepartCargoChargedWeight());
        param.setDepartRemark(airDepartRequest.getDepartRemark());
        param.setBatchCodes(airDepartRequest.getBatchCodes());
        param.setOperateNodeCode(airDepartRequest.getOperateNodeCode());
        param.setOperateNodeName(airDepartRequest.getOperateNodeName());
        param.setOperateUserCode(airDepartRequest.getOperateUserCode());
        param.setOperateUserName(airDepartRequest.getOperateUserName());
        param.setFlightDate(airDepartRequest.getFlightDate());
        param.setOperateTime(airDepartRequest.getOperateTime());
        InvokeResult<String> invokeResult = ecpAirWSManager.submitSortAirDepartInfo(param);
        if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed("操作成功！");
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.supplementSortAirDepartInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<String> supplementSortAirDepartInfo(AirDepartRequest airDepartRequest) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        AirDepartInfoDto param = new AirDepartInfoDto();
        param.setTplBillCode(airDepartRequest.getTplBillCode());
        param.setBatchCodes(airDepartRequest.getBatchCodes());
        param.setOperateNodeCode(airDepartRequest.getOperateNodeCode());
        param.setOperateNodeName(airDepartRequest.getOperateNodeName());
        param.setOperateUserCode(airDepartRequest.getOperateUserCode());
        param.setOperateUserName(airDepartRequest.getOperateUserName());
        param.setOperateTime(airDepartRequest.getOperateTime());
        InvokeResult<String> invokeResult = ecpAirWSManager.supplementSortAirDepartInfo(param);
        if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed("操作成功！");
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.getAirTplBillDetailInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<AirTplBillRespDto> getAirTplBillDetailInfo(AirTplBillRequest airTplBillRequest) {
        JdCResponse<AirTplBillRespDto> jdCResponse = new JdCResponse<>();
        AirTplBillDto airTplBillDto = ecpQueryWSManager.getAirTplBillDetailInfo(airTplBillRequest.getBillCode());
        if(airTplBillDto == null){
            jdCResponse.toError("查询数据为空！");
            return jdCResponse;
        }
        AirTplBillRespDto airTplBillRespDto = new AirTplBillRespDto();
        airTplBillRespDto.setTplBillCode(airTplBillDto.getTplBillCode());
        airTplBillRespDto.setFlightNumber(airTplBillDto.getFlightNumber());
        airTplBillRespDto.setBeginNodeName(airTplBillDto.getBeginNodeName());
        airTplBillRespDto.setEndNodeName(airTplBillDto.getEndNodeName());
        airTplBillRespDto.setPlanTakeOffTime(airTplBillDto.getPlanTakeOffTime());
        airTplBillRespDto.setDepartCargoTypeName(airTplBillDto.getDepartCargoTypeName());
        airTplBillRespDto.setDepartCargoAmount(airTplBillDto.getDepartCargoAmount());
        airTplBillRespDto.setDepartCargoRealWeight(airTplBillDto.getDepartCargoRealWeight());
        airTplBillRespDto.setDepartCargoChargedWeight(airTplBillDto.getDepartCargoChargedWeight());
        airTplBillRespDto.setDepartRemark(airTplBillDto.getDepartRemark());
        jdCResponse.setData(airTplBillRespDto);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.AirNewPickupRegisterGateWayServiceImpl.getArContrabandReasonList", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<AirContrabandReason>> getArContrabandReasonList(String request){
        JdCResponse<List<AirContrabandReason>> res = new JdCResponse<>();
        res.toSucceed();

        InvokeResult<List<ArContrabandReason>> resourceRES=arAbnormalResource.getArContrabandReasonList();
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            String datastr= JsonHelper.toJson(resourceRES.getData());
            res.setData(JsonHelper.fromJson(datastr,new ArrayList<AirContrabandReason>().getClass()));
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }
}
