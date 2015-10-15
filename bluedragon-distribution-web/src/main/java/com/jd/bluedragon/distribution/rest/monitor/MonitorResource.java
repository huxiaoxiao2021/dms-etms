package com.jd.bluedragon.distribution.rest.monitor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.request.MonitorRequest;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.ObjectFactory;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.Base64;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.Constants;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MonitorResource {

    static String postAuthority=null;
    static {
        try{
            postAuthority= PropertiesHelper.newInstance().getValue("postAuthority");
        }
        catch(Exception e){
            postAuthority="yanghongqiang";
        }
    }

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
    public Object postRemoteURL(MonitorRequest request){

        // region 查查当前登录人是否有权限
        boolean hasAuth=false;
        for(String auth :postAuthority.split(",")){
            if (auth.equals(request.getUserName())) {
                hasAuth = true;
                break;
            }
        }
        if(!hasAuth)
            return null;
        // endregion

        // region 检查 当前人是否上传正确的token信息


        //endregion

        //region token没有验证成功，使用用户名密码登录并返回token返回


        //endregion

        String postData=request.getPostData();
        String url=request.getUrl();
        Object result = null;
        logger.info("monitor getRemoteRes start!url:"+postData);
        postData=getFromBase64(postData);
        RestTemplate template = new RestTemplate();
        try {
            result = template.getForEntity(postData, Object.class);
            logger.debug("result=" + result);
        }catch( Exception e ){
            logger.error("调用远程方法出错！", e);
        }
        logger.info("monitor getRemoteRes end!url:"+postData);
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
