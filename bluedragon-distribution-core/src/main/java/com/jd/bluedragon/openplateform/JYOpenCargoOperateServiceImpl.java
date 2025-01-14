package com.jd.bluedragon.openplateform;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.ReceiveRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.middleend.SortingServiceFactory;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.jmq.common.exception.JMQException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.openplateform
 * @ClassName: JYOpenCargoOperateServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/7 01:00
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class JYOpenCargoOperateServiceImpl implements IJYOpenCargoOperate {

    @Autowired
    private TaskService taskService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private SortingServiceFactory sortingServiceFactory;

    @Autowired
    @Qualifier("jyOpenPlatformSendProducer")
    private DefaultJMQProducer jyOpenPlatformSendProducer;

    @Autowired
    @Qualifier("jyOpenPlatformSendFinishProducer")
    private DefaultJMQProducer jyOpenPlatformSendFinishProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BoxService boxService;

    @Override
    public InvokeResult<Boolean> inspection(JYCargoOperateEntity entity) {
        // 纯箱号时，查箱号内明细，发验货记录 + 收货记录
        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(entity.getBarcode());
        // 如果是箱号
        if(BarCodeType.BOX_CODE.equals(barCodeType)){
            final List<SendDetail> boxDetailList = deliveryService.getCancelSendByBox(entity.getBarcode());
            String boxCode = entity.getBarcode();
            addReceiveTask(entity, boxCode);
            for (SendDetail sendDetail : boxDetailList) {
                entity.setBarcode(sendDetail.getPackageBarcode());
                addInspectionTask(entity, boxCode);
            }
        } else {
            if(Objects.equals(BarCodeType.PACKAGE_CODE, barCodeType) || Objects.equals(BarCodeType.WAYBILL_CODE, barCodeType)){
                final Boolean waybillExist = waybillQueryManager.queryExist(WaybillUtil.getWaybillCode(entity.getBarcode()));
                if(!Objects.equals(waybillExist, true)){
                    log.error("JYOpenCargoOperateServiceImpl.inspection 运单号{}在京东运单系统中不存在 param {}", entity.getBarcode(), JsonHelper.toJson(entity));
                    return new InvokeResult<>();
                }
            }
            addInspectionTask(entity, null);
        }

        return new InvokeResult<>();
    }

    /**
     * 添加验货任务
     */
    private void addInspectionTask(JYCargoOperateEntity entity, String boxCode){
        Integer businessType = Constants.BUSSINESS_TYPE_POSITIVE;
        InspectionRequest inspection = new InspectionRequest();
        inspection.setUserCode(entity.getOperatorInfo().getOperateUserId());
        inspection.setUserName(entity.getOperatorInfo().getOperateUserName());
        inspection.setSiteCode(entity.getOperatorInfo().getOperateSiteId());
        inspection.setSiteName(entity.getOperatorInfo().getOperateSiteName());
        inspection.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        inspection.setBusinessType(businessType);
        inspection.setPackageBarOrWaybillCode(entity.getBarcode());
        inspection.setBizSource(InspectionBizSourceEnum.valueOf(entity.getRequestProfile().getSysSource()).getCode());
        inspection.setBoxCode(boxCode);
        TaskRequest request = new TaskRequest();
        request.setBusinessType(businessType);
        request.setKeyword1(String.valueOf(entity.getOperatorInfo().getOperateSiteId()));
        request.setKeyword2(entity.getBarcode());
        request.setBoxCode(boxCode);
        request.setType(Task.TASK_TYPE_INSPECTION);
        request.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        request.setSiteCode(entity.getCreateSiteId());
        request.setSiteName(entity.getCreateSiteName());
        request.setUserCode(entity.getOperatorInfo().getOperateUserId());
        request.setUserName(entity.getOperatorInfo().getOperateUserName());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task = this.taskService.toTask(request, eachJson);
        if (log.isDebugEnabled()) {
            log.debug("JYOpenCargoOperateServiceImpl.addInspectionTask {}", JsonHelper.toJson(task));
        }
        this.taskService.add(task, true);

    }

    /**
     * 添加收货任务
     * @param entity
     * @param boxCode
     */
    private void addReceiveTask(JYCargoOperateEntity entity, String boxCode) {
        ReceiveRequest receiveRequest = new ReceiveRequest();
        receiveRequest.setShieldsCarCode(Constants.EMPTY_FILL);
        receiveRequest.setCarCode(Constants.EMPTY_FILL);
        receiveRequest.setPackOrBox(boxCode);
        receiveRequest.setId(0);
        Integer businessType = Constants.BUSSINESS_TYPE_POSITIVE;
        receiveRequest.setBusinessType(businessType);
        receiveRequest.setUserCode(entity.getOperatorInfo().getOperateUserId());
        receiveRequest.setUserName(entity.getOperatorInfo().getOperateUserName());
        receiveRequest.setSiteCode(entity.getOperatorInfo().getOperateSiteId());
        receiveRequest.setSiteName(entity.getOperatorInfo().getOperateSiteName());
        receiveRequest.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        receiveRequest.setSealBoxCode(Constants.EMPTY_FILL);
        receiveRequest.setTurnoverBoxCode(Constants.EMPTY_FILL);

        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET + JsonHelper.toJson(receiveRequest) + Constants.PUNCTUATION_CLOSE_BRACKET;

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setType(Task.TASK_TYPE_RECEIVE);
        taskRequest.setSiteCode(entity.getCreateSiteId());
        taskRequest.setSiteName(entity.getCreateSiteName());
        taskRequest.setReceiveSiteCode(entity.getOperatorInfo().getOperateSiteId());
        taskRequest.setKeyword1(String.valueOf(entity.getOperatorInfo().getOperateSiteId()));
        taskRequest.setKeyword2(entity.getBarcode());
        taskRequest.setBoxCode(boxCode);
        final Task task = this.taskService.toTask(taskRequest, eachJson);
        if (log.isDebugEnabled()) {
            log.debug("JYOpenCargoOperateServiceImpl.addReceiveTask {}", JsonHelper.toJson(task));
        }

        this.taskService.add(task);
    }

    @Override
    public InvokeResult<Boolean> sorting(JYCargoOperateEntity entity) {

        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(entity.getBarcode());
        if(Objects.equals(BarCodeType.PACKAGE_CODE, barCodeType) || Objects.equals(BarCodeType.WAYBILL_CODE, barCodeType)){
            final Boolean waybillExist = waybillQueryManager.queryExist(WaybillUtil.getWaybillCode(entity.getBarcode()));
            if(!Objects.equals(waybillExist, true)){
                log.error("JYOpenCargoOperateServiceImpl.sorting 运单号{}在京东运单系统中不存在 param {}", entity.getBarcode(), JsonHelper.toJson(entity));
                return new InvokeResult<>();
            }
        }

        this.handleDpBox(entity);

        if (Objects.equals(entity.getDataOperateType(),"ADD")) {
            Task task=new Task();
            task.setKeyword1(entity.getBoxCode());
            task.setKeyword2(entity.getPackageCode());
            task.setCreateSiteCode(entity.getCreateSiteId());
            task.setReceiveSiteCode(entity.getReceiveSiteId());
            task.setCreateTime(new Date(entity.getOperatorInfo().getOperateTime()));
            task.setType(Task.TASK_TYPE_SORTING);
            task.setBoxCode(entity.getBoxCode());
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            StringBuilder fingerprint = new StringBuilder("");
            fingerprint.append(task.getCreateSiteCode()).append("_")
                    .append(task.getReceiveSiteCode()).append("_").append(task.getBusinessType())
                    .append("_").append(task.getBoxCode()).append("_").append(task.getKeyword2())
                    .append("_").append(DateHelper.formatDateTimeMs(task.getOperateTime()));
            task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
            List<SortingRequest> list=new ArrayList<SortingRequest>(1);

            SortingRequest request=new SortingRequest();
            request.setBoxCode(entity.getBoxCode());
            request.setFeatureType(0);
            request.setIsCancel(0);
            request.setIsLoss(0);
            request.setReceiveSiteCode(entity.getReceiveSiteId());
            request.setReceiveSiteName(entity.getReceiveSiteName());
            request.setWaybillCode(entity.getWaybillCode());
            request.setPackageCode(entity.getPackageCode());
            request.setBusinessType(10);
            request.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
            request.setSiteCode(entity.getCreateSiteId());
            request.setSiteName(entity.getCreateSiteName());
            request.setUserCode(entity.getOperatorInfo().getOperateUserId());
            request.setUserName(entity.getOperatorInfo().getOperateUserName());
            list.add(request);
            task.setOwnSign(BusinessHelper.getOwnSign()); //fix 增加ownsign避免直接写库没有ownsign问题
            task.setBody(JsonHelper.toJson(list));
            log.info("插入分拣任务为：{}",task.toString());
            taskService.add(task,true);
        } else if (Objects.equals(entity.getDataOperateType(), "DELETE")) {
            log.info("执行取消分拣任务,{}" , JsonHelper.toJson(entity));
            Sorting sorting = new Sorting();
            String aPackageCode = entity.getPackageCode();
            sorting.setBoxCode(entity.getBoxCode());
            sorting.setOperateTime(new Date(entity.getOperatorInfo().getOperateTime()));//增加操作时间

            if (WaybillUtil.isPackageCode(aPackageCode)) {
                sorting.setPackageCode(aPackageCode);
            } else if (WaybillUtil.isWaybillCode(aPackageCode)) {
                sorting.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
            } else if (BusinessHelper.isBoxcode(aPackageCode)) {
                sorting.setBoxCode(aPackageCode);
            }

            sorting.setCreateSiteCode(entity.getCreateSiteId());
            sorting.setUpdateUser(entity.getCreateSiteName());
            sorting.setUpdateUserCode(entity.getOperatorInfo().getOperateUserId());
            sorting.setUpdateUser(entity.getOperatorInfo().getOperateUserName());
            sorting.setType(10);

            sorting.setBizSource(SortingBizSourceEnum.valueOf(entity.getRequestProfile().getSysSource()).getCode());
            sortingServiceFactory.getSortingService(entity.getCreateSiteId()).cancelSorting(sorting);
        }

        return new InvokeResult<>();
    }

    private void handleDpBox(JYCargoOperateEntity entity) {
        try {
            if(BusinessHelper.isDPBoxCode(entity.getBoxCode())){
                // 德邦箱号兼容处理
                final Box box = boxService.findBoxByCode(entity.getBoxCode());
                if(box == null){
                    Box boxAdd = new Box();
                    boxAdd.setId(boxService.newBoxId());
                    boxAdd.setCode(entity.getBoxCode());
                    boxAdd.setType(Box.TYPE_DP);
                    boxAdd.setCreateSiteCode(entity.getCreateSiteId());
                    boxAdd.setCreateSiteName(entity.getCreateSiteName());
                    boxAdd.setReceiveSiteCode(entity.getReceiveSiteId());
                    boxAdd.setReceiveSiteName(entity.getReceiveSiteName());
                    boxAdd.setCreateTime(new Date(entity.getOperatorInfo().getOperateTime()));
                    boxAdd.setUpdateTime(boxAdd.getCreateTime());
                    boxAdd.setCreateUserCode(0);
                    boxAdd.setCreateUser(entity.getOperatorInfo().getOperateUserName());
                    boxAdd.setUpdateUserCode(boxAdd.getCreateUserCode());
                    boxAdd.setUpdateUser(boxAdd.getUpdateUser());
                    boxAdd.setTimes(1);
                    boxAdd.setStatus(Box.BOX_STATUS_SEND);
                    boxAdd.setTransportType(Box.BOX_TRANSPORT_TYPE_HIGHWAY);
                    boxAdd.setMixBoxType(0);
                    boxAdd.setLastNodeType(OpBoxNodeEnum.SEND.getNodeCode());
                    boxAdd.setBoxSource(BoxSystemTypeEnum.PRINT_CLIENT.getCode());
                    boxService.add(boxAdd);
                }
            }
        } catch (Exception e) {
            log.error("JYOpenCargoOperateServiceImpl.handleBox {}", JsonHelper.toJson(entity), e);
        }
    }

    @Override
    public InvokeResult<Boolean> send(JYCargoOperateEntity entity) {

        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(entity.getBarcode());
        if(Objects.equals(BarCodeType.PACKAGE_CODE, barCodeType) || Objects.equals(BarCodeType.WAYBILL_CODE, barCodeType)){
            final Boolean waybillExist = waybillQueryManager.queryExist(WaybillUtil.getWaybillCode(entity.getBarcode()));
            if(!Objects.equals(waybillExist, true)){
                log.error("JYOpenCargoOperateServiceImpl.send 运单号{}在京东运单系统中不存在 param {}", entity.getBarcode(), JsonHelper.toJson(entity));
                return new InvokeResult<>();
            }
        }

        if (Objects.equals(entity.getDataOperateType(), "ADD")) {
            SendBizSourceEnum bizSource = SendBizSourceEnum.valueOf(entity.getRequestProfile().getSysSource());
            SendM domain = new SendM();
            domain.setReceiveSiteCode(entity.getReceiveSiteId());
            domain.setCreateSiteCode(entity.getCreateSiteId());
            domain.setSendCode(entity.getSendCode());
            domain.setBoxCode(entity.getBarcode());//包裹号+箱号
            domain.setCreateUser(entity.getOperatorInfo().getOperateUserName());
            domain.setCreateUserCode(entity.getOperatorInfo().getOperateUserId());

            domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
            domain.setYn(Constants.YN_YES);

            if (entity.getOperatorInfo().getOperateTime() != null) {
                domain.setCreateTime(new Date(entity.getOperatorInfo().getOperateTime()));
                domain.setOperateTime(new Date(entity.getOperatorInfo().getOperateTime()));
            } else {
                domain.setCreateTime(new Date());
                domain.setOperateTime(new Date());
            }

            if (log.isDebugEnabled()) {
                log.debug("拣运开放发货--begin--参数【{}】", JsonHelper.toJson(domain));
            }
            //调用一车一单发货接口
            deliveryService.packageSend(bizSource, domain);
            if (log.isDebugEnabled()) {
                log.debug("拣运开放发货--end--参数【{}】", JsonHelper.toJson(domain));
            }
            this.sendOpenPlatformSendMq(entity);
        } else if (Objects.equals(entity.getDataOperateType(), "DELETE")) {
            log.info("执行取消发货开始：{}", JsonHelper.toJson(entity));
            SendM sendM = new SendM();
            sendM.setBoxCode(entity.getBarcode());
            sendM.setCreateSiteCode(entity.getCreateSiteId());
            sendM.setUpdateUserCode(entity.getOperatorInfo().getOperateUserId());
            sendM.setUpdaterUser(entity.getOperatorInfo().getOperateUserName());
            sendM.setSendType(10);
            sendM.setSendCode(entity.getSendCode());
            sendM.setOperateTime(new Date(entity.getOperatorInfo().getOperateTime()));
            if (!BusinessHelper.isBoxcode(entity.getBarcode())) {
                sendM.setReceiveSiteCode(entity.getReceiveSiteId());
            }
            sendM.setUpdateTime(sendM.getOperateTime());
            sendM.setYn(0);

            DeliveryResponse checkResponse = deliveryService.dellCancelDeliveryCheckSealCar(sendM);

            if (checkResponse!=null && !JdResponse.CODE_OK.equals(checkResponse.getCode())) {
                log.error("取消发货校验失败：{}", JsonHelper.toJson(checkResponse));
                return new InvokeResult(checkResponse.getCode(),checkResponse.getMessage(), null);
            }

            ThreeDeliveryResponse response = deliveryService.dellCancelDeliveryMessageWithOperateTime(sendM, true);
            log.info("取消发货结果：{}", JsonHelper.toJson(response));

        }
        return new InvokeResult<>();
    }

    private void sendOpenPlatformSendMq(JYCargoOperateEntity jyCargoOperateEntity) {
        try {
            if(log.isInfoEnabled()) {
                log.info("sendOpenPlatformSendMq param {}", JsonHelper.toJson(jyCargoOperateEntity));
            }
            jyOpenPlatformSendProducer.send(jyCargoOperateEntity.getPackageCode(), JsonHelper.toJson(jyCargoOperateEntity));
        } catch (JMQException e) {
            log.error("sendOpenPlatformSendMq exception {}", JsonHelper.toJson(jyCargoOperateEntity), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发货完成处理
     * @param entity 发货数据
     * @return 处理结果
     * @author fanggang7
     * @time 2023-06-13 21:36:57 周二
     */
    @Override
    public InvokeResult<Boolean> sendVehicleFinish(JYCargoOperateEntity entity) {
        try {
            if(log.isInfoEnabled()) {
                log.info("JYOpenCargoOperateServiceImpl sendVehicleFinish {}", JsonHelper.toJson(entity));
            }
            this.sendOpenPlatformSendFinishMq(entity);
            return new InvokeResult<>();
        } catch (Exception e) {
            log.error("JYOpenCargoOperateServiceImpl sendVehicleFinish exception {}", JsonHelper.toJson(entity), e);
            throw new RuntimeException(e);
        }
    }

    private void sendOpenPlatformSendFinishMq(JYCargoOperateEntity jyCargoOperateEntity) {
        try {
            if(log.isInfoEnabled()) {
                log.info("sendOpenPlatformSendFinishMq param {}", JsonHelper.toJson(jyCargoOperateEntity));
            }
            jyOpenPlatformSendFinishProducer.send(jyCargoOperateEntity.getPackageCode(), JsonHelper.toJson(jyCargoOperateEntity));
        } catch (JMQException e) {
            log.error("sendOpenPlatformSendFinishMq exception {}", JsonHelper.toJson(jyCargoOperateEntity), e);
            throw new RuntimeException(e);
        }
    }
}
