package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SpringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * <p>
 *      IWeightVolumeHandler处理类的工厂生产方法
 *
 * @author wuzuxiang
 * @since 2020/1/9
 **/
public class WeightVolumeHandlerFactory {

    private static Logger logger = LoggerFactory.getLogger(WeightVolumeHandlerFactory.class);

    /**
     * 根据单号判断称重量方处理接口，以及是否交接的处理逻辑
     * @param barCode 单号
     * @param handoverFlag 是否交接称重
     * @return
     */
    public static IWeightVolumeHandler getHandler(String barCode, Boolean handoverFlag) {
        /* 目前交接称重只支持运单和包裹的交接称重,不支持按箱的交接称重 */
        if (Boolean.TRUE.equals(handoverFlag) && (WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode))) {
            return (HandoverWeightVolumeHandler) SpringHelper.getBean("handoverWeightVolumeHandler");
        }
        if (WaybillUtil.isWaybillCode(barCode)) {
            return (WaybillWeightVolumeHandler) SpringHelper.getBean("waybillWeightVolumeHandler");
        }
        if (WaybillUtil.isPackageCode(barCode)) {
            return (PackageWeightVolumeHandler) SpringHelper.getBean("packageWeightVolumeHandler");
        }
        if (BusinessUtil.isBoxcode(barCode)) {
            return (BoxWeightVolumeHandler) SpringHelper.getBean("boxWeightVolumeHandler");
        }
        logger.error("根据单号无法获取该单号【{}】的称重量方处理逻辑,是否交接称重：{}",barCode,handoverFlag);
        throw new RuntimeException(MessageFormat.format("获取该单号的称重量方处理器失败，单号为：{0},是否交接称重：{1}",barCode, handoverFlag));
    }
}
