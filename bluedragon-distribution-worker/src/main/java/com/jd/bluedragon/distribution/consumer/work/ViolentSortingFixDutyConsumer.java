package com.jd.bluedragon.distribution.consumer.work;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.work.ViolentSortingMessageDTO;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 定责系统下发暴力分拣消息
 * https://joyspace.jd.com/pages/Y5IcP5IB1iq3NK1Kj1ye
 */
@Slf4j
@Service("violentSortingConsumer")
public class ViolentSortingFixDutyConsumer extends MessageBaseConsumer {
    @Autowired
    private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.ViolentSortingFixResponsibleConsumer.consume",
                Constants.UMP_APP_NAME_DMSWORKER, false, true);
        String body = message.getText();
        try {
            if (StringHelper.isEmpty(body)) {
                log.error("定责系统下发暴力分拣消息，内容为空");
                return;
            }
            if (!JsonHelper.isJsonString(body)) {
                log.error("定责系统下发暴力分拣消息，内容非json格式内容为【{}】", body);
                return;
            }
            log.info("定责系统下发暴力分拣消息Body为【{}】", body);
            ViolentSortingMessageDTO violentSortingDto = JsonHelper.fromJson(body, ViolentSortingMessageDTO.class);
            if(!checkParam(violentSortingDto, body)){
                return;
            }
            jyBizTaskWorkGridManagerService.generateViolentSortingTask(violentSortingDto);
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("定责系统下发暴力分拣消消费失败:body:{}", body,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }
    private Boolean checkParam(ViolentSortingMessageDTO violentSortingDto, String body){
        if(violentSortingDto == null){
            log.error("定责系统下发暴力分拣消息,反序列化对象为空，body:{}", body);
            return false;
        }
        Long createTime = violentSortingDto.getCreateTime();
        if(createTime == null){
            log.warn("定责系统下发暴力分拣消息,创建时间为空，createTime为空,body:{}", body);
            return false;
        }
        Long id = violentSortingDto.getId();
        if(id == null){
            log.warn("定责系统下发暴力分拣消息,创建时间为空，id为空,body:{}", body);
            return false;
        }
        if(StringUtils.isBlank(violentSortingDto.getDeviceNo())){
            log.warn("定责系统下发暴力分拣消息, devcieNo为空,body:{}", body);
            return false;
        }
        if(StringUtils.isBlank(violentSortingDto.getDeviceName())){
            log.warn("定责系统下发暴力分拣消息, DeviceName为空,body:{}", body);
            return false;
        }
        if(StringUtils.isBlank(violentSortingDto.getNationalChannelCode())){
            log.warn("定责系统下发暴力分拣消息, NationalChannelCode为空,body:{}", body);
            return false;
        }
        if(StringUtils.isBlank(violentSortingDto.getUrl())){
            log.warn("定责系统下发暴力分拣消息,Url为空,body:{}", body);
            return false;
        }
        if(StringUtils.isBlank(violentSortingDto.getProcessInstanceId())){
            log.warn("定责系统下发暴力分拣消息, ProcessInstanceId为空,body:{}", body);
            return false;
        }
        return true;
    }
}
