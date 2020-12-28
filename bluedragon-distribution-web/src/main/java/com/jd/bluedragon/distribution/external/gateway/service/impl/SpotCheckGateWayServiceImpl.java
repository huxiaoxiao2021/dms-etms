package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.IsExcessReq;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.external.gateway.service.SpotCheckGateWayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: PDA抽检
 * @author: wuming
 * @create: 2020-12-28 14:47
 */
public class SpotCheckGateWayServiceImpl implements SpotCheckGateWayService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckGateWayServiceImpl.class);

    @Autowired
    private WeightAndVolumeCheckOfB2bService weightAndVolumeCheckOfB2bService;


    @JProfiler(jKey = "DMSWEB.SpotCheckGateWayServiceImpl.checkIsExcess", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<Integer> checkIsExcess(IsExcessReq req) {
        JdCResponse<Integer> jdCResponse = new JdCResponse<>();
        if (null == req) {
            jdCResponse.toConfirm("请求参数不能为空！");
            return jdCResponse;
        }
        if (StringUtils.isBlank(req.getWaybillCode())) {
            jdCResponse.toConfirm("运单号为空！");
            return jdCResponse;
        }
        if (StringUtils.isBlank(req.getLoginErp())) {
            jdCResponse.toConfirm("操作人信息不能为空！");
            return jdCResponse;
        }
        if (null == req.getCreateSiteCode()) {
            jdCResponse.toConfirm("操作人所属站点不能为空！");
            return jdCResponse;
        }
        if (null == req.getWeight() || null == req.getVolume()) {
            jdCResponse.toConfirm("抽检总量体积不能为空！");
            return jdCResponse;
        }
        try {
            WeightVolumeCheckConditionB2b conditionB2b = new WeightVolumeCheckConditionB2b();
            conditionB2b.setCreateSiteCode(req.getCreateSiteCode());
            conditionB2b.setWaybillOrPackageCode(req.getWaybillCode());
            conditionB2b.setLoginErp(req.getLoginErp());
            conditionB2b.setWaybillVolume(req.getVolume());
            conditionB2b.setWaybillWeight(req.getWeight());
            InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> invokeResult = weightAndVolumeCheckOfB2bService.checkIsExcessOfWaybill(conditionB2b);
            if (null == invokeResult || CollectionUtils.isEmpty(invokeResult.getData())) {
                jdCResponse.toFail("抽检校验超标操作失败！");
                return jdCResponse;
            }
            WeightVolumeCheckOfB2bWaybill weightVolumeCheckOfB2bWaybill = invokeResult.getData().get(0);
            if (null != weightVolumeCheckOfB2bWaybill) {
                jdCResponse.toSucceed("操作成功！");
                jdCResponse.setData(weightVolumeCheckOfB2bWaybill.getIsExcess());
                return jdCResponse;
            }
        } catch (Exception e) {
            logger.error("DMSWEB.SpotCheckGateWayServiceImpl.checkIsExcess error={},waybillCode={}", e, req.getWaybillCode());
        }
        jdCResponse.toFail("操作失败！");
        return jdCResponse;
    }
}
