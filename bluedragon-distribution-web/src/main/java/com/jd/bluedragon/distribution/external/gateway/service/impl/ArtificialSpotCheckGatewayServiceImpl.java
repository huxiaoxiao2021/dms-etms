package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckRequest;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckResult;
import com.jd.bluedragon.common.dto.spotcheck.PicAutoDistinguishRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckHandlerTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.external.gateway.service.ArtificialSpotCheckGatewayService;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 人工抽检网关服务实现
 *
 * @author hujiping
 * @date 2021/8/19 8:09 下午
 */
@Service("artificialSpotCheckGatewayService")
public class ArtificialSpotCheckGatewayServiceImpl implements ArtificialSpotCheckGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(ArtificialSpotCheckGatewayServiceImpl.class);

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.obtainBaseInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<ArtificialSpotCheckResult> obtainBaseInfo(ArtificialSpotCheckRequest request) {
        JdCResponse<ArtificialSpotCheckResult> response = new JdCResponse<ArtificialSpotCheckResult>();
        response.toSucceed();
        try {
            InvokeResult<Waybill> invokeResult = spotCheckCurrencyService.obtainBaseInfo(request.getBarCode());
            ArtificialSpotCheckResult result = new ArtificialSpotCheckResult();
            int packNum = (invokeResult.getData() == null || invokeResult.getData().getGoodNumber() == null) ? Constants.NUMBER_ZERO : invokeResult.getData().getGoodNumber();
            result.setIsMultiPack(packNum > Constants.CONSTANT_NUMBER_ONE);
            response.init(invokeResult.getCode(), invokeResult.getMessage());
            response.setData(result);
        }catch (Exception e){
            logger.error("根据单号:{}获取基础信息异常!", request.getBarCode(), e);
            response.toError();
        }
        return response;
    }

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.artificialCheckIsExcess", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<Integer> artificialCheckIsExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest) {
        JdCResponse<Integer> response = new JdCResponse<Integer>();
        response.toSucceed();
        try {
            SpotCheckDto spotCheckDto = transferSpotCheckDto(artificialSpotCheckRequest);
            spotCheckDto.setSpotCheckHandlerType(SpotCheckHandlerTypeEnum.ONLY_CHECK_IS_EXCESS.getCode());
            InvokeResult<Integer> invokeResult = spotCheckCurrencyService.checkIsExcess(spotCheckDto);
            response.init(invokeResult.getCode(), invokeResult.getMessage());
            response.setData(invokeResult.getData());
        }catch (Exception e){
            logger.error("校验抽检是否超标异常!", e);
            response.toError();
        }
        return response;
    }

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.artificialCheckExcess", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<ArtificialSpotCheckResult> artificialCheckExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest) {
        JdCResponse<ArtificialSpotCheckResult> response = new JdCResponse<ArtificialSpotCheckResult>();
        response.toSucceed();
        try {
            SpotCheckDto spotCheckDto = transferSpotCheckDto(artificialSpotCheckRequest);
            InvokeResult<SpotCheckResult> spotCheckResult = spotCheckCurrencyService.checkExcess(spotCheckDto);
            response.init(spotCheckResult.getCode(), spotCheckResult.getMessage());
            if(spotCheckResult.getData() != null){
                ArtificialSpotCheckResult artificialSpotCheckResult = new ArtificialSpotCheckResult();
                BeanUtils.copyProperties(spotCheckResult.getData(), artificialSpotCheckResult);
                response.setData(artificialSpotCheckResult);
            }
        }catch (Exception e){
            logger.error("校验抽检超标异常!", e);
            response.toError();
        }
        return response;
    }

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.picAutoDistinguish", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<Void> picAutoDistinguish(PicAutoDistinguishRequest picAutoDistinguishRequest) {
        JdCResponse<Void> response = new JdCResponse<Void>();
        response.toSucceed();
        try {
            InvokeResult<Boolean> result = spotCheckCurrencyService.picAutoDistinguish(picAutoDistinguishRequest);
            response.init(result.getCode(), result.getMessage());
        }catch (Exception e){
            logger.error("校验抽检超标异常!", e);
            response.toError();
        }
        return response;
    }

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.artificialSubmitSpotCheckInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<Void> artificialSubmitSpotCheckInfo(ArtificialSpotCheckRequest artificialSpotCheckRequest) {
        JdCResponse<Void> response = new JdCResponse<Void>();
        response.toSucceed();
        try {
            SpotCheckDto spotCheckDto = transferSpotCheckDto(artificialSpotCheckRequest);
            spotCheckDto.setSpotCheckHandlerType(SpotCheckHandlerTypeEnum.CHECK_AND_DEAL.getCode());
            InvokeResult<Boolean> result = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
            response.init(result.getCode(), result.getMessage());
        }catch (Exception e){
            logger.error("提交抽检数据异常!", e);
            response.toError();
        }
        return response;
    }

    private SpotCheckDto transferSpotCheckDto(ArtificialSpotCheckRequest artificialSpotCheckRequest) {
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(artificialSpotCheckRequest.getBarCode());
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName());
        spotCheckDto.setWeight(artificialSpotCheckRequest.getWeight());
        spotCheckDto.setLength(artificialSpotCheckRequest.getLength());
        spotCheckDto.setWidth(artificialSpotCheckRequest.getWidth());
        spotCheckDto.setHeight(artificialSpotCheckRequest.getHeight());
        spotCheckDto.setVolume(artificialSpotCheckRequest.getVolume());
        spotCheckDto.setOrgId(artificialSpotCheckRequest.getOperateOrgId());
        spotCheckDto.setOrgName(artificialSpotCheckRequest.getOperateOrgName());
        spotCheckDto.setSiteCode(artificialSpotCheckRequest.getOperateSiteCode());
        spotCheckDto.setSiteName(artificialSpotCheckRequest.getOperateSiteName());
        spotCheckDto.setOperateUserId(artificialSpotCheckRequest.getOperateUserId());
        spotCheckDto.setOperateUserErp(artificialSpotCheckRequest.getOperateUserErp());
        spotCheckDto.setOperateUserName(artificialSpotCheckRequest.getOperateUserName());
        spotCheckDto.setDimensionType(Objects.equals(artificialSpotCheckRequest.getIsWaybillSpotCheck(), true)
                ? SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode() : SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode());
        spotCheckDto.setExcessStatus(artificialSpotCheckRequest.getExcessStatus());
        spotCheckDto.setExcessType(artificialSpotCheckRequest.getExcessType());
        spotCheckDto.setPictureUrls(artificialSpotCheckRequest.getPictureUrlsMap());
        spotCheckDto.setIsReformedSpotCheck(artificialSpotCheckRequest.getIsReformedSpotCheck() != null && artificialSpotCheckRequest.getIsReformedSpotCheck());
        return spotCheckDto;
    }
}
