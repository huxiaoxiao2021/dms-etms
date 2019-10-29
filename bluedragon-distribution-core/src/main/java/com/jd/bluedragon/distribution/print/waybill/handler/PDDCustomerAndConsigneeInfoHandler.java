package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * <p>
 *     拼多多收寄件人的姓名和联系方式的处理，需要从拼多多的外部接口中获取，如果获取失败，则用原字符（来自运单加密字符）
 *
 * @author wuzuxiang
 * @since 2019/10/16
 **/
@Service("pddCustomerAndConsigneeInfoHandler")
public class PDDCustomerAndConsigneeInfoHandler implements InterceptHandler<WaybillPrintContext, String> {

    private static final Logger logger = LoggerFactory.getLogger(PDDCustomerAndConsigneeInfoHandler.class);

    @Autowired
    private PDDService pddService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<>();
        result.toSuccess();//初始状态成功

        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        /* 非拼多多订单不走获取拼多多数据逻辑 */
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            logger.info("该单不是拼多多单，不需要从拼多多接口中获取收寄件人信息：{}", waybillCode);
            return result;
        }

        /* 10*5的面单无需获取拼多多面单联系人逻辑 */
        if (DmsPaperSize.PAPER_SIZE_CODE_1005.equals(context.getRequest().getPaperSizeCode())) {
            logger.info("该单打印的是10*5面单，不需要从拼多多接口中获取收寄件人信息：{}", waybillCode);
            return result;
        }

        /* todo 如果运单那边有联系人的联系方式则不调用拼多多的数据接口 */
        /* todo 如果是分拣中心则不调用拼多多的数据接口 */

        PDDResponse<PDDWaybillDetailDto> pddWaybillDetailDtoPDDResponse = pddService.queryPDDWaybillByWaybillCode(waybillCode);
        if (pddWaybillDetailDtoPDDResponse == null || !Boolean.TRUE.equals(pddWaybillDetailDtoPDDResponse.getSuccess())
                || pddWaybillDetailDtoPDDResponse.getResult() == null) {
            logger.warn("拼多多订单信息获取失败:{}",waybillCode);
            result.toWeakSuccess(MessageFormat.format("该单【{0}】为拼多多订单，获取联系人信息失败", waybillCode));
            return result;
        }

        PDDWaybillDetailDto pddWaybillDetailDto = pddWaybillDetailDtoPDDResponse.getResult();

        context.getBasePrintWaybill().setConsigner(pddWaybillDetailDto.getSenderName());
        context.getBasePrintWaybill().setConsignerMobile(pddWaybillDetailDto.getSenderMobile());
        context.getBasePrintWaybill().setConsignerTel(pddWaybillDetailDto.getSenderPhone());
        context.getBasePrintWaybill().setConsignerTelText(StringHelper.isEmpty(pddWaybillDetailDto.getSenderPhone())?
                pddWaybillDetailDto.getSenderMobile() : pddWaybillDetailDto.getSenderPhone());

        context.getBasePrintWaybill().setCustomerName(pddWaybillDetailDto.getConsigneeName());
        context.getBasePrintWaybill().setCustomerPhoneText(StringHelper.isEmpty(pddWaybillDetailDto.getConsigneeMobile())?
                pddWaybillDetailDto.getConsigneePhone() : pddWaybillDetailDto.getConsigneeMobile());

        //设置前四位和后四位
        context.getBasePrintWaybill().setCustomerContacts(concatPhone(pddWaybillDetailDto.getConsigneeMobile(),pddWaybillDetailDto.getConsigneePhone()));
        String mobile = pddWaybillDetailDto.getConsigneeMobile();
        String tel = pddWaybillDetailDto.getSenderPhone();
        if (StringUtils.isNotBlank(mobile) && mobile.length() >= StringHelper.PHONE_HIGHLIGHT_NUMBER) {
            String firstMobile = mobile.substring(0, mobile.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER);
            context.getBasePrintWaybill().setMobileFirst(firstMobile);
            context.getBasePrintWaybill().setMobileLast(mobile.substring(mobile.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
        }
        if (StringUtils.isNotBlank(tel) && tel.length() >= StringHelper.PHONE_HIGHLIGHT_NUMBER) {
            String firstTel = tel.substring(0, tel.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER);
            context.getBasePrintWaybill().setTelFirst(firstTel);
            context.getBasePrintWaybill().setTelLast(tel.substring(tel.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
        }

        return context.getResult();
    }

    private final String concatPhone(String mobile,String phone){
        StringBuilder sb=new StringBuilder();
        if(StringHelper.isNotEmpty(mobile)){
            sb.append(mobile);
        }

        if( StringHelper.isNotEmpty(phone)){
            if(StringHelper.isNotEmpty(mobile)) {
                sb.append(",");
            }
            sb.append(phone);
        }
        return sb.toString();
    }
}
