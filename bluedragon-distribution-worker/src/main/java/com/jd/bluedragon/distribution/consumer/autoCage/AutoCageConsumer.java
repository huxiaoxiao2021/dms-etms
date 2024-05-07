package com.jd.bluedragon.distribution.consumer.autoCage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorData;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.autoCage.domain.AutoCageMq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.cage.DmsDeviceCageJsfService;
import com.jd.bluedragon.distribution.cage.request.CollectPackageReq;
import com.jd.bluedragon.distribution.cage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动化装笼消费
 */
@Service("autoCageConsumer")
@Slf4j
public class AutoCageConsumer extends MessageBaseConsumer {

    @Autowired
    private DmsDeviceCageJsfService dmsDeviceCageJsfService;
    @Autowired
    JyBizTaskComboardService jyBizTaskComboardService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    private SendCodeService sendCodeService;
    @Autowired
    private JyComBoardSendService jyComBoardSendService;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.AutoCageConsumer.consume", mState = JProEnum.TP)
    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message) || StringUtils.isEmpty(message.getText())) {
            log.warn("AutoCageConsumer 消息为空！");
            return;
        }

        AutoCageMq mq = JsonHelper.fromJson(message.getText(), AutoCageMq.class);
        if (mq == null) {
            log.info("自动化装笼json转换失败：{}", JsonHelper.toJson(message));
            return;
        }

        if (StringUtils.isEmpty(mq.getBarcode())
                || StringUtils.isEmpty(mq.getCageBoxCode())
                || mq.getOperatorTime() == null
                || mq.getSiteCode() == null
                || mq.getOperatorErp() == null) {
            log.info("自动化装笼参数校验错误，缺少必须参数：{}", JsonHelper.toJson(mq));
            return;
        }

        try{

            //补充组板任务
            JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(mq.getSiteCode(), mq.getBoardCode());
            ComboardScanReq comboardScanReq = new ComboardScanReq();
            String sendCode = null;
            if (entity == null) {
                JyBizTaskComboardEntity record = createJyBizTaskComboardEntity(mq, comboardScanReq);
                log.info("补组板任务："+JsonHelper.toJson(record));
                jyBizTaskComboardService.save(record);
                sendCode = record.getSendCode();
            }else{
                sendCode = entity.getSendCode();
            }

            //发货
            fillComboardScanReq(comboardScanReq,mq,sendCode);
            log.info("装笼发货参数组装："+JsonHelper.toJson(comboardScanReq));
            jyComBoardSendService.execSend(comboardScanReq);
        }catch (Exception e) {
            throw new RuntimeException("AutoCageConsumer ,jmq自动重试!");
        }

    }


    private JyBizTaskComboardEntity createJyBizTaskComboardEntity(AutoCageMq mq, ComboardScanReq comboardScanReq) {
        JyBizTaskComboardEntity record = new JyBizTaskComboardEntity();
        record.setBoardCode(mq.getBoardCode());
        record.setBizId(genTaskBizId(comboardScanReq));
        record.setSendCode(genSendCode(mq));
        record.setStartSiteId(Long.valueOf(mq.getSiteCode()));
        record.setEndSiteId(Long.valueOf(mq.getDestinationId()));
        record.setBoardStatus(ComboardStatusEnum.FINISHED.getCode());
        record.setCreateTime(mq.getOperatorTime());
        record.setUpdateTime(mq.getOperatorTime());
        record.setCreateUserErp(mq.getOperatorErp());
        record.setCreateUserName(mq.getOperatorName());
        record.setUpdateUserErp(mq.getOperatorErp());
        record.setUpdateUserName(mq.getOperatorName());
        record.setHaveScanCount(1);
        record.setComboardSource(2);
        record.setUnsealTime(mq.getOperatorTime());
        return record;
    }

    private String genSendCode(AutoCageMq mq) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code,
                String.valueOf(mq.getSiteCode()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code,
                String.valueOf(mq.getDestinationId()));
        String sendCode = sendCodeService
                .createSendCode(attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS, mq.getOperatorErp());
        return sendCode;
    }

    private String genTaskBizId(ComboardScanReq request) {
        String ownerKey = String.format(JyBizTaskComboardEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        String bizId = ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
        request.setBizId(bizId);
        return bizId;
    }
    private ComboardScanReq fillComboardScanReq(ComboardScanReq req,AutoCageMq mq,String sendCode) {
        User user = new User();
        if (StringUtils.isNotEmpty(mq.getOperatorErp())){
            try{
                BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseStaffByErpCache(mq.getOperatorErp());
                if (baseStaffSiteOrgDto != null){
                    user.setUserCode(baseStaffSiteOrgDto.getStaffNo());
                }
            }catch (Exception e){
                log.error("查询用户信息失败",e);
            }
        }

        user.setUserErp(mq.getOperatorErp());
        user.setUserName(mq.getOperatorName());
        req.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(mq.getSiteCode());
        BaseSite site = baseMajorManager.getSiteBySiteCode(mq.getSiteCode());
        currentOperate.setSiteName(site.getSiteName());
        currentOperate.setOperateTime(mq.getOperatorTime());
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        operatorData.setOperatorId(mq.getMachineCode());
        operatorData.setMachineCode(mq.getMachineCode());
        currentOperate.setOperatorTypeCode(operatorData.getOperatorTypeCode());
        currentOperate.setOperatorId(operatorData.getOperatorId());
        currentOperate.setOperatorData(operatorData);

        req.setCurrentOperate(currentOperate);
        req.setBarCode(mq.getCageBoxCode());
        req.setBizSource(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name());
        req.setBoardCode(mq.getBoardCode());
        req.setEndSiteId(mq.getDestinationId());
        req.setDestinationId(mq.getDestinationId());
        req.setEndSiteName(mq.getDestination());
        req.setSendCode(sendCode);
        return req;
    }



}
