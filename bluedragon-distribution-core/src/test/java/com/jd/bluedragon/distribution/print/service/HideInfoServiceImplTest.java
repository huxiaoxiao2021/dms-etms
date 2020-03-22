package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HideInfoServiceImplTest {

    final static String WAYBILL_SIGN_XM = "30001000310800000000000020008040000010020002001000002402010000000000001000000017000100000000100000000000000010000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    final static String WAYBILL_SIGN_DH = "30001000310800000000000020008040000020020002002000002402010000000000001000000017000100000000100000000000000010000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    final static String WAYBILL_SIGN_DZ = "30001000310800000000000020008040000040020002004000002402010000000000001000000017000100000000100000000000000010000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000";


    @Test
    public void setHideInfo() {
        System.out.println("微笑面单测试开始.....");

        HideInfoServiceImpl hideInfoService = new HideInfoServiceImpl();
        BasePrintWaybill waybill = new BasePrintWaybill();
        waybill.setPrintAddress("北京市丰台区槐房北路2号德鑫家园33号楼2单元1301");
        waybill.setConsignerAddress("黑龙江省铁力市槐房北路2号德鑫家园33号楼2单元1301");
        waybill.setCustomerName("刘铎");
        waybill.setConsigner("关婕");
        waybill.setConsignerTel("04582483555");
        waybill.setConsignerMobile("13888888888");
        waybill.setMobileFirst("1234");

        hideInfoService.setHideInfo(WAYBILL_SIGN_DZ,null,waybill);
        System.out.println("收件人地址隐藏:"+waybill.getPrintAddress());
        Assert.assertTrue("北京市丰台区槐房北路2号德鑫家园33号楼2^_^".equals(waybill.getPrintAddress()));
        System.out.println("寄件人地址隐藏:"+waybill.getConsignerAddress());
        Assert.assertTrue("黑龙江省铁力市槐房北路2号德鑫家园33号楼2^_^".equals(waybill.getConsignerAddress()));

        hideInfoService.setHideInfo(WAYBILL_SIGN_XM,null,waybill);
        System.out.println("收件人姓名隐藏:"+waybill.getCustomerName());
        Assert.assertTrue("刘^_^".equals(waybill.getCustomerName()));

        System.out.println("寄件人姓名隐藏:"+waybill.getConsigner());
        Assert.assertTrue("关^_^".equals(waybill.getConsigner()));

        hideInfoService.setHideInfo(WAYBILL_SIGN_DH,null,waybill);
        System.out.println("收件人电话隐藏:"+waybill.getMobileFirst());
        Assert.assertTrue("123^_^".equals(waybill.getMobileFirst()));

        System.out.println("寄件人电话隐藏:"+waybill.getConsignerTel());
        Assert.assertTrue("045^_^3555".equals(waybill.getConsignerTel()));

        System.out.println("寄件人电话隐藏:"+waybill.getConsignerMobile());
        Assert.assertTrue("138^_^8888".equals(waybill.getConsignerMobile()));

    }
}