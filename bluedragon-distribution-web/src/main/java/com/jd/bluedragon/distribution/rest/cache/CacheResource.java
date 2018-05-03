package com.jd.bluedragon.distribution.rest.cache;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.ql.dms.common.cache.CacheService;

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
public class CacheResource {

    private static final Log logger= LogFactory.getLog(CacheResource.class);

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    /**
     * 获取缓存的数据
     * @param key
     * @return
     */
    @GET
    @GZIP
    @Path("/cache/get/{key}")
    public String get(@PathParam("key") String key){
       return jimdbCacheService.get(key);
    }
    /**
     * 获取缓存的hash单个值
     * @param key
     * @param keyField
     * @return
     */
    @GET
    @GZIP
    @Path("/cache/hGet/{key}/{keyField}")
    public String hGet(@PathParam("key") String key,@PathParam("keyField") String keyField){
       return jimdbCacheService.hGet(key, keyField);
    }
    /**
     * 获取缓存的hash所有值
     * @param key
     * @return
     */
    @GET
    @GZIP
    @Path("/cache/hGetAll/{key}")
    public Map<String,String> hGetAll(@PathParam("key") String key){
       return jimdbCacheService.hGetAll(key);
    }
}
