package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.FileUtils;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByPackageService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
import com.jd.dbs.util.CollectionUtils;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.eclp.common.etl.util.DateUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.preseparate.util.*;
import com.jd.preseparate.vo.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

@Scope("prototype")
@Service("weighByPackageServiceImpl")
public class WeighByPackageServiceImpl implements WeighByPackageService
{
    private static final Logger log = LoggerFactory.getLogger(WeighByPackageServiceImpl.class);

    private final Double MAX_WEIGHT = 999999.99;
    private final Double MAX_VOLUME = 999.99;

    /*10：表示经调取运单接口WaybillQueryApi，已查到该运单，可直接入库*/
    /*20：表示经调取运单接口WaybillQueryApi，未查到该运单，需经处理*/
    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final Integer NO_NEED_WEIGHT = 201;
    private final Integer WAYBILL_STATE_FINISHED = 202;
    private final Integer KAWAYBILL_NEEDPACKAGE_WEIGHT=203;

    private final Integer EXCESS_CODE = 600;
    private static final String PACKAGE_WEIGHT_VOLUME_EXCESS_HIT = "您的包裹超规，请确认。超过'200kg/包裹'或'1方/包裹'为超规件";
    private static final String WAYBILL_WEIGHT_VOLUME_EXCESS_HIT = "您的运单包裹超规，请确认。超过'包裹数*200kg/包裹'或'包裹数*1方/包裹'";

    @Value("${weight.transfer.b2c.min:5}")
    private double weightTransferB2cMin;

    @Value("${weight.transfer.b2c.max:30}")
    private double weightTransferB2cMax;
    @Value("${preseparate.systemCode}")
    private String preseparateSystemCode;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private LogEngine logEngine;
    @Autowired
    private GoddessService goddessService;
    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;
    @Autowired
    private TaskService taskService;

    @Autowired
    private JssService jssService;

    @Value("${jss.feedback.bucket}")
    private String bucket;

