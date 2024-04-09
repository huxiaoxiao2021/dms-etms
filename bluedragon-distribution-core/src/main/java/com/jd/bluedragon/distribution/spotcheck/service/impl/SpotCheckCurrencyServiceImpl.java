package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.spotcheck.PicAutoDistinguishRequest;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckBusinessException;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.handler.SpotCheckHandlerStrategy;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 抽检通用接口实现
 *
 * @author hujiping
 * @date 2021/8/10 9:36 上午
 */
@Service("spotCheckCurrencyService")
public class SpotCheckCurrencyServiceImpl implements SpotCheckCurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCurrencyServiceImpl.class);

    @Autowired
    private SpotCheckHandlerStrategy spotCheckHandlerStrategy;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public InvokeResult<Waybill> obtainBaseInfo(String barCode) {
        InvokeResult<Waybill> result = new InvokeResult<Waybill>();
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(barCode));
        if(waybill == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "此单无运单数据，请联系'分拣小秘'!");
            return result;
        }
        result.setData(waybill);
        return result;
    }

    @Override
    public InvokeResult<Integer> checkIsExcessWithOutOtherCheck(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.checkIsExcessWithOutOtherCheck", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            result = spotCheckHandlerStrategy.checkIsExcessWithOutOtherCheck(spotCheckDto);
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public InvokeResult<Integer> checkIsExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.checkIsExcess", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            InvokeResult<SpotCheckResult> spotCheckResult = spotCheckHandlerStrategy.checkExcess(spotCheckDto);
            result.customMessage(spotCheckResult.getCode(), spotCheckResult.getMessage());
            result.setData(spotCheckResult.getData().getExcessStatus());
            // 新版抽检入口返回具体的超标类型
            if (Constants.NUMBER_ONE.equals(spotCheckDto.getVersion())) {
                result.setData(spotCheckResult.getData().getExcessType());
            }
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public InvokeResult<SpotCheckResult> checkExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<SpotCheckResult> result = new InvokeResult<SpotCheckResult>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.checkExcess", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            result = spotCheckHandlerStrategy.checkExcess(spotCheckDto);
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> picAutoDistinguish(PicAutoDistinguishRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.picAutoDistinguish", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            Double weight = request.getWeight();
            String picUrl = request.getPicUrl();
            // 将图片url转换为对应的内网域名
            picUrl = transformUrl(picUrl);
            if (logger.isInfoEnabled()) {
                logger.info("picAutoDistinguish|人工抽检转换图片url域名结果:picUrl={}", picUrl);
            }
            Integer uploadPicType = request.getPicType();
            Integer excessType = request.getExcessType();
            ImmutablePair<Integer, String> immutablePair
                    = spotCheckDealService.singlePicAutoDistinguish(waybillCode, weight, picUrl, uploadPicType, excessType);
            result.customMessage(immutablePair.getLeft(), immutablePair.getRight());
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(request), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 将图片url转换成对应的内网域名
     */
    private String transformUrl(String picUrl) {
        if (StringUtils.isBlank(picUrl)) {
            return picUrl;
        }
        try {
            // 查询内网域名与外网域名转换关系对应的配置
            SysConfig domainMapJsonStr = sysConfigService.findConfigContentByConfigName(Constants.VIDEO_DOMAIN_TRANSFORM_MAP);
            if (domainMapJsonStr == null) {
                logger.warn("picAutoDistinguish|人工抽检转换图片url域名未找到对应的配置信息,不作替换");
                return picUrl;
            }
            // 将配置信息转换成map
            Map<String, Object> domainMap = JsonHelper.json2MapNormal(domainMapJsonStr.getConfigContent());
            if (domainMap == null || domainMap.isEmpty()) {
                logger.warn("picAutoDistinguish|人工抽检转换图片url域名配置信息转换map为空,不作替换");
                return picUrl;
            }
            for (String externalDomain : domainMap.keySet()) {
                // 找到对应的外网域名
                if (picUrl.contains(externalDomain)) {
                    String internalDomain = String.valueOf(domainMap.get(externalDomain));
                    // 用内网域名进行替换
                    picUrl = picUrl.replace(externalDomain, internalDomain);
                    return picUrl;
                }
            }
        } catch (Exception e) {
            logger.error("picAutoDistinguish|将图片url转换成对应的内网域名时出现异常:picUrl={}", picUrl, e);
        }
        return picUrl;
    }

    /**
     * 抽检处理
     *
     * @param spotCheckDto
     * @return
     */
    @Override
    public InvokeResult<Boolean> spotCheckDeal(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.spotCheckDeal", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            result = spotCheckHandlerStrategy.dealSpotCheck(spotCheckDto);
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (SpotCheckSysException e){
            logger.error("抽检系统异常，进行重试!");
            throw e;
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    public boolean isWaybillSignValid(Waybill waybill) {
        String waybillSign = waybill.getWaybillSign();
        // 快递：waybillSign40=0
        if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_0)) {
            return true;
        }
        // 快运：waybillSign40=1/2/3，且80位不等于6/7/8（剔除冷链），且89位不等于1/2（剔除tc），且99位不等于1（剔除京小仓）
        if (BusinessUtil.isBInternet(waybillSign)) {
            return true;
        }
        // 医药冷链（此次新增）：waybillSign31位=D
        if (BusinessUtil.isMedicalFreshProductType(waybillSign)) {
            return true;
        }
        // 医药零担（此次新增）：waybillSign40=2/3，且waybillSign80 =7，且waybillSign54=4
        if (BusinessUtil.isMedicine(waybillSign)) {
            return true;
        }
        // 冷链专送：waybillSign31位=G
        if (BusinessUtil.isColdDelivery(waybillSign)) {
            return true;
        }
        // 冷链城配/卡班/小票：waybillSign40=2，且waybillSign54=2，且waybillSign80=6/7
        if (BusinessUtil.isColdChainCPKBReceipt(waybillSign)) {
            return true;
        }
        return false;
    }

}
