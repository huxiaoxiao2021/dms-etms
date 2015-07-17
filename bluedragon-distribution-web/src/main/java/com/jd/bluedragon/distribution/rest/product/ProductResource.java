package com.jd.bluedragon.distribution.rest.product;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ProductResponse;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ProductResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ProductService productService;

    @GET
    @Path("/order/products/{orderId}")
    @Profiled(tag = "ProductResource.getOrderProducts")
    public ProductResponse getOrderProducts(@PathParam("orderId") Long orderId) {
        if (orderId == null) {
            return this.paramError();
        }
		
        this.logger.info("获取订单商品详情, 订单号：" + orderId);

        List<Product> products = this.productService.getOrderProducts(orderId);
        if (products == null || products.isEmpty()) {
            return this.orderNotFound();
        }

        return this.ok(products);
    }

    @GET
    @Path("/pickware/products/{code}")
    @Profiled(tag = "ProductResource.getPickwareProducts")
    public ProductResponse getPickwareProducts(@PathParam("code") String code) {
        if (code == null) {
            return this.paramError();
        }

        this.logger.info("获取取件单商品详情, 取件单面单号：" + code);

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
