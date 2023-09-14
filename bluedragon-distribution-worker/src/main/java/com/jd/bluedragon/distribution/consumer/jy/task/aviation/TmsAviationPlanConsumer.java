package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.AirTypeEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.TmsAviationPlanDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyBizIgnoreException;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanCacheService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.enums.JySendTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.api.resource.req.AirlineReq;
import com.jd.etms.api.resource.resp.AirLineResp;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/8/7 10:52
 * @Description 运输航空计划消费
 */
@Service("tmsAviationPlanConsumer")
public class TmsAviationPlanConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(TmsAviationPlanConsumer.class);

    public static final String CACHE_DEFAULT = "1";

    public static final String JY_LOCK_AVIATION_PLAN_SEND_KEY = "jy:lock:aviationPlan:send:%s";
    public static final int JY_LOCK_AVIATION_PLAN_SEND_KEY_TIMEOUT_SECONDS = 120;

    public static final String JY_CACHE_AVIATION_PLAN_CANCEL = "jy:cache:aviationPlan:bornCancel:%s";


    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    private SendVehicleTransactionManager transactionManager;
    @Autowired
    private JyBizTaskSendAviationPlanCacheService aviationPlanCacheService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.tmsAviationPlanConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("tmsAviationPlanConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("tmsAviationPlanConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsAviationPlanDto mqBody = JsonHelper.fromJson(message.getText(), TmsAviationPlanDto.class);
        if(mqBody == null){
            log.error("tmsAviationPlanConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理tmsAviationPlanConsumer开始，内容{}",message.getText());
        }

        mqBody.setBusinessId(message.getBusinessId());

        //无效数据
        if(invalidDataFilter(mqBody)) {
            log.warn("无效航空计划丢弃：{}", JsonHelper.toJson(mqBody));
            return;
        }

        this.generateAviationSendPlanTask(mqBody);
        if (log.isInfoEnabled()) {
            log.info("tmsAviationPlanConsumer航空计划生成航空发货任务成功，businessId={}，订舱号={}", message.getBusinessId(), mqBody.getBookingCode());
        }
    }


    /**
     * 生成航空发货任务
     * @param mqBody
     * @return
     */
    private void generateAviationSendPlanTask(TmsAviationPlanDto mqBody) {
        //订舱号唯一：并发锁处理
        if(!this.lockGenerateSendAviationPlan(mqBody.getBookingCode())) {
            String warnMsg = String.format("（并发锁需要重试）航空计划发货任务生成-订舱号%s正在处理中!", mqBody.getBookingCode());
            log.warn(warnMsg, JsonHelper.toJson(mqBody));
            throw new JyBizException(warnMsg);
        }
        CallerInfo info = Profiler.registerInfo("DMS.BASE.TmsAviationPlanConsumer.generateAviationSendPlanTask", false, true);

        try{
            Long bornCancelTime = this.getCacheBornCancelTime(mqBody.getBookingCode());
            if(!Objects.isNull(bornCancelTime)) {
                log.warn("首次消费【{}】时订舱量为0该发货计划已经取消，不在消费,mqBody={}", bornCancelTime, JsonHelper.toJson(mqBody));
                return;
            }

            BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getStartNodeCode());
            if (Objects.isNull(currSite)) {
                log.warn("航空计划发货任务创建：基础资料未查到始发分拣【{}】场地信息：mqBody={}" , mqBody.getStartNodeCode(), JsonHelper.toJson(mqBody));
                throw new JyBizException();
            }
            JyBizTaskSendVehicleEntity existSendTaskMain = jyBizTaskSendVehicleService.findByBookingCode(mqBody.getBookingCode(), Long.valueOf(currSite.getSiteCode()));
            if(Objects.isNull(existSendTaskMain)) {
                //0订舱量丢弃&记录缓存
                if(!NumberHelper.gt0(mqBody.getBookingWeight())) {
                    this.saveCacheBornCancel(mqBody.getBookingCode(), mqBody.getOperateTime());
                    log.warn("航空计划订舱号【{}】首次消费订舱量0，直接丢弃，mqBody={}", JsonHelper.toJsonMs(mqBody));
                    return;
                }
                //插入
                if(log.isInfoEnabled()) {
                    log.info("航空计划订舱号【{}】发货任务插入开始，mqBody={}", JsonHelper.toJsonMs(mqBody));
                }
                JyBizTaskSendAviationPlanEntity aviationPlanEntity = this.generateAviationPlanEntity(mqBody);
                JyBizTaskSendVehicleEntity sendVehicleEntity = this.aviationPlanConvertSendTask(aviationPlanEntity);
                JyBizTaskSendVehicleDetailEntity sendVehicleDetailEntity = this.aviationPlanConvertSendTaskDetail(aviationPlanEntity);
                transactionManager.saveAviationPlanAndTaskSendAndDetail(aviationPlanEntity, sendVehicleEntity, sendVehicleDetailEntity);

            }else {
                //修改
                JyBizTaskSendAviationPlanEntity queryEntity = jyBizTaskSendAviationPlanService.findByBizId(existSendTaskMain.getBizId());
                if(!Objects.isNull(queryEntity)) {
                    if(mqBody.getBookingWeight().equals(queryEntity.getBookingWeight())) {
                        log.warn("bizId={},订舱号={},航空计划订舱量未变化，不做更新，businessId={}", existSendTaskMain.getBizId(), mqBody.getBookingCode(), mqBody.getBusinessId());
                        return;
                    }
                    //同一订舱号约束变更仅变更订舱量，其他字段变更是生成新的订舱号任务，订舱量为0标识任务中途取消 2023年08月09日
                    if(!NumberHelper.gt0(queryEntity.getBookingWeight()) || (!Objects.isNull(queryEntity.getIntercept())) && queryEntity.getIntercept().equals(1)) {
                        log.warn("bizId={},订舱号={},该航空计划已经0订舱量取消处理，不在更新直接丢弃消息。消息体={}", existSendTaskMain.getBizId(), mqBody.getBookingCode(), JsonHelper.toJson(mqBody));
                        return;
                    }
                    JyBizTaskSendAviationPlanEntity updateAviationPlan = new JyBizTaskSendAviationPlanEntity();
                    updateAviationPlan.setBizId(existSendTaskMain.getBizId());
                    Date curTime = new Date();
                    updateAviationPlan.setUpdateTime(curTime);
                    updateAviationPlan.setUpdateUserErp(Constants.SYS_NAME);
                    updateAviationPlan.setUpdateUserName(Constants.SYS_NAME);
                    boolean interceptCancel = false;
                    updateAviationPlan.setBookingWeight(mqBody.getBookingWeight());
                    if(!NumberHelper.gt0(mqBody.getBookingWeight())) {
                        interceptCancel = true;
                        updateAviationPlan.setIntercept(1);
                        updateAviationPlan.setInterceptTime(curTime);
                    }
                    jyBizTaskSendAviationPlanService.updateByBizId(updateAviationPlan);
                    if(interceptCancel){
                        //取消缓存
                        aviationPlanCacheService.saveCacheAviationPlanCancel(existSendTaskMain.getBizId());
                    }
                }else {
                    //理论上发货主表不为空这里一定不为空, log记录
                    log.error("航空发货数据异常，发货主表数据航空任务和航空发货计划数据差异了，发货任务bizId={},mqBody={}", existSendTaskMain.getBizId(), JsonHelper.toJson(mqBody));
                }
            }
        }catch (JyBizIgnoreException ignoreEx){
            log.warn("消费运输航空计划生成航空发货任务，查询路由系统任务下一流向为空或者服务不可用，丢弃该消息，订舱号={},mqBody={}", mqBody.getBookingCode(), JsonHelper.toJson(mqBody), ignoreEx);
            return;
        }catch (Exception e) {
            Profiler.functionError(info);
            log.error("运输航空计划消费生成航空发货任务出错，errMsg={},mqBody={}", e.getMessage(), JsonHelper.toJson(mqBody), e);
            throw new JyBizException("运输航空计划消费生成航空发货任务出错" + mqBody.getBookingCode());
        }finally {
            //释放锁
            unlockGenerateSendAviationPlan(mqBody.getBookingCode());
            Profiler.registerInfoEnd(info);
        }
    }


    private JyBizTaskSendAviationPlanEntity generateAviationPlanEntity(TmsAviationPlanDto mqBody) {
        JyBizTaskSendAviationPlanEntity entity = new JyBizTaskSendAviationPlanEntity();
//        entity.setBizId(tmsTransWorkItemOperateConsumer.genMainTaskBizId());
        entity.setBizId(jyBizTaskSendVehicleService.genMainTaskBizId());
        entity.setBookingCode(mqBody.getBookingCode());
        entity.setStartSiteCode(mqBody.getStartNodeCode());
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getStartNodeCode());
        if (Objects.isNull(currSite)) {
            log.warn("航空计划发货任务创建：基础资料未查到始发分拣【{}】场地信息：mqBody={}" , mqBody.getStartNodeCode(), JsonHelper.toJson(mqBody));
            throw new JyBizException();
        }
        entity.setStartSiteId(currSite.getSiteCode());
        entity.setStartSiteName(mqBody.getStartNodeName());
        entity.setFlightNumber(mqBody.getFlightNumber());
        entity.setTakeOffTime(mqBody.getTakeOffTime());
        entity.setTouchDownTime(mqBody.getTouchDownTime());
        entity.setAirCompanyCode(mqBody.getAirCompanyCode());
        entity.setAirCompanyName(mqBody.getAirCompanyName());
        entity.setBeginNodeCode(mqBody.getBeginNodeCode());
        entity.setBeginNodeName(mqBody.getBeginNodeName());
        entity.setEndNodeCode(mqBody.getEndNodeCode());
        entity.setEndNodeName(mqBody.getEndNodeName());
        entity.setCarrierCode(mqBody.getCarrierCode());
        entity.setCarrierName(mqBody.getCarrierName());
        entity.setBookingWeight(mqBody.getBookingWeight());
        entity.setCargoType(mqBody.getCargoType());
        entity.setAirType(mqBody.getAirType());
        entity.setCreateUserErp(Constants.SYS_NAME);
        entity.setCreateUserName(Constants.SYS_NAME);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        entity.setYn(Constants.YN_YES);
        entity.setIntercept(0);
        entity.setTaskStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());

        this.fillNextSiteInfo(entity);
        return entity;
    }

    //该接口可以决定航空计划始发有效
    private void fillNextSiteInfo(JyBizTaskSendAviationPlanEntity entity) {
        //测试代码
        entity.setNextSiteCode("010F016");
        entity.setNextSiteId(40240);
        entity.setNextSiteName("北京通州分拣中心cs");

        String nextSiteCode = null;
        Integer nextSiteId = null;
        String nextSiteName = null;
        try{
            AirlineReq airlineReq = new AirlineReq();
            if(AirTypeEnum.AIR_TYPE_BULK.getCode().equals(entity.getAirType())) {
                airlineReq.setAirTransportType(2);
            }else {
                airlineReq.setAirTransportType(1);//todo zcf 确认1是什么类型
            }
            airlineReq.setEndAirportCode(entity.getEndNodeCode());
            airlineReq.setStartAirportCode(entity.getBeginNodeCode());
            airlineReq.setValidDate(new Date());
            InvokeResult<List<AirLineResp>> invokeResult = vrsRouteTransferRelationManager.queryAirLineByAirLineReq(airlineReq);
            //可能流向多个
            Set<String> nextSiteSet = new HashSet<>();
            if(invokeResult.codeSuccess() && CollectionUtils.isNotEmpty(invokeResult.getData())) {
                invokeResult.getData().forEach(po -> {
                    if(StringUtils.isNotBlank(po.getEndNodeCode())) {
                        nextSiteSet.add(po.getEndNodeCode());
                    }
                });
            }
            if(CollectionUtils.isNotEmpty(nextSiteSet)) {
                for(String siteCode : nextSiteSet) {
                    BaseStaffSiteOrgDto nextSite ;
                    try{
                        nextSite = baseMajorManager.getBaseSiteByDmsCode(siteCode);
                    }catch (Exception e) {
                        log.error("根据场地分拣编码【{}】获取场地异常, errMsg={}", siteCode, e.getMessage(), e);
                        continue;
                    }
                    if(Objects.isNull(nextSite)) {
                        continue;
                    }
                    nextSiteCode = siteCode;
                    nextSiteId = nextSite.getSiteCode();
                    nextSiteName = nextSite.getSiteName();
                    break;
                }
            }
            if(Objects.isNull(nextSiteId)) {
                throw new JyBizIgnoreException("航空计划生成发货任务没有找到流向，无效计划");
            }
            if(nextSiteSet.size() > 1) {
                log.warn("航空发货任务【订舱号：{}】查找路由运力找到多个目的地{}，取任一个【{}|{}】", entity.getBookingWeight(), JsonHelper.toJson(nextSiteSet), nextSiteId, nextSiteName);
            }
        }catch (Exception ignoreEx) {
            throw new JyBizIgnoreException(ignoreEx.getMessage());
        }
        entity.setNextSiteCode(nextSiteCode);
        entity.setNextSiteId(nextSiteId);
        entity.setNextSiteName(nextSiteName);
    }


    private JyBizTaskSendVehicleEntity aviationPlanConvertSendTask(JyBizTaskSendAviationPlanEntity aviationPlanEntity) {
        JyBizTaskSendVehicleEntity sendVehicleEntity = new JyBizTaskSendVehicleEntity();
        sendVehicleEntity.setBizId(aviationPlanEntity.getBizId());
        sendVehicleEntity.setStartSiteId(aviationPlanEntity.getStartSiteId().longValue());
        sendVehicleEntity.setVehicleStatus(aviationPlanEntity.getTaskStatus());
        sendVehicleEntity.setCreateUserErp(aviationPlanEntity.getCreateUserErp());
        sendVehicleEntity.setCreateUserName(aviationPlanEntity.getCreateUserName());
        sendVehicleEntity.setCreateTime(aviationPlanEntity.getCreateTime());
        sendVehicleEntity.setUpdateTime(aviationPlanEntity.getUpdateTime());
        sendVehicleEntity.setBookingCode(aviationPlanEntity.getBookingCode());
        sendVehicleEntity.setTaskType(JySendTaskTypeEnum.AVIATION.getCode());
        sendVehicleEntity.setTransWorkCode(StringUtils.EMPTY);
        return sendVehicleEntity;
    }

    private JyBizTaskSendVehicleDetailEntity aviationPlanConvertSendTaskDetail(JyBizTaskSendAviationPlanEntity aviationPlanEntity) {
        JyBizTaskSendVehicleDetailEntity taskSendVehicleDetailEntity = new JyBizTaskSendVehicleDetailEntity();
        taskSendVehicleDetailEntity.setSendVehicleBizId(aviationPlanEntity.getBizId());
        taskSendVehicleDetailEntity.setBizId(aviationPlanEntity.getBookingCode());
        taskSendVehicleDetailEntity.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode());
        taskSendVehicleDetailEntity.setStartSiteId(aviationPlanEntity.getStartSiteId().longValue());
        taskSendVehicleDetailEntity.setStartSiteName(aviationPlanEntity.getStartSiteName());
        taskSendVehicleDetailEntity.setEndSiteId(aviationPlanEntity.getNextSiteId().longValue());
        taskSendVehicleDetailEntity.setEndSiteName(aviationPlanEntity.getNextSiteName());
        taskSendVehicleDetailEntity.setCreateUserErp(aviationPlanEntity.getCreateUserErp());
        taskSendVehicleDetailEntity.setCreateUserName(aviationPlanEntity.getCreateUserName());
        taskSendVehicleDetailEntity.setCreateTime(aviationPlanEntity.getCreateTime());
        taskSendVehicleDetailEntity.setUpdateTime(aviationPlanEntity.getUpdateTime());
        return taskSendVehicleDetailEntity;
    }


    /**
     * 过滤无效数据  返回true 无效
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsAviationPlanDto mqBody) {
        if(StringUtils.isBlank(mqBody.getBookingCode())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：订舱号为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(StringUtils.isBlank(mqBody.getStartNodeCode())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：操作场地StartNodeCode为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(StringUtils.isBlank(mqBody.getFlightNumber())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：航班号flightNumber为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(mqBody.getTakeOffTime())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：七分时间takeOffTime为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(mqBody.getTouchDownTime())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：降落时间touchDownTime为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(StringUtils.isBlank(mqBody.getBeginNodeCode()) || StringUtils.isBlank(mqBody.getBeginNodeName())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：始发机场信息beginNodeCode|beginNodeName为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(StringUtils.isBlank(mqBody.getEndNodeCode()) || StringUtils.isBlank(mqBody.getEndNodeName())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：目的机场信息endNodeCode|endNodeName为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(mqBody.getBookingWeight())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：订舱量bookingWeigh为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(mqBody.getAirType())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：航空类型airType为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            log.warn("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：下发消息时间operateTime为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        return false;
    }


    //订舱号并发锁获取
    private boolean lockGenerateSendAviationPlan(String bookingCode) {
        return redisClientOfJy.set(
                this.getLockKeyGenerateSendAviationPlan(bookingCode),
                TmsAviationPlanConsumer.CACHE_DEFAULT,
                TmsAviationPlanConsumer.JY_LOCK_AVIATION_PLAN_SEND_KEY_TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                false);
    }
    //订舱号并发锁释放
    private void unlockGenerateSendAviationPlan(String bookingCode) {
        redisClientOfJy.del(this.getLockKeyGenerateSendAviationPlan(bookingCode));
    }
    //订舱号并发锁key获取
    private String getLockKeyGenerateSendAviationPlan(String bookingCode) {
        return String.format(TmsAviationPlanConsumer.JY_LOCK_AVIATION_PLAN_SEND_KEY, bookingCode);
    }



    /**
     * 订舱号生成0订舱量直接取消：  * 避免因JMQ消费乱序产生先取消后又生成新的航空计划数据
     * @param bookingCode
     * @return
     */
    private void saveCacheBornCancel(String bookingCode, Date operateTime){
        redisClientOfJy.setEx(this.getBornCancelKey(bookingCode), Long.toString(operateTime.getTime()), 24, TimeUnit.HOURS);
    }
    private Long getCacheBornCancelTime(String bookingCode) {
        String timeStamp = redisClientOfJy.get(this.getBornCancelKey(bookingCode));
        if(StringUtils.isBlank(timeStamp)) {
            return null;
        }
        return Long.valueOf(timeStamp);
    }
    private String getBornCancelKey(String bookingCode) {
        return String.format(JY_CACHE_AVIATION_PLAN_CANCEL, bookingCode);
    }


}
