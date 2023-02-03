package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyh
 * @className InitialPrintCompleteHandler
 * @description
 * @date 2021/12/2 16:15
 **/
@Service("initialPrintCompleteHandler")
public class InitialPrintCompleteHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(InitialPrintCompleteHandler.class);

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {
        InterceptResult<Boolean> result = context.getResult();

        PrintCompleteRequest request = context.getRequest();

        // 设置运单数据
        if(StringUtils.isEmpty(request.getWaybillSign())){ // 当前未设置运单信息
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(request.getWaybillCode());
            if (waybill == null) {
                logger.warn("根据运单号{}未获取到运单信息!", request.getWaybillCode());
                result.toError(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
                return result;
            }
            request.setWaybillSign(waybill.getWaybillSign());
        }

        boolean printByWaybill = WaybillUtil.isWaybillCode(request.getPackageBarcode());

        // 按运单打印，保存运单所有包裹的打印记录
        if (printByWaybill) {
            String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarcode());
            BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(waybillCode);
            if (null == baseEntity){
                result.toError(InterceptResult.CODE_ERROR, "运单数据为空！");
                return result;
            }
            if (EnumBusiCode.BUSI_SUCCESS.getCode() != baseEntity.getResultCode()){
                result.toError(InterceptResult.CODE_ERROR, baseEntity.getMessage());
                return result;
            }
            // 运单数据为空，直接返回运单数据为空异常
            if (null == baseEntity.getData() || CollectionUtils.isEmpty(baseEntity.getData())) {
                result.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(),
                        WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
                logger.warn("调用运单接口获取运单数据为空，waybillCode：{}", waybillCode);
                return result;
            }

            for (DeliveryPackageD deliveryPackageD : baseEntity.getData()) {
                context.addToDealPackageCodes(deliveryPackageD.getPackageBarcode());
            }
        }
        else {
            context.addToDealPackageCodes(request.getPackageBarcode());
        }

        return result;
    }
}
