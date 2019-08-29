package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <P>
 *     校验包裹补打是否在1小时内重复打印
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */
@Service("duplicatePrintInterceptHandler")
public class DuplicatePrintInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicatePrintInterceptHandler.class);

    @Autowired
    private ReprintRecordService reprintRecordService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        String barCode = context.getRequest().getBarCode();

        if (StringHelper.isNotEmpty(barCode) && reprintRecordService.isBarCodeRePrinted(barCode)) {
            LOGGER.warn("DuplicatePrintInterceptHandler.handler-->{}该单号重复打印",barCode);
            result.toWeakSuccess(JdResponse.CODE_RE_PRINT_REPEAT, JdResponse.MESSAGE_RE_PRINT_REPEAT);
        }
        return result;
    }
}
