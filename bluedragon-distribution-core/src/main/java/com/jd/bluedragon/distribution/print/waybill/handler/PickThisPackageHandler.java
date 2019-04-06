package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 *     如果用户用的是包裹号进行打印则将运单的包裹列表过滤为当前的包裹信息
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */
@Service("pickThisPackageHandler")
public class PickThisPackageHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickThisPackageHandler.class);

    /* 最大打印包裹数 */
    private static final int MAX_PRINT_SIZE = 100;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();

        String barCode = context.getRequest().getBarCode();
        if (WaybillUtil.isWaybillCode(barCode)) {
            return result;
        }

        /* 获取打印结果 */
        WaybillPrintResponse waybillPrintResponse = context.getResponse();
        if (null == waybillPrintResponse) {
            result.toError(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
            return result;
        }

        /* 获取包裹列表 */
        List<PrintPackage> packages =  waybillPrintResponse.getPackList();
        if (null == packages || packages.isEmpty()) {
            LOGGER.warn("handler.PrintCheckInterceptHandler-->运单{}没有包裹信息，无法打印包裹标签",
                    waybillPrintResponse.getWaybillCode());
            result.toError(JdResponse.CODE_RE_PRINT_NO_PACK_LIST,MessageFormat
                    .format(JdResponse.MESSAGE_RE_PRINT_NO_PACK_LIST,waybillPrintResponse.getWaybillCode()));
            return result;
        }

        if (WaybillUtil.isPackageCode(barCode)) {
            List<PrintPackage> newPack = new ArrayList<>(1);
            for (PrintPackage printPackage : packages) {
                if (barCode.equals(printPackage.getPackageCode())) {
                    newPack.add(printPackage);
                }
            }
            /* 将当前打印的包裹过滤出来重新赋值 */
            waybillPrintResponse.setPackList(newPack);

            if (waybillPrintResponse.getPackList().isEmpty()) {
                LOGGER.warn("handler.PrintCheckInterceptHandler-->运单{}中不存在该包裹{}！",
                        waybillPrintResponse.getWaybillCode(),barCode);
                result.toError(JdResponse.CODE_RE_PRINT_NO_THIS_PACK, MessageFormat
                        .format(JdResponse.MESSAGE_RE_PRINT_NO_THIS_PACK,waybillPrintResponse.getWaybillCode()));
                return result;
            }
        }

        if (waybillPrintResponse.getPackList().size() > MAX_PRINT_SIZE) {
            LOGGER.warn("handler.PrintCheckInterceptHandler-->该单{}包裹数为{}，确定打印所有包裹",
                    waybillPrintResponse.getWaybillCode(),waybillPrintResponse.getPackList().size());
            result.toWeakSuccess(JdResponse.CODE_RE_PRINT_PACK_SIZE_TOO_LARGE, MessageFormat
                    .format(JdResponse.MESSAGE_RE_PRINT_PACK_SIZE_TOO_LARGE,waybillPrintResponse.getPackList().size()));
            return context.getResult();
        }
        return result;
    }
}
