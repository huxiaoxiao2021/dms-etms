package com.jd.bluedragon.distribution.weightVolume.check;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣称重校验的参数校验逻辑
 *
 * @author wuzuxiang
 * @since 2020/1/15
 **/
@Service
public class ParameterWeightVolumeChecker implements IWeightVolumeChecker {

    private static final Logger logger = LoggerFactory.getLogger(ParameterWeightVolumeChecker.class);

    public ParameterWeightVolumeChecker() {
        WeightVolumeChecker.register(this);
    }

    @Override
    public InvokeResult<Boolean> checkOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        /* 空对象判断 */
        if (entity == null) {
            result.parameterError("传入数据对象为空");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 单号校验 */
        if (StringHelper.isEmpty(entity.getBarCode()) ||
                (!WaybillUtil.isPackageCode(entity.getBarCode())
                        && !WaybillUtil.isWaybillCode(entity.getBarCode())
                        && !BusinessUtil.isBoxcode(entity.getBarCode()))) {
            result.parameterError("当前操作单号无效【" + String.valueOf(entity.getBarCode()) + "】");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 操作类型校验 */
        if (WeightVolumeBusinessTypeEnum.BY_WAYBILL.equals(entity.getBusinessType())) {
            if (!WaybillUtil.isWaybillCode(entity.getBarCode())) {
                result.parameterError("当前操作单号非运单类型");
                result.setData(Boolean.FALSE);
                return result;
            }
        } else if (WeightVolumeBusinessTypeEnum.BY_PACKAGE.equals(entity.getBusinessType())) {
            if (!WaybillUtil.isPackageCode(entity.getBarCode())) {
                result.parameterError("当前操作单号非包裹类型");
                result.setData(Boolean.FALSE);
                return result;
            }
        } else if (WeightVolumeBusinessTypeEnum.BY_BOX.equals(entity.getBusinessType())) {
            if (!BusinessUtil.isBoxcode(entity.getBarCode())) {
                result.parameterError("当前操作单号非箱号类型");
                result.setData(Boolean.FALSE);
                return result;
            }
        } else if (WeightVolumeBusinessTypeEnum.BY_HANDOVER.equals(entity.getBusinessType())) {
            if (!WaybillUtil.isPackageCode(entity.getBarCode()) && !WaybillUtil.isWaybillCode(entity.getBarCode())) {
                result.parameterError("交接称重只支持包裹和运单号");
                result.setData(Boolean.FALSE);
                return result;
            }
        } else if (null == entity.getBusinessType()){
            result.parameterError("称重量方操作类型为空");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 系统来源 */
        if (null == entity.getSourceCode()) {
            result.parameterError("未识别到系统入口标识");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 用户判断 */
        if (!NumberHelper.gt0(entity.getOperateSiteCode())) {
            result.parameterError("获取用户站点异常【" + entity.getOperateSiteCode() + "】");
            result.setData(Boolean.FALSE);
            return result;
        }
        if (!NumberHelper.gt0(entity.getOperatorId())) {
            result.parameterError("获取用户ID异常【" + entity.getOperatorId() + "】");
            result.setData(Boolean.FALSE);
            return result;
        }
        if (StringHelper.isEmpty(entity.getOperatorCode())) {
            result.parameterError("获取用户ERP异常【" + entity.getOperatorId() + "】");
            result.setData(Boolean.FALSE);
            return result;
        }

        if (!NumberHelper.gt0(entity.getWeight())
                && !NumberHelper.gt0(entity.getHeight())
                && !NumberHelper.gt0(entity.getLength())
                && !NumberHelper.gt0(entity.getWidth())
                && !NumberHelper.gt0(entity.getVolume())) {
            result.parameterError("未上传有效的称重量方数据");
            result.setData(Boolean.FALSE);
            return result;
        }
        result.success();
        result.setData(Boolean.TRUE);
        return result;
    }
}
