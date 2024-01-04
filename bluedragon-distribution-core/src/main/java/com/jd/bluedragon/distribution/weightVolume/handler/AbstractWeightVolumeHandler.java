package com.jd.bluedragon.distribution.weightVolume.handler;

import com.esotericsoftware.minlog.Log;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.merchant.ExpressOrderServiceWsManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.enums.OverLengthAndWeightTypeEnum;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeCheckService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillPickup;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.merchant.sdk.b2b.constant.enumImpl.SystemCallerEnum;
import com.jd.merchant.sdk.order.dto.BaseInfo;
import com.jd.merchant.sdk.order.dto.UpdateOrderRequest;
import com.jd.merchant.sdk.product.dto.OverLengthAndWeight;
import com.jd.merchant.sdk.product.dto.ChannelInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;

import com.jdl.basic.api.enums.WorkSiteTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.WAYBILL_ZERO_WEIGHT_NOT_IN_MESSAGE;
import static com.jd.bluedragon.distribution.waybill.domain.WaybillStatus.*;
import static com.jd.bluedragon.distribution.waybill.domain.WaybillStatus.WAYBILL_STATUS_CODE_SITE_SORTING;
import static com.jd.bluedragon.distribution.weightvolume.FromSourceEnum.*;
import static com.jd.bluedragon.distribution.weightvolume.FromSourceEnum.DMS_WEB_PACKAGE_FAST_TRANSPORT;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isConvey;
import static com.jd.bluedragon.utils.BusinessHelper.isThirdSite;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public abstract class AbstractWeightVolumeHandler implements IWeightVolumeHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    @Autowired
    @Qualifier("dmsWeightVolumeFlowProducer")
    private DefaultJMQProducer dmsWeightVolumeFlowProducer;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Qualifier("dmsWeightVolumeCheckService")
    @Autowired
    private DMSWeightVolumeCheckService dmsWeightVolumeCheckService;
    
    @Autowired
    @Qualifier("expressOrderServiceWsManager")
    private ExpressOrderServiceWsManager expressOrderServiceWsManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillService waybillService;

    @Override
    public InvokeResult<Boolean> handlerOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);
        /* 处理称重量方任务 */
        handlerWeighVolume(entity);

        /* 处理之后记录流水 */
        postHandler(entity);

        return result;
    }

    protected abstract void handlerWeighVolume(WeightVolumeEntity entity);

    protected void postHandler(WeightVolumeEntity entity) {
        //推送统一称重量方消息
        dmsWeightVolumeFlowProducer.sendOnFailPersistent(entity.getBarCode(),JsonHelper.toJson(entity));

        WeightVolumeFlowEntity weightVolumeEntity = new WeightVolumeFlowEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        weightVolumeFlowJSFService.recordWeightVolumeFlow(weightVolumeEntity);
    }
    /**
     * 上传超长超重服务信息
     * @param entity
     */
    protected boolean uploadOverWeightInfo(WeightVolumeEntity entity) {
    	if(!dmsConfigManager.getPropertyConfig().isUploadOverWeightSwitch()
    			|| !Boolean.TRUE.equals(entity.getOverLengthAndWeightEnable())
    			|| CollectionUtils.isEmpty(entity.getOverLengthAndWeightTypes())) {
    		restLongPackage(entity);
    		return false;
    	}
    	UpdateOrderRequest updateData = new UpdateOrderRequest();
    	BaseInfo baseInfo = new BaseInfo();
    	baseInfo.setWaybillCode(entity.getWaybillCode());
    	baseInfo.setUpdateTime(entity.getOperateTime());
    	baseInfo.setUpdateUser(entity.getOperatorCode());
    	updateData.setBaseInfo(baseInfo);
    	ChannelInfo channelInfo = new ChannelInfo();
    	channelInfo.setSystemCaller(SystemCallerEnum.DMS_ETMS.getSystemCaller());
    	updateData.setChannelInfo(channelInfo);
    	OverLengthAndWeight overLengthAndWeight = new OverLengthAndWeight();
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.ONE_SIDE.getCode())) {
    		overLengthAndWeight.setSingleSideOverLength(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.THREED_SIDE.getCode())) {
    		overLengthAndWeight.setThreeSidesOverLength(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	if(entity.getOverLengthAndWeightTypes().contains(OverLengthAndWeightTypeEnum.OVER_WEIGHT.getCode())) {
    		overLengthAndWeight.setOverWeight(DmsConstants.OVER_LENGTHANDWEIGHT_FLAG);
    	}
    	updateData.setOverLengthAndWeight(overLengthAndWeight);
    	JdResult<Boolean> result = expressOrderServiceWsManager.updateOrderSelective(updateData);
    	
    	if(result.isSucceed()) {
    		logger.warn("{}超长超重服务上传成功！",entity.getWaybillCode());
    		return true;
    	}else {
    		logger.warn("{}超长超重服务上传失败！",entity.getWaybillCode());
    		restLongPackage(entity);
    	}
    	return false;
    }
    private void restLongPackage(WeightVolumeEntity entity){
        if(DmsConstants.WAYBILL_LONG_PACKAGE_OVER_WEIGHT.equals(entity.getLongPackage())) {
        	entity.setLongPackage(DmsConstants.WAYBILL_LONG_PACKAGE_DEFAULT);
        	Log.warn("{}重置超长超重标longPackage识为0！",entity.getWaybillCode());
        }
    }
    @Override
    public InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition) {
        InvokeResult<Boolean> result = new  InvokeResult<Boolean>();
        try {
            // 初始化上下文
            WeightVolumeContext weightVolumeContext = initWeightVolumeContext(condition);
            // 是否需要校验处理
            if(!checkIsNeedDeal(weightVolumeContext)){
                return result;
            }
            // 基础校验
            basicVerification(weightVolumeContext, result);
            if(!result.codeSuccess()){
                return result;
            }
            // dws校验
            if(dwsWeightVolumeCheck(weightVolumeContext, result)){
                return result;
            }
            // 校验处理
            if (dmsConfigManager.getPropertyConfig().getWeightVolumeSwitchVersion() == 0) {
                weightVolumeRuleCheckHandler(weightVolumeContext, result);
            } else if (dmsConfigManager.getPropertyConfig().getWeightVolumeSwitchVersion() == 1 && !WeightVolumeBusinessTypeEnum.BY_BOX.name().equals(condition.getBusinessType())) {
                weightVolumeRuleCheckHandlerNew(weightVolumeContext, result);
            }
        }catch (Exception e){
            // 为了不影响前台正常操作，异常只记录错误日志，但是返回成功
            logger.error("根据条件：{}校验称重量方规则异常!", JsonHelper.toJson(condition));
        }
        return result;
    }

    /**
     * 是否需要校验
     *  hit：
     *      1、信任商家不校验重量体积（站点平台除外）
     * @param weightVolumeContext
     * @return
     */
    private boolean checkIsNeedDeal(WeightVolumeContext weightVolumeContext) {
        if(!Objects.equals(weightVolumeContext.getSourceCode(), FromSourceEnum.DMS_CLIENT_SITE_PLATE_PRINT.name())){
            if(BusinessHelper.isTrust(weightVolumeContext.getWaybill().getWaybillSign())){
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化称重量方上下文
     * @param condition
     * @return
     */
    private WeightVolumeContext initWeightVolumeContext(WeightVolumeRuleCheckDto condition) {
        // 初始化基本数据
        WeightVolumeContext weightVolumeContext = new WeightVolumeContext();
        BeanUtils.copyProperties(condition, weightVolumeContext);
        if(condition.getWeight() == null){
            weightVolumeContext.setWeight(Constants.DOUBLE_ZERO);
        }
        if(condition.getLength() == null){
            weightVolumeContext.setLength(Constants.DOUBLE_ZERO);
        }
        if(condition.getWidth() == null){
            weightVolumeContext.setWidth(Constants.DOUBLE_ZERO);
        }
        if(condition.getHeight() == null){
            weightVolumeContext.setHeight(Constants.DOUBLE_ZERO);
        }
        if(condition.getVolume() == null || Objects.equals(condition.getVolume(), Constants.DOUBLE_ZERO)){
            weightVolumeContext.setVolume(weightVolumeContext.getLength() * weightVolumeContext.getWidth() * weightVolumeContext.getHeight());
        }
        // 初始化运单数据
        if(WaybillUtil.isWaybillCode(condition.getBarCode()) || WaybillUtil.isPackageCode(condition.getBarCode())){
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(WaybillUtil.getWaybillCode(condition.getBarCode()));
            if(waybill == null){
                throw new RuntimeException("无运单信息!");
            }
            weightVolumeContext.setWaybill(waybill);
        }
        // 初始化场地数据
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(condition.getOperateSiteCode());
        if(operateSite == null){
            throw new RuntimeException("操作人场地不存在!");
        }
        weightVolumeContext.setOperateSite(operateSite);

        // 设置称重量方规则
        weightVolumeContext.setWeightVolumeRuleConstant(JsonHelper.fromJson(dmsConfigManager.getPropertyConfig().getWeightVolumeRuleStandard(), WeightVolumeRuleConstant.class));
        return weightVolumeContext;
    }

    private boolean dwsWeightVolumeCheck(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!Objects.equals(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name(), weightVolumeContext.getSourceCode())){
            return false;
        }
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
        StringBuilder hintMessage = new StringBuilder();
        // 重量标准：大于1000kg按1000kg记录
        int weightMaxLimitC = weightVolumeRuleConstant.getWeightMaxLimitCS();
        if(weightVolumeContext.getWeight() > weightMaxLimitC){
            setNextRowChar(hintMessage);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_2,weightMaxLimitC,weightMaxLimitC));
        }
        // 边长标准：大于3m按3m记录
        int sideMaxLengthC = weightVolumeRuleConstant.getSideMaxLengthC();
        if(weightVolumeContext.getLength() > sideMaxLengthC
                || weightVolumeContext.getWidth() > sideMaxLengthC || weightVolumeContext.getHeight() > sideMaxLengthC){
            setNextRowChar(hintMessage);
            String sideMaxLengthCStr = keepDigitCompute(sideMaxLengthC, WeightVolumeRuleConstant.CM_M_MAGNIFICATION, Constants.NUMBER_ZERO);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_0,sideMaxLengthCStr));
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_RECORD,sideMaxLengthCStr));
        }
        // 体积标准：大于4m3按4m3记录
        int volumeMaxLimitRecord = weightVolumeRuleConstant.getVolumeMaxLimitRecord();
        if(weightVolumeContext.getVolume() > volumeMaxLimitRecord){
            setNextRowChar(hintMessage);
            String volumeMaxLimitRecordStr = keepDigitCompute(volumeMaxLimitRecord, WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION, Constants.NUMBER_ZERO);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_1,volumeMaxLimitRecordStr));
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_RECORD,volumeMaxLimitRecordStr));
        }
        // 设置提示信息
        if (StringUtils.isNotEmpty(hintMessage.toString())){
            result.hintMessage(hintMessage.toString());
        }
        return true;
    }

    protected abstract void weightVolumeRuleCheckHandler(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result);

    /**
     * 新的称重量方的判断标准，区别于<code>#weightVolumeRuleCheckHandler</code>
     * 需要新老版本的切换开关
     * @param weightVolumeContext
     * @param result
     */
    protected void weightVolumeRuleCheckHandlerNew(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result) {
        // 非0复重量体积拦截
        InvokeResult<Boolean> interceptResult= waybillZeroWeightIntercept(getWeightVolumeEntity(weightVolumeContext));
        if (interceptResult.getData()) {
            result.setCode(interceptResult.getCode());
            result.setMessage(interceptResult.getMessage());
            return;
        }

        if(!WeightVolumeBusinessTypeEnum.BY_BOX.name().equals(weightVolumeContext.getBusinessType())
                && commonCheckIntercept(weightVolumeContext, result)){
            return;
        }
        if(weightVolumeContext.getWaybill() != null && BusinessUtil.isCInternet(weightVolumeContext.getWaybill().getWaybillSign())){
            checkCInternetRuleNew(weightVolumeContext, result);
            return;
        }
        if (weightVolumeContext.getWaybill() != null && BusinessUtil.isWeightVolumeB(weightVolumeContext.getWaybill().getWaybillSign())) {
            checkBInternetRuleNew(weightVolumeContext, result);
            return;
        }
        checkBInternetRule(weightVolumeContext, result);
    }

    private WeightVolumeEntity getWeightVolumeEntity(WeightVolumeContext weightVolumeContext) {
        WeightVolumeEntity entity = new WeightVolumeEntity();
        entity.setOperateSiteCode(weightVolumeContext.getOperateSiteCode());
        entity.setBarCode(weightVolumeContext.getBarCode());
        entity.setOperatorCode(weightVolumeContext.getOperatorCode());
        entity.setSourceCode(FromSourceEnum.valueOf(weightVolumeContext.getSourceCode()));
        return entity;
    }

    protected abstract void basicVerification(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result);

    /**
     * C网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkCInternetRule(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        double volume = weightVolumeContext.getVolume();
        double weight = weightVolumeContext.getWeight();
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
        // 1、确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // 边长标准值
        int sideMaxLengthC = weightVolumeRuleConstant.getSideMaxLengthC();
        if(length > sideMaxLengthC || width > sideMaxLengthC || height > sideMaxLengthC){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_0,
                    keepDigitCompute(sideMaxLengthC,WeightVolumeRuleConstant.CM_M_MAGNIFICATION,Constants.NUMBER_ZERO)));
        }
        // 体积标准值
        int volumeMaxLimit = weightVolumeRuleConstant.getVolumeMaxLimitC();
        if(volume > volumeMaxLimit){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_1,
                    keepDigitCompute(volumeMaxLimit,WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION,Constants.CONSTANT_NUMBER_ONE)));
        }
        // 重量标准值
        if(Objects.equals(weight,Constants.DOUBLE_ZERO)){
            setNextRowChar(confirmMessage);
            confirmMessage.append(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_CONFIRM_8);
        }
        int weightMaxLimitF = weightVolumeRuleConstant.getWeightMaxLimitCF();
        int weightMaxLimitS = weightVolumeRuleConstant.getWeightMaxLimitCS();
        if(weight >= weightMaxLimitF && weight <= weightMaxLimitS){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_4,weightMaxLimitF));
        }
        if(weight > weightMaxLimitS){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_2,weightMaxLimitS,weightMaxLimitS));
        }
        // 泡重比、泡重比倍数标准值
        int foamWeightRatio = weightVolumeRuleConstant.getFoamWeightRatioC();
        int foamWeightRatioMultiple = weightVolumeRuleConstant.getFoamWeightRatioMultiple();
        if(volume > Constants.DOUBLE_ZERO && weight > Constants.DOUBLE_ZERO && volume > foamWeightRatio * foamWeightRatioMultiple * weight){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_3,foamWeightRatio,foamWeightRatioMultiple));
        }

        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    /**
     * https://joyspace.jd.com/pages/TCA96JrxtEiRPDLjPvKV
     *
     * @return
     */
    public InvokeResult<Boolean> waybillZeroWeightIntercept(WeightVolumeEntity entity) {
        String barCode = entity.getBarCode();
        String operatorCode = entity.getOperatorCode();
        FromSourceEnum sourceCode = entity.getSourceCode();
        Integer operateSiteCode = entity.getOperateSiteCode();
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);

        // 拦截开关
        if (!dmsConfigManager.getPropertyConfig().getWaybillZeroWeightInterceptSwitch()) {
            result.setData(false);
            return result;
        }

        // 只校验包裹和运单
        if (!WaybillUtil.isWaybillCode(barCode) && !WaybillUtil.isPackageCode(barCode)) {
            result.setData(false);
            return result;
        }

        // 根据erp判断当前操作人员所属机构是否为分拣场地人员
        if (checkErpBindingSite(operatorCode, result, sourceCode)) {
            return result;
        }

        // 获取运单信息
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        BigWaybillDto bigWaybill = waybillService.getWaybill(waybillCode);
        if (bigWaybill == null || bigWaybill.getWaybill() == null || bigWaybill.getWaybill().getWaybillSign() == null) {
            logger.info("未获取运单信息{}", waybillCode);
            result.setData(false);
            return result;
        }

        // 判断当前称重单据是否为快递快运正向外单单据 若不是，则不进行校验
        if (checkWaybillType(bigWaybill, result)) {
            logger.info("运单{}非快递快运正向外单单据", waybillCode);
            return result;
        }

        // 判断该包裹的揽收站点是否为城配车队，若为城配车队，则不进行以下校验。
        if (checkWaybillPickup(bigWaybill,result)) {
            logger.info("运单{}揽收站点不为城配车队", waybillCode);
            return result;
        }

        // 判断该包裹的预分拣派送站点是否为三方快递，若为三方快递，则不进行以下校验。
        if (checkOleSite(bigWaybill, result)) {
            logger.info("运单{}预分拣派送站点非三方快递", waybillCode);
            return result;
        }

        // 判断当前运单是否存在重量体积, 若存在则拦截
        if (checkWeightAndVolumeZero(bigWaybill, waybillCode,operateSiteCode, result)) {
            return result;
        }
        // 自动化称重 校验包裹在当前场地是否使用周转筐
