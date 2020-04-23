package ld;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SearchOrganizationOtherManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.test.OrderDataUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReverseReceiveNotifyStockServiceTest {

    private Long ORDER_CODE = 123456789L;

    @InjectMocks
    ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private OrderWebService orderWebService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderBankService orderBankService;

    @Mock
    protected UccPropertyConfiguration uccPropertyConfiguration;

    @Mock
    private DefaultJMQProducer wmsStockChuGuanMQ;

    @Mock
    private StockExportManager stockExportManager;


    @Mock
    private SearchOrganizationOtherManager searchOrganizationOtherManager;

    @Before
    public void before() {

        when(baseMajorManager.getBaseSiteBySiteId(anyInt()))
                .thenReturn(JsonHelper.fromJson(SITEJSON, BaseStaffSiteOrgDto.class));

        when(orderWebService.getOrder(anyLong())).thenReturn(OrderDataUtil.DX_ORDER);

        when(productService.getOrderProducts(anyLong())).thenReturn(JsonHelper.jsonToList(PRODUCTS, Product.class));

        when(stockExportManager.queryByWaybillCode(anyString())).thenReturn(JsonHelper.fromJson(KUGUANJSON, KuGuanDomain.class));

        when(orderBankService.getOrderBankResponse(anyString())).thenReturn(JsonHelper.fromJson(OrderBankResponseJSON, OrderBankResponse.class));

        when(searchOrganizationOtherManager.findFinancialOrg(any(SendpayOrdertype.class))).thenReturn(new Organization());
    }

    @Test
    public void test() {

        new StockYJHXTest.SystemLogUtilMockUp();

        try {
            Boolean result = reverseReceiveNotifyStockService.nodifyStock(ORDER_CODE);
            Assert.isTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
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

    private static String PRODUCTS = "[{\n" +
            "  \"PRODUCT_ACCESSORY_PREFIX\": \"String\",\n" +
            "  \"DEFAULT_PRODUCT_ACCESSORY_ID\": \"String\",\n" +
            "  \"DEFAULT_PRODUCT_ACCESSORY_QUANTITY\": 0,\n" +
            "  \"productId\": \"123\",\n" +
            "  \"name\": \"String\",\n" +
            "  \"quantity\": 0,\n" +
            "  \"price\": 1,\n" +

            "  \"skuId\": \"0\"\n" +
            "}]";

    private static String SITEJSON = "{\n" +
            "  \"serialVersionUID\": \"0\",\n" +
            "  \"sId\": 0,\n" +
            "  \"staffName\": \"String\",\n" +
            "  \"staffNo\": 0,\n" +
            "  \"role\": 0,\n" +
            "  \"phone\": \"String\",\n" +
            "  \"mail\": \"String\",\n" +
            "  \"siteName\": \"String\",\n" +
            "  \"siteCode\": 0,\n" +
            "  \"siteType\": 0,\n" +
            "  \"subType\": 0,\n" +
            "  \"sitePhone\": \"String\",\n" +
            "  \"targetType\": 0,\n" +
            "  \"orgId\": 0,\n" +
            "  \"address\": \"String\",\n" +
            "  \"orgName\": \"String\",\n" +
            "  \"dmsSiteCode\": \"String\",\n" +
            "  \"customCode\": \"String\",\n" +
            "  \"storeCode\": \"String\",\n" +
            "  \"provinceId\": 0,\n" +
            "  \"provinceName\": \"String\",\n" +
            "  \"cityId\": 0,\n" +
            "  \"cityName\": \"String\",\n" +
            "  \"countryId\": 0,\n" +
            "  \"countryName\": \"String\",\n" +
            "  \"countrySideId\": 0,\n" +
            "  \"countrySideName\": \"String\",\n" +
            "  \"dmsId\": 0,\n" +
            "  \"dmsName\": \"String\",\n" +
            "  \"areaId\": \"0\",\n" +
            "  \"areaName\": \"String\",\n" +
            "  \"parentSiteCode\": 0,\n" +
            "  \"parentSiteName\": \"String\",\n" +
            "  \"subTypes\": \"String\",\n" +
            "  \"memo\": \"String\",\n" +
            "  \"traderTypeEbs\": 0,\n" +
            "  \"accountingOrg\": 0,\n" +
            "  \"airTransport\": \"String\",\n" +
            "  \"staffPhoto\": \"String\",\n" +
            "  \"bigStaffPhoto\": \"String\",\n" +
            "  \"siteBusinessType\": 0,\n" +
            "  \"isMiniWarehouse\": 0,\n" +
            "  \"dispatchRange\": 0.0,\n" +
            "  \"fixTime\": 0,\n" +
            "  \"subSiteCode\": 0,\n" +
            "  \"subSiteName\": \"String\",\n" +
            "  \"dmsShortName\": \"String\",\n" +
            "  \"yn\": 0,\n" +
            "  \"createTime\": \"2019-11-22 14:51:37\",\n" +
            "  \"updateTime\": \"2019-11-22 14:51:37\",\n" +
            "  \"operateState\": 0,\n" +
            "  \"siteNamePym\": \"String\",\n" +
            "  \"siteMail\": \"String\",\n" +
            "  \"siteContact\": \"String\",\n" +
            "  \"businessHoursStart\": \"String\",\n" +
            "  \"businessHoursEnd\": \"String\",\n" +
            "  \"accountNumber\": \"String\",\n" +
            "  \"roleName\": \"String\",\n" +
            "  \"countyId\": 0,\n" +
            "  \"townId\": 0,\n" +
            "  \"connector\": \"String\",\n" +
            "  \"telephone\": \"String\",\n" +
            "  \"outerOrderProduct\": \"String\",\n" +
            "  \"orderTimeEffective\": \"String\",\n" +
            "  \"isResign\": 0,\n" +
            "  \"userType\": 0,\n" +
            "  \"erp\": \"String\",\n" +
            "  \"jdAccount\": \"String\",\n" +
            "  \"thirdType\": 0,\n" +
            "  \"route\": \"String\",\n" +
            "  \"userCode\": \"String\",\n" +
            "  \"provinceCompanyCode\": \"String\",\n" +
            "  \"provinceCompanyName\": \"String\",\n" +
            "  \"areaCode\": \"String\",\n" +
            "  \"partitionCode\": \"String\",\n" +
            "  \"partitionName\": \"String\",\n" +
            "  \"staffSign\": \"String\",\n" +
            "  \"siteSign\": \"String\",\n" +
            "  \"organCode\": \"String\",\n" +
            "  \"mobilePhone1\": \"String\",\n" +
            "  \"mobilePhone2\": \"String\",\n" +
            "  \"thirdWebsite\": \"String\",\n" +
            "  \"expand1\": \"String\",\n" +
            "  \"expand2\": \"String\",\n" +
            "  \"displaySiteName\": \"String\"\n" +
            "}";

    private String KUGUANJSON = "{\n" +
            "  \"serialVersionUID\": \"0\",\n" +
            "  \"ddlType\": \"String\",\n" +
            "  \"waybillCode\": \"String\",\n" +
            "  \"lblKdanhao\": \"String\",\n" +
            "  \"lKdanhao\": \"String\",\n" +
            "  \"lblWay\": \"出库\",\n" +
            "  \"lblType\": \"放货\",\n" +
            "  \"lblOtherWay\": \"String\",\n" +
            "  \"lblJingban\": \"String\",\n" +
            "  \"lblDate\": \"String\",\n" +
            "  \"lblFrom\": \"String\",\n" +
            "  \"lblKuanx\": \"String\",\n" +
            "  \"lblYun\": \"String\",\n" +
            "  \"lblYouhui\": \"String\",\n" +
            "  \"lblOther\": \"0\",\n" +
            "  \"lblZjine\": \"String\",\n" +
            "  \"lblCdanhao1\": \"String\",\n" +
            "  \"lblOrderid\": \"String\",\n" +
            "  \"lblCdanhao\": \"String\",\n" +
            "  \"lblWareId\": \"String\",\n" +
            "  \"lblWare\": \"String\",\n" +
            "  \"lblNum\": \"String\",\n" +
            "  \"lblPrice\": \"String\",\n" +
            "  \"lbljine\": \"String\",\n" +
            "  \"lblOrg\": \"String\",\n" +
            "  \"lblStock\": \"String\",\n" +
            "  \"lblLuru\": \"String\",\n" +
            "  \"lblStation\": \"String\",\n" +
            "  \"lblSure\": \"String\",\n" +
            "  \"lblYdanhao\": \"String\",\n" +
            "  \"lblRemark\": \"String\",\n" +
            "  \"lblstatistics\": \"String\",\n" +
            "  \"typeId\": 0\n" +
            "}";

    private String OrderBankResponseJSON = "{\n" +
            "  \"discount\": \"1\",\n" +
            "  \"shouldPay\": \"1\"" +
            "}";
}
