package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.domain.DmsWaybillTransferInterceptMQDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.web.LoginContext;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.preseparate.util.BusinessTypeEnum;
import com.jd.preseparate.util.OperationExpectEnum;
import com.jd.preseparate.util.OperationNodeEnum;
import com.jd.preseparate.util.TransferStatusEnum;
import com.jd.preseparate.vo.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 运单称重
 * B2B的称重量方简化功能，支持按运单/包裹号维度录入总重量（KG）和总体积（立方米）
 *
 * @author luyue  2017-12
 */
@Service
public class WeighByWaybillServiceImpl implements WeighByWaybillService {
    private static final Log logger = LogFactory.getLog(WeighByWaybillServiceImpl.class);

    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final String CASSANDRA_SIGN = "WaybillWeight_";

    @Autowired
    SysConfigService sysConfigService;

    /*运单接口 用于运单校验*/
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /*用于记录操作日志*/
    @Autowired
    private GoddessService goddessService;

    /* MQ消息生产者： topic:dms_waybill_weight*/
    @Autowired
    @Qualifier("weighByWaybillProducer")
    private DefaultJMQProducer weighByWaybillProducer;

    @Autowired
    @Qualifier("dmsWeightFlowService")
    private DmsWeightFlowService dmsWeightFlowService;
    @Autowired
    private TaskDao dao;

    @Autowired
    WaybillTraceManager waybillTraceManager;

    //topic:dms_waybill_transfer_intercept
    @Autowired
    @Qualifier("dmsWaybillTransferInterceptMQ")
    private DefaultJMQProducer dmsWaybillTransferInterceptMQ;


    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Value("${preseparate.systemCode}")
    private String preseparateSystemCode;

    @Autowired
    private TaskService taskService;


    private static final Integer FEATURE_TYPE_C2B = 7;
    private static final Integer FEATURE_TYPE_B2C = 8;
    private static final String SYSTEM_CODE_DMS ="DMS";

