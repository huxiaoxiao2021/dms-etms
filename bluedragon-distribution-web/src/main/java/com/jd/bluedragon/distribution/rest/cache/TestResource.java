package com.jd.bluedragon.distribution.rest.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transportation.common.utils.JsonHelper;

/**
 * 
 * @ClassName: CacheResource
 * @Description: 缓存管理rest
 * @author: wuyoude
 * @date: 2018年5月3日 下午1:48:32
 *
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TestResource {

    private static final Log logger= LogFactory.getLog(TestResource.class);

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    /**
     * 获取缓存的数据
     * @param key
     * @return
     */
    @POST
    @GZIP
    @Path("/test")
    public Object test(TestRequest request){
    	Object bean = SpringHelper.getBean(request.beanId);
    	if(bean != null) {
    		Class c = bean.getClass();
    		try {
    			Method[] ms  =c.getMethods();
				if( ms!= null
						&& ms.length > 0) {
					for(Method m:ms) {
						if(m.getName().equals(request.method)) {
							Class<?>[] pts = m.getParameterTypes();
							int ptl = 0;
							int pml = 0;
							if(pts != null) {
								ptl = pts.length;
							}
							if(request.params != null) {
								pml = request.params.length;
							}
							if(ptl == pml) {
								Object[] params = new Object[pml];
								for(int i=0;i<ptl;i++) {
									params[i] = JsonHelper.fromJson(JsonHelper.toJson(request.params[i]), pts[i]);
								}
								return m.invoke(bean, params);
							}else {
								continue;
							}
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
    	}
       return null;
    }
    public static class TestRequest{
    	private String beanId;
    	private String method;
    	private Object[]  params;
		public String getBeanId() {
			return beanId;
		}
		public void setBeanId(String beanId) {
			this.beanId = beanId;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public Object[] getParams() {
			return params;
		}
		public void setParams(Object[] params) {
			this.params = params;
		}
    }
}
