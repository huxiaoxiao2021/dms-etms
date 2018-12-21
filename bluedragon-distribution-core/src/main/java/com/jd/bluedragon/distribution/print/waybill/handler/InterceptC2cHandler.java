package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.Constants.RESULT_SUCCESS;
import static com.jd.bluedragon.Constants.WAYBILLTRACE_STATE;
import static com.jd.bluedragon.distribution.handler.InterceptResult.MESSAGE_NEED_RECEIVE;

/**
 * @ClassName: InterceptC2cHandler
 * @Description: C2C运单打印面单校验揽收完成
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service("interceptC2cHandler")
public class InterceptC2cHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Log logger = LogFactory.getLog(InterceptC2cHandler.class);

    @Autowired
    private WaybillTraceApi waybillTraceApi;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        logger.info("InterceptC2cHandler-C2C运单打印面单校验揽收完成");
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        if (context.getWaybill() != null
                && (WaybillPrintOperateTypeEnum.PLATE_PRINT.getType().equals(context.getRequest().getOperateType()) || WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType().equals(context.getRequest().getOperateType()))
                && BusinessHelper.isC2c(context.getWaybill().getWaybillSign())) {
            BaseEntity<List<PackageState>> baseEntity = waybillTraceApi.getPkStateByWCodeAndState(context.getWaybill().getWaybillCode(), WAYBILLTRACE_STATE);
            //没有揽收完成的全程跟踪  就报错
            if (!(baseEntity != null && baseEntity.getResultCode() == RESULT_SUCCESS && baseEntity.getData() != null && baseEntity.getData().size() > 0)) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, MESSAGE_NEED_RECEIVE);
                return interceptResult;
            }
        }
        return interceptResult;
    }
}
