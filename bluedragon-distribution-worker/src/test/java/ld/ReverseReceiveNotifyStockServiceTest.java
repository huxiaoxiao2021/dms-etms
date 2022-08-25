package ld;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.util.Date;

import com.google.common.collect.Lists;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.order.domain.InternationOrderDto;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.test.OrderDataUtil;
import com.jd.bluedragon.utils.ConstantEnums;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.ipc.csa.model.AllotRequestDetail;
import com.jd.ipc.csa.model.AllotResponseDetail;
import com.jd.ipc.csa.model.AllotScenarioEnum;
import com.jd.ipc.csa.model.DimAllotResult;
import com.jd.jdorders.component.vo.request.OrderQueryRequest;
import com.jd.jdorders.component.vo.response.OrderQueryResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.stock.iwms.export.vo.ChuguanDetailVo;
import com.jd.stock.iwms.export.vo.ChuguanVo;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
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
    private GeneralStockAllotOutInterfaceManager generalStockAllotOutInterfaceManager;

    @Mock
    private ProductService productService;

    @Mock
    private com.jd.jdorders.component.export.OrderMiddlewareCBDExport  newOrderMiddlewareJsfService;

    @Mock
    private OrderBankService orderBankService;

    @Mock
    protected UccPropertyConfiguration uccPropertyConfiguration;

    @Mock
    private DefaultJMQProducer wmsStockChuGuanMQ;

    @Mock
    private StockExportManager stockExportManager;

    @Mock
    private ChuguanExportManager chuguanExportManager;

    @Mock
    private SearchOrganizationOtherManager searchOrganizationOtherManager;

    @Before
    public void before() {

        InternationOrderDto internationOrderDto = new InternationOrderDto();
        internationOrderDto.setOrderType(0);
        when(baseMajorManager.getBaseSiteBySiteId(anyInt()))
                .thenReturn(JsonHelper.fromJson(SITEJSON, BaseStaffSiteOrgDto.class));

        List<ChuguanVo> chuguanVos = Lists.newArrayList();
        extracted(chuguanVos);
        when(chuguanExportManager.queryChuGuan(anyString(),ArgumentMatchers.<ConstantEnums.ChuGuanTypeId>any())).thenReturn(chuguanVos);


        when(orderWebService.getInternationOrder(anyLong())).thenReturn(internationOrderDto);
        List<Product> productList = JsonHelper.jsonToList(PRODUCTS, Product.class);
        when(productService.getInternationProducts(anyLong())).thenReturn(productList);
        KuGuanDomain kuGuanDomain = new  KuGuanDomain();
        kuGuanDomain.setLblWay("出库");
        kuGuanDomain.setLblType("销售");
        when(stockExportManager.queryByWaybillCode(anyString())).thenReturn(kuGuanDomain);
        when(uccPropertyConfiguration.isChuguanPurchaseAndSaleSwitch()).thenReturn(true);
        List<AllotResponseDetail> allotResponseDetailList = Lists.newArrayList();
        AllotResponseDetail allotResponseDetail = new AllotResponseDetail();
        allotResponseDetail.setSkuId(0L);
        allotResponseDetail.setDcId(0);
        allotResponseDetail.setStoreId(0);
        allotResponseDetail.setAllotQty(0);
        allotResponseDetail.setRowKey("");
        List<DimAllotResult> dimAllotResultList = Lists.newArrayList();
        DimAllotResult dimAllotResult = new DimAllotResult();
        dimAllotResult.setDimName("");
        dimAllotResult.setDimValue("121-1212");
        dimAllotResult.setAllotQty(0);
        dimAllotResult.setSalesType("");
        DimAllotResult dimAllotResult2 = new DimAllotResult();
        dimAllotResult2.setDimName("");
        dimAllotResult2.setDimValue("121-1212");
        dimAllotResult2.setAllotQty(0);
        dimAllotResult2.setSalesType("");
        dimAllotResultList.add(dimAllotResult);
        dimAllotResultList.add(dimAllotResult2);
        allotResponseDetail.setDimAllotResultList(dimAllotResultList);
        allotResponseDetailList.add(allotResponseDetail);
        when(generalStockAllotOutInterfaceManager.batchAllotStock(ArgumentMatchers.<AllotRequestDetail>anyList(),ArgumentMatchers.<AllotScenarioEnum>any(),anyString(),anyBoolean(),anyString())).thenReturn(allotResponseDetailList);



        when(chuguanExportManager.queryByWaybillCode(anyString())).thenReturn(kuGuanDomain);
        when(orderBankService.getOrderBankResponse(anyString())).thenReturn(JsonHelper.fromJson(OrderBankResponseJSON, OrderBankResponse.class));

        when(searchOrganizationOtherManager.findFinancialOrg(any(SendpayOrdertype.class))).thenReturn(new Organization());

        when(newOrderMiddlewareJsfService.getOrderData(ArgumentMatchers.<OrderQueryRequest>anyObject())).thenReturn(ArgumentMatchers.<OrderQueryResponse>any());
    }

    private void extracted(List<ChuguanVo> chuguanVos) {
        ChuguanVo chuguanVo1 = new ChuguanVo();
        chuguanVo1.setKdanhao(0L);
        chuguanVo1.setRfId("");
        chuguanVo1.setRfType(0);
        chuguanVo1.setChuruId(0);
        chuguanVo1.setChuru("");
        chuguanVo1.setOrderId(0L);
        chuguanVo1.setYuanDanHao(0);
        chuguanVo1.setCaiGouDanHao(0);
        chuguanVo1.setBusiNo("");
        chuguanVo1.setTypeId(0);
        chuguanVo1.setSubType("");
        chuguanVo1.setExtNo("");
        chuguanVo1.setExtType("");
        chuguanVo1.setDcId(0);
        chuguanVo1.setSid(0);
        chuguanVo1.setToDcId(0);
        chuguanVo1.setToSid(0);
        chuguanVo1.setSiteId(0);
        chuguanVo1.setToSiteId(0);
        chuguanVo1.setSiteType(0);
        chuguanVo1.setToSiteType(0);
        chuguanVo1.setIsAdjust(0);
        chuguanVo1.setIsVirtual(0);
        chuguanVo1.setSystemId("");
        chuguanVo1.setBusinessTime(new Date());
        List<ChuguanDetailVo> chuguanDetailVoList = Lists.newArrayList();
        ChuguanDetailVo chuguanDetailVo = new ChuguanDetailVo();
        chuguanDetailVo.setKdanhao(0L);
        chuguanDetailVo.setSkuId(11111L);
        chuguanDetailVo.setNum(0);
        chuguanDetailVo.setBatchId("");
        chuguanDetailVo.setToBatchId("");
        chuguanDetailVo.setLevel("");
        chuguanDetailVo.setToLevel("");
        chuguanDetailVo.setSkuName("");
        chuguanDetailVo.setJiaGe(new BigDecimal("0"));
        chuguanDetailVo.setZongJinE(new BigDecimal("0"));
        chuguanDetailVo.setDaiMa("");
        chuguanDetailVo.setSucceed(0);
        chuguanDetailVo.setYn(0);
        chuguanDetailVo.setYeJi(new BigDecimal("0"));
        chuguanDetailVo.setCaiGouRenNo(0L);
        chuguanDetailVo.setBiLv(0);
        chuguanDetailVo.setTuiKuChengBen(new BigDecimal("0"));
        chuguanDetailVo.setCaiWuDanHao(0);
        chuguanDetailVo.setInnerTransBillid("");
        chuguanDetailVo.setOriginalProfitLossId("");
        chuguanDetailVo.setProfitLossId("");
        HashMap paramExtMap = Maps.newHashMap();
        chuguanDetailVo.setParamExtMap(paramExtMap);
        chuguanDetailVo.setVendorCode("");
        chuguanDetailVo.setOuId("");
        chuguanDetailVo.setPurchaseChannel("11");
        HashMap SalesModel = Maps.newHashMap();
        SalesModel.put("1","2");
        SalesModel.put("2","2");
        SalesModel.put("2","2");
        chuguanDetailVo.setSalesModel(SalesModel);
        List<String> istriOrderIds = Lists.newArrayList();
        chuguanDetailVo.setDistriOrderIds(istriOrderIds);
        chuguanDetailVoList.add(chuguanDetailVo);
        chuguanVo1.setChuguanDetailVos(chuguanDetailVoList);

        ChuguanVo chuguanVo2 = new ChuguanVo();
        chuguanVo2.setBusinessTime(DateHelper.parseDateTime("2022-07-01 12:00:00"));
        ChuguanVo chuguanVo3 = new ChuguanVo();
        chuguanVo3.setBusinessTime(DateHelper.parseDateTime("2021-07-01 12:00:00"));
        chuguanVos.add(chuguanVo1);
        chuguanVos.add(chuguanVo2);
        chuguanVos.add(chuguanVo3);
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
//
    @Test
    public void test2(){
        orderWebService.getInternationOrder(ORDER_CODE);

    }




    //Jmock 方式
    public static class SystemLogUtilMockUp extends MockUp<SystemLogUtil> {
        // Mock静态方法
        @mockit.Mock
        public static int log(SystemLog systemLog) {
            return 1;
        }

    }

    private final static String PRODUCTS = "[{\"PRODUCT_ACCESSORY_PREFIX\":\"demoData\",\"DEFAULT_PRODUCT_ACCESSORY_ID\":\"demoData\",\"DEFAULT_PRODUCT_ACCESSORY_QUANTITY\":1,\"productId\":\"1111111111\",\"name\":\"demoData\",\"quantity\":1,\"price\":1,\"skuId\":11111,\"profitChannelId\":1}]";

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
            "  \"lblType\": \"销售\",\n" +
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
