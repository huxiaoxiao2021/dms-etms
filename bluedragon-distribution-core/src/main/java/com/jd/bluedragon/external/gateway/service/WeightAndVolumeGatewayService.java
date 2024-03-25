package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeCondition;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeRuleCheckDto;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeUploadResult;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-03-22 13:55
 **/
public interface WeightAndVolumeGatewayService {

    /**
     * 进行重量体积规则检查
     * @param dto 应用重量体积规则检查的数据传输对象
     * @return 响应布尔值，表示规则检查结果
     */
    JdCResponse<Boolean> weightVolumeRuleCheck(AppWeightVolumeRuleCheckDto dto);
    /**
     * 检查和提交
     * @param condition
     * @return
     */
    JdCResponse<AppWeightVolumeUploadResult> checkAndUpload(AppWeightVolumeCondition condition);
}
