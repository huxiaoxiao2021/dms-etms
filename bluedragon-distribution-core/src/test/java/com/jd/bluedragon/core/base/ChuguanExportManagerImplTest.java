package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.ContantsEnum;
import com.jd.stock.iwms.export.param.ChuguanParam;
import org.junit.Assert;
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
public class ChuguanExportManagerImplTest {

    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Test
    public void testinsertChuguan(){
        List<ChuguanParam> chuguanParamList = Lists.newArrayList();
        ChuguanParam in = new ChuguanParam();
        in.setRfId("1122343333333333");
        in.setRfType(ContantsEnum.ChuGuanRfType.IN.getType());
        in.setChuruId(ContantsEnum.ChuGuanChuruId.IN_KU.getType());
        in.setChuru("");
        in.setOrderId(0L);
        in.setYuanDanHao(0);
        in.setCaiGouDanHao(0);
        in.setBusiNo("");
        in.setTypeId(0);
        in.setSubType("");
        in.setExtNo("");
        in.setExtType("");
        in.setDcId(0);
        in.setSid(0);
        in.setToDcId(0);
        in.setToSid(0);
        in.setSiteId(0);
        in.setToSiteId(0);
        in.setSiteType(0);
        in.setToSiteType(0);
        in.setIsAdjust(0);
        in.setIsVirtual(0);
//        in.setChuguanDetailVoList(Lists.newArrayList());
        in.setFenLei("");
        in.setFenLeiId(0);
        in.setJingBan("");
        in.setLaiYuan("");
        in.setLaiYuanCode("");
        in.setOrgId(0);
        in.setOrgName("");
        in.setZongJinE(new BigDecimal("0"));
        in.setRemark("");
        in.setQiTaFangShi("");
        in.setBusinessTime("");
        in.setKuanXiang("");
        in.setMoneyn(0);
        in.setLuru("");
        in.setCityId(0);
        in.setQianZi(0);
        in.setYunFei(new BigDecimal("0"));
        in.setYouHui(new BigDecimal("0"));
        in.setQiTaFeiYong(new BigDecimal("0"));
        in.setPeiHuoDanHao(0);
        in.setRemarkId(0);
        in.setTag("");
        in.setCaiWudanHao(0);
        in.setNationalTypeId(0);
        in.setVenderId("");

        long result = chuguanExportManager.insertChuguan(chuguanParamList);
        Assert.assertEquals(result,1);
    }

    @Test
    public void testqueryByWaybillCode(){
        KuGuanDomain result = chuguanExportManager.queryByWaybillCode("1111111166666666");
        Assert.assertNotNull(result);
    }

}
