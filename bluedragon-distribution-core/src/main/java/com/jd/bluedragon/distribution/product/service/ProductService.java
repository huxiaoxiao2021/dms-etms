package com.jd.bluedragon.distribution.product.service;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BigDecimalHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.loss.client.BlueDragonWebService;
import com.jd.loss.client.LossProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("productService")
public class ProductService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OrderWebService orderWebService;
	
	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;
	
	@Autowired
	private BlueDragonWebService lossWebService;
	
	@Autowired
	private WaybillService waybillService;

	@Autowired
	private WaybillCommonService waybillCommonService;

	public List<Product> getProductsByWaybillCode(String waybillCode){
		List<Product> products = new ArrayList<Product>();
		BigWaybillDto bigWaybillDto =  waybillService.getWaybillProduct(waybillCode);
		List<Goods> goodses = bigWaybillDto.getGoodsList();
		for(Goods goods:goodses){
			Product product = new Product();
			product.setProductId(String.valueOf(goods.getGoodId()));
			product.setName(goods.getGoodName());
			product.setQuantity(goods.getGoodCount());
			product.setPrice(BigDecimalHelper.toBigDecimal(goods.getGoodPrice()));
			products.add(product);
		}
		return products;
	}
	
	public List<Product> getOrderProducts(Long orderId) {
		List<com.jd.ioms.jsf.export.domain.OrderDetail> orderDetails = this.orderWebService.getOrderDetailById(orderId);
		List<Product> products = new ArrayList<Product>();
		
		if (orderDetails == null || orderDetails.isEmpty()) {
			orderDetails = this.getOrderDetailByWaybillMiddleware(orderId);
		}

		if (orderDetails == null || orderDetails.isEmpty()) {
			orderDetails = this.getHistoryOrderDetailByOrderMiddleware(orderId);
		}
		
		for (com.jd.ioms.jsf.export.domain.OrderDetail orderDetail : orderDetails) {
			Product product = new Product();
			
			product.setName(StringHelper.getStringValue(orderDetail.getName()));
			product.setQuantity(orderDetail.getNum());
			product.setProductId(String.valueOf(orderDetail.getProductId()));
			product.setPrice(orderDetail.getPrice());
			if(orderDetail.getSkuId() != null){
				product.setSkuId(orderDetail.getSkuId());
			}
			this.log.info("订单号：{}, 商品详情：{}" ,orderId, product.toString());
			
			products.add(product);
		}
		
		return products;
	}
	
	private List<com.jd.ioms.jsf.export.domain.OrderDetail> getOrderDetailByWaybillMiddleware(Long orderId) {
		List<com.jd.ioms.jsf.export.domain.OrderDetail> orderDetails = new ArrayList<com.jd.ioms.jsf.export.domain.OrderDetail>();
		
		BigWaybillDto waybillDto = this.waybillService.getWaybillProduct(String.valueOf(orderId));
		
		if (waybillDto.getGoodsList() != null) {
			for (Goods goods : waybillDto.getGoodsList()) {
				com.jd.ioms.jsf.export.domain.OrderDetail orderDetail = new com.jd.ioms.jsf.export.domain.OrderDetail();
				orderDetail.setName(goods.getGoodName());
				orderDetail.setProductId(new Integer(goods.getSku()));
				orderDetail.setNum(goods.getGoodCount());
				orderDetail.setPrice(BigDecimalHelper.toBigDecimal(goods.getGoodPrice()));

				//这里加一个sku的赋值 不确定是不是goods.getSKU
//				orderDetail.setSkuId(goods.getSku());

				orderDetails.add(orderDetail);
			}
		}
		
		return orderDetails;
	}
	
	private List<com.jd.ioms.jsf.export.domain.OrderDetail> getHistoryOrderDetailByOrderMiddleware(Long orderId) {
		List<com.jd.ioms.jsf.export.domain.OrderDetail> orderDetails = new ArrayList<com.jd.ioms.jsf.export.domain.OrderDetail>();
		List<jd.oom.client.orderfile.OrderDetail> orderFileOrderDetails = this.orderWebService
				.getHistoryOrderDetailById(orderId.intValue());
		
		if (orderFileOrderDetails != null) {
			for (jd.oom.client.orderfile.OrderDetail orderFileOrderDetail : orderFileOrderDetails) {
				com.jd.ioms.jsf.export.domain.OrderDetail orderDetail = new com.jd.ioms.jsf.export.domain.OrderDetail();
				orderDetail.setName(orderFileOrderDetail.getName());
				orderDetail.setProductId(orderFileOrderDetail.getProductId());
				orderDetail.setNum(orderFileOrderDetail.getNum());
				orderDetail.setPrice(orderFileOrderDetail.getPrice());
				orderDetails.add(orderDetail);
			}
		}
		
		return orderDetails;
	}
	
	public List<Product> getPickwareProductds(String code) {
		BaseEntity<PickupTask> pickware = this.waybillPickupTaskApi.getDataBySfCode(code);
		if (pickware == null || pickware.getData() == null) {
			return Collections.emptyList();
		}
		
		List<Product> products = new ArrayList<Product>();
		
		Product product0 = new Product(); // 取件单内商品
		product0.setName(StringHelper.getStringValue(pickware.getData().getProductName()));
		product0.setQuantity(pickware.getData().getAmount());
		product0.setProductId(pickware.getData().getProductCode());
		
		products.add(product0);
		
		this.log.info("取件单面单号：{}, 商品详情：{}" ,code, product0.toString());
		
		if (StringHelper.isNotEmpty(pickware.getData().getAttaches())) {
			Product product1 = new Product(); // 取件单内商品附件
			product1.setName(Product.PRODUCT_ACCESSORY_PREFIX
					+ StringHelper.getStringValue(pickware.getData().getAttaches()));
			product1.setQuantity(Product.DEFAULT_PRODUCT_ACCESSORY_QUANTITY);
			product1.setProductId(Product.DEFAULT_PRODUCT_ACCESSORY_ID);
			products.add(product1);
			
			this.log.info("取件单面单号：{}, 商品附件详情：{}" ,code, product1.toString());
		}
		
		return products;
	}
	
	public List<Product> getLossOrderProducts(Long orderId) {
		List<LossProduct> lossProducts = this.lossWebService.getLossProductByOrderId(String
				.valueOf(orderId));
		
		if (lossProducts == null) {
			return Collections.emptyList();
		}
		
		List<Product> products = new ArrayList<Product>();
		for (LossProduct lossProduct : lossProducts) {
			Product product = new Product();
			
			product.setName(StringHelper.getStringValue(lossProduct.getName()));
			product.setQuantity(lossProduct.getLossCount());
			product.setProductId(String.valueOf(lossProduct.getSku()));
			product.setPrice(lossProduct.getPrice());
			
			this.log.info("订单号：{}, 商品详情：{}" ,orderId, product.toString());
			
			products.add(product);
		}
		
		return products;
	}

	/*
	* 获取报损后的商品数量
	* */
	public Integer getWaybillLossResult(String waybillCode) {
		try {

			Long orderId = waybillCommonService.findOrderIdByWaybillCode(waybillCode);
			if(orderId == null){
				return -1;
			}
			log.info("获取订单商品详情及报损详情, 订单号：{}", orderId);

			List<Product> actualProducts = this.getOrderProducts(orderId);
			List<Product> lossProducts = this.getLossOrderProducts(orderId);

			if (actualProducts == null || actualProducts.isEmpty()) {
				return -1;
			}

			Integer actualQuantity = getProductQuantity(actualProducts);
			Integer LossQuantity = getProductQuantity(lossProducts);
			Integer returnQuantity = getProductQuantity(actualProducts) - getProductQuantity(lossProducts);

			if (LossQuantity == null || LossQuantity == 0) {
				return -1;
			}
			if (LossQuantity < actualQuantity) {
				return 0;
			}
			if (actualQuantity.equals(returnQuantity)) {
				return 1;
			}
		} catch (Exception e) {
			log.error("获取报丢订单商品明细失败"+waybillCode, e);
		}
		return -1;
	}

	private Integer getProductQuantity(List<Product> products) {
		Integer quantity = 0;
		for (Product aProduct : products) {
			quantity += aProduct.getQuantity();
		}
		return quantity;
	}
	
}
