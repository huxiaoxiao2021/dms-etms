package com.jd.bluedragon.distribution.rest.monitor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.popAbnormal.ws.client.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.Constants;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MonitorResource {

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
}
