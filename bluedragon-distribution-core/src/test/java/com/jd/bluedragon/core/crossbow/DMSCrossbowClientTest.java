package com.jd.bluedragon.core.crossbow;

import com.jd.bluedragon.core.crossbow.security.MessageSignSecurityProcessor;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/distribution-core-crossbow-manager.xml",
    }
)
@Suite.SuiteClasses(MessageSignSecurityProcessor.class)
public class DMSCrossbowClientTest {

    @Autowired
    @Qualifier("pddWaybillQueryManager")
    private PDDBusinessManager pddWaybillQueryManager;

    @Before
    public void before(){
        MessageSignSecurityProcessor processor = new MessageSignSecurityProcessor();
    }

    @Test
    public void executor() {

        PDDWaybillQueryDto pddWaybillQueryDto = new PDDWaybillQueryDto();
        pddWaybillQueryDto.setWaybillCode("PDD2365897458952");
        System.out.println(JsonHelper.toJson(pddWaybillQueryManager.doRestInterface(pddWaybillQueryDto)));
        System.out.println("dms_crossbow.properties");
    }
}