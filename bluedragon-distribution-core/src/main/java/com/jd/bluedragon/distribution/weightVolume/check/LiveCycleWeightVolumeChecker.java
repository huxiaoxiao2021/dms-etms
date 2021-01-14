package com.jd.bluedragon.distribution.weightVolume.check;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.base.ZhongyouexQueryManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.box.service.DmsBoxQueryService;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BoxHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/16
 **/
@Service
public class LiveCycleWeightVolumeChecker implements IWeightVolumeChecker {

    private static final Logger logger = LoggerFactory.getLogger(LiveCycleWeightVolumeChecker.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WeighByWaybillService weighByWaybillService;

    @Autowired
    private ZhongyouexQueryManager zhongyouexQueryManager;

    @Autowired
    private DmsBoxQueryService dmsBoxQueryService;


    public LiveCycleWeightVolumeChecker() {
        WeightVolumeChecker.register(this);
    }

    @Override
    public InvokeResult<Boolean> checkOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        //按运单称重
        if (WeightVolumeBusinessTypeEnum.BY_WAYBILL.equals(entity.getBusinessType())) {
            return this.checkOperateWeightVolumeByWaybill(entity);
        }
        //按箱称重校验
        if (WeightVolumeBusinessTypeEnum.BY_BOX.equals(entity.getBusinessType())) {
            return this.checkOperateWeightVolumeByBox(entity);
        }
        //其他
        result.setData(Boolean.TRUE);
        result.success();
        return result;
    }

    /**
     * 按运单校验
     * @param entity
     * @return
     */
    private InvokeResult<Boolean> checkOperateWeightVolumeByWaybill(WeightVolumeEntity entity){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();
        BaseEntity<Waybill> baseWaybillEntity = waybillQueryManager.getWaybillByWaybillCode
                (WaybillUtil.getWaybillCode(entity.getBarCode()));
        if (null ==baseWaybillEntity || null == baseWaybillEntity.getData()) {
            result.error("当前运单信息不存在");
            result.setData(Boolean.FALSE);
            return result;
        }
        Waybill waybill = baseWaybillEntity.getData();
        //经济网的逻辑
        if (BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
            /* 经济网的操作需要回传长宽高 */
            if ((!NumberHelper.gt0(entity.getLength()) || !NumberHelper.gt0(entity.getWidth()) || !NumberHelper.gt0(entity.getHeight()))
                    && NumberHelper.gt0(entity.getVolume())) {
                result.parameterError("众邮运单必须录入长宽高");
                result.setData(Boolean.FALSE);
                return result;
            }
        }
        /* 是否需要称重校验 */
        if (BusinessUtil.isNoNeedWeight(waybill.getWaybillSign())) {
            result.error(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightExceptionMessage);
            result.setData(Boolean.FALSE);
            return result;
        }
        /* 校验是否已经妥投 */
        if(waybillTraceManager.isWaybillFinished(waybill.getWaybillCode())){
            result.error(WeightByWaybillExceptionTypeEnum.WaybillFinishedExceptionMessage);
            result.setData(Boolean.FALSE);
            return result;
        }
        /* 判断是否转网 */
        WaybillWeightVO weightVO = new WaybillWeightVO();
        weightVO.setOperatorSiteCode(entity.getOperateSiteCode());
        weightVO.setOperatorSiteName(entity.getOperateSiteName());
        weightVO.setOperatorId(entity.getOperatorId());
        weightVO.setOperatorName(entity.getOperatorName());
        weightVO.setWeight(entity.getWeight());
        /* 涉及到单位转换 */
        Double volume = entity.getVolume() != null? entity.getVolume() / 1000000 :
                ((entity.getHeight() != null && entity.getLength() != null && entity.getWidth() != null)?
                        (entity.getHeight() * entity.getWidth() * entity.getLength())/ 1000000 : 0);
        weightVO.setVolume(volume);
        weightVO.setOperateTimeMillis(entity.getOperateTime().getTime());
        weightVO.setCodeStr(entity.getWaybillCode());
        weightVO.setStatus(10);
        if (weighByWaybillService.waybillTransferB2C(weightVO)) {
            result.error(MessageFormat.format(InvokeResult.RESULT_INTERCEPT_MESSAGE, WaybillUtil.getWaybillCode(waybill.getWaybillCode())));
            result.setData(Boolean.FALSE);
            return result;
        }
        return result;
    }


    /**
     * 按箱校验
     * 规则1：经济网 集包称重时，判断是否有运单明细，若无运单明细
     * @param entity
     * @return
     */
    private InvokeResult<Boolean> checkOperateWeightVolumeByBox(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        result.success();
        /*-------------------------------------逻辑校验--------------------------------------*/
        //经济网的逻辑
        Response<Boolean> isEconomicNetBoxAndNotBoundingPackage = dmsBoxQueryService.isEconomicNetBoxAndNotBoundingPackage(entity.getBarCode());
        if (null == isEconomicNetBoxAndNotBoundingPackage || Response.CODE_SUCCESS != isEconomicNetBoxAndNotBoundingPackage.getCode()){
            result.error(MessageFormat.format(InvokeResult.RESULT_NO_BOX_MESSAGE, entity.getBarCode()));
            result.setData(Boolean.FALSE);
            return result;
        }
        if (isEconomicNetBoxAndNotBoundingPackage.getData()) {
            //规则1
            boolean isEmpty = zhongyouexQueryManager.findBoxIsEmpty(entity.getBarCode());
            if (isEmpty){
                result.customMessage(InvokeResult.RESULT_BOX_EMPTY_CODE,InvokeResult.RESULT_BOX_EMPTY_MESSAGE);
                result.setData(Boolean.FALSE);
                return result;
            }
        }
        return result;
    }
}
