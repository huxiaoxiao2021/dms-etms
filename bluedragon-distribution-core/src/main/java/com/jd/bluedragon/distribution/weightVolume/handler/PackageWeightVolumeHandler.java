package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.distribution.weight.domain.OpeSendObject;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *     安包裹称重的处理类：
 *     调用运单的包裹称重的处理waybillPackageApi.uploadOpe()
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("packageWeightVolumeHandler")
public class PackageWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("dmsWeightSendMQ")
    private DefaultJMQProducer dmsWeightSendMQ;

    @Autowired
    private SiteService siteService;

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("dwsSpotCheckProducer")
    private DefaultJMQProducer dwsSpotCheckProducer;

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @Override
    protected void weightVolumeRuleCheckHandler(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(commonCheckIntercept(weightVolumeContext, result)){
            return;
        }
        if(BusinessUtil.isCInternet(weightVolumeContext.getWaybill().getWaybillSign())){
            checkCInternetRule(weightVolumeContext, result);
            return;
        }
        checkBInternetRule(weightVolumeContext, result);
    }

    @Override
    protected void basicVerification(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!WaybillUtil.isWaybillCode(weightVolumeContext.getBarCode()) && !WaybillUtil.isPackageCode(weightVolumeContext.getBarCode())){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_0);
            return;
        }
        if(Objects.equals(weightVolumeContext.getCheckWeight(),true)){
            if(weightVolumeContext.getWeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_1);
                return;
            }
        }
        if(Objects.equals(weightVolumeContext.getCheckLWH(),true)){
            if(weightVolumeContext.getLength() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_2);
                return;
            }
            if(weightVolumeContext.getWidth() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_3);
                return;
            }
            if(weightVolumeContext.getHeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_4);
            }
        }
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        entity.setPackageCode(entity.getBarCode());
        if (entity.getLength() != null && entity.getHeight() != null && entity.getVolume() != null && !NumberHelper.gt0(entity.getVolume())) {
            entity.setVolume(entity.getHeight() * entity.getLength() * entity.getWidth());
        }

        // 抽检数据处理
        spotCheckDeal(entity);

        PackOpeDto packOpeDto = new PackOpeDto();
        packOpeDto.setWaybillCode(entity.getWaybillCode());
        packOpeDto.setOpeType(1);//分拣操作环节赋值：1
        // 根据用户ERP获取站点类型，分拣中心默认传1，非分拣中心都传2
        this.setPackOpeSiteType(entity, packOpeDto);

        PackOpeDetail packOpeDetail = new PackOpeDetail();
        packOpeDetail.setPackageCode(entity.getPackageCode());
        packOpeDetail.setOpeSiteId(entity.getOperateSiteCode());
        packOpeDetail.setOpeSiteName(entity.getOperateSiteName());
        packOpeDetail.setOpeUserId(entity.getOperatorId());
        packOpeDetail.setOpeUserName(entity.getOperatorName());
        packOpeDetail.setpHigh(entity.getHeight());
        packOpeDetail.setpLength(entity.getLength());
        packOpeDetail.setpWidth(entity.getWidth());
        packOpeDetail.setpWeight(entity.getWeight());
        packOpeDetail.setOpeTime(DateHelper.formatDateTime(entity.getOperateTime()));
        packOpeDetail.setLongPackage(entity.getLongPackage());
        packOpeDto.setOpeDetails(Collections.singletonList(packOpeDetail));
        try {
            logger.info("PackageWeightVolumeHandler handlerWeighVolume uploadOpe param: " + JsonHelper.toJson(packOpeDto));
            Map<String, Object> resultMap = waybillPackageManager.uploadOpe(JsonHelper.toJson(packOpeDto));
            if (resultMap != null && resultMap.containsKey("code")
                    && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(resultMap.get("code").toString())) {
                logger.info("向运单系统回传包裹称重信息成功：{}", entity.getPackageCode());
            } else {
                logger.warn("向运单系统回传包裹称重信息失败：{}，运单返回值：{}", entity.getPackageCode(), JsonHelper.toJson(resultMap));
            }

            /* 原始逻辑：发送MQ，在未来的日志里看看能否合并 */
            OpeSendObject opeSend = new OpeSendObject();
            opeSend.setPackage_code(entity.getPackageCode());
            opeSend.setDms_site_id(entity.getOperateSiteCode());
            opeSend.setThisUpdateTime(entity.getOperateTime().getTime());
            opeSend.setWeight(entity.getWeight() == null? 0f : (float)(double)entity.getWeight());//精度丢失问题
            opeSend.setLength(entity.getLength() == null? 0f :(float)(double)entity.getLength());//精度丢失问题
            opeSend.setWidth(entity.getWidth() == null? 0f :(float)(double)entity.getWidth());//精度丢失问题
            opeSend.setHigh(entity.getHeight() == null? 0f :(float)(double)entity.getHeight());//精度丢失问题
            opeSend.setOpeUserId(entity.getOperatorId());
            opeSend.setOpeUserName(entity.getOperatorName());
            if (opeSend.getHigh() != null && opeSend.getLength() != null && opeSend.getWidth() != null) {
                //计算体积
                opeSend.setVolume(opeSend.getHigh() * opeSend.getLength() * opeSend.getWidth());
            }
            dmsWeightSendMQ.send(entity.getPackageCode(),JsonHelper.toJson(opeSend));
        } catch (RuntimeException | JMQException e) {
            logger.warn("按包裹称重量方发生异常，处理失败：{}",JsonHelper.toJson(entity));
        }
    }

    /**
     * 抽检数据处理
     *  自动化称重量方设备上传的运单/包裹，且为一单一件，且上游站点/分拣中心操作过称重，才进行抽检
     * @param entity
     */
    private void spotCheckDeal(WeightVolumeEntity entity) {
        if(!FromSourceEnum.DMS_AUTOMATIC_MEASURE.equals(entity.getSourceCode()) || isFirstWeightVolume(entity)){
            return;
        }
        try {
            PackWeightVO packWeightVO = convertToPackWeightVO(entity);
            dwsSpotCheckProducer.send(entity.getBarCode(), JsonHelper.toJson(packWeightVO));
        }catch (Exception e){
            logger.error("发送dws抽检MQ异常!", e);
        }
    }

    /**
     * 自动化设备是否超标处理接口
     * @param entity
     * @return
     */
    public InvokeResult<Boolean> automaticDealSportCheck(WeightVolumeEntity entity) {
        if (logger.isInfoEnabled()) {
            logger.info("自动化称重抽检-handler参数:{}", JsonHelper.toJson(entity));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();
        //自动化称重量方设备上传的运单/包裹，且为一单一件，且上游站点/分拣中心操作过称重，才进行抽检
        if(FromSourceEnum.DMS_AUTOMATIC_MEASURE.equals(entity.getSourceCode()) && !isFirstWeightVolume(entity)){
            InvokeResult<Integer> checkExcessStatusResult = spotCheckCurrencyService.checkIsExcessWithOutOtherCheck(convertToSpotCheckDto(entity));
            result.setMessage(checkExcessStatusResult.getMessage());
            result.setData(Objects.equals(checkExcessStatusResult.getData(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));
            return result;
        }
        if (logger.isInfoEnabled()) {
            logger.info("自动化称重抽检-handler-不满足自动化抽检条件-参数:{}", JsonHelper.toJson(entity));
        }
        result.setMessage("首次称重不进行抽检!");
        return result;
    }

    private void setPackOpeSiteType(WeightVolumeEntity entity, PackOpeDto packOpeDto){
        /* 2021年01月20日15:57:30  增加 经济网按箱拆分包裹称重类型*/
        if(entity.getSourceCode().equals(FromSourceEnum.ENET_BOX_SPLIT_PACKAGE)){
            packOpeDto.setOpeType(14);

        }else {
            BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(entity.getOperatorCode());
            // 线上【青龙基础资料】-【数据字典】-【部门类型】
            if (baseStaffByErp != null && !BusinessUtil.isSortingSiteType(baseStaffByErp.getSiteType())) {
                packOpeDto.setOpeType(2);
            }
        }
    }

    //是否为首次称重量方，根据运单/包裹的全程跟踪状态值是否为“-160”
    public boolean isFirstWeightVolume(WeightVolumeEntity entity){
        String waybillCode = WaybillUtil.getWaybillCode(entity.getBarCode());
        String state = "-160";
        List<PackageStateDto> packageStateDtos = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode,state);
        if(CollectionUtils.isEmpty(packageStateDtos)){
            return true;
        }else {
            return false;
        }
    }

    public PackWeightVO convertToPackWeightVO(WeightVolumeEntity entity){
        PackWeightVO packWeightVO = new PackWeightVO();
        packWeightVO.setWeight(entity.getWeight());
        packWeightVO.setLength(entity.getLength());
        packWeightVO.setWidth(entity.getWidth());
        packWeightVO.setHigh(entity.getHeight());
        packWeightVO.setCodeStr(entity.getBarCode());
        packWeightVO.setOperatorId(entity.getOperatorId());
        packWeightVO.setOperatorName(entity.getOperatorName());
        packWeightVO.setErpCode(entity.getOperatorCode());
        BaseStaffSiteOrgDto site = siteService.getSite(entity.getOperateSiteCode());
        packWeightVO.setOrganizationName(site.getOrgName());
        packWeightVO.setOrganizationCode(site.getOrgId());
        packWeightVO.setOperatorSiteName(entity.getOperateSiteName());
        packWeightVO.setOperatorSiteCode(entity.getOperateSiteCode());
        packWeightVO.setMachineCode(entity.getMachineCode());
        return packWeightVO;
    }

    private SpotCheckDto convertToSpotCheckDto(WeightVolumeEntity entity) {
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(entity.getBarCode());
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName());
        spotCheckDto.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode());
        spotCheckDto.setWeight(entity.getWeight());
        spotCheckDto.setLength(entity.getLength());
        spotCheckDto.setWidth(entity.getWidth());
        spotCheckDto.setHeight(entity.getHeight());
        spotCheckDto.setVolume(entity.getVolume());
        spotCheckDto.setSiteCode(entity.getOperateSiteCode());
        spotCheckDto.setSiteName(entity.getOperateSiteName());
        spotCheckDto.setOperateUserErp(entity.getOperatorCode());
        spotCheckDto.setOperateUserName(entity.getOperatorName());
        spotCheckDto.setDimensionType(SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode());
        return spotCheckDto;
    }
}
