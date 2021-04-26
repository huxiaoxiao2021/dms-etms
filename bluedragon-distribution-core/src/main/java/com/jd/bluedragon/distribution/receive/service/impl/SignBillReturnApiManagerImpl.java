package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.receive.service.SignBillReturnApiManager;
import com.jd.bluedragon.utils.JsonHelper;
import erp.ql.station.api.dto.CommonResponseDto;
import erp.ql.station.api.service.SignBillReturnApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author: liming522
 * @Description: 签单返回 审批结果获取接口
 *  参考文档： https://cf.jd.com/pages/viewpage.action?pageId=476539083
 * @Date: create in 2021/4/25 16:47
 */
@Service
public class SignBillReturnApiManagerImpl implements SignBillReturnApiManager {
    private static final Logger log = LoggerFactory.getLogger(SignBillReturnApiManagerImpl.class);


    @Autowired
    private SignBillReturnApi signBillReturnApi;

    @Override
    public InvokeResult<Boolean> checkSignBillReturn(String newWaybillCode, Integer siteId) {
        InvokeResult<Boolean>  result = new InvokeResult<>();
        result.success();

        CommonResponseDto<Boolean> responseDto = null;
        try {
            responseDto =   signBillReturnApi.checkSignBillReturn(newWaybillCode,siteId);
            if(log.isInfoEnabled()){
                log.info("签单返回审批接口返还结果:reponseDto:{},waybillCode:{}", JsonHelper.toJsonMs(responseDto),newWaybillCode);
            }
            // 返回空结果
            if(responseDto == null){
                log.error("签单返回审批接口返回空结果 waybillCode:{},siteCode:{}",newWaybillCode,siteId);
                result.setCode(InvokeResult.RESULT_NULL_CODE);
                result.setMessage("获取签单返还审核状态"+InvokeResult.RESULT_NULL_MESSAGE);
                return result;
            }

            //异常编码
            if(responseDto.getCode() != CommonResponseDto.CODE_SUCCESS){
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(StringUtils.isEmpty(responseDto.getMessage())?"签单返还异常不通过":responseDto.getMessage());
                return result;
            }

            //审批不通过
            if( responseDto.getData() == null || responseDto.getData().equals(Boolean.FALSE)){
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(StringUtils.isEmpty(responseDto.getMessage())?"签单返还审核不通过":responseDto.getMessage());
                return result;
            }
        }catch (Exception e){
            log.error("调用终端签单返回 审核状态接口异常 waybillCode:{},siteCode:{}",newWaybillCode,siteId,e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
    
