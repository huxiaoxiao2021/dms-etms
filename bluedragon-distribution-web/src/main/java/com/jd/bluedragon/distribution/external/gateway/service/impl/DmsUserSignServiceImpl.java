package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jsf.domain.UserSignQueryRequest;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRecordData;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRequest;
import com.jd.bluedragon.distribution.jsf.service.DmsUserSignService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/23 17:35
 * @Description:
 */
@Service("dmsUserSignService")
public class DmsUserSignServiceImpl implements DmsUserSignService {

    private static final Logger log = LoggerFactory.getLogger(DmsUserSignServiceImpl.class);

    @Autowired
    @Qualifier("userSignRecordService")
    private UserSignRecordService userSignRecordService;

    @Override
    public JdResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest) {
        JdResponse<UserSignRecordData> response = new JdResponse<>();
        response.toSucceed();
        try{
            com.jd.bluedragon.common.dto.station.UserSignRequest request = new com.jd.bluedragon.common.dto.station.UserSignRequest();
            BeanUtils.copyProperties(userSignRequest,request);
            if(log.isInfoEnabled()){
                log.info("签到签出 signAutoWithGroup 入参-{} ", JSON.toJSONString(request));
            }
            JdCResponse<com.jd.bluedragon.common.dto.station.UserSignRecordData> jdCResponse = userSignRecordService.signAutoWithGroup(request);
            if(log.isInfoEnabled()){
                log.info("签到签出 signAutoWithGroup 出参-{} ", JSON.toJSONString(jdCResponse));
            }
            if(!Objects.equals(JdCResponse.CODE_SUCCESS,jdCResponse.getCode()) || jdCResponse.getData() == null){
                response.setCode(jdCResponse.getCode());
                response.setMessage(jdCResponse.getMessage());
                return response;
            }
            UserSignRecordData recordData = new UserSignRecordData();
            BeanUtils.copyProperties(jdCResponse.getData(),recordData);
            response.setData(recordData);
        }catch (Exception e){
            log.error("用户自动签到、签退异常 param-{}",JSON.toJSONString(userSignRequest),e);
            response.toError("用户自动签到、签退异常!");
        }
        return response;
    }

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
            PageDto<UserSignRecordData> pageDto = new PageDto<>();
            BeanUtils.copyProperties(jdCResponse.getData(),pageDto);
            response.setData(pageDto);

        }catch (Exception e){
            log.error("查询签到记录异常! param-{}",JSON.toJSONString(query),e);
            response.toError("查询签到记录异常!");
        }
        return response;
    }
}
