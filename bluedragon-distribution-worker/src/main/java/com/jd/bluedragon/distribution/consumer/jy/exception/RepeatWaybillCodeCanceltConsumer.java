package com.jd.bluedragon.distribution.consumer.jy.exception;

import IceInternal.Ex;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWaybillDiffService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 重复运单取消拦截处理，临时处理
 */
@Service("repeatWaybillCodeCanceltConsumer")
public class RepeatWaybillCodeCanceltConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RepeatWaybillCodeCanceltConsumer.class);


    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private ReprintRecordService reprintRecordService;

    @Autowired
    @Qualifier("repeatWaybillcodeCancelProducer")
    private DefaultJMQProducer repeatWaybillcodeCancelProducer;

    @Autowired
    private AbnormalWaybillDiffService abnormalWaybillDiffService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.BASE.RepeatWaybillCodeCanceltConsumer.consume", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            if (StringHelper.isEmpty(message.getText())) {
                logger.warn("重复运单取消拦截 consume --> 消息为空");
                return;
            }
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("重复运单取消拦截 -->消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            if(uccPropertyConfiguration.isPrintCompeteUpdateCancel()){
                logger.warn("重复运单取消拦截开关已关闭");
                return;
            }

            RePrintRecordMq rePrintRecordMq = JsonHelper.fromJson(message.getText(), RePrintRecordMq.class);

            if(StringUtils.isEmpty(rePrintRecordMq.getWaybillCode())){
                logger.warn("重复运单取消拦截运单号为空{}", rePrintRecordMq.getWaybillCode());
                return;
            }
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(rePrintRecordMq.getWaybillCode());
            if(waybill == null ){
                logger.error("重复运单取消拦截没有查询到运单信息WaybillCode[{}]",rePrintRecordMq.getWaybillCode());
                return;
            }
            if(waybill.getGoodNumber() == null){
                logger.error("重复运单取消拦截查询包裹数量为空WaybillCode[{}]GoodNumber[{}]",rePrintRecordMq.getWaybillCode(),waybill.getGoodNumber());
                return;
            }
            AbnormalWaybillDiff abnormalWaybillDiff = new AbnormalWaybillDiff();
            //新单号，只判断新单号是否存在，后续根据新单号所有包裹是否补打在取消拦截记录
            abnormalWaybillDiff.setWaybillCodeC(rePrintRecordMq.getWaybillCode());
            List<AbnormalWaybillDiff> abnormalWaybillDiffs = abnormalWaybillDiffService.query(abnormalWaybillDiff);
            if(CollectionUtils.isEmpty(abnormalWaybillDiffs) || StringUtils.isBlank(abnormalWaybillDiffs.get(0).getWaybillCodeE())){
                logger.error("重复运单取消拦截查询运单异常表为空WaybillCode[{}]",rePrintRecordMq.getWaybillCode());
                return;
            }
            if(uccPropertyConfiguration.isPrintCompeteAllPackageUpdateCancel()){
                int alreadyPringGoodNumber = reprintRecordService.selectCountByBarCode(rePrintRecordMq.getWaybillCode());
                if(waybill.getGoodNumber() != 1 && alreadyPringGoodNumber < waybill.getGoodNumber() ){
                    logger.error("重复运单取消拦截包裹未完全补打WaybillCode[{}]GoodNumber[{}]alreadyPringGoodNumber[{}]",rePrintRecordMq.getWaybillCode(),waybill.getGoodNumber(),alreadyPringGoodNumber);
                    throw new RuntimeException("重复运单取消拦截包裹未完全补打");
                }
            }
            logger.warn("重复运单取消拦截-补打运单号[{}]，包裹号[{}],取消拦截运单号[{}]",rePrintRecordMq.getWaybillCode(),rePrintRecordMq.getPackageCode(),abnormalWaybillDiffs.get(0).getWaybillCodeE());
            waybillCancelService.updateByWaybillCodeInterceptType99(abnormalWaybillDiffs.get(0).getWaybillCodeE());
            Map<String,Object> msgBody= Maps.newHashMap();
            msgBody.put("waybillCode",abnormalWaybillDiffs.get(0).getWaybillCodeE());
            repeatWaybillcodeCancelProducer.sendOnFailPersistent(abnormalWaybillDiffs.get(0).getWaybillCodeE(),JsonHelper.toJson(msgBody));
        }catch (Exception e){
            Profiler.functionError(callerInfo);
            logger.error("重复运单取消拦截报错[{}]",message.getText(),e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

}

