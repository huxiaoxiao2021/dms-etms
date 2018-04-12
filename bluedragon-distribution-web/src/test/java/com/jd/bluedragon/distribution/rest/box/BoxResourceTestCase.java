package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class BoxResourceTestCase {

    private final RestTemplate template = new RestTemplate();

//    String urlRoot = "http://192.168.226.157:8080/services/";
    String urlRoot = "http://localhost:8081/services/";
//    String urlRoot = "http://dms1.etms.360buy.com/services/";
@Test
    public void test_add_box() {
        String url = urlRoot+"boxes";
        // String url = "http://localhost/services/boxes";

        BoxRequest request = new BoxRequest();

        request.setType("TC");
        request.setUserCode(99);
        request.setUserName("王治澎");
        request.setCreateSiteCode(233957);
        request.setCreateSiteName("西安分拣中心");
        request.setReceiveSiteCode(1008);
        request.setReceiveSiteName("贵阳亚一分拨中心");
        request.setQuantity(1);
        request.setTransportType(2);
        request.setPredictSendTime(new Date());

        BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCodes());
        System.out.println("routinfo are " + response.getRouterInfo());
        if(response.getRouterInfo()!=null)
	        for(String s:response.getRouterInfo()){
	        	 System.out.println(s);
	        }
    }


    public void test_getRouterInfo() {
        String url = urlRoot+"boxes/getRouterInfo";
        // String url = "http://localhost/services/boxes";

        BoxRequest request = new BoxRequest();

        request.setCreateSiteCode(2015);
        request.setReceiveSiteCode(2002);
        request.setTransportType(2);

        BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCodes());
        System.out.println("routinfo are " + response.getRouterInfo());
        if(response.getRouterInfo()!=null)
	        for(String s:response.getRouterInfo()){
	        	 System.out.println(s);
	        }
    }
    
    @Test
    public void test_get_boxbyboxcode() {
    	String url = urlRoot+"boxes/TC029F001851F00200006001";

        BoxResponse response = this.template.getForObject(url, BoxResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCode());
        System.out.println("routinfo are " + response.getRouterInfo());
        if(response.getRouterInfo()!=null)
	        for(String s:response.getRouterInfo()){
	        	 System.out.println(s);
	        }
    }
    
    @Test
    public void test_get_box() {
        String url = urlRoot+"sorting/check?boxCode=179619230N1S3&createSiteCode=1006&receiveSiteCode=39&businessType=10&packageCode=179619230N1S3%20";

        BoxResponse response = this.template.getForObject(url, BoxResponse.class,
                "B010F039010Y04010010007");

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCode());
    }

    public void test_print_box() {
        String url = urlRoot+"boxes/reprint";
        BoxRequest request = new BoxRequest();

        request.setBoxCode("B027Z001027F00100001001");
        request.setUserCode(1001);
        request.setUserName("刘备");

        BoxResponse response = this.template.postForObject(url, request, BoxResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
    }

    @Test
    public void testPost() throws Exception {
        URL url = new URL(urlRoot+"/boxes/print");

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
}
