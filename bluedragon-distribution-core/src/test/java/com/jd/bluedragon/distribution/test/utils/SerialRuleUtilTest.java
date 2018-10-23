package com.jd.bluedragon.distribution.test.utils;

import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SerialRuleUtilTest {

    @Test
    public void testGetReceiveSiteCodeFromSendCode() throws Exception {
        String sendCode="1609-32-20150415141756989";
        Assert.assertEquals(Integer.valueOf(32),SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode));
    }


    @Test
    public void testIsMatchWaybillNo() throws Exception {
        String packageCode="123456789";
        Assert.assertTrue(SerialRuleUtil.isMatchWaybillNo(packageCode));
        packageCode="VC02015690736";
        Assert.assertTrue(SerialRuleUtil.isMatchWaybillNo(packageCode));
    }



    @Test
    public void testIsMatchReceiveWaybillNo() throws Exception {
        String packageCode="123456789-1-1-3";
        Assert.assertFalse(SerialRuleUtil.isMatchReceiveWaybillNo(packageCode));
        packageCode="VC02015690736";
        Assert.assertTrue(SerialRuleUtil.isMatchReceiveWaybillNo(packageCode));

        packageCode="123456789N1S1H3";
        Assert.assertFalse(SerialRuleUtil.isMatchReceiveWaybillNo(packageCode));
        packageCode="VC02015690736N1S8H56";
        Assert.assertFalse(SerialRuleUtil.isMatchReceiveWaybillNo(packageCode));
    }


    @Test
    public void testIsMatchAllPackageNo() throws Exception {
        String packageCode="123456789-1-1-3";
        Assert.assertTrue(SerialRuleUtil.isMatchAllPackageNo(packageCode));
        packageCode="VC02015690736";
        Assert.assertFalse(SerialRuleUtil.isMatchAllPackageNo(packageCode));

        packageCode="123456789N1S1H3";
        Assert.assertTrue(SerialRuleUtil.isMatchAllPackageNo(packageCode));
        packageCode="VC02015690736N1S8H56";
        Assert.assertTrue(SerialRuleUtil.isMatchAllPackageNo(packageCode));
    }

    @Test
    public void testMain() throws Exception {

    }

    @Test
    public void testIsMatchAllWaybillNo() throws Exception {
        String packageCode="123456789";
        Assert.assertTrue(SerialRuleUtil.isMatchAllWaybillNo(packageCode));
        packageCode="VC02015690736";
        Assert.assertTrue(SerialRuleUtil.isMatchAllWaybillNo(packageCode));
    }

    @Test
    public void testIsWaybillOrPackageNo() throws Exception {
        String packageCode="123456789-1-1-3";
        Assert.assertTrue(SerialRuleUtil.isWaybillOrPackageNo(packageCode));
        packageCode="VC02015690736-1-8-56";
        Assert.assertTrue(SerialRuleUtil.isWaybillOrPackageNo(packageCode));
        packageCode="123456789";
        Assert.assertTrue(SerialRuleUtil.isWaybillOrPackageNo(packageCode));
        packageCode="VC02015690736";
        Assert.assertTrue(SerialRuleUtil.isWaybillOrPackageNo(packageCode));
    }


    @Test
    public void testGenerateAllPackageCodes() throws Exception {
        /*一单一件单，自营【-】*/
        List<String> expect=null;
        String packageCode="12345679-1-1-3";
        List<String> results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        for (String item:results){
            Assert.assertEquals(packageCode, item);
        }
        /*一单一件，自营【NSH】*/
        packageCode="12345679N1S1H3";
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        for (String item:results){
            Assert.assertEquals(packageCode,item);
        }
        /*一单多件，自营【-】*/
        packageCode="12345679-1-8-3";
        expect=new ArrayList<String>(8);
        for(int i=1;i<=8;i++){
            expect.add("12345679-"+i+"-8-3");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*一单多件，自营【NSH】*/
        packageCode="12345679N1S8H3";
        expect=new ArrayList<String>(8);
        for(int i=1;i<=8;i++){
            expect.add("12345679N"+i+"S8H3");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*一件多件，外单【-】*/
        packageCode="VC02015690736-1-8-56";
        expect=new ArrayList<String>(8);
        for(int i=1;i<=8;i++){
            expect.add("VC02015690736-"+i+"-8-56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*一单多件，外单【NSH】*/
        packageCode="VC02015690736N1S8H56";
        expect=new ArrayList<String>(8);
        for(int i=1;i<=8;i++){
            expect.add("VC02015690736N"+i+"S8H56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*一单一件，外单【-】*/
        packageCode="VC02015690736-1-1-56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("VC02015690736-"+i+"-1-56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*一单一件，外单【NSH】*/
        packageCode="VC02015690736N1S1H56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("VC02015690736N"+i+"S1H56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
        /*非包裹号*/
        packageCode="dfsafsd-1-1-56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("dfsafsd-"+i+"-1-56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }

        /*一单一件 外单 VT*/
        packageCode="VT0033214321-1-1-56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("VT0033214321-"+i+"-1-56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }

        /*一单一件，外单限制最大包裹数测试*/
        packageCode="VT0033214321-1-100000-56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("VT0033214321-"+i+"-100000-56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }

        /*非包裹号*/
        packageCode="VTDFDASFDASADS56";
        expect=new ArrayList<String>(1);
        for(int i=1;i<=1;i++){
            expect.add("VTDFDASFDASADS56");
        }
        results= SerialRuleUtil.generateAllPackageCodes(packageCode);
        Assert.assertEquals(expect.size(),results.size());
        for (int i=0;i<expect.size();i++){
            Assert.assertEquals(expect.get(i),results.get(i));
        }
    }

    @Test
    public void testIsMatchNumeric() throws Exception {
        String content="FDXFD";
        Assert.assertFalse(SerialRuleUtil.isMatchNumeric(content));
        content="323223";
        Assert.assertTrue(SerialRuleUtil.isMatchNumeric(content));
    }

    @Test
    public void testIsMatchBoxCode() throws Exception {
        String box="BC010F001023D78912345678";
        Assert.assertTrue(SerialRuleUtil.isMatchBoxCode(box));
        box="BC010F001023D789123456789098";
        Assert.assertFalse(SerialRuleUtil.isMatchBoxCode(box));
    }
}