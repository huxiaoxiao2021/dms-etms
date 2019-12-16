package com.jd.bluedragon.distribution.rest.product;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ProductResponse;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductService productService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @GET
    @Path("/order/waybillAndGoods/{orderId}")
    public ProductResponse getwaybillAndGoods(@PathParam("orderId") String  orderId){
        if (orderId == null) {
            return this.paramError();
        }

        this.log.info("调用运单接口获取订单商品明细, 订单号：{}", orderId);

        Waybill waybill = waybillCommonService.findWaybillAndGoods(orderId);
        if(waybill == null){
            return this.orderNotFound();
        }
        List<Product> products = waybill.getProList();
        if (products == null || products.isEmpty()) {
            return this.orderNotFound();
        }

        return this.ok(products);
    }

    @GET
    @Path("/order/products/{codeStr}")
    public ProductResponse getOrderProducts(@PathParam("codeStr") String codeStr) {
        if (codeStr == null) {
            return this.paramError();
        }

        Long orderId = waybillCommonService.findOrderIdByWaybillCode(codeStr);
        if(orderId == null){
            return paramError();
        }
        this.log.info("获取订单商品详情, 订单号：{}", orderId);

        List<Product> products = this.productService.getOrderProducts(orderId);
        if (products == null || products.isEmpty()) {
            return this.orderNotFound();
        }

        return this.ok(products);
    }

    @GET
    @Path("/pickware/products/{code}")
    public ProductResponse getPickwareProducts(@PathParam("code") String code) {
        if (code == null) {
            return this.paramError();
        }

        this.log.info("获取取件单商品详情, 取件单面单号：{}", code);

        List<Product> products = this.productService.getPickwareProductds(code);
        if (products == null || products.isEmpty()) {
            return this.orderNotFound();
        }

        return this.ok(products);
    }

    private ProductResponse ok(List<Product> products) {
        ProductResponse response = new ProductResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        List<com.jd.bluedragon.distribution.api.response.Product> bProducts = new ArrayList<com.jd.bluedragon.distribution.api.response.Product>();
        for (Product aProduct : products) {
            com.jd.bluedragon.distribution.api.response.Product bProduct = new com.jd.bluedragon.distribution.api.response.Product();
            BeanUtils.copyProperties(aProduct, bProduct);
        	bProduct.setProductPrice(bProduct.getPrice());
            bProducts.add(bProduct);
        }

        response.setData(bProducts);

        return response;
    }

    private ProductResponse orderNotFound() {
        ProductResponse response = new ProductResponse();
        response.setCode(ProductResponse.CODE_ORDER_NOT_FOUND);
        response.setMessage(ProductResponse.MESSAGE_ORDER_NOT_FOUND);
        return response;
    }

    private ProductResponse paramError() {
        ProductResponse response = new ProductResponse();
        response.setCode(JdResponse.CODE_PARAM_ERROR);
        response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        return response;
    }

}
