package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.merchant.ExpressOrderServiceWsManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.enums.OverLengthAndWeightTypeEnum;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeCheckService;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.common.web.LoginContext;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.merchant.sdk.b2b.constant.enumImpl.SystemCallerEnum;
import com.jd.merchant.sdk.order.dto.BaseInfo;
import com.jd.merchant.sdk.order.dto.UpdateOrderRequest;
import com.jd.merchant.sdk.product.dto.ChannelInfo;
import com.jd.merchant.sdk.product.dto.OverLengthAndWeight;
import com.jd.preseparate.util.*;
import com.jd.preseparate.vo.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(WeighByWaybillServiceImpl.class);

    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final String CASSANDRA_SIGN = "WaybillWeight_";

    private static final String PACKAGE_WEIGHT_VOLUME_EXCESS_HIT = "您的包裹超规，请确认。超过'200kg/包裹'或'1方/包裹'为超规件";
    private static final String WAYBILL_WEIGHT_VOLUME_EXCESS_HIT = "您的运单包裹超规，请确认。超过'包裹数*200kg/包裹'或'包裹数*1方/包裹'";

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

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Value("${preseparate.systemCode}")
    private String preseparateSystemCode;

    @Autowired
    private TaskService taskService;

    @Value("${weight.transfer.b2c.min:5}")
    private double weightTransferB2cMin;

    @Value("${weight.transfer.b2c.max:30}")
    private double weightTransferB2cMax;

    @Autowired
    private LogEngine logEngine;

    @Qualifier("dmsWeightVolumeCheckService")
    @Autowired
    private DMSWeightVolumeCheckService dmsWeightVolumeCheckService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;
    
    @Autowired
    @Qualifier("expressOrderServiceWsManager")
    private ExpressOrderServiceWsManager expressOrderServiceWsManager;
    
    @Autowired
    private DmsConfigManager dmsConfigManager;
    /**
     * 运单称重信息录入入口 最终发送mq消息给运单部门
     *
     * @param vo 运单称重参数
     * @throws WeighByWaybillExcpetion 运单称重异常
     */
    @Override
    public void insertWaybillWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion {
        String codeStr = vo.getCodeStr();
        /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
        String waybillCode = this.convertToWaybillCode(codeStr);

        /*将重量体积单位转为 运单部门要求的单位*/
        Double weight = this.convertWeightUnitToRequired(vo.getWeight());
        Double volume = vo.getVolume() == null ? null : this.convertVolumeUnitToRequired(vo.getVolume());
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
    @Override
    public String convertToWaybillCode(String codeStr) throws WeighByWaybillExcpetion {

        String waybillCode = null;
        log.debug("单号或包裹号正则校验:{}",codeStr);

        if (WaybillUtil.isPackageCode(codeStr))
        {
            waybillCode = WaybillUtil.getWaybillCode(codeStr);
        } else if (WaybillUtil.isWaybillCode(codeStr))
        {
            waybillCode = codeStr;
        } else
        {
            log.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则:{}",codeStr);

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        if (null == waybillCode)
        {
            log.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则:{}",codeStr);

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
    @Override
    public boolean validateWaybillCodeReality(String waybillCode, Integer siteCode) throws WeighByWaybillExcpetion {
        BaseEntity<Waybill> waybillBaseEntity = null;

        try {
            waybillBaseEntity = waybillQueryManager.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e) {
            log.error("waybillQueryManager.getWaybillByWaybillCode 异常：{}",waybillCode,e);
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
        }else if(waybill.getWaybillSign() != null && BusinessUtil.needWeighingSquare(waybill.getWaybillSign())){
            //waybillsign66=3
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNeedPackageWeightException);
        }

        //校验是否已经妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            //弹出提示
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
        }

        if(siteCode != null){
            // 集配站点揽收后不能操作称重
            InvokeResult<Boolean> jpSiteCanWeightResult = dmsWeightVolumeCheckService.checkJPIsCanWeight(waybillCode, siteCode);
            if(!jpSiteCanWeightResult.codeSuccess()){
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.JPForbidWeightAfterLLException);
            }
        }

        return true;
    }

    /**
     * 当运单经校验存在时的流程
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion MQServiceNotAvailableException WaybillWeightVOConvertExcetion
     */
    @Override
    public void validWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion {

        JSONObject request=new JSONObject();
        request.put("waybillCode",dto.getWaybillCode());
        request.put("operatorCode",dto.getOperatorName());
        request.put("operatorName",dto.getOperatorId());

        JSONObject response=new JSONObject();
        response.put("body", JsonHelper.toJson(dto));

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.WEIGH_WAYBILL_VALIDWAYBILL)
                .operateRequest(request)
                .operateResponse(response)
                .methodName("WeighByWaybillServiceImpl#validWaybillProcess")
                .build();

        logEngine.addLog(businessLogProfiler);

                this.logToOperationlogCassandra(dto);

        this.sendMessageToMq(dto);
        this.uploadOverWeightInfo(dto);
        //保存称重流水入库
        dmsWeightFlowService.saveOrUpdate(convertToDmsWeightFlow(dto));
    }
    /**
     * 上传超长超重服务信息
     * @param entity
     */
    protected boolean uploadOverWeightInfo(WaybillWeightDTO entity) {
    	if(!dmsConfigManager.getUccPropertyConfig().isUploadOverWeightSwitch()
    			|| !Boolean.TRUE.equals(entity.getOverLengthAndWeightEnable())
    			|| CollectionUtils.isEmpty(entity.getOverLengthAndWeightTypes())) {
    		restLongPackage(entity);
    		return false;
    	}
    	UpdateOrderRequest updateData = new UpdateOrderRequest();
    	BaseInfo baseInfo = new BaseInfo();
    	baseInfo.setWaybillCode(entity.getWaybillCode());
    	baseInfo.setUpdateTime(new Date());
    	baseInfo.setUpdateUser(entity.getOperatorName());
    	updateData.setBaseInfo(baseInfo);
    	ChannelInfo channelInfo = new ChannelInfo();
    	channelInfo.setSystemCaller(SystemCallerEnum.DMS_ETMS.getSystemCaller());
    	updateData.setChannelInfo(channelInfo);
    	OverLengthAndWeight overLengthAndWeight = new OverLengthAndWeight();
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.ONE_SIDE.getCode())) {
    		overLengthAndWeight.setSingleSideOverLength(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.THREED_SIDE.getCode())) {
    		overLengthAndWeight.setThreeSidesOverLength(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.OVER_WEIGHT.getCode())) {
    		overLengthAndWeight.setOverWeight(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	updateData.setOverLengthAndWeight(overLengthAndWeight);
    	JdResult<Boolean> result = expressOrderServiceWsManager.updateOrderSelective(updateData);
    	if(result.isSucceed()) {
    		log.warn("{}超长超重服务上传成功！",entity.getWaybillCode());
    		return true;
    	}else {
    		log.warn("{}超长超重服务上传失败！",entity.getWaybillCode());
    		restLongPackage(entity);
    	}  
    	return false;
    }
    private void restLongPackage(WaybillWeightDTO entity){
        if(DmsConstants.WAYBILL_LONG_PACKAGE_OVER_WEIGHT.equals(entity.getLongPackage())) {
        	entity.setLongPackage(DmsConstants.WAYBILL_LONG_PACKAGE_DEFAULT);
        	log.warn("{}重置超长超重标longPackage识为0！",entity.getWaybillCode());
        }
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
    @Override
    public void invalidWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion {
        JSONObject request=new JSONObject();
        request.put("waybillCode",dto.getWaybillCode());
        request.put("operatorCode",dto.getOperatorName());
        request.put("operatorName",dto.getOperatorId());

        JSONObject response=new JSONObject();
        response.put("body", JsonHelper.toJson(dto));


        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.WEIGH_WAYBILL_INVALIDWAYBILL)
                .operateRequest(request)
                .operateResponse(response)
                .methodName("WeighByWaybillServiceImpl#invalidWaybillProcess")
                .build();

        logEngine.addLog(businessLogProfiler);

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
            log.error("weighByWaybillProducer.send 异常：{}",dto.getWaybillCode(),e);

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
            log.error("运单称重：cassandra操作日志记录失败：{}" ,dto.getWaybillCode(), e);
        }
    }

    /**
     * 记录引操作人引起的异常
     *
     * @param dto 操作消息对象
     */
    @Override
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

            JSONObject operateRequest=new JSONObject();
            operateRequest.put("operatorCode",dto.getOperatorId());
            operateRequest.put("operatorName",dto.getOperatorName());

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateRequest(operateRequest)
                    .operateResponse(goddess)
                    .methodName("WeighByWaybillServiceImpl#errorLogForOperator")
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.WEIGH_WAYBILL_OPERATEEXCEPTION)
                    .build();

            logEngine.addLog(businessLogProfiler);

            goddessService.save(goddess);
        } catch (Exception e) {
            log.error("运单称重：cassandra操作日志记录失败：{}" ,JsonHelper.toJson(dto), e);
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
    @Override
    public boolean waybillTransferB2C(WaybillWeightVO vo){
        boolean flag = false;
        //从vo中取出运单号、重量体积、操作人和操作站点信息
        String waybillCode = WaybillUtil.getWaybillCode(vo.getCodeStr());

        if(StringUtils.isNotBlank(waybillCode)){
            //调用运单接口获取waybillSign
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
            if(baseEntity == null || baseEntity.getResultCode() != 1){
                log.warn("获取运单信息失败,运单号:{}.返回值:{}" ,waybillCode, JSON.toJSONString(baseEntity));
                return false;
            }
            if(baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
                log.warn("获取运单信息为空,运单号:{}.返回值:{}" ,waybillCode, JSON.toJSONString(baseEntity));
                return false;
            }

            BigWaybillDto bigWaybillDto = baseEntity.getData();
            String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();

            if(canTrasnferB2C(bigWaybillDto,vo.getWeight())){
                BatchTransferRequest batchTransferRequest = buildTransferRequest(vo,waybillCode,waybillSign);
                BaseResponseIncidental<BatchTransferResult> baseResponse = new BaseResponseIncidental<BatchTransferResult>();
                try {
                    if(log.isDebugEnabled()){
                        log.debug("调用预分拣批量转网接口参数：{}" , JSON.toJSONString(batchTransferRequest));
                    }
                    baseResponse= preseparateWaybillManager.batchTransfer(batchTransferRequest);
                    if(log.isDebugEnabled()){
                        log.debug("调用预分拣批量转网接口返回值:{}" , JSON.toJSONString(baseResponse));
                    }
                    if (baseResponse == null || !baseResponse.getCode().equals(BaseResponse.CODE_OK)) {
                        log.warn("调用预分拣批量转网接口失败,参数:{},返回值:{}" , JSON.toJSONString(batchTransferRequest), JsonHelper.toJson(baseResponse));
                        return false;
                    }
                }catch(Exception e){
                    log.error("调用预分拣批量转网接口异常.运单号:{}" , waybillCode,e);
                    return false;
                }

                if(baseResponse.getData()!= null && baseResponse.getData().getTransferStatus().equals(TransferStatusEnum.SUCCESS_TRANSFERRED.getStatus())){
                    flag = true;
                }
            }
        }

        //如果转网成功发送全称跟踪
        if(flag){
            sendWaybillTrace(vo);
        }

        return flag;
    }

    @Override
    public InvokeResult checkIsExcess(String codeStr, String weight, String volume){
        InvokeResult result = new InvokeResult();

        if(com.jd.common.util.StringUtils.isEmpty(codeStr) || com.jd.common.util.StringUtils.isEmpty(weight)
                || com.jd.common.util.StringUtils.isEmpty(volume)){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try{
            if(WaybillUtil.isWaybillCode(codeStr)){
                int packNum = 0;
                BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(codeStr, true, true, true, false);
                if(entity!= null && entity.getData() != null && entity.getData().getWaybill() != null){
                    packNum = entity.getData().getWaybill().getGoodNumber() == null?0:entity.getData().getWaybill().getGoodNumber();
                    if(Double.parseDouble(weight) > 200*packNum || Double.parseDouble(volume) > packNum){
                        result.setCode(600);
                        result.setMessage(WAYBILL_WEIGHT_VOLUME_EXCESS_HIT);
                    }
                }else{
                    result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                    result.setMessage("运单信息为空!");
                }
            }else{
                if(Double.parseDouble(weight) > 200 || Double.parseDouble(volume) > 1){
                    result.setCode(600);
                    result.setMessage(PACKAGE_WEIGHT_VOLUME_EXCESS_HIT);
                }
            }
        }catch (Exception e){
            log.error("通过运单号:{}获取运单信息失败!",codeStr, e);
        }

        return result;
    }

    @Override
    public InvokeResult checkIsExcessNew(String codeStr, String weight, String volume, Integer siteCode) {
        WeightVolumeRuleCheckDto condition = new WeightVolumeRuleCheckDto();
        condition.setBarCode(WaybillUtil.getWaybillCode(codeStr));
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        condition.setSourceCode(FromSourceEnum.DMS_WEB_FAST_TRANSPORT.name());
        condition.setVolume(Double.parseDouble(volume) * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION);
        condition.setCheckVolume(Boolean.TRUE);
        condition.setWeight(Double.parseDouble(weight));
        condition.setCheckWeight(Boolean.TRUE);
        condition.setCheckLWH(Boolean.FALSE);
        condition.setOperateSiteCode(siteCode);
        return dmsWeightVolumeService.weightVolumeRuleCheck(condition);
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
        String operateSiteName = vo.getOperatorSiteName();

        //根据操作人编码获取操作人erp
        String userErp = "";
        BaseStaffSiteOrgDto staffdto = baseMajorManager.getBaseStaffByStaffId(operatorId);
        if (staffdto == null || StringUtils.isBlank(staffdto.getErp())) {
            log.warn("根据操作人id:{} 获取操作人erp失败.返回值:{}",operatorId,JSON.toJSONString(staffdto));
        } else {
            userErp = staffdto.getErp();
            if(operateSiteCode == null || operateSiteCode <= 0){
                operateSiteCode = staffdto.getSiteCode();
            }
            if(StringUtils.isBlank(operateSiteName)){
                operateSiteName = staffdto.getSiteName();
            }
        }

        //组装参数
        BatchTransferRequest batchTransferRequest = new BatchTransferRequest();
        List<TransferRequestOrder> waybillList = new ArrayList<TransferRequestOrder>(1);
        TransferRequestOrder transferRequestOrder = new TransferRequestOrder();
        transferRequestOrder.setWaybillCode(waybillCode);
        transferRequestOrder.setWaybillSign(waybillSign);
        transferRequestOrder.setWeight(new BigDecimal(String.valueOf(weight)));
        transferRequestOrder.setVolume(new BigDecimal(String.valueOf(volume)));
        waybillList.add(transferRequestOrder);

        batchTransferRequest.setOrderList(waybillList);
        batchTransferRequest.setOperationNode(OperationNodeEnum.TRANSFER_CENTER.getNodeId());
        batchTransferRequest.setBusinessType(BusinessTypeEnum.DELIVER_ORDER.getCode());
        batchTransferRequest.setOperationBranchId(operateSiteCode);
        batchTransferRequest.setOperationBranchName(operateSiteName);
        batchTransferRequest.setOperationExpect(OperationExpectEnum.TRANSFER_TO_STATION.getExpectId());
        batchTransferRequest.setHandleType(HandleTypeEnum.CHECK_AND_TRANSFER.getHandleType());
        batchTransferRequest.setOperatorErp(userErp);
        batchTransferRequest.setOperationTime(new Date());
        batchTransferRequest.setSystemCode(preseparateSystemCode);

        return batchTransferRequest;
    }

    /**
     * 生成全称跟踪任务
     * @param vo
     */
    private void sendWaybillTrace(WaybillWeightVO vo){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WeighByWaybillServiceImpl.sendWaybillTrace", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            WaybillStatus waybillStatus = this.getWaybillStatus(vo);
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            log.error("B网转C网全称跟踪发送失败:{}",JsonHelper.toJson(vo), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 组织全称跟踪参数
     * @param vo
     * @return
     */
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

    /**
     * 判断是否能够满足转网范围
     * @param bigWaybillDto
     * @param weight
     * @return
     */
    private boolean canTrasnferB2C(BigWaybillDto bigWaybillDto,Double weight){
        String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
        String waybillCode = bigWaybillDto.getWaybill().getWaybillCode();

        //满足标位，&& 寄付运费>0 && 重量在转网范围之内才能转网
        if(BusinessUtil.isForeignWaybill(waybillSign)
                && BusinessUtil.isPureDeliveryWaybill(waybillSign)
                && !BusinessUtil.isTc(waybillSign)){
            if(BusinessHelper.hasSendFreightForB2b(bigWaybillDto) &&
                    NumberHelper.gte(weight,weightTransferB2cMin) &&
                    NumberHelper.lte(weight,weightTransferB2cMax)){
                return true;
            }
        }
        log.warn("不满足转网条件，不能进行转网.运单号:{},重量:{}" ,waybillCode, weight);
        return false;
    }
}
