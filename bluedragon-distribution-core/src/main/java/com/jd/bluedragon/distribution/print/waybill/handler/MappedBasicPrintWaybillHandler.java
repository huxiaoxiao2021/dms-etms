package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT;

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

    private static final String REVERSE_PRINT_COMMENT = "逆向换单，原单号【{0}】新单号【{1}】";

    private static final String SPLIT = "；";

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        WaybillPrintResponse printWaybill = context.getResponse();
        if (null == printWaybill) {
            return context.getResult();
        }

        /* packText如果为空的话则设置当前打印的时间yyyy-MM-dd HH:mm */
        printWaybill.setPackText(DateHelper.formatDate(new Date(),"yyyy-MM-dd HH:mm"));

        /* 新加字段：CompanyId 从：popSupId */
        printWaybill.setCompanyId(printWaybill.getPopSupId());

        /* 新加字段：CompanyName 从：popSupName */
        printWaybill.setCompanyName(printWaybill.getPopSupName());

        /* 新加字段：bCustomerId 从：busiId */
        printWaybill.setbCustomerId(printWaybill.getBusiId());

        /* 新加字段：bCustomerName 从：busiName */
        printWaybill.setbCustomerName(printWaybill.getBusiName());

        /* 新加字段：packageCounter 从：quantity */
        printWaybill.setPackageCounter(printWaybill.getQuantity());

        /* 新加字段：barCode 从：orderCode */
        printWaybill.setBarCode(printWaybill.getOrderCode());

        /* 新加字段：printSiteCode 从：prepareSiteCode */
        printWaybill.setPrintSiteCode(printWaybill.getPrepareSiteCode());

        /* 新加字段：receivable 从：packagePrice */
        printWaybill.setReceivable(printWaybill.getPackagePrice());

        /* 新加字段：waybillType 从：type */
        printWaybill.setWaybillType(printWaybill.getType());

        /* 新加字段：rodeCode 从：roadCode （截止到2019年4月18日客户端取值从road中，后面会进行迁移使用roadCode） */
        printWaybill.setRodeCode(printWaybill.getRoadCode());

        /* 新加字段：customerPhoneText 从：customerContacts */
        printWaybill.setCustomerPhoneText(printWaybill.getCustomerContacts());

        /* 新加字段：hasPrintInvoice 从：isPrintInvoice */
        printWaybill.setHasPrintInvoice(printWaybill.isPrintInvoice());

        /* 新加字段：packList.packageWeight */
        if (printWaybill.getPackList() != null) {
            for (PrintPackage packItem : printWaybill.getPackList()) {
                packItem.setPackageWeight(String.valueOf(packItem.getWeight()) + Constants.MEASURE_UNIT_NAME_KG);
            }
        }

        /* 加工字段：dmsName */
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

        /* 加工字段：处理代收金额receivable */
        String receivable = printWaybill.getReceivable();
        if (StringHelper.isEmpty(receivable)) {
            receivable = "￥0.00";
        } else if (NumberHelper.isNumber(printWaybill.getReceivable())) {
            receivable = "￥" + receivable;
        }
        printWaybill.setReceivable(receivable);

        /* 加工字段：寄件人电话consignerTelText */
        String consignerTelText = "";
        if (StringHelper.isNotEmpty(printWaybill.getConsignerTel())) {
            consignerTelText += printWaybill.getConsignerTel();
        }
        if (StringHelper.isNotEmpty(printWaybill.getConsignerMobile())) {
            consignerTelText += " ";
            consignerTelText += printWaybill.getConsignerMobile();
        }
        printWaybill.setConsignerTelText(consignerTelText);

        /* 加工字段：寄件信息前缀字符consignerPrefixText */
        String consignerPrefixText = "";
        if (StringHelper.isNotEmpty(printWaybill.getConsignerTel())
                || StringHelper.isNotEmpty(printWaybill.getConsigner())
                || StringHelper.isNotEmpty(printWaybill.getConsignerMobile())) {
            consignerPrefixText += "寄件地址：";
        }
        printWaybill.setConsignerPrefixText(consignerPrefixText);

        /* 加工字段：barCode */
        printWaybill.setBarCode(WaybillUtil.getWaybillCode(context.getRequest().getBarCode()));

        String newWaybillCode = context.getResponse().getWaybillCode();//获取新单号的运单号

        /* 包裹补打且是T、F单 或者是换单打印 */
        boolean bool = (SITE_MASTER_PACKAGE_REPRINT.getType().equals(context.getRequest().getOperateType())
                        && (WaybillUtil.isReturnCode(WaybillUtil.getWaybillCode(context.getRequest().getBarCode()))
                            || WaybillUtil.isSwitchCode(WaybillUtil.getWaybillCode(context.getRequest().getBarCode()))))
                || SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType());
        if (bool) {
            /* 加工字段：wareHouseText */
            printWaybill.setWarehouseText(context.getRequest().getSiteName());

            /*
                加工字段：comment
                逆向换单，原单号【{0}】新单号【{1}】
            */
            String oldWaybillCode = context.getResponse().getOldWaybillCode();//获取原单号的运单号
            if (StringHelper.isEmpty(oldWaybillCode)) {
                oldWaybillCode = WaybillUtil.getWaybillCode(context.getRequest().getOldBarCode());
            }

            printWaybill.setComment(MessageFormat.format(REVERSE_PRINT_COMMENT,
                    oldWaybillCode == null? "" : oldWaybillCode, newWaybillCode));
        }

        return context.getResult();
    }

}
