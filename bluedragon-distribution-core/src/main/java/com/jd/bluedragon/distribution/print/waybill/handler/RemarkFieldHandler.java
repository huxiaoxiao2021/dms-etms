package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_RESCHEDULE_PRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SMS_REVERSE_CHANGE_PRINT;

/**
 * 
 * @ClassName: RemarkFieldHandler
 * @Description: 包裹标签打印-备注字段处理
 * @author: wuyoude
 * @date: 2018年2月5日 下午5:36:29
 *
 */
@Service
public class RemarkFieldHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(RemarkFieldHandler.class);
    
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Value("${accordedVersion:20200113}")
    private int accordedVersion;

    /**
	 * 20200113WM
	 * 版本号后缀：WM
	 * */
    private static final String VERSION_SUFFIX = "WM";
    private static final String NULL_STR = "";
    
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.RemarkFieldHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-备注字段处理");
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		Waybill waybill = context.getWaybill();
		String waybillCode = waybill==null?null:waybill.getWaybillCode();
		String waybillSign = waybill==null?null:waybill.getWaybillSign();
		String sendPay = waybill==null?null:waybill.getSendPay();
		/**
		 * 1、自营运单-备注展示订单号
		 */
		String remark = basePrintWaybill.getRemark()==null?NULL_STR:basePrintWaybill.getRemark();
		if(BusinessUtil.isSelf(waybillSign)){
			String orderCode = waybillQueryManager.getOrderCodeByWaybillCode(waybillCode, true);
			if(StringHelper.isNotEmpty(orderCode)){
                remark = TextConstants.PRINT_TEXT_ORDER_CODE_PREFIX + orderCode + remark;
			}
		}
		/**
		 * 依据版本号升级，防止前后端升级异常
		 * */
		try {
			WaybillPrintRequest request = context.getRequest();
			String versionCode = request.getVersionCode();
			if(SITE_MASTER_PACKAGE_REPRINT.getType().equals(request.getOperateType())
                    || SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(request.getOperateType())
                    || SITE_MASTER_RESCHEDULE_PRINT.getType().equals(request.getOperateType())
                    || (StringUtils.isNotBlank(versionCode) && NumberUtils.isDigits(versionCode.replace(VERSION_SUFFIX,NULL_STR))
                    && Integer.valueOf(versionCode.replace(VERSION_SUFFIX,NULL_STR)) > accordedVersion)){
				if (!BusinessUtil.isBusinessNet(waybillSign)) {
					if (WaybillUtil.isSwitchCode(waybillCode)
							&& BusinessUtil.isSignChar(sendPay,8,'6')) {
						remark += DmsConstants.BAD_WAREHOURSE_FOR_PORT;
					}
					if (StringHelper.isNotEmpty(basePrintWaybill.getServiceCode())) {
						if (!remark.contains(basePrintWaybill.getServiceCode())) {
							if (remark.length() > 0) {
								remark += Constants.SEPARATOR_SEMICOLON;
							}
							remark += DmsConstants.PICKUP_CUSTOMER_COMMET;
							remark += basePrintWaybill.getServiceCode();
						}
					}
					if (StringHelper.isNotEmpty(basePrintWaybill.getBusiOrderCode())) {
						if (!remark.contains(basePrintWaybill.getBusiOrderCode())) {
							if (remark.length() > 0) {
								remark += Constants.SEPARATOR_SEMICOLON;
							}
							remark += DmsConstants.BUSINESS_ORDER_CODE_REMARK;
							remark += basePrintWaybill.getBusiOrderCode();
						}
					}
				}else {
					remark = basePrintWaybill.getBusiOrderCode();
				}
			}
		}catch (Exception e){
			log.error("RemarkFieldHandler error! {}",waybillCode,e);
		}
		//Sendpay292位为1，面单打印“合约机 需激活”
		if(BusinessUtil.isContractPhone(sendPay)){
			remark = StringHelper.appendIfNotExist(remark, TextConstants.REMARK_CONTRACT_PHONE);
		}
		/**
		 * 爱回收
		 */
		if(SWITCH_BILL_PRINT.getType().equals(context.getRequest().getOperateType())
				|| SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType())
				|| SMS_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType())){
			if(context.getBigWaybillDto() != null && context.getBigWaybillDto().getWaybill() != null
					&& StringUtils.isNotBlank(context.getBigWaybillDto().getWaybill().getRelWaybillCode())){
				remark += DmsConstants.REL_WAYBILL_CODE;
				remark += context.getBigWaybillDto().getWaybill().getRelWaybillCode();
			}
		}
        /**
         * 产品类型为md-m-0005时:医药专送
         */
        if(context.getBigWaybillDto() != null
                && context.getBigWaybillDto().getWaybill() != null
                && context.getBigWaybillDto().getWaybill().getWaybillExt() != null
                && StringUtils.isNotBlank(context.getBigWaybillDto().getWaybill().getWaybillExt().getProductType())){
            String productType = context.getBigWaybillDto().getWaybill().getWaybillExt().getProductType();
            if(Constants.PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY.equals(productType)){
				if (remark.length() > 0) {
					remark = StringHelper.append(remark, Constants.SEPARATOR_SEMICOLON);
				}
				//企业名称 todo
                remark = StringHelper.append(remark, TextConstants.COMMON_TEXT_COMPANY);
				//联系人 todo
				remark = StringHelper.append(remark, Constants.SEPARATOR_SEMICOLON);
				remark = StringHelper.append(remark, TextConstants.COMMON_TEXT_CONTACT);
                //存储条件
				remark = StringHelper.append(remark, Constants.SEPARATOR_SEMICOLON);
                remark = StringHelper.append(remark, TextConstants.COMMON_TEXT_STORAGE_CONDITION);
				//优先配送
				remark = StringHelper.append(remark, Constants.SEPARATOR_SEMICOLON);
                remark = StringHelper.append(remark, TextConstants.COMMON_TEXT_DELIVERY);
            }
        }
		log.info("RemarkFieldHandler waybillCode:{},remark:{}", waybillCode, remark);
		basePrintWaybill.setRemark(remark);
		return context.getResult();
	}
}