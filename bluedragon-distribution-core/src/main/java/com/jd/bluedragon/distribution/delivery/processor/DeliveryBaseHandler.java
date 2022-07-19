package com.jd.bluedragon.distribution.delivery.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.external.constants.BoxStatusEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.*;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wyh
 * @className DeliveryBaseHandler
 * @description
 * @date 2021/8/6 17:36
 **/
public abstract class DeliveryBaseHandler implements IDeliveryBaseHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    static int SEND_SPLIT_NUM = 30;

    static int EXPIRE_TIME_SECOND = 2 * 60 * 60;

    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;

    @Autowired
    protected SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    protected UccPropertyConfiguration uccConfig;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected DeliveryService deliveryService;

    @Autowired
    protected WaybillPackageManager waybillPackageManager;
    @Autowired
    private SendDatailDao sendDatailDao;
    @Autowired
    ReverseDeliveryService reverseDeliveryService;
    @Autowired
    private ReversePartDetailService reversePartDetailService;
    @Autowired
    private RedisManager redisManager;
    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;
    @Autowired
    private SendMDao sendMDao;
    @Autowired
    private BoxService boxService;
    @Autowired
    private SortingService tSortingService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    @Qualifier("deliveryCancelSendMQ")
    private DefaultJMQProducer deliveryCancelSendMQ;
    @Autowired
    private ColdChainSendService coldChainSendService;
    @Autowired
    @Qualifier("dmsColdChainSendWaybill")
    private DefaultJMQProducer dmsColdChainSendWaybill;


    /**
     * 生成该次发货操作的唯一标识
     *
     * @param wrapper
     * @return
     */
    @Override
    public String genBatchTaskUniqKey(SendMWrapper wrapper) {
        String sequence;
        try {
            sequence = String.valueOf(sequenceGenAdaptor.newId(wrapper.getKeyType().toString()));
        }
        catch (Throwable ex) {
            log.error("[发货包裹任务]生成唯一标识失败.", ex);

            try {
                sequence = String.valueOf(sequenceGenAdaptor.newId(wrapper.getKeyType().toString()));
            }
            catch (Throwable ex1) {
                log.error("[发货包裹任务]再次生成唯一标识失败.");
                throw new RuntimeException("[发货]服务器异常！请重试！如果仍失败，请咚咚联系[分拣小秘]！");
            }
        }

        return sequence + Constants.UNDER_LINE + wrapper.getSendM().getSendCode();
    }

    @Override
    public DeliveryResponse initDeliveryTask(SendMWrapper wrapper) {

        int onePageSize = uccConfig.getOldSendSplitPageSize() <= 0 ? SEND_SPLIT_NUM : uccConfig.getOldSendSplitPageSize();
        int totalNum = wrapper.getBarCodeList().size();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;

        SendM sendM = wrapper.getSendM();

        // 生成本次发货的唯一标识
        String batchUniqKey = wrapper.getBatchUniqKey();

        // 设置本次发货的批处理锁，值为本次任务的总页数
        lockPageDelivery(batchUniqKey, pageTotal);

        // 锁定本次发货的包裹/箱号
        batchLockBox(wrapper.getBarCodeList(), sendM.getCreateSiteCode());

        List<List<String>> pageBarCode = CollectionHelper.splitList(wrapper.getBarCodeList(), onePageSize);

        for (int i = 0; i < pageTotal; i++) {

            SendMWrapper copyWrapper = new SendMWrapper();
            copyWrapper.setSendM(sendM);
            copyWrapper.setBatchUniqKey(batchUniqKey);
            copyWrapper.setKeyType(wrapper.getKeyType());
            // 设置本次需要执行的包裹或箱号
            copyWrapper.setBarCodeList(pageBarCode.get(i));
            copyWrapper.setPageNo(i + 1);
            copyWrapper.setPageSize(onePageSize);
            copyWrapper.setTotalPage(pageTotal);

            Task task = new Task();

            task.setCreateSiteCode(sendM.getCreateSiteCode());
            task.setReceiveSiteCode(sendM.getReceiveSiteCode());

            task.setType(Task.TASK_TYPE_DELIVERY_ASYNC_V2);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setKeyword1(wrapper.getKeyType().name());
            task.setKeyword2(i + 1 + Constants.UNDER_LINE + pageTotal + Constants.UNDER_LINE + sendM.getSendCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(copyWrapper));

            String fingerprint = batchUniqKey +
                    Constants.UNDER_LINE + System.currentTimeMillis();
            task.setFingerprint(Md5Helper.encode(fingerprint));

            taskService.doAddTask(task,false);
        }

        return DeliveryResponse.oK();
    }

    @Override
    public DeliveryResponse initTransferTask(SendMWrapper wrapper) {
        SendM sendM = wrapper.getSendM();

        int onePageSize = uccConfig.getOldSendSplitPageSize() <= 0 ? SEND_SPLIT_NUM : uccConfig.getOldSendSplitPageSize();
        int totalNum = wrapper.getBarCodeList().size();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
        List<List<String>> pageBarCode = CollectionHelper.splitList(wrapper.getBarCodeList(), onePageSize);

        //拆分mini-batch
        for (int i = 0; i < pageTotal; i++) {
            SendMWrapper copyWrapper = new SendMWrapper();
            copyWrapper.setSendM(sendM);
            copyWrapper.setKeyType(wrapper.getKeyType());
            // 设置本次需要执行的包裹或箱号
            copyWrapper.setBarCodeList(pageBarCode.get(i));
            copyWrapper.setPageNo(i + 1);
            copyWrapper.setPageSize(onePageSize);
            copyWrapper.setTotalPage(pageTotal);
            copyWrapper.setNewSendCode(wrapper.getNewSendCode());

            Task task = new Task();
            task.setCreateSiteCode(sendM.getCreateSiteCode());
            task.setReceiveSiteCode(sendM.getReceiveSiteCode());
            task.setType(Task.TASK_TYPE_DELIVERY_TRANSFER);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setKeyword1(wrapper.getKeyType().name());
            task.setKeyword2(i + 1 + Constants.UNDER_LINE + pageTotal + Constants.UNDER_LINE + sendM.getSendCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(copyWrapper));
            String fingerprint = pageBarCode.get(i).get(0) + Constants.UNDER_LINE + System.currentTimeMillis();
            task.setFingerprint(Md5Helper.encode(fingerprint));

            taskService.doAddTask(task,false);
        }

        return DeliveryResponse.oK();
    }

    /**
     * 锁定本次批处理任务
     * @param batchUniqKey
     * @param pageTotal
     * @return
     */
    @Override
    public boolean lockPageDelivery(String batchUniqKey, int pageTotal) {
        String redisKey = String.format(CacheKeyConstants.INITIAL_SEND_COUNT_KEY, batchUniqKey);
        try {
            redisClientCache.incrBy(redisKey,pageTotal);
            log.info("批次任务初始化 {}，计数成功：{}",batchUniqKey,pageTotal);
        } catch (Exception e) {
            log.error("lockPageDelivery初始化批次计数异常",e);
            return false;
        }
        return true;
    }

    /**
     * 批量锁定包裹/箱号发货数据
     * @param barCodeList
     * @param siteCode
     * @return
     */
    private boolean batchLockBox(List<String> barCodeList, Integer siteCode) {
        for (String barCode : barCodeList) {
            String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_LOCK_KEY, barCode, siteCode);
            redisClientCache.set(redisKey, "lock", EXPIRE_TIME_SECOND, TimeUnit.SECONDS, false);
        }

        return true;
    }

    /**
     * 处理发货逻辑
     *
     * @param wrapper
     * @return
     */
    @Override
    public boolean dealCoreDelivery(final SendMWrapper wrapper) {
        List<SendM> sendMList = Lists.newArrayListWithCapacity(wrapper.getBarCodeList().size());
        final SendM sendM = wrapper.getSendM();
        for (String barCode : wrapper.getBarCodeList()) {
            SendM domain = BeanUtils.copy(sendM, SendM.class);
            domain.setBoxCode(barCode);
            sendMList.add(domain);
        }

        final int pageNo = wrapper.getPageNo();
        final int pageTotal = wrapper.getTotalPage();
        final String batchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(sendMList.get(0).getBizSource(), sendMList);

        // 判断是否推送全程跟踪任务
        SendM taskSendM = sendMList.get(0);
        return judgePushSendTracking(sendM, pageNo, pageTotal, batchUniqKey, taskSendM);
    }

    @Override
    public boolean dealCoreDeliveryV2(final SendMWrapper wrapper) {
        List<SendM> sendMList = Lists.newArrayListWithCapacity(wrapper.getBarCodeList().size());
        final SendM sendM = wrapper.getSendM();
        for (String barCode : wrapper.getBarCodeList()) {
            SendM domain = BeanUtils.copy(sendM, SendM.class);
            domain.setBoxCode(barCode);
            sendMList.add(domain);
        }

        final String batchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(sendMList.get(0).getBizSource(), sendMList);

        return competeTaskIncrCount(batchUniqKey);
    }

    @Override
    public boolean dealSendTransfer(SendMWrapper wrapper) {
        final SendM sendM = wrapper.getSendM();

        SendDetail sendDRequest = new SendDetail();
        sendDRequest.setCreateSiteCode(sendM.getCreateSiteCode());
        sendDRequest.setReceiveSiteCode(sendM.getReceiveSiteCode());
        sendDRequest.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
        Date now =new Date();

        for (String barCode : wrapper.getBarCodeList()) {
            SendM sendMItem =BeanUtils.copy(sendM, SendM.class);
            sendMItem.setBoxCode(barCode);
            sendMItem.setUpdateTime(now);

            if (WaybillUtil.isWaybillCode(barCode)) {
                sendDRequest.setWaybillCode(barCode);
            } else if (WaybillUtil.isPackageCode(barCode)){
                sendDRequest.setPackageBarcode(barCode);
            } else {
                sendDRequest.setBoxCode(barCode);
            }
            List<SendDetail> tlist = sendDatailDao.querySendDatailsBySelective(sendDRequest);//查询sendD明细
            //log.info("取消发货查询sendd明细,{}",tlist.toString());

            if (WaybillUtil.isWaybillCode(sendMItem.getBoxCode())
                    || WaybillUtil.isPackageCode(sendMItem.getBoxCode())) {
                log.info("dealSendTransfer按运单/包裹维度进行取消：{}",sendMItem.getBoxCode());
                /* 按包裹号和运单号的逻辑走 */
                ThreeDeliveryResponse responsePack = cancelUpdateDataByPack(sendMItem, tlist);
                if (responsePack.getCode().equals(200)) {
                    log.info("dealSendTransfer按运单/包裹维度进行取消成功");
                    delDeliveryFromRedis(sendMItem);
                    sendMessage(tlist, sendMItem, true);
                    //同步取消半退明细
                    reversePartDetailService.cancelPartSend(sendMItem);
                    // 更新包裹装车记录表的扫描状态为取消扫描状态
                    updateScanActionByPackageCodes(tlist, sendMItem);
                } else {
                    continue;
                }
            } else if (BusinessHelper.isBoxcode(sendMItem.getBoxCode())) {
                /* 按箱号的逻辑走 */
                List<SendM> sendMs = new ArrayList<>();
                sendMs.add(sendMItem);
                ThreeDeliveryResponse threeDeliveryResponse = cancelUpdateDataByBox(sendMItem, sendDRequest, sendMs);
                if (threeDeliveryResponse.getCode().equals(200)) {
                    log.info("dealSendTransfer按箱维度进行取消成功");
                    delDeliveryFromRedis(sendMItem);     //取消发货成功，删除redis缓存的发货数据
                    //更新箱号状态
                    openBox(sendMItem);
                    sendMessage(tlist, sendMItem, true);
                } else {
                    continue;
                }
            } else {
                log.info("暂时不支持按该范畴进行取消：{}" , JsonHelper.toJson(sendMItem));
                continue;
            }

            //生成新的发货
            SendBizSourceEnum bizSource = SendBizSourceEnum.getEnum(sendMItem.getBizSource());
            sendMItem.setSendCode(wrapper.getNewSendCode());
            sendMItem.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(wrapper.getNewSendCode()));
            sendMItem.setCreateTime(now);
            sendMItem.setOperateTime(now);
            sendMItem.setUpdateTime(now);
            deliveryService.packageSend(bizSource,sendMItem);
        }
        return true;
    }

    private void openBox(SendM tSendM){
        //参数构建
        BoxReq boxReq = new BoxReq();
        boxReq.setBoxCode(tSendM.getBoxCode());
        boxReq.setBoxStatus(BoxStatusEnum.OPEN.getStatus());
        boxReq.setOpNodeCode(OpBoxNodeEnum.CANCELSEND.getNodeCode());
        boxReq.setOpNodeName(OpBoxNodeEnum.CANCELSEND.getNodeName());
        boxReq.setOpSiteCode(tSendM.getCreateSiteCode());
        boxReq.setOpSiteName("");
        boxReq.setOpErp(tSendM.getCreateUser());
        boxReq.setOpTime(tSendM.getOperateTime());
        boxReq.setOpDescription(String.format("%s操作取消发货，打开此箱号%s的箱子", tSendM.getCreateUser(),tSendM.getBoxCode()));
        //修改箱状态
        boxService.updateBoxStatus(boxReq);
    }

    //处理运单
    private ThreeDeliveryResponse cancelUpdateDataByPack(SendM tSendM,
                                                         List<SendDetail> tList) {
        Collections.sort(tList);
        for (SendDetail dSendDetail : tList) {
            tSendM.setBoxCode(dSendDetail.getBoxCode());
            List<SendM> sendMList = this.sendMDao.findSendMByBoxCode(tSendM);
            // 发车验证 2021年12月15日18:08:57 下线
            if (sendMList == null || sendMList.isEmpty()) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING), null);
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
            //如果按包裹取消发货，需取消分拣，更新取消分拣的操作时间晚取消分拣一秒
            sorting.setOperateTime(new Date(tSendM.getUpdateTime().getTime() + 1000));
            tSortingService.canCancel2(sorting);
        }
        return new ThreeDeliveryResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK, null);
    }
    private ThreeDeliveryResponse cancelUpdateDataByBox(SendM tSendM,
                                                        SendDetail tSendDatail, List<SendM> sendMList) {
        Collections.sort(sendMList);
        if (sendMList != null && !sendMList.isEmpty()) {
            SendM dSendM = getLastSendDate(sendMList);
            dSendM.setUpdaterUser(tSendM.getUpdaterUser());
            dSendM.setUpdateUserCode(tSendM.getUpdateUserCode());
            dSendM.setUpdateTime(new Date());
            dSendM.setOperateTime(tSendM.getOperateTime());
            // 是否发车 2021年12月15日18:09:28 下线

            tSendDatail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
            // 是否发货状态更新
            if (sendDatailDao.querySendDatailBySendStatus(tSendDatail) != null) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        HintService.getHint(HintCodeConstants.DELIVERY_PROCESSING), null);
            }
            return cancelDeliveryStatusByBox(dSendM, tSendDatail);
        } else {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_NO_MESAGE,
                    HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING), null);
        }
    }

    //箱子更新取消发货状态
    public ThreeDeliveryResponse cancelDeliveryStatusByBox(SendM tSendM, SendDetail tSendDatail) {
        SendDetail mSendDetail = new SendDetail();
        mSendDetail.setBoxCode(tSendM.getBoxCode());
        mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
        mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
        mSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);
        List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
        Collections.sort(tlist);
        //更新m表和d表
        reverseDeliveryService.updateIsCancelByBox(tSendM);
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

    private void delDeliveryFromRedis(SendM sendM) {
        Long result = redisManager.del(
                CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                        + sendM.getCreateSiteCode()
                        + sendM.getBoxCode());
        if (result <= 0) {
            log.warn("remove sendms of key [{}-{}-{}] from redis fail",
                    CacheKeyConstants.REDIS_KEY_IS_DELIVERY,sendM.getCreateSiteCode(),sendM.getBoxCode());
        } else {
            log.warn("remove sendms of key [{}-{}-{}] from redis success",
                    CacheKeyConstants.REDIS_KEY_IS_DELIVERY,sendM.getCreateSiteCode(),sendM.getBoxCode());
        }
    }
    private void sendMessage(List<SendDetail> sendDetails, SendM tSendM, boolean needSendMQ) {
        try {
            if (sendDetails == null || sendDetails.isEmpty()) {
                return;
            }
            Set<String> coldChainWaybillSet = new HashSet<>();
            List<SendDetail> coldChainSendDetails = new ArrayList<>();
            //按照包裹
            for (SendDetail model : sendDetails) {
                if (StringHelper.isNotEmpty(model.getSendCode())) {
                    // 发送全程跟踪任务
                    send(model, tSendM);
                    if (needSendMQ) {
                        // 发送取消发货MQ
                        sendMQ(model, tSendM);
                        log.info("发送取消发货全程跟踪成功：{}",JsonHelper.toJson(model));
                        if (this.isColdChainSend(model, tSendM, coldChainWaybillSet)) {
                            coldChainSendDetails.add(model);
                        }
                    }
                }
            }
            this.sendColdChainSendMQ(coldChainSendDetails);
        } catch (Exception ex) {
            log.error("取消发货 发全程跟踪sendMessage： " + ex);
        }
    }

    /**
     * 取消发货判断运单是否为冷链卡班发货
     *
     * @param sendD
     * @param tSendM
     * @param coldChainWaybillSet
     * @return
     */
    private boolean isColdChainSend(SendDetail sendD, SendM tSendM, Set<String> coldChainWaybillSet) {
        Integer bizSource = sendD.getBizSource() == null ? tSendM.getBizSource() : sendD.getBizSource();
        if (bizSource != null) {
            if (SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_CHAIN_SEND
                    || SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND
                    || SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_LOAD_CAR_SEND
            ) {
                if (coldChainWaybillSet.add(sendD.getWaybillCode())) {
                    return true;
                }
            }
        } else {
            if (!coldChainWaybillSet.contains(sendD.getWaybillCode())) {
                List<ColdChainSend> coldChainSends = coldChainSendService.getByWaybillCode(sendD.getWaybillCode());
                if (coldChainSends != null && coldChainSends.size() > 0) {
                    if (coldChainWaybillSet.add(sendD.getWaybillCode())) {
                        if (StringUtils.isEmpty(sendD.getSendCode())) {
                            sendD.setSendCode(coldChainSends.get(0).getSendCode());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 冷链取消发货MQ消息
     *
     * @param coldChainSendDetails
     */
    private void sendColdChainSendMQ(List<SendDetail> coldChainSendDetails) {
        try {
            if (coldChainSendDetails.size() > 0) {
                List<Message> messageList = new ArrayList<>();
                for (SendDetail sendDetail : coldChainSendDetails) {
                    BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
                    BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
                    if (createSiteDto != null && receiveSiteDto != null) {
                        ColdChainSend coldChainSend = coldChainSendService.getBySendCode(sendDetail.getWaybillCode(), sendDetail.getSendCode());
                        if (coldChainSend != null && com.jd.common.util.StringUtils.isNotEmpty(coldChainSend.getTransPlanCode())) {
                            ColdChainSendMessage messageBody = new ColdChainSendMessage();
                            messageBody.setWaybillCode(sendDetail.getWaybillCode());
                            messageBody.setSendCode(sendDetail.getSendCode());
                            // 取消发货
                            messageBody.setSendType(2);
                            messageBody.setTransPlanCode(coldChainSend.getTransPlanCode());
                            messageBody.setCreateSiteCode(createSiteDto.getDmsSiteCode());
                            messageBody.setReceiveSiteCode(receiveSiteDto.getDmsSiteCode());
                            messageBody.setOperateTime(sendDetail.getOperateTime().getTime());
                            messageBody.setOperateUserName(sendDetail.getCreateUser());
                            if (sendDetail.getCreateUserCode() != null) {
                                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(sendDetail.getCreateUserCode());
                                if (dto != null) {
                                    messageBody.setOperateUserErp(dto.getErp());
                                }
                            }
                            messageList.add(new Message(dmsColdChainSendWaybill.getTopic(), JSON.toJSONString(messageBody), sendDetail.getWaybillCode()));
                        }
                    }
                }
                dmsColdChainSendWaybill.batchSend(messageList);
            }
        } catch (JMQException e) {
            log.error("[PDA操作取消发货]冷链取消发货 - 推送TMS运输MQ消息时发生异常：{}",JsonHelper.toJson(coldChainSendDetails), e);
        }
    }
    /**
     * PDA操作取消发货MQ消息发送
     */
    private void sendMQ(SendDetail sendDetail, SendM sendM) {
        try {
            DeliveryCancelSendMQBody body = new DeliveryCancelSendMQBody();
            body.setPackageBarcode(sendDetail.getPackageBarcode());
            body.setWaybillCode(sendDetail.getWaybillCode());
            body.setSendCode(sendDetail.getSendCode());
            body.setOperateTime(sendM.getUpdateTime());
            Integer userCode = sendM.getUpdateUserCode();
            if (userCode != null) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(userCode);
                if (dto != null) {
                    body.setOperatorErp(dto.getErp());
                }
            }
            deliveryCancelSendMQ.send(sendDetail.getPackageBarcode(), JsonHelper.toJson(body));
        } catch (Exception e) {
            log.error("[PDA操作取消发货]发送MQ消息时发生异常:{}",JsonHelper.toJson(sendDetail), e);
        }
    }

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
        status.setRemark("取消发货，批次号为：" +sendDetail.getSendCode());
        status.setCreateSiteCode(tSendM.getCreateSiteCode());

        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(tSendM.getCreateSiteCode());

        status.setCreateSiteName(dto.getSiteName());
        tTask.setBody(JsonHelper.toJson(status));
        log.info("取消发货 发全程跟踪work6666-3800：{} " ,sendDetail.getWaybillCode());
        taskService.add(tTask);
    }

    public void updateScanActionByPackageCodes(List<SendDetail> list, SendM sendM) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl.updateScanActionByPackageCodes", false, true);
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                List<String> packageCodes = new ArrayList<>();
                for (SendDetail detail : list) {
                    packageCodes.add(detail.getPackageBarcode());
                }
                record.setCreateSiteCode(Long.valueOf(list.get(0).getCreateSiteCode()));
                record.setPackageCodeList(packageCodes);
                record.setUpdateTime(new Date());
                record.setUpdateUserName(sendM.getUpdaterUser());
                record.setUpdateUserCode(sendM.getUpdateUserCode());
                goodsLoadScanRecordDao.updateScanActionByPackageCodes(record);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("取消发货--根据包裹号列表批量更新取消发货的包裹为取消扫描状态发生错误e=", e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
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
    @Override
    public boolean competeTaskIncrCount(String batchUniqKey) {
        String compeletedCountKey = String.format(CacheKeyConstants.COMPELETE_SEND_COUNT_KEY, batchUniqKey);
        try {
            redisClientCache.incr(compeletedCountKey);
            log.info("发货批次 {} competeTaskIncrCount加一",batchUniqKey);
        } catch (Exception e) {
            log.error("任务完成计数异常",e);
        }
        return true;
    }

    private boolean judgePushSendTracking(SendM sendM, int pageNo, int pageTotal, String batchUniqKey, SendM taskSendM) {
        String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_BATCH_KEY, batchUniqKey);
        String redisVal = redisClientCache.get(redisKey);
        if (StringUtils.isEmpty(redisVal)) {
            log.warn("[包裹/箱号]获取批次任务缓存数据为空. key:{}", batchUniqKey);
            return true;
        }

        String countRedisKey = String.format(CacheKeyConstants.PACKAGE_SEND_COUNT_KEY, batchUniqKey);
        // 设置单页处理完成标志位
        redisClientCache.setBit(countRedisKey, pageNo, true);
        redisClientCache.expire(countRedisKey, EXPIRE_TIME_SECOND, TimeUnit.SECONDS);

        // 全部分页任务处理完成，生成发货任务
        if (Integer.parseInt(redisVal) == redisClientCache.bitCount(countRedisKey).intValue()) {

            // 删除批任务处理锁
            redisClientCache.del(redisKey);

            redisClientCache.del(countRedisKey);

            // 插入发货任务
            deliveryService.addTaskSend(taskSendM);

            if (log.isInfoEnabled()) {
                log.info("[包裹/箱号]当前批次任务全部处理完毕! total:{}, sendM:{}", pageTotal, JsonHelper.toJson(sendM));
            }
        }
        return false;
    }
}
