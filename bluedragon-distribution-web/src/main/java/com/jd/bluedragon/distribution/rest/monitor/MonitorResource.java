package com.jd.bluedragon.distribution.rest.monitor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.MonitorRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.ObjectFactory;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.apache.xml.security.utils.Base64;
import org.springframework.http.HttpHeaders;

import com.jd.bluedragon.Constants;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
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

	private final Log logger = LogFactory.getLog(this.getClass());

	@GET
	@Path("/monitor/getRemoteRes")
	@GZIP
	public Object getRemoteRes(@QueryParam("url") String url) {
        Object result = null;
        logger.info("monitor getRemoteRes start!url:"+url);
		RestTemplate template = new RestTemplate();
        try {
            result = template.getForObject(url, Object.class);
            logger.debug("result=" + result);
        }catch( Exception e ){
        	logger.error("调用远程方法出错！", e);
        }
        logger.info("monitor getRemoteRes end!url:"+url);
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
        logger.info("monitor getRemoteRes start!url:"+url);
        RestTemplate template = new RestTemplate();
        try {
            result = template.getForObject(url, Object.class);
            logger.debug("result=" + result);
        }catch( Exception e ){
            logger.error("调用远程方法出错！", e);
        }
        logger.info("monitor getRemoteRes end!url:"+url);
        return result;
    }

    /**
     * POST 请求，可以测试本地分拣中心是否可用
     * @param request 完整的URL请求data数据中包含1url2请求参数3erp帐户密码（或者成功能登录erp后的token）
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
            // region 查查当前登录人是否有权限
            boolean hasAuth = false;
            for (String auth : postAuthority.split(",")) {
                if (auth.equals(request.getUserName())) {
                    hasAuth = true;
                    break;
                }
            }
            if (!hasAuth)
                return null;
            if(request.getUrl()!=null&&request.getUrl().indexOf("postRemoteURL")>0){
                logger.error(getBase64(request.getUserName()).replace("+","").replace("=",""));
                return new JdResponse(401,"非法帐户");
            }

            // endregion

            //region post信息有用户名密码，使用用户名密码登录并返回token返回

            PdaStaff login = baseService.login(getFromBase64(request.getUserName()), getFromBase64(request.getPassWord()));
            if(login.isError())
                return  new JdResponse(401,"验证失败");;

            //endregion


            // region 检查 当前人是否上传正确的token信息


            //endregion


            String postData = request.getPostData();
            String url = request.getUrl();

            logger.info("monitor getRemoteRes start!url:" + postData);
            try {

                RestTemplate template = new RestTemplate();;
                ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 60);
                HttpHeaders headers = new HttpHeaders();
                //org.springframework.http.MediaType type = org.springframework.http.MediaType.parseMediaType("application/json; charset=UTF-8");
                //headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                HttpEntity<String> formEntity = new HttpEntity<String>(postData, headers);
                ResponseEntity<Object> response = template.postForEntity(url, formEntity, Object.class);
                if (null != response && null != response.getBody()) {
                    result=  response.getBody();
                }
                logger.debug("result=" + JsonHelper.toJson(result));
            } catch (Exception e) {
                logger.error("调用远程方法出错！", e);
            }
            logger.info("monitor getRemoteRes end!url:" + postData);
        }
        catch (Exception ex){
            logger.error(ex+data);
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
