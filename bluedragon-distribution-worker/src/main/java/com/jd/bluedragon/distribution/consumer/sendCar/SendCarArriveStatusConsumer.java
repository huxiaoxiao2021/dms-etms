package com.jd.bluedragon.distribution.consumer.sendCar;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.JySendArriveStatusDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.jdi.dto.AccountDto;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.TransWorkPlatformEnterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author laoqingchang1
 * @description 消费车辆到达状态消息，
 *              发送车辆停靠时间到运输平台的运输执行系统(JDI)
 * @date 2022-08-30 13:19
 */
@Service("sendCarArriveStatusConsumer")
public class SendCarArriveStatusConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(SendCarArriveStatusConsumer.class);

    @Autowired
    JdiTransWorkWSManager jdiTransWorkWSManager;
    @Autowired
    BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        if(!JsonHelper.isJsonString(message.getText())){
            log.warn("拣运发货任务车辆拍照-消息体非JSON格式，内容为【{}】",message.getText());
            return;
        }
        try {
            JySendArriveStatusDto jySendArriveStatusDto =
                JsonHelper.fromJsonUseGson(message.getText(), JySendArriveStatusDto.class);
            if (jySendArriveStatusDto == null){
                log.error("拣运发货发送车辆停靠时间消息参数错误,消息丢弃,入参：{}",message.getText());
                return;
            }
            AccountDto accountDto = genAccountDto(jySendArriveStatusDto);
            TransWorkPlatformEnterDto transWorkPlatformEnterDto = genTransWorkPlatformEnterDto(jySendArriveStatusDto);
            CommonDto ret = jdiTransWorkWSManager.recordBeginPlatformEnterTime(accountDto, transWorkPlatformEnterDto);
            if(!ret.isSuccess()){
                log.error("拣运发货任务发送车辆停靠时间失败,入参: {}, 错误: {}", JsonHelper.toJson(jySendArriveStatusDto), ret.getMessage());
            }else {
                log.info("调用发送车辆停靠时间接口成功, account: {}, operate: {}",
                    JsonHelper.toJson(accountDto), JsonHelper.toJson(transWorkPlatformEnterDto));
            }
        }catch (Exception e){
            log.error("【{}】消费消息异常",message.getText(),e);
            throw e;
        }
    }

    private AccountDto genAccountDto(JySendArriveStatusDto statusDto){
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountCode(statusDto.getOperateUserErp());
        accountDto.setAccountName(statusDto.getOperateUserName());
        return accountDto;
    }

    private TransWorkPlatformEnterDto genTransWorkPlatformEnterDto(JySendArriveStatusDto jySendArriveStatusDto){
        TransWorkPlatformEnterDto transWorkPlatformEnterDto = new TransWorkPlatformEnterDto();
        transWorkPlatformEnterDto.setTransWorkCode(jySendArriveStatusDto.getTransWorkCode());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(jySendArriveStatusDto.getOperateSiteId().intValue());
        if(baseStaffSiteOrgDto != null) {
            transWorkPlatformEnterDto.setBeginNodeCode(baseStaffSiteOrgDto.getDmsSiteCode());
        }
        transWorkPlatformEnterDto.setVehicleArrived(jySendArriveStatusDto.getVehicleArrived());
        transWorkPlatformEnterDto.setOperateTime(new Date(jySendArriveStatusDto.getOperateTime()));
        transWorkPlatformEnterDto.setImgList(jySendArriveStatusDto.getImgList());
        return transWorkPlatformEnterDto;
    }
}
