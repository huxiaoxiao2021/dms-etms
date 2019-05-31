package com.jd.bluedragon.distribution.rest.inventory;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class InventoryResource {
    private final static Logger logger = Logger.getLogger(InventoryResource.class);

    @Autowired
    private InventoryTaskService inventoryTaskService;

    /**
     * 获取卡位列表
     * @param siteCode
     * @return
     */
    @GET
    @Path("/inventory/getDirectionList/{siteCode}")
    public JdResult<List<SiteEntity>> getDirectionList(@PathParam("siteCode") Integer siteCode) {
        JdResult<List<SiteEntity>> result = new JdResult<List<SiteEntity>>();
        result.toSuccess();

        if (siteCode == null || siteCode <= 0) {
            result.toError("操作站点不能为空.");
        }
        List<SiteEntity> siteEntityList = new ArrayList<>();

        //调service的接口获取列表
        result.setData(siteEntityList);
        return result;
    }

    /**
     * 生成盘点任务
     * @param request
     * @return
     */
    @POST
    @Path("/inventory/addInventoryTask")
    public JdResult<InventoryTaskResponse> addInventoryTask(InventoryTaskRequest request) {
        logger.info("生成盘点任务参数:" + JSON.toJSONString(request));
        JdResult result = new JdResult();
        result.toSuccess();

        try {
            return inventoryTaskService.addInventoryTask(request);
        } catch (Exception e) {
            logger.error("生成盘点任务异常.参数:"+JSON.toJSONString(request)+",异常原因:", e);
            result.toError("生成盘点任务异常");
        }
        return result;
    }
}
