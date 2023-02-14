package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.weight.request.WeightVolumeRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.external.gateway.service.JyWeightVolumeGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zs
 * @date 2023/2/14 15:13
 * @description 企配仓称重量方接口
 */
@Slf4j
@Service
public class JyWeightVolumeGatewayServiceImpl implements JyWeightVolumeGatewayService {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    /**
     * @param request 请求req
     * @return {@link Boolean}
     * @description 企配仓称重量方
     * @author zs
     * @date 2023/2/14 15:11
     **/
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeGatewayService.weightVolumeCheckAndDeal",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Boolean> weightVolumeCheckAndDeal(WeightVolumeRequest request) {

        String barCode = request.getBarCode();
        if (log.isInfoEnabled()) {
            log.info("barCode{} 称重量方 request={}", barCode, JSON.toJSONString(request));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();

        try {
            //称重量方前置校验
            WeightVolumeRuleCheckDto weightVolumeRuleCheckDto = new WeightVolumeRuleCheckDto();
            BeanUtils.copyProperties(request, weightVolumeRuleCheckDto);
            weightVolumeRuleCheckDto.setSourceCode(request.getSourceCode());
            weightVolumeRuleCheckDto.setBusinessType(request.getBusinessType());
            InvokeResult<Boolean> ruleCheck = dmsWeightVolumeService.weightVolumeRuleCheck(weightVolumeRuleCheckDto);

            if (!ruleCheck.codeSuccess()) {
                result.error(ruleCheck.getMessage());
                return result;
            }

            //称重数据超限处理
            WeightVolumeCondition condition = new WeightVolumeCondition();
            BeanUtils.copyProperties(request, condition);
            String remark = dmsWeightVolumeService.weightVolumeExcessDeal(condition);
            WeightVolumeEntity entity = new WeightVolumeEntity()
                    .barCode(condition.getBarCode())
                    .businessType(WeightVolumeBusinessTypeEnum.valueOf(condition.getBusinessType()))
                    .sourceCode(FromSourceEnum.valueOf(condition.getSourceCode()))
                    .height(condition.getHeight()).weight(condition.getWeight()).width(condition.getWidth()).length(condition.getLength()).volume(condition.getVolume())
                    .operateSiteCode(condition.getOperateSiteCode()).operateSiteName(condition.getOperateSiteName())
                    .operatorId(condition.getOperatorId()).operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                    .operateTime(new Date(condition.getOperateTime())).longPackage(condition.getLongPackage())
                    .machineCode(condition.getMachineCode()).remark(remark);

            //称重上传
            result = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        } catch (Exception e) {
            log.error("barCode{}, error:", barCode, e);
            result.error("称重量方功能异常，请联系分拣小秘！");
        } finally {
            if (log.isInfoEnabled()) {
                log.info("barCode{} 称重量方 response={}", barCode, JSON.toJSONString(result));
            }
        }
        return result;
    }
}
