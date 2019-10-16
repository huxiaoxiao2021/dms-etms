package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     拼多多收寄件人的姓名和联系方式的处理，需要从拼多多的外部接口中获取，如果获取失败，则用原字符（来自运单加密字符）
 *
 * @author wuzuxiang
 * @since 2019/10/16
 **/
@Service("pddCustomerAndConsigneeInfoHandler")
public class PDDCustomerAndConsigneeInfoHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger logger = LoggerFactory.getLogger(PDDCustomerAndConsigneeInfoHandler.class);

    @Autowired
    private PDDService pddService;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            logger.debug("该单不是拼多多单，不需要从拼多多接口中获取收寄件人信息：{}", waybillCode);
            return context.getResult();
        }
        PDDWaybillDetailDto pddWaybillDetailDto = pddService.queryWaybillDetailByWaybillCode(waybillCode);
        if (pddWaybillDetailDto == null) {
            logger.warn("拼多多订单信息获取失败:{}",waybillCode);
            return context.getResult();
        }
        context.getBasePrintWaybill().setConsigner(pddWaybillDetailDto.getSenderName());
        context.getBasePrintWaybill().setConsignerMobile(pddWaybillDetailDto.getSenderMobile());
        context.getBasePrintWaybill().setConsignerTel(pddWaybillDetailDto.getSenderPhone());
        context.getBasePrintWaybill().setCustomerName(pddWaybillDetailDto.getConsigneeName());
        context.getBasePrintWaybill().setCustomerContacts(pddWaybillDetailDto.getConsigneeMobile());
        context.getBasePrintWaybill().setCustomerPhoneText(pddWaybillDetailDto.getConsigneePhone());

        return context.getResult();
    }
}