    /**校验是否是包裹号 且包裹号是否存在运单中*/
    @Override
    public InvokeResult<Boolean> verifyPackageReality(String codeStr) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(true);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        try {
            /*1 将包裹号正则校验*/
            String packageCode = this.checkPackageCode(codeStr);
            /*2 对包裹进行存在进行校验*/
            boolean isExist = this.validatePackageCodeReality(packageCode);
            result.setData(isExist);
            if (isExist) {
                result.setMessage("存在该包裹相关信息，可以录入！");
            }
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion) {
            result.setData(false);

            WeightByWaybillExceptionTypeEnum exceptionType = weighByWaybillExcpetion.exceptionType;
            if (exceptionType.shouldBeThrowToTop) {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException)) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    log.warn("运单称重：{}", exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightException)){
                    //不称重
                    result.setCode(NO_NEED_WEIGHT);
                    log.warn("运单称重：{}-{} " ,codeStr, exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNeedPackageWeightException)){
                    //KA运单 必须按包裹维度录入重量体积
                    result.setCode(KAWAYBILL_NEEDPACKAGE_WEIGHT);
                    log.warn("运单称重:{}-{} ", codeStr, exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillFinishedException)){
                    //运单已经妥投，不允许录入
                    result.setCode(WAYBILL_STATE_FINISHED);
                    log.warn("运单称重:{}-{} ", codeStr, exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.NoPackageException)){
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }
                result.setData(false);
                result.setMessage(exceptionType.exceptionMessage);
            } else {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException)) {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNotFindException)) {
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

            }
        }
        return result;
    }

    /**
     * 校验传入称重量方参数
     *
     * @param vo 传入重量体积参数对象
     * @return boolean 校验结果
     */
    @Override
    public boolean validateParam(WaybillWeightVO vo) {
        Integer status = vo.getStatus();
        Double weight = vo.getWeight();
        Double volume = vo.getVolume();

        if (!(status.equals(VALID_EXISTS_STATUS_CODE) || status.equals(VALID_NOT_EXISTS_STATUS_CODE))) {
            return false;
        }

        if (weight.compareTo(this.MAX_WEIGHT) != -1 || weight <= 0.0) {
            return false;
        }

        if (WaybillUtil.isEconomicNet(WaybillUtil.getWaybillCode(vo.getCodeStr()))) {
            //经济网称重的体积为null或者<=0时，体积填默认值0.22（无意义）
            if (vo.getVolume() == null || vo.getVolume() <= 0.0) {
                vo.setVolume(0.22/1000000);
            }
            return true;
        }

        if (volume.compareTo(this.MAX_VOLUME) != -1 || volume <= 0.0) {
            return false;
        }



        return true;
    }

    @Override
    public InvokeResult checkIsExcess(String codeStr, String weight, String volume) {
        InvokeResult result = new InvokeResult();

        if(StringUtils.isEmpty(codeStr) || StringUtils.isEmpty(weight)
                || StringUtils.isEmpty(volume)){
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
                        result.setCode(EXCESS_CODE);
                        result.setMessage(WAYBILL_WEIGHT_VOLUME_EXCESS_HIT);
                    }
                }else{
                    result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                    result.setMessage("运单信息为空!");
                }
            }else{
                if(Double.parseDouble(weight) > 200 || Double.parseDouble(volume) > 1){
                    result.setCode(EXCESS_CODE);
                    result.setMessage(PACKAGE_WEIGHT_VOLUME_EXCESS_HIT);
                }
            }
        }catch (Exception e){
            log.error("通过运单号:{}获取运单信息失败!",codeStr, e);
        }

        return result;
    }



    /**
     * 组装导出数据
     * @param list
     * @return
     */
    @Override
    public List<List<Object>> getExportDataPackage(List<Map> list) {

        List<List<Object>> resList = new ArrayList<List<Object>>();

        List<Object> heads = new ArrayList<Object>();

        heads.add("包裹号");
        heads.add("包裹重量(kg)");
        heads.add("长(cm)");
        heads.add("宽(cm)");
        heads.add("高(cm)");

        resList.add(heads);

        for(Map waybillWeightVO :list){
            List<Object> body = new ArrayList<Object>();
            //运单号
            body.add(waybillWeightVO.get("codeStr"));
            //重量
            body.add(waybillWeightVO.get("weight"));
            //长
            body.add(waybillWeightVO.get("packageLength"));
            //宽
            body.add(waybillWeightVO.get("packageWidth"));
            //高
            body.add(waybillWeightVO.get("packageHigh"));
            resList.add(body);
        }

        return resList;
    }

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Override
    public boolean insertPackageWeightEntry(WaybillWeightVO uploadData) throws WeighByWaybillExcpetion{

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity()
                .barCode(uploadData.getCodeStr())
                .businessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE)
                .sourceCode(FromSourceEnum.DMS_CLIENT_SWITCH_BILL_PRINT)
                .height(uploadData.getPackageHigh())
                .width(uploadData.getPackageWidth())
                .length(uploadData.getPackageLength())
                .weight(uploadData.getWeight())
                .operateSiteCode(uploadData.getOperatorSiteCode())
                .operateSiteName(uploadData.getOperatorSiteName())
                .operatorId(uploadData.getOperatorId())
                .operatorName(uploadData.getOperatorName())
                .operateTime(new Date());
        weightVolumeEntity.setVolume(uploadData.getVolume());
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffIdNoCache(uploadData.getOperatorId());
        if(dto != null){
            weightVolumeEntity.setOperatorCode(dto.getAccountNumber());
        }
        InvokeResult<Boolean> result = dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity);
        if(result != null && InvokeResult.RESULT_SUCCESS_CODE == result.getCode()){
            log.info("包裹回传运单称重信息成功:{}",JsonHelper.toJson(weightVolumeEntity));
            return true;
        }else{
            log.error("包裹回传运单称重信息失败：{}，返回信息：{}",JsonHelper.toJson(weightVolumeEntity),result==null ? "null" : result.getMessage());
            return false;
        }
    }

    @Override
    public boolean uploadExcelToJss(MultipartFile file,String userCode) {
        String fileName = file.getOriginalFilename();
        String keyName = this.getKeyName(userCode, FileUtils.getFileExtName(fileName));
        try {
            jssService.uploadFile(bucket, keyName, file.getSize(), file.getInputStream());
            log.info("WeighByPackageServiceImpl-uploadExcelToJss上传至jss成功,文件名:{}",keyName);
        } catch (Exception e) {
            log.error("上传jss异常,erp:{},原文件名:{},自定义文件名:{}!",userCode,fileName,keyName,e);
            return false;
        }
        return true;
    }

    /**
     * 生成一个JSS文件系统的KeyName
     *
     * @return
     */
    private String getKeyName(String userCode, String extFileName) {
        Random random = new Random();
        String  dateString = DateUtil.parse(new Date(),"yyyyMMddHHmmss");
        return String.valueOf(System.currentTimeMillis()) + String.valueOf(random.nextInt(1000) + userCode+dateString) + "." + extFileName;
    }

    private String checkPackageCode(String codeStr) throws WeighByWaybillExcpetion{

        String packageCode = null;
        log.debug("包裹号正则校验:{}",codeStr);

        if (WaybillUtil.isPackageCode(codeStr))
        {
            packageCode = codeStr;
        } else
        {
            log.warn("所输入的编码格式有误：不符合包裹号编码规则:{}",codeStr);

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }
        return packageCode;

    }

    /***
     * 校验包裹号是否存在
     * @param packageCode
     * @return
     * @throws WeighByWaybillExcpetion
     */
    //string 可能是包裹号也可能是运单号;Integer 0 标识包裹号存在 1包裹号不存在 2标识运单通过校验 3标识运单没有通过校验,不存在也属于其中一种情况
    Map<String,Integer> packageMap = new HashMap<>();
    private boolean validatePackageCodeReality(String packageCode) throws WeighByWaybillExcpetion {

        BaseEntity<BigWaybillDto> baseEntity = null;
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        //包裹号存在
        if(Objects.equals(packageMap.get(packageCode),0)){
            return true;
        }else if(Objects.equals(packageMap.get(packageCode),1)){//包裹不存在
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
            //return false;
        }
        //运单通过校验,但是该包裹没有在map中说明运单下没有该包裹
        if(Objects.equals(packageMap.get(waybillCode),2)) {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
        }else if(Objects.equals(packageMap.get(waybillCode),3)){//运单没有通过校验,说明该包裹所属运单不需要称重,或者包裹所属运单没有信息
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNotFindException);
        }else if(Objects.equals(packageMap.get(waybillCode),4)){
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightException);
        }else if(Objects.equals(packageMap.get(waybillCode),5)){

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNeedPackageWeightException);
        }else if(Objects.equals(packageMap.get(waybillCode),6)){
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
        }
        WChoice wChoice = new WChoice();
        //只查询运单下的包裹
        wChoice.setQueryPackList(true);
        wChoice.setQueryWaybillC(true);
        try {
            baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            //waybillBaseEntity = waybillQueryManager.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e) {
            log.error("waybillQueryManager.getWaybillByWaybillCode 异常：{}",packageCode,e);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException);
        }

        int resultCode = baseEntity.getResultCode();
        BigWaybillDto waybill = baseEntity.getData();
        Waybill baseWaybill = waybill.getWaybill();
        if (waybill == null || baseWaybill == null) {
            packageMap.put(waybillCode,3);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNotFindException);
        }

        //是否需要称重逻辑校验  2018 07 27  update 刘铎
        if (baseWaybill.getWaybillSign() != null && BusinessUtil.isNoNeedWeight(baseWaybill.getWaybillSign())) {
            packageMap.put(waybillCode,4);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightException);
        }else if(baseWaybill.getWaybillSign() != null && BusinessUtil.isKaPackageOrNo(baseWaybill.getWaybillSign())){
            //waybillsign66=3
            packageMap.put(waybillCode,5);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNeedPackageWeightException);
        }

        //校验是否已经妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            packageMap.put(waybillCode,6);
            //弹出提示
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
        }

        List<DeliveryPackageD> packageList = waybill.getPackageList();
        if(CollectionUtils.isNotEmpty(packageList)){
            for(int i=0;i<packageList.size();i++){
                packageMap.put(packageList.get(i).getPackageBarcode(),0);
            }
        }
        //运单通过校验
        packageMap.put(waybillCode,2);
        //包裹不存在该运单下
        if(!Objects.equals(packageMap.get(packageCode),0)){
            packageMap.put(packageCode,1);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
        }

        return Objects.equals(packageMap.get(packageCode),0);
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
                    .methodName("WeighByPackageServiceImpl#errorLogForOperator")
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.WEIGH_WAYBILL_OPERATEEXCEPTION)
                    .build();

            logEngine.addLog(businessLogProfiler);

            //goddessService.save(goddess);
        } catch (Exception e) {
            log.error("运单称重：cassandra操作日志记录失败：{}" ,JsonHelper.toJson(dto), e);
        }
    }



    /**
     * 判断是否转网--B转C
     * @return
     */
    public boolean waybillTransferB2C(WaybillWeightVO vo){
        boolean flag = false;
        //从vo中取出运单号、重量体积、操作人和操作站点信息
        String waybillCode = WaybillUtil.getWaybillCode(vo.getCodeStr());

        if(org.apache.commons.lang.StringUtils.isNotBlank(waybillCode)){
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
        if (staffdto == null || org.apache.commons.lang.StringUtils.isBlank(staffdto.getErp())) {
            log.warn("根据操作人id:{} 获取操作人erp失败.返回值:{}",operatorId,JSON.toJSONString(staffdto));
        } else {
            userErp = staffdto.getErp();
            if(operateSiteCode == null || operateSiteCode <= 0){
                operateSiteCode = staffdto.getSiteCode();
            }
            if(org.apache.commons.lang.StringUtils.isBlank(operateSiteName)){
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
        try {
            WaybillStatus waybillStatus = this.getWaybillStatus(vo);
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            log.error("B网转C网全称跟踪发送失败:{}",JsonHelper.toJson(vo), e);
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
}
