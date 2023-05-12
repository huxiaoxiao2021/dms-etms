package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/13
 **/
@Service
public class WeightVolumeHandlerStrategy {

    private static Logger logger = LoggerFactory.getLogger(WeightVolumeHandlerStrategy.class);

    @Autowired
    @Qualifier("boxWeightVolumeHandler")
    private IWeightVolumeHandler boxWeightVolumeHandler;

    @Autowired
    @Qualifier("packageWeightVolumeHandler")
    private IWeightVolumeHandler packageWeightVolumeHandler;

    @Autowired
    @Qualifier("waybillWeightVolumeHandler")
    private IWeightVolumeHandler waybillWeightVolumeHandler;

    @Autowired
    @Qualifier("handoverWeightVolumeHandler")
    private IWeightVolumeHandler handoverWeightVolumeHandler;

    @Autowired
    @Qualifier("splitWaybillWeightVolumeHandler")
    private IWeightVolumeHandler splitWaybillWeightVolumeHandler;

    @Autowired
    private WaybillService waybillService;

    public InvokeResult<Boolean> doHandler(WeightVolumeEntity entity) throws RuntimeException {
        /* 目前交接称重只支持运单和包裹的交接称重,不支持按箱的交接称重 */

        /* 1. 交接称重支持运单和包裹（加盟商交接称重） */
        if (WaybillUtil.isWaybillCode(entity.getBarCode()) || WaybillUtil.isPackageCode(entity.getBarCode())) {
            BigWaybillDto waybill = waybillService.getWaybill(WaybillUtil.getWaybillCode(entity.getBarCode()));
            if (waybill == null || waybill.getWaybill() == null) {
                logger.error("获取运单信息失败，无法处理称重交接任务：{}",entity.getBarCode());
                return new InvokeResult<>(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "无运单信息!", false);
            }
            if (BusinessUtil.isForeignForward(waybill.getWaybill().getWaybillSign())
                    && BusinessUtil.isAllianceBusi(waybill.getWaybill().getWaybillSign())) {
                /* 属于加盟商的交接业务 */
                return handoverWeightVolumeHandler.handlerOperateWeightVolume(entity);
            }
        }

        /* 2. 按箱称重的内部拆分处理逻辑 */
        if (FromSourceEnum.DMS_INNER_SPLIT.equals(entity.getSourceCode())) {
            return splitWaybillWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        /* 3. 按包裹称重 */
        if (WaybillUtil.isPackageCode(entity.getBarCode())) {
            return packageWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        /* 4. 按运单称重（B网） */
        if (WaybillUtil.isWaybillCode(entity.getBarCode())) {
            return waybillWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        /* 5. 按箱称重业务 */
        if (BusinessUtil.isBoxcode(entity.getBarCode())) {
            return boxWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        logger.error("根据单号无法获取该数据的称重量方处理策略：{}", JsonHelper.toJson(entity));
        throw new RuntimeException(MessageFormat.format("获取该单号的称重量方处理器失败：{0}",JsonHelper.toJson(entity)));
    }

    public InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(condition == null){
            result.parameterError("查询条件不能为空!");
            return result;
        }
        // 操作类型
        String businessType = condition.getBusinessType();
        /* 1. 按包裹称重 */
        if (Objects.equals(WeightVolumeBusinessTypeEnum.BY_PACKAGE.name(),businessType)) {
            return packageWeightVolumeHandler.weightVolumeRuleCheck(condition);
        }
        /* 2. 按运单称重  */
        if (Objects.equals(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name(),businessType)) {
            return waybillWeightVolumeHandler.weightVolumeRuleCheck(condition);
        }
        /* 3. 按箱称重业务 */
        if (Objects.equals(WeightVolumeBusinessTypeEnum.BY_BOX.name(),businessType)) {
            return boxWeightVolumeHandler.weightVolumeRuleCheck(condition);
        }
        result.parameterError("未知场景不予处理!");
        return result;
    }
}
