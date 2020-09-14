package com.jd.bluedragon.distribution.rest.product;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.message.MessageDto;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.LossProductResponse;
import com.jd.bluedragon.distribution.api.response.ProductResponse;
import com.jd.bluedragon.distribution.consumer.reverse.LossOrderConsumer;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/services")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class LossProductResource {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;
	
	@Autowired
	private LossOrderConsumer lossOrderConsumer;

	@Autowired
	@Qualifier("waybillCommonService")
	private WaybillCommonService waybillCommonService;

	@POST
	@Deprecated
	@Path("/order/loss")
    @JProfiler(jKey = "DMS.WEB.LossProductResource.add", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResponse add(MessageDto message) {
		Message newMessage = new Message();
		newMessage.setText(message.getContent());
		this.lossOrderConsumer.consume(newMessage);

		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}
	
	@POST
	@Path("/lossProduct/add")
    @JProfiler(jKey = "DMS.WEB.LossProductResource.add", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResponse add(Message message) {
		this.lossOrderConsumer.consume(message);

		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}

	@GET
	@Path("/order/product/quantity/{codeStr}")
    @JProfiler(jKey = "DMS.WEB.LossProductResource.getOrderProductQuantity", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public LossProductResponse getOrderProductQuantity(@PathParam("codeStr") String codeStr) {
		if (codeStr == null) {
			return paramError();
		}
		Long orderId = waybillCommonService.findOrderIdByWaybillCode(codeStr);
		if(orderId == null){
			return paramError();
		}
		this.log.info("获取订单商品详情, 订单号：{}", orderId);

		List<Product> actualProducts = this.productService.getOrderProducts(orderId);
		if (actualProducts == null || actualProducts.isEmpty()) {
			return orderNotFound();
		}

		return ok(actualProducts);
	}

	@GET
	@Path("/order/loss/products/{codeStr}")
    @JProfiler(jKey = "DMS.WEB.LossProductResource.getLossOrderProducts", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public LossProductResponse getLossOrderProducts(@PathParam("codeStr") String codeStr) {
		if (codeStr == null) {
			return paramError();
		}
		Long orderId = waybillCommonService.findOrderIdByWaybillCode(codeStr);
		if(orderId == null){
			return paramError();
		}
		this.log.info("获取订单商品详情及报损详情, 订单号：{}", orderId);

		List<Product> actualProducts = this.productService.getOrderProducts(orderId);
		List<Product> lossProducts = this.productService.getLossOrderProducts(orderId);

		if (actualProducts == null || actualProducts.isEmpty()) {
			return orderNotFound();
		}

		return ok(actualProducts, lossProducts);
	}

	private LossProductResponse ok(List<Product> actualProducts) {
		LossProductResponse response = new LossProductResponse();
		response.setCode(JdResponse.CODE_OK);
		response.setMessage("OK");

		response.setActualQuantity(getProductQuantity(actualProducts));
		response.setActualProducts(parse(actualProducts));

		return response;
	}

	private LossProductResponse ok(List<Product> actualProducts, List<Product> lossProducts) {
		LossProductResponse response = new LossProductResponse();
		response.setCode(JdResponse.CODE_OK);
		response.setMessage("OK");

		response.setActualQuantity(getProductQuantity(actualProducts));
		response.setLossQuantity(getProductQuantity(lossProducts));
		response.setReturnQuantity(Integer.valueOf(getProductQuantity(actualProducts).intValue()
		        - getProductQuantity(lossProducts).intValue()));

		response.setActualProducts(parse(actualProducts));
		response.setLossProducts(parse(lossProducts));

		return response;
	}

	private List<com.jd.bluedragon.distribution.api.response.Product> parse(List<Product> products) {
		List<com.jd.bluedragon.distribution.api.response.Product> bProducts = new ArrayList<com.jd.bluedragon.distribution.api.response.Product>();

		for (com.jd.bluedragon.distribution.product.domain.Product aProduct : products) {
			com.jd.bluedragon.distribution.api.response.Product bProduct = new com.jd.bluedragon.distribution.api.response.Product();
			BeanUtils.copyProperties(aProduct, bProduct);
			bProduct.setProductPrice(bProduct.getPrice());
			bProducts.add(bProduct);
		}

		return bProducts;
	}

	private Integer getProductQuantity(
	        List<com.jd.bluedragon.distribution.product.domain.Product> products) {
		Integer quantity = Integer.valueOf(0);
		for (com.jd.bluedragon.distribution.product.domain.Product aProduct : products) {
			quantity = Integer.valueOf(quantity.intValue() + aProduct.getQuantity().intValue());
		}
		return quantity;
	}

	private LossProductResponse orderNotFound() {
		LossProductResponse response = new LossProductResponse();
		response.setCode(ProductResponse.CODE_ORDER_NOT_FOUND);
		response.setMessage("无订单信息");
		return response;
	}

	private LossProductResponse paramError() {
		LossProductResponse response = new LossProductResponse();
		response.setCode(JdResponse.CODE_PARAM_ERROR);
		response.setMessage("参数错误");
		return response;
	}
}