package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.weight.request.WeightVolumeRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyWeightVolumeGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author zs
 * @date 2023/2/14 15:13
 * @description 企配仓称重量方接口
 */
@Slf4j
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
    public JdVerifyResponse<Boolean> weightVolumeCheckAndDeal(WeightVolumeRequest request) {

        String barCode = request != null ? request.getBarCode() : null;
        if (log.isInfoEnabled()) {
            log.info("barCode{}, 企配仓称重量方request：{}", barCode, JSON.toJSONString(request));
        }

        JdVerifyResponse<Boolean> result = new JdVerifyResponse<>();
        if (StringUtils.isBlank(barCode)) {
            result.toError("运单号/包裹号为空, 请重新输入");
            return result;
        }

        //对包裹发送全程跟踪补充体积
        if (WaybillUtil.isPackageCode(request.getBarCode()) &&
                request.getHeight() != null && request.getLength() != null && request.getWidth() != null) {
            request.setVolume(request.getHeight() * request.getLength() * request.getWidth());
        }

        try {
            //称重量方前置校验
            if (!request.isForceSubmit()) {
                WeightVolumeRuleCheckDto weightVolumeRuleCheckDto = new WeightVolumeRuleCheckDto();
                BeanUtils.copyProperties(request, weightVolumeRuleCheckDto);
                InvokeResult<Boolean> ruleCheck = dmsWeightVolumeService.weightVolumeRuleCheck(weightVolumeRuleCheckDto);

                if (!ruleCheck.codeSuccess()) {
                    result.setCode(ruleCheck.getCode());
                    result.setMessage(ruleCheck.getMessage());
                    result.setData(ruleCheck.getData());
                    if (InvokeResult.CODE_CONFIRM.equals(result.getCode())) {
                        result.setMsgBoxes(Lists.newArrayList(new JdVerifyResponse.MsgBox(MsgBoxTypeEnum.CONFIRM, ruleCheck.getCode(), ruleCheck.getMessage())));
                    }
                    return result;
                }
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
                    .operatorId(condition.getOperatorId())
                    .operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                    .operateTime(new Date()).longPackage(condition.getLongPackage())
                    .machineCode(condition.getMachineCode()).remark(remark);

            //称重上传
            InvokeResult<Boolean> invokeResult = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.TRUE);
            result.setCode(invokeResult.getCode());
            result.setMessage(invokeResult.getMessage());
            result.setData(invokeResult.getData());
        } catch (Exception e) {
            log.error("barCode{}, error:", barCode, e);
            result.toError("企配仓称重量方功能异常，请联系分拣小秘！");
        } finally {
            if (log.isInfoEnabled()) {
                log.info("barCode{}, 企配仓称重量方response：{}", barCode, JSON.toJSONString(result));
            }
        }
        return result;
    }
}
