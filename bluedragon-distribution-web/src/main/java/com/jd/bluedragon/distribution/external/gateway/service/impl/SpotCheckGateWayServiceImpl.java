package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.SpotCheckCheckReq;
import com.jd.bluedragon.common.dto.spotcheck.SpotCheckRecordReq;
import com.jd.bluedragon.common.dto.spotcheck.SpotCheckSubmitReq;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.external.gateway.service.SpotCheckGateWayService;
import com.alibaba.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @JProfiler(jKey = "DMSWEB.SpotCheckGateWayServiceImpl.checkIsExcess", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<Integer> checkIsExcess(SpotCheckCheckReq req) {
        logger.info("DMSWEB.SpotCheckGateWayServiceImpl.checkIsExcess-parameter={}", JSON.toJSONString(req));
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
            // 执行新抽检逻辑
            if(uccPropertyConfiguration.getAndroidIsExecuteNewSpotCheck()){
                com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer> checkIsExcessResult
                        = spotCheckCurrencyService.checkIsExcess(convertToSpotCheckDto(req));
                jdCResponse.setCode(checkIsExcessResult.getCode());
                jdCResponse.setMessage(checkIsExcessResult.getMessage());
                jdCResponse.setData(checkIsExcessResult.getData());
                return jdCResponse;
            }
            WeightVolumeCheckConditionB2b conditionB2b = new WeightVolumeCheckConditionB2b();
            conditionB2b.setCreateSiteCode(req.getCreateSiteCode());
            conditionB2b.setWaybillOrPackageCode(req.getWaybillCode());
            conditionB2b.setLoginErp(req.getLoginErp());
            conditionB2b.setWaybillVolume(req.getVolume());
            conditionB2b.setWaybillWeight(req.getWeight());
            conditionB2b.setAndroidSpotCheck(true);
            InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> invokeResult = weightAndVolumeCheckOfB2bService.checkIsExcessOfWaybill(conditionB2b);
            if (null == invokeResult || CollectionUtils.isEmpty(invokeResult.getData())) {
                jdCResponse.toFail("操作失败！");
                return jdCResponse;
            }
            WeightVolumeCheckOfB2bWaybill weightVolumeCheckOfB2bWaybill = invokeResult.getData().get(0);
            if (null != weightVolumeCheckOfB2bWaybill) {
                jdCResponse.toSucceed("操作成功！");
                jdCResponse.setData(weightVolumeCheckOfB2bWaybill.getIsExcess());
                return jdCResponse;
            }
        } catch (Exception e) {
            logger.error("DMSWEB.SpotCheckGateWayServiceImpl.checkIsExcess error waybillCode={}", req.getWaybillCode(), e);
        }
        jdCResponse.toFail("操作失败！");
        return jdCResponse;
    }

    @JProfiler(jKey = "DMSWEB.SpotCheckGateWayServiceImpl.spotCheckSubmit", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<Void> spotCheckSubmit(SpotCheckSubmitReq req) {
        logger.info("SpotCheckGateWayServiceImpl.spotCheckSubmit-parameter={}", JSON.toJSONString(req));
        JdCResponse<Void> jdCResponse = new JdCResponse<>();
        if (null == req) {
            jdCResponse.toConfirm("请求参数不能为空！");
            return jdCResponse;
        }
        if (StringUtils.isBlank(req.getWaybillCode())) {
            jdCResponse.toConfirm("运单号不能为空！");
            return jdCResponse;
        }
        if (StringUtils.isBlank(req.getLoginErp()) || null == req.getCreateSiteCode()) {
            jdCResponse.toConfirm("操作人或站点编号不能为空！");
            return jdCResponse;
        }
        if (null == req.getVolume() || null == req.getWeight()) {
            jdCResponse.toConfirm("复核重量或体积不能为空！");
            return jdCResponse;
        }
        if (Constants.IS_EXCESS.equals(req.getExcessFlag()) && CollectionUtils.isEmpty(req.getUrls())) {
            jdCResponse.toConfirm("超标提交需要上传抽检图片！");
            return jdCResponse;
        }
        if (Constants.IS_EXCESS.equals(req.getExcessFlag()) && CollectionUtils.isNotEmpty(req.getUrls()) && req.getUrls().size() < 5) {
            jdCResponse.toConfirm("超标上传图片数不可小于五张！");
            return jdCResponse;
        }
        try {
            // 执行新抽检逻辑
            if(uccPropertyConfiguration.getAndroidIsExecuteNewSpotCheck()){
                spotCheckCurrencyService.spotCheckDeal(convertToSpotCheckDealDto(req));
                jdCResponse.toSucceed("操作成功！");
                return jdCResponse;
            }
            InvokeResult<String> result = weightAndVolumeCheckOfB2bService.dealExcessDataOfWaybill(convert(req));
            if (null != result && Constants.SUCCESS_CODE.equals(result.getCode())) {
                jdCResponse.toSucceed("操作成功！");
                return jdCResponse;
            }
        } catch (Exception e) {
            logger.error("DMSWEB.SpotCheckGateWayServiceImpl.spotCheckSubmit error waybillCode={}", req.getWaybillCode(), e);
        }
        jdCResponse.toFail("操作失败！");
        return jdCResponse;
    }

    private SpotCheckDto convertToSpotCheckDto(SpotCheckCheckReq req) {
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(req.getWaybillCode());
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName());
        spotCheckDto.setWeight(req.getWeight());
        spotCheckDto.setVolume(req.getVolume() * SpotCheckConstants.CM3_M3_MAGNIFICATION);
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(req.getLoginErp());
        spotCheckDto.setOrgId(baseStaff.getOrgId());
        spotCheckDto.setOrgName(baseStaff.getOrgName());
        spotCheckDto.setSiteCode(req.getCreateSiteCode());
        spotCheckDto.setSiteName(baseStaff.getSiteName());
        spotCheckDto.setOperateUserId(baseStaff.getStaffNo());
        spotCheckDto.setOperateUserErp(req.getLoginErp());
        spotCheckDto.setOperateUserName(baseStaff.getStaffName());
        spotCheckDto.setDimensionType(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode());
        return spotCheckDto;
    }

    private SpotCheckDto convertToSpotCheckDealDto(SpotCheckSubmitReq req) {
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(req.getWaybillCode());
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName());
        spotCheckDto.setWeight(req.getWeight());
        spotCheckDto.setVolume(req.getVolume() * SpotCheckConstants.CM3_M3_MAGNIFICATION);
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(req.getLoginErp());
        spotCheckDto.setOrgId(baseStaff.getOrgId());
        spotCheckDto.setOrgName(baseStaff.getOrgName());
        spotCheckDto.setSiteCode(req.getCreateSiteCode());
        spotCheckDto.setSiteName(baseStaff.getSiteName());
        spotCheckDto.setOperateUserId(baseStaff.getStaffNo());
        spotCheckDto.setOperateUserErp(req.getLoginErp());
        spotCheckDto.setOperateUserName(baseStaff.getStaffName());
        spotCheckDto.setDimensionType(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode());
        spotCheckDto.setExcessStatus(req.getExcessFlag());
        if(Objects.equals(ExcessStatusEnum.EXCESS_ENUM_YES.getCode(), req.getExcessFlag())){
            Map<String, String> picUtlMap = new LinkedHashMap<>();
            picUtlMap.put("total", StringUtils.join(req.getUrls(), Constants.SEPARATOR_SEMICOLON));
            spotCheckDto.setPictureUrls(picUtlMap);
        }
        return spotCheckDto;
    }


    /**
     * 转换参数
     *
     * @param req
     * @return
     */
    public WeightVolumeCheckConditionB2b convert(SpotCheckSubmitReq req) {
        WeightVolumeCheckConditionB2b conditionB2b = new WeightVolumeCheckConditionB2b();
        conditionB2b.setCreateSiteCode(req.getCreateSiteCode());
        conditionB2b.setIsExcess(req.getExcessFlag());
        conditionB2b.setLoginErp(req.getLoginErp());
        conditionB2b.setWaybillVolume(req.getVolume());
        conditionB2b.setWaybillOrPackageCode(req.getWaybillCode());
        conditionB2b.setWaybillWeight(req.getWeight());
        conditionB2b.setPdaSource(1);
        conditionB2b.setUrls(req.getUrls());
        conditionB2b.setAndroidSpotCheck(true);
        return conditionB2b;
    }


    /**
     * 校验抽检记录是否存在
     *
     * @param req
     * @return
     */
    @Override
    public JdCResponse<Void> checkRecordExist(SpotCheckRecordReq req) {
        JdCResponse<Void> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(req.getWaybillCode())) {
            jdCResponse.toConfirm("运单号不能为空");
            return jdCResponse;
        }
        if (null == req.getCreateSiteCode()) {
            jdCResponse.toConfirm("抽检站点编号为空");
            return jdCResponse;
        }
        InvokeResult<String> result = weightAndVolumeCheckOfB2bService.checkRecordExist(req.getWaybillCode(), req.getCreateSiteCode());
        if (null == result) {
            jdCResponse.toConfirm("操作失败");
            return jdCResponse;
        }
        if (result.getCode() == 600) {
            jdCResponse.toConfirm(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed("操作成功");
        return jdCResponse;
    }
}
