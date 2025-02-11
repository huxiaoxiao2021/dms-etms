package com.jd.bluedragon.distribution.cyclebox;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.box.response.BoxCodeGroupBinDingDto;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CycleBoxExternalManager;
import com.jd.bluedragon.core.base.TMSBossQueryManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.OrderBindMessageRequest;
import com.jd.bluedragon.distribution.api.request.RecyclableBoxRequest;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.response.box.BCGroupBinDingDto;
import com.jd.bluedragon.distribution.api.response.box.GroupBoxDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.box.service.GroupBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.WaybillExtPro;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.LL_BOX_BINDING_MATERIAL_TYPE_ERROR;

@Service("cycleBoxService")
public class CycleBoxServiceImpl implements CycleBoxService {
    private final Logger log = LoggerFactory.getLogger(CycleBoxServiceImpl.class);
    @Autowired
    private SortingService tSortingService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CycleBoxExternalManager cycleBoxExternalManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private TMSBossQueryManager tmsBossQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("recyclableBoxSendMQ")
    private DefaultJMQProducer recyclableBoxSendMQ;

    @Autowired
    @Qualifier("dmsCycleBoxBindNotice")
    private DefaultJMQProducer dmsCycleBoxBindNotice;

    @Autowired
    BoxMaterialRelationService boxMaterialRelationService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    @Autowired
    private GroupBoxService groupBoxService;

    @Autowired
    private SendMManager sendMManager;

    @Value("${materialCode.bind.boxCode.send.time:4}")
    private Integer materialCodeBoxCodeSendTime;

    @Autowired
    private ReceiveService receiveService;


    private static final String FIELD_NAME_CLEAR_BOX_NUM = "clearBoxNum";

    private static final Integer CYCLE_BOX_STATUS_SEND_OUT = 22;
    private static final Integer CYCLE_BOX_STATUS_REVERSE_RECEIVE = 51;
    private static final Integer CYCLE_BOX_STATUS_REVERSE_RECEIVE_EXCEPTION=52;

    /**
     * 根据快运发货的请求获取清流箱总数
     * @param request
     * @return
     */
    @Override
    public CycleBox getCycleBoxNum(List<DeliveryRequest> request) {
        Integer clearBoxNum = 0;
        //获取运单号列表
        List<String> waybillCodeList = getWaybillCodeListByDeliveryRequest(request);

        //调用运单接口获取获取青流箱数量
        if (waybillCodeList != null && waybillCodeList.size() > 0) {
            List<String> properties = new ArrayList<String>();
            //设置青流箱数量的属性名
            properties.add(FIELD_NAME_CLEAR_BOX_NUM);

            List<WaybillExtPro> waybillExtProList = waybillQueryManager.getWaybillExtByProperties(waybillCodeList, properties);

            if (waybillExtProList != null) {
                for (WaybillExtPro extPro : waybillExtProList) {
                    String boxNumStr = (String)(extPro.getProMap().get(FIELD_NAME_CLEAR_BOX_NUM));
                    Integer boxNum = 0;
                    if(NumberHelper.isNumber(boxNumStr)){
                        boxNum = Integer.parseInt(boxNumStr);
                    }
                    log.info("根据运单号:{}获取到的青流箱数量为:{}",extPro.getWaybillCode(),boxNumStr);
                    clearBoxNum += boxNum;
                }
            }
        }

        CycleBox cycleBox = new CycleBox();
        cycleBox.setWaybillCodeList(waybillCodeList);
        cycleBox.setCycleBoxNum(clearBoxNum);

        return cycleBox;
    }


