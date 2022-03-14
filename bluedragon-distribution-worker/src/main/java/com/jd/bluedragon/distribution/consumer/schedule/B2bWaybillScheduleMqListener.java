package com.jd.bluedragon.distribution.consumer.schedule;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.schedule.entity.B2bWaybillScheduleMq;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
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
        if(StringHelper.isEmpty(b2bWaybillScheduleMq.getBeginNodeCode())){
            log.warn("b2bWaybillScheduleMqListener.consume消息体无效，beginNodeCode为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
            return;
        }
        DmsScheduleInfo dmsScheduleInfo = new DmsScheduleInfo();
        dmsScheduleInfo.setWaybillCode(b2bWaybillScheduleMq.getWaybillCode());
        dmsScheduleInfo.setScheduleBillCode(b2bWaybillScheduleMq.getTransJobCode());
        dmsScheduleInfo.setCarrierName(b2bWaybillScheduleMq.getCarrierName());
        dmsScheduleInfo.setPackageNum(b2bWaybillScheduleMq.getBoxCount());
        dmsScheduleInfo.setScheduleTime(b2bWaybillScheduleMq.getScheduleTime());
        //根据网点编码获取对应的分拣中心Id
        Integer baseSiteCodeByNodeCode = this.getBaseSiteCodeByNodeCode(b2bWaybillScheduleMq.getBeginNodeCode());
        if(null != baseSiteCodeByNodeCode){
            dmsScheduleInfo.setDestDmsSiteCode(baseSiteCodeByNodeCode);
        }
        boolean syncScheduleInfoToDbResult = dmsScheduleInfoService.syncScheduleInfoToDb(dmsScheduleInfo);
        if(!syncScheduleInfoToDbResult){
            log.warn("b2bWaybillScheduleMqListener.consume消息落库失败，[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
        }
    }

    private Integer getBaseSiteCodeByNodeCode(String beginNodeCode){
        //调用基础资料获取网点编码对应的预分拣站点
        BaseStaffSiteOrgDto orgDto = baseMajorManager.getBaseSiteByDmsCode(beginNodeCode);
        if(orgDto != null && orgDto.getSiteCode() != null){
            //调用基础资料获取预分拣站点对应的分拣中心
            Integer siteCode = orgDto.getSiteCode();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteCode);
            if(baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getDmsId() != null){
                return baseStaffSiteOrgDto.getDmsId();
            }else{
                log.warn("b2bWaybillScheduleMqListener.getBaseSiteCodeByNodeCode 根据预分拣站点Id获取对应分拣中心失败，beginNodeCode:{}，siteCode:{}",beginNodeCode,siteCode);
            }
        }else{
            log.warn("b2bWaybillScheduleMqListener.getBaseSiteCodeByNodeCode 根据网点编码获取站点Id失败，beginNodeCode:{}",beginNodeCode);
        }
        return null;
    }
}
