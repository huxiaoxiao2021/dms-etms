package com.jd.bluedragon.distribution.test.box;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;

public class BoxResourceTestCase {

	private final RestTemplate template = new RestTemplate();

	@Test
	public void test_add_box() {
		// String url = "http://dms.etms.360buy.com/services/boxes";
		String url = "http://localhost:1111/services/boxes";

		BoxRequest request = new BoxRequest();

		request.setType("BC");
		request.setUserCode(7157);
		request.setUserName("whfchun");
		request.setCreateSiteCode(1609);
		request.setCreateSiteName("武汉沌口分拣中心");
		request.setReceiveSiteCode(3);
		request.setReceiveSiteName("中原站");
		request.setQuantity(9);

		BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCodes());
	}

	public void test_get_box() {
		String url = "http://192.168.200.202:28060/services/sorting/check?boxCode=179619230N1S3&createSiteCode=1006&receiveSiteCode=39&businessType=10&packageCode=179619230N1S3%20";

		BoxResponse response = this.template.getForObject(url, BoxResponse.class, "B010F039010Y04010010007");

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCode());
	}

	public void test_print_box() {
		String url = "http://localhost:8080/services/boxes/reprint";
		BoxRequest request = new BoxRequest();

		request.setBoxCode("B027Z001027F00100001001");
		request.setUserCode(1001);
		request.setUserName("刘备");

		BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
	}

	public void testPost() throws Exception {
		URL url = new URL("http://localhost:8080/services/boxes/print");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Accept", "application/json");
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(1000);

		String userXML = "{\"boxCode\":\"1-1001-1003-6\",\"UserCode\":\"aa\",\"UserName\":\"aa\"}";

		OutputStream os = connection.getOutputStream();

		os.write(userXML.getBytes());
		os.flush();
		System.out.println("dddd:" + connection.getResponseCode());
		System.out.println("-------------------------");
		InputStream is = connection.getInputStream();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		int loop = -1;

		while ((loop = is.read()) != -1) {
			bao.write(loop);
		}

		System.out.println("共计：" + connection.getContentLength());
		System.out.println(bao.toString());

		connection.disconnect();
	}
	
	public void test_getRouterInfo() {
		// String url = "http://dms.etms.360buy.com/services/boxes";
		String url = "http://192.168.226.157/:8080/services/boxes/getRouterInfo";

		BoxRequest request = new BoxRequest();

		request.setType("BC");
		request.setUserCode(7157);
		request.setUserName("whfchun");
		request.setCreateSiteCode(1609);
		request.setCreateSiteName("武汉沌口分拣中心");
		request.setReceiveSiteCode(3);
		request.setReceiveSiteName("中原站");
		request.setQuantity(9);
		request.setTransportType(1);

		BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCodes());
		System.out.println("Routerinfo is " + response.getRouterInfo());
	}
	
	public void test_getBox() {
		// String url = "http://dms.etms.360buy.com/services/boxes";
		String url = "http://192.168.226.157/:8080/services/boxes/BC025F001010A00600001001";

		BoxRequest request = new BoxRequest();

		request.setType("BS");
		request.setUserCode(7157);
		request.setUserName("whfchun");
		request.setCreateSiteCode(1610);
		request.setCreateSiteName("武汉沌口分拣中心");
		request.setReceiveSiteCode(3);
		request.setReceiveSiteName("中原站");
		request.setQuantity(9);
		request.setTransportType(1);

		BoxResponse response = this.template.getForObject(url, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCodes());
		System.out.println("Routerinfo is " + response.getRouterInfo());
	}
	
	public void test_getBoxValidation() {
		// String url = "http://dms.etms.360buy.com/services/boxes";
		String url = "http://192.168.226.157:8080/services/boxes/validation?boxCode=BC025F001010A00600001001&operateType=2";

		BoxRequest request = new BoxRequest();

		request.setType("BS");
		request.setUserCode(7157);
		request.setUserName("whfchun");
		request.setCreateSiteCode(1610);
		request.setCreateSiteName("武汉沌口分拣中心");
		request.setReceiveSiteCode(3);
		request.setReceiveSiteName("中原站");
		request.setQuantity(9);
		request.setTransportType(1);

		BoxResponse response = this.template.getForObject(url, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCodes());
		System.out.println("Routerinfo is " + response.getRouterInfo());
	}
	
	public void test_getBoxcache() {
		// String url = "http://dms.etms.360buy.com/services/boxes";
		String url = "http://192.168.226.157/:8080/services/boxes/cache/BC025F001010A00600001001";

		BoxRequest request = new BoxRequest();

		request.setType("BS");
		request.setUserCode(7157);
		request.setUserName("whfchun");
		request.setCreateSiteCode(1610);
		request.setCreateSiteName("武汉沌口分拣中心");
		request.setReceiveSiteCode(3);
		request.setReceiveSiteName("中原站");
		request.setQuantity(9);
		request.setTransportType(1);

		BoxResponse response = this.template.getForObject(url, BoxResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
		System.out.println("box code are " + response.getBoxCodes());
		System.out.println("Routerinfo is " + response.getRouterInfo());
	}
}
