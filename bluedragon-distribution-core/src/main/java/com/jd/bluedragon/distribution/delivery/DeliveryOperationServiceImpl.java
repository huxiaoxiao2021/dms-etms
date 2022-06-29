package com.jd.bluedragon.distribution.delivery;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.delivery.processor.IDeliveryBaseHandler;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wyh
 * @className DeliveryOperationService
 * @description
 * @date 2021/8/4 16:48
 **/
@Service
public class DeliveryOperationServiceImpl implements IDeliveryOperationService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryOperationServiceImpl.class);

    @Autowired
    @Qualifier("deliveryPackageHandler")
    private IDeliveryBaseHandler packageHandler;

    @Autowired
    @Qualifier("deliveryBoxHandler")
    private IDeliveryBaseHandler boxHandler;

    @Autowired
    @Qualifier("deliveryWaybillHandler")
    private IDeliveryBaseHandler waybillHandler;

    @Autowired
    @Qualifier("deliveryBoardHandler")
    private IDeliveryBaseHandler boardHandler;

    @Autowired
    private UccPropertyConfiguration uccConfig;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected SequenceGenAdaptor sequenceGenAdaptor;
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    DeliveryService deliveryService;

    /**
     * 按包裹、箱号、运单处理发货数据
     * @param requests
     * @param sourceEnum
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.asyncHandleDelivery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeliveryResponse asyncHandleDelivery(List<SendM> requests, SendBizSourceEnum sourceEnum) {

        SendMWrapper packageSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_PACKAGE);
        SendMWrapper boxSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOX);
        SendMWrapper waybillWrapper = new SendMWrapper(SendKeyTypeEnum.BY_WAYBILL);

        // 单次发货的公共属性
        SendM sendM = makeDeliveryDomain(requests.get(0), sourceEnum);
        long uniqueId = genSendBatchTaskUniqueId(sendM.getSendCode());
        log.info("===========asyncHandleDelivery==========生成批次唯一id：{},批次号码：{}",uniqueId,sendM.getSendCode());

        // 每次发货的操作时间、操作人、批次号等认为是一样的
        packageSendWrapper.setSendM(sendM);
        packageSendWrapper.setBatchUniqKey(sendM.getSendCode()+"_"+uniqueId);
        boxSendWrapper.setSendM(sendM);
        boxSendWrapper.setBatchUniqKey(sendM.getSendCode()+"_"+uniqueId);
        waybillWrapper.setSendM(sendM);
        waybillWrapper.setBatchUniqKey(sendM.getSendCode()+"_"+uniqueId);


        for (SendM request : requests) {
            String barCode = request.getBoxCode();
            if (WaybillUtil.isPackageCode(barCode)) {
                packageSendWrapper.add(barCode);
            }
            else if (BusinessHelper.isBoxcode(barCode)) {
                boxSendWrapper.add(barCode);
            }
            else if (WaybillUtil.isWaybillCode(barCode)) {
                waybillWrapper.add(barCode);
            }
        }

        String compeletedCountKey = String.format(CacheKeyConstants.COMPELETE_SEND_COUNT_KEY, sendM.getSendCode()+"_"+uniqueId);
        try {
            redisClientCache.set(compeletedCountKey,"0",uccConfig.getCreateSendTasktimeOut(), TimeUnit.MINUTES,false);
        } catch (Exception e) {
            log.error("redis给发货任务compeletedCountKey设置过期时间异常",e);
        }

        if (CollectionUtils.isNotEmpty(packageSendWrapper.getBarCodeList())) {
            packageHandler.initDeliveryTask(packageSendWrapper);
        }

        if (CollectionUtils.isNotEmpty(boxSendWrapper.getBarCodeList())) {
            boxHandler.initDeliveryTask(boxSendWrapper);
        }

        if (CollectionUtils.isNotEmpty(waybillWrapper.getBarCodeList())) {
            waybillHandler.initDeliveryTask(waybillWrapper);
        }

        Task task = new Task();
        task.setCreateSiteCode(sendM.getCreateSiteCode());
        task.setReceiveSiteCode(sendM.getReceiveSiteCode());
        task.setType(Task.TASK_TYPE_SEND_DELIVERY);
        task.setTableName(Task.getTableName(task.getType()));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1("20");
        task.setKeyword2(sendM.getSendCode());
        task.setOwnSign(BusinessHelper.getOwnSign());

        SendMWrapper sendMWrapper =new SendMWrapper(SendKeyTypeEnum.BY_SENDCODE);
        sendMWrapper.setSendM(sendM);
        sendMWrapper.setBatchUniqKey(sendM.getSendCode()+"_"+uniqueId);
        task.setBody(JsonHelper.toJson(sendMWrapper));

        task.setFingerprint(Md5Helper.encode(String.valueOf(uniqueId)));
        taskService.doAddTask(task,false);
        log.info("===========asyncHandleDelivery==========生成task调度任务");


        return DeliveryResponse.oK();
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.asyncHandleTransfer", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeliveryResponse asyncHandleTransfer(List<SendM> sendMList, String newSendCode) {
        SendM sendM = makeTransferDomain(sendMList.get(0));
        log.info("===========asyncHandleTransfer==========发货批次迁移,原批次号：{}，新批次号:{}",sendM.getSendCode(),newSendCode);

        SendMWrapper packageSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_PACKAGE);
        SendMWrapper boxSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOX);
        SendMWrapper boardSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOARD);

        packageSendWrapper.setSendM(sendM);
        boxSendWrapper.setSendM(sendM);
        boardSendWrapper.setSendM(sendM);

        Set<String> boardSet =new HashSet<>();
        for (SendM sendm : sendMList) {
            String barCode = sendm.getBoxCode();
            String boardCode =sendm.getBoardCode();
            //按板发的货
            if (ObjectHelper.isNotNull(boardCode)){
                boardSet.add(boardCode);
            }
            //按包裹-运单
            else if (WaybillUtil.isPackageCode(barCode)) {
                packageSendWrapper.add(barCode);
            }
            //按箱
            else if (BusinessHelper.isBoxcode(barCode)) {
                boxSendWrapper.add(barCode);
            }
        }
        boardSendWrapper.setBarCodeList(new ArrayList(boardSet));

        if (CollectionUtils.isNotEmpty(packageSendWrapper.getBarCodeList())) {
            packageHandler.initTransferTask(packageSendWrapper);
        }
        if (CollectionUtils.isNotEmpty(boxSendWrapper.getBarCodeList())) {
            boxHandler.initTransferTask(boxSendWrapper);
        }
        if (CollectionUtils.isNotEmpty(boardSendWrapper.getBarCodeList())) {
            boardHandler.initTransferTask(boardSendWrapper);
        }
        return DeliveryResponse.oK();
    }

    private SendM makeTransferDomain(SendM request) {
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getCreateSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        sendM.setCreateUserCode(request.getCreateUserCode());
        sendM.setSendType(request.getSendType());
        sendM.setCreateUser(request.getCreateUser());
        sendM.setSendCode(request.getSendCode());
        sendM.setCreateTime(request.getCreateTime());
        sendM.setOperateTime(request.getOperateTime());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(request.getTurnoverBoxCode());
        sendM.setTransporttype(request.getTransporttype());
        sendM.setBizSource(request.getBizSource());
        return sendM;
    }

    long genSendBatchTaskUniqueId(String nameSpace){
        long sequence;
        try {
            sequence = sequenceGenAdaptor.newId(nameSpace);
        }
        catch (Throwable ex) {
            log.error("[发货批次任务]生成唯一id失败 ", ex);
            return System.currentTimeMillis();
        }
        return sequence;
    }

    private SendM makeDeliveryDomain(SendM request, SendBizSourceEnum sourceEnum) {
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getCreateSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        sendM.setCreateUserCode(request.getCreateUserCode());
        sendM.setSendType(request.getSendType());
        sendM.setCreateUser(request.getCreateUser());
        sendM.setSendCode(request.getSendCode());
        sendM.setCreateTime(request.getCreateTime());
        sendM.setOperateTime(request.getOperateTime());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(request.getTurnoverBoxCode());
        sendM.setTransporttype(request.getTransporttype());
        sendM.setBizSource(sourceEnum.getCode());
        return sendM;
    }

    /**
     * 处理发货任务
     * @param task
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.dealDeliveryTask", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealDeliveryTask(Task task) {

        final SendMWrapper wrapper = JsonHelper.fromJson(task.getBody(), SendMWrapper.class);
        if (null == wrapper || null == wrapper.getKeyType()) {
            return;
        }

        SendKeyTypeEnum typeEnum = wrapper.getKeyType();
        switch (typeEnum) {
            case BY_PACKAGE:

                packageHandler.dealCoreDelivery(wrapper);
                break;
            case BY_BOX:

                boxHandler.dealCoreDelivery(wrapper);
                break;
            case BY_WAYBILL:

                waybillHandler.dealCoreDelivery(wrapper);
                break;
            default:
                break;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.dealDeliveryTaskV2", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealDeliveryTaskV2(Task task) {

        final SendMWrapper wrapper = JsonHelper.fromJson(task.getBody(), SendMWrapper.class);
        if (null == wrapper || null == wrapper.getKeyType()) {
            return;
        }

        SendKeyTypeEnum typeEnum = wrapper.getKeyType();
        switch (typeEnum) {
            case BY_PACKAGE:

                packageHandler.dealCoreDeliveryV2(wrapper);
                break;
            case BY_BOX:

                boxHandler.dealCoreDeliveryV2(wrapper);
                break;
            case BY_WAYBILL:

                waybillHandler.dealCoreDeliveryV2(wrapper);
                break;
            default:
                break;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.dealSendTransferTask", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealSendTransferTask(Task task) {
        final SendMWrapper wrapper = JsonHelper.fromJson(task.getBody(), SendMWrapper.class);
        if (null == wrapper || null == wrapper.getKeyType()) {
            return;
        }
        SendKeyTypeEnum typeEnum = wrapper.getKeyType();
        switch (typeEnum) {
            case BY_PACKAGE:
                packageHandler.dealSendTransfer(wrapper);
                break;
            case BY_BOX:
                boxHandler.dealSendTransfer(wrapper);
                break;
            case BY_BOARD:
                boardHandler.dealSendTransfer(wrapper);
                break;
            default:
                break;
        }
    }

    /**
     * 发货异步任务切换开关
     * @param createSiteCode
     * @return
     */
    @Override
    public boolean deliverySendAsyncSwitch(Integer createSiteCode) {
        String configSites = uccConfig.getDeliverySendAsyncSite();
        if (StringUtils.isBlank(configSites)) {
            return false;
        }
        if (null == createSiteCode) {
            return false;
        }
        // 全国场地生效
        if (Constants.STR_ALL.equalsIgnoreCase(configSites)) {
            return true;
        }

        List<String> enableSites = Arrays.asList(configSites.split(Constants.SEPARATOR_COMMA));
        return enableSites.contains(createSiteCode.toString());
    }
}
