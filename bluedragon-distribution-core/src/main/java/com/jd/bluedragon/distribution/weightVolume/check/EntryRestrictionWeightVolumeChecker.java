package com.jd.bluedragon.distribution.weightVolume.check;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     入口限制校验（更正时间：2020-1-15）
 *     1. 按箱称重量方只能在新的入口中操作对应的sourceCode为：DMS_CLIENT_WEIGHT_VOLUME
 *     2. B网称重页面只能支持按运单操作
 *     3. 交接称重（目前有加盟商交接时间截止到2020-1-15）只能在站点平台打印和平台打印中操作
 *     4. 平台打印新增按箱称重量方
 *
 * @author wuzuxiang
 * @since 2020/1/15
 **/
@Service
public class EntryRestrictionWeightVolumeChecker implements IWeightVolumeChecker {

    private static final Logger logger = LoggerFactory.getLogger(EntryRestrictionWeightVolumeChecker.class);

    public EntryRestrictionWeightVolumeChecker() {
        WeightVolumeChecker.register(this);
    }

    @Override
    public InvokeResult<Boolean> checkOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        /* 按箱称重的目前只能在经济网项目新开的那个客户端页面中称重 */
        if (BusinessUtil.isBoxcode(entity.getBarCode())
                && !FromSourceEnum.DMS_CLIENT_WEIGHT_VOLUME.equals(entity.getSourceCode())
                && !FromSourceEnum.DMS_CLIENT_PLATE_PRINT.equals(entity.getSourceCode())){
            result.error("当前页面不允许操作按箱称重");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* B网快运称重只能操作运单称重 */
        if (FromSourceEnum.DMS_WEB_FAST_TRANSPORT.equals(entity.getSourceCode()) && !WaybillUtil.isWaybillCode(entity.getBarCode())) {
            result.error("快运称重页面只允许操作运单称重");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 换单打印只能操作包裹称重 */
        if (FromSourceEnum.DMS_CLIENT_SWITCH_BILL_PRINT.equals(entity.getSourceCode()) && !WaybillUtil.isPackageCode(entity.getBarCode())) {
            result.error("当前页面只允许操作包裹称重");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 交接称重（目前是加盟商交接）只能在平台打印和站点平台打印中操作，因为这两个页面的称重数据是校验的 */
        if (WeightVolumeBusinessTypeEnum.BY_HANDOVER.equals(entity.getBusinessType()) &&
                !(FromSourceEnum.DMS_CLIENT_PLATE_PRINT.equals(entity.getSourceCode())
                        || FromSourceEnum.DMS_CLIENT_SITE_PLATE_PRINT.equals(entity.getSourceCode()))) {
            result.error("交接称重只能在站点平台打印和平台打印中操作");
            result.setData(Boolean.FALSE);
            return result;
        }

        /* 后续待补充... */

        result.success();
        result.setData(Boolean.TRUE);
        return result;
    }
}
