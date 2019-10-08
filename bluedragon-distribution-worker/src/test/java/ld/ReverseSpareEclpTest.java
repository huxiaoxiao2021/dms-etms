package ld;

import com.alibaba.fastjson.JSON;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareEclpImpl;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.test.WaybillDataUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.eclp.isv.domain.GoodsInfo;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/ld/distribution-worker-ld-common.xml")*/
@RunWith(MockitoJUnitRunner.class)
public class ReverseSpareEclpTest {

    @InjectMocks
    ReverseSpareEclpImpl reverseSpareEclp;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private WaybillCommonService waybillCommonService;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private EclpItemManager eclpItemManager;

    @Mock
    private OBCSManager obcsManager;

    @Mock
    private SysConfigService sysConfigService;

    @Mock
    private SendDatailDao sendDetailDao;

    @Mock
    private EclpOpenManager eclpOpenManager;

    @Before
    public void before(){
        //MockitoAnnotations.initMocks(this);

        when(baseMajorManager.getBaseSiteBySiteId(anyInt()))
                .thenReturn(JsonHelper.fromJson(siteJson, BaseStaffSiteOrgDto.class));


        when(obcsManager.getClaimListByClueInfo(anyInt(),anyString()))
                .thenReturn(JsonHelper.fromJson(LPJson, LocalClaimInfoRespDTO.class));

        when(eclpItemManager.getltemBySoNo(anyString()))
                .thenReturn(Arrays.asList(JsonHelper.fromJson(goodJson, ItemInfo[].class)));

        ReflectionTestUtils.setField(reverseSpareEclp, "C2C_DEFT_NO", "EBU0000000000571");

    }

    /**
     * 测试新老方法出入是否一致\
     * 仓配
     */
    @Test
    public void makeInboundOrderCPTest(){

        when(waybillQueryManager.getDataByChoiceNoCache(anyString(),any(WChoice.class)))
                .thenReturn(WaybillDataUtil.CANG_P_WAYBILL_RESULT);

        when(waybillQueryManager.getWaybillByReturnWaybillCode(anyString()))
                .thenReturn(WaybillDataUtil.CANG_P_RETURN_WAYBILL_RESULT);

        String waybillCode = "JDVA00024194366";
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode("910-23-20190103101251330");
        sendDetail.setWaybillCode(waybillCode);
        sendDetail.setCreateSiteCode(910);
        sendDetail.setBoxCode(waybillCode+"-1-5-");
        sendDetail.setPackageBarcode(waybillCode+"-1-5-");
        sendDetail.setReceiveSiteCode(25031);


        InboundOrder oldResult = reverseSpareEclp.createInboundOrder(waybillCode,sendDetail);
        InboundOrder newResult = reverseSpareEclp.makeInboundOrder(waybillCode,sendDetail);

        System.out.println(JsonHelper.toJson(oldResult));
        System.out.println(JsonHelper.toJson(newResult));
        Assert.assertTrue(JsonHelper.toJson(newResult).equals(JsonHelper.toJson(oldResult)));
    }

    /**
     * 测试新老方法出入是否一致\
     * 纯配
     */
    @Test
    public void makeInboundOrderCHUNPTest(){

        when(waybillQueryManager.getDataByChoiceNoCache(anyString(),any(WChoice.class)))
                .thenReturn(WaybillDataUtil.CHUN_P_WAYBILL_RESULT);

        when(waybillQueryManager.getWaybillByReturnWaybillCode(anyString()))
                .thenReturn(WaybillDataUtil.CHUN_P_RETURN_WAYBILL_RESULT);

        String waybillCode = "JDVA00024194366";
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode("910-23-20190103101251330");
        sendDetail.setWaybillCode(waybillCode);
        sendDetail.setCreateSiteCode(910);
        sendDetail.setBoxCode(waybillCode+"-1-5-");
        sendDetail.setPackageBarcode(waybillCode+"-1-5-");
        sendDetail.setReceiveSiteCode(25031);


        InboundOrder oldResult = reverseSpareEclp.createInboundOrder(waybillCode,sendDetail);
        InboundOrder newResult = reverseSpareEclp.makeInboundOrder(waybillCode,sendDetail);

        System.out.println(JsonHelper.toJson(oldResult));
        System.out.println(JsonHelper.toJson(newResult));
        Assert.assertTrue(JsonHelper.toJson(newResult).equals(JsonHelper.toJson(oldResult)));
    }

