package com.jd.bluedragon.distribution.test.receive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.api.request.ReceiveRequest;
import com.jd.bluedragon.distribution.api.request.ShieldsCarErrorRequest;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.receiveInspectionExc.service.ShieldsErrorService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonUtil;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context-test.xml" })
public class ReceiveTestCase {

    @Test
    public void test_doReceiveing() throws Exception {
    	
	    ReceiveRequest re=new ReceiveRequest();
	    re.setCarCode("6666");
	    re.setShieldsCarCode("8745");
	    re.setPackOrBox("477777N66");
	    //re.setReceiveType(Short.parseShort("1"));
//	    re.setSiteCode("01");
//	    re.setSiteName("北辰");
//	    re.setUserCode("01");
	    re.setUserName("李东");
	    
	    ReceiveRequest re2=new ReceiveRequest();
	    re2.setCarCode("6666");
	    re2.setShieldsCarCode("8745");
	    re2.setPackOrBox("B757777");
	   // re2.setReceiveType(Short.parseShort("1"));
//	    re2.setSiteCode("01");
//	    re2.setSiteName("北辰");
//	    re2.setUserCode("01");
	    re2.setUserName("李东");
	    
	    ReceiveRequest re3=new ReceiveRequest();
	    re3.setCarCode("6789");
	    re3.setShieldsCarCode("9086");
	    re3.setPackOrBox("B977777");
	  //  re3.setReceiveType(Short.parseShort("1"));
//	    re3.setSiteCode("01");
//	    re3.setSiteName("北辰");
//	    re3.setUserCode("01");
	    re3.setUserName("李东");
	    
	    ReceiveRequest re4=new ReceiveRequest();
	    re4.setCarCode("6666");
	    re4.setShieldsCarCode("8745");
	    re4.setPackOrBox("B357777");
	    //re4.setReceiveType(Short.parseShort("1"));
//	    re4.setSiteCode("01");
//	    re4.setSiteName("北辰");
//	    re4.setUserCode("01");
	    re4.setUserName("李东");
	    
	    List<ReceiveRequest> list=new ArrayList<ReceiveRequest>();
		list.add(re);
		list.add(re2);
        list.add(re3);
        list.add(re4);
	    
        String json=JsonUtil.getInstance().list2Json(list);
		System.out.println("请求json:"+json);

//		task.setCreated(date);
//		task.setModified(date);
		//boolean bl=receiveService.doReceiveing(receiveService.doParse(json,task));
		//System.out.println("reslut="+bl);
     
    }
    @Test
    public void test_doReceiveProcessed() throws Exception {
//    	boolean bl=receiveService.doReceiveProcessed("1");
//    	System.out.println(bl);
    	
    }
    @Test
    public void test_doInspectionProcessed() throws Exception {
//    	boolean bl=receiveService.doInspectionProcessed("1");
//    	System.out.println(bl);
    	
    }
    @Test
    public void test_doAddShieldsError() throws Exception {
    	ShieldsCarErrorRequest r=new ShieldsCarErrorRequest();
    	//r.setBoxCode("7777");
    	r.setShieldsCode("F7777");
    	r.setShieldsError("封签破损");
//    	r.setSiteCode("b1");
//    	r.setSiteName("上地站");
//    	r.setUserCode("u1");
    	r.setUserName("李会");
    	
    	ShieldsCarErrorRequest r2=new ShieldsCarErrorRequest();
    	r2.setCarCode("8888");
    	r2.setShieldsCode("F8888");
    	r2.setShieldsError("封签破损");
//    	r2.setSiteCode("b1");
//    	r2.setSiteName("上地站");
//    	r2.setUserCode("u1");
    	r2.setUserName("李东");
    	
    	ShieldsCarErrorRequest r3=new ShieldsCarErrorRequest();
    	//r3.setBoxCode("55555");
    	r3.setShieldsCode("F55555");
    	r3.setShieldsError("封签破损");
//    	r3.setSiteCode("b1");
//    	r3.setSiteName("上地站");
//    	r3.setUserCode("u1");
    	r3.setUserName("李会");
    	List list=new ArrayList();
    	list.add(r);
    	list.add(r2);
    	list.add(r3);
    	
    	String json=JsonUtil.getInstance().list2Json(list);
    	System.out.println("------->json="+json);

//		task.setCreated(date);
//		task.setModified(date);
    	//boolean bl=shieldsErrorService.doAddShieldsError(shieldsErrorService.doParse(json, task));
    	//System.out.println(bl);
    	
    }
    
}
