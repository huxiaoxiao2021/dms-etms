package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.reverse.domain.*;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.service.PackExchangeServiceManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.wms.packExchange.Result;
import com.jd.wms.packExchange.domains.packExchange.PePackage;
import com.jd.wms.packExchange.domains.packExchange.dto.BlueLongDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("baseWmsService")
public class BaseWmsServiceImpl implements BaseWmsService {
	
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(BaseWmsServiceImpl.class);
	
	private String packExchangeBizTokenJasonAsia;
	
	@Autowired
	PackExchangeServiceManager packExchangeServiceManager;
	
	@Autowired
	BaseService baseService;
	
	
	@Override
	public ReverseSendAsiaWms getWaybillByOrderCode(String orderCode,String packcodes, WmsSite site,boolean falge) {
		ReverseSendAsiaWms reverseSendWms = new ReverseSendAsiaWms();
		reverseSendWms.setCky2(site.getCky2());
		reverseSendWms.setLossQuantity(0);
		reverseSendWms.setOrgId(site.getOrgId());
		reverseSendWms.setProList(getOrderProducts(orderCode,packcodes,site,falge));
		reverseSendWms.setStoreId(site.getStoreId());
		
		if (reverseSendWms.getProList()==null||reverseSendWms.getProList().size()==0) {
			log.info("BaseWmsServiceImpl 订单号： {} 获得商品数据为空", orderCode);
		}
		return reverseSendWms;
	}
	
	/**
	 * 亚一未报丢的订单通过新接口获取商品明细
	 * @param orderCode
	 * @param packcodes
	 * @param site
	 * @param falge
	 * @return 商品明细，如果是异地退取运单中商品明细，反之从上海亚一仓储接口取得商品明细
	 */
	public List<Product> getOrderProducts(String orderCode,String packcodes, WmsSite site,boolean falge) {
		this.log.info("亚一通过包裹获取出库明细getOrderProducts");
		if (packExchangeBizTokenJasonAsia == null) {
			initPackExchangeBizTokenAsia();
		}
		BlueLongDTO reportDto = new BlueLongDTO();
		reportDto.setOrgNo(site.getOrgId().toString());
		reportDto.setWarehouseNo(site.getStoreId().toString());
		reportDto.setDistributeNo(site.getCky2().toString());
		reportDto.setBusinessNo(orderCode);
		String[] packArray = packcodes.split(",");
		List<String> packNoList = new ArrayList<String>();
		Set<String> packNoSet = new HashSet<String>();
		for (String pack : packArray) {
			packNoList.add(pack);
			packNoSet.add(pack);
		}
		reportDto.setPackNoList(packNoList);
		
		// 1.判断是否打包齐全
		boolean isPackagedFull = false;
		if (packNoSet.size() < 1) {
			isPackagedFull = true;
		} else {
			String packageBarCode = packNoList.get(0);
			int packageNum = WaybillUtil.getPackNumByPackCode(packageBarCode);
			if (packNoSet.size() == packageNum) {
				isPackagedFull = true;
			}
		}
		//2.先从运单取得明细，及出库仓储号cky2\orgId\storeId
		ReverseSendWms send = baseService.getWaybillByOrderCode(orderCode);
		
		//3.判断是否是异地退货，也就是他仓退;默认非本仓
		boolean isOtherStore = true;
		if(send==null){
			log.info("该订单号逆向发货不齐全，调用出库明细：{}" , orderCode);
			isOtherStore = false;//运单获得不了数据，那么从仓储接口试试
		}else if (site.getCky2().equals(send.getCky2())
				&& site.getOrgId().equals(send.getOrgId())
				&& site.getStoreId().equals(send.getStoreId())){
			isOtherStore = false;//表示是生产仓与退货仓是一个仓
		}
		
		//3.1 齐全订单或者异地退货使用运单中明细
		if (isPackagedFull||isOtherStore) {
			log.info("该订单号逆向发货齐全或为异地退上海亚一：{}" , orderCode);
			List<Product> result = new ArrayList<Product>();
			if (send != null) {
				List<Product> resultRaw = send.getProList();
				for (Product product : resultRaw) {
					product.setProductPrice("0");
					result.add(product);
				}
			}
			return result;
		}
		log.info("该订单号逆向发货不齐全，调用出库明细：{}" , orderCode);
		
		// 4.需要取上海亚一明细的
		try {
			String bizBody = JsonHelper.toJson(reportDto);
			this.log.info("亚一通过包裹获取出库明细packExchangeBizTokenJasonAsia的值:{}",packExchangeBizTokenJasonAsia);
			this.log.info("亚一通过包裹获取出库明细bizBody的值:{}",bizBody);
			Result result = packExchangeServiceManager.queryWs(
					packExchangeBizTokenJasonAsia, bizBody);

			int resultCode = result.getResultCode();
			if (resultCode == 1) {
				String value = result.getResultValue();
				List<PePackage> reportResults = Arrays.asList(JsonHelper
						.jsonToArrayWithDateFormatOne(value, PePackage[].class));
				return toAsiaProduct(reportResults);
			} else {
				log.warn("亚一call wms packExchangeWebService failed, result code:{}", resultCode);
			}
		} catch (Exception e) {
			log.error("亚一getProductsByWms failed", e);
		}
		return null;
	}
	
	private List<Product> toAsiaProduct(List<PePackage> reportResults) {
		this.log.info("亚一通过包裹获取出库明细toAsiaProduct");
		if (reportResults == null || reportResults.isEmpty()) {
			return null;
		}
		List<Product> products = new ArrayList<Product>();
		List<String> productIds = new ArrayList<String>();
		for (PePackage pePackage : reportResults) {
			if(productIds.contains(pePackage.getGoodsNo())){
				for (Product product : products){
					if(pePackage.getGoodsNo().equals(product.getProductId())){
						Integer count = product.getProductNum();
						product.setProductNum(count+pePackage.getQty());
					}
				}
			}else{
				productIds.add(pePackage.getGoodsNo());
				Product product = new Product();
				product.setProductId(pePackage.getGoodsNo());
				product.setProductName(pePackage.getGoodsName());
				product.setProductNum(pePackage.getQty());
				product.setProductPrice("0");
				product.setProductLoss("0");
				products.add(product);
			}
		}
		return products;
	}
	
	private void initPackExchangeBizTokenAsia() {
		try {
			String bizType = PropertiesHelper.newInstance().getValue(
					"wms.packExchange.service.bizTypeAsia");
			String callCode = PropertiesHelper.newInstance().getValue(
					"wms.packExchange.service.callCode");
			String uuid = PropertiesHelper.newInstance().getValue(
					"wms.packExchange.service.uuid");
			BizToken token = new BizToken();
			token.setBizType(bizType);
			token.setCallCode(callCode);
			token.setUuid(uuid);
			packExchangeBizTokenJasonAsia = JsonHelper.toJson(token);
		} catch (Exception e) {
			log.error("packExchangeBizTokenJason failed", e);
		}
	}
	
}
