package com.jd.bluedragon.distribution.consumer.jy.strand;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.JyStrandReportTaskCreateReq;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.config.constants.StrandReasonBusinessTagEnum;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.service.ConfigStrandReasonService;
import com.jd.bluedragon.distribution.jy.dto.strand.TransportRejectAddCarApplyMQ;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 运输驳回加车申请消费
 *  content：系统自建滞留任务
 *
 * @author hujiping
 * @date 2023/3/31 5:45 PM
 */
@Service("transportRejectAddCarApplyConsumer")
public class TransportRejectAddCarApplyConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(TransportRejectAddCarApplyConsumer.class);

    @Autowired
    private JyBizTaskStrandReportService jyBizTaskStrandReportService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private StrandService strandService;

    @Autowired
    private ConfigStrandReasonService configStrandReasonService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("TransportRejectAddCarApplyConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("运输驳回处理消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            TransportRejectAddCarApplyMQ rejectAddCarApplyMQ = JsonHelper.fromJsonUseGson(message.getText(), TransportRejectAddCarApplyMQ.class);
            if(rejectAddCarApplyMQ == null) {
                logger.warn("运输驳回处理消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            if(!Objects.equals(rejectAddCarApplyMQ.getBusinessSource(), 170) // 170-表示来源于分拣
                    || StringUtils.isEmpty(rejectAddCarApplyMQ.getTransPlanCode()) 
                    || StringUtils.isEmpty(rejectAddCarApplyMQ.getBeginNodeCode())
                    || StringUtils.isEmpty(rejectAddCarApplyMQ.getEndNodeCode())
                    || StringUtils.isEmpty(rejectAddCarApplyMQ.getReasonType())){
                logger.warn("运输驳回消息体参数错误，内容为【{}】", message.getText());
                return;
            }
            // 运输唯一标识校验
            if(jyBizTaskStrandReportService.existCheck(rejectAddCarApplyMQ.getTransPlanCode())){
                logger.warn("加车申请运输驳回:{}已处理!", rejectAddCarApplyMQ.getTransPlanCode());
                return;
            }
            // 系统自建任务
            InvokeResult<Void> result = jyBizTaskStrandReportService.systemCreateStrandReportTask(convertStrand(rejectAddCarApplyMQ));
            if(!result.codeSuccess()){
               logger.error("运输驳回系统自建滞留任务失败，失败原因:{}", result.getMessage()); 
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("运输驳回消息处理消息消费异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private JyStrandReportTaskCreateReq convertStrand(TransportRejectAddCarApplyMQ rejectAddCarApplyMQ) {
        JyStrandReportTaskCreateReq jyStrandReportTaskCreateReq = new JyStrandReportTaskCreateReq();
        jyStrandReportTaskCreateReq.setTransportRejectBiz(rejectAddCarApplyMQ.getTransPlanCode());
        String beginNodeCode = rejectAddCarApplyMQ.getBeginNodeCode();
        BaseStaffSiteOrgDto beginNode = baseMajorManager.getBaseSiteByDmsCode(beginNodeCode);
        if(beginNode != null){
            jyStrandReportTaskCreateReq.setSiteCode(beginNode.getSiteCode());
            jyStrandReportTaskCreateReq.setSiteName(beginNode.getSiteName());
        }
        String endNodeCode = rejectAddCarApplyMQ.getEndNodeCode();
        BaseStaffSiteOrgDto endNode = baseMajorManager.getBaseSiteByDmsCode(endNodeCode);
        if(endNode != null){
            jyStrandReportTaskCreateReq.setNextSiteCode(endNode.getSiteCode());
            jyStrandReportTaskCreateReq.setNextSiteName(endNode.getSiteName()); 
        }
        String createUserCode = rejectAddCarApplyMQ.getCreateUserCode();
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(createUserCode);
        jyStrandReportTaskCreateReq.setOperateUserErp(createUserCode);
        jyStrandReportTaskCreateReq.setOperateUserName(baseStaff == null ? createUserCode : baseStaff.getStaffName());
        // 运输驳回原因对应我们系统的滞留原因处理
        strandReasonDeal(rejectAddCarApplyMQ);
        jyStrandReportTaskCreateReq.setStrandCode(Integer.valueOf(rejectAddCarApplyMQ.getReasonType()));
        jyStrandReportTaskCreateReq.setStrandReason(rejectAddCarApplyMQ.getReasonTypeName());
        jyStrandReportTaskCreateReq.setProveUrlList(rejectAddCarApplyMQ.getPhotoUrlList());
        jyStrandReportTaskCreateReq.setTaskType(JyBizStrandTaskTypeEnum.TRANS_REJECT_SYSTEM.getCode());
        jyStrandReportTaskCreateReq.setWaveTime(rejectAddCarApplyMQ.getPlanDepartTime() == null 
                ? null : DateHelper.formatDate(rejectAddCarApplyMQ.getPlanDepartTime(), DateHelper.DATE_FORMAT_HHmm));
        return jyStrandReportTaskCreateReq;
    }

    private void strandReasonDeal(TransportRejectAddCarApplyMQ rejectAddCarApplyMQ) {
        InvokeResult<List<ConfigStrandReasonData>> result = strandService.queryJyStrandReasons();
        if(result != null && CollectionUtils.isNotEmpty(result.getData())){
            List<Integer> strandReasonCodeList = Lists.newArrayList();
            for (ConfigStrandReasonData tempReason : result.getData()) {
                strandReasonCodeList.add(tempReason.getReasonCode());
            }
            if(!strandReasonCodeList.contains(Integer.valueOf(rejectAddCarApplyMQ.getReasonType()))){
                ConfigStrandReason newReason = new ConfigStrandReason();
                newReason.setReasonCode(Integer.valueOf(rejectAddCarApplyMQ.getReasonType()));
                newReason.setReasonName(rejectAddCarApplyMQ.getReasonTypeName());
                newReason.setSyncFlag(Constants.NUMBER_ONE);
                newReason.setOrderNum(Constants.NUMBER_ONE);
                newReason.setRemark("加车申请-运输驳回新增滞留原因");
                newReason.setCreateUser(rejectAddCarApplyMQ.getCreateUserCode());
                newReason.setCreateTime(new Date());
                newReason.setYn(Constants.NUMBER_ONE);
                newReason.setBusinessTag(StrandReasonBusinessTagEnum.BUSINESS_TAG_NEW.getCode());
                configStrandReasonService.insert(newReason);
            }
        }
    }
}
