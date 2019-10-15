package com.jd.bluedragon.distribution.reverse.service;

import com.google.common.collect.Lists;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.bluedragon.distribution.product.domain.Product
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/10/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class ReverseReceiveNotifyStockServiceTest {

    @Autowired
    private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

    @Test
    public void testinsertNewChuguan()throws Exception{
//        Long waybillCode, boolean isOldForNewType,Order order, List< Product > products,
//                Integer payType
        Order order = new Order();
        order.setDeliveryCenterID(3);//DcId 源配送中心Id	3;上海(3)
        order.setStoreId(49);//Sid 仓ID	49;49号仓(49)
        order.setIdCompanyBranch(3);//DcId 源配送中心Id	3;上海(3)
        order.setTotalFee(new BigDecimal(3333));
        List<Product> products = Lists.newArrayList();
        Product product = new Product();
        product.setSkuId(1414552);
        product.setPrice(new BigDecimal(111));
        product.setQuantity(2222);
        products.add(product);
        reverseReceiveNotifyStockService.insertNewChuguan(1111222444111L,true,order,products,2);
    }
}
