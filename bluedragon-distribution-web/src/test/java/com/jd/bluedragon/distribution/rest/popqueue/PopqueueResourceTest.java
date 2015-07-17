package com.jd.bluedragon.distribution.rest.popqueue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.PopQueueRequest;
import com.jd.bluedragon.distribution.api.request.PopSigninRequest;
import com.jd.bluedragon.distribution.api.response.PopQueueResponse;
import com.jd.bluedragon.distribution.api.response.PopSigninResponse;

public class PopqueueResourceTest {
	public static final String baseURL="http://localhost:8080/services";
	@Test
	public void addQueueNo(){
		PopQueueRequest popQueueRequest = new PopQueueRequest();
		popQueueRequest.setCreateSiteCode(1610);
		popQueueRequest.setQueueType(1);
		popQueueRequest.setCreateUserCode(45302);
		String url=this.baseURL+"/popQueue/add";
		RestTemplate template = new RestTemplate();
		PopQueueResponse response = template.postForObject(url,
				popQueueRequest, PopQueueResponse.class);
		
		popQueueRequest.setQueueType(2);
		popQueueRequest.setExpressCode("00001");
		popQueueRequest.setExpressName("京东物流");
		template.postForObject(url,
				popQueueRequest, PopQueueResponse.class);
	}
	@Test
	public void start(){
		String url=this.baseURL+"/popQueue/start/027F004-201301310001";	
		RestTemplate template = new RestTemplate();
		PopQueueResponse response = template.postForObject(url,
				null, PopQueueResponse.class);
		
	}
	@Test
	public void finish(){
		String url=this.baseURL+"/popQueue/finish/027F004-201301310001";	
		RestTemplate template = new RestTemplate();
		PopQueueResponse response = template.postForObject(url,
				null, PopQueueResponse.class);
	}
	@Test
	public void getExpressList(){
		String url=this.baseURL+"/popQueue/getExpressList";
		PopQueueRequest request= new PopQueueRequest();
		//request.setKeyword("顺丰");
		RestTemplate template = new RestTemplate();
		List list = template.postForObject(url,
				request, List.class);
		System.out.println(list.size());
	}
	@Test
	public void getPopSigninList(){

		PopSigninRequest request = new PopSigninRequest();
		request.setPageNo(1);
		String url=this.baseURL+"/popSignin/getPopSigninList";
		RestTemplate template = new RestTemplate();
		PopSigninResponse response = template.postForObject(url,
				request, PopSigninResponse.class);
	/*	System.out.println("currentPage:"+response.getCurrentPage());
		System.out.println("totalCount:"+response.getTotalCount());
		System.out.println("totalPage:"+response.getTotalPage());
		System.out.println("size:"+response.getData().size());*/
		
		
	}

	
	
	@Test 
	public void  heh(){
		String baseUrl="http://dms.etms.360buy.com/services/popMessage/sendMessage/";
		String url="";
		RestTemplate template = new RestTemplate();
		List<String> list = getData();
		StringBuilder error1 = new StringBuilder();
		StringBuilder error2 = new StringBuilder();
		int m=0;
		for(String str:list){
			 try {
				url=baseUrl+str;
				 String ss =template.getForObject(url, String.class);
				 System.out.println(url+",返回结果："+ss);
				 m++;
				 System.out.println("-------------------------------"+m);
				 if(!ss.equals("200")){
					 error1.append(str).append(",");
					} 
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				error2.append(str).append(",");
			}
		}
		
		System.out.println("ERROR1:"+error1.toString());
		System.out.println("ERROR2:"+error2.toString());
		
		
		
	}
	
	
	public List<String> getData(){
		List<String> list = new ArrayList<String>();
		list.add("473961255");
		list.add("469633116");
		list.add("466371616");
		list.add("466014967");
		list.add("465564736");
		list.add("465519313");
		list.add("464658818");
		list.add("464189677");
		list.add("463029486");
		list.add("462807885");
		list.add("462756339");
		list.add("462314909");
		list.add("462043075");
		list.add("461950919");
		list.add("461601841");
		list.add("461423608");
		list.add("460859883");
		list.add("460809605");
		list.add("460707553");
		list.add("460634989");
		list.add("460596971");
		list.add("460572008");
		list.add("460167449");
		list.add("460155430");
		list.add("459936327");
		list.add("459819574");
		list.add("459759424");
		list.add("459619773");
		list.add("459466849");
		list.add("459243616");
		list.add("459154576");
		list.add("459104419");
		list.add("458778675");
		list.add("458600593");
		list.add("458461627");
		list.add("458188607");
		list.add("457138842");
		list.add("456910035");
		list.add("456192921");
		list.add("456191709");
		list.add("455985466");
		list.add("455596955");
		list.add("455319756");
		list.add("454978794");
		list.add("454933458");
		list.add("454853083");
		list.add("454560807");
		list.add("454435291");
		list.add("454404643");
		list.add("454282507");
		list.add("454133618");
		list.add("454049070");

        return list;
	}
	
	


}
