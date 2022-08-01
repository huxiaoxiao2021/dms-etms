package com.jd.bluedragon.distribution.UnloadCar;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.external.enums.AppVersionEnums;
import com.jd.bluedragon.distribution.inventory.controller.inventoryTaskController;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.util.List;

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
        int loginSiteCode = -1;
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && DmsConstants.SITE_TYPE_DMS.equals(loginUser.getSiteType())){
            loginSiteCode = loginUser.getSiteCode();
        }
        model.addAttribute("loginSiteCode",loginSiteCode);
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
        log.info("distributeTask|分配卸车任务:request={}", JSON.toJSONString(request));
        JdResponse result = new JdResponse<>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            request.setUpdateUserErp(erpUser.getUserCode());
            request.setUpdateUserName(erpUser.getUserName());
            try {
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(request.getUnloadUserErp());
                if (baseStaffSiteOrgDto == null) {
                    log.warn("distributeTask|分配卸车任务的负责人erp不存在:request={}", JSON.toJSONString(request));
                    result.toFail("卸车负责人ERP不存在，请确认erp是否输入正确");
                    return result;
                }
                String staffName = baseStaffSiteOrgDto.getStaffName();
                // 如果分配的负责人姓名为空或者与青龙基础资料不一致，则以青龙基础资料为准
                if (StringUtils.isBlank(request.getUnloadUserName()) || !request.getUnloadUserName().equals(staffName)) {
                    if (StringUtils.isNotBlank(staffName)) {
                        request.setUnloadUserName(staffName);
                    }
                }

                List<UnloadCar> unloadCarList = unloadCarService.getTaskInfoBySealCarCodes(request.getSealCarCodes());
                //
                if(CollectionUtils.isNotEmpty(unloadCarList)) {
                    String msgCodes = "";
                    int count = 0;
                    for(UnloadCar uc : unloadCarList) {
                        if(StringUtils.isNotBlank(uc.getVersion()) && AppVersionEnums.PDA_GUIDED.getVersion().equals(uc.getVersion())) {
                            msgCodes =  StringUtils.isEmpty(msgCodes) ? uc.getVersion() : (msgCodes + "," + uc.getVersion());
                            count++;
                        }
                    }
                    if(count > 0) {
                        log.warn("distributeTask|分配卸车任务的负责人封车编码已被新版APP操作:request={}，新版操作封车编码为={}", JSON.toJSONString(request), msgCodes);
                        result.toFail(count+"个任务正被新版app操作，无法领取，封车编码为："+msgCodes);
                        return result;
                    }
                }
                if (unloadCarService.distributeTask(request)) {
                    result.setCode(JdResponse.CODE_SUCCESS);
                    result.setMessage(JdResponse.MESSAGE_SUCCESS);
                }

            } catch (LoadIllegalException e) {
                log.error("分配卸车任务发生异常：e=", e);
                result.setCode(JdResponse.CODE_FAIL);
                result.setMessage(e.getMessage());
                return result;
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
        } else {
            result.toFail("卸车负责人ERP不存在，请确认erp是否输入正确");
        }
        return result;
    }

}
