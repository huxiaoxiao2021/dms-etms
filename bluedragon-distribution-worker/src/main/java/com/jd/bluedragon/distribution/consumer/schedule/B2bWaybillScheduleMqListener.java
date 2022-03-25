package com.jd.bluedragon.distribution.consumer.schedule;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.schedule.entity.B2bWaybillScheduleMq;
import com.jd.bluedragon.distribution.schedule.entity.BusinessTypeEnum;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * B2B-TMS企配仓运单调度结果MQ
 * @author wengguoqi
 * @date 2022/3/10 19:30
 */
@Service("b2bWaybillScheduleMqListener")
public class B2bWaybillScheduleMqListener extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(B2bWaybillScheduleMqListener.class);

    @Autowired
    private DmsScheduleInfoService dmsScheduleInfoService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        if(log.isInfoEnabled()){
            log.info("b2bWaybillScheduleMqListener.consume消息入参:[{}]",message.getText());
        }
        B2bWaybillScheduleMq b2bWaybillScheduleMq = JsonHelper.fromJson(message.getText(), B2bWaybillScheduleMq.class);
        if(b2bWaybillScheduleMq == null){
            log.warn("b2bWaybillScheduleMqListener.consume消息转换失败，[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            return;
        }
        if(StringHelper.isEmpty(b2bWaybillScheduleMq.getWaybillCode())){
            log.warn("b2bWaybillScheduleMqListener.consume消息体无效，WaybillCode为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            return;
        }
        if(StringHelper.isEmpty(b2bWaybillScheduleMq.getSiteNodeCode())){
            log.warn("b2bWaybillScheduleMqListener.consume消息体无效，siteNodeCode为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            return;
        }
        //根据网点编码获取青龙基础资料中的分拣信息
        String scheduleMqSiteNodeCode = b2bWaybillScheduleMq.getSiteNodeCode();
        BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseSiteByDmsCode(scheduleMqSiteNodeCode);
        if(null == baseSiteByDmsCode){
            log.warn("b2bWaybillScheduleMqListener.consume 根据网点编码获取青龙基础资料分拣信息为空！scheduleMqSiteNodeCode:[{}]-WaybillCode:[{}]",scheduleMqSiteNodeCode,b2bWaybillScheduleMq.getWaybillCode());
            return;
        }
        //根据子类型SubType==6540判断是否企配仓类型
        if(!BusinessUtil.isEdnDmsSite(baseSiteByDmsCode.getSubType())){
            log.warn("b2bWaybillScheduleMqListener.consume 运单非企配仓网点类型数据，不做处理！WaybillCode:[{}] scheduleMqSiteNodeCode:[{}] SubType:[{}]",b2bWaybillScheduleMq.getWaybillCode(),scheduleMqSiteNodeCode,baseSiteByDmsCode.getSubType());
            return;
        }
        DmsScheduleInfo dmsScheduleInfo = new DmsScheduleInfo();
        dmsScheduleInfo.setWaybillCode(b2bWaybillScheduleMq.getWaybillCode());
        dmsScheduleInfo.setScheduleBillCode(b2bWaybillScheduleMq.getTransJobCode());
        dmsScheduleInfo.setCarrierName(b2bWaybillScheduleMq.getCarrierName());
        dmsScheduleInfo.setPackageNum(b2bWaybillScheduleMq.getBoxCount());
        dmsScheduleInfo.setScheduleTime(b2bWaybillScheduleMq.getScheduleTime());
        dmsScheduleInfo.setBusinessType(BusinessTypeEnum.EDN.getCode());
        if(null != baseSiteByDmsCode.getSiteCode()){
            dmsScheduleInfo.setDestDmsSiteCode(baseSiteByDmsCode.getSiteCode());
        }else{
            log.warn("b2bWaybillScheduleMqListener.consume 获取青龙分拣信息SiteCode字段值为空！WaybillCode:[{}] scheduleMqSiteNodeCode:[{}] ",b2bWaybillScheduleMq.getWaybillCode(),scheduleMqSiteNodeCode);
        }
        boolean syncScheduleInfoToDbResult = dmsScheduleInfoService.syncScheduleInfoToDb(dmsScheduleInfo);
        if(!syncScheduleInfoToDbResult){
            log.warn("b2bWaybillScheduleMqListener.consume消息落库结果为false，[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            throw new RuntimeException("b2bWaybillScheduleMqListener.consume消息落库结果为false message=" + message.getText());
        }
    }
}
