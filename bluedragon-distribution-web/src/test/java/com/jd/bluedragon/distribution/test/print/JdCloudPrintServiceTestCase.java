package com.jd.bluedragon.distribution.test.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.service.JdCloudPrintService;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/distribution-web-print-test.xml"})
public class JdCloudPrintServiceTestCase {
	
	public static void main(String[] args) throws Exception{
	}
    
    @Autowired
    @Qualifier("jdCloudPrintService")
    private JdCloudPrintService jdCloudPrintService;
    @Autowired
    @Qualifier("localPrintService")
    private JdCloudPrintService localPrintService;
    
    @Test
    public void testUseNewTemplate() throws Exception{
    	//http://dmswebtest.360buy.com/sysconfig/list?pageNo=1&pageSize=10&configName=print.dmsSiteCodes.useNewTemplate
    	//测试启用新模板配置功能
    	JdCloudPrintRequest<Map<String,String>> req = jdCloudPrintService.getDefaultPdfRequest();
    	List<Map<String,String>> list = new ArrayList<>();
    	req.setModel(list);
    	Map<String,String> data = new HashMap<>();
    	data.put("packageCode", "JD0000000278343-1-1-");
    	data.put("waybillCode", "JD0000000278343");
    	for(int i=0;i<1;i++){
    		list.add(data);
    	}
    	req.setLocation("910");
    	req.setTemplate("wms_mjn_pickingnote1");
    	req.setTemplateVer("1");
    	req.setOrderNum("56289554274");
    	jdCloudPrintService.jdCloudPrint(req);
    	req.setTemplate("dms-b2b-m");
    	localPrintService.jdCloudPrint(req);
    }
}
