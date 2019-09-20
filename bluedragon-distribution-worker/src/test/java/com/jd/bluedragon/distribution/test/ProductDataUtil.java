package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.distribution.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDataUtil {

    public static List<Product> NOMAL_PRODUCTS = new ArrayList<>();
    static{
        Product p1 = new Product();
        p1.setProductId("1001");
        p1.setName("测试商品");
        p1.setQuantity(2);
        p1.setPrice(BigDecimal.ONE);
        p1.setSkuId(12L);

        NOMAL_PRODUCTS.add(p1);

    }
}
