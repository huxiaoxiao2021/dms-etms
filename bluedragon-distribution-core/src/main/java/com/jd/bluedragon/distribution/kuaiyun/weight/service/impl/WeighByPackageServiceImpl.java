package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.PackageWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByPackageService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeCheckService;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.eclp.common.etl.util.DateUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jsf.gd.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final Integer JP_FORBID_WEIGHT = 204;

    private final Integer EXCESS_CODE = 600;
    private static final String PACKAGE_WEIGHT_VOLUME_EXCESS_HIT = "您的包裹超规，请确认。超过'200kg/包裹'或'1方/包裹'为超规件";
    private static final String WAYBILL_WEIGHT_VOLUME_EXCESS_HIT = "您的运单包裹超规，请确认。超过'包裹数*200kg/包裹'或'包裹数*1方/包裹'";
    //请求根据包裹获取运单包裹信息接口异常 可重试
    private static final Integer SERVICENOTAVALIABLE = 0;
    //不支持按包裹维度导入 包裹所属运单校验不通过 不重试
    private static final Integer NOTSUPPORTUPWEIGHTBYPACKAGE   = 1;
    //是C网 C网单子不能从此处导入
    private static final Integer NOTSUPPORTUPCWAYBILL  = 2;
    //运单没有通过校验 运单已妥投 不可重试
    private static final Integer WAYBILLFINISHED  = 3;
    //请求根据包裹获取运单包裹信息接口数据返回为空-包裹不存在
    private static final Integer PACKAGEINFONULL = 4;
    //运单通过校验
    private static final Integer WAYBILLCHECKSUCCESS = 5;
    //包裹号存在
    private static final Integer PACKAGECODEEXITES = 6;
    // 集配揽收后禁止称重
    private static final Integer JP_FORBID_WEIGHT_AFTER_LL  = 7;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Qualifier("dmsWeightVolumeCheckService")
    @Autowired
    private DMSWeightVolumeCheckService dmsWeightVolumeCheckService;

    /**校验是否是包裹号 且包裹号是否存在运单中*/
    @Override
    public InvokeResult<Boolean> verifyPackageReality(String codeStr, Map<String, Integer> threadLocal, Integer siteCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(true);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        try {
            /*1 将包裹号正则校验*/
            String packageCode = this.checkPackageCode(codeStr);
            /*2 对包裹进行存在进行校验*/
            boolean isExist = this.validatePackageCodeReality(packageCode, threadLocal, siteCode);
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
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillFinishedException)){
                    //运单已经妥投，不允许录入
                    result.setCode(WAYBILL_STATE_FINISHED);
                    log.warn("运单称重:{}-{} ", codeStr, exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.JPForbidWeightAfterLLException)){
                    // 集配站点揽收后限制称重
                    result.setCode(JP_FORBID_WEIGHT);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.NoPackageException)){
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.NotSupportUpWeightByPackageException)){
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.NotSupportUpCWaybillException)){
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                }
                result.setData(false);
                result.setMessage(exceptionType.exceptionMessage);
            } else {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException)) {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }else if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNotFindException)) {
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.NotPackageCodeException)){
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
    public boolean validateParam(PackageWeightVO vo) {
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
        if(Double.parseDouble(weight) > 200 || Double.parseDouble(volume) > 1){
            result.setCode(EXCESS_CODE);
            result.setMessage(PACKAGE_WEIGHT_VOLUME_EXCESS_HIT);
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



    @Override
    public boolean insertPackageWeightEntry(PackageWeightVO uploadData) throws WeighByWaybillExcpetion{

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity()
                .barCode(uploadData.getCodeStr())
                .businessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE)
                .sourceCode(FromSourceEnum.DMS_WEB_PACKAGE_FAST_TRANSPORT)
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
        weightVolumeEntity.setOperatorCode(uploadData.getOperatorCode());
        log.info("调用dmsWeightVolumeService#dealWeightAndVolume参数:{}", JsonUtils.toJSONString(weightVolumeEntity));
        try {
            //todo 同步先使用 如有问题可以转异步
            InvokeResult<Boolean> result = dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity);
            if(result != null && InvokeResult.RESULT_SUCCESS_CODE == result.getCode()){
                log.info("包裹回传运单称重信息成功:{}",JsonHelper.toJson(weightVolumeEntity));
                return true;
            }else{
                log.error("包裹回传运单称重信息失败：{}，返回信息：{}",JsonHelper.toJson(weightVolumeEntity),result==null ? "null" : result.getMessage());
                return false;
            }
        }catch (Exception ex){
            log.error("调用dmsWeightVolumeService.dealWeightAndVolume异常,参数信息为:{},异常为:",
                    JsonUtils.toJSONString(weightVolumeEntity),ex);
            return false;
        }
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

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NotPackageCodeException);
        }
        return packageCode;

    }

    /***
     * 校验包裹号是否存在
     * @param packageCode
     * @return
     * @throws WeighByWaybillExcpetion
     */

    private boolean validatePackageCodeReality(String packageCode,  Map<String,Integer> packageMap, Integer siteCode) throws WeighByWaybillExcpetion {

        BaseEntity<BigWaybillDto> baseEntity = null;
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);

        //运单通过校验
        if(Objects.equals(packageMap.get(waybillCode),WAYBILLCHECKSUCCESS )) {
            //包裹号存在
            if(Objects.equals(packageMap.get(packageCode),PACKAGECODEEXITES)){
                return true;
            }
        }else if(Objects.equals(packageMap.get(waybillCode),PACKAGEINFONULL)){
            //运单没有通过校验 运单信息没有找到
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
        }else if(Objects.equals(packageMap.get(waybillCode),NOTSUPPORTUPWEIGHTBYPACKAGE)){
            //运单不支持按包裹维度
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NotSupportUpWeightByPackageException);
        }else if(Objects.equals(packageMap.get(waybillCode),WAYBILLFINISHED)){
            //运单已经妥投
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
        }else if(Objects.equals(packageMap.get(waybillCode),NOTSUPPORTUPCWAYBILL)){
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NotSupportUpCWaybillException);
        }else if(Objects.equals(packageMap.get(waybillCode), JP_FORBID_WEIGHT_AFTER_LL)){
            // 集配站点揽收不能称重
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.JPForbidWeightAfterLLException);
        }


        try {
            //根据包裹号获取包裹信息
            DeliveryPackageD deliveryPackageD = waybillPackageManager.getPackageInfoByPackageCode(packageCode);
            if(deliveryPackageD == null){
                packageMap.put(packageCode,PACKAGEINFONULL);
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
            }else{
                //如果该包裹所属运单已经通过校验;无需再进行如下校验 直接返回成功
                if(Objects.equals(packageMap.get(waybillCode),WAYBILLCHECKSUCCESS )){
                    packageMap.put(packageCode,PACKAGECODEEXITES);
                    return true;
                }
                WChoice wChoice = new WChoice();
                wChoice.setQueryWaybillC(true);
                baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);
            }

        }catch (WeighByWaybillExcpetion weighByWaybillExcpetion){
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
        } catch (Exception e) {
            log.error("waybillQueryManager.getWaybillByPackCode 异常：{}",packageCode,e);
            packageMap.put(waybillCode,SERVICENOTAVALIABLE);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException);
        }
        if(baseEntity != null
                && baseEntity.getResultCode() == 1 && baseEntity.getData() != null){
            BigWaybillDto baseWaybill = baseEntity.getData();
            //校验WaybillSign66，当WaybillSign66=0或1时，不支持按包裹维度批量导入；
            if(baseWaybill.getWaybill() !=null && baseWaybill.getWaybill().getWaybillSign() != null && BusinessUtil.isNotSupportUpWeightByPackage(baseWaybill.getWaybill().getWaybillSign())){
                //Integer 1 不支持按包裹维度导入 包裹所属运单校验不通过
                packageMap.put(waybillCode,NOTSUPPORTUPWEIGHTBYPACKAGE);
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NotSupportUpWeightByPackageException);
            }
            //是否是C网 只录入B网重量
            if(baseWaybill.getWaybill() !=null && baseWaybill.getWaybill() .getWaybillSign() != null && BusinessUtil.isCInternet(baseWaybill.getWaybill() .getWaybillSign())){
                packageMap.put(waybillCode,NOTSUPPORTUPCWAYBILL);
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NotSupportUpCWaybillException);
            }
            //校验是否已经妥投
            if(waybillTraceManager.isWaybillFinished(waybillCode)){
                //弹出提示
                packageMap.put(waybillCode,WAYBILLFINISHED);
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillFinishedException);
            }
            // 集配站点揽收后不能操作称重
            InvokeResult<Boolean> jpSiteCanWeightResult = dmsWeightVolumeCheckService.checkJPIsCanWeight(waybillCode, siteCode);
            if(!jpSiteCanWeightResult.codeSuccess()){
                packageMap.put(waybillCode, JP_FORBID_WEIGHT_AFTER_LL);
                throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.JPForbidWeightAfterLLException);
            }
        }else {
            log.error("根据包裹号:{}查询运单包裹信息返回数据为空!",packageCode);
            packageMap.put(packageCode,PACKAGEINFONULL);
            packageMap.put(waybillCode,PACKAGEINFONULL);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.NoPackageException);
        }
        packageMap.put(waybillCode,WAYBILLCHECKSUCCESS);
        packageMap.put(packageCode,PACKAGECODEEXITES);
        return true;

    }

    /**
     * 记录引操作人引起的异常
     *
     * @param dto 操作消息对象
     */
    @Override
    public void errorLogForOperator(PackageWeightVO dto,LoginContext loginContext,boolean isImport) {
        try {
            Goddess goddess = new Goddess();
            if(isImport){
                goddess.setKey("packageImportError");
            }else{
                goddess.setKey("packageError");
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

}
