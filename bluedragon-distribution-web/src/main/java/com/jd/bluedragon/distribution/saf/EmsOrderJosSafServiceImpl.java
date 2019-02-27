package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillInfo;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CnUpperCaser;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("emsOrderJosSafService")
public class EmsOrderJosSafServiceImpl implements EmsOrderJosSafService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SendDatailDao sendDatailDao;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private BaseService baseService;
	
	@Autowired
	private ReverseDeliveryService tReverseDeliveryService;

	// EMS快递站点信息读取
	public static final String EMS_SITE = "EMS_SITE";

	@Override
	public WaybillInfoResponse getWaybillInfo(String waybillCode) {
		if (waybillCode == null || waybillCode.isEmpty()) {
			logger.error("JOS获取订单信息参数为空");
			return new WaybillInfoResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		return getEmsWaybillInfo(waybillCode);
	}
	
	/**
     * 比较时间大小
     * 
     * @param SendDetailList
     */
    public SendDetail getLastSendDate(List<SendDetail> SendDetailList) {
    	SendDetail tSendDetail = null;
        if (SendDetailList != null && !SendDetailList.isEmpty()) {
            for (SendDetail dSendDetail : SendDetailList) {
                if (tSendDetail == null) {
                	tSendDetail = dSendDetail;
                } else if (tSendDetail.getCreateTime().getTime() < dSendDetail.getCreateTime().getTime()) {
                	tSendDetail = dSendDetail;
                }
            }
        }
        return tSendDetail;
    }

	@JProfiler(jKey= "DMSWEB.EmsOrderJosSafServiceImpl.getEmsWaybillInfo",mState = {JProEnum.TP})
	private WaybillInfoResponse getEmsWaybillInfo(String waybillCode) {

		logger.error("JOS获取订单信息,订单号为" + waybillCode);
		SendDetail send = new SendDetail();
		send.setWaybillCode(waybillCode);
		List<SendDetail> sendlist = sendDatailDao
				.querySendDatailsBySelective(send);//FIXME:无create_site_code有跨节点风险
		if (sendlist.isEmpty()) {
			logger.error("JOS获取订单信息,订单没有发货记录" + waybillCode);
			return new WaybillInfoResponse(JdResponse.CODE_OK_NULL,
					JdResponse.MESSAGE_OK_NULL);
		}
		send = getLastSendDate(sendlist);

		List<SysConfig> configs = baseService
				.queryConfigByKeyWithCache(EMS_SITE);
		for (SysConfig sys : configs) {
			if (!StringHelper.matchSiteRule(sys.getConfigContent(), send.getReceiveSiteCode().toString()))
				return new WaybillInfoResponse(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
		}

		configs = baseService.queryConfigByKeyWithCache(EMS_SITE + "_"
				+ send.getReceiveSiteCode());
		String sysAccount = "";
		for (SysConfig sys : configs) {
			sysAccount = sys.getConfigContent();
		}

		Integer createSiteCode = send.getCreateSiteCode();
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(createSiteCode);

		List<WaybillInfo> list = new ArrayList<WaybillInfo>();
		try {
			BigWaybillDto WaybillDto = waybillService.getWaybill(waybillCode);
			//如果订单信息为空咋调用快生运单数据源获取信息
			if (WaybillDto == null || WaybillDto.getWaybill() == null){
				WaybillDto = tReverseDeliveryService.getWaybillQuickProduce(waybillCode);
			}
			
			if (WaybillDto != null && WaybillDto.getWaybill() != null) {
				Waybill waybill = WaybillDto.getWaybill();
				List<DeliveryPackageD> deliveryPackage = WaybillDto
						.getPackageList();
				if (deliveryPackage != null
						&& !deliveryPackage.isEmpty()
						&& BusinessHelper.checkIntNumRange(deliveryPackage
								.size())) {
					String collection = "";
					Double needFund = 0.00;
					String declaredValue = waybill.getCodMoney();
					String payment = String.valueOf(waybill.getPayment());
					for (DeliveryPackageD delivery : deliveryPackage) {
						WaybillInfo info = new WaybillInfo();
						info.setPackageBarcode(delivery.getPackageBarcode());
						info.setWaybillCode(waybill.getWaybillCode());
						info.setPackageNum(waybill.getGoodNumber());

						if (bDto != null) {
							if(bDto.getDmsName()!=null)
								info.setScontactor(bDto.getDmsName());
							else
								info.setScontactor("");
							if(bDto.getPhone()!=null)
								info.setScustMobile(bDto.getPhone());
							else
								info.setScustMobile("");
							if(bDto.getAddress()!=null)
								info.setScustAddr(bDto.getAddress());
							else
								info.setScustAddr("");
						}

						info.setTcontactor(waybill.getReceiverName());
						info.setTcustMobile(waybill.getReceiverMobile());
						info.setTcustTelplus(waybill.getReceiverTel());
						info.setTcustPost(waybill.getReceiverZipCode());
						info.setTcustAddr(waybill.getReceiverAddress());
						info.setTcustProvince(waybill.getProvinceName());
						info.setTcustCity(waybill.getCityName());
						info.setTcustCounty(waybill.getCountryName());
						if(delivery.getAgainWeight()!=null)
							info.setWeight(delivery.getAgainWeight());
						else
							info.setWeight(0.0);
						if(delivery.getGoodVolume()!=null)
							info.setVolume(delivery.getGoodVolume());
						else
							info.setVolume("0");
						// 1货到付款 3上门自提
						if (payment.equals("1") || payment.equals("3")) {
							collection = "2";
							if (declaredValue != null) {
								needFund = Math.round(Double
										.parseDouble(declaredValue) * 100) / 100.00;
							}
						}
						if(declaredValue == null){
							declaredValue = "";
						}
						info.setFeeUppercase(new CnUpperCaser(declaredValue).getCnString());
						info.setFee(needFund);
						info.setPayMode("1");// 1现金
						info.setBusinessType(collection);
						info.setMainSubPayMode("4");

						info.setSysAccount(sysAccount);
						list.add(info);
					}
				}
			} else {
				logger.error("JOS获取订单信息,订单接口返回空信息" + waybillCode);
				return new WaybillInfoResponse(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			logger.error("JOS获取订单信息失败：信息为", e);
			return new WaybillInfoResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return new WaybillInfoResponse(JdResponse.CODE_OK,
				JdResponse.MESSAGE_OK, JsonHelper.toJson(list));
	}

}
