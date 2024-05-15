package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.EclpCustomerPinRelManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.AbstractHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.logistics.customer.center.domain.CustomerPinRel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: EclpShoppingInterceptHandler
 * @Description: eclp商城打印业务拦截
 * @author: wuyoude
 * @date: 2019年3月20日 下午6:51:57
 */
@Service("eclpShoppingInterceptHandler")
public class EclpShoppingInterceptHandler extends AbstractHandler<WaybillPrintContext, JdResult<String>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EclpCustomerPinRelManager eclpCustomerPinRelManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        // 请求的siteName传递BW码作为商家唯一编码
        String pin = context.getRequest().getSiteName();
        String waybillMemberId = context.getBigWaybillDto().getWaybill().getMemberId();
        // 商家BW码为空或者与运单不一致禁止打印
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
            CustomerPinRel customerPinRel = eclpCustomerPinRelManager.getCustomerPinRelByPin(pin);
            if (customerPinRel != null) {
                return customerPinRel.getDisperseCustomerNo();
            }
        } catch (Exception e) {
            log.error("[B网商家查询接口]根据pin码获取BW编号时发生异常，参数:{}, 异常信息:{}", pin, e.getMessage(), e);
        }
        return null;
    }
}
