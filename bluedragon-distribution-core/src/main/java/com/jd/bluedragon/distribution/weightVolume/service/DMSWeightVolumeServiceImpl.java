package com.jd.bluedragon.distribution.weightVolume.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.funcSwitchConfig.TraderMoldTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.weightVolume.check.WeightVolumeChecker;
import com.jd.bluedragon.distribution.weightVolume.domain.*;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.alibaba.fastjson.JSON;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.DisposeNodeConstants;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *     分拣称重量方处理逻辑
 *
 * @author wuzuxiang
 * @since 2020/1/9
 **/
@Service("dmsWeightVolumeService")
public class DMSWeightVolumeServiceImpl implements DMSWeightVolumeService {

    private static final Logger logger = LoggerFactory.getLogger(DMSWeightVolumeServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    private WaybillQueryManager waybillQueryManager;


    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    PackageWeightingService packageWeightingService;

    @Autowired
    private BaseMinorManager baseMinorManager;


    @Override
    @JProfiler(jKey = "DMSWEB.DMSWeightVolumeService.dealWeightAndVolume", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity, boolean isSync) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);

        InvokeResult<Boolean> checkResult = WeightVolumeChecker.check(entity);
        if(checkResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            logger.warn("称重数据上传，校验未通过，参数：{}，返回值：{}",JsonHelper.toJson(entity), JsonHelper.toJson(checkResult));
            this.saveInterceptLog(entity, checkResult);
            return checkResult;
        }
        if (isSync) {
            //同步处理
            result = weightVolumeHandlerStrategy.doHandler(entity);
            this.sendDisposeAfterInterceptMsg(entity);
            return result;
        } else {
            //异步处理
            Task weightVolumeTask = new Task();
            weightVolumeTask.setKeyword1(entity.getBarCode());
            weightVolumeTask.setKeyword2(entity.getBusinessType().name());
            weightVolumeTask.setCreateSiteCode(entity.getOperateSiteCode());
            weightVolumeTask.setCreateTime(entity.getOperateTime());
            weightVolumeTask.setType(Task.TASK_TYPE_WEIGHT_VOLUME);
            weightVolumeTask.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT_VOLUME));
            weightVolumeTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_WEIGHT_VOLUME));
            String ownSign = BusinessHelper.getOwnSign();
            weightVolumeTask.setOwnSign(ownSign);

            weightVolumeTask.setBody(JsonHelper.toJson(entity));
            taskService.add(weightVolumeTask);
            return result;
        }
    }

    /**
     * 发送拦截消息
     * @author fanggang7
     * @time 2020-12-10 11:21:39 周四
     */
    private void saveInterceptLog(WeightVolumeEntity entity, InvokeResult<Boolean> checkResult){
        // 上传拦截报表
        SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
        // 设备类型判断
        final FromSourceEnum sourceCode = entity.getSourceCode();
        saveInterceptMsgDto.setOperateNode(businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.PRINT));
        saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.PRINT_CLIENT));
        if (FromSourceEnum.DMS_DWS_MEASURE.equals(sourceCode)){
            saveInterceptMsgDto.setOperateNode(businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.MEASURE_WEIGHT));
            saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.MACHINE_DWS));
        }
        saveInterceptMsgDto.setInterceptCode(checkResult.getCode());
        saveInterceptMsgDto.setInterceptMessage(checkResult.getMessage());
        saveInterceptMsgDto.setBarCode(entity.getBarCode());
        saveInterceptMsgDto.setSiteCode(entity.getOperateSiteCode());
        saveInterceptMsgDto.setDeviceCode(StringUtils.isNotBlank(entity.getMachineCode()) ? entity.getMachineCode() : Constant.DEVICE_CODE_PRINT);
        long operateTimeMillis = System.currentTimeMillis();
        if(entity.getOperateTime() != null){
            operateTimeMillis = entity.getOperateTime().getTime();
        }
        saveInterceptMsgDto.setOperateTime(operateTimeMillis);
        saveInterceptMsgDto.setSiteName(entity.getOperateSiteName());
        saveInterceptMsgDto.setOperateUserCode(entity.getOperatorId());
        saveInterceptMsgDto.setOperateUserErp(entity.getOperatorCode());
        saveInterceptMsgDto.setOperateUserName(entity.getOperatorName());
        saveInterceptMsgDto.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());

        try {
            businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
        } catch (Exception e) {
            String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
            logger.error("DMSWeightVolumeServiceImpl.saveInterceptLog call sendInterceptMsg exception [{}]", saveInterceptMqMsg, e);
        }
    }

    /**
     * 发送拦截后处理动作消息
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-10 11:21:39 周四
     */
    private boolean sendDisposeAfterInterceptMsg(WeightVolumeEntity weightVolumeEntity){
        long currentTimeMillis = System.currentTimeMillis();
        SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
        saveDisposeAfterInterceptMsgDto.setBarCode(weightVolumeEntity.getBarCode());
        saveDisposeAfterInterceptMsgDto.setOperateTime(currentTimeMillis);
        saveDisposeAfterInterceptMsgDto.setDisposeNode(businessInterceptConfigHelper.getDisposeNodeByConstants(DisposeNodeConstants.FINISH_WEIGHT));
        saveDisposeAfterInterceptMsgDto.setOperateUserErp(weightVolumeEntity.getOperatorCode());
        saveDisposeAfterInterceptMsgDto.setOperateUserName(weightVolumeEntity.getOperatorName());
        saveDisposeAfterInterceptMsgDto.setSiteCode(weightVolumeEntity.getOperateSiteCode());
        saveDisposeAfterInterceptMsgDto.setSiteName(weightVolumeEntity.getOperateSiteName());

        try {
            // logger.info("DMSWeightVolumeServiceImpl sendDisposeAfterInterceptMsg saveDisposeAfterInterceptMsgDto: {}", JSON.toJSONString(saveInterceptMqMsg));
            businessInterceptReportService.sendDisposeAfterInterceptMsg(saveDisposeAfterInterceptMsgDto);
        } catch (Exception e) {
            String saveInterceptMqMsg = JSON.toJSONString(saveDisposeAfterInterceptMsgDto);
            logger.error("DMSWeightVolumeServiceImpl sendDisposeAfterInterceptMsg exception [{}]" , saveInterceptMqMsg, e);
        }
        return true;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DMSWeightVolumeService.dealWeightAndVolume.sync", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity) {
        /* 同步处理 */
        return this.dealWeightAndVolume(entity,true);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DMSWeightVolumeService.weightVolumeRuleCheck", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition) {
        return weightVolumeHandlerStrategy.weightVolumeRuleCheck(condition);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DMSWeightVolumeService.weightVolumeExcessDeal", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public String weightVolumeExcessDeal(WeightVolumeCondition condition) {
        if(!isCInternet(condition.getBarCode())){
            return null;
        }
        JSONObject remark = new JSONObject();
        if(condition.getWeight() > WeightVolumeRuleConstant.WEIGHT_MAX_LIMIT_C){
            remark.put("weight",condition.getWeight());
            condition.setWeight(Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.WEIGHT_MAX_LIMIT_C)));
        }
        if(!Objects.equals(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name(),condition.getSourceCode())){
            return remark.isEmpty() ? null : remark.toJSONString();
        }
        if(condition.getVolume() == null || condition.getVolume() <= Constants.DOUBLE_ZERO){
            condition.setVolume(condition.getLength()*condition.getWidth()*condition.getHeight());
        }
        if(condition.getVolume() > WeightVolumeRuleConstant.VOLUME_MAX_LIMIT_RECORD_C
                || condition.getLength() * condition.getWidth() * condition.getHeight() > WeightVolumeRuleConstant.VOLUME_MAX_LIMIT_RECORD_C){
            remark.put("volume",condition.getVolume());
            condition.setVolume(Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.VOLUME_MAX_LIMIT_RECORD_C)));
        }
        if(condition.getLength() > WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C){
            remark.put("length",condition.getLength());
            condition.setLength(Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C)));
        }
        if(condition.getWidth() > Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C))){
            remark.put("width",condition.getWidth());
            condition.setWidth(Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C)));
        }
        if(condition.getHeight() > Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C))){
            remark.put("height",condition.getHeight());
            condition.setHeight(Double.parseDouble(String.valueOf(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C)));
        }
        return remark.isEmpty() ? null : remark.toJSONString();
    }

    /**
     * 无称重量方拦截
     *
     * @param entity@return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DMSWeightVolumeService.zeroWeightVolumeIntercept", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public Boolean zeroWeightVolumeIntercept(ZeroWeightVolumeCheckEntity entity) {
        String waybillCode = entity.getWaybillCode();
        String waybillSign = entity.getWaybillSign();
        String packageCode = entity.getPackageCode();
        String customerCode = entity.getCustomerCode();
        WaybillCache waybillNoCache = null;
        logger.info("零称重量方拦截逻辑开始,waybillCode={},packageCode={}",waybillCode,packageCode);
        try{

            //waybillSign没有 或者 waybillSign和customerCode同时没有 前置补充数据
            if(StringUtils.isBlank(waybillSign) ||
                    (StringUtils.isBlank(waybillSign) && StringUtils.isBlank(customerCode))){
                logger.info("零称重量方，需要提前加载数据,waybillCode={},packageCode={}",waybillCode,packageCode);
                waybillNoCache = waybillCacheService.getNoCache(waybillCode);
                if(waybillNoCache == null){
                    logger.warn("零称重量方，未获取到运单数据,waybillCode={},packageCode={}",waybillCode,packageCode);
                    return Boolean.FALSE;
                }
                if(StringUtils.isBlank(waybillSign)){
                    waybillSign = waybillNoCache.getWaybillSign();
                }
                if(StringUtils.isBlank(customerCode)){
                    customerCode = waybillNoCache.getCustomerCode();
                }
            }

            //获取拦截场景
            ZeroWeightVolumeCheckType checkType = needCheckWeightAndVolume(waybillCode,waybillSign,customerCode);
            if(!ZeroWeightVolumeCheckType.NOT_CHECK.equals(checkType)){
                //获取称重流水数据
                if (!packageWeightingService.weightVolumeValidate(waybillCode, packageCode,checkType)) {
                    logger.info("零称重量方 本地库未查到重量体积,waybillCode={},packageCode={}",waybillCode,packageCode);
                    if(ZeroWeightVolumeCheckType.CHECK_DMS_AGAIN_WEIGHT.equals(checkType)){
                        logger.info("零称重量方 经济网场景无重量拦截,waybillCode={},packageCode={}",waybillCode,packageCode);
                        return Boolean.TRUE;
                    }else{
                        logger.info("零称重量方 本地库未查到重量体积，调用运单接口检查,waybillCode={},packageCode={}",waybillCode,packageCode);
                        //称重流水未获取到时需要从运单接口查  数据没有下放的极端情况下 防止已加载少加载一次
                        waybillNoCache = waybillNoCache == null ? waybillCacheService.getNoCache(waybillCode):waybillNoCache;
                        if (waybillNoCache != null) {
                            logger.info("零称重量方 调用运单接口检查称重量方数据,waybillCode={},againW={},againV={}",waybillCode,waybillNoCache.getAgainWeight(),waybillNoCache.getSpareColumn2());
                            //判断运单上重量体积（复重：AGAIN_WEIGHT、复量方SPARE_COLUMN2，下单重量、下单体积）是否同时不存在（非空，>0）
                            if(ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_OR_VOLUME.equals(checkType)){
                                if ((waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0)
                                        && (org.apache.commons.lang.StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0)
                                        && (waybillNoCache.getWeight() == null || waybillNoCache.getWeight() <= 0)
                                        && (waybillNoCache.getVolume() == null || waybillNoCache.getVolume() <= 0)){
                                    logger.info("零称重量方 无复重且复量方且下单重量且下单量方数据，需要拦截，运单号{}",waybillCode);
                                    return Boolean.TRUE;
                                }

                            }else {
                                //判断运单上重量体积（复重：AGAIN_WEIGHT、复量方SPARE_COLUMN2）是否同时存在（非空，>0）
                                if (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                                        || org.apache.commons.lang.StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0) {
                                    logger.info("零称重量方 无复重或复量方数据，需要拦截，运单号{}",waybillCode);
                                    return Boolean.TRUE;
                                }
                            }
                        }else{
                            logger.info("零称重量方 调用运单接口检查称重量方数据,未获取到运单信息,waybillCode={}",waybillCode);
                        }
                    }
                }else{
                    logger.info("零称重量方 通过称重流水获取到称重量方信息，不需要拦截,waybillCode={}",waybillCode);
                }
            }
            return Boolean.FALSE;
        }catch (Exception e){
            logger.error("零称重量方拦截逻辑异常,运单号{},包裹号{}",waybillCode,packageCode,e);
            return Boolean.FALSE;
        }finally {
            logger.info("零称重量方拦截逻辑结束,waybillCode={},packageCode={}",waybillCode,packageCode);
        }
    }


    private ZeroWeightVolumeCheckType needCheckWeightAndVolume(String waybillCode,String waybillSign,String customerCode){
        /*******************************     经济网      *************************************/
        if(BusinessUtil.isEconomicNetValidateWeightVolume(waybillCode,waybillSign)){
            logger.info("零称重量方拦截经济网，需要拦截，运单号{}",waybillCode);
            return ZeroWeightVolumeCheckType.CHECK_DMS_AGAIN_WEIGHT;
        }
        /*******************************     经济网 结束     *************************************/


        /*******************************     外单      *************************************/

        /*******************************     纯配外单      *************************************/

        if(BusinessHelper.isAllPureOutWaybill(waybillSign)){

            /*************纯配外单 统一不拦截场景开始******************/

            //逆向不拦截
            if (!BusinessUtil.isForeignForwardAndWaybillMarkForward(waybillSign)) {
                logger.info("零称重量方 纯配外单 逆向场景，不需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            if (WaybillUtil.isReturnCode(waybillCode)) {
                logger.info("零称重量方 纯配外单 返单场景，不需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            //退货取件
            if (!BusinessUtil.isSignChar(waybillSign, 17, '0')) {
                logger.info("零称重量方 纯配外单 逆向退货取件场景，不需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            //内部商家不拦截
            if(org.apache.commons.lang.StringUtils.isNotBlank(customerCode)){
                BasicTraderNeccesaryInfoDTO basicTraderNeccesaryInfoDTO = baseMinorManager.getBaseTraderNeccesaryInfo(customerCode);
                //traderMold  内部商家类型编码
                if(basicTraderNeccesaryInfoDTO != null &&
                        basicTraderNeccesaryInfoDTO.getTraderMold()!=null &&
                        basicTraderNeccesaryInfoDTO.getTraderMold().equals(TraderMoldTypeEnum.inside_type.getCode())){
                    logger.info("零称重量方 纯配外单 内部商家，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }
            }

            /*************纯配外单 统一不拦截场景结束******************/

            /*************纯配外单 统一拦截场景开始******************/
            //运费临时欠款
            if(BusinessUtil.isTemporaryArrearsWaybill(waybillSign)){
                logger.info("零称重量方 运费临时欠款，需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
            }

            /*************纯配外单 统一拦截场景结束******************/

            if(BusinessUtil.isCInternet(waybillSign)){
                /*************纯配外单 C网 统一拦截场景开始******************/

                //C网的信任商家拦截
                if(BusinessHelper.isTrust(waybillSign)){
                    logger.info("零称重量方 纯配外单 C网 信任商家，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_OR_VOLUME;
                }

                //特殊商家类型  且  个性化运单场景
                if (BusinessUtil.isSignInChars(waybillSign, 32, '0','2','K','Y')
                        &&
                        BusinessUtil.isSignInChars(waybillSign, 24, '0','1','3')
                ) {
                    logger.info("零称重量方 纯配外单 C网 特殊商家类型 且 个性化运单场景，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
                }
                /*************纯配外单 C网 统一拦截场景结束******************/
            }else{
                /*************纯配外单 非C网 统一不拦截场景开始******************/
                //非C网信任商家不拦截
                if(BusinessHelper.isTrust(waybillSign)){
                    logger.info("零称重量方 纯配外单 非C网 信任商家场景，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }
                //不需要称重
                if(BusinessUtil.isNoNeedWeight(waybillSign)){
                    logger.info("零称重量方 纯配外单 非C网 不需要称重场景，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }

                /*************纯配外单 非C网 统一不拦截场景结束******************/


                /*************纯配外单 非C网 统一拦截场景开始******************/
                //快运零担
                if(BusinessUtil.isSignChar(waybillSign, 40, '2')){
                    logger.info("零称重量方 纯配外单 非C网 快运零担场景，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
                }

                /*************纯配外单 非C网 统一拦截场景结束******************/

            }

        }

        /*******************************     纯配外单 结束      *************************************/

        /*******************************     外单 结束    *************************************/
        logger.info("零称重量方 任何条件未匹配到，不需要拦截，运单号{}",waybillCode);
        return ZeroWeightVolumeCheckType.NOT_CHECK;
    }



    /**
     * 判断是否是C网运单
     * @param barCode
     * @return
     */
    private boolean isCInternet(String barCode) {
        // 是否C网标识
        boolean isCInternet = false;
        try {
            if(WaybillUtil.isWaybillCode(barCode) || WaybillUtil.isPackageCode(barCode)){
                BaseEntity<String> baseEntity
                        = waybillQueryManager.getWaybillSignByWaybillCode(WaybillUtil.getWaybillCode(barCode));
                if(baseEntity != null && BusinessUtil.isCInternet(baseEntity.getData())){
                    isCInternet = true;
                }
            }
        }catch (Exception e){
            logger.error("根据单号{}查询运单异常!",barCode);
        }
        return isCInternet;
    }
}
