package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <P>
 *     将C#客户端的domain字段的重新映射逻辑移植到这个接口中
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/9
 */
@Service("mappedBasicPrintWaybillHandler")
public class MappedBasicPrintWaybillHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MappedBasicPrintWaybillHandler.class);

    private static final String BAD_WAREHOURSE_FOR_PORT = "保税";

    private static final String PICKUP_CUSTOMER_COMMET = "服务单号：";

    private static final String BUSINESS_ORDER_CODE_REMARK = "商家订单号：";

    private static final String PREFIX_T = "T";

    private static final String SPLIT = "；";

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        WaybillPrintResponse printWaybill = context.getResponse();
        if (null == printWaybill) {
            return context.getResult();
        }

        /* 处理dmsName */
        if (StringHelper.isNotEmpty(printWaybill.getOriginalDmsName())) {
            String originalDmsName = printWaybill.getOriginalDmsName().replace("分拣中心","")
                                                                      .replace("中转站","")
                                                                      .replace("分拨中心","");
            printWaybill.setOriginalDmsName(originalDmsName);
        }
        if (StringHelper.isNotEmpty(printWaybill.getDestinationDmsName())) {
            String destinationDmsName = printWaybill.getDestinationDmsName().replace("分拣中心","")
                    .replace("中转站","")
                    .replace("分拨中心","");
            printWaybill.setDestinationDmsName(destinationDmsName);
        }

        /* 处理代收金额 */
        String receivable = printWaybill.getReceivable();
        if (StringHelper.isEmpty(receivable)) {
            receivable = "￥0.00";
        } else if (NumberHelper.isNumber(printWaybill.getReceivable())) {
            receivable = "￥" + receivable;
        }
        printWaybill.setReceivable(receivable);

        /*   remark
             增加判断，如果remark字段已经打了  就不在打印服务单号，  客户端自己解决重复服务单号问题
             VY中remark会存在服务单号，其他类型还要打印 ServiceCode，
             没人知道需求，不敢贸然修改代码，目前做硬编码判断避免重复
         */
        String remark = "";
        if (printWaybill.getWaybillCode().startsWith(PREFIX_T) && StringHelper.isNotEmpty(printWaybill.getSendPay()) &&
            printWaybill.getSendPay().length() >= 8 && printWaybill.getSendPay().charAt(7) == '6' ) {
            remark += BAD_WAREHOURSE_FOR_PORT;
        } else {
            remark += printWaybill.getRemark();
        }
        if (StringHelper.isNotEmpty(printWaybill.getServiceCode())) {
            if (!remark.contains(printWaybill.getServiceCode())) {
                if (remark.length() > 0) {
                    remark += SPLIT;
                }
                remark += PICKUP_CUSTOMER_COMMET;
                remark += printWaybill.getServiceCode();
            }

        }
        if (StringHelper.isNotEmpty(printWaybill.getBusiOrderCode())) {
            if (!remark.contains(printWaybill.getBusiOrderCode())) {
                if (remark.length() > 0) {
                    remark += SPLIT;
                }
                remark += BUSINESS_ORDER_CODE_REMARK;
                remark += printWaybill.getBusiOrderCode();
            }
        }
        printWaybill.setRemark(remark);

        /* 寄件人电话 */
        String consignerTelText = "";
        if (StringHelper.isNotEmpty(printWaybill.getConsignerTel())) {
            consignerTelText += printWaybill.getConsignerTel();
        }
        if (StringHelper.isNotEmpty(printWaybill.getConsignerMobile())) {
            if (consignerTelText.length() > 0) {
                consignerTelText += ",";
            }
            consignerTelText += printWaybill.getConsignerMobile();
        }
        printWaybill.setConsignerTelText(consignerTelText);

        /* 寄件信息前缀字符 */
        String consignerPrefixText = "";
        if (StringHelper.isNotEmpty(printWaybill.getConsignerTel())
                || StringHelper.isNotEmpty(printWaybill.getConsigner())
                || StringHelper.isNotEmpty(printWaybill.getConsignerMobile())) {
            consignerPrefixText += "寄件地址：";
        }
        printWaybill.setConsignerPrefixText(consignerPrefixText);

        /* packText如果为空的话则设置当前打印的时间yyyy-MM-dd HH:mm */
        printWaybill.setPackText(DateHelper.formatDate(new Date(),"yyyy-MM-dd HH:mm"));

        return context.getResult();
    }

}
