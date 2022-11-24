package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdExtraMessageResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.delivery.DeliveryOperationServiceImpl;
import com.jd.bluedragon.distribution.seal.manager.SealCarManager;
import com.jd.bluedragon.distribution.transport.service.TransportRelatedService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.vos.dto.StopoverInfoDto;
import com.jd.etms.vos.dto.StopoverQueryDto;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/7/12 11:16 AM
 */
@Service("transportRelatedService")
public class TransportRelatedServiceImpl implements TransportRelatedService {

    private static final Logger log = LoggerFactory.getLogger(TransportRelatedServiceImpl.class);

    @Autowired
    private SealCarManager sealCarManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 校验运输任务
     *  left：code编码表示成功或失败
     *  right：表示原因
     * @param siteCode 场地ID
     * @param transWorkCode 派车单号
     * @param sealCarCode 封车编码
     * @param simpleCode 明细简码
     * @param vehicleNumber 车牌号
     * @retur
     */
    @Override
    public ImmutablePair<Integer, String> checkTransportTask(Integer siteCode, String transWorkCode, String sealCarCode,
                                                             String simpleCode, String vehicleNumber) {
        if(siteCode == null || StringUtils.isEmpty(transWorkCode) && StringUtils.isEmpty(sealCarCode)
                && StringUtils.isEmpty(simpleCode) && StringUtils.isEmpty(vehicleNumber)){
            return ImmutablePair.of(InvokeResult.RESULT_PARAMETER_ERROR_CODE, InvokeResult.PARAM_ERROR);
        }
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
        StopoverQueryDto stopoverQueryDto = new StopoverQueryDto();
        stopoverQueryDto.setSiteCode(operateSite == null ? null : operateSite.getDmsSiteCode());
        stopoverQueryDto.setTransWorkCode(transWorkCode);
        stopoverQueryDto.setSealCarCode(sealCarCode);
        stopoverQueryDto.setSimpleCode(simpleCode);
        stopoverQueryDto.setVehicleNumber(vehicleNumber);
        StopoverInfoDto stopoverInfoDto = sealCarManager.queryStopoverInfo(stopoverQueryDto);
        if(stopoverInfoDto == null){
            return ImmutablePair.of(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        // 站点类型1:始发;2:经停;3:目的
        // 站点类型=经停 且 装车计数＞0  且 卸车计数=0
        if(Objects.equals(stopoverInfoDto.getSiteType(), 2)
                && NumberHelper.isPositiveNumber(stopoverInfoDto.getLoadCount())
                && Objects.equals(stopoverInfoDto.getUnloadCount(), 0)){
            return ImmutablePair.of(JdExtraMessageResponse.CODE_HINT, "途径本场地只装不卸，请先操作无任务解封签!");
        }
        // 站点类型=经停  且 装车计数=0  且 卸车计数＞0
        if(Objects.equals(stopoverInfoDto.getSiteType(), 2)
                && Objects.equals(stopoverInfoDto.getLoadCount(), 0)
                && NumberHelper.isPositiveNumber(stopoverInfoDto.getUnloadCount())){
            return ImmutablePair.of(JdExtraMessageResponse.CODE_HINT, "途径本场地只卸不装，卸车完成后请操作无货上封签!");
        }
        return ImmutablePair.of(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
    }

    @Override
    public String isMergeCar(Integer siteCode, String transWorkCode, String sealCarCode,
                                                             String simpleCode, String vehicleNumber) {
        if(siteCode == null || StringUtils.isEmpty(transWorkCode) && StringUtils.isEmpty(sealCarCode)
                && StringUtils.isEmpty(simpleCode) && StringUtils.isEmpty(vehicleNumber)){
            return null;
        }
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
        StopoverQueryDto stopoverQueryDto = new StopoverQueryDto();
        stopoverQueryDto.setSiteCode(operateSite == null ? null : operateSite.getDmsSiteCode());
        stopoverQueryDto.setTransWorkCode(transWorkCode);
        stopoverQueryDto.setSealCarCode(sealCarCode);
        stopoverQueryDto.setSimpleCode(simpleCode);
        stopoverQueryDto.setVehicleNumber(vehicleNumber);
        StopoverInfoDto stopoverInfoDto = sealCarManager.queryStopoverInfo(stopoverQueryDto);
        log.info("siteCode:{},transWorkCode:{},sealCarCode:{},simpleCode:{},vehicleNumber:{}",
                siteCode,transWorkCode,sealCarCode,simpleCode,vehicleNumber);
        log.info("stopoverInfoDto:{}", JSON.toJSONString(stopoverInfoDto));
        if(stopoverInfoDto == null){
            return "否";
        }
        // 站点类型=经停  且 装车计数=0  且 卸车计数＞0
        if(Objects.equals(stopoverInfoDto.getSiteType(), 2)
                && Objects.equals(stopoverInfoDto.getLoadCount(), 0)
                && NumberHelper.isPositiveNumber(stopoverInfoDto.getUnloadCount())){
            return "是";
        } else {
            return "否";
        }
    }
}