//        checkRecycleBasket(barCode, operateSiteCode, result, sourceCode);
//        if (!result.getData()) {
//            return result;
//        }

        result.setCode(WAYBILL_ZERO_WEIGHT_INTERCEPT_CODE);
        result.setMessage(WAYBILL_ZERO_WEIGHT_INTERCEPT_MESSAGE);
        result.setData(true);
        return result;
    }

    private void checkRecycleBasket(String barCode, Integer operateSiteCode, InvokeResult<Boolean> result, FromSourceEnum sourceCode) {
//        if (DMS_AUTOMATIC_MEASURE.equals(sourceCode) || DMS_DWS_MEASURE.equals(sourceCode)) {
//            AkboxDetailJsfRequest request = new AkboxDetailJsfRequest();
//            request.setPackageCode(barCode);
//            request.setSiteId(operateSiteCode.toString());
//            request.setCurrentPage(1);
//            request.setPageSize(1);
//            List<AkboxDetailData> akboxDetailData = autoAkboxJsfManager.queryAkboxDetail(request);
//            if (!CollectionUtils.isEmpty(akboxDetailData)) {
//                result.setData(false);
//            }else {
//                result.setCode(WAYBILL_ZERO_WEIGHT_RECYCLE_BASKET_CODE);
//                result.setMessage(WAYBILL_ZERO_WEIGHT_RECYCLE_BASKET_MESSAGE);
//                result.setData(true);
//            }
//        }
    }

    private boolean checkWeightAndVolumeZero(BigWaybillDto bigWaybill, String waybillCode, Integer operateSiteCode, InvokeResult<Boolean> result) {
        Double weight = bigWaybill.getWaybill().getAgainWeight();
        String volume = bigWaybill.getWaybill().getSpareColumn2();
        if (weight == null || weight <= 0 || org.apache.commons.lang.StringUtils.isEmpty(volume) || Double.parseDouble(volume) <= 0) {
            logger.info("运单号不存在复重复量方{}", JsonHelper.toJson(bigWaybill));
            result.setData(false);
            return true;
        }

        // 如果当前运单不存在，则对包裹的重量体积进行校验
        List<DeliveryPackageD> packageList = bigWaybill.getPackageList();
        for (DeliveryPackageD deliveryPackageD : packageList) {
            if (deliveryPackageD.getAgainWeight() == null
                    || deliveryPackageD.getAgainWeight() <= 0
                    || deliveryPackageD.getAgainVolume() == null
                    || deliveryPackageD.getAgainVolume() <= 0) {
                logger.info("运单号下的包裹号{}不存在复重复量方", deliveryPackageD.getPackageBarcode());
                // 对0存在重量的运单，校验当前分拣中心在全程跟踪是否存在前置操作节点（解封车、验货、装箱、发货、分拣、封车等任意一条记录即可）
                Set<Integer> stateSet = getStateSet();
                // 不支持按场地查询，只能自己过滤
                List<PackageState> waybillTrace = waybillTraceManager.getAllOperationsByOpeCodeAndState(waybillCode, stateSet);
                for (PackageState packageState : waybillTrace) {
                    if (Objects.equals(packageState.getOperatorSiteId(), operateSiteCode)) {
                        result.setData(false);
                        return true;
                    }
                }
                result.setCode(WAYBILL_ZERO_WEIGHT_NOT_IN_CODE);
                result.setMessage(WAYBILL_ZERO_WEIGHT_NOT_IN_MESSAGE);
                result.setData(true);
                return true;
            }
        }
        return false;
    }

    private Set<Integer> getStateSet() {
        HashSet<Integer> stateSet = new HashSet<>();
        stateSet.add(WAYBILL_TRACK_UNSEAL_VEHICLE);
        stateSet.add(WAYBILL_STATUS_CODE_FORWARD_INSPECTION);
        stateSet.add(WAYBILL_STATUS_CODE_FORWARD_SORTING);
        stateSet.add(WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
        stateSet.add(WAYBILL_TRACK_SEAL_VEHICLE);
        stateSet.add(WAYBILL_STATUS_CODE_SITE_SORTING);
        return stateSet;
    }

    private boolean checkOleSite(BigWaybillDto bigWaybill, InvokeResult<Boolean> result) {
        Waybill waybill = bigWaybill.getWaybill();
        Integer oldSiteId = waybill.getOldSiteId();
        if (oldSiteId == null) {
            logger.info("运单号未获取到预分拣站点{}", JsonHelper.toJson(bigWaybill));
            result.setData(false);
            return true;
        }
        BaseStaffSiteOrgDto oldSite = baseMajorManager.getBaseSiteBySiteId(oldSiteId);
        if (isThirdSite(oldSite)) {
            logger.info("运单号的预分拣派送站点是为三方快递{}", JsonHelper.toJson(bigWaybill));
            result.setData(false);
            return true;
        }
        return false;
    }

    private boolean checkWaybillPickup(BigWaybillDto bigWaybill, InvokeResult<Boolean> result) {
        WaybillPickup waybillPickup = bigWaybill.getWaybillPickup();
        if (waybillPickup == null || waybillPickup.getPickupSiteId() == null) {
            result.setData(false);
            return false;
        }
        BaseStaffSiteOrgDto pickupSite = baseMajorManager.getBaseSiteBySiteId(waybillPickup.getPickupSiteId());
        if (isConvey(pickupSite.getSiteType())) {
            result.setData(false);
            return false;
        }
        return true;
    }

    private boolean checkErpBindingSite(String operatorCode, InvokeResult<Boolean> result, FromSourceEnum sourceCode) {
        if (DMS_CLIENT_BATCH_SORT_WEIGH_PRINT.equals(sourceCode) || DMS_CLIENT_SITE_PLATE_PRINT.equals(sourceCode)
                || DMS_CLIENT_FAST_TRANSPORT_PRINT.equals(sourceCode) || DMS_WEB_FAST_TRANSPORT.equals(sourceCode) || DMS_WEB_PACKAGE_FAST_TRANSPORT.equals(sourceCode)) {
            BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(operatorCode);
            if (null == baseStaffByErp || null == baseStaffByErp.getSortType() || null == baseStaffByErp.getSortSubType()) {
                logger.info("erp{}未获取到所属站点站点", operatorCode);
                result.setData(false);
                return true;
            }
            if (!Objects.equals(WorkSiteTypeEnum.DTS_TYPE.getFirstTypesOfThird(), baseStaffByErp.getSortType())
                    || !Objects.equals(WorkSiteTypeEnum.DTS_TYPE.getSecondTypesOfThird(), baseStaffByErp.getSortSubType())) {
                result.setData(false);
                return true;
            }
        }
        return false;
    }

    private boolean checkWaybillType(BigWaybillDto bigWaybill, InvokeResult<Boolean> result) {
        String waybillSign = bigWaybill.getWaybill().getWaybillSign();
        // 快递：waybillSign40=0
        // 快运：waybillSign40=1/2/3，且80位不等于6/7/8（剔除冷链），且89位不等于1/2（剔除tc），且99位不等于1（剔除京小仓）
        if (!BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_0)
                && !BusinessUtil.isBInternet(waybillSign)) {
            result.setData(false);
            return true;
        }
        // 正向外单 waybillsign1=3且waybillsign34=0
        if (!BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_3) &&
                !BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_34, WaybillSignConstants.CHAR_34_0)) {
            result.setData(false);
            return true;
        }
        return false;
    }
    /**
     * B网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkBInternetRule(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        double volume = weightVolumeContext.getVolume();
        double weight = weightVolumeContext.getWeight();
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
        Waybill waybill = weightVolumeContext.getWaybill();

        int packNum = waybill == null ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber() == null
                ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber();
        if(Objects.equals(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name(), weightVolumeContext.getBusinessType())){
            // 以包裹维度比较
            weight = weight / packNum;
            volume = volume / packNum;
        }

        // 1、强制提示信息
        StringBuilder forceMessage;

        if (!checkMinWeightAndVolume(weight, volume, weightVolumeContext, result)) {
            return;
        }

        // 重量校验：是否大于5000kg
        int weightMaxLimitB = weightVolumeRuleConstant.getWeightMaxLimitB();
        if(weight > weightMaxLimitB){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_3, weightMaxLimitB))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }
        // 体积校验：是否大于5m3
        int volumeMaxLimit = weightVolumeRuleConstant.getVolumeMaxLimitB();
        if(volume > volumeMaxLimit){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_1,
                    keepDigitCompute(volumeMaxLimit,WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION,Constants.NUMBER_ZERO)))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }
        // 泡重比校验：是否大于7800
        int foamWeightRatioB = weightVolumeRuleConstant.getFoamWeightRatioB();
        if(volume > Constants.DOUBLE_ZERO && weight * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION > foamWeightRatioB * volume){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_2,foamWeightRatioB))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }

        // 2、确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // 边长标准值：大于3m
        int sideMaxLengthB = weightVolumeRuleConstant.getSideMaxLengthB();
        if(length > sideMaxLengthB || width > sideMaxLengthB || height > sideMaxLengthB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_0,
                    keepDigitCompute(sideMaxLengthB,WeightVolumeRuleConstant.CM_M_MAGNIFICATION,Constants.NUMBER_ZERO)));
        }
        if(Objects.equals(weight,Constants.DOUBLE_ZERO)){
            setNextRowChar(confirmMessage);
            confirmMessage.append(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_CONFIRM_8);
        }
        // 重量及体积：200kg/1包裹 或 1立方/1包裹
        int weightMaxLimitConfirmB = weightVolumeRuleConstant.getWeightMaxLimitConfirmB();
        int volumeMaxLimitConfirmB = weightVolumeRuleConstant.getVolumeMaxLimitConfirmB();
        if(weight > weightMaxLimitConfirmB || volume > volumeMaxLimitConfirmB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_1,
                    weightMaxLimitConfirmB,volumeMaxLimitConfirmB/WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION));
        }
        // 泡重比：大于168小于330
        int foamWeightRatioConfirmFloorB = weightVolumeRuleConstant.getFoamWeightRatioConfirmFloorB();
        int foamWeightRatioConfirmCeilingB = weightVolumeRuleConstant.getFoamWeightRatioConfirmCeilingB();
        if(volume > Constants.DOUBLE_ZERO && !(weight * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION > volume * foamWeightRatioConfirmFloorB
                && weight * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION < volume * foamWeightRatioConfirmCeilingB)){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_3,
                    foamWeightRatioConfirmFloorB,foamWeightRatioConfirmCeilingB));
        }
        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    /**
     * C网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkCInternetRuleNew(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        Double volume = weightVolumeContext.getVolume();
        if (!NumberHelper.gt0(volume)) {
            volume = !NumberHelper.gt0(height) || !NumberHelper.gt0(width) || !NumberHelper.gt0(length)?
                    0 : length * width * height;
        }
        double weight = NumberHelper.gt0(weightVolumeContext.getWeight())? weightVolumeContext.getWeight() : 0;

        // 确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // C网包裹维度的校验
        if (WeightVolumeBusinessTypeEnum.BY_PACKAGE.name().equals(weightVolumeContext.getBusinessType())) {
            // 强卡控：包裹维度校验-单边大于1.5m，单包裹大于200KG
            if (length > WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C_1 || height > WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C_1
                    || width > WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C_1) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_PACKAGE_MAX_SIDE_C,
                        keepDigitCompute(WeightVolumeRuleConstant.SIDE_MAX_LENGTH_C_1, WeightVolumeRuleConstant.CM_M_MAGNIFICATION, Constants.CONSTANT_NUMBER_TWO)));
                result.setData(Boolean.FALSE);
                return;
            }
            if (weight > WeightVolumeRuleConstant.WEIGHT_MAX_LIMIT_C_1) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_PACKAGE_MAX_WEIGHT_C,WeightVolumeRuleConstant.WEIGHT_MAX_LIMIT_C_1));
                result.setData(Boolean.FALSE);
                return;
            }

            // 弱卡控：单包裹超30KG
            if (weight > WeightVolumeRuleConstant.PACKAGE_WEIGHT_MAX_LIMIT_C) {
                setNextRowChar(confirmMessage);
                confirmMessage.append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_6);
            }
            // 弱卡控：体积（cm³）除以重量（kg）大于20000小于500
            if (Boolean.TRUE.equals(weightVolumeContext.getCheckVolume())) {
                if (volume > WeightVolumeRuleConstant.FOAM_WEIGHT_RATIO_C_1 * weight
                        || volume < WeightVolumeRuleConstant.FOAM_WEIGHT_RATIO_C_2 * weight) {
                    result.confirmMessage(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_6);
                    result.setData(Boolean.FALSE);
                }
            }
        }

        // C网运单维度的校验
        if (WeightVolumeBusinessTypeEnum.BY_WAYBILL.name().equals(weightVolumeContext.getBusinessType())) {
            // 强卡控：运单维度校验-运单体积超过2m³，整个运单大于5000KG；禁止揽收
            if (weight > WeightVolumeRuleConstant.WAYBILL_WEIGHT_MAX_LIMIT_C) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_WAYBILL_MAX_WEIGHT_C,WeightVolumeRuleConstant.WAYBILL_WEIGHT_MAX_LIMIT_C));
                result.setData(Boolean.FALSE);
                return;
            }
            if (volume > WeightVolumeRuleConstant.WAYBILL_VOLUME_MAX_LIMIT_C * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_WAYBILL_MAX_VOLUME_C, WeightVolumeRuleConstant.WAYBILL_VOLUME_MAX_LIMIT_C));
                result.setData(Boolean.FALSE);
                return;
            }
        }

        if (!checkMinWeightAndVolume(weight, volume, weightVolumeContext, result)) {
            return;
        }
        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    private boolean checkMinWeightAndVolume(double weight, Double volume, WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        // 强卡控 重量校验：是否小于0.01kg
        Double waybillWeightMinLimit = weightVolumeContext.getWeightVolumeRuleConstant().getWeightMinLimit();
        if (Objects.equals(weightVolumeContext.getCheckWeight(),true) && weight < waybillWeightMinLimit) {
            result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_5, waybillWeightMinLimit));
            return false;
        }

        // 强卡控 体积校验：是否小于0.01cm3
        Double waybillVolumeMinLimit = weightVolumeContext.getWeightVolumeRuleConstant().getVolumeMinLimit();
        if(Objects.equals(weightVolumeContext.getCheckVolume(),true) && volume < waybillVolumeMinLimit){
            result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_6, waybillVolumeMinLimit));
            return false;
        }
        return true;
    }

    /**
     * B网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkBInternetRuleNew(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        Double volume = weightVolumeContext.getVolume();
        if (!NumberHelper.gt0(volume)) {
            volume = !NumberHelper.gt0(height) || !NumberHelper.gt0(width) || !NumberHelper.gt0(length)?
                    0 : length * width * height;
        }
        double weight = NumberHelper.gt0(weightVolumeContext.getWeight())? weightVolumeContext.getWeight() : 0;

        if (!checkMinWeightAndVolume(weight, volume, weightVolumeContext, result)) {
            return;
        }

        // 确认提示信息
        StringBuilder confirmMessage = new StringBuilder();

        //弱卡控：1. 重泡比校验： 重体录入不合规提醒：录入的体积（m³）除以重量（kg）大于0.2或者小于0.0005时，此处有体积单位换算
        if (Boolean.TRUE.equals(weightVolumeContext.getCheckVolume())) {
            if (volume > WeightVolumeRuleConstant.FOAM_WEIGHT_RATIO_B_1 * weight || volume < WeightVolumeRuleConstant.FOAM_WEIGHT_RATIO_B_2 * weight) {
                setNextRowChar(confirmMessage);
                confirmMessage.append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_5);
            }
        }

        // B网包裹维度的称重校验逻辑
        if (WeightVolumeBusinessTypeEnum.BY_PACKAGE.name().equals(weightVolumeContext.getBusinessType())) {
            // 包裹维度-单体积超过27m³禁止揽收；超1000KG禁止称重
            if (volume > WeightVolumeRuleConstant.PACKAGE_MAX_VOLUME_B * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_PACKAGE_MAX_WEIGHT_B, WeightVolumeRuleConstant.PACKAGE_MAX_VOLUME_B));
                result.setData(Boolean.FALSE);
                return;
            }
            if (weight > WeightVolumeRuleConstant.PACKAGE_MAX_WEIGHT_B) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_PACKAGE_MAX_VOLUME_B, WeightVolumeRuleConstant.PACKAGE_MAX_WEIGHT_B));
                result.setData(Boolean.FALSE);
                return;
            }
        }

        if (WeightVolumeBusinessTypeEnum.BY_WAYBILL.name().equals(weightVolumeContext.getBusinessType())) {
            // 强卡控：运单维度-体积超过100m³或者重量超过10000kg
            if (volume > WeightVolumeRuleConstant.WAYBILL_MAX_VOLUME_B * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_WAYBILL_MAX_VOLUME_B, WeightVolumeRuleConstant.WAYBILL_MAX_VOLUME_B));
                result.setData(Boolean.FALSE);
                return;
            }
            if (weight > WeightVolumeRuleConstant.WAYBILL_MAX_WEIGHT_B) {
                result.parameterError(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_WAYBILL_MAX_WEIGHT_B, WeightVolumeRuleConstant.WAYBILL_MAX_WEIGHT_B));
                result.setData(Boolean.FALSE);
                return;
            }
        }
        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    protected boolean commonCheckIntercept(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        // 1、集配、城配不能操作称重（前提：揽收站点操作了发货 或 揽收站点下游站点操作了验货）
        BaseStaffSiteOrgDto operateSite = weightVolumeContext.getOperateSite();
        InvokeResult<Boolean> jpCheckResult = dmsWeightVolumeCheckService.checkJPIsCanWeight(weightVolumeContext.getBarCode(), operateSite.getSiteCode());
        if(!jpCheckResult.codeSuccess()){
            result.customMessage(jpCheckResult.getCode(), jpCheckResult.getMessage());
            return true;
        }
        // 后续公共拦截待补充
        return false;
    }

    /**
     * 计算并保留几位小数
     * @param num 数值
     * @param ratio 比率
     * @param ratio 保留位数
     * @return
     */
    private String keepDigitCompute(int num,int ratio,int digit){
        try {
            String format;
            if(digit == Constants.NUMBER_ZERO){
                return String.valueOf(num / ratio);
            }else if(digit == Constants.CONSTANT_NUMBER_ONE){
                format = "#0.0";
            }else if(digit == Constants.CONSTANT_NUMBER_TWO){
                format = "#0.00";
            }else {
                return Constants.EMPTY_FILL;
            }
            DecimalFormat doubleFormat = new DecimalFormat(format);
            return doubleFormat.format(Double.parseDouble(String.valueOf(num))/ratio);
        }catch (Exception e){
            logger.error("保留小数异常!");
        }
        return Constants.EMPTY_FILL;
    }

    /***
     * 设置换行符
     * @param confirmMessage
     */
    private void setNextRowChar(StringBuilder confirmMessage) {
        if(StringUtils.isEmpty(confirmMessage.toString())){
            return;
        }
        confirmMessage.append(Constants.LINE_NEXT_CHAR);
    }

    /**
     * 设置确认类型结尾提示语
     * @param confirmMessage
     * @param result
     */
    private void setEndConfirmMessage(StringBuilder confirmMessage, InvokeResult<Boolean> result) {
        if(StringUtils.isEmpty(confirmMessage.toString())){
            return;
        }
        confirmMessage.append(Constants.LINE_NEXT_CHAR);
        confirmMessage.append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM);
        result.confirmMessage(confirmMessage.toString());
    }
}
