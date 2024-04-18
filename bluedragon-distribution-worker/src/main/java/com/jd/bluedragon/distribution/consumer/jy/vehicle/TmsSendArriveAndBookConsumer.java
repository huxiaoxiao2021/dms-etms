package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.AutoInspectionSiteTypeConf;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.TmsSendArriveAndBookMqBody;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.inspection.JyTrustHandoverAutoInspectionService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.Constants.TMS_SEND_ARRIVE_AND_BOOK_SITE_TYPE_CONF;

/**
 * 运输系统-围栏到车后发送包裹到达消息
 * 文档：https://cf.jd.com/pages/viewpage.action?pageId=364760234
 * @Author zhengchengfa
 * @Date 2024/2/29 14:58
 * @Description
 */
@Service
public class TmsSendArriveAndBookConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsSendArriveAndBookConsumer.class);

    // 操作类型	: 1是发车，2是到车 99:委托书;100:围栏发车，200：围栏到车，10-铁路发车，20-铁路到车，30-发货登记
    private static final Integer SEND_ARRIVE_TYPE_200 = 200;


    @Autowired
    private JyTrustHandoverAutoInspectionService jyTrustHandoverAutoInspectionService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SysConfigService sysConfigService;

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
    @JProfiler(jKey = "DMSWORKER.jy.TmsSendArriveAndBookConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsSendArriveAndBookConsume consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsSendArriveAndBookConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsSendArriveAndBookMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsSendArriveAndBookMqBody.class);
        if(mqBody == null){
            logger.error("TmsSendArriveAndBookConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        CallerInfo info = Profiler.registerInfo("DMS.WORKER.jy.TmsSendArriveAndBookConsume.consume",
                Constants.UMP_APP_NAME_DMSWORKER,false,true);
        try{
            //无效数据过滤
            if(!invalidDataFilter(mqBody)) {
                return;
            }
            if(SEND_ARRIVE_TYPE_200.equals(mqBody.getSendArriveType())) {
                logInfo("运输围栏到车包裹到达消息开始消费，mqBody={}", message.getText());
                PackageArriveAutoInspectionDto packageArriveAutoInspectionDto = this.convertPackageArriveAutoInspectionDto(mqBody);
                jyTrustHandoverAutoInspectionService.packageArriveCarAutoInspection(packageArriveAutoInspectionDto);
                logInfo("运输围栏到车包裹到达消息消费成功，内容{}", message.getText());
            }else {
                logInfo("运输非围栏到车节点暂无消费逻辑，不做处理，mqBody={}", message.getText());
            }
        }catch (Exception ex) {
            Profiler.functionError(info);
            logger.error("topic:tms_send_arrive_and_book消息消费异常，businessId={},errMsg={},content={}");
            throw new JyBizException("topic:tms_send_arrive_and_book消息消费异常,businessId=" + message.getBusinessId());
        }finally {
            Profiler.registerInfoEnd(info);
        }

    }

    private PackageArriveAutoInspectionDto convertPackageArriveAutoInspectionDto(TmsSendArriveAndBookMqBody mqBody) {
        PackageArriveAutoInspectionDto dto = new PackageArriveAutoInspectionDto();
        dto.setTransBookCode(mqBody.getTransBookCode());
        dto.setPackageCode(mqBody.getPackageCode());
        dto.setWaybillCode(StringUtils.isNotBlank(mqBody.getWaybillCode()) ? mqBody.getWaybillCode() : WaybillUtil.getWaybillCode(mqBody.getPackageCode()));
        dto.setTransWorkCode(mqBody.getTransWorkCode());
        dto.setTransWorkItemCode(mqBody.getTransWorkItemCode());
        dto.setArriveSiteCode(mqBody.getEndNodeCode());

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getEndNodeCode());
        if(Objects.isNull(baseSite) || Objects.isNull(baseSite.getSiteCode())) {
            logger.error("基础资料获取围栏到车场地失败");
            throw new JyBizException(String.format("包裹%s围栏到车场地%s基础资料获取失败", mqBody.getPackageCode(), mqBody.getEndNodeCode()));
        }
        if(StringUtils.isBlank(baseSite.getSiteName())) {
            logWarn("围栏到车包裹到达场地名称为空，packageCode={},到达场地={}，基础资料返回={}", mqBody.getPackageCode(), mqBody.getEndNodeCode(), JsonHelper.toJson(baseSite));
        }
        dto.setArriveSiteId(baseSite.getSiteCode());
        dto.setArriveSiteName(StringUtils.isBlank(baseSite.getSiteName()) ? StringUtils.EMPTY : baseSite.getSiteName());
        dto.setCreateTime(mqBody.getCreateTime());
        dto.setOperateTime(mqBody.getOperateTime());
        dto.setFirstConsumerTime(System.currentTimeMillis());
        return dto;
    }


    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsSendArriveAndBookMqBody mqBody) {
        if(StringUtils.isBlank(mqBody.getTransBookCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，transBookCode【委托书编码:TMS系统全局唯一】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(!WaybillUtil.isPackageCode(mqBody.getPackageCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，packageCode非法，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getTransWorkCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，TransWorkCode【派车任务编码】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getTransWorkItemCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，TransWorkItemCode【派车任务明细编码】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getEndNodeCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，EndNodeCode【到达场地】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            logInfo("运输围栏到车包裹到达消息过滤，OperateTime【操作时间】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getCreateTime())) {
            logInfo("运输围栏到车包裹到达消息过滤，createTime【下发时间】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getEndNodeCode());
        if(Objects.isNull(baseSite) || Objects.isNull(baseSite.getSiteCode()) || !BusinessUtil.isSorting(baseSite.getSiteType())) {
            logInfo("运输围栏到车包裹到达消息过滤，到达场地{}基础资料不存在或者非分拣中心，mqBody={},site={}", mqBody.getEndNodeCode(), JsonHelper.toJson(mqBody), JsonHelper.toJson(baseSite));
            return false;
        }

        if(!sysConfigService.getByListContainOrAllConfig(Constants.TMS_SEND_ARRIVE_AND_BOOK_SITE_CONF,String.valueOf(baseSite.getSiteCode()))){
            logInfo("运输围栏到车包裹到达消息场地开关过滤，到达场地{}，mqBody={},site={}", mqBody.getEndNodeCode(), JsonHelper.toJson(mqBody), JsonHelper.toJson(baseSite));
            return false;
        }

        // 站点类型白名单校验
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(TMS_SEND_ARRIVE_AND_BOOK_SITE_TYPE_CONF);
        if (sysConfig == null || StringUtils.isEmpty(sysConfig.getConfigContent())) {
            logInfo("运输围栏到车包裹到达消息场地类型白名单过滤，未配置场地类型白名单！");
            return false;
        }
        AutoInspectionSiteTypeConf siteTypeConf = JsonHelper.fromJson(sysConfig.getConfigContent(), AutoInspectionSiteTypeConf.class);
        if (siteTypeConf == null) {
            logInfo("运输围栏到车未获取到配置场地类型白名单！");
            return false;
        }
        if (!isSiteTypeValid(siteTypeConf, baseSite)) {
            logInfo("运输围栏到车包裹到达消息场地类型过滤，到达场地{}，mqBody={},site={}", mqBody.getEndNodeCode(), JsonHelper.toJson(mqBody), JsonHelper.toJson(baseSite));
            return false;
        }
        return true;
    }

    public static boolean isSiteTypeValid(AutoInspectionSiteTypeConf siteTypeConf, BaseStaffSiteOrgDto baseSite) {
        return isTypeListValid(siteTypeConf.getSortTypeList(), baseSite.getSortType()) ||
                isTypeListValid(siteTypeConf.getSortSubTypeList(), baseSite.getSortSubType()) ||
                isTypeListValid(siteTypeConf.getSortThirdTypeList(), baseSite.getSortThirdType());
    }

    public static boolean isTypeListValid(List<Integer> typeList, Integer siteType) {
        return CollectionUtils.isNotEmpty(typeList) && typeList.contains(siteType);
    }
}
