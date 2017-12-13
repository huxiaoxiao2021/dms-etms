package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageDestinationConstant;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.batch.dao.BatchSendDao;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.failqueue.service.IFailQueueService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.*;
import com.jd.bluedragon.distribution.send.ws.client.dmc.DmsToTmsWebService;
import com.jd.bluedragon.distribution.send.ws.client.dmc.Result;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.*;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.erp.ws.SupportServiceInterface;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSON;
import com.jd.jmq.client.producer.MessageProducer;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Service("deliveryService")
public class DeliveryServiceImpl implements DeliveryService {


    private final Logger logger = Logger.getLogger(DeliveryServiceImpl.class);

    @Resource(name = "cityDeliveryVerification")
    private DeliveryVerification cityDeliveryVerification;

    @Autowired
    DepartureService departureService;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private ReverseSpareDao reverseSpareDao;

    @Autowired
    private BoxService boxService;

    @Autowired
    WaybillQueryApi waybillQueryApi;

    @Autowired
    private SortingService tSortingService;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private DmsToTmsWebService dmsToTmsWebService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private InspectionExceptionService inspectionExcetionService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private IFailQueueService newFailQueueService;

    @Autowired
    private TaskService tTaskService;

    @Autowired
    ReverseDeliveryService reverseDeliveryService;

    @Autowired
    private SupportServiceInterface supportProxy;

    @Autowired
    @Qualifier("batchSendDao")
    private BatchSendDao batchSendDao;

    @Autowired
    @Qualifier("forwardComputer")
    private PackageDiffrence forwardComputer;

    @Autowired
    @Qualifier("reverseComputer")
    private PackageDiffrence reverseComputer;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SiteService siteService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Resource
    @Qualifier("workerProducer")
    private MessageProducer workerProducer;

    @Qualifier("pop1MQ")
    @Autowired
    private DefaultJMQProducer pop1MQ;

    @Autowired
    @Qualifier("turnoverBoxMQ")
    private DefaultJMQProducer turnoverBoxMQ;

    @Autowired
    @Qualifier("dmsWorkSendDetailMQ")
    private DefaultJMQProducer dmsWorkSendDetailMQ;

    //added by hanjiaxing 2016.12.20
    @Autowired
    private GantryExceptionService gantryExceptionService;

    @Autowired
    private TransBillScheduleService transBillScheduleService;

    @Resource(name = "transbillMService")
    private TransbillMService transbillMService;

    //自营
    public static final Integer businessTypeONE = 10;
    //退货
    public static final Integer businessTypeTWO = 20;
    //第三方
    public static final Integer businessTypeTHR = 30;
    private static final int OPERATE_TYPE_REVERSE_SEND = 50;
    private static final int OPERATE_TYPE_FORWARD_SORTING = 1;
    private static final int OPERATE_TYPE_FORWARD_SEND = 2;
    private static final int OPERATE_TYPE_REVERSE_SORTING = 40;
    private static final int OPERATE_TYPE_CANCEL_L = 0;
    private static final int OPERATE_TYPE_CANCEL_Y = 1;
    private final Integer BATCH_NUM = 999;
    private final Integer BATCH_NUM_M = 99;

    /**
     * 原包发货[前提条件]1：箱号、原包没有发货; 2：原包调用分拣拦截验证通过; 3：批次没有发车
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     * UPDATED BY wangtingwei@jd.com
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败  4：需要用户确认
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSend", mState = {
            JProEnum.TP, JProEnum.FunctionError})
    public SendResult packageSend(SendM domain, boolean isForceSend) {
        CallerInfo temp_info1 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info1", false, true);
        SendM queryPara = new SendM();
        queryPara.setBoxCode(domain.getBoxCode());
        queryPara.setCreateSiteCode(domain.getCreateSiteCode());
        queryPara.setReceiveSiteCode(domain.getReceiveSiteCode());
        //查询箱子发货记录
        List<SendM> sendMList = this.sendMDao.selectBySendSiteCode(queryPara);/*不直接使用domain的原因，SELECT语句有[test="createUserId!=null"]等其它*/

        if (null != sendMList && sendMList.size() > 0) {
            return new SendResult(2, "箱子已经在批次" + sendMList.get(0).getSendCode() + "中发货");
        }
        Profiler.registerInfoEnd(temp_info1);

