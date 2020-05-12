package com.jd.bluedragon.distribution.rest.shortcode;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusServiceImpl;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ShortCodeUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.common.Result;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private WaybillService waybillService;

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

    @GET
    @Path("/short/trace/{key}/{code}")
    public JdResponse<String> trace(@PathParam("key") String key, @PathParam("code") String code) {

        JdResponse<String> jdResponse = new JdResponse<>();
        jdResponse.toSucceed();
        String packageCode;
        if (WaybillUtil.isPackageCode(code)) {
            packageCode = code;
        } else {
            String shortCodeKey = key + Constants.SEPARATOR_HYPHEN + code;
            packageCode = jimdbCacheService.get(shortCodeKey);
            if (StringHelper.isEmpty(packageCode)) {
                jdResponse.toError("简码没有对应的信息！");
                return jdResponse;
            }
        }

        String waybillCode = WaybillUtil.getWaybillCode(code);
        Task task = new Task();
        Date date = new Date();
        String longTimeStr = String.valueOf(date.getTime());
        String body = "{\n  \"boxCode\" : \"\",\n  \"waybillCode\" : \""+ waybillCode + "\",\n  \"packageCode\" : \"" + packageCode + "\",\n  \"orgId\" : 6,\n  \"orgName\" : \"总公司\",\n  \"createSiteCode\" : 870292,\n  \"createSiteType\" : 64,\n  \"createSiteName\" : \"青龙UAT分拣中心02\",\n  \"operatorId\" : 20457728,\n  \"operator\" : \"韩嘉星\",\n  \"operateType\" : 0,\n  \"operateTime\" : " + longTimeStr + "\n}";
        task.setBody(body);
        task.setCreateSiteCode(870292);
        task.setExecuteTime(new Date());
        task.setFingerprint("98BEE6209A604670E3AA03EE8B303305");
        task.setKeyword1(waybillCode);
        task.setKeyword2(packageCode);
        task.setOwnSign("DMS");
        task.setSequenceName("SEQ_TASK_WAYBILL");
        task.setTableName("task_waybill");
        task.setType(0);
        boolean result = waybillService.doWaybillStatusTask(task);

        return jdResponse;
    }
}
