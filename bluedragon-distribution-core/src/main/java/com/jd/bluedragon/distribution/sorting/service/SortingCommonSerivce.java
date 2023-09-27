package com.jd.bluedragon.distribution.sorting.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefundBlockerComplete;
import com.jd.bluedragon.distribution.fastRefund.service.FastRefundService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.material.service.CycleMaterialNoticeService;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * 分拣业务抽象类
 */
public abstract class SortingCommonSerivce {

    protected Logger log = LoggerFactory.getLogger(SortingCommonSerivce.class);

    public final static String TASK_SORTING_FINGERPRINT_SORTING_5S = "TASK_SORTING_FP_5S_"; //5前缀

    public static final int TASK_1200_EX_TIME_5_S = 5;

    @Autowired
    @Qualifier("sortingService")
    private SortingService sortingService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    protected InspectionDao inspectionDao;

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

    @Qualifier("blockerComOrbrefundRqMQ")
    @Autowired
    private DefaultJMQProducer blockerComOrbrefundRqMQ;

    @Autowired
    protected InspectionECDao inspectionECDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    WaybillService waybillService;

    @Autowired
    FastRefundService fastRefundService;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Autowired
    private LogEngine logEngine;
    
    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private CycleMaterialNoticeService cycleMaterialNoticeService;
    
    @Autowired
    private JyOperateFlowService jyOperateFlowService;

    public abstract boolean doSorting(SortingVO sorting);

    /**
     * 执行方法
     * @param sorting
     */
    public boolean execute(SortingVO sorting){
        if (log.isInfoEnabled()) {
            log.info("分拣任务执行:this={},sorting={}", this.getClass().getName(), JSON.toJSONString(sorting));
        }
        String keyword = this.getClass().getSimpleName();
        CallerInfo sendMonitor = ProfilerHelper.registerInfo("DMSWORKER."+keyword+".execute",
                Constants.UMP_APP_NAME_DMSWORKER);
        String fingerPrintKey = "";
        try {
            fingerPrintKey = TASK_SORTING_FINGERPRINT_SORTING_5S + sorting.getCreateSiteCode() +"|"+ sorting.getBoxCode()
                    +"|"+sorting.getWaybillCode()+"|"+sorting.getPackageCode()+"|"+ sorting.getPageNo()+"|"+sorting.getPageSize();
            if(check(sorting,fingerPrintKey)){
                before(sorting);
                if(doSorting(sorting)){
                    after(sorting);
                }else{
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            log.error("分拣任务执行异常:{}|{}",sorting.getWaybillCode(), sorting.getPackageCode(),e);
            Profiler.functionError(sendMonitor);
            return false;
        }finally {
            cacheService.del(fingerPrintKey);
            Profiler.registerInfoEnd(sendMonitor);
        }

    }

    /**
     * 检查
     * 去重检查
     * 只是为了减少并发 ，分拣可重复执行
     * @param sorting
     * @return
     */
    private boolean check(SortingVO sorting,String fingerPrintKey){

        try{
            //判断是否重复分拣, 5秒内如果同操作场地、同目的地、同扫描号码即可判断为重复操作。
            // 立刻置失败，转到下一次执行。只使用key存不存在做防重
            Boolean isSucdess = cacheService.setNx(fingerPrintKey, "1", TASK_1200_EX_TIME_5_S, TimeUnit.SECONDS);
            if(!isSucdess){//说明有重复任务
                this.log.warn("分拣任务重复：{}", JsonHelper.toJson(sorting));
                return false;
            }
        }catch(Exception e){
            this.log.error("获得分拣任务指纹失败:{}", JsonHelper.toJson(sorting), e);
        }
        return true;
    }

    private void before(SortingVO  sorting) {
        //初始化运单数据
        String waybillCode = sorting.getWaybillCode();
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(false);

        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        if(baseEntity!=null && baseEntity.getData()!=null){
            if(sorting.isWaybillSplitSorting()){
                //初始化包裹数据
                BaseEntity<List<DeliveryPackageD>> pageLists = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode,sorting.getPageNo(),sorting.getPageSize());
                if(pageLists != null && pageLists.getData() != null ){
                    baseEntity.getData().setPackageList(pageLists.getData());
                }

            }
            sorting.setWaybillDtoBaseEntity(baseEntity);
        }

        //初始化当前操作分拣中心
        BaseStaffSiteOrgDto createSite = null;
        try {
            createSite = this.baseMajorManager.getBaseSiteBySiteId(sorting.getCreateSiteCode());
        } catch (Exception e) {
            this.log.error("sortingServiceImpl.pushInspection.初始化当前操作分拣中心：{}",sorting.getCreateSiteCode(), e);
        }
        sorting.setCreateSite(createSite);


        //已箱号目的地为准（未定位到什么样的入口会有目的地和箱号目的地不一致的数据）
        if(StringUtils.isNotBlank(sorting.getBoxCode()) && BusinessUtil.isBoxcode(sorting.getBoxCode())){
            Box box = boxService.findBoxByCode(sorting.getBoxCode());
            if (box != null) {
                sorting.setReceiveSiteCode(box.getReceiveSiteCode());
            }
        }

    }

    private void after(SortingVO sorting) {
        if(sorting.getSortingType() != SortingVO.SORTING_TYPE_WAYBILL_SPLIT
                && sorting.getIsCancel().equals(SortingService.SORTING_CANCEL_NORMAL)){
            //非 运单转换成的分批包裹任务才执行
            sortingService.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING,"SortingCommonSerivce#after");
            //快退
            notifyBlocker(sorting);
            backwardSendMQ(sorting);
            //更新运单状态
            sortingService.addSortingAdditionalTask(sorting);

            // 分拣发送循环集包袋MQ
            pushCycleMaterialMessage(sorting);
            //发送操作流水mq
            sendSortingFlowMq(sorting);
        }


    }
    /**
     * 发送操作流水mq
     * @param sorting
     */
	private void sendSortingFlowMq(SortingVO sorting) {
	    JyOperateFlowMqData sortingFlowMq = BeanConverter.convertToJyOperateFlowMqData(sorting);
	    sortingFlowMq.setOperateBizSubType(OperateBizSubTypeEnum.SORTING.getCode());
		jyOperateFlowService.sendMq(sortingFlowMq);
	}
    private void pushCycleMaterialMessage(SortingVO sorting) {
        BoxMaterialRelationMQ mqBody = new BoxMaterialRelationMQ();
        mqBody.setBusinessType(BoxMaterialRelationEnum.SORTING.getType());
        mqBody.setSiteCode(String.valueOf(sorting.getCreateSiteCode()));
        mqBody.setBoxCode(sorting.getBoxCode());
        mqBody.setWaybillCode(Collections.singletonList(sorting.getWaybillCode()));
        mqBody.setPackageCode(Collections.singletonList(sorting.getPackageCode()));
        mqBody.setOperatorCode(sorting.getCreateUserCode());
        mqBody.setOperatorName(sorting.getCreateUser());
        mqBody.setOperatorTime(sorting.getOperateTime());

        cycleMaterialNoticeService.sendSortingMaterialMessage(mqBody);
    }



