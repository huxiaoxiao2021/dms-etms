package com.jd.bluedragon.distribution.box.jsf;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.sdk.api.box.IDMSBoxApi;
import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;
import com.jd.bluedragon.distribution.external.sdk.constants.ServiceMessageEnum;
import com.jd.bluedragon.distribution.external.sdk.dto.box.BoxDto;
import com.jd.bluedragon.distribution.external.sdk.dto.box.BoxReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoxExternalServiceImpl implements IDMSBoxApi {

    private static final Logger log = LoggerFactory.getLogger(BoxExternalServiceImpl.class);

    @Autowired
    private BoxService boxService;
    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    @Override
    public ServiceResult<BoxDto> getBoxByBoxCode(String boxCode) {
        ServiceResult<BoxDto> result = new ServiceResult<BoxDto>();
        try{
            Box box = boxService.findBoxByCode(boxCode);
            BoxDto resultData = new BoxDto();
            BeanUtils.copyProperties(box,resultData);
            result.toSuccess(resultData);
        }catch (Exception ex){
            log.error("getBoxByBoxCode has error. The error is " + ex.getMessage(),ex);
            result.toSystemError();
        }
        return result;
    }

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    @Override
    public ServiceResult<String> updateBoxStatus(BoxReq boxReq) {
        ServiceResult<String> result = new ServiceResult<String>();
        try{
            Boolean isUpdated = boxService.updateBoxStatus(boxReq);
            if (!isUpdated){
                result.customFail(ServiceMessageEnum.CODE_DEAL_ERROR.getCode(),ServiceMessageEnum.CODE_DEAL_ERROR.getMessage());
                return result;
            }
            return result;
        }catch (Exception ex){
            log.error("getBoxByBoxCode has error. The error is " + ex.getMessage(),ex);
            result.toSystemError();
        }
        return result;
    }
}
