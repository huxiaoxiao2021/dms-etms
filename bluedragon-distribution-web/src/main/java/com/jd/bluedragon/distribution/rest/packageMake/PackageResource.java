package com.jd.bluedragon.distribution.rest.packageMake;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

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
    private BaseMajorManager baseMajorManager;

    @GET
    @Path("/packageMake/packageRePrint/{barCode}/{waybillSign}/{siteId}/{operateName}")
    public JdResponse packageRePrint(@PathParam("barCode") String barCode,
                                  @PathParam("waybillSign") String waybillSign,
                                  @PathParam("siteId") Integer siteId,
                                  @PathParam("operateName") String operateName){
        JdResponse jdResponse = new JdResponse();
        try{
            BaseStaffSiteOrgDto bDto = null;
            Integer siteType = 0;
            if(siteId != null){
                bDto = baseMajorManager.getBaseSiteBySiteId(siteId);
            }
            if(bDto != null){
                siteType = bDto.getSiteType();
            }
            if(siteType != 0 && StringHelper.isNotEmpty(waybillSign)){
                //操作人所在机构是配送站并且waybillSign第八位是1或2或3的触发全程跟踪
                if(siteType == 4 && (BusinessHelper.isSignChar(waybillSign,8,'1' ) || // 1 仅修改地址
                        BusinessHelper.isSignChar(waybillSign,8,'2') ||             // 2 修改地址和其他
                        BusinessHelper.isSignChar(waybillSign,8,'3')                // 3 未修改地址仅修改其他
                )){
                    if(barCode != null && operateName != null){
                        taskService.add(this.toTask(barCode, operateName));
                        jdResponse.setCode(200);
                        return jdResponse;
                    }
                }
            }
        }catch (Exception e){
            this.logger.warn("包裹补打触发全程跟踪失败",e);
        }
        jdResponse.setCode(400);
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
