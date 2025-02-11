package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.handler.AbstractHandler;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.WaybillExt;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandler
 * @Description: 包裹标签打印-面单收、寄件人信息处理逻辑
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class CustomerAndConsignerInfoHandler extends AbstractHandler<WaybillPrintContext,JdResult<String>> {
	private static final Logger log = LoggerFactory.getLogger(CustomerAndConsignerInfoHandler.class);
	@Autowired
	@Qualifier("hideInfoService")
	private HideInfoService hideInfoService;
	/**
	 * 注入ucc配置
	 */
	@Resource
	private DmsConfigManager dmsConfigManager;

	/**
	 * 收件人联系方式需要突出显示的位数
	 */
	private static final int PHONE_HIGHLIGHT_NUMBER = 4;

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.CustomerAndConsignerInfoHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public JdResult<String> handle(WaybillPrintContext context) {
		if(context == null){
			log.warn("处理面单收/寄件信息，context为空");
			return null;
		}
		if(context.getBasePrintWaybill() == null){
			log.warn("处理面单收/寄件信息，context.BasePrintWaybill为空:{}" , JSON.toJSONString(context));
			return context.getResult();
		}
		//处理国际化运单的收件信息
		internationalCustomerInfo(context);

		//处理收/寄件的微笑
		log.debug("包裹标签打印-微笑面单-隐藏电话和地址");
		String waybillSign = "";
		String sendPay = "";
		if (context.getBigWaybillDto() != null && context.getBigWaybillDto().getWaybill() != null) {
			waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();
			sendPay = context.getBigWaybillDto().getWaybill().getSendPay();
		}
		hideInfoService.setHideInfo(waybillSign,sendPay, context.getBasePrintWaybill());
		//手机号、座机一样只保留一个
		removeRepeatedTel(context);
		//地址追加备注信息
		appendAdressRemark(context);
		//阿迪隐藏逻辑
		removeAddiConsigner(context);

		return context.getResult();
	}


    //移除阿迪寄件人信息
	private void removeAddiConsigner(WaybillPrintContext context) {
		try {
			//获取阿迪 ucc配置
			String addiOwnNumberConf = dmsConfigManager.getPropertyConfig().getAddiOwnNumberConf();
			Waybill waybill = context.getBigWaybillDto().getWaybill();
			if(StringHelper.isNotEmpty(addiOwnNumberConf)){
				List<String> asList = Arrays.asList(addiOwnNumberConf.split(Constants.SEPARATOR_COMMA));
				//获取商家青龙业主号
				String customerCode = waybill.getCustomerCode();
				String waybillSign = waybill.getWaybillSign();
				if(asList.contains(customerCode)&&BusinessUtil.isJDConsigner(waybillSign)){
					//置换为空
					BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
					basePrintWaybill.setConsigner(StringUtils.EMPTY);
					basePrintWaybill.setConsignerAddress(StringUtils.EMPTY);
					basePrintWaybill.setConsignerTel(StringUtils.EMPTY);
					basePrintWaybill.setConsignerMobile(StringUtils.EMPTY);
					basePrintWaybill.setConsignerPrefixText(StringUtils.EMPTY);
					basePrintWaybill.setConsignerTelText(StringUtils.EMPTY);
				}
			}
		} catch (Exception e) {
			log.error("阿迪隐藏寄件人信息异常: context:{}" , JsonHelper.toJson(context),e);
		}
	}

	//地址追加备注信息
	private void appendAdressRemark(WaybillPrintContext context) {
		if (context.getBasePrintWaybill() == null){
			return;
		}
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		//将printAddressRemark追加printAddress中
		String newPrintAddress = StringHelper.append(basePrintWaybill.getPrintAddress(), basePrintWaybill.getPrintAddressRemark());
		basePrintWaybill.setPrintAddress(newPrintAddress);
	}
	/**
	 * 手机号、座机一样只保留一个
	 * @param context
	 */
	private void removeRepeatedTel(WaybillPrintContext context) {
		BasePrintWaybill printWaybill = context.getBasePrintWaybill();
		//寄件人电话
		if(Objects.equal(printWaybill.getConsignerTel(), printWaybill.getConsignerMobile())){
			printWaybill.setConsignerTel("");
			printWaybill.setConsignerTelText(printWaybill.getConsignerMobile());
		}
		if(StringHelper.isNotEmpty(printWaybill.getCustomerContacts())){
			String[] tels = printWaybill.getCustomerContacts().split(",", 2);
			if(tels.length == 2){
				if(Objects.equal(tels[0], tels[1])){
					printWaybill.setCustomerContacts(tels[0]);
					printWaybill.setCustomerPhoneText(tels[0]);
					printWaybill.setTelFirst("");
					printWaybill.setTelLast("");
				}
			}
		}

	}

	/**
	 * 处理自营国际配送运单的收件信息
	 *
	 * @param context
	 */
	private void internationalCustomerInfo(WaybillPrintContext context) {
		if(context.getBigWaybillDto() == null){
			log.warn("处理国际化运单的收件信息.没有初始化运单信息:{}" , JSON.toJSONString(context));
			return;
		}

		if (context.getBigWaybillDto().getWaybill() != null && context.getBigWaybillDto().getWaybill().getWaybillExt() != null) {
			WaybillExt etmsWaybill = context.getBigWaybillDto().getWaybill().getWaybillExt();
			String sendPay = context.getBigWaybillDto().getWaybill().getSendPay();

			BasePrintWaybill printWaybill = context.getBasePrintWaybill();

			//国际配送运单替换原收件人地址，姓名，电话
			if (BusinessUtil.isInternationalWaybill(sendPay) && etmsWaybill != null) {
				if(StringUtils.isNotBlank(etmsWaybill.getConsolidatorAddress())) {
					printWaybill.setPrintAddress(etmsWaybill.getConsolidatorAddress());
				}
				if(StringUtils.isNotBlank(etmsWaybill.getConsolidatorName())) {
					printWaybill.setCustomerName(etmsWaybill.getConsolidatorName());
				}

				String receiverMobile = etmsWaybill.getConsolidatorPhone();
				if(StringUtils.isNotBlank(receiverMobile)) {
					printWaybill.setCustomerContacts(receiverMobile);

					//设置前四位和后四位
					if (receiverMobile.length() >= PHONE_HIGHLIGHT_NUMBER) {
						printWaybill.setMobileFirst(receiverMobile.substring(0, receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
						printWaybill.setMobileLast(receiverMobile.substring(receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
					} else {
						printWaybill.setMobileFirst(receiverMobile);
						printWaybill.setMobileLast("");
					}

					//避免重复显示 tel设置成空
					printWaybill.setTelFirst("");
					printWaybill.setTelLast("");
				}
			}
		}
	}
}