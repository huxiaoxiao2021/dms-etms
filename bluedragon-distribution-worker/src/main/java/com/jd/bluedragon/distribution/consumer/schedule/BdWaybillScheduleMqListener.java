package com.jd.bluedragon.distribution.consumer.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.schedule.entity.BdWaybillScheduleMq;
import com.jd.bluedragon.distribution.schedule.entity.BusinessTypeEnum;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.utils.StringHelper;

@Service("bdWaybillScheduleMqListener")
public class BdWaybillScheduleMqListener extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(BdWaybillScheduleMqListener.class);

    @Autowired
    private DmsScheduleInfoService dmsScheduleInfoService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Override
    public void consume(Message message) throws Exception {
    	BdWaybillScheduleMq mqObj = JsonHelper.fromJson(message.getText(), BdWaybillScheduleMq.class);
    	if(mqObj == null){
    		log.warn("消息转换失败！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	if(StringHelper.isEmpty(mqObj.getWaybillCode())){
    		log.warn("消息体无效，WaybillCode为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	BusinessTypeEnum businessType = null;
    	if(BusinessUtil.isEdn(mqObj.getSendPay(),mqObj.getWaybillSign())){
    		businessType = BusinessTypeEnum.EDN;
    	}
    	if(!BusinessTypeEnum.EDN.equals(businessType)){
    		log.warn("非企配仓数据，不做处理！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    		return;
    	}
    	DmsScheduleInfo dmsScheduleInfo = new DmsScheduleInfo();
    	dmsScheduleInfo.setWaybillCode(mqObj.getWaybillCode());
    	dmsScheduleInfo.setScheduleBillCode(mqObj.getScheduleBillCode());
    	dmsScheduleInfo.setCarriername(mqObj.getCarriername());
    	dmsScheduleInfo.setPackageNum(mqObj.getGoodNumber());
    	dmsScheduleInfo.setScheduleTime(mqObj.getScheduleTime());
    	dmsScheduleInfo.setBusinessType(businessType.getCode());
    	//调用基础资料获取预分拣站点对应的分拣中心
    	Integer oldSiteId = mqObj.getOldSiteId();
    	if(oldSiteId != null){
    		BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByStaffId(oldSiteId);
    		if(baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getDmsId() != null){
    			dmsScheduleInfo.setDestDmsSiteCode(baseStaffSiteOrgDto.getDmsId());
    		}else{
    			log.warn("根据预分拣站点[{}]获取绑定的分拣中心失败！[{}-{}]:[{}]",oldSiteId,message.getTopic(),message.getBusinessId(),message.getText());
    		}
    	}else{
    		log.warn("消息体中预分拣站点oldSiteId为空！[{}-{}]:[{}]",message.getTopic(),message.getBusinessId(),message.getText());
    	}
    	dmsScheduleInfoService.syncScheduleInfoToDb(dmsScheduleInfo);
    }
    
}
