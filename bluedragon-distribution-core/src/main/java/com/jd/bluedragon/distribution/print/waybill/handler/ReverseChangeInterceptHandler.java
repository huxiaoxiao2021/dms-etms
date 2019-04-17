package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <P>
 *     逆向换单打印时如果是非取件单的话， 添加包裹半收必须称重量方 和 对重量量方数据的校验
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/6
 */
@Service("reverseChangeInterceptHandler")
public class ReverseChangeInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseChangeInterceptHandler.class);

    @Autowired
    private WaybillPrintService waybillPrintService;

    @Autowired
    private ReversePrintService reversePrintService;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        String oldBarCode = context.getRequest().getOldBarCode();/* 获取输入旧单号 */
        String oldWaybillCode = WaybillUtil.getWaybillCode(oldBarCode);/* 获取旧运单号 */
        String newWaybillCode = "";/* 新运单号 */
        if (!WaybillUtil.isWaybillCode(oldWaybillCode) && !WaybillUtil.isPickupCode(oldWaybillCode)) {
            LOGGER.error("ReverseChangeInterceptHandler.handle-->单号输入不正确{}",oldBarCode);
            result.toError(JdResponse.CODE_PARAM_ERROR,JdResponse.MESSAGE_PACKAGE_ERROR);
            return result;
        }
        /* 是否包裹半收 */
        boolean isHalfPackage = false;

        /* 获取旧单号的数据 */
        if (!WaybillUtil.isPickupCode(oldWaybillCode)) {
            /* 不是取件单的话，需要判断包裹半收的标识 */
            InvokeResult<WaybillPrintResponse> invokeResult =  waybillPrintService
                    .getPrintWaybill(context.getRequest().getDmsSiteCode(),oldWaybillCode,0);
            if (null == invokeResult || invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                    || null == invokeResult.getData()) {
                LOGGER.error("ReverseChangeInterceptHandler.handle-->该单号{}无运单信息", oldBarCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO);
                return result;
            }
            Integer waybillStatus = invokeResult.getData().getWaybillStatus();/* 获取旧单号的运单状态 */
            isHalfPackage = (waybillStatus != null && waybillStatus == 600);
        }

        /* 获取新单的信息 */
        InvokeResult<RepeatPrint> newWaybillResult = reversePrintService.getNewWaybillCode1(oldWaybillCode, true);
        if (null == newWaybillResult ||
                newWaybillResult.getCode() != JdResponse.CODE_OK || null == newWaybillResult.getData()) {
            LOGGER.warn("ReverseChangeInterceptHandler.handle-->获取新单的信息失败{}",oldWaybillCode);
            result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_INFO,
                    JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_INFO);
            return result;
        } else if (!WaybillUtil.isWaybillCode(newWaybillResult.getData().getNewWaybillCode())){
            LOGGER.warn("ReverseChangeInterceptHandler.handle-->未获取到新单号{}",oldWaybillCode);
            result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_WAYBILLCODE,
                    JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_WAYBILLCODE);
            return result;
        } else if (newWaybillResult.getData().getOverTime()) {
            LOGGER.warn("ReverseChangeInterceptHandler.handle-->单号已经超过十五天{}",oldWaybillCode);
            result.toWeakSuccess(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS,
                    JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS);
            return result;
        }

        /* 将新单号替换为请求 */
        newWaybillCode = newWaybillResult.getData().getNewWaybillCode();
        context.getRequest().setBarCode(newWaybillCode);
        context.getRequest().setPackageIndex(WaybillUtil.getPackIndexByPackCode(oldBarCode));//设置包裹的index

        WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();
        int weightVolumeOperEnable = context.getRequest().getWeightVolumeOperEnable() == null ?
                0 : context.getRequest().getWeightVolumeOperEnable();

        /* 校验重量和量方 */
        result = receiveWeightCheckService.reverseChangePrintCheckWeightAndVolume(oldWaybillCode,newWaybillCode,
                weightVolumeOperEnable, weightOperFlow,isHalfPackage);
        return result;
    }
}