    /**
     * 运单称重信息录入入口 最终发送mq消息给运单部门
     *
     * @param vo 运单称重参数
     * @throws WeighByWaybillExcpetion 运单称重异常
     */
    public void insertWaybillWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion {
        String codeStr = vo.getCodeStr();
        /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
        String waybillCode = this.convertToWaybillCode(codeStr);

        /*将重量体积单位转为 运单部门要求的单位*/
        Double weight = this.convertWeightUnitToRequired(vo.getWeight());
        Double volume = this.convertVolumeUnitToRequired(vo.getVolume());
        WaybillWeightDTO waybillWeightDTO = new WaybillWeightDTO();
        BeanUtils.copyProperties(vo, waybillWeightDTO);
        waybillWeightDTO.setWaybillCode(waybillCode);
        waybillWeightDTO.setWeight(weight);
        waybillWeightDTO.setVolume(volume);
        waybillWeightDTO.setOperateTimeMillis(System.currentTimeMillis());

        /*经与运单方讨论,现阶段定为:无论是否存在运单,都将信息推送给运单,运单在后续流程补数据。
            为防止以后流程变动 将验证存在运单与不存在的对应处理方法拆开*/
        if (vo.getStatus().equals(VALID_EXISTS_STATUS_CODE)) {
            try {
                this.validWaybillProcess(waybillWeightDTO);
            } catch (WeighByWaybillExcpetion e) {
                throw e;
            }
        } else if (vo.getStatus().equals(VALID_NOT_EXISTS_STATUS_CODE)) {
            try {
                this.invalidWaybillProcess(waybillWeightDTO);
            } catch (WeighByWaybillExcpetion e) {
                throw e;
            }

        } else {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.InvalidMethodInvokeException);
        }

    }

    /**
     * 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号
     *
     * @param codeStr 运单号/包裹号
     * @return 运单号
     * @throws WeighByWaybillExcpetion 运单号/包裹号格式错误异常 UnknownCodeException
     */
    public String convertToWaybillCode(String codeStr) throws WeighByWaybillExcpetion {

        String waybillCode = null;
        logger.info("单号或包裹号正则校验"+codeStr);

        if (WaybillUtil.isPackageCode(codeStr))
        {
            waybillCode = WaybillUtil.getWaybillCode(codeStr);
        } else if (WaybillUtil.isWaybillCode(codeStr))
        {
            waybillCode = codeStr;
        } else
        {
            logger.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则"+codeStr);

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        if (null == waybillCode)
        {
            logger.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则"+codeStr);

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        return waybillCode;

    }

    /**
     * 对运单进行存在校验
     *
     * @param waybillCode 运单号
     * @return 是否存在运单
     * @throws WeighByWaybillExcpetion 运单称重异常 WaybillServiceNotAvailableException WaybillNotFindException
     */
    public boolean validateWaybillCodeReality(String waybillCode) throws WeighByWaybillExcpetion {
        BaseEntity<Waybill> waybillBaseEntity = null;

        try {
            waybillBaseEntity = waybillQueryManager.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e) {
            logger.error(e);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException);
        }

        int resultCode = waybillBaseEntity.getResultCode();
        Waybill waybill = waybillBaseEntity.getData();

        if (waybill == null) {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNotFindException);
        }

        //是否需要称重逻辑校验  2018 07 27  update 刘铎

        if (waybill.getWaybillSign() != null && BusinessUtil.isNoNeedWeight(waybill.getWaybillSign())) {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightException);
        }

        //校验是否已经妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            //弹出提示
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
        }
        return true;
    }

    /**
     * 当运单经校验存在时的流程
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion MQServiceNotAvailableException WaybillWeightVOConvertExcetion
     */
    public void validWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion {
        this.logToOperationlogCassandra(dto);
        this.sendMessageToMq(dto);
        //保存称重流水入库
        dmsWeightFlowService.saveOrUpdate(convertToDmsWeightFlow(dto));
    }

    /**
     * 对象转换为DmsWeightFlow
     *
     * @param dto
     * @return
     */
    private DmsWeightFlow convertToDmsWeightFlow(WaybillWeightDTO dto) {
        DmsWeightFlow dmsWeightFlow = new DmsWeightFlow();
        dmsWeightFlow.setBusinessType(Constants.BUSINESS_TYPE_WEIGHT);
        dmsWeightFlow.setOperateType(Constants.OPERATE_TYPE_WEIGHT_BY_WAYBILL);
        dmsWeightFlow.setDmsSiteCode(dto.getOperatorSiteCode());
        dmsWeightFlow.setDmsSiteName(dto.getOperatorSiteName());
        dmsWeightFlow.setWaybillCode(dto.getWaybillCode());
        dmsWeightFlow.setWeight(dto.getWeight());
        dmsWeightFlow.setVolume(dto.getVolume());
        dmsWeightFlow.setOperateTime(DateHelper.toDate(dto.getOperateTimeMillis()));
        dmsWeightFlow.setOperatorCode(dto.getOperatorId());
        dmsWeightFlow.setOperatorName(dto.getOperatorName());
        return dmsWeightFlow;
    }

    /**
     * 当运单经校验不存在时的流程
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion MQServiceNotAvailableException WaybillWeightVOConvertExcetion
     */
    public void invalidWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion {
        this.logToOperationlogCassandra(dto);
        this.sendMessageToMq(dto);
    }

    /**
     * 发送运单称重MQ消息
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion WaybillWeightVOConvertExcetion MQServiceNotAvailableException
     */
    private void sendMessageToMq(WaybillWeightDTO dto) throws WeighByWaybillExcpetion {
        try {
            weighByWaybillProducer.send(dto.getWaybillCode(), JsonHelper.toJson(dto));
        } catch (Exception e) {
            logger.error(e);

            /*如mq服务不可用，将转为message_task进行消息发送重试*/
            Task task = new Task();
            task.setBoxCode(dto.getWaybillCode());
            task.setCreateSiteCode(null);
            task.setKeyword1(weighByWaybillProducer.getTopic());
            task.setKeyword2(null);
            task.setTableName("task_message");
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(dto));
            task.setType(8000);

            dao.add(TaskDao.namespace, task);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableException);
        }

    }

    /**
     * 将待传输MQ的信息记录 Cassandra,便于特殊问题定则
     *
     * @param dto 操作消息对象
     */
    private void logToOperationlogCassandra(WaybillWeightDTO dto) {
        try {
            Goddess goddess = new Goddess();
            goddess.setKey(dto.getWaybillCode());
            goddess.setBody(JsonHelper.toJson(dto));
            goddess.setHead(CASSANDRA_SIGN + String.valueOf(dto.getStatus()));

            goddessService.save(goddess);
        } catch (Exception e) {
            logger.error("运单称重：cassandra操作日志记录失败：" + e);
        }
    }

    /**
     * 记录引操作人引起的异常
     *
     * @param dto 操作消息对象
     */
    public void errorLogForOperator(WaybillWeightVO dto,LoginContext loginContext,boolean isImport) {
        try {
            Goddess goddess = new Goddess();
            if(isImport){
                goddess.setKey("weightImportError");
            }else{
                goddess.setKey("weightError");
            }
            goddess.setBody(JsonHelper.toJson(dto)+"|"+JsonHelper.toJson(loginContext));
            goddess.setHead(loginContext==null?"null":loginContext.getPin());

            goddessService.save(goddess);
        } catch (Exception e) {
            logger.error("运单称重：cassandra操作日志记录失败：" + e);
        }
    }

    /**
     * 传入值为kg 标准为kg 重量现阶段单位相同不需转换 后续有可能变化
     *
     * @param weight kg
     * @return kg
     */
    private Double convertWeightUnitToRequired(Double weight) {
        return weight;
    }

    /**
     * 体积单位 传入值为立方米 运单要求标准为立方厘米
     *
     * @param cbm 立方米
     * @return 体积 立方厘米
     */
    private Double convertVolumeUnitToRequired(Double cbm) {
        return cbm * 1000000.0;
    }



    /**
     * b2b.weight.user.switch
     * 等于1 开启校验
     * 不维护 或者 等于0 不校验
     * @return
     */
    @Override
    public boolean isOpenIntercept(){

        return sysConfigService.getConfigByName("b2b.weight.user.switch");

    }

    /**
     * 判断是否转网--B转C
     * @return
     */
    public boolean waybillTransferB2C(WaybillWeightVO vo){
        boolean flag = false;
        //从vo中取出运单号、重量体积、操作人和操作站点信息
        String waybillCode = WaybillUtil.getWaybillCode(vo.getCodeStr());

        if(StringUtils.isNotBlank(waybillCode)){
            //调用运单接口获取waybillSign
            BaseEntity<String> baseEntity = waybillQueryManager.getWaybillSignByWaybillCode(waybillCode);
            if(baseEntity == null || baseEntity.getResultCode() != 1){
                logger.error("获取waybillSign失败,运单号:" + waybillCode + ".返回值:" + JSON.toJSONString(baseEntity));
                return false;
            }
            String waybillSign = baseEntity.getData();
            //如果是纯配外单，调用预分拣接口判断是否需要转网
            if(BusinessUtil.isForeignWaybill(waybillSign)
                    && BusinessUtil.isPureDeliveryWaybill(waybillSign)
                    && BusinessUtil.isSignChar(waybillSign,89,'0')){
                BatchTransferRequest batchTransferRequest =buildTransferRequest(vo,waybillCode,waybillSign);
                BaseResponseIncidental<BatchTransferResult> baseResponse = new BaseResponseIncidental<BatchTransferResult>();
                try {
                    baseResponse= preseparateWaybillManager.batchTransfer(batchTransferRequest);
                    logger.debug("调用预分拣批量转网接口返回值:" + JSON.toJSONString(baseResponse));
                    if (baseResponse == null || !baseResponse.getCode().equals(BaseResponse.CODE_OK)) {
                        logger.error("调用预分拣批量转网接口失败,参数:" + JSON.toJSONString(batchTransferRequest) + ",返回值:" + baseResponse);
                        return false;
                    }
                }catch(Exception e){
                    logger.error("调用预分拣批量转网接口异常.",e);                   
                    return false;
                }

                if(baseResponse.getData()!= null && baseResponse.getData().getTransferStatus().equals(TransferStatusEnum.already_transferred)){
                    flag = true;
                }
            }
        }

        //如果转网成功发拦截任务给分拣拦截并发送全称跟踪
        if(flag){
            //发送拦截任务
            pushInterceptTask(vo,FEATURE_TYPE_B2C);

            //发送全称跟踪
            sendWaybillTrace(vo);
        }

        return flag;
    }

    /**
     * 组装请求预分拣批量转网接口的参数
     * @param vo
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    private BatchTransferRequest buildTransferRequest(WaybillWeightVO vo,String waybillCode,String waybillSign){
        Double weight = this.convertWeightUnitToRequired(vo.getWeight());
        Double volume = this.convertVolumeUnitToRequired(vo.getVolume());
        Integer operatorId = vo.getOperatorId();
        Integer operateSiteCode = vo.getOperatorSiteCode();

        //根据操作人编码获取操作人erp
        String userErp = "";
        BaseStaffSiteOrgDto staffdto = baseMajorManager.getBaseStaffByStaffId(operatorId);
        if (staffdto == null || StringUtils.isBlank(staffdto.getErp())) {
            logger.error("根据操作人id:" + operatorId + "获取操作人erp失败.返回值:" + JSON.toJSONString(staffdto));
        } else {
            userErp = staffdto.getErp();
        }

        //组装参数
        BatchTransferRequest batchTransferRequest = new BatchTransferRequest();
        List<TransferRequestOrder> waybillList = new ArrayList<TransferRequestOrder>(0);
        TransferRequestOrder transferRequestOrder = new TransferRequestOrder();
        transferRequestOrder.setWaybillCode(waybillCode);
        transferRequestOrder.setWaybillSign(waybillSign);
        transferRequestOrder.setWeight(new BigDecimal(String.valueOf(weight)));
        transferRequestOrder.setVolume(new BigDecimal(String.valueOf(volume)));
        waybillList.add(transferRequestOrder);

        batchTransferRequest.setOrderList(waybillList);
        batchTransferRequest.setOperationNode(OperationNodeEnum.SORTING_CENTER);
        batchTransferRequest.setBusinessType(BusinessTypeEnum.DELIVER_ORDER);
        batchTransferRequest.setOperationBranchId(operateSiteCode);
        batchTransferRequest.setOperationExpect(OperationExpectEnum.transfer_to_station);
        batchTransferRequest.setOperatorErp(userErp);
        batchTransferRequest.setOperationTime(new Date());
        batchTransferRequest.setSystemCode(preseparateSystemCode);

        return batchTransferRequest;
    }

    /**
     * 发送转网拦截
     * @param vo
     * @param featureType
     */
    private void pushInterceptTask(WaybillWeightVO vo,Integer featureType){
        DmsWaybillTransferInterceptMQDto mqDto = new DmsWaybillTransferInterceptMQDto();
        mqDto.setWaybillCode(WaybillUtil.getWaybillCode(vo.getCodeStr()));
        mqDto.setFeatureType(featureType);
        mqDto.setOperatorCode(vo.getOperatorId());
        mqDto.setSiteCode(vo.getOperatorSiteCode());
        mqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
        mqDto.setSourceSystem(SYSTEM_CODE_DMS);
        dmsWaybillTransferInterceptMQ.sendOnFailPersistent(mqDto.getWaybillCode(),JSON.toJSONString(mqDto));
    }

    private void sendWaybillTrace(WaybillWeightVO vo){
        try {
            WaybillStatus waybillStatus = this.getWaybillStatus(vo);
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            logger.error("B网转C网全称跟踪发送失败.", e);
        }
    }
    private WaybillStatus getWaybillStatus(WaybillWeightVO vo) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(vo.getCodeStr()));

        tWaybillStatus.setCreateSiteCode(vo.getOperatorSiteCode());
        tWaybillStatus.setCreateSiteName(vo.getOperatorSiteName());

        tWaybillStatus.setOperatorId(vo.getOperatorId());
        tWaybillStatus.setOperator(vo.getOperatorName());
        tWaybillStatus.setOperateTime(new Date());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WAYBILL_TRANSFER);
        tWaybillStatus.setRemark(WaybillStatus.WAYBILL_TRACK_MESSAGE_WAYBILL_TRANSFER_B2C);

        return tWaybillStatus;
    }

    /**
     * 转换成全称跟踪的Task
     *
     * @param waybillStatus
     * @return
     */
    private Task toTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(JSON.toJSONString(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }

    public String getPreseparateSystemCode() {
        return preseparateSystemCode;
    }

    public void setPreseparateSystemCode(String preseparateSystemCode) {
        this.preseparateSystemCode = preseparateSystemCode;
    }

}
