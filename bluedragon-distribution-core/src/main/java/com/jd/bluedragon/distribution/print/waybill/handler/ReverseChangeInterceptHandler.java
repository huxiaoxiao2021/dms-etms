package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
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
        String barCode = context.getRequest().getBarCode();/* 获取输入单号 */
        String oldWaybillCode = WaybillUtil.getWaybillCode(barCode);/* 获取运单号 */
        String newWaybillCode = "";/* 新运单号 */
        if (!WaybillUtil.isWaybillCode(oldWaybillCode)) {
            LOGGER.error("ReverseChangeInterceptHandler.handle-->单号输入不正确{}",barCode);
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
                LOGGER.error("ReverseChangeInterceptHandler.handle-->该单号{}无运单信息", barCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO);
                return result;
            }
            Integer waybillStatus = invokeResult.getData().getWaybillStatus();/* 获取旧单号的运单状态 */
            if (waybillStatus != null && waybillStatus == 600) {
                isHalfPackage = Boolean.TRUE;
            }
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
            result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS,
                    JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS);
            return result;
        }

        /* 将新单号替换为请求 */
        newWaybillCode = newWaybillResult.getData().getNewWaybillCode();
        context.getRequest().setBarCode(newWaybillCode);
        context.getRequest().setPackageIndex(WaybillUtil.getPackIndexByPackCode(barCode));//设置包裹的index

        WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();
        /* 非签单返还 符合包裹半收，则必须进行称重和量方 */
        if (!WaybillUtil.isReturnCode(newWaybillCode) && isHalfPackage) {
            if (weightOperFlow.getHigh() <= 0 || weightOperFlow.getWidth() <= 0 || weightOperFlow.getLength() <= 0) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->体积录入异常！此包裹{}为半收包裹，长宽高必须输入！",
                        oldWaybillCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_VOLUME,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_VOLUME);
                return result;
            } else if (weightOperFlow.getWeight() <= 0) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->重量录入异常！此包裹{}为半收包裹，重量必须录入！",
                        oldWaybillCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_WEIGHT,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_WEIGHT);
                return result;
            }
        }

        /*
            校验是否必须进行称重量方 获取是够进行必须称重量方的结果，目前只判断取件单和自营拒收单
            0表示不需要称重，不需要量方
            1表示不需要称重，需要量方
            2表示需要称重，不需要量方
            3表示需要称重，需要量方
         */
        InvokeResult<Integer> weightAndVolumeCheck = receiveWeightCheckService
                .waybillExchangeCheckWeightAndVolume(oldWaybillCode,newWaybillCode);
        if (null == weightAndVolumeCheck || weightAndVolumeCheck.getCode() != JdResponse.CODE_OK
                || null == weightAndVolumeCheck.getData()) {
            LOGGER.warn("ReverseChangeInterceptHandler.handle-->校验是否需要进行称重量方失败，旧单{},新单{}",
                    oldWaybillCode,newWaybillCode);
            return result;
        } else if (weightAndVolumeCheck.getData() == 1) {
            if (null == weightOperFlow || weightOperFlow.getLength() <= 0
                    || weightOperFlow.getWidth() <= 0 || weightOperFlow.getHigh() <= 0) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->旧单号{}操作换单打印必须进行量方",oldWaybillCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME);
                return result;
            }
        } else if (weightAndVolumeCheck.getData() == 2) {
            if (null == weightOperFlow || weightOperFlow.getWeight() <= 0) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->旧单号{}操作换单打印必须进行称重",oldWaybillCode);
                result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_WEIGHT,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_WEIGHT);
                return result;
            }
        } else if (weightAndVolumeCheck.getData() == 3) {
            if (null == weightOperFlow || weightOperFlow.getWeight() <= 0 || weightOperFlow.getLength() <= 0
                    || weightOperFlow.getWidth() <= 0 || weightOperFlow.getHigh() <= 0) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->旧单号{}操作换单打印必须进行称重量方",oldWaybillCode);
                result.toError(JdResponse.
                        CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME_WEIGHT,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME_WEIGHT);
                return result;
            }
        }

        /* 当客户端进行称重量方时，对称重量方的数据进行判断  0/null不启用 1启用称重 2启用量方 3启用称重量方 */
        if (context.getRequest().getWeightVolumeOperEnable() == null) {
            return result;
        }

        /* 启用称重 01 */
        if ((Constants.WEIGHT_ENABLE & context.getRequest().getWeightVolumeOperEnable()) == Constants.WEIGHT_ENABLE) {
            if (null == weightOperFlow || weightOperFlow.getWeight() <= 0.00 ) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->{}启用包裹称重，未回传重量信息",oldWaybillCode);
                result.toError(JdResponse.
                                CODE_REVERSE_CHANGE_PRINT_NO_WEIGHT,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_NO_WEIGHT);
                return result;
            } else if (weightOperFlow.getWeight() >= Constants.ILLEGAL_WEIGHT_1000) {
                LOGGER.error("ReverseChangeInterceptHandler.handle-->{}启用包裹称重，重量超过1000KG！",oldWaybillCode);
                result.toError(JdResponse.
                                CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_1000,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_1000);
                return result;
            } else if (weightOperFlow.getWeight() >= Constants.CONFIRM_WEIGHT_100) {
                LOGGER.warn("ReverseChangeInterceptHandler.handle-->{}启用包裹称重，重量超过100KG！",oldWaybillCode);
                result.toWeakSuccess(JdResponse.
                                CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_100,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_100);
                return result;
            } else if (weightOperFlow.getWeight() >= Constants.CONFIRM_WEIGHT_25) {
                LOGGER.debug("ReverseChangeInterceptHandler.handle-->{}启用包裹称重，重量超过25KG！",oldWaybillCode);
                result.toWeakSuccess(JdResponse.
                                CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT,
                        JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT);
                return result;
            }
        }

        return result;
    }
}
