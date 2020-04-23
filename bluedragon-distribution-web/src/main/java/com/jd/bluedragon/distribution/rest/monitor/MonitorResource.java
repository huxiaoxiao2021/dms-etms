package com.jd.bluedragon.distribution.rest.monitor;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.MonitorRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.apache.xml.security.utils.Base64;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MonitorResource {

    /**
     * 姓名，token，应缓存至数据库并加失效时间
     * */
    Map<String,String> tokenMap=new HashMap<String, String>();

    static String postAuthority="eWFuZ2hvbmdxaWFuZw==";
    static {
        try{
            postAuthority = PropertiesHelper.newInstance().getValue("postAuthority") == null ?
                    "eWFuZ2hvbmdxaWFuZw==" :
                    PropertiesHelper.newInstance().getValue("postAuthority");
        }
        catch(Exception e){
            postAuthority="eWFuZ2hvbmdxaWFuZw==";
        }
    }
    @Autowired
    private BaseService baseService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@GET
	@Path("/monitor/getRemoteRes")
	@GZIP
	public Object getRemoteRes(@QueryParam("url") String url) {
        Object result = null;
        log.info("monitor getRemoteRes start!url:{}", url);
		RestTemplate template = new RestTemplate();
        try {
            result = template.getForObject(url, Object.class);
            log.debug("result={}", result);
        }catch( Exception e ){
        	log.error("调用远程方法出错！", e);
        }
        return result;
	}


    /**
     * POST 请求，可以测试本地分拣中心是否可用
     * @param url 完整的URL
     * @return
     * */
    @POST
    @Path("/monitor/remoteURL")
    @GZIP
    public Object getRemoteURL(String url){
        Object result = null;
        log.info("monitor getRemoteRes start!url:{}", url);
        RestTemplate template = new RestTemplate();
        try {
            result = template.getForObject(url, Object.class);
            log.debug("result={}", result);
        }catch( Exception e ){
            log.error("调用远程方法出错！", e);
        }
        return result;
    }

    /**
     * POST 请求，可以测试本地分拣中心是否可用
     * @param data 完整的URL请求data数据中包含1url2请求参数3erp帐户密码（或者成功能登录erp后的token）
     * 记日志，限制请求次数，仅为查询线上问题而实现此方法
     * @return
     * */
    @POST
    @Path("/monitor/postRemoteURL")
    @GZIP
    public Object postRemoteURL(String data){
        Object result = null;
        try {
            String realData=getFromBase64(data);
            MonitorRequest request= JsonHelper.fromJson(realData,MonitorRequest.class);

            String postData = request.getPostData();
            String url = request.getUrl();

            log.info("monitor getRemoteRes start!url:{}", postData);
            try {

                RestTemplate template = new RestTemplate();
                ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 60);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/json; charset=UTF-8"));
                headers.add("Accept", org.springframework.http.MediaType.APPLICATION_JSON.toString());
                HttpEntity<String> formEntity = new HttpEntity<String>(postData, headers);
                ResponseEntity<Object> response = template.postForEntity(url, formEntity, Object.class);
                if (null != response && null != response.getBody()) {
                    result=  response.getBody();
                }
            } catch (Exception e) {
                log.error("调用远程方法出错！", e);
            }
        }catch (Exception ex){
            log.error(data,ex);
            return  new JdResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }

    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = Base64.encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            try {
                b = Base64.decode(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        Base64.encode(MessageDigest.getInstance("md5").digest(
//                (osdJson + validateStr).getBytes("UTF-8")))
        return result;
    }
}
