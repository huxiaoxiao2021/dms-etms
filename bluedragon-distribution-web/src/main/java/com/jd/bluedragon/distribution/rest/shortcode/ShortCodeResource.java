package com.jd.bluedragon.distribution.rest.shortcode;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.ShortCodeUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hanjiaxing1 on 2018/8/16.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ShortCodeResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @GET
    @Path("/short/encode/{key}/{packageCode}")
    public JdResponse<String> encode(@PathParam("key") String key,  @PathParam("packageCode") String packageCode) {

        JdResponse<String> jdResponse = new JdResponse<>();
        jdResponse.toSucceed();
        String packageCodeKey = key + Constants.SEPARATOR_HYPHEN + packageCode;
        String code = jimdbCacheService.get(packageCodeKey);
        if (code != null) {
            jdResponse.setData(code);
        } else {
            String[] resCodes = ShortCodeUtil.encode(key, packageCode);
            for (String resCode : resCodes) {
                String shortCodeKey = key + Constants.SEPARATOR_HYPHEN + resCode;
                String packageCodeValue = jimdbCacheService.get(shortCodeKey);
                if (packageCodeValue == null) {
                    //更新缓存，缓存24小时
                    jimdbCacheService.setEx(shortCodeKey, packageCode, 24 * Constants.TIME_SECONDS_ONE_HOUR);
                    jimdbCacheService.setEx(packageCodeKey, resCode, 24 * Constants.TIME_SECONDS_ONE_HOUR);
                    jdResponse.setData(resCode);
                    break;
                }
            }
        }

        return jdResponse;
    }

    @GET
    @Path("/short/decode/{key}/{code}")
    public JdResponse<String> decode(@PathParam("key") String key, @PathParam("code") String code) {

        JdResponse<String> jdResponse = new JdResponse<>();
        jdResponse.toSucceed();
        String shortCodeKey = key + Constants.SEPARATOR_HYPHEN + code;
        String packageCodeValue = jimdbCacheService.get(shortCodeKey);
        if (StringHelper.isEmpty(packageCodeValue)) {
            jdResponse.toError("简码没有对应的信息！");
        } else {
            jdResponse.setData(packageCodeValue);
        }

        return jdResponse;
    }
}
