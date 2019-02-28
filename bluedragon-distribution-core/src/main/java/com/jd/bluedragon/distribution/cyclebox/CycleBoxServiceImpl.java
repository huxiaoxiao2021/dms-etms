package com.jd.bluedragon.distribution.cyclebox;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.TMSBossQueryManager;
import com.jd.bluedragon.core.base.CycleBoxExternalManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.RecyclableBoxRequest;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.bluedragon.distribution.send.domain.RecyclableBoxSend;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.WaybillExtPro;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("cycleBoxService")
public class CycleBoxServiceImpl implements CycleBoxService {
    private final Logger logger = Logger.getLogger(CycleBoxServiceImpl.class);
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
                    logger.info("根据运单号:" + extPro.getWaybillCode() + "获取到的青流箱数量为:"+boxNumStr);
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
        logger.info("同步青流箱状态：" + JsonHelper.toJson(task));

        boolean isSuccess = true;
        try {
            RecyclableBoxRequest request = JsonHelper.fromJson(task.getBody(), RecyclableBoxRequest.class);

            //运单号
            String waybillCode = request.getWayBillNo();
            if (StringUtils.isNotBlank(waybillCode)) {
                //根据运单号获取青流箱箱号列表
                List<String> cycleBoxList = cycleBoxExternalManager.getCbUniqueNoByWaybillCode(waybillCode);
                logger.info("同步青流箱状态，根据运单号" + waybillCode + "获取到的青流箱号列表为:" + JsonHelper.toJson(cycleBoxList));
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
                            logger.error("同步青流箱，根据操作人编码获取操作人erp异常.", e);
                        }
                    }
                    recyclableBoxSend(request);
                }
            }
        }catch (Exception e){
            logger.error("处理同步青流箱任务异常.",e);
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
                        logger.error("根据发货请求获取运单号列表,发货请求中站点信息为空" + JsonHelper.toJson(deliveryRequest));
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
    public RecyclableBoxSend recyclableBoxSend(RecyclableBoxRequest request){
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
        }

        RecyclableBoxSend res=new RecyclableBoxSend();

        //现场扫描的清流箱号发【逆向回收】的mq
        if(cycleBoxCodeListScan != null && cycleBoxCodeListScan.size() > 0) {
            request.setUniqueCode(cycleBoxCodeListScan);
            request.setNodeType(CYCLE_BOX_STATUS_REVERSE_RECEIVE);
            res= pushCycleBoxStatusMQ(request);
        }

        //差异的青流箱号发【异常】的mq
        if(cycleBoxCodeListInBatch != null && cycleBoxCodeListInBatch.size() > 0){
            request.setUniqueCode(cycleBoxCodeListInBatch);
            request.setNodeType(CYCLE_BOX_STATUS_REVERSE_RECEIVE_EXCEPTION);
            res= pushCycleBoxStatusMQ(request);
        }
        return res;
    }

    /**
     * 发送青流箱状态的MQ消息
     * @param request
     * @return
     */
    private RecyclableBoxSend pushCycleBoxStatusMQ(RecyclableBoxRequest request){
        RecyclableBoxSend res=new RecyclableBoxSend();
        try {
            String businessId = "";
            if(StringUtils.isNotBlank(request.getBatchCode())){
                businessId = request.getBatchCode();
            }else if(StringUtils.isNotBlank(request.getWayBillNo())){
                businessId = request.getWayBillNo();
            }

            request.setSourceSysCode("DMS");
            recyclableBoxSendMQ.send(businessId, JsonHelper.toJson(request));
            res.setCode(JdResponse.CODE_OK);
            res.setMessage(JdResponse.MESSAGE_OK);
        } catch (Exception e) {
            res.setCode(JdResponse.CODE_TIME_ERROR);
            res.setMessage(e.getMessage());
            logger.error("[PDA循环箱]发送MQ消息时发生异常", e);
        }

        return res;
    }
}
