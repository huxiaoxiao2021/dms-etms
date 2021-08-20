package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.external.gateway.service.ArtificialSpotCheckGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(ArtificialSpotCheckGatewayServiceImpl.class);

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @JProfiler(jKey = "dms.ArtificialSpotCheckGatewayService.artificialCheckIsExcess", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<Integer> artificialCheckIsExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest) {
        JdCResponse<Integer> response = new JdCResponse<Integer>();
        response.toSucceed();
        try {
            InvokeResult<Integer> invokeResult = spotCheckCurrencyService.checkIsExcess(transferSpotCheckDto(artificialSpotCheckRequest));
            response.setData(invokeResult.getData());
        }catch (Exception e){
            logger.error("校验抽检是否超标异常!", e);
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
            spotCheckCurrencyService.spotCheckDeal(transferSpotCheckDto(artificialSpotCheckRequest));
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
        spotCheckDto.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode());
        spotCheckDto.setWeight(artificialSpotCheckRequest.getWeight());
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
        spotCheckDto.setPictureUrls(artificialSpotCheckRequest.getPictureUrlsMap());
        return spotCheckDto;
    }
}
