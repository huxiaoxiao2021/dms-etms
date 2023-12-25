package com.jd.bluedragon.distribution.external.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.external.intensive.service.ForeignUserSignService;
import com.jd.bluedragon.distribution.jsf.domain.PositionData;
import com.jd.bluedragon.distribution.jsf.domain.UserSignQueryRequest;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRecordData;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRequest;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/23 17:35
 * @Description:
 */
@Service("foreignUserSignService")
public class ForeignUserSignServiceImpl implements ForeignUserSignService {

    private static final Logger log = LoggerFactory.getLogger(ForeignUserSignServiceImpl.class);

    @Autowired
    @Qualifier("userSignRecordService")
    private UserSignRecordService userSignRecordService;

    @Autowired
    private PositionManager positionManager;
    
    private <S, T> JdResponse<T> retJdCResponse(JdCResponse<S> jdCResponse, Class<T> tClass) {
        try {
            if(jdCResponse.isSucceed() && jdCResponse.getData() != null){
                T target = tClass.newInstance();
                BeanUtils.copyProperties(jdCResponse.getData(), target);
                return new JdResponse<T>(jdCResponse.getCode(), jdCResponse.getMessage(), target);
            }
            return new JdResponse<T>(jdCResponse.getCode(), jdCResponse.getMessage());
        }catch (Exception e){
            log.error("类型转换异常!", e);
        }
        return new JdResponse<T>(JdResponse.CODE_ERROR, JdResponse.MESSAGE_ERROR);
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.queryPositionInfo",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<PositionData> queryPositionInfo(String positionCode) {
        JdResponse<PositionData> jdResponse = new JdResponse<>();
        jdResponse.toSucceed();
        if(StringUtils.isEmpty(positionCode)){
            jdResponse.toFail("参数错误");
            return jdResponse;
        }
        Result<com.jdl.basic.api.domain.position.PositionData> result = positionManager.queryPositionInfo(positionCode);
        if(result.isSuccess() && result.getData() != null){
            PositionData data = new PositionData();
            BeanUtils.copyProperties(result.getData(), data);
            jdResponse.toSucceed();
            jdResponse.setData(data);
        }else {
            jdResponse.toFail(result.getMessage());
        }
        return jdResponse;
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.checkBeforeSignIn",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<UserSignRecordData> checkBeforeSignIn(UserSignRequest userSignRequest) {
        com.jd.bluedragon.common.dto.station.UserSignRequest request = new com.jd.bluedragon.common.dto.station.UserSignRequest();
        BeanUtils.copyProperties(userSignRequest, request);
        JdCResponse<com.jd.bluedragon.common.dto.station.UserSignRecordData> jdCResponse = userSignRecordService.checkBeforeSignIn(request);
        return retJdCResponse(jdCResponse, UserSignRecordData.class);
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.signAutoWithGroup",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest) {
        com.jd.bluedragon.common.dto.station.UserSignRequest request = new com.jd.bluedragon.common.dto.station.UserSignRequest();
        BeanUtils.copyProperties(userSignRequest,request);
        JdCResponse<com.jd.bluedragon.common.dto.station.UserSignRecordData> jdCResponse = userSignRecordService.signAutoWithGroup(request);
        return retJdCResponse(jdCResponse, UserSignRecordData.class);
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.signOutWithGroup",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<UserSignRecordData> signOutWithGroup(UserSignRequest userSignRequest) {
        com.jd.bluedragon.common.dto.station.UserSignRequest request = new com.jd.bluedragon.common.dto.station.UserSignRequest();
        BeanUtils.copyProperties(userSignRequest, request);
        JdCResponse<com.jd.bluedragon.common.dto.station.UserSignRecordData> result = userSignRecordService.signOutWithGroup(request);
        return retJdCResponse(result, UserSignRecordData.class);
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.deleteUserSignRecord",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<UserSignRecordData> deleteUserSignRecord(UserSignRequest userSignRequest) {
        com.jd.bluedragon.common.dto.station.UserSignRequest request = new com.jd.bluedragon.common.dto.station.UserSignRequest();
        BeanUtils.copyProperties(userSignRequest, request);
        JdCResponse<com.jd.bluedragon.common.dto.station.UserSignRecordData> result = userSignRecordService.deleteUserSignRecord(request);
        return retJdCResponse(result, UserSignRecordData.class);
    }

    @JProfiler(jKey = "dms.ForeignUserSignService.querySignListByOperateUser",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query) {
        JdResponse<PageDto<UserSignRecordData>> response = new JdResponse<>();
        response.toSucceed();
        try{

            com.jd.bluedragon.common.dto.station.UserSignQueryRequest queryRequest = new com.jd.bluedragon.common.dto.station.UserSignQueryRequest();
            BeanUtils.copyProperties(query,queryRequest);
            if(log.isInfoEnabled()){
                log.info("查询签到记录 querySignListByOperateUser 入参—{}",JSON.toJSONString(queryRequest));
            }
            JdCResponse<PageDto<com.jd.bluedragon.common.dto.station.UserSignRecordData>> jdCResponse = userSignRecordService.querySignListByOperateUser(queryRequest);
            if(log.isInfoEnabled()){
                log.info("查询签到记录 querySignListByOperateUser 出参-{} ", JSON.toJSONString(jdCResponse));
            }
            if(!Objects.equals(JdCResponse.CODE_SUCCESS,jdCResponse.getCode()) || jdCResponse.getData() == null){
                response.toFail("查询签到记录失败！");
                return response;
            }
            PageDto<com.jd.bluedragon.common.dto.station.UserSignRecordData> dmsPageDto = jdCResponse.getData();
            PageDto<UserSignRecordData> pageDto = new PageDto<>();
            pageDto.setCurrentPage(dmsPageDto.getCurrentPage());
            pageDto.setPageSize(dmsPageDto.getPageSize());
            pageDto.setTotalRow(dmsPageDto.getTotalRow());
            if(CollectionUtils.isNotEmpty(dmsPageDto.getResult())){
                pageDto.setResult(dmsPageDto.getResult().stream().map(item -> {
                    UserSignRecordData userSignRecordData = new UserSignRecordData();
                    BeanUtils.copyProperties(item, userSignRecordData);
                    return userSignRecordData;
                }).collect(Collectors.toList()));    
            }
            response.setData(pageDto);

        }catch (Exception e){
            log.error("查询签到记录异常! param-{}",JSON.toJSONString(query),e);
            response.toError("查询签到记录异常!");
        }
        return response;
    }
}
