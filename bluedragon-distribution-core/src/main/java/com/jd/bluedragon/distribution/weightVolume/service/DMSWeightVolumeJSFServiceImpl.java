package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.handler.PackageWeightVolumeHandler;
import com.jd.bluedragon.distribution.weightvolume.DMSWeightVolumeJSFService;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeJSFEntity;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣称重量方处理JSF实现
 *
 * @author wuzuxiang
 * @since 2020/1/10
 **/
@Slf4j
@Service("dmsWeightVolumeJSFService")
public class DMSWeightVolumeJSFServiceImpl implements DMSWeightVolumeJSFService {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;
    @Autowired
    private PackageWeightVolumeHandler packageWeightVolumeHandler;

    @Override
    public InvokeResult<Boolean> dealSyncWeightVolume(WeightVolumeJSFEntity entity) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.success();
        invokeResult.setData(Boolean.TRUE);

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result =
                dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity);
        if (null != result) {
            invokeResult.setData(result.getData());
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            return invokeResult;
        }
        return invokeResult;
    }

    @Override
    public InvokeResult<Boolean> dealAsyncWeightVolume(WeightVolumeJSFEntity entity) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.success();
        invokeResult.setData(Boolean.TRUE);

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result =
                dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity, Boolean.FALSE);
        if (null != result) {
            invokeResult.setData(result.getData());
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            return invokeResult;
        }
        return invokeResult;
    }

    @Override
    public InvokeResult<Boolean> checkAndDealWeightVolume(WeightVolumeJSFEntity entity) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.success();
        invokeResult.setData(Boolean.TRUE);

        // 称重量方前置校验
        WeightVolumeRuleCheckDto weightVolumeRuleCheckDto = new WeightVolumeRuleCheckDto();
        BeanUtils.copyProperties(entity,weightVolumeRuleCheckDto);
        weightVolumeRuleCheckDto.setSourceCode(entity.getSourceCode().name());
        weightVolumeRuleCheckDto.setBusinessType(entity.getBusinessType().name());
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result
                = dmsWeightVolumeService.weightVolumeRuleCheck(weightVolumeRuleCheckDto);
        if(!result.codeSuccess() && result.getCode() != com.jd.bluedragon.distribution.base.domain.InvokeResult.CODE_HINT){
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            invokeResult.setData(result.getData());
            return invokeResult;
        }
        // 称重量方上传处理
        invokeResult = dealAsyncWeightVolume(entity);

        // 前置校验成功并提示，且称重量方上传成功，则提示前置校验提示语
        if(result.getCode() == com.jd.bluedragon.distribution.base.domain.InvokeResult.CODE_HINT
                && invokeResult.codeSuccess()){
            invokeResult.setCode(com.jd.bluedragon.distribution.base.domain.InvokeResult.CODE_HINT);
            invokeResult.setMessage(result.getMessage());
            return invokeResult;
        }
        return invokeResult;
    }

    @Override
    public InvokeResult<Boolean> automaticWeightCheckExcess(WeightVolumeJSFEntity entity) {
        if (log.isInfoEnabled()) {
            log.info("自动化称重抽检参数:{}", JsonHelper.toJson(entity));
        }
        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        BeanUtils.copyProperties(entity, weightVolumeEntity);

        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> invokeResult =
                packageWeightVolumeHandler.automaticDealSportCheck(weightVolumeEntity);

        InvokeResult<Boolean> result = new InvokeResult<>();
        BeanUtils.copyProperties(invokeResult, result);
        if (log.isInfoEnabled()) {
            log.info("自动化称重抽检参数:{},响应:{}", JsonHelper.toJson(entity),JsonHelper.toJson(result));
        }
        return result;
    }
}
