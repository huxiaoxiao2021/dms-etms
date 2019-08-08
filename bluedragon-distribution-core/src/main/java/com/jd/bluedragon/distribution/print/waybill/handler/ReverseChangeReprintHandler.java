package com.jd.bluedragon.distribution.print.waybill.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.dms.utils.WaybillUtil;

/**
 * 
 * @ClassName: ReverseChangeReprintHandler
 * @Description: 换单补打,通过旧单号进行打印
 * @author: wuyoude
 * @date: 2019年6月26日 下午12:47:33
 *
 */
@Service("reverseChangeReprintHandler")
public class ReverseChangeReprintHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseChangeReprintHandler.class);

    @Autowired
    private ReversePrintService reversePrintService;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

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
        }
        /* 将新单号替换为请求 */
        newWaybillCode = newWaybillResult.getData().getNewWaybillCode();
        context.getRequest().setBarCode(newWaybillCode);
        context.getRequest().setPackageIndex(WaybillUtil.getPackIndexByPackCode(oldBarCode));//设置包裹的index
        return result;
    }
}
