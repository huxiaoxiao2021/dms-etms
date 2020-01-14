package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
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
    private WaybillService waybillService;

    public InvokeResult<Boolean> doHandler(WeightVolumeEntity entity) throws RuntimeException {
        /* 目前交接称重只支持运单和包裹的交接称重,不支持按箱的交接称重 */

        /* 1. 交接称重支持运单和包裹（加盟商交接称重） */
        if (WaybillUtil.isWaybillCode(entity.getBarCode()) || WaybillUtil.isPackageCode(entity.getBarCode())) {
            BigWaybillDto waybill = waybillService.getWaybill(WaybillUtil.getWaybillCode(entity.getBarCode()));
            if (waybill == null || waybill.getWaybill() == null) {
                logger.error("获取运单信息失败，无法处理称重交接任务：{}",entity.getBarCode());
                throw new RuntimeException(MessageFormat.format("称重量方处理器失败，获取运单信息失败，单号为：{0}", entity.getBarCode()));
            }
            if (BusinessUtil.isForeignForward(waybill.getWaybill().getWaybillSign())
                    && BusinessUtil.isAllianceBusi(waybill.getWaybill().getWaybillSign())) {
                /* 属于加盟商的交接业务 */
                return handoverWeightVolumeHandler.handlerOperateWeightVolume(entity);
            }
        }

        /* 2. 按包裹称重 */
        if (WaybillUtil.isPackageCode(entity.getPackageCode()) || WaybillUtil.isPackageCode(entity.getPackageCode())) {
            return packageWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        /* 3. 按运单称重（B网） */
        if (WaybillUtil.isWaybillCode(entity.getBarCode()) || WaybillUtil.isWaybillCode(entity.getWaybillCode())) {
            return waybillWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        /* 4. 按箱称重业务 */
        if (BusinessUtil.isBoxcode(entity.getBarCode()) || BusinessUtil.isBoxcode(entity.getBoxCode())) {
            return boxWeightVolumeHandler.handlerOperateWeightVolume(entity);
        }

        logger.error("根据单号无法获取该数据的称重量方处理策略：{}", JsonHelper.toJson(entity));
        throw new RuntimeException(MessageFormat.format("获取该单号的称重量方处理器失败：{0}",JsonHelper.toJson(entity)));
    }

    public InvokeResult doCheck(WeightVolumeEntity entity) {
        InvokeResult result = new InvokeResult<>();
        result.parameterError(InvokeResult.PARAM_ERROR);
        if(entity == null){
            return result;
        }
        String barCode = entity.getBarCode();
        if(WaybillUtil.isWaybillCode(barCode) || WaybillUtil.isPackageCode(barCode)){
            if(isBusinessNet(WaybillUtil.getWaybillCode(barCode))){
                //加盟商交接称重校验
                return handoverWeightVolumeHandler.checkWeightVolume(entity);
            }
        }
        if(BusinessUtil.isBoxcode(barCode)) {
            //按箱称重校验
            return boxWeightVolumeHandler.checkWeightVolume(entity);
        }
        if(WaybillUtil.isWaybillCode(barCode)) {
            //按运单称重校验
            return waybillWeightVolumeHandler.checkWeightVolume(entity);
        }
        if(WaybillUtil.isPackageCode(barCode)) {
            //按包裹称重校验
            return packageWeightVolumeHandler.checkWeightVolume(entity);
        }
        return result;
    }

    /**
     * 是否加盟商运单
     * @param waybillCode 运单号
     * @return
     */
    private Boolean isBusinessNet(String waybillCode){
        String waybillSign = null;
        try {
            BigWaybillDto bigWaybillDto = waybillService.getWaybill(waybillCode);
            if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
                logger.error("获取运单信息失败：{}",waybillCode);
                return Boolean.FALSE;
            }
            waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
        }catch (Exception e){
            logger.error("服务异常!");
        }
        return BusinessUtil.isBusinessNet(waybillSign);
    }
}