    /**
     * 生成同步青流箱状态的任务--发出状态
     *
     * @param request
     */
    public void addCycleBoxStatusTask(WaybillCodeListRequest request) {
        if (request == null || request.getWaybillCodeList() == null || request.getWaybillCodeList().size() < 1) {
            return;
        }
        //循环waybillCodeList，生成任务
        List<String> waybillCodeList = request.getWaybillCodeList();
        for (String waybillCode : waybillCodeList) {
            RecyclableBoxRequest recyclableBoxRequest = new RecyclableBoxRequest();

            recyclableBoxRequest.setWayBillNo(waybillCode);
            recyclableBoxRequest.setOperator(request.getUserCode().toString()); //我们传的都是操作人id，任务处理时需要转换成操作人erp
            recyclableBoxRequest.setOperateTime(request.getOperateTime());
            recyclableBoxRequest.setThreeLevelNodeNo(request.getSiteCode().toString()); //分拣中心编码

            Task task = new Task();
            task.setBody(JsonHelper.toJson(recyclableBoxRequest));
            task.setType(Task.TASK_TYPE_CYCLE_BOX_STATUS);
            task.setTableName(Task.getTableName(Task.TASK_TYPE_CYCLE_BOX_STATUS));
            task.setCreateSiteCode(request.getSiteCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setKeyword1(waybillCode);

            taskService.add(task);
        }
    }

    /**
     * 根据流水号调运输系统获取青流箱箱号列表
     *
     * @param batchCode
     * @return
     */
    public CycleBox getCycleBoxByBatchCode(String batchCode) {
        CycleBox cycleBox = new CycleBox();
        cycleBox.setBatchCode(batchCode);

        //获取青流箱 箱号列表
        List<String> cycleBoxCodeList = tmsBossQueryManager.getRecyclingBoxFaceInfoByBatchCode(batchCode);

        if (cycleBoxCodeList != null) {
            cycleBox.setCycleBoxCodeList(cycleBoxCodeList);
            cycleBox.setCycleBoxNum(cycleBoxCodeList.size());
        }
        return cycleBox;
    }


    /**
     * 同步青流箱状态
     * 1.根据运单号获取青流箱明细
     * 2.发送JMQ
     *
     * @param task
     */
    public boolean pushCycleBoxStatus(Task task) {
        if(log.isDebugEnabled()){
            log.debug("同步青流箱状态：{}" , JsonHelper.toJson(task));
        }

        boolean isSuccess = true;
        try {
            RecyclableBoxRequest request = JsonHelper.fromJson(task.getBody(), RecyclableBoxRequest.class);

            //运单号
            String waybillCode = request.getWayBillNo();
            if (StringUtils.isNotBlank(waybillCode)) {
                //根据运单号获取青流箱箱号列表
                List<String> cycleBoxList = cycleBoxExternalManager.getCbUniqueNoByWaybillCode(waybillCode);
                if(log.isDebugEnabled()){
                    log.debug("同步青流箱状态，根据运单号{}获取到的青流箱号列表为:{}" ,waybillCode, JsonHelper.toJson(cycleBoxList));
                }
                //清流箱号不为空，发送MQ
                if (cycleBoxList != null && cycleBoxList.size() > 0) {
                    request.setUniqueCode(cycleBoxList); //设置青流箱号列表
                    request.setNodeType(CYCLE_BOX_STATUS_SEND_OUT); //设置清流箱号状态为发出状态
                    //设置操作人erp
                    if (StringUtils.isNotBlank(request.getOperator())) {
                        try {
                            Integer userCode = Integer.parseInt(request.getOperator());
                            BaseStaffSiteOrgDto staffdto = baseMajorManager.getBaseStaffByStaffId(userCode);
                            if (staffdto != null) {
                                request.setOperator(staffdto.getErp());
                            }
                        } catch (Exception e) {
                            log.error("同步青流箱，根据操作人编码获取操作人erp异常.", e);
                        }
                    }
                    recyclableBoxSend(request);
                }
            }
        }catch (Exception e){
            log.error("处理同步青流箱任务异常.",e);
            isSuccess = false;
        }

        return isSuccess;
    }

    /**
     * 根据发货请求获取运单号列表
     *
     * @param request
     * @return
     */
    private List<String> getWaybillCodeListByDeliveryRequest(List<DeliveryRequest> request) {
        Set<String> waybillCodeSet = new HashSet<String>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                //扫描的是运单号或者包裹号
                if (WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    String waybillCode = WaybillUtil.getWaybillCode(deliveryRequest.getBoxCode());
                    if (StringUtils.isNotBlank(waybillCode) && !waybillCodeSet.contains(waybillCode)) {
                        waybillCodeSet.add(waybillCode);
                    }
                } else if (BusinessUtil.isBoxcode(deliveryRequest.getBoxCode())) {
                    if (deliveryRequest.getSiteCode() == null) {
                        log.error("根据发货请求获取运单号列表,发货请求中站点信息为空：{}" , JsonHelper.toJson(deliveryRequest));
                        continue;
                    }
                    //扫描的是箱号，查sorting表，获取运单号列表
                    Sorting sortingParam = new Sorting();
                    sortingParam.setBoxCode(deliveryRequest.getBoxCode());
                    sortingParam.setCreateSiteCode(deliveryRequest.getSiteCode());
                    List<Sorting> sortingList = this.tSortingService.findByBoxCode(sortingParam);
                    if (sortingList != null) {
                        for (Sorting sorting : sortingList) {
                            if (StringUtils.isNotBlank(sorting.getWaybillCode()) && !waybillCodeSet.contains(sorting.getWaybillCode())) {
                                waybillCodeSet.add(sorting.getWaybillCode());
                            }
                        }
                    }
                }
            }
        }
        if (waybillCodeSet.size() < 1) {
            return null;
        }
        return new ArrayList<String>(waybillCodeSet);
    }

    /**
     * 循环箱发MQ
     * @param request
     * @return
     */
    @Override
    public void recyclableBoxSend(RecyclableBoxRequest request) throws Exception{
        List<String> cycleBoxCodeListInBatch = new ArrayList<String >();
        List<String> cycleBoxCodeListScan = new ArrayList<String>();
        //逆向回收的需要对比流水号内的青流箱和扫描的青流箱的差异
        if(StringUtils.isNotBlank(request.getBatchCode()) && request.getNodeType().equals(CYCLE_BOX_STATUS_REVERSE_RECEIVE)){
            //获取青流箱 箱号列表
            cycleBoxCodeListInBatch = tmsBossQueryManager.getRecyclingBoxFaceInfoByBatchCode(request.getBatchCode());
            cycleBoxCodeListScan = request.getUniqueCode();
            if(cycleBoxCodeListInBatch != null){
                for(String cycleBoxCode : cycleBoxCodeListScan){
                    if(cycleBoxCodeListInBatch.contains(cycleBoxCode)){
                        cycleBoxCodeListInBatch.remove(cycleBoxCode);
                    }
                }
            }

            //现场扫描的清流箱号发【逆向回收】的mq
            if(cycleBoxCodeListScan != null && cycleBoxCodeListScan.size() > 0) {
                request.setUniqueCode(cycleBoxCodeListScan);
                request.setNodeType(CYCLE_BOX_STATUS_REVERSE_RECEIVE);
                pushCycleBoxStatusMQ(request);
            }

            //差异的青流箱号发【异常】的mq
            if(cycleBoxCodeListInBatch != null && cycleBoxCodeListInBatch.size() > 0){
                request.setUniqueCode(cycleBoxCodeListInBatch);
                request.setNodeType(CYCLE_BOX_STATUS_REVERSE_RECEIVE_EXCEPTION);
                pushCycleBoxStatusMQ(request);
            }
        }else{
            //其他发出的正常
            pushCycleBoxStatusMQ(request);
        }
    }

    /**
     * 根据箱号获取箱号绑定的集包袋
     * @param boxCode
     * @return
     */
    @Override
    public String getBoxMaterialRelation(String boxCode) {
      String res = null;
      BoxMaterialRelation boxMaterial = boxMaterialRelationService.getDataByBoxCode(boxCode);
      if (boxMaterial != null) {
         res=boxMaterial.getMaterialCode();
      }
      return res;
    }





    /**
     * 绑定集包袋校验逻辑
     *  1.集包袋规则校验
     *  2.该箱号已发货，不能再绑定集包袋
     *  3. 如果本地场地已经绑定了箱号 而且已在4小时内发货 ，不能再绑定
     *  4. 若循环集包袋绑定的最近2个箱号在本场地发货过，，且发货的目的地相同时，弹窗提示「疑似虚假操作，当前AD码已锁定，请使用其它循环袋」，并禁止操作集包袋绑定
     *  5.此集包袋号曾经存在绑定箱号时做此校验 此场景使用的箱号是集包袋曾经绑定的箱号 集包袋绑定的箱号，在当前场地操作收箱 集包袋绑定的箱号的目的地不是本场地
     * @param request
     * @return
     */
    public InvokeResult boxMaterialRelationAlterOfCheck(BoxMaterialRelationRequest request) {
        InvokeResult result = new InvokeResult();
        result.success();
        // 忽略校验
        if (shouldSkipValidation(request)) {
            return result;
        }
        //集包袋规则校验
        // 如果是LL类型箱号，只能绑定围板箱或者笼车号
        if (BusinessHelper.isLLBoxType(request.getBoxCode().substring(0, 2))) {
            if (!BusinessUtil.isLLBoxBindingCollectionBag(request.getMaterialCode())) {
                result.setCode(Integer.valueOf(LL_BOX_BINDING_MATERIAL_TYPE_ERROR));
                result.setMessage(HintService.getHint(HintCodeConstants.LL_BOX_BINDING_MATERIAL_TYPE_ERROR, Boolean.TRUE));
            }
        }else {
            if (!BusinessUtil.isCollectionBag(request.getMaterialCode())) {
                result.setCode(Integer.valueOf(HintCodeConstants.CYCLE_BOX_RULE_ERROR));
                result.setMessage(HintService.getHint(HintCodeConstants.CYCLE_BOX_RULE_ERROR, Boolean.TRUE));
                return result;
            }
            
        }
        //该箱号已发货，不能再绑定集包袋
        if (isBoxAlreadySent(request)) {
            result.setCode(InvokeResult.RESULT_BOX_SENT_CODE);
            result.setMessage(InvokeResult.RESULT_BOX_SENT_MESSAGE);
            return result;
        }
        //如果本地场地已经绑定了箱号 而且已在4小时内发货 ，不能再绑定
        if (isBoundWithin4Hours(request)) {
            result.customMessage(InvokeResult.RESULT_RFID_BIND_BOX_SENT_CODE,
                    InvokeResult.RESULT_RFID_BIND_BOX_SENT_MESSAGE);
            return result;
        }
        // 若循环集包袋绑定的最近2个箱号在本场地发货过，，且发货的目的地相同时，弹窗提示「疑似虚假操作，当前AD码已锁定，请使用其它循环袋」，并禁止操作集包袋绑定
        if (isSuspiciousOperation(request)) {
            result.setCode(Integer.valueOf(HintCodeConstants.CYCLE_BOX_IS_LOCK_ERROR));
            result.setMessage(HintService.getHint(HintCodeConstants.CYCLE_BOX_IS_LOCK_ERROR, Boolean.TRUE));
            return result;
        }
        //此集包袋号曾经存在绑定箱号时做此校验 此场景使用的箱号是集包袋曾经绑定的箱号 在当前场地操作收箱 集包袋绑定的箱号的目的地不是本场地
        if (!request.getForceFlag() && isBoxPreviouslyBound(request)) {
            result.setCode(Integer.valueOf(HintCodeConstants.CYCLE_BOX_NOT_BELONG_ERROR));
            result.setMessage(HintService.getHint(HintCodeConstants.CYCLE_BOX_NOT_BELONG_ERROR, Boolean.TRUE));
            return result;
        }

        return result;
    }
    
    /**
     * 跳过校验
     * @param request
     * @return
     */
    private boolean shouldSkipValidation(BoxMaterialRelationRequest request) {
        return BoxMaterialRelationRequest.BIZ_SORTING_MACHINE.equals(request.getBizSource()) ||
                Constants.CONSTANT_NUMBER_TWO == request.getBindFlag();
    }

    /**
     * 该箱号已发货，不能再绑定集包袋
     * @param request
     * @return
     */
    private boolean isBoxAlreadySent(BoxMaterialRelationRequest request) {
        SendM queryPara = new SendM();
        queryPara.setBoxCode(request.getBoxCode());
        queryPara.setCreateSiteCode(request.getSiteCode());
        List<SendM> sendMList = sendMManager.findSendMByBoxCode(queryPara);
        return sendMList != null && !sendMList.isEmpty();
    }

    /**
     *  如果本地场地已经绑定了箱号 而且已在4小时内发货 ，不能再绑定
     * @param request
     * @return
     */
    private boolean isBoundWithin4Hours(BoxMaterialRelationRequest request) {
        BoxMaterialRelation boxMaterial = boxMaterialRelationService.getDataByMaterialCode(request.getMaterialCode());
        if (boxMaterial != null && request.getSiteCode().equals(boxMaterial.getSiteCode())) {
            SendM queryPara = new SendM();
            queryPara.setBoxCode(boxMaterial.getBoxCode());
            queryPara.setCreateSiteCode(request.getSiteCode());
            List<SendM> sendMList = sendMManager.findSendMByBoxCode(queryPara);
            if (CollectionUtils.isNotEmpty(sendMList)) {
                Date nowSubtract4Hour = DateHelper.add(new Date(), Calendar.HOUR, -4);
                for (SendM sendM : sendMList) {
                    if (sendM.getOperateTime().getTime() > nowSubtract4Hour.getTime()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 若循环集包袋绑定的最近2个箱号在本场地发货过，，且发货的目的地相同时，弹窗提示「疑似虚假操作，当前AD码已锁定，请使用其它循环袋」，并禁止操作集包袋绑定
     * @param request
     * @return
     */
    private boolean isSuspiciousOperation(BoxMaterialRelationRequest request) {

        List<BoxMaterialRelation> boxMaterialRelations = boxMaterialRelationService.getLimitDataByMaterialCode(request.getMaterialCode(), Constants.CONSTANT_NUMBER_TWO);
        if(boxMaterialRelations==null
            || boxMaterialRelations.isEmpty()
            || boxMaterialRelations.size() < Constants.CONSTANT_NUMBER_TWENTY){
            if(log.isInfoEnabled()){
                log.info("isSuspiciousOperation boxMaterialRelations size < 20 ,{},{}",JsonHelper.toJson(request),JsonHelper.toJson(boxMaterialRelations));
            }
            return false;
        }
        SendM querySendParam = new SendM();
        querySendParam.setCreateSiteCode(request.getSiteCode());
        querySendParam.setBoxCodeList(boxMaterialRelations.stream()
                .map(BoxMaterialRelation::getBoxCode)
                .collect(Collectors.toList()));
        List<SendM> sends = sendMManager.batchQuerySendMListBySiteAndBoxes(querySendParam);
        if(log.isInfoEnabled()){
            log.info("isSuspiciousOperation param:{} , sends:{}",JsonHelper.toJson(querySendParam),JsonHelper.toJson(sends));
        }
        if(sends==null
                || sends.isEmpty()
                || sends.size() < Constants.CONSTANT_NUMBER_TWENTY){
            if(log.isInfoEnabled()){
                log.info("isSuspiciousOperation sends size < 0 ,{},{}",JsonHelper.toJson(request),JsonHelper.toJson(boxMaterialRelations));
            }
            return false;
        }
        Collections.sort(sends, (o1, o2) -> -DateHelper.compare(o1.getOperateTime(), o2.getOperateTime()));
        Integer lastReceiveSiteCode = null;
        for (SendM sendM : sends) {
            if (lastReceiveSiteCode != null && lastReceiveSiteCode.equals(sendM.getReceiveSiteCode())) {
                    return true;
            }
            lastReceiveSiteCode = sendM.getReceiveSiteCode();
        }

        return false;
    }

    /**
     * 此集包袋号曾经存在绑定箱号时做此校验 此场景使用的箱号是集包袋曾经绑定的箱号 在当前场地操作收箱 集包袋绑定的箱号的目的地不是本场地
     * @param request
     * @return
     */
    private boolean isBoxPreviouslyBound(BoxMaterialRelationRequest request) {
        BoxMaterialRelation boxMaterial = boxMaterialRelationService.getDataByMaterialCode(request.getMaterialCode());
        if(log.isInfoEnabled()){
            log.info("isBoxPreviouslyBound boxMaterial param:{} ,result:{}",JsonHelper.toJson(request),JsonHelper.toJson(boxMaterial));
        }
        if (boxMaterial != null) {
            Receive receive = receiveService.findLastByBoxCodeAndSiteCode(boxMaterial.getBoxCode(), request.getSiteCode());
            if (receive == null || receive.getBoxCode() == null) {
                return true;
            }
            Box box = boxService.findBoxByCode(boxMaterial.getBoxCode());
            return box != null && !box.getReceiveSiteCode().equals(request.getSiteCode());
        }
        return false;
    }


    /**
     * 绑定、删除集包袋
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CycleBoxServiceImpl.boxMaterialRelationAlter", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult boxMaterialRelationAlter(BoxMaterialRelationRequest request){
        InvokeResult result = new InvokeResult();
        result.success();
        if(request.getForceFlag()==null){
            request.setForceFlag(Boolean.TRUE);
        }
        //自动化设备不校验  解绑不校验  终端不校验
        if(!shouldSkipValidation(request)){

            InvokeResult businessCheck = boxMaterialRelationAlterOfCheck(request);
            if(!businessCheck.codeSuccess()){
                return businessCheck;
            }

        }

        BoxMaterialRelation par = new BoxMaterialRelation();
        par.setBoxCode(request.getBoxCode());
        par.setMaterialCode(request.getMaterialCode());
        par.setBindFlag(request.getBindFlag());
        par.setOperatorErp(request.getOperatorERP());
        par.setSiteCode(request.getSiteCode());
        par.setIsDelete(0);

        //如果从未绑定过
        if (boxMaterialRelationService.getCountByBoxCode(request.getBoxCode())<=0){
            if (boxMaterialRelationService.add(par)==0){
                result.error("保存循环集包袋绑定关系失败！");
                return result;
            }

        }

        if (boxMaterialRelationService.update(par)==0){
            result.error("箱号已绑定其他循环集包袋，更新绑定关系失败！");
            return result;
        }
        //其他绑定了该循环集包袋的都解绑
        int count = boxMaterialRelationService.updateUnBindByMaterialCode(par);
        return result;

    }

    /**
     * 外单靑流箱绑定发MQ
     * @param request
     * @return
     */
    @Override
    public InvokeResult cycleBoxBindToWD(OrderBindMessageRequest request)  throws Exception{
        InvokeResult result = new InvokeResult();
        result.success();

        log.info("发送外单靑流箱MQ：{}" , JsonHelper.toJson(request));
        dmsCycleBoxBindNotice.send(request.getWaybillNo(),JsonHelper.toJson(request));
        return result;
    }

    /**
     * 判断单个箱号绑定情况
     * @param request
     * @return
     */
    @Override
    public InvokeResult<BoxCodeGroupBinDingDto> checkBingResult(BoxMaterialRelationRequest request) {
        InvokeResult<BoxCodeGroupBinDingDto> result = new InvokeResult();
        BoxCodeGroupBinDingDto boxCodeGroupBinDingDto = new BoxCodeGroupBinDingDto();
        result.success();
        result.setData(boxCodeGroupBinDingDto);
        String  boxCode  = request.getBoxCode();
        // 1.先查询箱信息
        Box box = boxService.findBoxByCode(boxCode);
        if(box==null){
            result.customMessage(InvokeResult.RESULT_NO_BOX_CODE,InvokeResult.RESULT_NO_BOX_MESSAGE);
            return result;
        }

        //2.查询箱号绑定关系(BC非BC均查询)
        String boxMaterialCode = this.getBoxMaterialRelation(boxCode);
        if(StringUtils.isEmpty(boxMaterialCode)){
            // 2.1 不是BC的不拦截
            if(!BusinessHelper.isBCBoxType(box.getType())){
                return result;
            }

            //2.2 判断BC箱号绑定循环集包袋拦截状态
            if(!getBCFilterFlag(request.getSiteCode())){
                return result;
            }

            // 未绑定循环集包袋
            result.customMessage(InvokeResult.RESULT_BC_BOX_NO_BINDING_CODE,InvokeResult.RESULT_BC_BOX_NO_BINDING_MESSAGE);
            return result;
        }
        result.getData().setBinDingMaterialCode(boxMaterialCode);
        return result;
    }

    /**
     * 查询分组绑定循环集包袋状态
     * @param request
     * @return
     */
    public InvokeResult<BCGroupBinDingDto> checkGroupBingResult(BoxMaterialRelationRequest request){
        InvokeResult<BCGroupBinDingDto> result = new InvokeResult();
        result.success();

        BCGroupBinDingDto bcGroupBinDingDto = new BCGroupBinDingDto();
         // 3.开启分组扫描添加(且是拦截状态)-查询分组绑定情况
        List<GroupBoxDto> noBinDingList = new ArrayList<GroupBoxDto>(); // 没绑定的
        List<GroupBoxDto> binDingList = new ArrayList<GroupBoxDto>(); // 没绑定的
        for (GroupBoxDto  groupBoxDto: request.getGroupList()){
            // 只统计BC 箱号类型
            if(groupBoxDto.getBoxType().equals(BoxTypeEnum.TYPE_BC.getCode())){
                String materialCode =  this.getBoxMaterialRelation(groupBoxDto.getBoxCode());
                if(StringUtils.isEmpty(materialCode)){
                    noBinDingList.add(groupBoxDto);
                }else {
                    groupBoxDto.setMaterialCode(materialCode);
                    binDingList.add(groupBoxDto);
                }
            }
        }
        bcGroupBinDingDto.setBinDingList(binDingList);
        bcGroupBinDingDto.setNoBingDingList(noBinDingList);
        result.setData(bcGroupBinDingDto);

        if(CollectionUtils.isNotEmpty(noBinDingList)){
            result.customMessage(InvokeResult.RESULT_BC_BOX_GROUP_NO_BINDING_CODE,"同组箱号"+noBinDingList.get(0).getBoxCode()+"未绑定循环集包袋,分组共"+noBinDingList.size()+"个未绑定");
            return result;
        }
        return result;
    }

    @Override
    public List<BoxMaterialRelation> getBoxMaterialRelationList(List<String> boxCodeList) {
        return boxMaterialRelationService.getDataByBoxCodeList(boxCodeList);
    }


    //获取开关状态
    private boolean getBCFilterFlag(Integer siteCode){
       return   funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(),siteCode);
    }


    /**
     * 发送青流箱状态的MQ消息
     * @param request
     * @return
     */
    private void pushCycleBoxStatusMQ(RecyclableBoxRequest request) throws Exception{
        String businessId = "";
        if (StringUtils.isNotBlank(request.getBatchCode())) {
            businessId = request.getBatchCode();
        } else if (StringUtils.isNotBlank(request.getWayBillNo())) {
            businessId = request.getWayBillNo();
        }
        request.setSourceSysCode("DMS");
        recyclableBoxSendMQ.send(businessId, JsonHelper.toJson(request));
    }
}
