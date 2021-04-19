package com.jd.bluedragon.distribution.order.ws;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.order.domain.InternationDetailOrderDto;
import com.jd.bluedragon.distribution.order.domain.InternationOrderDto;
import com.jd.bluedragon.utils.OrderServiceHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.i18n.order.dict.FieldKeyEnum;
import com.jd.ioms.jsf.export.domain.ExportResult;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ioms.jsf.export.domain.OrderDetail;
import com.jd.jdorders.component.vo.request.OrderQueryRequest;
import com.jd.jdorders.component.vo.response.OrderQueryResponse;
import com.jd.order.export.domain.ComponentBase;
import com.jd.ql.basic.domain.Assort;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import jd.oom.client.core.OrderLoadFlag;
import jd.oom.client.orderfile.OrderArchiveInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("orderWebService")
public class OrderWebService {

	@Autowired
	private  BaseService baseService;

	@Value("${internation.Order.Region:301}")
	private String region_JD_LOGISTICS;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PreseparateWaybillManager preseparateWaybillManager;

    @JProfiler(jKey = "DMS.WEB.OrderWebService.getOrderDetailById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<com.jd.ioms.jsf.export.domain.OrderDetail> getOrderDetailById(long orderId) {
		com.jd.ioms.jsf.export.OrderMiddlewareJSFService oomServiceSoap = (com.jd.ioms.jsf.export.OrderMiddlewareJSFService) SpringHelper
		        .getBean("orderMiddlewareJSFService");
		ExportResult<List<OrderDetail>> orderDetail = oomServiceSoap
		        .getOrderDetailListByOrderId(orderId);

		if (orderDetail.getData() != null && orderDetail.getData().size() > 0) {

			return orderDetail.getData();
		}

		return Collections.emptyList();
	}

	/**
	 * 获取国际化订单详情集合接口
	 * @param orderId
	 * @return
	 */
	@JProfiler(jKey = "DMS.WEB.OrderWebService.getOrderDetailById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<InternationDetailOrderDto> getInternationDetailById(long orderId){
		com.jd.jdorders.component.export.OrderMiddlewareCBDExport orderMidd = this.getOrderMiddlewareCBDExport();

		// 请求对象
		OrderQueryRequest request = new OrderQueryRequest();
		// 通用业务参数
		ComponentBase componentBase = new ComponentBase();
		componentBase.setRegion(region_JD_LOGISTICS);
		// 必传字段，测试环境或者物理机部署的应用无法自动获取appname，可手动传入，默认取jvm的启动参数deploy.app.name
		componentBase.setAppName(Constants.UMP_APP_NAME_DMSWEB);
		List<String> querys = OrderServiceHelper.getQueryDetailKeys();
		// 赋值
		request.setComponentBase(componentBase);

		//1 订单中间件表字段(订单基本信息+订单详情信息)
		request.setBizType("1");
		request.setOrderId(orderId);
		request.setQueryKeys(querys);

		OrderQueryResponse response = orderMidd.getOrderData(request);
		if (response.isSuccess()) {
			Map<String, Object> dataMap = response.getDataMap();
			if (dataMap != null && dataMap.size() > 0){
				return  transDetailForm(dataMap);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 转化国际化订单详情集合
	 * @param dataMap
	 * @return
	 */
	private List<InternationDetailOrderDto> transDetailForm(Map<String, Object> dataMap) {
		List<InternationDetailOrderDto> list = new ArrayList<InternationDetailOrderDto>();
		// 订单商品详情
		List<Map<String, Object>> details = (List<Map<String, Object>>) dataMap.get(FieldKeyEnum.M_DETAILS.getFieldName());

		if (CollectionUtils.isNotEmpty(details)) {
			for (Map<String, Object> orderDetailMap : details) {
				// 名称
				String  name = (String) orderDetailMap.get(FieldKeyEnum.M_DETAIL_NAME.getFieldName());
				// 数量
				Integer num = (Integer)orderDetailMap.get(FieldKeyEnum.M_DETAIL_NUM.getFieldName());
				// 产品Id
				Long productId = (Long) orderDetailMap.get(FieldKeyEnum.M_DETAIL_PRODUCTID.getFieldName());
				// 价格
				BigDecimal price = (BigDecimal) orderDetailMap.get(FieldKeyEnum.M_DETAIL_PRICE.getFieldName());
				// shu
				Long skuId = (Long) orderDetailMap.get(FieldKeyEnum.M_DETAIL_SKUID.getFieldName());
				// 渠道ID
				Integer channelId = (Integer) orderDetailMap.get(FieldKeyEnum.M_DETAIL_PROFITCHANNELID.getFieldName());

				InternationDetailOrderDto orderDetail = new InternationDetailOrderDto();
				orderDetail.setName(name);
				orderDetail.setNum(num);
				orderDetail.setProductId(productId);
				orderDetail.setPrice(price);
				orderDetail.setSkuId(skuId);
				orderDetail.setProfitChannelId(channelId);
				list.add(orderDetail);
			}
		}
		return list;
	}


	@JProfiler(jKey = "DMS.WEB.OrderWebService.getHistoryOrderDetailById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<jd.oom.client.orderfile.OrderDetail> getHistoryOrderDetailById(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");

		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));

		if (orderArchiveInfo != null && orderArchiveInfo.getOrder() != null
		        && orderArchiveInfo.getOrder().getDetails() != null) {
			return orderArchiveInfo.getOrder().getDetails().getOrderDetail();
		}

		return Collections.emptyList();
	}

