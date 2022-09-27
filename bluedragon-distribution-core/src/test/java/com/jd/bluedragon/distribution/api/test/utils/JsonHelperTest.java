package com.jd.bluedragon.distribution.api.test.utils;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.JsonHelper;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonHelperTest {

    private String jsonText="{\n" +
            "\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"OK\",\n" +
            "    \"siteCode\": 54106,\n" +
            "    \"siteName\": \"泉州水头站\",\n" +
            "    \"siteType\": 4,\n" +
            "    \"dmsCode\": \"595Y007\"\n" +
            "\n" +
            "}";
    @BeforeClass
    public static void init(){

    }

    @Test
    public void testFromJson() throws Exception {
        JdResponse response= JsonHelper.fromJson(jsonText,JdResponse.class);

        Assert.assertNotNull(response);
    }

    @Test
    public void testJsonToArray() throws Exception {
        JdResponse response= JsonHelper.fromJson(jsonText,JdResponse.class);

        Assert.assertNotNull(response);
    }

    @Test
    public void testToJson() throws Exception {
        String json = "{    \"systemCode\":\"dms\",    \"programType\":60,    \"businessType\":1005,    \"operateType\":100501,    \"data\":\"{\\\"barCode\\\":\\\"5345wesdf\\\",\\\"businessType\\\":1005,\\\"operateType\\\":100501,\\\"paperSizeCode\\\":\\\"1005\\\",\\\"dmsSiteCode\\\":1263366,\\\"userCode\\\":20035355,\\\"userName\\\":\\\"wzx\\\",\\\"siteCode\\\":1263366,\\\"siteName\\\":\\\"wzx\\\"}\"}";

        System.out.println(JsonHelper.getObject(JSONObject.parseObject(json), "data.userCode"));
        System.out.println(JsonHelper.getObject(JSONObject.parseObject(json), ""));

        String json2 = "{\n" +
                "    \"code\":200,\n" +
                "    \"data\":\"{\\\"bcSign\\\":\\\"KA\\\",\\\"popSupId\\\":1905063,\\\"templateVersionStr\\\":\\\"1\\\",\\\"originalTabletrolleyCode\\\":\\\"E1-5T\\\",\\\"waybillCodeLast\\\":\\\"9702\\\",\\\"purposefulDmsName\\\":\\\"安庆中转站\\\",\\\"destinationTabletrolleyCode\\\":\\\"安42-二线\\\",\\\"userLevel\\\":\\\"\\\",\\\"quantity\\\":1,\\\"bjCheckFlg\\\":false,\\\"originalDmsCode\\\":755380,\\\"specialMark\\\":\\\"KA众退\\\",\\\"purposefulDmsCode\\\":1460264,\\\"specialMarkNew\\\":\\\"众退\\\",\\\"companyName\\\":\\\"桑丽娜\\\",\\\"consignerPrefixText\\\":\\\"寄件地址：\\\",\\\"timeCategory\\\":\\\"隔日达\\\",\\\"longPack\\\":false,\\\"statusMessage\\\":\\\"OK\\\",\\\"useNewTemplate\\\":true,\\\"prepareSiteCode\\\":434809,\\\"printSiteName\\\":\\\"安庆望江营业部\\\",\\\"muslimSignText\\\":\\\"\\\",\\\"packText\\\":\\\"2022-09-08 14:58\\\",\\\"jZDFlag\\\":\\\"\\\",\\\"originalCityCode\\\":2376,\\\"normalText\\\":\\\"无\\\",\\\"hasPrintInvoice\\\":false,\\\"freightText\\\":\\\"无\\\",\\\"additionalComment\\\":\\\"http://www.jdwl.com 客服电话：950616\\\",\\\"road\\\":\\\"003\\\",\\\"busiCode\\\":\\\"010K1169236\\\",\\\"purposefulCrossCode\\\":\\\"996\\\",\\\"priceProtectText\\\":\\\"\\\",\\\"codMoneyText\\\":\\\"\\\",\\\"totalChargeText\\\":\\\"\\\",\\\"examineFlag\\\":\\\"[已验视]\\\",\\\"originalDmsName\\\":\\\"西安灞桥\\\",\\\"originalCityName\\\":\\\"西安市\\\",\\\"luxuryText\\\":\\\"\\\",\\\"busiOrderCode\\\":\\\"LBF00000000016711662727468\\\",\\\"purposefulTableTrolley\\\":\\\"安42-二线\\\",\\\"popSupName\\\":\\\"桑丽娜\\\",\\\"waybillType\\\":10000,\\\"packList\\\":[{\\\"packageWeight\\\":\\\"\\\",\\\"weight\\\":0.0,\\\"printPack\\\":false,\\\"isPrintPack\\\":false,\\\"packageIndex\\\":\\\"1/1\\\",\\\"packageSuffix\\\":\\\"-1-1-\\\",\\\"packageIndexNum\\\":1,\\\"packageCode\\\":\\\"JDX010979059702-1-1-\\\"}],\\\"sopOrExternalFlg\\\":true,\\\"destinationCrossCode\\\":\\\"996\\\",\\\"prepareSiteName\\\":\\\"安庆望江营业部\\\",\\\"originalCrossCode\\\":\\\"15\\\",\\\"bCustomerId\\\":1905063,\\\"destinationDmsName\\\":\\\"安庆\\\",\\\"isAir\\\":false,\\\"priceProtectFlag\\\":0,\\\"originalCrossType\\\":1,\\\"busiId\\\":1905063,\\\"printSiteCode\\\":434809,\\\"templateGroupCode\\\":\\\"C\\\",\\\"remark\\\":\\\"服务单号:LBF00000000016711662727468\\\",\\\"isSelfService\\\":false,\\\"bCustomerName\\\":\\\"北京有竹居网络技术有限公司\\\",\\\"deliveryMethod\\\":\\\"【送】\\\",\\\"type\\\":10000,\\\"popularizeMatrixCode\\\":\\\"http://weixin.qq.com/q/02ixD6QH52bQO100000074\\\",\\\"originalTabletrolley\\\":\\\"E1-5T\\\",\\\"busiName\\\":\\\"北京有竹居网络技术有限公司\\\",\\\"waybillCode\\\":\\\"JDX010979059702\\\",\\\"dmsBusiAlias\\\":\\\"CMBC\\\",\\\"sendPay\\\":\\\"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\\\",\\\"waybillSignText\\\":\\\"KA众退\\\",\\\"barCode\\\":\\\"JDX010979059702\\\",\\\"transportMode\\\":\\\"特惠送\\\",\\\"specialMark1\\\":\\\"\\\",\\\"waybillVasSign\\\":\\\"\\\",\\\"consignerTelText\\\":\\\" 13201570510\\\",\\\"waybillSign\\\":\\\"30006000011900337000000060000102000000000002401000002300210000000000001000000010000100000000100000000000000010000000800000020000000000000000000000000000000000000000000000000000000000000000000000000000\\\",\\\"receivable\\\":\\\"在线支付\\\",\\\"customerPhoneText\\\":\\\"13329268530\\\",\\\"signBackText\\\":\\\"\\\",\\\"featherLetterWaybill\\\":false,\\\"popularizeMatrixCodeDesc\\\":\\\"扫码寄快递\\\",\\\"goodsName\\\":\\\"童装/婴儿装/亲子装\\\",\\\"needPrintFlag\\\":true,\\\"printInvoice\\\":false,\\\"packagePrice\\\":\\\"在线支付\\\",\\\"goodsPaymentText\\\":\\\"无\\\",\\\"jdLogoImageKey\\\":\\\"JDLogo.gif\\\",\\\"packageCounter\\\":1,\\\"statusCode\\\":200,\\\"distributTypeText\\\":\\\"\\\",\\\"outputType\\\":0,\\\"templateVersion\\\":0,\\\"rodeCode\\\":\\\"003\\\",\\\"printTime\\\":\\\"2022-09-08 14:58:18\\\",\\\"roadCode\\\":\\\"003\\\",\\\"securityCheck\\\":\\\"[已安检]\\\",\\\"waybillCodeFirst\\\":\\\"JDX01097905\\\",\\\"willPrintPackageIndex\\\":0,\\\"companyId\\\":1905063,\\\"templateName\\\":\\\"dms-nopaperyhd-m\\\"}\",\n" +
                "    \"failed\":false,\n" +
                "    \"message\":\"200-操作成功\",\n" +
                "    \"messageCode\":200,\n" +
                "    \"succeed\":true,\n" +
                "    \"warn\":false\n" +
                "}";

        System.out.println(JsonHelper.getObject(JSONObject.parseObject(json2), "data.packList[0].packageCode"));
    }

    @Test
    public void testToJson1() throws Exception {

    }

    @Test
    public void testJson2Map() throws Exception {

    }
}