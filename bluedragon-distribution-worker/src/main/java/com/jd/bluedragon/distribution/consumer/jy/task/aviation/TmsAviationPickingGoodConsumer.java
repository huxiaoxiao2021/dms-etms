package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.AirTransportBillDto;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.TmsAviationPickingGoodMqBody;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskExtendInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.template.AviationPickingGoodTaskInit;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息文档： https://cf.jd.com/pages/viewpage.action?pageId=1421890482
 * 对接研发：zhangyaqiang6
 * 对接产品：zhujian6
 * 我方产品：zhangshuo37
 *
 * 消费规则：tplBillCode 做流水号
 * （1） 重复tplBillCode时，历史任务清除，重新生成任务
 * （2） 实际降落时间非空后，在收到同tplBillCode的消息，视为无效数据
 * （3） 业务系统处理，实际降落时间之前只允许展示任务，不允许实操， 实际降落时间到达之后，才开始做实操处理
 *
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service
public class TmsAviationPickingGoodConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(TmsAviationPickingGoodConsumer.class);

    private static final String DEFAULT_VALUE_1 = "1";

    @Autowired
    private AviationPickingGoodTaskInit aviationPickingGoodTask;
    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;



    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsAviationPickingGoodConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("TmsAviationPickingGoodConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("TmsAviationPickingGoodConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsAviationPickingGoodMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsAviationPickingGoodMqBody.class);
        if(Objects.isNull(mqBody)){
            log.error("TmsAviationPickingGoodConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            return;
        }
        if(log.isInfoEnabled()){
            log.info("航空提货计划消费开始，mqBody={}", message.getText());
        }
        if(!this.lockPickingGoodTplBillCode(mqBody.getTplBillCode())) {
            log.warn("没有获取到锁，重试处理, msg={}", message.getText());
            throw new JyBizException(String.format("航空提货计划主运单号获取锁失败,businessId：%s|主运单号：%s", message.getBusinessId(), mqBody.getTplBillCode()));
        }
        try{
            if(!filterHistoryConsume(mqBody)) {
                return;
            }
            PickingGoodTaskInitDto initDto = this.convertPickingGoodTaskInitDto(mqBody);
            aviationPickingGoodTask.initTaskTemplate(initDto);
        }catch (JyBizException ex) {
            this.unlockPickingGoodTplBillCode(mqBody.getTplBillCode());
            log.error("航空提货计划消费失败,businessId={},errMsg={}, mqBody={}", message.getBusinessId(), ex.getMessage(), message.getText());
            throw new JyBizException(String.format("航空提货计划消费失败,businessId：%s", message.getBusinessId()));
        }catch (Exception ex) {
            this.unlockPickingGoodTplBillCode(mqBody.getTplBillCode());
            log.error("航空提货计划消费异常,businessId={},errMsg={}, mqBody={}", message.getBusinessId(), ex.getMessage(), message.getText(), ex);
            throw new JyBizException(String.format("航空提货计划消费异常,businessId：%s", message.getBusinessId()));
        }

    }

    private boolean filterHistoryConsume(TmsAviationPickingGoodMqBody mqBody) {
        Long lastTime = this.getCacheValuePickingGoodTplBillCode(mqBody.getTplBillCode());
        if(Objects.isNull(lastTime)) {
            this.saveCachePickingGoodTplBillCode(mqBody.getTplBillCode(), mqBody.getOperateTime().getTime());
        }else {
            if(mqBody.getOperateTime().getTime() < lastTime) {
                if(log.isInfoEnabled()) {
                    log.info("航空提货计划生成提货任务消费，历史消息消费，不作处理，msg={}", JsonHelper.toJson(mqBody));
                }
                return false;
            }
        }
        return true;
    }

    private PickingGoodTaskInitDto convertPickingGoodTaskInitDto(TmsAviationPickingGoodMqBody mqBody) {
        PickingGoodTaskInitDto initDto = new PickingGoodTaskInitDto();
        initDto.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        initDto.setBusinessNumber(mqBody.getTplBillCode());
        initDto.setServiceNumber(mqBody.getFlightNumber());
        initDto.setBeginNodeCode(mqBody.getBeginNodeCode());
        initDto.setBeginNodeName(mqBody.getBeginNodeName());
        initDto.setEndNodeCode(mqBody.getEndNodeCode());
        initDto.setEndNodeName(mqBody.getEndNodeName());
        initDto.setNodePlanStartTime(mqBody.getPlanTakeOffTime());
        initDto.setNodePlanArriveTime(mqBody.getPlanTouchDownTime());
        initDto.setNodeRealStartTime(mqBody.getRealTakeOffTime());
        initDto.setNodeRealArriveTime(mqBody.getRealTouchDownTime());
        initDto.setOperateNodeType(mqBody.getOperateType());
        initDto.setCargoNumber(mqBody.getDepartCargoAmount());
        initDto.setCargoWeight(mqBody.getDepartCargoRealWeight());
        initDto.setCreateUserErp(Constants.SYS_NAME);
        initDto.setCreateUserName(Constants.SYS_NAME);
        initDto.setOperateTime(mqBody.getOperateTime().getTime());
        List<AirTransportBillDto> airTransportBillDtoList = mqBody.getTransbillList();
        List<PickingGoodTaskExtendInitDto> extendInitDtoList = new ArrayList<>();
        airTransportBillDtoList.forEach(o -> {
            PickingGoodTaskExtendInitDto dto = new PickingGoodTaskExtendInitDto();
            dto.setBatchCode(o.getBatchCode());
            dto.setSealCarCode(o.getSealCarCode());
            dto.setStartSiteCode(o.getBeginNodeCode());
            dto.setStartSiteName(o.getBeginNodeName());
            dto.setEndSiteCode(o.getEndNodeCode());
            dto.setEndSiteName(o.getEndNodeName());
            dto.setTransportCode(o.getTransportCode());
            extendInitDtoList.add(dto);
        });
        initDto.setExtendInitDtoList(extendInitDtoList);
        return initDto;
    }


    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsAviationPickingGoodMqBody mqBody) {
        if(StringUtils.isBlank(mqBody.getTplBillCode())) {
            log.error("tplBillCode为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getEndNodeCode()) || StringUtils.isBlank(mqBody.getEndNodeName()) ) {
            log.error("提货机场编码或名称为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            log.error("operateTime为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(CollectionUtils.isEmpty(mqBody.getTransbillList())) {
            log.error("批次信息为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }else {
            //消息完整性保障
            for(AirTransportBillDto dto : mqBody.getTransbillList()) {
                if(StringUtils.isBlank(dto.getBatchCode()) || StringUtils.isBlank(dto.getSealCarCode())) {
                    log.error("批次信息中批次号或者封车编码为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
                    return false;
                }
                if(StringUtils.isBlank(dto.getEndNodeCode())) {
                    log.error("批次信息中批次提货场地编码为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 防重锁:航空主运单号
     */
    private boolean lockPickingGoodTplBillCode(String tplBillCode) {
        String lockKey = this.getLockKeyPickingGoodTplBillCode(tplBillCode);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, 5, TimeUnit.MINUTES);
    }
    private void unlockPickingGoodTplBillCode(String tplBillCode) {
        String lockKey = this.getLockKeyPickingGoodTplBillCode(tplBillCode);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyPickingGoodTplBillCode(String tplBillCode) {
        return String.format("lock:aviation:picking:good:plan:%s", tplBillCode);
    }

    /**
     * 同一个tplBillCode，缓存保证mq消费顺序
     * @param tplBillCode
     * @return
     */
    private void saveCachePickingGoodTplBillCode(String tplBillCode, Long time) {
        String cacheKey = this.getCacheKeyPickingGoodTplBillCode(tplBillCode);
        redisClientOfJy.setEx(cacheKey, time.toString(), 30, TimeUnit.HOURS);
    }
    private Long getCacheValuePickingGoodTplBillCode(String tplBillCode) {
        String cacheKey = this.getCacheKeyPickingGoodTplBillCode(tplBillCode);
        String value = redisClientOfJy.get(cacheKey);
        if(StringUtils.isNotBlank(value)) {
            return Long.valueOf(value);
        }
        return null;
    }
    private String getCacheKeyPickingGoodTplBillCode(String tplBillCode) {
        return String.format("cache:aviation:picking:good:plan:%s", tplBillCode);
    }

}