    public void notifyBlocker(SortingVO sorting) {
        long startTime=new Date().getTime();

        try {
            if (Sorting.TYPE_REVERSE.equals(sorting.getType())) {
                String wayBillCode = sorting.getWaybillCode();
                BaseEntity<BigWaybillDto> baseEntity = sorting.getWaybillDtoBaseEntity();
                if(baseEntity != null && baseEntity.getData() != null) {
                    Waybill waybill = baseEntity.getData().getWaybill();
                    if (waybill != null) {
                        String waybillsign = waybill.getWaybillSign();
                        if (waybillsign != null && waybillsign.length() > 0) {
                            if(BusinessUtil.isSick(waybill.getWaybillSign())){
                                //TODO 上线观察一段时间 可删除该log
                                this.log.warn("分拣中心逆向病单屏蔽退款100分MQ,运单号：{}", waybill.getWaybillCode());
                                return;
                            }
                        }
                        String refundMessage = this.refundMessage(sorting.getWaybillCode(),
                                DateHelper.formatDateTimeMs(sorting.getOperateTime()));
                        //bd_blocker_complete的MQ
                        this.bdBlockerCompleteMQ.send( sorting.getWaybillCode(),refundMessage);
                        this.log.info("退款100分MQ消息推送成功,运单号：{}", waybill.getWaybillCode());
                        //【逆向分拣理货】增加orbrefundRqMQ  add by lhc  2016.8.17
                        //这里需要暂时注释掉 逆向取件单不应该发送快退的mq,属于售后的范围  modified by zhanglei 20161025
                        //fastRefundService.execRefund(sorting);
                    }else{
                        log.info("{}对应的运单信息为空！",wayBillCode);
                    }
                }
            }
        } catch (Exception e) {
            this.log.error("回传退款100分逆向分拣信息失败，运单号：{}", sorting.getWaybillCode(), e);
            try{
                long endTime = new Date().getTime();

                JSONObject request=new JSONObject();
                request.put("waybillCode",sorting.getWaybillCode());

                JSONObject response=new JSONObject();
                response.put("keyword1", sorting.getWaybillCode());
                response.put("keyword2", "BLOCKER_QUEUE_DMS");
                response.put("keyword3", "");
                response.put("keyword4", sorting.getType());
                response.put("content", e.getMessage());

                BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                        .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SORTING_REVERSE_SORTING_100SCORE)
                        .methodName("SortingCommonSerivce#notifyBlocker")
                        .processTime(endTime,startTime)
                        .operateRequest(request)
                        .operateResponse(response)
                        .build();

                logEngine.addLog(businessLogProfiler);

                SystemLogUtil.log(sorting.getWaybillCode(),"BLOCKER_QUEUE_DMS","",sorting.getType(),e.getMessage(),Long.valueOf(12201));
            }catch (Exception ex){
                log.error("退款100分MQ消息推送记录日志失败", ex);
            }
        }
    }


    /**
     * 正向【分拣理货】所有逆向订单发送topic是blockerComOrbrefundRq的mq,供快退系统和拦截系统消费.
     * add by lhc
     * 2016.08.17
     * @param sorting
     */
    public void backwardSendMQ(SortingVO sorting){
        String wayBillCode = sorting.getWaybillCode();
//		String wayBillCode = "T42747129215";//VA00080450101
        // 验证运单号
        if(wayBillCode != null){
            BaseEntity<BigWaybillDto> baseEntity = sorting.getWaybillDtoBaseEntity();
            if(baseEntity != null && baseEntity.getData() != null){
                Waybill waybill = baseEntity.getData().getWaybill();
                if(waybill != null){
                    String waybillsign = waybill.getWaybillSign();
                    if(waybillsign != null && waybillsign.length()>0){
                        //waybillsign  1=T  ||  waybillsign  15=6表示逆向订单
                        if((waybill.getWaybillSign().charAt(0)=='T' || waybill.getWaybillSign().charAt(14)=='6')){
                            if(BusinessUtil.isSick(waybill.getWaybillSign())){
                                //TODO 上线观察一段时间 可删除该log
                                this.log.warn("分拣中心逆向病单屏蔽快退MQ,运单号：{}", waybill.getWaybillCode());
                                return;
                            }
                            //组装FastRefundBlockerComplete
                            FastRefundBlockerComplete frbc = toMakeFastRefundBlockerComplete(sorting);
                            String json = JsonHelper.toJson(frbc);
                            this.log.info("分拣中心逆向订单快退:运单号[{}]",wayBillCode);
                            try {
                                blockerComOrbrefundRqMQ.send(wayBillCode,json);
                            } catch (Exception e) {
                                this.log.error("分拣中心逆向订单快退MQ失败[{}]",json , e);
                            }
                        }else{
                            log.info("{}对应的订单为非逆向订单！",waybillsign);
                        }
                    }
                }else{
                    log.info("{}对应的运单信息为空！",wayBillCode);
                }
            }
        }
    }


    private FastRefundBlockerComplete toMakeFastRefundBlockerComplete(Sorting sorting){
        FastRefundBlockerComplete frbc = new FastRefundBlockerComplete();
        //新运单号获取老运单号的所有信息  参数返单号
        try{
            BaseEntity<Waybill> wayBillOld = waybillQueryManager.getWaybillByReturnWaybillCode(sorting.getWaybillCode());
            if(wayBillOld.getData() != null){
                String vendorId = wayBillOld.getData().getVendorId();
                if(vendorId == null || "".equals(vendorId)){
                    frbc.setOrderId("0");//没有订单号的外单,是非京东平台上下的订单
                }else{
                    frbc.setOrderId(vendorId);
                }
            }else{
                frbc.setOrderId("0");
            }
        }catch(Exception e){
            this.log.error("发送blockerComOrbrefundRq的MQ时新运单号获取老运单号失败,waybillcode:[{}]",sorting.getWaybillCode(), e);
        }
        frbc.setWaybillcode(sorting.getWaybillCode());
        frbc.setApplyReason("分拣中心快速退款");
        frbc.setApplyDate(sorting.getOperateTime().getTime());
        frbc.setSystemId(87);//blockerComOrbrefundRq的systemId设定为87
        frbc.setReqErp(String.valueOf(sorting.getCreateUserCode()));
        frbc.setReqName(sorting.getCreateUser());
        frbc.setOrderType(sorting.getType());
        frbc.setMessageType("BLOCKER_QUEUE_DMS_REVERSE_PRINT");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        frbc.setOperatTime(dateFormat.format(sorting.getOperateTime()));
        frbc.setSys("ql.dms");

        return frbc;
    }


    public String refundMessage(String waybillCode, String operateTime) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        message.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        message.append("<OrderId>" + waybillCode + "</OrderId>");
        message.append("<OrderType>20</OrderType>");
        message.append("<MessageType>BLOCKER_QUEUE_DMS</MessageType>");
        message.append("<OperatTime>" + operateTime + "</OperatTime>");
        message.append("</OrderTaskInfo>");

        return message.toString();
    }

    /**
     * 记录业务日志
     *
     * @param sorting
     */
    private void addBusinessLog(Sorting sorting,Task task) {
        //写入自定义日志
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_B_INSPECTION);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_INSPECTION);
        businessLogProfiler.setOperateRequest(JsonHelper.toJson(sorting));
        businessLogProfiler.setOperateResponse(JsonHelper.toJson(task));
        businessLogProfiler.setTimeStamp(System.currentTimeMillis());
        BusinessLogWriter.writeLog(businessLogProfiler);
    }

    SortingService getSortingService(){
        return sortingService;
    }



}
