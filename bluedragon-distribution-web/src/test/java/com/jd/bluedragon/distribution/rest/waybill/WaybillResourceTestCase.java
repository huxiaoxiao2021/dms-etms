package com.jd.bluedragon.distribution.rest.waybill;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jd.oom.client.clientbean.Order;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.response.Product;
import com.jd.bluedragon.distribution.api.response.ProductResponse;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.domain.OrderStockInfo;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.loss.client.BlueDragonWebService;
import com.jd.loss.client.BlueDragonWebServiceService;

public class WaybillResourceTestCase {

	private RestTemplate template = new RestTemplate();

	String urlRoot = "http://dms1.etms.360buy.com/services/waybillOrPack/3011/";
	String urlOderDetail = "http://dms1.etms.360buy.com/services/order/products/";
	String urlReverseSpare = "http://dms1.etms.360buy.com/services/audit/reverseSpare/";
	String urlOrderInfo = "http://dms1.etms.360buy.com/services/order/";
	String urlStockInfo ="http://dms1.etms.360buy.com/services/audit/stock/";
	

	@Test
	public void test_get_waybillOrPack() {

		//导入处理订单
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodes.txt"));
		String url = urlRoot;

		for (String waybillCode : waybillCodeArr) {
//			WaybillResponse<Waybill> response = template.getForObject(url
//					+ waybillCode, WaybillResponse.class);
//			if (response.getData() == null)
//				System.out.println(waybillCode + "," + null);
//			else
//				System.out.println(waybillCode + ","
//						+ ((Map) response.getData()).get("type"));
			
			//获得实际发送数量与实际收货数量
			List rsResponse = template.getForObject(urlReverseSpare
					+ waybillCode, List.class);

			Map<String, Integer> spareTranCodeMap = new HashMap<String, Integer>();
			for(Object o : rsResponse){
				System.out.print(waybillCode);
				Map rs = (Map)o;
				if(rs.get("spareTranCode")!=null){
					String tempSpareTranCode = rs.get("spareTranCode").toString();
					System.out.println(","
							+ tempSpareTranCode);
					break;
				}
			}
		}
		
		
	}

	@Test
	public void test_get_NotifyStock() {

		String url = "http://dms1.etms.360buy.com/services/reverse/stock/nodify/";
		//导入处理订单
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodes.txt"));

		for (String waybillCode : waybillCodeArr) {
			String response = this.template.getForObject(url + waybillCode,
					String.class);
			System.out.println(waybillCode + "," + response);
		}
	}

	
	@Test
	public void test_get_kuguaninfo() {

		//导入处理订单
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodes.txt"));
		
		for (String waybillCode : waybillCodeArr) {	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(StringHelper.isEmpty(waybillCode.trim())) continue;
			String type = null;
			String fangshi = null;
			String fenlei = null;
			String qita = null;
			String qitafangshi = null;
			
			//1.查运单类型
//			WaybillResponse<Waybill> typeResp = template.getForObject(waybillUrl
//					+ waybillCode, WaybillResponse.class);
//			if (typeResp.getData() != null)
//				type = ((Map) typeResp.getData()).get("type").toString();

			
			//2.1查询页面上的值，用于判断先后款
			KuGuanDomain stockPageResp =this.template.getForObject(urlStockInfo + waybillCode+"/1",
					KuGuanDomain.class);
			
			if(stockPageResp!=null){
				fangshi = stockPageResp.getLblWay();
				fenlei = stockPageResp.getLblType();
				qita = stockPageResp.getLblOther();
				qitafangshi=stockPageResp.getLblOther();
			}
			
			//生成订单对象自行判断
			OrderStockInfo osi = new OrderStockInfo(waybillCode, type, fangshi, fenlei, qita, qitafangshi);
			osi.judgeStockInfo();
		}
		
	}
	

	@Test
	public void testCheckOrderDetail() {
		//导入处理订单
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodeAndProduct.txt"));
		System.out.println("序号,运单号,产品id,订单中此产品数,订单发货总数,实际本商品发货数,实际本商品备件收货数,spareTranCode");
		int index = 1;
		for (String waybillCode : waybillCodeArr) {	
			String[] waybillCodeAndProductId = waybillCode.split(",");
			waybillCode = waybillCodeAndProductId[0];
			String productId = waybillCodeAndProductId[1];
			
			ProductResponse response = template.getForObject(urlOderDetail
					+ waybillCode, ProductResponse.class);
			
			List<Product> products = response.getData();
			
			int productNum = 0;
			if(products!=null)
				for(Product p : products){
					if(p.getProductId().equals(productId))
						productNum+=p.getQuantity();
				}
			StringBuilder sb = new StringBuilder();
			sb.append(index++).append(",");
			sb.append(waybillCode).append(",");
			sb.append(productId).append(",");
			sb.append(productNum).append(",");
			
			
			//获得实际发送数量与实际收货数量
			List rsResponse = template.getForObject(urlReverseSpare
					+ waybillCode, List.class);
			int rsAllProductNum = 0;
			int rsSendNum = 0;
			int rsReceiveNum = 0;
			Map<String, Integer> spareTranCodeMap = new HashMap<String, Integer>();
			for(Object o : rsResponse){
				rsAllProductNum++;
				Map rs = (Map)o;
				if(rs.get("productId").toString().equals(productId)){
					rsSendNum++;
					if(rs.get("spareTranCode")!=null){
						String tempSpareTranCode = rs.get("spareTranCode").toString();
						rsReceiveNum++;
						if(spareTranCodeMap.get(tempSpareTranCode)!=null){
							spareTranCodeMap.put(tempSpareTranCode, spareTranCodeMap.get(tempSpareTranCode)+1);
						}else{
							spareTranCodeMap.put(tempSpareTranCode, 1);
						}
					}

				}
			}
			sb.append(rsAllProductNum).append(",");
			sb.append(rsSendNum).append(",");
			sb.append(rsReceiveNum).append(",");
			if(spareTranCodeMap.size()>0) sb.append(spareTranCodeMap.toString().replace(',', ' '));
			System.out.println(sb);
		}
	}
	
	@Test
	public void testGetLossProduct() throws MalformedURLException {
		QName SERVICE_NAME = new QName("http://ws.ldms.pis.bk.jd.com/", "BlueDragonWebServiceService");
		URL wsdlURL = new URL("file:/C:/Users/ADMINI~1.JD-/AppData/Local/Temp/tempdir8027233176766867800.tmp/blueDragonWebService_1.wsdl");
		BlueDragonWebServiceService ss = new BlueDragonWebServiceService(wsdlURL, SERVICE_NAME);
        BlueDragonWebService port = ss.getBlueDragonWebServicePort();  
        
		int lossCount = port.getLossProductCountOrderId("1596892161");
		System.out.println(lossCount);
	}
	
	

	@Test
	public void test_get_OrderInfo() {

		//导入处理订单
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodes.txt"));
		String url = urlRoot;

		for (String waybillCode : waybillCodeArr) {
		
			//获得实际发送数量与实际收货数量
			Order order = template.getForObject(this.urlOrderInfo
					+ waybillCode, Order.class);

			if(order!=null)
				System.out.println(waybillCode+","+order.getAreaNum());
			else
				System.out.println(waybillCode+",");

		}
		
		
	}
}
