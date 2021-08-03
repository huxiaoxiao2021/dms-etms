package com.jd.bluedragon.distribution.weightVolume.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightVolume.check.WeightVolumeChecker;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.alibaba.fastjson.JSON;
import com.jd.etms.waybill.domain.BaseEntity;
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
