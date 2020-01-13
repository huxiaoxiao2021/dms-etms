package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private int ACCORDED_VERSION;

    /**
	 * 20200113WM
	 * 版本号后缀：WM
	 * */
    private static final String VERSION_SUFFIX = "WM";
    private static final String NULL_STR = "";
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-备注字段处理");
		WaybillPrintResponse response = context.getResponse();
		String waybillSign = response.getWaybillSign();
		/**
		 * 1、自营运单-备注展示订单号
		 */
		String remark = response.getRemark()==null?NULL_STR:response.getRemark();
		if(BusinessUtil.isSelf(waybillSign)){
			String orderCode = waybillQueryManager.getOrderCodeByWaybillCode(response.getWaybillCode(), true);
			if(StringHelper.isNotEmpty(orderCode)){
				response.setRemark(TextConstants.PRINT_TEXT_ORDER_CODE_PREFIX + orderCode);
				response.appendRemark(remark);
			}
		}
		/**
		 * 依据版本号升级，防止前后端升级异常
		 * */
		try {
			WaybillPrintRequest request = context.getRequest();
			String versionCode = request.getVersionCode();
			if(StringUtils.isBlank(versionCode)
					|| Integer.valueOf(versionCode.replace(VERSION_SUFFIX,NULL_STR)) > ACCORDED_VERSION){
				if (!BusinessUtil.isBusinessNet(response.getWaybillSign())) {
					if (WaybillUtil.isSwitchCode(response.getWaybillCode())
							&& BusinessUtil.isSignChar(response.getSendPay(),8,'6')) {
						remark += DmsConstants.BAD_WAREHOURSE_FOR_PORT;
					} else {
						remark += StringHelper.isEmpty(response.getRemark())? NULL_STR : response.getRemark();
					}
					if (StringHelper.isNotEmpty(response.getServiceCode())) {
						if (!remark.contains(response.getServiceCode())) {
							if (remark.length() > 0) {
								remark += Constants.SEPARATOR_SEMICOLON;
							}
							remark += DmsConstants.PICKUP_CUSTOMER_COMMET;
							remark += response.getServiceCode();
						}

					}
					if (StringHelper.isNotEmpty(response.getBusiOrderCode())) {
						if (!remark.contains(response.getBusiOrderCode())) {
							if (remark.length() > 0) {
								remark += Constants.SEPARATOR_SEMICOLON;
							}
							remark += DmsConstants.BUSINESS_ORDER_CODE_REMARK;
							remark += response.getBusiOrderCode();
						}
					}
				}else {
					remark = response.getBusiOrderCode();
				}
			}
		}catch (Exception e){
			log.error("版本号异常!");
		}
		response.setRemark(remark);
		return context.getResult();
	}
}