        CallerInfo temp_info2 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info2", false, true);
        if (!SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())) {//大件分拣拦截验证
            SortingCheck sortingCheck = new SortingCheck();
            sortingCheck.setReceiveSiteCode(domain.getReceiveSiteCode());
            sortingCheck.setCreateSiteCode(domain.getCreateSiteCode());
            sortingCheck.setBoxCode(domain.getBoxCode());
            sortingCheck.setPackageCode(domain.getBoxCode());
            sortingCheck.setBusinessType(domain.getSendType());
            sortingCheck.setOperateUserCode(domain.getCreateUserCode());
            sortingCheck.setOperateUserName(domain.getCreateUser());
            sortingCheck.setOperateTime(DateHelper.formatDateTime(new Date()));
            sortingCheck.setOperateType(1);
            SortingJsfResponse response = null;
            CallerInfo info1 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.callsortingcheck", false, true);
            try {
                response = jsfSortingResourceService.check(sortingCheck);
            } catch (Exception ex) {
                logger.error("调用总部VER验证JSF服务失败", ex);
                return new SendResult(DeliveryResponse.CODE_VER_CHECK_EXCEPTION, DeliveryResponse.MESSAGE_VER_CHECK_EXCEPTION, 100, 0);
            }finally {
                Profiler.registerInfoEnd(info1);
            }
            if (!response.getCode().equals(200)) {//如果校验不OK
                //获得运单的预分拣站点
                Integer preSortingSiteCode = null;
                CallerInfo infoSendfindByWaybillCode = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.findByWaybillCode", false, true);
                try {
                    com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(BusinessHelper.getWaybillCode(domain.getBoxCode()), true, false, false, false);
                    if (null != waybill) {
                        preSortingSiteCode = waybill.getSiteCode();
                    }
                } catch (Throwable e) {
                    logger.error("一车一单获取预分拣站点异常", e);
                }finally {
                    Profiler.registerInfoEnd(infoSendfindByWaybillCode);
                }

                if (response.getCode() >= 39000) {
                    if (!isForceSend)
                        return new SendResult(DeliveryResponse.CODE_Delivery_SEND_CONFIRM, response.getMessage(), response.getCode(), preSortingSiteCode);
                } else{
                    return new SendResult(2, response.getMessage(), response.getCode(), preSortingSiteCode);
                }
            }

        }
        Profiler.registerInfoEnd(temp_info2);
        if(!isForceSend){
            DeliveryVerification.VerificationResult verificationResult=cityDeliveryVerification.verification(domain.getBoxCode(),domain.getReceiveSiteCode(),false);
            if(!verificationResult.getCode()){//按照箱发货，校验派车单是否齐全，判断是否强制发货
                return new SendResult(4, verificationResult.getMessage());
            }
        }
        CallerInfo temp_info3 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info3", false, true);
        //插入SEND_M
        this.sendMDao.insertSendM(domain);
        if (!SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())) {
            pushSorting(domain);//大件写TASK_SORTING
        } else {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(domain.getBoxCode());
            tSendDatail.setCreateSiteCode(domain.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(domain.getReceiveSiteCode());
            this.updateCancel(tSendDatail);//更新SEND_D状态

        }
        this.transitSend(domain);
        this.pushStatusTask(domain);
        Profiler.registerInfoEnd(temp_info3);
        return new SendResult(1, "发货成功");
    }
    /**
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     * @param domain 发货对象
     * @return 1：发货成功
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.offlinePackageSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult offlinePackageSend(SendM domain) {
        //插入SEND_M
        this.sendMDao.insertSendM(domain);
        if (!SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())) {
            pushSorting(domain);//大件写TASK_SORTING
        } else {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(domain.getBoxCode());
            tSendDatail.setCreateSiteCode(domain.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(domain.getReceiveSiteCode());
            this.updateCancel(tSendDatail);//更新SEND_D状态

        }
        this.transitSend(domain);
        this.pushStatusTask(domain);
        return new SendResult(1, "发货成功");
    }


    /**
     * 推分拣任务
     * @param domain
     */
    private void pushSorting(SendM domain) {
        BaseStaffSiteOrgDto create = siteService.getSite(domain.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;
        BaseStaffSiteOrgDto receive = siteService.getSite(domain.getReceiveSiteCode());
        String receiveSiteName = null != receive ? receive.getSiteName() : null;
        Task task = new Task();
        task.setBoxCode(domain.getBoxCode());
        task.setCreateSiteCode(domain.getCreateSiteCode());
        task.setReceiveSiteCode(domain.getReceiveSiteCode());
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SORTING);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(domain.getCreateSiteCode().toString());
        task.setKeyword2(domain.getBoxCode());
        task.setOperateTime(new Date(domain.getOperateTime().getTime()-30000));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());
        SortingRequest sortDomain = new SortingRequest();
        sortDomain.setOperateTime(DateHelper.formatDateTimeMs(new Date(domain.getOperateTime().getTime()-30000)));
        sortDomain.setBoxCode(domain.getBoxCode());
        sortDomain.setUserCode(domain.getCreateUserCode());
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setPackageCode(domain.getBoxCode());
        sortDomain.setSiteName(createSiteName);
        sortDomain.setIsCancel(0);
        sortDomain.setSiteCode(domain.getCreateSiteCode());
        sortDomain.setBsendCode("");
        sortDomain.setIsLoss(0);
        sortDomain.setFeatureType(0);
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setBusinessType(10);
        sortDomain.setWaybillCode(SerialRuleUtil.getWaybillCode(domain.getBoxCode()));
        sortDomain.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortDomain.setReceiveSiteName(receiveSiteName);
        task.setBody(JsonHelper.toJson(new SortingRequest[]{sortDomain}));
        taskService.add(task, true);
        logger.info("一车一单插入task_sorting" + JsonHelper.toJson(task));
    }
    
    /**
     * 推送分拣机按箱自动发货任务
     * @author lihuachang  
     * @category 2017.11.30
     * @param domain
     * @param barCode
     */
    private void pushAtuoSorting(SendM domain,String packageCode) {
        BaseStaffSiteOrgDto create = siteService.getSite(domain.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;
        BaseStaffSiteOrgDto receive = siteService.getSite(domain.getReceiveSiteCode());
        String receiveSiteName = null != receive ? receive.getSiteName() : null;
        Task task = new Task();
        task.setBoxCode(domain.getBoxCode());
        task.setCreateSiteCode(domain.getCreateSiteCode());
        task.setReceiveSiteCode(domain.getReceiveSiteCode());
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SORTING);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(domain.getCreateSiteCode().toString());
        task.setKeyword2(packageCode);
        task.setOperateTime(new Date(domain.getOperateTime().getTime()-30000));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());
        SortingRequest sortDomain = new SortingRequest();
        sortDomain.setOperateTime(DateHelper.formatDateTimeMs(new Date(domain.getOperateTime().getTime()-30000)));
        sortDomain.setBoxCode(domain.getBoxCode());
        sortDomain.setUserCode(domain.getCreateUserCode());
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setPackageCode(packageCode);
        sortDomain.setSiteName(createSiteName);
        sortDomain.setIsCancel(0);
        sortDomain.setSiteCode(domain.getCreateSiteCode());
        sortDomain.setBsendCode("");
        sortDomain.setIsLoss(0);
        sortDomain.setFeatureType(0);
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setBusinessType(10);
        sortDomain.setWaybillCode(SerialRuleUtil.getWaybillCode(packageCode));
        sortDomain.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortDomain.setReceiveSiteName(receiveSiteName);
        task.setBody(JsonHelper.toJson(new SortingRequest[]{sortDomain}));
        taskService.add(task, true);
        logger.info("自动分拣机按照箱号发货task_sorting" + JsonHelper.toJson(task));
    }

    @Override
    public int pushStatusTask(SendM domain) {
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(2);
        body.copyFromParent(domain);
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(JsonHelper.toJson(body));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("1");// 1 回传运单状态
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
        //只有箱号添加回传周转箱任务
        if (BusinessHelper.isBoxcode(domain.getBoxCode())) {
            tTask.setKeyword1("2");// 2回传周转箱号
            tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
            tTaskService.add(tTask, true);
        }
        return 0;
    }

    /**
     * 查询箱号发货记录
     *
     * @param boxCode 箱号
     * @return 发货记录列表
     */
    @JProfiler(jKey = "DMSWORKER.deliveryServiceImpl.getSendMListByBoxCode", mState = {JProEnum.TP,
            JProEnum.FunctionError})
    public List<SendM> getSendMListByBoxCode(String boxCode) {
        SendM domain = new SendM();
        domain.setBoxCode(boxCode);
        return this.sendMDao.findSendMByBoxCode(domain);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(SendDetail sendDetail) {
        if (sendDetail.getPackageBarcode() == null) {
            sendDetail.setPackageNum(0);
        } else {
            Integer packageNum = getPackageNum(sendDetail.getPackageBarcode());
            if (packageNum == null) {
                this.logger.error("无法获得包裹数量[" + sendDetail.getPackageBarcode()
                        + "]");
                sendDetail.setPackageNum(0);
            } else {
                sendDetail.setPackageNum(packageNum);
            }
        }
        return this.sendDatailDao.add(SendDatailDao.namespace, sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer update(SendDetail sendDetail) {
        return this.sendDatailDao.update(SendDatailDao.namespace, sendDetail);
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.deliveryService.updateCancel", mState = {JProEnum.TP,
            JProEnum.FunctionError})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateCancel(SendDetail sendDetail) {
        return this.sendDatailDao.updateCancel(sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveOrUpdateBatch(List<SendDetail> sdList) {
        List<SendDetail>[] sendArray = splitList(sdList);
        List<SendDetail> result = new ArrayList<SendDetail>();

        List<SendDetail> updateList = new ArrayList<SendDetail>();
        //批量查询是否存在send_d
        for (List<SendDetail> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendDatailDao.batchQuerySendDList(request));
        }

        //对不存在send_d的包裹写入
        for (SendDetail senddetail : sdList) {
            if (!result.contains(senddetail.getBoxCode())) {
                this.add(senddetail);
            } else {
                updateList.add(senddetail);
            }
        }
        //对于存在send_d的执行批量更新
        sendArray = splitList(updateList);
        for (List<SendDetail> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            sendDatailDao.updateCancelBatch(request);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveOrUpdate(SendDetail sendDetail) {
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean canCancel(SendDetail sendDetail) {
        return this.sendDatailDao.canCancel(sendDetail);
    }
    /**
     * 取消发货操作 不判断send_type added by zhanglei
     * @param sendDetail
     * @return
     */
    @Override
    public Boolean canCancel2(SendDetail sendDetail) {
        return this.sendDatailDao.canCancel2(sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Boolean canCancelFuzzy(SendDetail sendDetail) {
        return this.sendDatailDao.canCancelFuzzy(sendDetail);
    }

    /**
     * 发货主表数据写入
     *
     * @param sendMlist 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertSendM(List<SendM> sendMlist, List<String> list) {

        for (SendM dSendM : sendMlist) {
            if (!list.contains(dSendM.getBoxCode())) {
                this.sendMDao.insertSendM(dSendM);
            }
        }
    }

    /***
     * 发货写入任务表
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private void addTaskSend(SendM sendM) {
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(1);
        body.copyFromParent(sendM);
        Task tTask = new Task();
        tTask.setBoxCode(sendM.getSendCode());
        //tTask.setBody(sendM.getSendCode());
        tTask.setBody(JsonHelper.toJson(body));
        tTask.setCreateSiteCode(sendM.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(sendM.getSendType()));
        tTask.setReceiveSiteCode(sendM.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("1");// 1 回传运单状态
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);
        tTask.setKeyword1("2");// 2回传周转箱号
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);

        tTask.setBody(sendM.getSendCode());

        tTask.setKeyword1("3");// 3回传dmc
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);
        if (businessTypeTWO.equals(sendM.getSendType())) {
            //&& sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)  取消逆向发车的时候推送仓储任务，修改到发货环节推送 20150724

            tTask.setKeyword1("4");//4逆向任务
            tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
            tTaskService.add(tTask);
        }

		/* 第三方发货推送财务
		*  写到task_delviery_to_finance_batch表*/
        if (businessTypeTHR.equals(sendM.getSendType())){
            String sendCode = sendM.getSendCode();
            Task task = new Task();
            task.setKeyword1(businessTypeTHR+"");
            task.setBody(sendCode);
            task.setTableName(Task.TABLE_NAME_DELIVERY_TO_FINANCE_BATCH);
            task.setType(Task.TASK_TYPE_DELIVERY_TO_FINANCE_BATCH);
            task.setOwnSign(BusinessHelper.getOwnSign());

            taskService.add(task);
        }

        /*添加自动化分拣发货回传波次表*/
        BatchSend batchSend = new BatchSend();
        batchSend.setSendCode(sendM.getSendCode());
        batchSendDao.batchUpdateStatus(batchSend);
    }

    /**
     * 插入pda操作日志表
     *
     * @param sendDetail
     */
    private void addOperationLog(SendDetail sendDetail) {
        OperationLog operationLog = new OperationLog();
        operationLog.setBoxCode(sendDetail.getBoxCode());
        operationLog.setCreateSiteCode(sendDetail.getCreateSiteCode());
        operationLog.setCreateTime(sendDetail.getOperateTime());
        operationLog.setCreateUser(sendDetail.getCreateUser());
        operationLog.setCreateUserCode(sendDetail.getCreateUserCode());
        if (sendDetail.getIsCancel() == 2) {
            operationLog.setLogType(OperationLog.LOG_TYPE_SEND_CANCELDELIVERY);
        } else {
            operationLog.setLogType(OperationLog.LOG_TYPE_SEND_DELIVERY);
        }
        operationLog.setOperateTime(sendDetail.getOperateTime());
        operationLog.setPackageCode(sendDetail.getPackageBarcode());
        operationLog.setPickupCode(sendDetail.getPickupCode());
        operationLog.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        operationLog.setUpdateTime(sendDetail.getUpdateTime());
        operationLog.setWaybillCode(sendDetail.getWaybillCode());
        operationLogService.add(operationLog);
    }

    /**
     * 生成发货数据处理
     *
     * @param sendMList 发货相关数据
     */
    public DeliveryResponse dellDeliveryMessage(List<SendM> sendMList) {
        try {
            return this.dellCreateSendM(sendMList);
        } catch (Exception e) {
            this.logger.error("生成发货数据处理", e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
    }

    /**
     * 发货主表数据处理
     *
     * @param sendMList 发货相关数据
     */
    public DeliveryResponse dellCreateSendM(List<SendM> sendMList) {
        CallerInfo info1 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send", false, true);
        Collections.sort(sendMList);
        // 加send_code幂
        boolean sendCodeIdempotence = this.querySendCode(sendMList);/*判断当前批次号是否已经发货*/

        if (sendCodeIdempotence) {
            return new DeliveryResponse(JdResponse.CODE_OK,
                    JdResponse.MESSAGE_OK);
        }
        List<String> list = batchQuerySendMList(sendMList);/*查询已发货的箱号*/

        Profiler.registerInfoEnd(info1);
        // 取消发货在发货状态位回执
        this.cancelStatusReceipt(sendMList, list);

        CallerInfo info2 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send2", false, true);
        // 写入发货表数据
        this.insertSendM(sendMList, list);

        for (SendM domain : sendMList) {
            this.transitSend(domain);//插入中转任务
        }
        // 写入任务
        addTaskSend(sendMList.get(0));
        Profiler.registerInfoEnd(info2);

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }


    /**
     * 批量判断箱号是否已经发货，提出公用，减少查询次数
     * *
     */
    private List<String> batchQuerySendMList(List<SendM> sendMList) {
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<String>();
        for (List<SendM> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQuerySendMList(request));
        }

        return result;
    }

    /**
     * 次发货数据状态更新,添加批量操作
     *
     * @param sendMList 待发货列表
     * @param list      已发货的箱号列表
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private void cancelStatusReceipt(List<SendM> sendMList, List<String> list) {
        //操作过取消发货的箱子查询  result结果集
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<String>();
        for (List<SendM> slist : sendArray) {
            String boxCode = StringHelper.join(slist, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = slist.get(0).getCreateSiteCode();
            Integer receiveSiteCode = slist.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQueryCancelSendMList(request));
        }
        List<SendDetail> sdList = new ArrayList<SendDetail>();

        for (SendM tsendM : sendMList) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tsendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tsendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(tsendM.getReceiveSiteCode());
            if (!list.contains(tsendM.getBoxCode())) {
                if (BusinessHelper.isBoxcode(tsendM.getBoxCode())
                        && result.contains(tsendM.getBoxCode())) {
                    tSendDatail.setStatus(2);
                    this.updateCancel(tSendDatail);
                }
                if (BusinessHelper.isPackageCode(tsendM.getBoxCode())
                        || BusinessHelper.isPickupCode(tsendM.getBoxCode())) {
                    this.fillPickup(tSendDatail, tsendM);
                    tSendDatail.setOperateTime(tsendM.getCreateTime());
                    sdList.add(tSendDatail);
                }
            }
        }
        //批量处理
        if (sdList != null && !sdList.isEmpty()) {
            this.saveOrUpdateBatch(sdList);
        }

    }

    /**
     * 查询send_m是否存在本次发货的批次号
     *
     * @param sendMList
     * @return
     */
    private boolean querySendCode(List<SendM> sendMList) {
        SendM sendM = sendMDao.selectBySendCode(sendMList.get(0).getSendCode());
        if (null != sendM && sendM.getSendMId() > 0) {
            return true;
        }

        return false;
    }

    /***
     * 补全包裹信息
     */
    private void fillPickup(SendDetail tSendDatail, SendM tsendM) {
        tSendDatail.setCreateUser(tsendM.getCreateUser());
        tSendDatail.setCreateUserCode(tsendM.getCreateUserCode());
        tSendDatail.setSendType(tsendM.getSendType());
        tSendDatail.setStatus(0);
        tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
        tSendDatail.setOperateTime(new Date());
        tSendDatail.setUpdateTime(new Date());
        if (BusinessHelper.isPackageCode(tsendM.getBoxCode())) {
            // 如果大件包裹没有分拣添加明细表信息
            tSendDatail.setPackageBarcode(tsendM.getBoxCode());
            tSendDatail.setWaybillCode(BusinessHelper.getWaybillCode(tsendM
                    .getBoxCode()));
        } else if (BusinessHelper.isPickupCode(tsendM.getBoxCode())) {
            if (BusinessHelper.isPickupCodeWW(tsendM.getBoxCode())) {
                tSendDatail
                        .setWaybillCode(tsendM.getBoxCode());
            } else {
                BaseEntity<PickupTask> pickup = null;
                try {
                    pickup = this.waybillPickupTaskApi.getDataBySfCode(tsendM
                            .getBoxCode());
                } catch (Exception e) {
                    this.logger.error("调用取件单号信息ws接口异常");
                }
                if (pickup != null && pickup.getData() != null) {
                    tSendDatail.setPickupCode(pickup.getData().getPickupCode());
                    tSendDatail.setPackageBarcode(tsendM.getBoxCode());
                    tSendDatail
                            .setWaybillCode(pickup.getData().getOldWaybillCode());
                }
            }
        }
    }

    public DeliveryResponse findSendMByBoxCode(SendM tSendM, boolean flage) {
        String reslut = "0";
        Box box = null;
        List<SendM> tSendMList = new ArrayList<SendM>();
        // 验证是否发货箱子和包裹分开处理
        if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
            SendM dSendM = new SendM();
            dSendM.setBoxCode(tSendM.getBoxCode());
            dSendM.setCreateSiteCode(tSendM.getCreateSiteCode());
            dSendM.setSendType(tSendM.getSendType());
            tSendMList = this.sendMDao.findSendMByBoxCode(dSendM);
        } else if (BusinessHelper.isPackageCode(tSendM.getBoxCode())) {
            // 再投标识
            tSendMList = this.sendMDao.findSendMByBoxCode(tSendM);
        }

        if (tSendMList != null && !tSendMList.isEmpty()) {
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_IS_SEND,
                    DeliveryResponse.MESSAGE_Delivery_IS_SEND);
        }
        if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
            box = this.boxService.findBoxByCode(tSendM.getBoxCode());
            if (box != null) {
                if (!box.getReceiveSiteCode().equals(
                        tSendM.getReceiveSiteCode())
                        && !flage) {
                    return new DeliveryResponse(
                            DeliveryResponse.CODE_Delivery_IS_SITE,
                            DeliveryResponse.MESSAGE_Delivery_IS_SITE);
                }
                if (tSendM.getSendType().equals(businessTypeTHR)) {
                    // 查找该箱子的包裹数量
                    SendDetail tsendDatail = new SendDetail();
                    tsendDatail.setBoxCode(tSendM.getBoxCode());
                    tsendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
                    tsendDatail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                    tsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
                    List<SendDetail> sendDatailist = this.sendDatailDao
                            .querySendDatailsBySelective(tsendDatail);
                    if (sendDatailist != null && !sendDatailist.isEmpty()) {
                        reslut = StringHelper.getStringValue(sendDatailist
                                .size());
                    }
                    if (inspectionExcetionService.queryExceptions(
                            tSendM.getCreateSiteCode(),
                            tSendM.getReceiveSiteCode(),
                            tSendM.getBoxCode()) > 0) {
                        // 第三方发货验证是否存在异常
                        return new DeliveryResponse(
                                DeliveryResponse.CODE_Delivery_ALL_CHECK,
                                DeliveryResponse.MESSAGE_Delivery_ALL_CHECK);
                    }
                }
            } else {
                return new DeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        DeliveryResponse.MESSAGE_Delivery_NO_MESAGE);
            }
        } else if (BusinessHelper.isPackageCode(tSendM.getBoxCode())) {
            reslut = StringHelper.getStringValue(1);
            Sorting sorting = new Sorting();
            sorting.setBoxCode(tSendM.getBoxCode());
            sorting.setCreateSiteCode(tSendM.getCreateSiteCode());
            sorting.setType(tSendM.getSendType());
            List<Sorting> tSorting = this.tSortingService
                    .findByBoxCode(sorting);
            if (tSorting != null && !tSorting.isEmpty()) {
                Sorting nSorting = getLastSortingDate(tSorting);
                if (!nSorting.getReceiveSiteCode().equals(
                        tSendM.getReceiveSiteCode())
                        && !flage) {
                    return new DeliveryResponse(
                            DeliveryResponse.CODE_Delivery_TRANSIT,
                            DeliveryResponse.MESSAGE_Delivery_TRANSIT);
                }
            }
        }
        return new DeliveryResponse(JdResponse.CODE_OK, reslut);
    }

    /**
     * 生成取消发货数据处理
     * updated by wangtingwei@jd.com
     * edit:将取消发货分为两类，一类为按箱号，另一类为按包裹（包括按运单、包裹、取件单）
     *
     * @param tSendM 发货相关数据
     */
    @JProfiler(jKey = "DMSWEB.DeliveryService.dellCancel", mState = {JProEnum.TP})
    public ThreeDeliveryResponse dellCancelDeliveryMessage(SendM tSendM) {
        try {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tSendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
            // 按照运单取消处理
            if (BusinessHelper.isWaybillCode(tSendM.getBoxCode())
                    || BusinessHelper.isPickupCode(tSendM.getBoxCode())
                    || BusinessHelper.isPackageCode(tSendM.getBoxCode())) {
                SendDetail mSendDetail = new SendDetail();
                if (BusinessHelper.isWaybillCode(tSendM.getBoxCode())) {
                    mSendDetail.setWaybillCode(tSendM.getBoxCode());
                } else {
                    mSendDetail.setPackageBarcode(tSendM.getBoxCode());
                }
                mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                mSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_L);
                List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
                if (tlist != null && !tlist.isEmpty()) {
                    ThreeDeliveryResponse responsePack = cancelUpdateDataByPack(tSendM, tlist);
                    if (responsePack.getCode().equals(200)) {
                        delDeliveryFromRedis(tSendM);      //取消发货成功，删除redis缓存的发货数据
                        sendMessage(tlist, tSendM);
                    }
					return responsePack;
				} else {
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_NO_MESAGE,
							DeliveryResponse.MESSAGE_Delivery_NO_PACKAGE, null);
				}
			} else if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
				List<SendM> sendMList = this.sendMDao.findSendMByBoxCode2(tSendM);
                SendDetail queryDetail = new SendDetail();
                queryDetail.setBoxCode(tSendM.getBoxCode());
                List<SendDetail> sendDatails = sendDatailDao.querySendDatailsByBoxCode(queryDetail);
                ThreeDeliveryResponse threeDeliveryResponse = cancelUpdateDataByBox(tSendM, tSendDatail, sendMList);
                if (threeDeliveryResponse.getCode().equals(200)) {
                    delDeliveryFromRedis(tSendM);     //取消发货成功，删除redis缓存的发货数据
                    sendMessage(sendDatails, tSendM);

                    // 更新箱子状态为正常
//                    List<String> boxCodes = new ArrayList<String>();
//                    boxCodes.add(tSendM.getBoxCode());
//                    boxService.batchUpdateStatus(boxCodes, Box.STATUS_PRINT);
                }
                return threeDeliveryResponse;
            }


            // 改变箱子状态为分拣
        } catch (Exception e) {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR, null);
        }
        return new ThreeDeliveryResponse(
                DeliveryResponse.CODE_Delivery_NO_MESAGE,
                DeliveryResponse.MESSAGE_Delivery_NO_REQUEST, null);
    }


    private void delDeliveryFromRedis(SendM sendM) {
        Long result = redisManager.del(
                CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                        + sendM.getCreateSiteCode()
                        + sendM.getBoxCode());
        if (result <= 0) {
            logger.warn("remove sendms of key ["
                    + CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                    + sendM.getCreateSiteCode() + sendM.getBoxCode()
                    + "] from redis fail");
        } else {
            logger.warn("remove sendms of key ["
                    + CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                    + sendM.getCreateSiteCode() + sendM.getBoxCode()
                    + "] from redis success");
        }
    }


    /****
     * 发送全程跟踪
     *
     * @param senddetail
     * @param tSendM
     */
    private void sendMessage(List<SendDetail> senddetail, SendM tSendM) {
        try {
            if (senddetail == null || senddetail.isEmpty()) {
                return;
            }
            //按照包裹
            for (SendDetail model : senddetail) {
                send(model, tSendM);
            }
        } catch (Exception ex) {
            logger.error("取消发货 发全程跟踪sendMessage： " + ex);
        }

    }

    /******
     * 按照包裹号取消发货 发送全程跟踪
     *
     * @param sendDetail
     * @param tSendM
     */
    private void send(SendDetail sendDetail, SendM tSendM) {
        Task tTask = new Task();
        tTask.setKeyword1(tSendM.getThirdWaybillCode());
        tTask.setCreateSiteCode(tSendM.getReceiveSiteCode());
        tTask.setCreateTime(new Date());
        tTask.setKeyword2(String.valueOf(3800));
        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status = new WaybillStatus();
        status.setOperateType(3800);
        status.setWaybillCode(sendDetail.getWaybillCode());
        status.setPackageCode(sendDetail.getPackageBarcode());
        status.setOperateTime(tSendM.getUpdateTime());
        status.setOperator(tSendM.getUpdaterUser());
        status.setOperatorId(tSendM.getUpdateUserCode());
        status.setRemark("取消发货");
        status.setCreateSiteCode(tSendM.getCreateSiteCode());

        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(tSendM.getCreateSiteCode());

        status.setCreateSiteName(dto.getSiteName());
        tTask.setBody(JsonHelper.toJson(status));
        logger.info("取消发货 发全程跟踪work6666-3800： " + JsonHelper.toJson(status));
        taskService.add(tTask);
    }


    //处理箱子
    private ThreeDeliveryResponse cancelUpdateDataByBox(SendM tSendM,
                                                        SendDetail tSendDatail, List<SendM> sendMList) {
        Collections.sort(sendMList);
        if (sendMList != null && !sendMList.isEmpty()) {
            SendM dSendM = this.getLastSendDate(sendMList);
            dSendM.setUpdaterUser(tSendM.getUpdaterUser());
            dSendM.setUpdateUserCode(tSendM.getUpdateUserCode());
            dSendM.setUpdateTime(new Date());
            String caruser = dSendM.getSendUser();
            // 是否发车
            if (caruser != null && !"".equals(caruser)) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_DEPART,
                        DeliveryResponse.MESSAGE_Delivery_NO_DEPART, null);
            }

            /*
            if (dSendM.getSendType() != null) {
				List<SendM> newList = new ArrayList<SendM>();
				newList.add(dSendM);
				List<SendThreeDetail> lackOrderList = checkThreePackage4Cancel(newList);
				if (lackOrderList != null && !lackOrderList.isEmpty()) {
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_LACK_ORDER,
							DeliveryResponse.MESSAGE_Delivery_LACK_ORDER,
							lackOrderList);
				}
			}*/

            tSendDatail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
            // 是否发货状态更新
            if (sendDatailDao.querySendDatailBySendStatus(tSendDatail) != null) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        DeliveryResponse.MESSAGE_Delivery_IS_MESAGE, null);
            }
            return this.cancelDeliveryStatusByBox(dSendM, tSendDatail);
        } else {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_NO_MESAGE,
                    DeliveryResponse.MESSAGE_Delivery_NO_MESAGE, null);
        }
    }

    //处理运单
    private ThreeDeliveryResponse cancelUpdateDataByPack(SendM tSendM,
                                                         List<SendDetail> tList) {
        Collections.sort(tList);
		for (SendDetail dSendDetail : tList) {
			tSendM.setBoxCode(dSendDetail.getBoxCode());
			List<SendM> sendMList = this.sendMDao.findSendMByBoxCode(tSendM);
			// 发车验证
			if (sendMList != null && !sendMList.isEmpty()) {
				SendM dSendM = this.getLastSendDate(sendMList);
				dSendM.setUpdaterUser(tSendM.getUpdaterUser());
				dSendM.setUpdateUserCode(tSendM.getUpdateUserCode());
				dSendM.setUpdateTime(new Date());
				String caruser = dSendM.getSendUser();
				if (caruser != null && !"".equals(caruser)) {
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_NO_DEPART,
							DeliveryResponse.MESSAGE_Delivery_NO_DEPART, null);
				}
				break;
			} else {
				return new ThreeDeliveryResponse(
						DeliveryResponse.CODE_Delivery_NO_MESAGE,
						DeliveryResponse.MESSAGE_Delivery_NO_MESAGE, null);
			}
		}
		for (SendDetail dSendDetail : tList) {
			cancelDeliveryStatusByPack(tSendM, dSendDetail);
			Sorting sorting = new Sorting.Builder(
					dSendDetail.getCreateSiteCode())
					.boxCode(dSendDetail.getBoxCode())
					.waybillCode(dSendDetail.getWaybillCode())
					.packageCode(dSendDetail.getPackageBarcode())
					.type(dSendDetail.getSendType())
					.receiveSiteCode(dSendDetail.getReceiveSiteCode())
					.updateUser(dSendDetail.getCreateUser())
					.updateUserCode(dSendDetail.getCreateUserCode())
					.updateTime(new Date()).build();
			tSortingService.canCancel2(sorting);
		}
		return new ThreeDeliveryResponse(JdResponse.CODE_OK,
				JdResponse.MESSAGE_OK, null);
	}

    //箱子更新取消发货状态
    public ThreeDeliveryResponse cancelDeliveryStatusByBox(SendM tSendM, SendDetail tSendDatail) {
        SendDetail mSendDetail = new SendDetail();
        mSendDetail.setBoxCode(tSendM.getBoxCode());
        mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
        mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
        mSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
        List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
        Collections.sort(tlist);
        //更新m表和d表
        reverseDeliveryService.updateIsCancelByBox(tSendM, tlist);
        //写入运单回传状态
        reverseDeliveryService.updateIsCancelToWaybillByBox(tSendM, tlist);
        return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
    }

    public ThreeDeliveryResponse cancelDeliveryStatusByPack(SendM tSendM,
                                                            SendDetail tSendDatail) {
        // 更新m表和d表
        reverseDeliveryService.updateIsCancelByPackageCode(tSendM, tSendDatail);
        // 写入运单回传状态
        reverseDeliveryService.updateIsCancelToWaybillByPackageCode(tSendM,
                tSendDatail);
        return new ThreeDeliveryResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK, null);
    }

    /**
     * 取消发货明主表数据处理
     *
     * @param tSendM 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendM(SendM tSendM) {
        return this.sendMDao.cancelSendM(tSendM);
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param tSendDetail 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendDatailByPackage(SendDetail tSendDetail) {
        if (tSendDetail != null) {
            try {
                this.sendDatailDao.cancelSendDatail(tSendDetail);
            } catch (Exception e) {
                this.logger.error("取消发货cancelSendDatailByPackage,参数"
                        + JsonHelper.toJson(tSendDetail), e);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param tlist 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendDatailByBox(List<SendDetail> tlist) {
        if (tlist != null && !tlist.isEmpty()) {
            List<SendDetail>[] splitListResultAl = splitList(tlist);
            for (List<SendDetail> splitListResult : splitListResultAl) {
                List<String> packCodes = new ArrayList<String>();
                SendDetail tSendDatail = new SendDetail();
                tSendDatail.setBoxCode(splitListResult.get(0).getBoxCode());
                tSendDatail.setReceiveSiteCode(splitListResult.get(0)
                        .getReceiveSiteCode());
                tSendDatail.setCreateSiteCode(splitListResult.get(0)
                        .getCreateSiteCode());
                for (SendDetail oneSendDetail : splitListResult) {
                    packCodes.add(oneSendDetail.getPackageBarcode());
                }
                String packCodein = StringHelper.join(packCodes, ",", "(", ")",
                        "'");
                tSendDatail.setSendCode(packCodein);
                try {
                    sendDatailDao.cancelSendDatail(tSendDatail);
                } catch (Exception e) {
                    this.logger.error("取消发货cancelSendDatailByBox,参数"
                            + JsonHelper.toJson(tSendDatail), e);
                }
            }
        }
        return true;
    }

    /**
     * 2012-10-12
     * 拆分数据数据 每组999
     *
     * @param transresult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<SendDetail>[] splitList(List<SendDetail> transresult) {
        List<List<SendDetail>> splitList = new ArrayList<List<SendDetail>>();
        for (int i = 0; i < transresult.size(); i += BATCH_NUM) {
            int size = i + BATCH_NUM > transresult.size() ? transresult.size() : i + BATCH_NUM;
            List<SendDetail> tmp = (List<SendDetail>) transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

    /**
     * 2012-10-12
     * 拆分数据数据 每组99
     *
     * @param transresult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<SendM>[] splitSendMList(List<SendM> transresult) {
        List<List<SendM>> splitList = new ArrayList<List<SendM>>();
        for (int i = 0; i < transresult.size(); i += BATCH_NUM_M) {
            int size = i + BATCH_NUM_M > transresult.size() ? transresult.size() : i + BATCH_NUM_M;
            List<SendM> tmp = (List<SendM>) transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

    @JProfiler(jKey = "DMSWEB.DeliveryService.updateWaybillStatus", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean updateWaybillStatus(List<SendDetail> sendDetails) {
        logger.info(JsonHelper.toJson(sendDetails));
        if (sendDetails != null && !sendDetails.isEmpty()) {
            List<SendDetail> isstatus = new ArrayList<SendDetail>();
            List<SendDetail> notstatus = new ArrayList<SendDetail>();

            // 增加获取订单类型判断是否是LBP订单s
            Set<String> waybillset = new HashSet<String>();
            Map<String, Integer> sendDatailMap = new HashMap<String, Integer>();
            for (SendDetail dSendDatail : sendDetails) {
                waybillset.add(dSendDatail.getWaybillCode());
            }
            List<String> waybillList = new CollectionHelper<String>().toList(waybillset);
            WChoice queryWChoice = new WChoice();
            queryWChoice.setQueryWaybillC(true);
            List<BigWaybillDto> tWaybillList = getWaillCodeListMessge(queryWChoice, waybillList);
            if (tWaybillList != null && !tWaybillList.isEmpty()) {
                for (BigWaybillDto tWaybill : tWaybillList) {
                    if (tWaybill != null && tWaybill.getWaybill() != null &&
                            tWaybill.getWaybill().getWaybillCode() != null && tWaybill.getWaybill().getWaybillType() != null) {
                        sendDatailMap.put(tWaybill.getWaybill().getWaybillCode(), tWaybill.getWaybill().getWaybillType());
                    }
                }
            }
            // 增加获取订单类型判断是否是LBP订单e

            for (SendDetail tSendDatail : sendDetails) {
                tSendDatail.setStatus(1);
                if (!tSendDatail.getIsCancel().equals(1)) {

                    BaseStaffSiteOrgDto cbDto = null;
                    BaseStaffSiteOrgDto rbDto = null;

                    try {
                        rbDto = this.baseMajorManager.getBaseSiteBySiteId(tSendDatail.getReceiveSiteCode());
                        cbDto = this.baseMajorManager.getBaseSiteBySiteId(tSendDatail.getCreateSiteCode());
                    } catch (Exception e) {
                        this.logger.error("发货全程跟踪调用站点信息异常", e);
                    }

                    if (cbDto == null)
                        cbDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(tSendDatail.getCreateSiteCode()));

                    if (rbDto == null)
                        rbDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(tSendDatail.getReceiveSiteCode()));

                    if (rbDto != null && rbDto.getSiteType() != null && cbDto != null && cbDto.getSiteType() != null) {
                        WaybillStatus tWaybillStatus = new WaybillStatus();
                        tWaybillStatus.setReceiveSiteCode(tSendDatail.getReceiveSiteCode());
                        tWaybillStatus.setReceiveSiteName(rbDto.getSiteName());
                        tWaybillStatus.setReceiveSiteType(rbDto.getSiteType());
                        tWaybillStatus.setOperatorId(tSendDatail.getCreateUserCode());
                        tWaybillStatus.setOperator(tSendDatail.getCreateUser());
                        tWaybillStatus.setOperateTime(tSendDatail.getOperateTime());
                        tWaybillStatus.setOrgId(rbDto.getOrgId());
                        tWaybillStatus.setOrgName(rbDto.getOrgName());
                        tWaybillStatus.setPackageCode(tSendDatail.getPackageBarcode());
                        tWaybillStatus.setCreateSiteCode(tSendDatail.getCreateSiteCode());
                        tWaybillStatus.setCreateSiteName(cbDto.getSiteName());
                        tWaybillStatus.setCreateSiteType(cbDto.getSiteType());
                        tWaybillStatus.setOperateType(OPERATE_TYPE_REVERSE_SEND);
                        tWaybillStatus.setWaybillCode(tSendDatail.getWaybillCode());
                        tWaybillStatus.setSendCode(tSendDatail.getSendCode());
                        tWaybillStatus.setBoxCode(tSendDatail.getBoxCode());

                        if (!checkParameter(tWaybillStatus)) {
                            this.logger.info("发货数据调用基础资料接口参数信息不全：包裹号为"
                                    + tSendDatail.getPackageBarcode());
                        } else {
                            if (tSendDatail.getYn().equals(1) && tSendDatail.getIsCancel().equals(0)) {
                                addOperationLog(tSendDatail);
                                if (businessTypeTWO.equals(tSendDatail
                                        .getSendType())) {
                                    tWaybillStatus
                                            .setOperateType(OPERATE_TYPE_REVERSE_SEND);
                                } else {
                                    tWaybillStatus
                                            .setOperateType(OPERATE_TYPE_FORWARD_SEND);
                                }
                                canSuccess(tWaybillStatus, tSendDatail);
                                sendInspection(tSendDatail, sendDatailMap);

                                //发送发货明细mq
                                Message sendMessage = parseSendDetailToMessage(tSendDatail);
                                this.logger.info("发送MQ["+sendMessage.getTopic()+"],业务ID["+sendMessage.getBusinessId()+"],消息主题: " + sendMessage.getText());
                                this.dmsWorkSendDetailMQ.sendOnFailPersistent(sendMessage.getBusinessId(),sendMessage.getText());

                                //added by hanjiaxing 2016.12.20 reason:update gantry_exception set send_status = 1
                                int updateCount = gantryExceptionService.getGantryExceptionCountForUpdate(tSendDatail.getBoxCode(), Long.valueOf(tSendDatail.getCreateSiteCode()));
                                if (updateCount > 0) {
                                    gantryExceptionService.updateSendStatus(tSendDatail.getBoxCode(), Long.valueOf(tSendDatail.getCreateSiteCode()));
                                    this.logger.info("更新异常信息发货状态，箱号：" + tSendDatail.getBoxCode());
                                }


                            } else if (tSendDatail.getYn().equals(0) && tSendDatail.getIsCancel().equals(2)) {
                                tSendDatail.setSendCode(null);
                                if (businessTypeTWO.equals(tSendDatail
                                        .getSendType())) {
                                    tWaybillStatus
                                            .setOperateType(OPERATE_TYPE_REVERSE_SORTING);
                                } else {
                                    tWaybillStatus
                                            .setOperateType(OPERATE_TYPE_FORWARD_SORTING);
                                }
                                canSuccess(tWaybillStatus, tSendDatail);
                            }
                        }

                    }
                    isstatus.add(tSendDatail);
                } else notstatus.add(tSendDatail);
            }
            if (isstatus != null && !isstatus.isEmpty())
                this.updateWaybillStatusByPackage(isstatus);

            if (notstatus != null && !notstatus.isEmpty())
                this.updateSendStatusByPackage(notstatus);
        }
        return true;
    }

    private boolean checkParameter(WaybillStatus tWaybillStatus) {
        if (tWaybillStatus.getOperatorId() == null) {
            return Boolean.FALSE;
        } else if (tWaybillStatus.getReceiveSiteType() == null) {
            return Boolean.FALSE;
        } else if (tWaybillStatus.getOperator() == null
                || StringHelper.isEmpty(tWaybillStatus.getOperator())) {
            return Boolean.FALSE;
        } else if (tWaybillStatus.getOperateTime() == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }


    /**
     * 如果没有正逆向验货记录补发验货回传
     */
    private boolean checkInspection(SendDetail tSendDatail) {
        Boolean falge = false;
        Inspection inspection = new Inspection();
        if (BusinessHelper.isWaybillCode(tSendDatail.getWaybillCode())) {
            inspection.setPackageBarcode(tSendDatail.getPackageBarcode());
            inspection.setInspectionType(businessTypeONE);
            falge = inspectionService.haveInspection(inspection);
        } else
            falge = true;
        return falge;
    }

    /**
     * 如果没有正逆向验货记录补发验货回传
     */
    private void sendInspection(SendDetail tSendDatail, Map<String, Integer> sendDatailMap) {
        // this.logger.info("sendInspection--------"+tSendDatail.getPackageBarcode());
        // 如果是非LBP类型的订单直接返回
        Integer orderType = sendDatailMap.get(tSendDatail.getWaybillCode());
        if (orderType != null && Constants.POP_LBP.equals(orderType)) {
            // this.logger.info("sendInspection----checkInspection----"+checkInspection(tSendDatail));
            if (!checkInspection(tSendDatail)) {
                snedMQpop(tSendDatail);
            }
        }
    }

    private void snedMQpop(SendDetail tSendDatail) {

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        sb.append("<OrderSendDetail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        sb.append("<id>").append(tSendDatail.getWaybillCode()).append("</id>");
        String date = DateHelper.formatDate(tSendDatail.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
        sb.append("<receiveTime>").append(date).append("</receiveTime>");
        sb.append("</OrderSendDetail>");

        this.logger.info("snedMQpop----snedMQpop----" + sb.toString());
        pop1MQ.sendOnFailPersistent(tSendDatail.getWaybillCode(), sb.toString());
    }

    private boolean canSuccess(WaybillStatus tWaybillStatus, SendDetail tSendDatail) {
        if (tWaybillStatus == null) {
            return false;
        }
        Task tTask = new Task();
        tTask.setBoxCode(tSendDatail.getBoxCode());
        tTask.setBody(JsonHelper.toJson(tWaybillStatus));
        tTask.setCreateSiteCode(tSendDatail.getCreateSiteCode());
        tTask.setKeyword2(tSendDatail.getPackageBarcode());
        tTask.setReceiveSiteCode(tSendDatail.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.TABLE_NAME_WAYBILL);
        tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
        tTask.setOwnSign(BusinessHelper.getOwnSign());
        tTask.setKeyword1(tSendDatail.getWaybillCode());//回传运单状态
        tTask.setFingerprint(Md5Helper.encode(tSendDatail.getCreateSiteCode() + "_"
                + tSendDatail.getReceiveSiteCode() + "_" + tSendDatail.getSendType() + "-"
                + tWaybillStatus.getOperateType() + "_" + tSendDatail.getPackageBarcode() + "_" + tSendDatail.getOperateTime()));
        tTaskService.add(tTask);
        return true;
    }

    private void updateWaybillStatusByPackage(List<SendDetail> newendDList) {
        Collections.sort(newendDList);
        for (SendDetail tSendDatail : newendDList) {
            sendDatailDao.updatewaybillCodeStatus(tSendDatail);
            if (BusinessHelper.isReverseSpareCode(tSendDatail
                    .getPackageBarcode())) {
                ReverseSpare tReverseSpare = new ReverseSpare();
                tReverseSpare.setSpareCode(tSendDatail.getPackageBarcode());
                tReverseSpare.setSendCode(tSendDatail.getSendCode());
                reverseSpareDao
                        .update(ReverseSpareDao.namespace, tReverseSpare);
            }
        }
    }

    private void updateSendStatusByPackage(List<SendDetail> newendDList) {
        Collections.sort(newendDList);
        for (SendDetail tSendDatail : newendDList) {
            sendDatailDao.updateSendStatusByPackage(tSendDatail);
        }
    }

    /**
     * 回传发货状态至运单【两种方式，一种按批次回传，另一种按箱号回传】
     *
     * @param task
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.DeliveryService.updatewaybillCodeMessage", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean updatewaybillCodeMessage(Task task) {
        logger.info("发货状态开始处理" + JsonHelper.toJson(task) + "是否JSON字符串" + JsonHelper.isJsonString(task.getBody()));
        logger.info(task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null);
        if (task == null || task.getBoxCode() == null
                || task.getCreateSiteCode() == null)
            return true;
        List<SendM> tSendM = null;
        if (JsonHelper.isJsonString(task.getBody())) {
            SendTaskBody body = JsonHelper.fromJson(task.getBody(), SendTaskBody.class);
            logger.info("发货状态BODY" + JsonHelper.toJson(body));
            if (body.getHandleCategory().equals(1)) {
                tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                        body.getCreateSiteCode(), body.getSendCode());
            } else {
                tSendM = new ArrayList<SendM>(1);
                tSendM.add(body);
                logger.info("BODY明细" + JsonHelper.toJson(body));
            }
        } else {
            tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                    task.getCreateSiteCode(), task.getBoxCode());
        }
        logger.info("SEND_M明细" + JsonHelper.toJson(tSendM));
        SendDetail tSendDatail = new SendDetail();
//        List<Message> sendDetailMessageList = new ArrayList<Message>();
        List<SendDetail> sendDatailList = new ArrayList<SendDetail>();
        for (SendM newSendM : tSendM) {
            tSendDatail.setBoxCode(newSendM.getBoxCode());
            tSendDatail.setCreateSiteCode(newSendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
            sendDatailList = this.sendDatailDao
                    .querySendDatailsBySelective(tSendDatail);
            for (SendDetail dSendDatail : sendDatailList) {
                dSendDatail.setSendCode(newSendM.getSendCode());
                dSendDatail.setOperateTime(newSendM.getOperateTime());
                dSendDatail.setCreateUser(newSendM.getCreateUser());
                dSendDatail.setCreateUserCode(newSendM.getCreateUserCode());

                //包装JMQ的Message对象,保存到sendDetailMessageList集合中
//                sendDetailMessageList.add(parseSendDetailToMessage(dSendDatail));
            }
            logger.info("SEND_D明细" + JsonHelper.toJson(sendDatailList));
            updateWaybillStatus(sendDatailList);


            // ============向西北西南区域三方配送用户发送预警短信 只运行在20150509~20150520============
//			try {
//				// 10. 循环所有发货批次
//				// 20.根据发货目的地判断是不是三方运输到西北西南区域
//				Integer receiveSiteCode = newSendM.getReceiveSiteCode();
//				BaseStaffSiteOrgDto rbDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
//				int siteType = rbDto.getSiteType().intValue();// 获得站点类型
//				int subType = rbDto.getSubType().intValue();
//				int orgId = rbDto.getOrgId().intValue();// 获得机构id
//				if (siteType == 16 && subType == 16 && (orgId == 645 || orgId == 4)) {// 30.符合三方运输1616发往西北645 西南4区域
//					logger.info("批次符合发送短信规则:" + newSendM.getSendCode());
//					sendSms(sendDatailList);
//				}
//			} catch (Exception e) {
//				logger.error("西北西南机构发送预警短信失败: ", e);
//			}
            //==================================================================
        }

        //使用下面的这种方式会在发送mq失败之后 通过worker再发送一次 modified by zhanglei
//        try {
//            workerProducer.send(sendDetailMessageList);
//        } catch (Throwable e) {
//            logger.error("发货明细发送JMQ失败: ", e);
//        }
//        for(Message itemMessage : sendDetailMessageList){
//            this.logger.info("发送MQ["+itemMessage.getTopic()+"],业务ID["+itemMessage.getBusinessId()+"],消息主题: " + itemMessage.getText());
//            this.dmsWorkSendDetailMQ.sendOnFailPersistent(itemMessage.getBusinessId(),itemMessage.getText());
//        }
        return true;
    }

    private Message parseSendDetailToMessage(SendDetail sendDatail) {
        Message message = new Message();
        SendDetail newSendDetail = new SendDetail();
        if (sendDatail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间
            newSendDetail.setPackageBarcode(sendDatail.getPackageBarcode());
            newSendDetail.setCreateSiteCode(sendDatail.getCreateSiteCode());
            newSendDetail.setReceiveSiteCode(sendDatail.getReceiveSiteCode());
            newSendDetail.setOperateTime(sendDatail.getOperateTime());
            newSendDetail.setSendCode(sendDatail.getSendCode());
            newSendDetail.setCreateUserCode(sendDatail.getCreateUserCode());
            newSendDetail.setCreateUser(sendDatail.getCreateUser());
            newSendDetail.setSource("DMS");
            newSendDetail.setBoxCode(sendDatail.getBoxCode());
            message.setTopic(MessageDestinationConstant.SendDetailMQ.getName());
            message.setText(JSON.toJSONString(newSendDetail));
            message.setBusinessId(sendDatail.getPackageBarcode());
        }
        return message;
    }

    public boolean findSendwaybillMessage(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null || task.getKeyword2() == null)
            return true;

        List<SendM> tSendM = null;
        if (JsonHelper.isJsonString(task.getBody())) {
            SendTaskBody body = JsonHelper.fromJson(task.getBody(), SendTaskBody.class);
            if (body.getHandleCategory().equals(1)) {
                tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                        body.getCreateSiteCode(), body.getSendCode());
            } else {
                tSendM = new ArrayList<SendM>(1);
                tSendM.add(body);
            }
        } else {
            tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                    task.getCreateSiteCode(), task.getBoxCode());
        }
        if (tSendM != null && !tSendM.isEmpty()) {
            for (SendM newSendM : tSendM) {
                if (BusinessHelper.isBoxcode(newSendM.getBoxCode()) && newSendM.getTurnoverBoxCode() != null) {
                    TurnoverBoxInfo tTurnoverBoxInfo = new TurnoverBoxInfo();
                    tTurnoverBoxInfo.setBoxCode(newSendM.getBoxCode());
                    tTurnoverBoxInfo.setDestSiteId(newSendM.getReceiveSiteCode());
                    tTurnoverBoxInfo.setMessageType("SORTING_DELIVERY_QUEUE");
                    tTurnoverBoxInfo.setOperateTime(DateHelper.formatDateTime(newSendM.getCreateTime()));
                    tTurnoverBoxInfo.setOperatorId(newSendM.getCreateUserCode());
                    tTurnoverBoxInfo.setOperatorName(newSendM.getCreateUser());
                    tTurnoverBoxInfo.setOperatorSortingId(newSendM.getCreateSiteCode());
                    tTurnoverBoxInfo.setSendCode(newSendM.getSendCode());
                    tTurnoverBoxInfo.setTurnoverBoxCode(newSendM.getTurnoverBoxCode());
                    String body = JsonHelper.toJson(tTurnoverBoxInfo);
                    //messageClient.sendMessage("turnover_box", body, newSendM.getSendCode());
                    turnoverBoxMQ.send(newSendM.getSendCode(), body);
                }
            }
        }
        return true;
    }

    /**
     * 发货明细表数据更新
     *
     * @param sendDetiallist 发货相关数据
     */
    public void updateSendDetial(List<SendDetail> sendDetiallist) {
        if (sendDetiallist != null && !sendDetiallist.isEmpty()) {
            for (SendDetail tSendDatail : sendDetiallist) {
                this.sendDatailDao.updateSendDatail(tSendDatail);
            }
        }
    }

    /**
     * 比较时间大小
     *
     * @param sendMList
     */
    public SendM getLastSendDate(List<SendM> sendMList) {
        SendM tSendM = null;
        if (sendMList != null && !sendMList.isEmpty()) {
            for (SendM dSendM : sendMList) {
                if (tSendM == null) {
                    tSendM = dSendM;
                } else if (tSendM.getCreateTime().getTime() < dSendM.getCreateTime().getTime()) {
                    tSendM = dSendM;
                }
            }
        }
        return tSendM;
    }

    /**
     * 比较时间大小
     *
     * @param sortinhList
     */
    public Sorting getLastSortingDate(List<Sorting> sortinhList) {
        Sorting tSorting = null;
        if (sortinhList != null && !sortinhList.isEmpty()) {
            for (Sorting dSorting : sortinhList) {
                if (tSorting == null) {
                    tSorting = dSorting;
                } else if (tSorting.getCreateTime().getTime() < dSorting.getCreateTime().getTime()) {
                    tSorting = dSorting;
                }
            }
        }
        return tSorting;
    }

    public boolean checkSend(SendDetail tSendDatail) {
        List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);//FIXME:无create_site_code有跨节点风险
        if (sendDetails != null && !sendDetails.isEmpty()) {
            Set<SendDetail> sendDatailset = new CollectionHelper<SendDetail>().toSet(sendDetails);
            sendDetails = new CollectionHelper<SendDetail>().toList(sendDatailset);
            for (SendDetail dSendDatail : sendDetails) {
                SendM tSendM = new SendM();
                tSendM.setBoxCode(dSendDatail.getBoxCode());
                tSendM.setReceiveSiteCode(dSendDatail.getReceiveSiteCode());
                tSendM.setCreateSiteCode(dSendDatail.getCreateSiteCode());
            }
        }
        return true;
    }

    public SendDetail getSendSiteID(String packbarCode, Integer sitecode) {
        if (packbarCode == null || packbarCode.isEmpty() || sitecode == null) {
            return null;
        }
        SendDetail sendDetail = new SendDetail();
        sendDetail.setPackageBarcode(packbarCode);
        sendDetail.setCreateSiteCode(sitecode);
        sendDetail.setReceiveSiteCode(sitecode);
        return getLastSendDetailDate(sendDatailDao.getSendSiteID(sendDetail));
    }

    /**
     * 比较时间大小
     *
     * @param SendDatailList
     */
    public SendDetail getLastSendDetailDate(List<SendDetail> SendDatailList) {
        SendDetail tSendDatail = null;
        if (SendDatailList != null && !SendDatailList.isEmpty()) {
            for (SendDetail dSendDatail : SendDatailList) {
                if (tSendDatail == null) {
                    tSendDatail = dSendDatail;
                } else if (tSendDatail.getOperateTime().getTime() < dSendDatail.getOperateTime().getTime()) {
                    tSendDatail = dSendDatail;
                }
            }
        }
        return tSendDatail;
    }

    public boolean sendMTooldtms(List<SendM> tSendMList) {
        SendM tSendM = tSendMList.get(0);
        Set<ShouHuoInfo> shouHuoes = new HashSet<ShouHuoInfo>();
        ShouHuoInfo tShouHuoInfo = new ShouHuoInfo();
        tShouHuoInfo.setBatchId(tSendM.getSendCode());
        tShouHuoInfo.setUuId(tSendM.getSendCode());
        tShouHuoInfo.setCreateTime(tSendM.getCreateTime());
        if (tSendM.getCarCode() != null) {
            tShouHuoInfo.setCarNo(tSendM.getCarCode());
        } else {
            tShouHuoInfo.setCarNo("");
        }
        tShouHuoInfo.setCallCode("360BUY.BJ.A");
        this.allBoxInfo(tSendMList, tShouHuoInfo);
        if (tShouHuoInfo.getBoxInfoList() == null
                || tShouHuoInfo.getBoxInfoList().isEmpty()) {
            this.logger.info("发货数据--------BoxInfoList参数不全");
        } else {
            shouHuoes.add(tShouHuoInfo);
            String requestXmls = XmlHelper
                    .objectToXml(tShouHuoInfo, new ShouHuoConverter());

            if (StringHelper.isNotEmpty(requestXmls)) {
                Result result = this.dmsToTmsWebService.shouHuoService(requestXmls);
                this.logger.info(result.getResultCode());
                this.logger.info(result.getResultMessage());
                if (result.getResultCode() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void allBoxInfo(List<SendM> tSendMList, ShouHuoInfo tShouHuoInfo) {
        List<BoxInfo> tBoxInfoList = new ArrayList<BoxInfo>();
        if (tSendMList != null && !tSendMList.isEmpty()) {
            for (SendM dSendM : tSendMList) {
                BoxInfo tBoxInfo = new BoxInfo();
                tBoxInfo.setBatchId(dSendM.getSendCode());
                tBoxInfo.setBoxId(dSendM.getBoxCode());
                BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(dSendM
                        .getReceiveSiteCode());
                if (bDto != null) {
                    tBoxInfo.setSendType(String.valueOf(bDto.getSiteType()));
                    tBoxInfo.setSendName(bDto.getSiteName());
                } else {
                    tBoxInfo.setSendType("");
                    tBoxInfo.setSendName("");
                }
                if (dSendM.getSendUserCode() != null) {
                    tBoxInfo.setCarrierId(String.valueOf(dSendM.getSendUserCode()));
                } else {
                    tBoxInfo.setCarrierId("");
                }
                if (dSendM.getSendUser() != null) {
                    tBoxInfo.setCarrierName(dSendM.getSendUser());
                } else {
                    tBoxInfo.setCarrierName("");
                }

                tBoxInfo.setOperatorId(String.valueOf(dSendM.getCreateUserCode()));
                tBoxInfo.setOperatorName(dSendM.getCreateUser());
                tBoxInfo.setSendId(String.valueOf(dSendM.getReceiveSiteCode()));
                this.allOrderInfo(tBoxInfo);
                if (tBoxInfo.getOrderInfoList() == null || tBoxInfo.getOrderInfoList().isEmpty()) {
                    this.logger.info("DMC数据同步-------OrderInfoList参数不全");
                    continue;
                }
                tBoxInfoList.add(tBoxInfo);
            }
        }

        tShouHuoInfo.setBoxInfoList(tBoxInfoList);
    }

    private void allOrderInfo(BoxInfo tBoxInfo) {
        SendDetail tSendDatail = new SendDetail();
        tSendDatail.setBoxCode(tBoxInfo.getBoxId());
        List<OrderInfo> tOrderInfoList = new ArrayList<OrderInfo>();
        List<SendDetail> tSendDatailList = this.sendDatailDao
                .querySendDatailsByBoxCode(tSendDatail);
        if (tSendDatailList != null && !tSendDatailList.isEmpty()) {
            Map<String, SendDetail> sendDatailMap = new HashMap<String, SendDetail>();
            Set<String> waybillset = new HashSet<String>();
            for (SendDetail dSendDatail : tSendDatailList) {
                waybillset.add(dSendDatail.getWaybillCode());
                sendDatailMap.put(dSendDatail.getWaybillCode(), dSendDatail);
            }
            List<String> waybillList = new CollectionHelper<String>().toList(waybillset);
            WChoice queryWChoice = new WChoice();
            queryWChoice.setQueryPackList(true);
            queryWChoice.setQueryWaybillC(true);
            List<BigWaybillDto> tWaybillList = getWaillCodeListMessge(queryWChoice, waybillList);
            if (tWaybillList != null && !tWaybillList.isEmpty()) {
                for (BigWaybillDto tWaybill : tWaybillList) {
                    if (tWaybill != null && tWaybill.getWaybill() != null) {
                        SendDetail dSendDatail = sendDatailMap.get(tWaybill.getWaybill().getWaybillCode());
                        OrderInfo tOrderInfo = new OrderInfo();

                        Waybill waybill = tWaybill.getWaybill();
                        List<DeliveryPackageD> deliveryPackage = tWaybill.getPackageList();
                        tOrderInfo.setOrderId(dSendDatail.getWaybillCode());
                        tOrderInfo.setOrderType((waybill == null || waybill.getWaybillType() == null) ? null : String.valueOf(waybill.getWaybillType()));
                        tOrderInfo.setOrderSource(String.valueOf(dSendDatail.getCreateSiteCode()));
                        tOrderInfo.setPackNum((waybill == null || waybill.getGoodNumber() == null) ? 1 : waybill.getGoodNumber());
                        tOrderInfo.setBoxId(dSendDatail.getBoxCode());
                        tOrderInfo.setOrderAdd((waybill == null || waybill.getReceiverAddress() == null) ? null : waybill.getReceiverAddress());
                        tOrderInfo.setRemark((waybill == null || waybill.getGoodWeight() == null) ? null : String.valueOf(waybill.getGoodWeight()));
                        Integer siteId = 0;
                        if (waybill != null && waybill.getOldSiteId() != null)
                            siteId = waybill.getOldSiteId();
                        tOrderInfo.setZdId(String.valueOf(siteId));
                        String createDate = DateHelper.formatDate(tSendDatail.getOperateTime(),
                                Constants.DATE_TIME_FORMAT);
                        tOrderInfo.setCreateTime(createDate);
                        tOrderInfo.setBatchId(tBoxInfo.getBatchId());
                        tOrderInfo.setDispatchType((waybill == null || waybill.getWaybillType() == null) ? "0" : String.valueOf(waybill.getDistanceType()));
                        this.allPackInfo(tSendDatailList, tOrderInfo, deliveryPackage);
                        if (tOrderInfo.getPackInfoList() == null
                                || tOrderInfo.getPackInfoList().isEmpty()) {
                            continue;
                        }
                        tOrderInfoList.add(tOrderInfo);
                    }
                }
            }
            tBoxInfo.setOrderInfoList(tOrderInfoList);
        }
    }

    private void allPackInfo(List<SendDetail> tSendDatailList, OrderInfo tOrderInfo, List<DeliveryPackageD> deliveryPackage) {
        List<PackInfo> list = new ArrayList<PackInfo>();
        for (SendDetail dSendDatail : tSendDatailList) {
            if (tOrderInfo.getOrderId().equals(dSendDatail.getWaybillCode())) {
                PackInfo tPackInfo = new PackInfo();
                tPackInfo.setPackNo(dSendDatail.getPackageBarcode());
                tPackInfo.setPackWeight(BigDecimal.valueOf(0.0));
                if (deliveryPackage != null && !deliveryPackage.isEmpty()
                        && BusinessHelper.checkIntNumRange(deliveryPackage.size())) {
                    for (DeliveryPackageD delivery : deliveryPackage) {
                        if (delivery.getPackageBarcode().equals(dSendDatail.getPackageBarcode()) && delivery.getAgainWeight() != null) {
                            tPackInfo.setPackWeight(BigDecimal.valueOf(delivery.getAgainWeight()));
                        }
                    }
                }
                tPackInfo.setExpNo(dSendDatail.getWaybillCode());
                list.add(tPackInfo);
            }
        }
        tOrderInfo.setPackInfoList(list);
    }

    public List<BigWaybillDto> getWaillCodeListMessge(WChoice queryWChoice, List<String> waybillCodes) {
        List<BigWaybillDto> datalist = new ArrayList<BigWaybillDto>();
        try {
            if (waybillCodes != null && !waybillCodes.isEmpty()) {
                int n = waybillCodes.size() / 50;
                int m = waybillCodes.size() % 50;
                if (n > 0) {
                    List<String> waybills = new ArrayList<String>();
                    int num = 0;
                    for (String code : waybillCodes) {
                        num++;
                        waybills.add(code);
                        if (num / 50 > 0 && num % 50 == 0 && n > 0) {
                            getWaybillResult(datalist, queryWChoice, waybills);
                            waybills = new ArrayList<String>();
                            n--;
                        } else if (n == 0 && m > 0 && waybillCodes.size() == num) {
                            getWaybillResult(datalist, queryWChoice, waybills);
                        }
                    }
                } else {
                    getWaybillResult(datalist, queryWChoice, waybillCodes);
                }
            }
        } catch (Exception e) {
            logger.error("取件单基础信息调用异常-------");
        }
        return datalist;
    }

    private void getWaybillResult(List<BigWaybillDto> datalist, WChoice queryWChoice, List<String> waybills) {
        BaseEntity<List<BigWaybillDto>> results = waybillQueryApi.getDatasByChoice(waybills, queryWChoice);
        if (results != null && results.getResultCode() > 0) {
            logger.info("调用运单接口返回信息" + results.getResultCode() + "-----" + results.getMessage());
            List<BigWaybillDto> datas = results.getData();
            if (datas != null && !datas.isEmpty()) {
                for (BigWaybillDto dto : datas) {
                    datalist.add(dto);
                }
            }
        }
    }

    /**
     * 根据包裹号、箱号、创建站点（分拣中心）、接收站点来判断
     * send_type=30表示三方
     * is_cancel=0表示未取消分拣或者发货
     *
     * @param sendDetail
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public boolean checkSendByPackage(SendDetail sendDetail) {
        //step 1.判断箱子是否发货send_m
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(sendDetail.getCreateSiteCode());
        sendM.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        sendM.setBoxCode(sendDetail.getBoxCode());
        sendM.setSendType(sendDetail.getSendType());
        if (sendMDao.checkSendByBox(sendM)) {//主表，记录箱子的发货信息
            //step 2.判断包裹是否发货
            return sendDatailDao.checkSendByPackage(sendDetail);
        }
        return false;
    }

    public List<SendDetail> findOrder(SendDetail sendDetail) {
        return sendDatailDao.findOrder(sendDetail);
    }

    /**
     * 快运发货校验运单包裹不齐
     * @param sendMList
     * @return
     */
    public ThreeDeliveryResponse checkThreePackageForKY(List<SendM> sendMList){
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        getAllList(sendMList, allList);
        //1.判断发货数据是否包含派车单并进行派车单运单不齐校验
        DeliveryResponse scheduleWaybillResponse = new DeliveryResponse();
        scheduleWaybillResponse.setCode(DeliveryResponse.CODE_OK);
        if(!businessType.equals(20)){    //非逆向才进行派车单运单齐全校验
            logger.info("发货数据判断运单是否不全");
            checkScheduleWaybill(allList, scheduleWaybillResponse);    //发货请求是否包含派车单
        }
        //2.发货数据判断包裹是否不全
        this.logger.info("发货数据判断包裹是否不全");
        if (businessType.equals(20)) {
            tDeliveryResponse =  reverseComputer.compute(allList, true);    //逆向不处理派车单发货的情况
        } else {
            tDeliveryResponse =  forwardComputer.compute(allList, true);
        }
        //派车单发货不齐不返回明细数据
        String msg = tDeliveryResponse != null && !tDeliveryResponse.isEmpty() ? DeliveryResponse.MESSAGE_SCHEDULE_PACKAGE_INCOMPLETE : "";
        if(!DeliveryResponse.CODE_OK.equals(scheduleWaybillResponse.getCode())){
            msg = StringUtils.isNotBlank(msg) ? "运单/" + msg : scheduleWaybillResponse.getMessage();
        }
        if(StringUtils.isNotBlank(msg)){
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE, msg, null);
        }else{
            return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
        }
    }

    /**
     * 老发货校验服务，校验包裹不齐
     * @param sendMList
     * @return
     */
    @SuppressWarnings("rawtypes")
    public ThreeDeliveryResponse checkThreePackage(List<SendM> sendMList) {
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        this.logger.info("发货数据判断包裹是否不全");
        getAllList(sendMList, allList);
        if (businessType.equals(20)) {
            tDeliveryResponse = reverseComputer.compute(allList, false);
        } else {
            tDeliveryResponse = forwardComputer.compute(allList, false);
        }
        if (tDeliveryResponse != null && !tDeliveryResponse.isEmpty()) {
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_THREE_SORTING,
                    DeliveryResponse.MESSAGE_Delivery_THREE_SORTING, tDeliveryResponse);
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
        }
    }

    /**
     * 获取发货明细中派车单号与运单号的对应关系
     * @param allList
     * @param scheduleWaybillResponse
     * @return
     */
    private boolean checkScheduleWaybill(List<SendDetail> allList, DeliveryResponse scheduleWaybillResponse){
        scheduleWaybillResponse.setCode(DeliveryResponse.CODE_OK);
        //获取派车单和箱号的对应关系
        Map<String/*scheduleCode*/, Set<String>/*Set<waybillCode>*/> scheduleWaybillCodeMap = getScheduleCodeWithBoxCode(allList);
        boolean isScheduleRequst = scheduleWaybillCodeMap.size() > 0 ;
        if(isScheduleRequst){//有派车单的箱子
            //依次校验各个派车单下已分拣运单是否齐全
            for (Map.Entry<String, Set<String>> entry : scheduleWaybillCodeMap.entrySet()) {
                String scheduleCode = entry.getKey();
                Set<String> waybillSet = entry.getValue();
                List<String> scheduleWaybillCodelist = transbillMService.getEffectWaybillCodesByScheduleBillCode(scheduleCode);
                if(scheduleWaybillCodelist != null && scheduleWaybillCodelist.size() != waybillSet.size()){//此派车单运单不齐
                    scheduleWaybillResponse.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                    scheduleWaybillResponse.setMessage(DeliveryResponse.MESSAGE_SCHEDULE_WAYBILL_INCOMPLETE);
                    break;
                }
            }
        }

        return isScheduleRequst;   //返回此发货请求是否含派车单号
    }

    /**
     * 由于一个派车单可以装多个箱子，这里获取派车单下所有发货的运单
     * @param allList
     * @return
     */
    private Map<String, Set<String>> getScheduleCodeWithBoxCode(List<SendDetail> allList){
        //其实可以不排序（生成的时候是按箱依次添加的）
        Collections.sort(allList, new Comparator<SendDetail>() {
            @Override
            public int compare(SendDetail lhs, SendDetail rhs) {
                return lhs.getBoxCode().compareToIgnoreCase(rhs.getBoxCode());
            }
        });
        Map<String/*scheduleCode*/, Set<String>/*Set<waybillCode>*/> scheduleCodeWaybillCodeMap = new HashMap<String, Set<String>>();
        String lastBoxCode = "";
        String scheduleBCode = null;
        for (SendDetail sendDetail : allList) {
            if (BusinessHelper.isBoxcode(sendDetail.getBoxCode())) {
                String boxCode = StringUtils.trim(sendDetail.getBoxCode());
                String wayBillCode = sendDetail.getWaybillCode();
                if(!lastBoxCode.equals(boxCode)){//下一箱
                    lastBoxCode = boxCode;
                    scheduleBCode = getScheduleCode(boxCode, wayBillCode);
                }
                if(StringUtils.isNotBlank(scheduleBCode)){
                    putValueToMap(scheduleCodeWaybillCodeMap, scheduleBCode, wayBillCode);
                }
            }else if (BusinessHelper.isPackageCode(sendDetail.getBoxCode())) {
                String wayBillCode = BusinessHelper.getWaybillCode(sendDetail.getBoxCode());
                String scheduleWCode = getScheduleCode(null, wayBillCode);
                //原包发货的包裹是派车单的包裹
                if(StringUtils.isNotBlank(scheduleWCode)){
                    putValueToMap(scheduleCodeWaybillCodeMap, scheduleWCode, wayBillCode);
                }
            }
        }
        return scheduleCodeWaybillCodeMap;
    }

    /**
     * 获取派车单号
     * @param boxCode
     * @param wayBillCode
     * @return
     */
    private String getScheduleCode(String boxCode, String wayBillCode){
        String scheduleCode = null;
        if(StringUtils.isNotBlank(boxCode)){    //1.缓存读
            scheduleCode = transBillScheduleService.getKey(boxCode);
        }
        if((StringUtils.isBlank(scheduleCode) || Constants.SCHEDULE_CODE_DEFAULT.equals(scheduleCode)) && StringUtils.isNotBlank(wayBillCode)){
            scheduleCode = transBillScheduleService.queryScheduleCode(wayBillCode);
        }
        if(StringUtils.isBlank(scheduleCode) || Constants.SCHEDULE_CODE_DEFAULT.equals(scheduleCode)){
            scheduleCode = null;
        }
        return scheduleCode;
    }

    /**
     * Map的value为集合时的put方法
     * @param map
     * @param key
     * @param aValue
     */
    private static void putValueToMap(Map<String, Set<String>> map, String key, String aValue){
        if (map.containsKey(key)) {
            map.get(key).add(aValue);
        } else {
            Set<String> temp = new HashSet<String>();
            temp.add(aValue);
            map.put(key, temp);
        }
    }

    /**
     * 获取所有发货明细
     * @param sendMList
     * @param allList
     */
    public void getAllList(List<SendM> sendMList, List<SendDetail> allList) {
        for (SendM tSendM : sendMList) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tSendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);

            if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
                List<SendDetail> oneList = sendDatailDao.querySendDatailsBySelective(tSendDatail);
                if (oneList != null && !oneList.isEmpty()) {
                    for (SendDetail dSendDatail : oneList) {
                        if (!BusinessHelper.isPickupCode(dSendDatail.getPackageBarcode()))
                            allList.add(dSendDatail);
                    }
                }
            } else if (BusinessHelper.isPackageCode(tSendM.getBoxCode())) {
                tSendDatail.setPackageBarcode(tSendM.getBoxCode());
                tSendDatail.setWaybillCode(BusinessHelper.getWaybillCode(tSendM.getBoxCode()));
                if (!BusinessHelper.isPickupCode(tSendM.getBoxCode()))
                    allList.add(tSendDatail);
            }
        }
    }


    @SuppressWarnings("rawtypes")
    public List<SendThreeDetail> checkThreePackage4Cancel(List<SendM> sendMList) {
        // 发货验证一单多件是否齐全
        Integer createSiteCode = sendMList.size() > 0 ? sendMList.get(0)
                .getCreateSiteCode() : 0;
        Integer receiveSiteCode = sendMList.size() > 0 ? sendMList.get(0)
                .getReceiveSiteCode() : 0;

        List<SendDetail> allList = new ArrayList<SendDetail>();
        List<SendThreeDetail> lostList = new ArrayList<SendThreeDetail>();
        Map<String, Integer> numMap = new HashMap<String, Integer>();
        getAllList(sendMList, allList);

        if (allList != null && !allList.isEmpty()) {
            getNumMap(allList, numMap);

            for (Iterator<Entry<String, Integer>> it = numMap.entrySet()
                    .iterator(); it.hasNext(); ) {

                Map.Entry entry = (Map.Entry) it.next();
                String waybillcode = (String) entry.getKey();
                Integer numCount = (Integer) entry.getValue();

                int num = 0;
                List<String> packlist = new ArrayList<String>();
                Map<String, String> sendMap = new HashMap<String, String>();
                Set<String> pbList = new HashSet<String>();
                num = getNumCount(allList, waybillcode, num, packlist, sendMap,
                        pbList);
                if (numCount != num) {
                    List<String> codeList = getPackageCode(packlist);
                    if (codeList != null && !codeList.isEmpty()) {

                        for (String packageBarcode : codeList) {
                            List<SendDetail> mlist = queryBoxCodeBypackageCode(
                                    packageBarcode, createSiteCode,
                                    receiveSiteCode);
                            if (mlist != null && !mlist.isEmpty()) {
                                for (SendDetail rendDatail : mlist) {
                                    SendThreeDetail mSend = new SendThreeDetail();
                                    mSend.setPackageBarcode(packageBarcode);
                                    mSend.setBoxCode(rendDatail.getBoxCode());
                                    lostList.add(mSend);
                                }
                            } else {
                                SendThreeDetail mSend = new SendThreeDetail();
                                mSend.setPackageBarcode(packageBarcode);
                                mSend.setBoxCode("");
                                lostList.add(mSend);
                            }
                        }
                    }
                }
            }
        }
        return lostList;
    }

    private int getNumCount(List<SendDetail> allList, String waybillcode,
                            int num, List<String> packlist, Map<String, String> sendMap,
                            Set<String> pbList) {
        for (SendDetail dSendDatail : allList) {
            if (dSendDatail.getWaybillCode().equals(waybillcode)
                    && !pbList.contains(dSendDatail.getPackageBarcode())) {
                pbList.add(dSendDatail.getPackageBarcode());
                num++;
                packlist.add(dSendDatail.getPackageBarcode());
                sendMap.put(waybillcode, dSendDatail.getBoxCode());
            }
        }
        return num;
    }

    private void getNumMap(List<SendDetail> allList, Map<String, Integer> numMap) {
        for (SendDetail dSendDatail : allList) {
            numMap.put(dSendDatail.getWaybillCode(), getPackageNum(dSendDatail.getPackageBarcode()));
        }
    }

    /*******************
     * 根据包裹和站点查询箱号
     ******************************************/
    private List<SendDetail> queryBoxCodeBypackageCode(String packageCode,
                                                       Integer createSite, Integer receiveSite) {
        SendDetail sendDatail = new SendDetail();
        sendDatail.setPackageBarcode(packageCode);
        sendDatail.setCreateSiteCode(createSite);
        sendDatail.setReceiveSiteCode(receiveSite);
        sendDatail.setIsCancel(0);
        List<SendDetail> resultList = sendDatailDao.querySendDatailsBySelective(sendDatail);
        return resultList;
    }

    /*************************************************************/
    public int getPackageNum(String packageBarcode) {
        int sum = 1;
        if (packageBarcode.indexOf("S") > 0 && packageBarcode.indexOf("H") > 0) {
            sum = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("S") + 1, packageBarcode.indexOf("H")));
        } else if (packageBarcode.indexOf("-") > 0 && (packageBarcode.split("-").length == 3 || packageBarcode.split("-").length == 4)) {
            sum = Integer.valueOf(packageBarcode.split("-")[2]);
        }
        if (sum > BusinessHelper.getMaxNum()) {
            sum = BusinessHelper.getMaxNum();
        }
        return sum;
    }

    /*******************
     * 发货已扫描数据
     ******************************************/
    @Deprecated
    private Set<String> getDeliveryPackageCode(List<String> packlist, List<String> code) {
        Set<String> codeList = new HashSet<String>();
        for (String packageBarcode : packlist) {
            for (String codepackage : code) {
                if (!packageBarcode.equals(codepackage)) {
                    codeList.add(packageBarcode);
                }
            }
        }
        return codeList;
    }

    /*******************
     * 未扫描数据
     ******************************************/
    public List<String> getPackageCode(List<String> packlist) {
        List<String> codeList = new ArrayList<String>();
        int num = 1;
        for (String packageBarcode : packlist) {
            if (packageBarcode.indexOf("S") > 0 && packageBarcode.indexOf("H") > 0) {
                num = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("S") + 1, packageBarcode.indexOf("H")));
                if (num > BusinessHelper.getMaxNum()) {
                    num = BusinessHelper.getMaxNum();
                }
                for (int i = 1; i <= num; i++) {
                    String newpackage = packageBarcode.substring(0, packageBarcode.indexOf("N") + 1)
                            + i
                            + packageBarcode.substring(packageBarcode.indexOf("S"), packageBarcode.length());
                    codeList.add(newpackage);
                }
            } else if (packageBarcode.indexOf("-") > 0 && (packageBarcode.split("-").length == 3 || packageBarcode.split("-").length == 4)) {
                num = Integer.valueOf(packageBarcode.split("-")[2]);
                if (num > BusinessHelper.getMaxNum()) {
                    num = BusinessHelper.getMaxNum();
                }
                for (int i = 1; i <= num; i++) {
                    StringBuffer newpackage = new StringBuffer();
                    newpackage.append(packageBarcode.split("-")[0]);
                    newpackage.append("-");
                    newpackage.append(i);
                    newpackage.append("-");
                    newpackage.append(packageBarcode.split("-")[2]);
                    newpackage.append("-");
                    if (packageBarcode.split("-").length == 4) {
                        newpackage.append(packageBarcode.split("-")[3]);
                    }
                    codeList.add(newpackage.toString());
                }
            }
            break;
        }
        for (String packageBarcode : packlist) {
            codeList.remove(packageBarcode);
        }
        return codeList;
    }

    @Override
    public List<SendDetail> findDeliveryPackageBySite(SendDetail sendDetail) {
        return sendDatailDao.findDeliveryPackageBySite(sendDetail);
    }

    @Override
    public List<SendDetail> findDeliveryPackageByCode(SendDetail sendDetail) {
        return sendDatailDao.findDeliveryPackageByCode(sendDetail);
    }

    @Override
    public List<SendDetail> findWaybillStatus(List<String> queryCondition) {
        logger.info("findWaybillStatus查询");
        return sendDatailReadDao.findUpdatewaybillCodeMessage(queryCondition);
    }

    @Override
    public List<SendM> queryCountByBox(SendM sendM) {
        return sendMDao.selectBySendSiteCode(sendM);
    }

    /**
     * 补全包裹重量
     */
    @Override
    public SendDetail measureRetrieve(SendDetail sendDetail) {

        //一单多件调用接口获取包裹欣慰为空处理
        BaseEntity<List<DeliveryPackageD>> waybillWSRs = new BaseEntity<List<DeliveryPackageD>>();
        List<DeliveryPackageD> datas = null;
        try {
            //logger.info("调用运单queryPackageListForParcodes调用参数"+sendDetail.getPackageBarcode());
            waybillWSRs = waybillPackageApi.queryPackageListForParcodes(
                    Arrays.asList(new String[]{sendDetail.getPackageBarcode()}));
            if (waybillWSRs != null) {
                datas = waybillWSRs.getData();
            }

            if (null != datas && !datas.isEmpty() && null != datas.get(0) && null != datas.get(0).getGoodWeight()) {
                sendDetail.setWeight(datas.get(0).getGoodWeight());
            }
        } catch (Exception e) {
            //如果重量写入失败不影响分拣的结果
            logger.error("调用运单queryPackageListForParcodes接口时候失败", e);
        }

        return sendDetail;
    }

    /**
     * 一单多件包裹不全验证
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    public List<SendThreeDetail> checkSortingDiff(String boxCode, Integer createSiteCode, Integer receiveSiteCode) {

        List<SendDetail> allList = new ArrayList<SendDetail>();
        List<SendThreeDetail> lostList = new ArrayList<SendThreeDetail>();
        Map<String, Integer> numMap = new HashMap<String, Integer>();

        if ((boxCode != null) && (!boxCode.trim().equals(""))) {
            SendDetail queryDetail = new SendDetail();
            queryDetail.setBoxCode(boxCode);
            queryDetail.setCreateSiteCode(createSiteCode);
            List<SendDetail> oneList = sendDatailDao.querySortingDiff(queryDetail);
            if (oneList != null && !oneList.isEmpty()) {
                for (SendDetail dSendDatail : oneList) {
                    allList.add(dSendDatail);
                }
            }
        } else {
            SendDetail queryDetail = new SendDetail();
            queryDetail.setCreateSiteCode(createSiteCode);
            queryDetail.setReceiveSiteCode(receiveSiteCode);
            List<SendDetail> oneList = sendDatailDao.querySortingDiff(queryDetail);
            if (oneList != null && !oneList.isEmpty()) {
                for (SendDetail dSendDatail : oneList) {
                    allList.add(dSendDatail);
                }
            }
        }

        if (allList != null && !allList.isEmpty()) {
            for (SendDetail dSendDatail : allList) {
                numMap.put(dSendDatail.getWaybillCode(), getPackageNum(dSendDatail.getPackageBarcode()));
            }

            for (Iterator<Entry<String, Integer>> it = numMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
                String waybillcode = (String) entry.getKey();
                Integer numCount = (Integer) entry.getValue();

                int num = 0;
                List<String> packlist = new ArrayList<String>();
                List<SendThreeDetail> duplicatedPacklist = new ArrayList<SendThreeDetail>();
                Map<String, String> sendMap = new HashMap<String, String>();
                Set<String> packset = new HashSet<String>();
                for (SendDetail dSendDatail : allList) {
                    if (dSendDatail.getWaybillCode().equals(waybillcode)) {
                        if (!packset.contains(dSendDatail.getPackageBarcode())) {
                            packset.add(dSendDatail.getPackageBarcode());
                            num++;
                            packlist.add(dSendDatail.getPackageBarcode());
                            sendMap.put(waybillcode, dSendDatail.getBoxCode());
                        }
                        SendThreeDetail sendThreeDetail = new SendThreeDetail();
                        sendThreeDetail.setPackageBarcode(dSendDatail.getPackageBarcode());
                        sendThreeDetail.setBoxCode(dSendDatail.getBoxCode());
                        sendThreeDetail.setMark("已扫描");
                        duplicatedPacklist.add(sendThreeDetail);
                    }
                }

                if (numCount != num) {
                    if (duplicatedPacklist != null) {
                        lostList.addAll(duplicatedPacklist);
                    }
                    //未在本批次的
                    List<String> codeList = getPackageCode(packlist);
                    if (codeList != null && !codeList.isEmpty()) {
                        for (String packageBarcode : codeList) {
                            SendThreeDetail sendThreeDetail = new SendThreeDetail();
                            sendThreeDetail.setPackageBarcode(packageBarcode);
                            sendThreeDetail.setBoxCode("");
                            sendThreeDetail.setMark("未扫描");
                            lostList.add(sendThreeDetail);
                        }
                    }
                }
            }
        }
        return lostList;
    }

    /**
     * 发货明细表包裹数量补全
     * 默认补全两天的数据
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    public Integer appendPackageNum(String boxCode, Integer createSiteCode,
                                    Integer receiveSiteCode) {

        SendDetail queryDetail = new SendDetail();
        queryDetail.setBoxCode(boxCode);
        queryDetail.setCreateSiteCode(createSiteCode);
        queryDetail.setReceiveSiteCode(receiveSiteCode);
        List<SendDetail> allList = sendDatailDao.queryWithoutPackageNum(queryDetail);

        Collections.sort(allList);
        Integer count = 0;
        if (allList != null && !allList.isEmpty()) {
            for (SendDetail record : allList) {
                count++;
                Integer packageNum = record.getPackageNum();
                Integer validPackageNum = null;
                if (packageNum == null) {
                    validPackageNum = getPackageNum(record.getPackageBarcode());
                }
                if (validPackageNum != null) {
                    record.setPackageNum(validPackageNum);
                    sendDatailDao.updatePackageNum(record);
                }
            }
        }
        return count;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean transitSend(SendM domain) {
        if (isTransferSend(domain)) {
            pushTransferSendTask(domain);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前发货是否为中转发货
     * 1:装箱、正向、逆向发货
     * 2.1:发货起始站与箱子起始站不同
     * 2.2:发货起始站与箱子起始站相同，但目的站不同，且发货目的站必须是分拣中心
     *
     * @param domain 发货对象
     * @return
     */
    @Override
    public boolean isTransferSend(SendM domain) {
        if (!BusinessHelper.isBoxcode(domain.getBoxCode()))
            return false;
        if (!domain.getSendType().equals(businessTypeONE) && !domain.getSendType().equals(businessTypeTWO)) {
            return false;
        }
        if (domain.getReceiveSiteCode() == null || domain.getCreateSiteCode() == null) {
            return false;
        }
        String sendReceiveSiteType = "";
        BaseStaffSiteOrgDto yrDto = this.baseMajorManager.getBaseSiteBySiteId(domain.getReceiveSiteCode());
        if (yrDto != null) {
            sendReceiveSiteType = String.valueOf(yrDto.getSiteType());
        }
        Box box = this.boxService.findBoxByCode(domain.getBoxCode());
        if (null == box || null == box.getCreateSiteCode() || null == box.getReceiveSiteCode()) {
            return false;
        }
        return (!domain.getCreateSiteCode().equals(box.getCreateSiteCode()))
                || (domain.getCreateSiteCode().equals(box.getCreateSiteCode())
                && !domain.getReceiveSiteCode().equals(box.getReceiveSiteCode())
                && sendReceiveSiteType.equals("64")
        );
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void pushTransferSendTask(SendM domain) {
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(domain.getSendCode());
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("5");//5 中转发货补全数据
        tTask.setFingerprint(Md5Helper.encode(domain.getBoxCode() + "_" + domain.getCreateSiteCode() + "_" + domain.getReceiveSiteCode() + "-" + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
        logger.info("插入中转发车任务" + JsonHelper.toJson(tTask));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWORKER.DeliveryService.findTransitSend", mState = {JProEnum.TP})
    public boolean findTransitSend(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null
                || task.getCreateSiteCode() == null
                || task.getKeyword2() == null
                || task.getReceiveSiteCode() == null)
            return true;
        Integer bCreateSiteCode = task.getCreateSiteCode();
        Integer bReceiveSiteCode = task.getReceiveSiteCode();
        String boxCode = task.getBoxCode();
        List<SendDetail> list = getSendByBox(boxCode);

        if (list != null && !list.isEmpty()) {
            for (SendDetail tsendDatail : list) {
                tsendDatail.setCreateSiteCode(bCreateSiteCode);
                tsendDatail.setReceiveSiteCode(bReceiveSiteCode);
                if ((!tsendDatail.getBoxCode().equals(task.getBody()))
                        && (!StringHelper.isEmpty(task.getBody()))
                        && task.getBody().contains("-")) {
                    tsendDatail.setSendCode(task.getBody());
                    tsendDatail.setYn(1);
                }
                this.saveOrUpdateCancel(tsendDatail, task.getCreateTime());
                if ((!tsendDatail.getBoxCode().equals(task.getBody()))
                        && (!StringHelper.isEmpty(task.getBody()))
                        && task.getBody().contains("-")) {
                    try {
                        SendM sendM = new SendM();
                        sendM.setCreateSiteCode(bCreateSiteCode);
                        sendM.setBoxCode(boxCode);
                        sendM.setReceiveSiteCode(bReceiveSiteCode);
                        List<SendM> sendMs = sendMDao.findSendMByBoxCode(sendM);
                        if (null != sendMs && !sendMs.isEmpty()) {
                            logger.warn("find senm from db success value <"
                                    + JsonHelper.toJson(sendMs.get(0)) + ">");
                            SendM s = sendMs.get(0);
                            tsendDatail.setOperateTime(s.getOperateTime());
                            tsendDatail.setCreateUser(s.getCreateUser());
                            tsendDatail.setCreateUserCode(s.getCreateUserCode());
                        }
                    } catch (Throwable e) {
                        logger.error("发货全程跟踪", e);
                    }
                    List<SendDetail> sendDetails = new ArrayList<SendDetail>(1);
                    sendDetails.add(tsendDatail);
                    this.updateWaybillStatus(sendDetails);     // 回传发货全程跟踪
                }
            }
        }
        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveOrUpdateCancel(SendDetail sendDetail, Date createTime) {
        logger.info("WORKER处理中转发货-插入SEND—D表" + JsonHelper.toJson(sendDetail));
        sendDetail.setOperateTime(createTime);
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
//		注释掉是因为：数据插入的时候就已经是初使状态了，如果已经有数据的话，再刷一次没有作用。另外之前也做过update了，
//		else {
//			this.updateCancel(sendDetail);
//		}
    }

    public List<SendDetail> getSendByBox(String boxCode) {
        Box box = null;
        box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return null;
        }
        Integer yCreateSiteCode = box.getCreateSiteCode();
        Integer yReceiveSiteCode = box.getReceiveSiteCode();
        SendDetail tsendDatail = new SendDetail();
        tsendDatail.setBoxCode(boxCode);
        tsendDatail.setCreateSiteCode(yCreateSiteCode);
        tsendDatail.setReceiveSiteCode(yReceiveSiteCode);
        tsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
        List<SendDetail> sendDatailist = this.sendDatailDao
                .querySendDatailsBySelective(tsendDatail);
        if (sendDatailist == null || sendDatailist.isEmpty()) {
            SendInfoDto sendInfoDto = new SendInfoDto();
            sendInfoDto.setBoxCode(boxCode);
            com.jd.etms.erp.service.domain.BaseEntity<List<SendInfoDto>> baseEntity = supportProxy
                    .getSendDetails(sendInfoDto);
            if (baseEntity != null && baseEntity.getResultCode() > 0) {
                List<SendInfoDto> datas = baseEntity.getData();
                if (datas != null && !datas.isEmpty()) {
                    for (SendInfoDto dto : datas) {
                        SendDetail dsendDatail = new SendDetail();
                        dsendDatail.setBoxCode(dto.getBoxCode());

                        dsendDatail.setWaybillCode(dto.getWaybillCode());
                        dsendDatail.setPackageBarcode(dto.getPackageBarcode());

                        if (BusinessHelper
                                .isPickupCode(dto.getPackageBarcode())) {
                            BaseEntity<PickupTask> pickup = null;
                            try {
                                pickup = this.waybillPickupTaskApi
                                        .getDataBySfCode(dto
                                                .getPackageBarcode());
                            } catch (Exception e) {
                                this.logger.error("调用取件单号信息ws接口异常");
                            }
                            if (pickup != null && pickup.getData() != null) {
                                dsendDatail.setPickupCode(pickup.getData()
                                        .getPickupCode());
                                dsendDatail.setWaybillCode(pickup.getData()
                                        .getOldWaybillCode());
                            }
                        }
                        dsendDatail.setCreateUser(dto.getOperatorName());
                        dsendDatail.setCreateUserCode(dto.getOperatorId());
                        dsendDatail.setOperateTime(dto.getHandoverDate());
                        dsendDatail.setSendType(businessTypeTWO);
                        if (dto.getPackageBarcode() != null)
                            dsendDatail.setPackageNum(getPackageNum(dto
                                    .getPackageBarcode()));
                        else
                            dsendDatail.setPackageNum(1);
                        dsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
                        sendDatailist.add(dsendDatail);
                    }
                } else {
                    logger.info("调用tms取站点箱子明细接口返回信息为空"
                            + baseEntity.getResultCode());
                }
            }

        }
        return sendDatailist;
    }

    @Override
    public List<SendDetail> queryBySendCodeAndSendType(String sendCode,
                                                       Integer sendType) {
        SendDetail queryDetail = new SendDetail();
        queryDetail.setSendCode(sendCode);
        queryDetail.setSendType(sendType);
        return this.sendDatailDao.queryBySendCodeAndSendType(queryDetail);
    }

    public Integer cancelDelivery(SendDetail sendDetail) {
        return this.sendDatailDao.cancelDelivery(sendDetail);
    }

    @Override
    public DeliveryResponse dealWithSendBatch(SendM sendM) {
        try {
            Sorting sorting = new Sorting();
            String sendCode = sendM.getSendCode();
            sorting.setBsendCode(sendCode);
            sorting.setCreateSiteCode(sendM.getCreateSiteCode());
            List<Sorting> list = tSortingService.findByBsendCode(sorting);
            if (list == null || list.isEmpty())
                return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        DeliveryResponse.MESSAGE_Delivery_NO_BATCH);
            String rsiteCode = null;
            for (Sorting tSorting : list) {
                List<SendM> sendMList = new ArrayList<SendM>();
                SendM nsendM;
                nsendM = (SendM) sendM.clone();
                nsendM.setBoxCode(tSorting.getBoxCode());
                nsendM.setSendCode(getBatchCode(sendCode, tSorting));
                nsendM.setReceiveSiteCode(tSorting.getReceiveSiteCode());
                sendMList.add(nsendM);
                DeliveryResponse response = dellDeliveryMessage(sendMList);
                if (!response.getCode().equals(DeliveryResponse.CODE_OK))
                    rsiteCode = rsiteCode + "," + String.valueOf(tSorting.getReceiveSiteCode());
            }
            if (rsiteCode != null)
                return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                        rsiteCode + "站点箱号" + DeliveryResponse.MESSAGE_Delivery_ERROR);

        } catch (Exception e) {
            logger.error("dealWithSendBatch处理异常", e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR);

        }

        return new DeliveryResponse(DeliveryResponse.CODE_OK,
                DeliveryResponse.MESSAGE_OK);
    }

    private String getBatchCode(String sendCode, Sorting tSorting) {
        int maxlength = 32;
        String code = tSorting.getCreateSiteCode() +
                Constants.SEPARATOR_HYPHEN +
                tSorting.getReceiveSiteCode() +
                Constants.SEPARATOR_HYPHEN +
                sendCode;
        //+getTimeCode();
        if (code.length() > maxlength)
            code = code.substring(0, maxlength);
        return code;
    }

    @Override
    public DeliveryResponse autoBatchSend(List<SendM> sendMList) {
        String rsiteCode = null;
        for (SendM sendM : sendMList) {
            List<SendM> sendMs = new ArrayList<SendM>();
            SendM nsendM;
            try {
                String[] boxs = sendM.getBoxCode().split(",");
                for (String boxCode : boxs) {
                    nsendM = (SendM) sendM.clone();
                    nsendM.setBoxCode(boxCode);
                    sendMs.add(nsendM);
                }

                DeliveryResponse response = dellDeliveryMessage(sendMs);
                if (!response.getCode().equals(DeliveryResponse.CODE_OK))
                    rsiteCode = rsiteCode + ","
                            + String.valueOf(sendM.getReceiveSiteCode());
            } catch (Exception e) {
                e.printStackTrace();
                return new DeliveryResponse(
                        DeliveryResponse.CODE_Delivery_ERROR,
                        DeliveryResponse.MESSAGE_Delivery_ERROR);
            }

        }
        if (rsiteCode != null) {
            rsiteCode = rsiteCode.replaceAll("null,", "");
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    rsiteCode + "站点箱号"
                            + DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
        return new DeliveryResponse(DeliveryResponse.CODE_OK,
                DeliveryResponse.MESSAGE_OK);
    }

    /**
     * 包裹不全差异对比计算
     */
    public static interface PackageDiffrence {
        /**
         * 计算差异结果
         *
         * @param list 发货明细列表
         * @param isScheduleRequest 是否包含派车单
         * @return 差异明细结果列表
         */
        List<SendThreeDetail> compute(List<SendDetail> list, boolean isScheduleRequest);
    }

    /**
     * 利用排序法计算差异
     * 1：按运单号升序排序
     */
    public static abstract class AbstructDiffrenceComputer implements PackageDiffrence {

        @Autowired
        private SendDatailDao sendDatailDao;

        @Override
        public List<SendThreeDetail> compute(List<SendDetail> list, boolean isScheduleRequest) {
            Collections.sort(list, new Comparator<SendDetail>() {
                @Override
                public int compare(SendDetail lhs, SendDetail rhs) {
                    return lhs.getPackageBarcode().compareToIgnoreCase(rhs.getPackageBarcode());
                }
            });
            return computeUsePackage(list, isScheduleRequest);
        }

        /**
         * 只保留不全订单的包裹，因为PDA操作界面太小，只应该看不全订单
         *
         * @param list
         * @return
         */
        private final List<SendThreeDetail> computeUsePackage(List<SendDetail> list, boolean isScheduleRequest) {
            String lastWaybillCode = null;
            int scanCount = 0;
            int pacageSumShoudBe = 0;
            int hasDiff = 0;
            ;
            List<SendThreeDetail> diffrenceList = new ArrayList<SendThreeDetail>();
            for (SendDetail item : list) {//遍历该箱的所有包裹
                //包含派车单且发现包裹不齐，直接退出循环（派车单校验不要明细）
                if(isScheduleRequest && hasDiff > 0){
                    break;
                }
                SendThreeDetail diff = new SendThreeDetail();
                diff.setBoxCode(item.getBoxCode());
                diff.setPackageBarcode(item.getPackageBarcode());
                diff.setMark(AbstructDiffrenceComputer.HAS_SCANED);
                diff.setIsWaybillFull(1);
                if (!item.getWaybillCode().equals(lastWaybillCode)) {//初次验单 或 每验完一单货，下一单开始验时 进入分支
                    //1.上一单已集齐 则返回0， 并重新初始化 pacageSumShoudBe、scanCount
                    //2.上一单未集齐 则返回未扫描的包裹（即缺失包裹数）循环结束后会根据此判断是否集齐包裹
                    hasDiff += invoke(pacageSumShoudBe, scanCount, diffrenceList);
                    lastWaybillCode = item.getWaybillCode();//获取当前要验证的运单号
                    pacageSumShoudBe = BusinessHelper.getPackageNum(item.getPackageBarcode());//根据运单中一个包裹的包裹号 获取包裹数量
                    scanCount = 0;
                }
                ++scanCount;//扫描计数器：1.如包裹全齐 则等于包裹总数量 2.如中间出现不齐的单，则等于不齐的单中已扫描的包裹
                diffrenceList.add(diff);//每次循环均加入结果diffrenceList，如中间某单的包裹不齐，会在invoke中将不齐的单子加入diffrenceList 以便最终结算时 获取该单的包裹（代码中该单无论缺几个，都返回该单的所有包裹）
            }
            hasDiff += invoke(pacageSumShoudBe, scanCount, diffrenceList);//遍历完成后，对该箱最后一单的未集齐包裹做处理，如最后一单已齐返回 0
            if (hasDiff > 0) {//hasDiff>0 则未集齐 需移除所有集齐的包裹 只保留未集齐的包裹 并封装list返回pda显示
                List<SendThreeDetail> targetList = removeFullPackages(diffrenceList);
                Integer createSiteCode = list.get(0).getCreateSiteCode();
                Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
                return setSortingBoxCode(createSiteCode, receiveSiteCode, targetList);
            } else {
                return null;
            }
        }


        /**
         * 设置未发货包裹的箱号[便于现场判断哪个已分拣的箱子未发货]！！！！
         * 1：已分拣包裹的箱号显示分拣箱号
         * 2：未分拣包裹的箱号显示空
         *
         * @param createSiteCode  发货站点
         * @param receiveSiteCode 收货站点
         * @param list            差异列表      差异列表
         * @return
         */
        private final List<SendThreeDetail> setSortingBoxCode(Integer createSiteCode, Integer receiveSiteCode, List<SendThreeDetail> list) {
            if (null == list || list.isEmpty())
                return list;
            List<SendThreeDetail> targetList = new ArrayList<SendThreeDetail>(list.size());
            for (SendThreeDetail item : list) {
                if (item.getMark().equals(AbstructDiffrenceComputer.HAS_SCANED)) {
                    targetList.add(item);
                } else {
                    targetList.addAll(getUnSendPackages(createSiteCode, receiveSiteCode, item.getPackageBarcode()));
                }
            }
            return targetList;
        }

        /**
         * 获取未发货包裹【若已分拣，则箱号显示已分拣箱号，否中央电视台显示空箱号表示未分拣】
         *
         * @param createSiteCode  发货站点
         * @param receiveSiteCode 接收站点
         * @param packageCode     包裹号
         * @return
         */
        private final List<SendThreeDetail> getUnSendPackages(Integer createSiteCode, Integer receiveSiteCode, String packageCode) {
            SendDetail sendDatail = new SendDetail();
            sendDatail.setPackageBarcode(packageCode);
            sendDatail.setCreateSiteCode(createSiteCode);
            sendDatail.setReceiveSiteCode(receiveSiteCode);
            sendDatail.setIsCancel(0);
            List<SendDetail> resultList = sendDatailDao.querySendDatailsBySelective(sendDatail);
            List<SendThreeDetail> list = new ArrayList<SendThreeDetail>(1);
            if (resultList != null && !resultList.isEmpty()) {
                for (SendDetail rendDatail : resultList) {
                    SendThreeDetail mSend = new SendThreeDetail();
                    mSend.setPackageBarcode(packageCode);
                    mSend.setBoxCode(rendDatail.getBoxCode());
                    mSend.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                    list.add(mSend);
                }
            } else {
                SendThreeDetail mSend = new SendThreeDetail();
                mSend.setPackageBarcode(packageCode);
                mSend.setBoxCode("");
                mSend.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                list.add(mSend);
            }
            return list;
        }

        /**
         * 从列表中去除已完全扫描[一单多件齐全]的包裹（按运单为单）
         *
         * @param list
         * @return
         */
        private final List<SendThreeDetail> removeFullPackages(List<SendThreeDetail> list) {
            if (null != list) {
                for (int index = 0; index < list.size(); ++index) {/*去除全的包裹*/
                    if (list.get(index).getIsWaybillFull() > 0) {
                        list.remove(index--);
                    }
                }
                return list;
            } else {
                return null;
            }
        }

        public abstract int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList);

        public static final String HAS_SCANED = "已扫描";

        public static final String NO_SCANEd = "未扫描";
    }

    /**
     * 正向发货差异对比
     * 1：包括自营发货及三方站点发货
     * 2：利用包裹号进行对比【差异结果不准确-当条码包裹号与运单中的不一致时】
     */
    public static class ForwardSendDiffrence extends AbstructDiffrenceComputer {
        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList) {
            int hasDiff = 0;
            if (counter != scanCount) {/* 有差异*/
                hasDiff = 1;
                List<String> geneList = SerialRuleUtil.generateAllPackageCodes(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
                for (int index = scanCount; index > 0; --index) {
                    geneList.remove(diffrenceList.get(diffrenceList.size() - index).getPackageBarcode());
                }
                List<SendThreeDetail> noScanList = new ArrayList<SendThreeDetail>(geneList.size());
                for (String packageCode : geneList) {
                    SendThreeDetail noScanDetail = new SendThreeDetail();
                    noScanDetail.setBoxCode(diffrenceList.get(diffrenceList.size() - 1).getBoxCode());
                    noScanDetail.setPackageBarcode(packageCode);
                    noScanDetail.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                    noScanDetail.setIsWaybillFull(0);
                    noScanList.add(noScanDetail);
                }
                if (noScanList.size() > 0) {
                    for (int index = scanCount; index > 0; --index) {
                        diffrenceList.get(diffrenceList.size() - index).setIsWaybillFull(0);
                    }
                }
                diffrenceList.addAll(noScanList);
            }
            return hasDiff;
        }

    }


    /**
     * 逆向发货差异对比
     * 1：针对退库房订单进行处理
     * 2：利用发货包裹明细与运单中包裹进行比对
     */
    public static class ReverseSendDiffrence extends AbstructDiffrenceComputer {

        private static final Log logger = LogFactory.getLog(ReverseSendDiffrence.class);
        @Autowired
        private WaybillCommonService waybillCommonService;

        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList) {
            int hasDiff = 0;
            if (counter != scanCount) {/* 有差异*/

                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(SerialRuleUtil.getWaybillCode(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode()));
                List<String> geneList = null;
                if (null != waybill && null != waybill.getPackList() && waybill.getPackList().size() > 0) {
                    if(waybill.getWaybillSign().charAt(33) == '2'){//病单则直接返回0 不验证包裹是否集齐
                        return 0;
                    }
                    logger.info("运单中包裹数量为" + waybill.getPackList().size());
                    geneList = new ArrayList<String>(waybill.getPackList().size());
                    for (Pack p : waybill.getPackList()) {
                        geneList.add(p.getPackCode());
                        logger.info("运单中包裹为" + p.getPackCode());
                    }
                } else {
                    logger.info("运单中没有包裹");
                    geneList = SerialRuleUtil.generateAllPackageCodes(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
                }
                for (int index = scanCount; index > 0; --index) {
                    geneList.remove(diffrenceList.get(diffrenceList.size() - index).getPackageBarcode());
                }
                List<SendThreeDetail> noScanList = new ArrayList<SendThreeDetail>(geneList.size());
                for (String packageCode : geneList) {
                    SendThreeDetail noScanDetail = new SendThreeDetail();
                    noScanDetail.setBoxCode(diffrenceList.get(diffrenceList.size() - 1).getBoxCode());
                    noScanDetail.setPackageBarcode(packageCode);
                    noScanDetail.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                    noScanDetail.setIsWaybillFull(0);
                    logger.info("未扫描" + noScanDetail.getPackageBarcode());
                    noScanList.add(noScanDetail);
                    ++hasDiff;
                }
                if (noScanList.size() > 0) {
                    for (int index = scanCount; index > 0; --index) {
                        diffrenceList.get(diffrenceList.size() - index).setIsWaybillFull(0);
                    }
                }
                diffrenceList.addAll(noScanList);
            }
            return hasDiff;
        }
    }

    /**
     * 判断自提柜类型
     *
     * @param waybill
     * @return
     */
    public Boolean isZiTiGui(com.jd.bluedragon.common.domain.Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if ('5' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 获取站点的ParentSite判断是否是速递中心
     *
     * @param siteCode
     * @return
     */
    public BaseStaffSiteOrgDto getParentSiteBySiteCode(Integer siteCode) {
        return this.baseMajorManager.getBaseSiteBySiteId(siteCode);
    }

    /**
     * 原包发货[前提条件]1：箱号、原包没有发货;
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败  4：需要用户确认
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.AtuopackageSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult atuoPackageSend(SendM domain, boolean isForceSend,String packageCode) {

        SendM queryPara = new SendM();
        queryPara.setBoxCode(domain.getBoxCode());
        queryPara.setCreateSiteCode(domain.getCreateSiteCode());
        queryPara.setReceiveSiteCode(domain.getReceiveSiteCode());
        List<SendM> sendMList = this.sendMDao.selectBySendSiteCode(queryPara);/*不直接使用domain的原因，SELECT语句有[test="createUserId!=null"]等其它*/

        if (null != sendMList && sendMList.size() > 0) {
            new SendResult(SendResult.CODE_SENDED, SendResult.MESSAGE_SENDED);
        }
        try {
            //插入SEND_M
            this.sendMDao.insertSendM(domain);
            //区分分拣机自动发货还是龙门架,分拣机按箱号自动发货   add by lhc  add by lhc 2017.11.27
            if(isForceSend && SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())){
            	pushAtuoSorting(domain,packageCode);
            }
            
            if (!SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())) {
                pushSorting(domain);//大件写TASK_SORTING
            } else {
                SendDetail tSendDatail = new SendDetail();
                tSendDatail.setBoxCode(domain.getBoxCode());
                tSendDatail.setCreateSiteCode(domain.getCreateSiteCode());
                tSendDatail.setReceiveSiteCode(domain.getReceiveSiteCode());
                this.updateCancel(tSendDatail);//更新SEND_D状态
            }
            this.transitSend(domain);//中转任务
            this.pushStatusTask(domain);//全程跟踪任务
        } catch (Exception e) {
            logger.error("一车一单自动发货异常", e);
            new SendResult(SendResult.CODE_SERVICE_ERROR, SendResult.MESSAGE_SERVICE_ERROR);
        }
        return new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    }
}
