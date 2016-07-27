package com.jd.bluedragon.distribution.product.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jd.oom.client.clientbean.OrderDetail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Service("productService")
public class ProductService {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private OrderWebService orderWebService;
	
	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;
	
	@Autowired
	private BlueDragonWebService lossWebService;
	
	@Autowired
	private WaybillService waybillService;
	
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
			product.setSkuId(orderDetail.getSkuId());

			this.logger.info("订单号：" + orderId + ", 商品详情：" + product.toString());
			
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
		
		this.logger.info("取件单面单号：" + code + ", 商品详情：" + product0.toString());
		
		if (StringHelper.isNotEmpty(pickware.getData().getAttaches())) {
			Product product1 = new Product(); // 取件单内商品附件
			product1.setName(Product.PRODUCT_ACCESSORY_PREFIX
					+ StringHelper.getStringValue(pickware.getData().getAttaches()));
			product1.setQuantity(Product.DEFAULT_PRODUCT_ACCESSORY_QUANTITY);
			product1.setProductId(Product.DEFAULT_PRODUCT_ACCESSORY_ID);
			products.add(product1);
			
			this.logger.info("取件单面单号：" + code + ", 商品附件详情：" + product1.toString());
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
			
			this.logger.info("订单号：" + orderId + ", 商品详情：" + product.toString());
			
			products.add(product);
		}
		
		return products;
	}
	
}
