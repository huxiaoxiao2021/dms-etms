package com.jd.bluedragon.distribution.delivery;

import static com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.delivery.processor.IDeliveryBaseHandler;
import com.jd.bluedragon.distribution.jy.dto.comboard.ComboardTaskDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.cancelComboardTaskDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.enums.ComboardBarCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.transboard.api.dto.AddBoardBoxes;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
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
    @Autowired
    protected WaybillPackageManager waybillPackageManager;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    final static int SEND_SPLIT_NUM = 1024;
    final static int COMBOARD_SPLIT_NUM = 1024;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    private VirtualBoardService virtualBoardService;

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
    public DeliveryResponse asyncHandleTransfer(List<SendM> sendMList, VehicleSendRelationDto dto) {
        SendM sendM = makeTransferDomain(sendMList.get(0));
        sendM.setUpdateUserCode(dto.getUpdateUserCode());
        sendM.setUpdaterUser(dto.getUpdateUserName());
        sendM.setCreateUserCode(dto.getUpdateUserCode());
        sendM.setCreateUser(dto.getUpdateUserName());
        log.info("===========asyncHandleTransfer==========发货批次迁移,原批次号：{}，新批次号:{}",sendM.getSendCode(),dto.getNewSendCode());

        SendMWrapper packageSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_PACKAGE);
        SendMWrapper boxSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOX);
        SendMWrapper boardSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOARD);

        packageSendWrapper.setSendM(sendM);packageSendWrapper.setNewSendCode(dto.getNewSendCode());
        boxSendWrapper.setSendM(sendM);boxSendWrapper.setNewSendCode(dto.getNewSendCode());
        boardSendWrapper.setSendM(sendM);boardSendWrapper.setNewSendCode(dto.getNewSendCode());

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

    @Override
    public void generateAsyncComboardAndSendTask(ComboardTaskDto dto) {
        // 获取运单包裹数
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(dto.getWaybillCode());
        if (waybill == null || waybill.getGoodNumber() == null) {
            log.error("[异步组板任务]获取运单包裹数失败! code:{}, sendM:{}", dto.getWaybillCode(), JsonHelper.toJson(dto));
            return;
        }

        int totalNum = waybill.getGoodNumber();
        int onePageSize = uccConfig.getWaybillSplitPageSize() == 0 ? SEND_SPLIT_NUM : uccConfig.getWaybillSplitPageSize();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
        dto.setTotalPage(pageTotal);

        // 插入分页任务
        for (int i = 0; i < pageTotal; i++) {
            dto.setPageNo(i+1);
            dto.setPageSize(onePageSize);
            Task task = new Task();
            task.setBoxCode(dto.getWaybillCode());//运单号
            task.setCreateSiteCode(dto.getStartSiteId());
            task.setReceiveSiteCode(dto.getEndSiteId());
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setType(Task.TASK_TYPE_COMBOARD_SEND);
            task.setTableName(Task.getTableName(task.getType()));
            task.setKeyword1(dto.getBoardCode());
            task.setKeyword2(dto.getWaybillCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(dto));
            String fingerprint =   Constants.UNDER_LINE + System.currentTimeMillis();
            task.setFingerprint(Md5Helper.encode(fingerprint));
            taskService.doAddTask(task,false);
        }
        log.info("====================成功生成大宗运单异步组板任务============================");
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

    @Override
    public void dealComboardAndSendTask(Task task) {
        //获取包裹列表，批次租板，批量发货
        if (ObjectHelper.isEmpty(task)){
            log.error("dealComboardAndSendTask异常 任务信息为空！");
            return;
        }
        ComboardTaskDto dto =JsonHelper.fromJson(task.getBody(),ComboardTaskDto.class);
        if (ObjectHelper.isEmpty(dto)){
            log.error("dealComboardAndSendTask异常 body体为空！");
            return;
        }
        log.info("=====================JyComboardAndSendTask====================,{}",JsonHelper.toJson(dto));
        final int pageSize = dto.getPageSize();
        final int pageNo = dto.getPageNo();
        final String waybillCode = dto.getWaybillCode();
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.error("[组板+发货]运单拆分任务分页获取包裹数量为空! waybillCode={}", waybillCode);
            return;
        }
        List<DeliveryPackageD> packageDList =baseEntity.getData();
        //批量组板
        List<String> packageList =new ArrayList<>();
        for (DeliveryPackageD deliveryPackageD:packageDList){
            packageList.add(deliveryPackageD.getPackageBarcode());
        }
        AddBoardBoxes addBoardBoxes =new AddBoardBoxes();
        addBoardBoxes.setBoardCode(dto.getBoardCode());
        addBoardBoxes.setBoxCodes(packageList);
        addBoardBoxes.setBarcodeType(getBarCodeType(packageList.get(0)));
        addBoardBoxes.setBizSource(BizSourceEnum.PDA.getValue());
        addBoardBoxes.setOperatorErp(dto.getUserErp());
        addBoardBoxes.setOperatorName(dto.getUserName());
        addBoardBoxes.setSiteCode(dto.getStartSiteId());
        addBoardBoxes.setSiteName(dto.getStartSiteName());
        addBoardBoxes.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
        Response<Integer> response = groupBoardManager.addBoxesToBoard(addBoardBoxes);
        if (response.getCode() != ResponseEnum.SUCCESS.getIndex()) {
            log.error("异步执行大宗组板"+response.getMesseage()!=null?response.getMesseage():BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
            return;
        }
        for (String packageCode:packageList){
            dto.setBarCode(packageCode);
            sendComboardWaybillTrace(dto);
        }
        log.info("运单异步执行组板{} 成功",JsonHelper.toJson(dto));
    }

    private void sendComboardWaybillTrace(ComboardTaskDto dto) {
        OperatorInfo operatorInfo = assembleComboardOperatorInfo(dto);
        virtualBoardService.sendWaybillTrace(dto.getBarCode(), operatorInfo, dto.getBoardCode(),
            dto.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION, BizSourceEnum.PDA.getValue());
    }

    private OperatorInfo assembleComboardOperatorInfo(ComboardTaskDto dto) {
        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setSiteCode(dto.getStartSiteId());
        operatorInfo.setSiteName(dto.getStartSiteName());
        operatorInfo.setUserCode(dto.getUserCode());
        operatorInfo.setOperateTime(dto.getOperateTime());
        return operatorInfo;
    }

    private Integer getBarCodeType(String barCode) {
        if (WaybillUtil.isWaybillCode(barCode)) {
            return ComboardBarCodeTypeEnum.WAYBILL.getCode();
        } else if (WaybillUtil.isPackageCode(barCode)) {
            return ComboardBarCodeTypeEnum.PACKAGE.getCode();
        } else {
            return ComboardBarCodeTypeEnum.BOX.getCode();
        }
    }

    @Override
    public void asyncSendComboardWaybillTrace(cancelComboardTaskDto dto) {
        // 获取运单包裹数
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(dto.getWaybillCode());
        if (waybill == null || waybill.getGoodNumber() == null) {
            log.error("[异步组板任务]获取运单包裹数失败! code:{}, sendM:{}", dto.getWaybillCode(), JsonHelper.toJson(dto));
            return;
        }

        int totalNum = waybill.getGoodNumber();
        int onePageSize = uccConfig.getWaybillSplitPageSize() == 0 ? COMBOARD_SPLIT_NUM : uccConfig.getWaybillSplitPageSize();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
        // 插入分页任务
        for (int i = 0; i < pageTotal; i++) {
            dto.setPageNo(i+1);
            dto.setPageSize(onePageSize);
            Task task = new Task();
            task.setBoxCode(dto.getWaybillCode());//运单号
            task.setCreateSiteCode(dto.getSiteCode());
            task.setType(Task.TASK_TYPE_COMBOARD_CANCEL);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setKeyword1(dto.getBoardCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(dto));
            String fingerprint =   Constants.UNDER_LINE + System.currentTimeMillis();
            task.setFingerprint(Md5Helper.encode(fingerprint));
            taskService.doAddTask(task,false);
        }
    }
}
