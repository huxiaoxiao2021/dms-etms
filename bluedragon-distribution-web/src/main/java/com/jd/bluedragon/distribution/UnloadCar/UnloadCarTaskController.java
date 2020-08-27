package com.jd.bluedragon.distribution.UnloadCar;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.inventory.controller.inventoryTaskController;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;

/**
 * @author lijie
 * @date 2020/6/22 0:31
 */
@Controller
@RequestMapping("/unloadCarTask")
public class UnloadCarTaskController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(inventoryTaskController.class);

    @Autowired
    private UnloadCarService unloadCarService;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    /* 返回主页面 */
    @Authorization(Constants.DMS_WEB_UNLOAD_CAR_TASK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        return "/unloadCar/unloadCarTask";
    }

    @RequestMapping("/listData")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_UNLOAD_CAR_TASK_R)
    public PagerResult<UnloadCarTask> listData(@RequestBody UnloadCarCondition condition) {

        return unloadCarService.queryByCondition(condition);
    }

    @RequestMapping("/distributeTask")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_UNLOAD_CAR_TASK_R)
    public JdResponse distributeTask(@RequestBody DistributeTaskRequest request) {
        JdResponse result = new JdResponse<>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            request.setUpdateUserErp(erpUser.getUserCode());
            request.setUpdateUserName(erpUser.getUserName());
            if (unloadCarService.distributeTask(request)) {
                result.setCode(JdResponse.CODE_SUCCESS);
                result.setMessage(JdResponse.MESSAGE_SUCCESS);
            }
        } else {
            result.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_SERVICE_ERROR);
            result.setMessage("获取Erp用户信息失败，结果为null，请重新登陆！");
        }

        return result;
    }

    @RequestMapping("/getUserName")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_UNLOAD_CAR_TASK_R)
    public JdResponse<String> distributeTask(@QueryParam("unloadUser") String unloadUser) {
        JdResponse<String> result = new JdResponse<>();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(unloadUser);
        if(baseStaffSiteOrgDto != null){
            result.setData(baseStaffSiteOrgDto.getStaffName());
        }
        return result;
    }

}
