package ld;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SearchOrganizationOtherManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;

import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.test.KuGuanDataUtil;
import com.jd.bluedragon.distribution.test.OrderDataUtil;
import com.jd.bluedragon.distribution.test.ProductDataUtil;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.stock.iwms.export.param.StockVOParam;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockYJHXTest {

    private Logger logger = LoggerFactory.getLogger(StockYJHXTest.class);

    @InjectMocks
    private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

    @Mock
    private OrderWebService orderWebService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderBankService orderBankService;

    @Mock
    private DefaultJMQProducer wmsStockChuGuanMQ;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private StockExportManager stockExportManager;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private SearchOrganizationOtherManager searchOrganizationOtherManager;

    /*@Mocked
    SystemLogUtil systemLogUtil;
*/


    @Before
    public void initMocks() throws Exception {
        //初始化当前测试类所有@Mock注解模拟对象  和 MockitoJUnitRunner 用一个就好
        //MockitoAnnotations.initMocks(this);
        //mock静态方法
        new SystemLogUtilMockUp();


        //mock 订单中间件接口
        when(orderWebService.getOrder(anyLong())).thenReturn(OrderDataUtil.YJHX_ORDER);
        //mock 商品接口
        when(productService.getOrderProducts(anyLong())).thenReturn(ProductDataUtil.NOMAL_PRODUCTS);
        //mock 接口
        when(stockExportManager.queryByWaybillCode(anyString())).thenReturn(KuGuanDataUtil.XKZF_TYPE);
        //mock 接口
        when(orderBankService.getOrderBankResponse(anyString())).thenReturn(new OrderBankResponse(){
            @Override
            public BigDecimal getDiscount() {
                return BigDecimal.valueOf(11.1);
            }

            @Override
            public BigDecimal getShouldPay() {
                return BigDecimal.valueOf(22.2);
            }
        });

        when(searchOrganizationOtherManager.findFinancialOrg(any(SendpayOrdertype.class))).thenReturn(new Organization(){
            @Override
            public Integer getOrgId() {
                return 7;
            }
        });

        //mock 接口 用来校验修改逻辑使用
        when(stockExportManager.insertStockVirtualIntOut(any(StockVOParam.class),any(StockVOParam.class))).thenAnswer(
                new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                StockVOParam stockVOParam1 = invocation.getArgument(0);
                StockVOParam stockVOParam2 = invocation.getArgument(1);
                if(stockVOParam1.getQtfs().equals("trade-in") && stockVOParam2.getQtfs().equals("trade-in"))
                    return 1L;

                return 0L;
            }
        });
    }



    @Test
    public void nodifyStockTest(){
        try {

            Boolean result = reverseReceiveNotifyStockService.nodifyStock(1L);
            Assert.isTrue(result);

        } catch (Exception e) {
            logger.error("nodifyStockTest fail",e);
        }

    }


    //Jmock 方式
    public static class SystemLogUtilMockUp extends MockUp<SystemLogUtil> {
        // Mock静态方法
        @mockit.Mock
        public static int log(SystemLog systemLog) {
            return 1;
        }

    }



}
