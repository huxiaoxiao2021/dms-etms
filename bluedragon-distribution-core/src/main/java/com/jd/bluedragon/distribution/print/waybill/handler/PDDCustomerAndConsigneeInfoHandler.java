package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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

    private static final Logger log = LoggerFactory.getLogger(PDDCustomerAndConsigneeInfoHandler.class);

    @Autowired
    private PDDService pddService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        /* 非拼多多订单不走获取拼多多数据逻辑 */
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            log.info("该单不是拼多多单，不需要从拼多多接口中获取收寄件人信息：{}", waybillCode);
            return result;
        }

        /* 10*5的面单无需获取拼多多面单联系人逻辑 */
        if (DmsPaperSize.PAPER_SIZE_CODE_1005.equals(context.getRequest().getPaperSizeCode())) {
            log.info("该单打印的是10*5面单，不需要从拼多多接口中获取收寄件人信息：{}", waybillCode);
            return result;
        }

        //获取操作人所属站点匹配是否为分拣中心，当时分拣中心并且操作的是包裹补打的时候需提示只能补打10*5面单
        //开关
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(context.getRequest().getUserCode());
        if(dto!=null && Integer.valueOf(64).equals(dto.getSiteType())){
            if(notNeedPddOfPrintType(context.getRequest().getOperateType())){
                result.toFail(MessageFormat.format("该单【{0}】为拼多多订单，请补打标签尺寸为10*5的包裹签", waybillCode));
                log.info(result.getMessage());
                return result;
            }
        }

        //判断运单中的收件人手机号或电话号都为非密文，则直接返回不需要调用拼多多
        if( (StringUtils.isNotBlank(context.getBigWaybillDto().getWaybill().getReceiverMobile())
                && context.getBigWaybillDto().getWaybill().getReceiverMobile().indexOf('*') == -1)
            || (StringUtils.isNotBlank(context.getBigWaybillDto().getWaybill().getReceiverTel())
                && context.getBigWaybillDto().getWaybill().getReceiverTel().indexOf('*') ==-1)){
            log.info("该单【{}】为拼多多订单，运单中的手机号或电话号非密文，不调用拼多多接口", waybillCode);
            return result;
        }

        PDDResponse<PDDWaybillDetailDto> pddWaybillDetailDtoPDDResponse = pddService.queryPDDWaybillByWaybillCode(waybillCode);
        if (pddWaybillDetailDtoPDDResponse == null || !Boolean.TRUE.equals(pddWaybillDetailDtoPDDResponse.getSuccess())
                || pddWaybillDetailDtoPDDResponse.getResult() == null) {
            log.warn("拼多多订单信息获取失败:{}",waybillCode);
            result.toWeakSuccess(MessageFormat.format("该单【{0}】为拼多多订单，获取联系人信息失败", waybillCode));
            return result;
        }
        //用于统计拼多多面单打印获取的次数
        if(log.isInfoEnabled()){
            log.info("pddService.queryPDDWaybillByWaybillCode,siteCode:{},siteName:{},req:{},operateType:{}",
                    context.getRequest().getSiteCode(),context.getRequest().getSiteName(), waybillCode, context.getRequest().getOperateType());
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
        String tel = pddWaybillDetailDto.getConsigneePhone();
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

        return result;
    }

    private boolean notNeedPddOfPrintType(Integer printType){
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_PDD_PRINT_TYPE_NOT_USE);
        if (content != null) {
            if (content.getMasterSwitch() || content.getKeyCodes().contains(printType.toString())) {
                return true;
            }
        }
        return false;
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
