package com.jd.bluedragon.core.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xumigen on 2019/11/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class EcpQueryWSManagerImplTest {

    @Autowired
    private EcpQueryWSManager ecpQueryWSManager;

    @Test
    public void getAirPortListByFlightNumber() throws Exception {
        ecpQueryWSManager.getAirPortListByFlightNumber("");
    }


    @Test
    public void getAirTplBillDetailInfo() throws Exception {
        ecpQueryWSManager.getAirTplBillDetailInfo("228-77770081");
    }
}