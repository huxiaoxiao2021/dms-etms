package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.SpmBWCompanyManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.spm.bwcompany.response.BwCompanyInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: EclpShoppingInterceptHandler
 * @Description: eclp商城打印业务拦截
 * @author: wuyoude
 * @date: 2019年3月20日 下午6:51:57
 */
@Service("eclpShoppingInterceptHandler")
public class EclpShoppingInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Log logger = LogFactory.getLog(EclpShoppingInterceptHandler.class);

    @Autowired
    private SpmBWCompanyManager spmBWCompanyManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.info("eclpShoppingInterceptHandler-eclp商城打印业务拦截");
        InterceptResult<String> interceptResult = context.getResult();
        // 请求的siteName传递BW码作为商家唯一编码
        String pin = context.getRequest().getSiteName();
        String waybillMemberId = context.getBigWaybillDto().getWaybill().getMemberId();
        //商家BW码为空或者与运单不一致禁止打印
        if (StringHelper.isEmpty(pin)) {
            interceptResult.toFail("传入参数中用户pin码标识不能为空！");
            return interceptResult;
        }

        // 逻辑1，根据pin码直接对比
        if (pin.equals(waybillMemberId)) {
            // 允许打印
            return interceptResult;
        }

        // 逻辑2，根据pin码后去bw编码
        String bwCode = this.getBWCodeByPin(pin);
        if (StringUtils.isNotBlank(bwCode) && bwCode.equals(waybillMemberId)) {
            return interceptResult;
        }

        // 逻辑3，判断青龙业主号请求内容中的青龙业主号
        String customerCode = context.getRequest().getCustomerCode();
        if (StringUtils.isBlank(customerCode)) {
            interceptResult.toFail("请求内容中的青龙业主号为空，禁止打印！");
            return interceptResult;
        }

        String waybillCustomerCode = context.getBigWaybillDto().getWaybill().getCustomerCode();
        if (customerCode.equals(waybillCustomerCode)) {
            return interceptResult;
        } else {
            interceptResult.toFail("根据Pin码和青龙业主号无法与运单中的信息匹配，禁止打印！");
            return interceptResult;
        }
    }

    /**
     * 根据pin码获取BW编号
     *
     * @param pin
     * @return
     */
    private String getBWCodeByPin(String pin) {
        try {
            List<BwCompanyInfo> bwCompanyInfoList = spmBWCompanyManager.getCompanyListByPin(pin);
            if (bwCompanyInfoList != null && bwCompanyInfoList.size() > 0) {
                return bwCompanyInfoList.get(0).getCompanyCode();
            }
        } catch (Exception e) {
            logger.error("[B网商家查询接口]根据pin码获取BW编号时发生异常", e);
        }
        return null;
    }
}
