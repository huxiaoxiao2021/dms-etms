package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeCondition;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeRuleCheckDto;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeUploadResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeUploadResult;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.WeightAndVolumeGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-03-26 16:26
 **/
public class WeightAndVolumeGatewayServiceImpl implements WeightAndVolumeGatewayService {

    private final Logger logger = LoggerFactory.getLogger(WeightAndVolumeGatewayServiceImpl.class);

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;
    /**
     * 进行重量体积规则检查
     * @param request 应用重量体积规则检查DTO
     * @return 响应布尔值
     */
    @Override
    @JProfiler(jKey = "DMSWEB.WeightAndVolumeGatewayServiceImpl.weightVolumeRuleCheck",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> weightVolumeRuleCheck(AppWeightVolumeRuleCheckDto request) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed();
        logger.info("WeightAndVolumeGatewayServiceImpl->weightVolumeRuleCheck入参:{}", JsonHelper.toJson(request));
        if (!checkBeforeScan(response, request)) {
            return response;
        }
        WeightVolumeRuleCheckDto condition = new WeightVolumeRuleCheckDto();
        BeanUtils.copyProperties(request, condition);
        //来源
        condition.setSourceCode(FromSourceEnum.DMS_PDA.name());
        // 操作类型
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        condition.setCheckWeight(true);
        condition.setCheckVolume(true);
        //校验
        InvokeResult<Boolean> jdCResponseResult = dmsWeightVolumeService.weightVolumeRuleCheck(condition);
        response.setCode(jdCResponseResult.getCode());
        response.setMessage(jdCResponseResult.getMessage());
        response.setData(jdCResponseResult.getData());
        return response;
    }

    /**
     * 检查扫描前的条件
     * @param response JdCResponse<Boolean> 响应对象
     * @param request AppWeightVolumeRuleCheckDto 请求对象
     * @return boolean 是否满足条件
     * @throws NullPointerException 如果请求对象为空
     */
    private boolean checkBeforeScan(JdCResponse<Boolean> response, AppWeightVolumeRuleCheckDto request) {
        if(request == null){
            response.toFail("参数为空！");
            return false;
        }
        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            response.toFail("请扫描单号！");
            return false;
        }
        if (!WaybillUtil.isWaybillCode(barCode)) {
            response.toFail("只支持扫描运单号！");
            return false;
        }
        if(request.getWeight() ==null ){
            response.toFail("请输入重量！");
            return false;
        }
        if(request.getVolume() ==null ){
            response.toFail("请输入体积！");
            return false;
        }
        if(request.getOperateSiteCode() == null){
            response.toFail("操作场地为空！");
            return false;
        }
        if(StringUtils.isBlank(request.getOperatorCode())){
            response.toFail("操作人为空！");
            return false;
        }
        return true;
    }

    /**
     * 检查并上传应用重量体积条件
     * @param request 应用重量体积条件
     * @return null
     */

    @Override
    @JProfiler(jKey = "DMSWEB.WeightAndVolumeGatewayServiceImpl.checkAndUpload",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<AppWeightVolumeUploadResult> checkAndUpload(AppWeightVolumeCondition request) {
        JdCResponse<AppWeightVolumeUploadResult> response = new JdCResponse<>();
        response.toSucceed();
        logger.info("WeightAndVolumeGatewayServiceImpl->checkAndUpload入参:{}", JsonHelper.toJson(request));
        if (!checkBeforeUpload(response, request)) {
            return response;
        }
        WeightVolumeCondition condition = new WeightVolumeCondition();
        BeanUtils.copyProperties(request, condition);
        //来源
        condition.setSourceCode(FromSourceEnum.DMS_PDA.name());
        // 操作类型
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        condition.setTotalVolumeFlag(true);
        JdResult<WeightVolumeUploadResult> jdCResponseResult = dmsWeightVolumeService.checkBeforeUpload(condition);
        //校验成功，上传处理
        if(jdCResponseResult != null
                && jdCResponseResult.isSucceed()
                && jdCResponseResult.getData() != null
                && Boolean.TRUE.equals(jdCResponseResult.getData().getCheckResult())) {
            InvokeResult<Boolean> uploadResult = upload(condition);
            if(uploadResult != null && uploadResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
                jdCResponseResult.toSuccess("上传成功！");
            }else if(uploadResult != null){
                jdCResponseResult.toFail(uploadResult.getMessage());
            } else {
                jdCResponseResult.toFail("上传失败！");
            }
        }
        response.setCode(jdCResponseResult.getCode());
        response.setMessage(jdCResponseResult.getMessage());
        //data暂时无数据，预留返回值
        //response.setData(jdCResponseResult.getData());
        return response;
    }

    /**
     * 检查上传前的条件
     * @param response JdCResponse<Boolean> 响应对象
     * @param request AppWeightVolumeCondition 请求对象
     * @return boolean 检查结果，true表示通过，false表示未通过
     * @throws NullPointerException 如果request为null
     */
    private boolean checkBeforeUpload(JdCResponse<AppWeightVolumeUploadResult> response, AppWeightVolumeCondition request) {
        if(request == null){
            response.toFail("参数为空！");
            return false;
        }
        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            response.toFail("请扫描单号！");
            return false;
        }
        if (!WaybillUtil.isWaybillCode(barCode)) {
            response.toFail("只支持扫描运单号！");
            return false;
        }
        if(request.getWeight() ==null ){
            response.toFail("请输入重量！");
            return false;
        }
        if(request.getVolume() ==null ){
            response.toFail("请输入体积！");
            return false;
        }
        if(request.getOperateSiteCode() == null){
            response.toFail("操作场地为空！");
            return false;
        }
        if(StringUtils.isBlank(request.getOperatorCode())){
            response.toFail("操作人为空！");
            return false;
        }
        return true;
    }
    /**
     * 上传重量体积条件
     * @param condition 重量体积条件
     * @return result 结果
     * @throws Exception 可能会抛出异常
     */
    private InvokeResult<Boolean> upload(WeightVolumeCondition condition) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        // 称重数据超额处理
        String remark = dmsWeightVolumeService.weightVolumeExcessDeal(condition);
        WeightVolumeEntity entity = new WeightVolumeEntity()
                .barCode(condition.getBarCode())
                .businessType(WeightVolumeBusinessTypeEnum.valueOf(condition.getBusinessType()))
                .sourceCode(FromSourceEnum.valueOf(condition.getSourceCode()))
                .height(condition.getHeight()).weight(condition.getWeight()).width(condition.getWidth()).length(condition.getLength()).volume(condition.getVolume())
                .operateSiteCode(condition.getOperateSiteCode()).operateSiteName(condition.getOperateSiteName())
                .operatorId(condition.getOperatorId()).operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                .operateTime(new Date(condition.getOperateTime())).longPackage(condition.getLongPackage())
                .machineCode(condition.getMachineCode()).remark(remark).operatorData(condition.getOperatorData());
        entity.setOverLengthAndWeightEnable(condition.getOverLengthAndWeightEnable());
        entity.setOverLengthAndWeightTypes(condition.getOverLengthAndWeightTypes());
        InvokeResult<Boolean> invokeResult = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        result.setCode(invokeResult.getCode());
        result.setMessage(invokeResult.getMessage());
        result.setData(invokeResult.getData());
        return result;
    }
}
