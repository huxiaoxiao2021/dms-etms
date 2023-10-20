package com.jd.bluedragon.distribution.consumer.user.login;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoginInfoDto;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("loginInfoConsumer")
@Slf4j
public class LoginInfoConsumer extends MessageBaseConsumer {
    @Autowired
    JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;
    @Override
    public void consume(Message message) throws Exception {
        LoginInfoDto dto = JsonHelper.fromJson(message.getText(), LoginInfoDto.class);
        if(log.isDebugEnabled()) {
            log.debug("app登录消息处理，loginInfoConsumer：[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
        }
        if(dto == null || dto.getLoginRequest() == null || dto.getLoginUserResponse() == null){
            log.warn("app登录消息处理，消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            return;
        }
        LoginRequest request = dto.getLoginRequest();;
        LoginUserResponse response = dto.getLoginUserResponse();
        String erp = request.getErpAccount();
        if(StringUtils.isBlank(erp)){
            log.info("app登录消息处理，erp为空，直接返回");
            return;
        }
        //登录不成功
        if (!JdResponse.CODE_OK.equals(response.getCode())) {
            log.info("app登录消息处理，登录失败，直接返回，erp:{},code:{},message:{}", erp, response.getCode(), 
                    response.getMessage());
            return;
        }
        //无岗位码不处理
        if(StringUtils.isBlank(request.getPositionCode())){
            log.info("app登录消息处理，登录成功，无岗位码，直接返回，erp:{}", erp);
            return;
        }
        try {
            jyBizTaskWorkGridManagerService.generateManageInspectionTask(erp, request.getPositionCode(), response.getStaffName()
            , response.getSiteCode());
        }catch (Exception e){
            log.error("app登录消息处理，生成管理巡视任务异常，erp:{}", erp, e);
        }
        
    }
}
