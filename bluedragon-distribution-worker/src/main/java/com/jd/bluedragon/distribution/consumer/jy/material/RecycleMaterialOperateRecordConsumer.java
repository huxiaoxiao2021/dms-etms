package com.jd.bluedragon.distribution.consumer.jy.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.material.MaterialOperateNodeV2Enum;
import com.jd.bluedragon.distribution.jy.dto.material.RecycleMaterialOperateRecordDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.inspection.JyTrustHandoverAutoInspectionService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 物资循环操作记录
 * 文档：https://joyspace.jd.com/pages/xJbX7AWnN2TFGIktJXSN
 * @Author zhengchengfa
 * @Date 2024/2/29 14:58
 * @Description
 */
@Service
public class RecycleMaterialOperateRecordConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(RecycleMaterialOperateRecordConsumer.class);

    private static final Integer TYPE_IN = 1;
    private static final Integer TYPE_OUT = 2;
    private static final Integer TYPE_ONLINE = 3;

    @Autowired
    private JyTrustHandoverAutoInspectionService jyTrustHandoverAutoInspectionService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, objects);
        }
    }
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.RecycleMaterialOperateRecordConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("RecycleMaterialOperateRecordConsume consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("RecycleMaterialOperateRecordConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        RecycleMaterialOperateRecordDto mqBody = JsonHelper.fromJson(message.getText(), RecycleMaterialOperateRecordDto.class);
        if(mqBody == null){
            logger.error("RecycleMaterialOperateRecordConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            return;
        }
        logInfo("物资循环实操监听开始消费，mqBody={}", message.getText());
        try{
            if(TYPE_IN.equals(mqBody.getOperateType()) && MaterialOperateNodeV2Enum.AUTO_SCAN.getCode().equals(mqBody.getOperateNodeCode())) {
                this.recycleMaterialEnterSiteHandler(mqBody);
            }else {
                logInfo("物资循环实操监听,当前节点无处理逻辑，不做消费，mqBody={}", message.getText());
            }

        }catch (Exception ex) {
            logger.error("物资循环实操监听消息消费异常，businessId={},errMsg={},content={}");
            throw new JyBizException("物资循环实操监听消息消费异常,businessId=" + message.getBusinessId());
        }
        logInfo("物资循环实操监听消息消费成功，内容{}", message.getText());

    }

    /**
     * 循环物资进场监听
     */
    private void recycleMaterialEnterSiteHandler(RecycleMaterialOperateRecordDto mqBody) {
        RecycleMaterialAutoInspectionDto dto = this.convertRecycleMaterialAutoInspectionDto(mqBody);
        jyTrustHandoverAutoInspectionService.recycleMaterialEnterSiteAutoInspection(dto);
    }

    //实体转换
    private RecycleMaterialAutoInspectionDto convertRecycleMaterialAutoInspectionDto(RecycleMaterialOperateRecordDto mqBody) {
        RecycleMaterialAutoInspectionDto dto = new RecycleMaterialAutoInspectionDto();
        dto.setMaterialCode(mqBody.getMaterialCode());
        Integer siteId = Integer.valueOf(mqBody.getOperateSiteId());
        dto.setOperateSiteId(siteId);
        String siteName = mqBody.getOperateSiteName();
        if(StringUtils.isBlank(siteName)) {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(siteId);
            if(!Objects.isNull(baseSite) && StringUtils.isNotBlank(baseSite.getSiteName())) {
                siteName = baseSite.getSiteName();
            }
        }
        dto.setOperateSiteName(siteName);
        dto.setOperateTime(mqBody.getOperateTime());
        dto.setSendTime(mqBody.getSendTime());
        return dto;
    }
    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(RecycleMaterialOperateRecordDto mqBody) {
       if(StringUtils.isBlank(mqBody.getMaterialCode())) {
           logInfo("循环物资操作监听，materialCode[物资编码]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
           return false;
       }
        if(Objects.isNull(mqBody.getOperateType())) {
            logInfo("循环物资操作监听，operateNodeCode[操作节点]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getOperateSiteId())) {
            logInfo("循环物资操作监听，operateSiteId[操作场地]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }else if(!NumberHelper.isNumber(mqBody.getOperateSiteId())) {
            logInfo("循环物资操作监听，operateSiteId[操作场地]非法，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }else {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(mqBody.getOperateSiteId()));
            if(Objects.isNull(baseSite) || Objects.isNull(baseSite.getSiteCode()) || !BusinessUtil.isSorting(baseSite.getSiteType())) {
                logInfo("循环物资操作监听，，到达场地{}基础资料不存在或者非分拣中心，无效数据剔除，mqBody={},baseSite={}", mqBody.getOperateSiteId(), JsonHelper.toJson(mqBody), JsonHelper.toJson(baseSite));
                return false;
            }
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            logInfo("循环物资操作监听，operateTime[操作时间]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getSendTime())) {
            logInfo("循环物资操作监听，sendTime[系统实际发消息时间]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getOperateNodeCode())) {
            logInfo("循环物资操作监听，operateNodeCode[操作节点]为空，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(!Constants.SYS_CODE_DMS.equals(mqBody.getBizSource())) {
            logInfo("循环物资操作监听，bizSource非[DMS]不处理，无效数据剔除，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        return true;
    }

}