    @JProfiler(jKey = "DMS.WEB.OrderWebService.getHisWaybillByOrderId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Waybill getHisWaybillByOrderId(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");

		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));

		if (orderArchiveInfo != null && orderArchiveInfo.getOrder() != null) {
			jd.oom.client.orderfile.Order order = orderArchiveInfo.getOrder();
			Waybill waybill = new Waybill();
			waybill.setWaybillCode(String.valueOf(orderId));
			waybill.setSiteCode(order.getPartnerId());
			waybill.setSiteName(order.getIdPickSiteName());
			waybill.setPaymentType(order.getIdPaymentType());
			waybill.setSendPay(order.getSendPay());
			if (order.getWeight() != null) {
				waybill.setWeight(order.getWeight().doubleValue());
			}
			waybill.setAddress(order.getAddress());
			waybill.setOrgId(order.getIdCompanyBranch());
			waybill.setStoreId(order.getStoreId());
			waybill.setType(order.getOrderType());
			waybill.setShipmentType(order.getIdShipmentType());
			return waybill;
		}

		return null;
	}

    @JProfiler(jKey = "DMS.WEB.OrderWebService.getOrder", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Order getOrder(long orderId) {
		com.jd.ioms.jsf.export.OrderMiddlewareJSFService orderMiddlewareJSFService = (com.jd.ioms.jsf.export.OrderMiddlewareJSFService) SpringHelper
				.getBean("orderMiddlewareJSFService");
		ExportResult<Order> exportResult = orderMiddlewareJSFService.getOrderById(orderId, false, OrderServiceHelper.getAllFlag());
		if (exportResult != null && exportResult.isSuccess()) {
			return exportResult.getData();
		}
		return null;
	}

	@JProfiler(jKey = "DMS.WEB.OrderWebService.getOrder", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InternationOrderDto getInternationOrder(long orderId){
		com.jd.jdorders.component.export.OrderMiddlewareCBDExport newOrderMiddlewareJsfService = this.getOrderMiddlewareCBDExport();

		// 请求对象
		OrderQueryRequest request = new OrderQueryRequest();
		// 通用业务参数
		ComponentBase componentBase = new ComponentBase();
		componentBase.setRegion(region_JD_LOGISTICS);
		// 必传字段，测试环境或者物理机部署的应用无法自动获取appname，可手动传入，默认取jvm的启动参数deploy.app.name
		componentBase.setAppName(Constants.UMP_APP_NAME_DMSWEB);
		List<String> querys = OrderServiceHelper.getQueryKeys();
		// 赋值
		request.setComponentBase(componentBase);
		request.setBizType("1");
		request.setOrderId(orderId);
		request.setQueryKeys(querys);

		OrderQueryResponse response = newOrderMiddlewareJsfService.getOrderData(request);
		if (response.isSuccess()) {
			Map<String, Object> dataMap = response.getDataMap();
			if (dataMap != null && dataMap.size() > 0){
				return  transForm(dataMap);
			}
		}
		return null;
	}

	/**
	 * 国际化订单jsf 对象
	 * @return
	 */
	private  com.jd.jdorders.component.export.OrderMiddlewareCBDExport getOrderMiddlewareCBDExport(){
		com.jd.jdorders.component.export.OrderMiddlewareCBDExport newOrderMiddlewareJsfService = (com.jd.jdorders.component.export.OrderMiddlewareCBDExport) SpringHelper
				.getBean("newOrderMiddlewareJsfService");
		return  newOrderMiddlewareJsfService;
	}


	// 普通订单对象
	private InternationOrderDto transForm(Map<String, Object> dataMap) {
		Long  id = (Long) dataMap.get(FieldKeyEnum.M_ID.getFieldName());
		Integer idCompanyBranch = (Integer) dataMap.get(FieldKeyEnum.M_OPRATOR.getFieldName());
		Integer deliveryCenterId = (Integer)dataMap.get(FieldKeyEnum.M_DELIVERYCENTERID.getFieldName());
		String customerName = (String) dataMap.get(FieldKeyEnum.M_CUSTOMERNAME.getFieldName());
		Integer orderType = (Integer) dataMap.get(FieldKeyEnum.M_ORDERTYPE.getFieldName());
		Integer storeId = (Integer) dataMap.get(FieldKeyEnum.M_STOREID.getFieldName());
		String sendPay = (String) dataMap.get(FieldKeyEnum.M_SENDPAY.getFieldName());
		Integer province = (Integer) dataMap.get(FieldKeyEnum.M_PROVINCE.getFieldName());
		Integer city = (Integer) dataMap.get(FieldKeyEnum.M_CITY.getFieldName());

		InternationOrderDto internationOrderDto = new InternationOrderDto();
		internationOrderDto.setId(id);
		internationOrderDto.setIdCompanyBranch(idCompanyBranch);
		internationOrderDto.setDeliveryCenterID(deliveryCenterId);
		internationOrderDto.setCustomerName(customerName);
		internationOrderDto.setOrderType(orderType);
		internationOrderDto.setStoreId(storeId);
		internationOrderDto.setCity(city);
		internationOrderDto.setProvince(province);
		internationOrderDto.setSendPay(sendPay);
		return internationOrderDto;
	}


	@JProfiler(jKey = "DMS.WEB.OrderWebService.getHistoryOrder", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public jd.oom.client.orderfile.Order getHistoryOrder(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");
		
		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));
		if (orderArchiveInfo != null){
			return orderArchiveInfo.getOrder();
		}
		
		return null;
	}

    @JProfiler(jKey = "DMS.WEB.OrderWebService.getWaybillByOrderId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Waybill getWaybillByOrderId(long orderId) {
		com.jd.ioms.jsf.export.OrderMiddlewareJSFService orderMiddlewareJSFService = (com.jd.ioms.jsf.export.OrderMiddlewareJSFService) SpringHelper
				.getBean("orderMiddlewareJSFService");
		ExportResult<Order> exportResult = orderMiddlewareJSFService.getOrderById(orderId, false, OrderServiceHelper.getWaybillFlag());
		if (exportResult != null && exportResult.isSuccess()) {
			Order order = exportResult.getData();
			if (order != null) {
				Waybill waybill = new Waybill();
				waybill.setWaybillCode(String.valueOf(orderId));

				try {
					waybill.setSiteCode(preseparateWaybillManager.getPreseparateSiteId(String.valueOf(orderId)));
					log.info("快生从预分拣获取预分拣站点{}-{}" ,orderId, waybill.getSiteCode());
				} catch (Exception ex) {
					waybill.setSiteCode(order.getPartnerId());
					log.error("快生预分拣接口异常:{}" , orderId, ex);
				}
				waybill.setSiteName(order.getIdPickSiteName());
				waybill.setPaymentType(order.getPaymentType());
				waybill.setSendPay(order.getSendPay());
				if (order.getWeight() != null) {
					waybill.setWeight(order.getWeight().doubleValue());
				}
				waybill.setAddress(order.getAddress());
				waybill.setOrgId(order.getIdCompanyBranch());
				waybill.setStoreId(order.getStoreId());
				waybill.setType(order.getOrderType());
				waybill.setShipmentType(order.getShipmentType());
				waybill.setReceiverTel(order.getPhone());
				waybill.setReceiverMobile(order.getMobile());
				waybill.setReceiverName(order.getCustomerName());
				waybill.setDistributeStoreId(order.getStoreId());
				waybill.setDistributeStoreName(order.getStoreName());
				// region  从redis中获取省市县，若无则从基础资料获取，并插入redis中
				waybill.setProvinceNameId(order.getProvince());
				waybill.setProvinceName(GetCityName(waybill.getProvinceNameId()));
				waybill.setCityNameId(order.getCity());
				waybill.setCityName(GetCityName(waybill.getCityNameId()));
				waybill.setCountryNameId(order.getCounty());
				waybill.setCountryName(GetCityName(waybill.getCountryNameId()));
				// endregion
				return waybill;
			}
		}
		return null;
	}

	private String GetCityName(Integer id) {
		String temp = null;
		if (id != null) {
			Assort ass = baseService.getAssortById(id);
			if (ass != null)
				temp = ass.getAssName();
		}
		return temp;
	}

}