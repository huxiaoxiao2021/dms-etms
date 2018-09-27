package com.jd.bluedragon.distribution.rest.packageMake;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hujiping on 2018/4/4.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PackageResource {

    private final Log logger= LogFactory.getLog(PackageResource.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisManager redisManager;

    public static String RE_PRINT_PREFIX = "RE_PRINT_CODE_";
    @GET
    @Path("/packageMake/packageRePrint/{barCode}/{waybillSign}/{siteId}/{operateName}")
    public JdResponse packageRePrint(@PathParam("barCode") String barCode,
                                     @PathParam("waybillSign") String waybillSign,
                                     @PathParam("siteId") Integer siteId,
                                     @PathParam("operateName") String operateName){
        JdResponse jdResponse = new JdResponse();
        if(StringHelper.isEmpty(barCode)){
            logger.error("包裹号"+barCode+"为空，不能触发包裹补打的全程跟踪!");
            jdResponse.setCode(400);
            jdResponse.setMessage("包裹号"+barCode+"为空，不能触发包裹补打的全程跟踪!");
        }
        if(StringHelper.isEmpty(operateName)){
            operateName = "-1";
        }

        redisManager.setex(RE_PRINT_PREFIX+barCode, 3600, barCode);//1小时

        taskService.add(this.toTask(barCode, operateName));
        jdResponse.setCode(JdResponse.CODE_OK);
        logger.info("触发包裹补打的全程跟踪成功,"+"包裹号"+barCode+",操作人"+operateName);
        return jdResponse;
    }

    @GET
    @Path("/package/checkRePrint/{barCode}")
    public JdResponse checkRePrint(@PathParam("barCode") String barCode){
        //1. 从redis中获得补打操作的条码缓存
        JdResponse jdResponse = new JdResponse();
        jdResponse.setCode(JdResponse.CODE_OK);
        if(StringHelper.isNotEmpty(barCode)) {
            String barCodeCached = redisManager.getCache(RE_PRINT_PREFIX+barCode);
            if(StringHelper.isNotEmpty(barCodeCached)){
                jdResponse.setCode(JdResponse.CODE_RE_PRINT_IN_ONE_HOUR);
                jdResponse.setMessage(JdResponse.MESSAGE_RE_PRINT_IN_ONE_HOUR);
            }
        }

        return jdResponse;
    }

    private Task toTask(String barCode, String operateName){
        WaybillStatus waybillStatus = new WaybillStatus();
        waybillStatus.setWaybillCode(barCode);
        waybillStatus.setOperator(operateName);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }
}