    /**
     * C2C
     */
    @Test
    public void makeInboundOrderC2CTest(){

        when(waybillQueryManager.getDataByChoiceNoCache(anyString(),any(WChoice.class)))
                .thenReturn(WaybillDataUtil.C2C_WAYBILL_RESULT);

        when(waybillQueryManager.getWaybillByReturnWaybillCode(anyString()))
                .thenReturn(WaybillDataUtil.C2C_RETURN_WAYBILL_RESULT);

        when(eclpOpenManager.transportGoodsInfo(any(GoodsInfo.class)))
                .thenReturn("EMG0001");

        String waybillCode = "JDVA00024194366";
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode("910-23-20190103101251330");
        sendDetail.setWaybillCode(waybillCode);
        sendDetail.setCreateSiteCode(910);
        sendDetail.setBoxCode(waybillCode+"-1-5-");
        sendDetail.setPackageBarcode(waybillCode+"-1-5-");
        sendDetail.setReceiveSiteCode(25031);

        InboundOrder newResult = reverseSpareEclp.makeInboundOrder(waybillCode,sendDetail);

        System.out.println(JsonHelper.toJson(newResult));
        //Assert.assertTrue(JsonHelper.toJson(newResult).equals(JsonHelper.toJson(oldResult)));
    }


    String siteJson = "{\n" +
            "    \"siteName\": \"北京3C备件库\",\n" +
            "    \"siteCode\": 25016,\n" +
            "    \"siteType\": 903,\n" +
            "    \"orgId\": 6,\n" +
            "    \"orgName\": \"总公司\",\n" +
            "    \"dmsSiteCode\": \"010V001\",\n" +
            "    \"customCode\": \"1\",\n" +
            "    \"storeCode\": \"spwms-6-1\",\n" +
            "    \"areaId\": 0,\n" +
            "    \"countyId\": 51216,\n" +
            "    \"connector\": \"北京3C逆向库\",\n" +
            "    \"telephone\": \"010-58527054\"\n" +
            "}";

    String LPJson = "{\n" +
            "  \"goodOwner\": \"0\",\n" +
            "  \"goodOwnerName\": \"String\",\n" +
            "  \"statusDesc\": \"String\",\n" +
            "  \"settleSubjectName\": \"String\",\n" +
            "  \"settleSubjectCode\": \"String\"\n" +
            "}";

    String goodJson = "[{\n" +
            "  \"shopGoodsId\": 0,\n" +
            "  \"shopGoodsNo\": \"String\",\n" +
            "  \"sellerGoodsSign\": \"String\",\n" +
            "  \"shopGoodsName\": \"String\",\n" +
            "  \"goodsId\": 0,\n" +
            "  \"goodsNo\": \"EMG123\",\n" +
            "  \"goodsName\": \"String\",\n" +
            "  \"spGoodsNo\": \"String\",\n" +
            "  \"isvGoodsNo\": \"String\",\n" +
            "  \"applyOutstoreQty\": 0,\n" +
            "  \"realOutstoreQty\": 0,\n" +
            "  \"deptApplyOutQty\": 0,\n" +
            "  \"deptRealOutQty\": 0,\n" +
            "  \"goodsStatus\": 0,\n" +
            "  \"deptId\": 0,\n" +
            "  \"version\": 0,\n" +
            "  \"test\": 0,\n" +
            "  \"goodsLevel\": \"String\",\n" +
            "  \"gid\": 0,\n" +
            "  \"insuredPriceFlag\": 0,\n" +
            "  \"batchNo\": \"String\",\n" +
            "  \"productionDate\": \"String\",\n" +
            "  \"expirationDate\": \"String\",\n" +
            "  \"orderLine\": \"String\",\n" +
            "  \"printName\": \"String\",\n" +
            "  \"occupyChangeNum\": 0,\n" +
            "  \"assignedChangeNum\": 0,\n" +
            "  \"installCompany\": \"String\",\n" +
            "  \"skyworthDetailNo\": \"String\",\n" +
            "  \"packagingDetails\": 0,\n" +
            "  \"suitId\": \"String\",\n" +
            "  \"jfsKey\": \"String\",\n" +
            "  \"soItemMark\": \"String\"\n" +
            "}]";


}
