package com.jd.bluedragon.core.crossbow;

import com.jd.bluedragon.core.crossbow.security.MessageSignSecurityProcessor;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequest;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequestItem;
import com.jd.bluedragon.external.crossbow.postal.manager.EmsTracesCompanyManager;
import com.jd.bluedragon.utils.JsonHelper;

import java.util.ArrayList;

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
    
    @Autowired
    @Qualifier("emsTracesCompanyManager")
    private EmsTracesCompanyManager emsTracesCompanyManager;

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
        
        EmsTracesCompanyManager a = new EmsTracesCompanyManager();
        a.setSecret("test");
        TracesCompanyRequest request = new TracesCompanyRequest();
        request.setBrandCode("jd");
        request.setTraces(new ArrayList<TracesCompanyRequestItem>());
        for(int i=0;i<10;i++) {
        	TracesCompanyRequestItem item = new TracesCompanyRequestItem();
        	item.setWaybillNo("test"+i);
        	request.getTraces().add(item);
        }
        emsTracesCompanyManager.doRestInterface(a);
    }
    public static void main(String[] args) {
        EmsTracesCompanyManager a = new EmsTracesCompanyManager();
        a.setSecret("789dde1cf8ews59deg6cdg4ew8dfe2w6");
        TracesCompanyRequest request = new TracesCompanyRequest();
        request.setBrandCode("JDL");
        request.setTraces(new ArrayList<TracesCompanyRequestItem>());
        for(int i=0;i<10;i++) {
        	TracesCompanyRequestItem item = new TracesCompanyRequestItem();
        	item.setWaybillNo("test"+i);
        	item.setOpCode("50");
        	item.setOpDesc("desc"+i);
        	request.getTraces().add(item);
        }
        
        a.getMyHeaderParams(request);
        System.out.println(JsonHelper.toJson(request));
        System.out.println(toCode12("653222211"));
    }
    
	private static String toCode12(String code) {
		if(code != null && code.length() < 12) {
			return code.concat("00000000000000000000000".substring(0, 12 - code.length()));
		}
		return code;
	}
}