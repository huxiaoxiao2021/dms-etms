package com.jd.bluedragon.distribution.web.admin;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.worker.domain.TBTaskType;
import com.jd.bluedragon.distribution.worker.service.TBTaskTypeService;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * WORKER配置
 * Created by wangtingwei on 2015/9/29.
 */
@Controller
@RequestMapping("worker-setting")
public class WorkerSettingController {

    private static final Logger log = LoggerFactory.getLogger(WorkerSettingController.class);

    @Autowired
    private TBTaskTypeService tbTaskTypeService;
    private static final String MODIFY_USERS = "modifyUsers";
    private static final List<String> modifyUsers;

    static {
        modifyUsers = Arrays.asList(PropertiesHelper.newInstance()
                .getValue(WorkerSettingController.MODIFY_USERS).split(Constants.SEPARATOR_COMMA));
    }

    /**
     * 任务列表
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_WORKER_SETTING_R)
    @RequestMapping(value = "/index")
    public String index(Pager<String> splitPager, Model model) throws Exception{
        splitPager.init();
        try {
            model.addAttribute("model",tbTaskTypeService.readByName(splitPager));
            return "admin/worker-setting/index";
        }catch (Exception throwable){
            log.error("ERROR",throwable);
            throw throwable;
        }

    }

    @Authorization(Constants.DMS_WEB_DEVELOP_WORKER_SETTING_R)
    @RequestMapping(value = "/create",method = RequestMethod.GET)
    public String create(HttpServletRequest request,Model model) throws Exception{
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (null!=erpUser&&WorkerSettingController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
            model.addAttribute("lastUrl", request.getHeader("referer"));
            return "admin/worker-setting/create";
        }else{
            return "index";
        }
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_WORKER_SETTING_R)
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String create(TBTaskType domain,String lastLastUrl,Model model) throws Exception {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (null!=erpUser&&WorkerSettingController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
            try {
                tbTaskTypeService.inserSingle(domain);
                return "redirect:" + lastLastUrl;
            } catch (Exception ex) {
                log.error("ERROR", ex);
                throw ex;
            }
        }else{
            return "index";
        }
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_WORKER_SETTING_R)
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    public String edit(int id,HttpServletRequest request,Model model) throws Exception{
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (null!=erpUser&& WorkerSettingController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
            try {
                model.addAttribute("model", tbTaskTypeService.readById(id));
                model.addAttribute("lastUrl", request.getHeader("referer"));
                return "admin/worker-setting/edit";
            } catch (Exception e) {
                log.error("ERROR", e);
                throw e;
            }
        }else {
            return "index";
        }

    }

    @Authorization(Constants.DMS_WEB_DEVELOP_WORKER_SETTING_R)
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public String edit(TBTaskType domain,String lastLastUrl,Model model) throws Exception{
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (null!=erpUser&&WorkerSettingController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
            try {
                tbTaskTypeService.updateSingleById(domain);
                return "redirect:" + lastLastUrl;
            } catch (Exception ex) {
                log.error("ERROR", ex);
                throw ex;
            }
        }else {
            return "index";
        }
    }
}
