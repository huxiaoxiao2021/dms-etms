package com.jd.bluedragon.distribution.worker.inspection;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.coldchain.dto.CCInAndOutBoundMessage;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainOperateTypeEnum;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 推送验货MQ消息
 * Created by wangtingwei on 2017/1/17.
 */
public class PushMessageHook implements TaskHook<InspectionTaskExecuteContext> {

    private static final Log log = LogFactory.getLog(PushMessageHook.class);

    @Autowired
    private InspectionNotifyService inspectionNotifyService;

    @Autowired
    @Qualifier("ccInAndOutBoundProducer")
    private DefaultJMQProducer ccInAndOutBoundProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @JProfiler(jKey = "dmsworker.PushMessageHook.hook")
    @Override
    public int hook(InspectionTaskExecuteContext context) {
        Set sendInspectionKey = new HashSet();
        for (CenConfirm cenConfirm : context.getCenConfirmList()) {
            InspectionMQBody inspectionMQBody = new InspectionMQBody();
            inspectionMQBody.setWaybillCode(null != cenConfirm.getWaybillCode() ? cenConfirm.getWaybillCode() : SerialRuleUtil.getWaybillCode(cenConfirm.getPackageBarcode()));
            if (inspectionMQBody.getWaybillCode() == null || sendInspectionKey.contains(inspectionMQBody.getWaybillCode())) {
                continue;
            }
            sendInspectionKey.add(inspectionMQBody.getWaybillCode() != null ? inspectionMQBody.getWaybillCode() : "");
            inspectionMQBody.setInspectionSiteCode(cenConfirm.getCreateSiteCode());
            inspectionMQBody.setCreateUserCode(cenConfirm.getInspectionUserCode());
            inspectionMQBody.setCreateUserName(cenConfirm.getInspectionUser());
            inspectionMQBody.setOperateTime(null != cenConfirm.getInspectionTime() ? cenConfirm.getInspectionTime() : new Date());
            inspectionMQBody.setSource("DMS-INSPECTION");

            inspectionNotifyService.send(inspectionMQBody);/*此处MQ推送时，失败将添加任务，以确保MQ发送成功*/
        }
        this.pushColdChainOperateMQ(context);
        return 0;
    }

    private void pushColdChainOperateMQ (InspectionTaskExecuteContext context) {
        Waybill waybill = context.getBigWaybillDto().getWaybill();
        if (waybill == null) {
            return;
        }
        String waybillSign = waybill.getWaybillSign();
        if (!(BusinessUtil.isColdChainKBWaybill(waybillSign) || BusinessUtil.isColdChainCityDeliveryWaybill(waybillSign))) {
            return ;
        }
        List<CenConfirm> cenConfirmList = context.getCenConfirmList();
        BaseStaffSiteOrgDto siteOrgDto = context.getCreateSite();
        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setOrgId(String.valueOf(siteOrgDto.getOrgId()));
        body.setOrgName(siteOrgDto.getOrgName());
        body.setSiteId(String.valueOf(siteOrgDto.getSiteCode()));
        body.setSiteName(siteOrgDto.getSiteName());
        body.setOperateType(ColdChainOperateTypeEnum.INSPECTION.getType());
        Integer userCode = cenConfirmList.get(0).getInspectionUserCode();
        if (userCode != null) {
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(userCode);
            if (dto != null) {
                body.setOperateERP(dto.getErp());
            }
        }

        List<Message> messageList = new ArrayList<>();
        for (CenConfirm cenConfirm : cenConfirmList) {
            body.setOperateTime(DateHelper.formatDateTime(cenConfirm.getOperateTime()));
            body.setPackageNo(cenConfirm.getPackageBarcode());
            body.setWaybillNo(cenConfirm.getWaybillCode());
            Message message = new Message();
            message.setBusinessId(cenConfirm.getPackageBarcode());
            message.setText(JSON.toJSONString(body));
            message.setTopic(ccInAndOutBoundProducer.getTopic());
            messageList.add(message);
        }
        try {
            ccInAndOutBoundProducer.batchSend(messageList);
        } catch (JMQException e) {
            log.error("[验货任务]推送冷链操作验货消息时发生异常" ,e);
            throw new RuntimeException(e);
        }
    }
}
