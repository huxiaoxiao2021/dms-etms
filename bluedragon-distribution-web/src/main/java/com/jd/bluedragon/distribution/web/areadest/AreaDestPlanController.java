package com.jd.bluedragon.distribution.web.areadest;

import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestPlanRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestPlanResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanDetailService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/18.
 */
@Controller
@RequestMapping("/areaDestPlan")
public class AreaDestPlanController {

    private final static Logger log = LoggerFactory.getLogger(AreaDestPlanController.class);

    @Autowired
    private AreaDestPlanService areaDestPlanService;

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private AreaDestPlanDetailService areaDestPlanDetailService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    /**
     * 功能首页
     *
     * @param model
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                model.addAttribute("currentSiteCode", dto.getSiteCode());
                model.addAttribute("currentSiteName", dto.getSiteName());
            } else {
                log.error("获取erp用户信息失败，结果为null");
            }
        } catch (Exception e) {
            log.error("获取始发分拣中心信息发生异常", e);
        }
        return "areadest/list";
    }

    /**
     * 获取方案分页列表
     *
     * @param request
     * @param pager
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestPlanResponse<Pager<List<AreaDestPlan>>> getList(Model model, AreaDestPlanRequest request, Pager<List<AreaDestPlan>> pager) {
        AreaDestPlanResponse<Pager<List<AreaDestPlan>>> response = new AreaDestPlanResponse<Pager<List<AreaDestPlan>>>();
        try {
            Integer operateSiteCode = request.getOperateSiteCode();
            if (operateSiteCode == null || operateSiteCode == 0) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数错误：获取当前站点编号为null");
                return response;
            }
            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<AreaDestPlan>>(Pager.DEFAULT_PAGE_NO);
            }
            List<AreaDestPlan> data = areaDestPlanService.getList(operateSiteCode, request.getMachineId(), pager);
            pager.setData(data);
            response.setData(pager);
            response.setCode(JdResponse.CODE_OK);
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("查询龙门架发货关系方案列表失败", e);
        }
        return response;
    }

    /**
     * 获取方案的所有列表
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/getAllList", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestPlanResponse<List<AreaDestPlan>> getAllList(AreaDestPlanRequest request) {
        AreaDestPlanResponse<List<AreaDestPlan>> response = new AreaDestPlanResponse<List<AreaDestPlan>>();
        try {
            Integer operateSiteCode = request.getOperateSiteCode();
            if (operateSiteCode == null || operateSiteCode == 0) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数错误：获取当前站点编号为null");
                return response;
            }
            List<AreaDestPlan> data = areaDestPlanService.getList(operateSiteCode, request.getMachineId());
            response.setData(data);
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("查询龙门架发货关系方案列表失败", e);
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/addView", method = RequestMethod.GET)
    public String addView(Model model, Integer machineId) {
        try {
            if (machineId != null) {
                model.addAttribute("machineId", machineId);
            }
            buildSiteViewParam(model);
        } catch (Exception e) {
            log.error("跳转新增方案页面发生异常", e);
        }
        return "areadest/add";
    }

    private void buildSiteViewParam(Model model) {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            if (dto != null) {
                model.addAttribute("currentSiteCode", dto.getSiteCode());
                model.addAttribute("currentSiteName", dto.getSiteName());
            } else {
                log.error("根据Erp用户信息获取基础信息失败，结果为null");
            }
        } else {
            log.error("获取Erp用户信息失败，结果为null");
        }
    }

    /**
     * 新增方案
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestPlanResponse save(@RequestBody AreaDestPlanRequest request) {
        AreaDestPlanResponse response = new AreaDestPlanResponse();
        Integer operateSiteCode = request.getOperateSiteCode();
        Integer machineId = request.getMachineId();
        String planName = request.getPlanName();
        if (operateSiteCode == null || machineId == null) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("参数错误：获取当前站点编号或龙门架编号为null！");
            return response;
        }
        if (planName == null || !StringUtils.isNotEmpty(StringUtils.trim(planName))) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("参数错误：方案名称输入非法！");
            return response;
        }
        if (areaDestPlanService.isRepeatName(operateSiteCode, planName)) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("方案名称已存在，请重新输入！");
            return response;
        }
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                if (areaDestPlanService.add(requestToDomain(request, erpUser))) {
                    response.setCode(JdResponse.CODE_OK);
                } else {
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null");
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("新增龙门架发货关系方案发生异常", e);
        }
        return response;
    }

    /**
     * 获取当前分拣中心的当前龙门架设备的在用发货方案
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/getMayPlan", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<AreaDestPlanDetail> queryMyPlan(AreaDestPlanRequest request) {
        InvokeResult<AreaDestPlanDetail> result = new InvokeResult<AreaDestPlanDetail>();
        result.setCode(400);
        result.setMessage("请求成功，无返回结果！！！");
        result.setData(null);

        if (null != request) {
            if (this.log.isInfoEnabled()) {
                this.log.info("分拣中心{}的龙门架{}获取当前的方案--AreaDestPlanController.queryMyPlan",request.getOperateSiteCode(),request.getMachineId());
            }
            try {
                AreaDestPlanDetail plan = areaDestPlanDetailService.getByScannerTime(request.getMachineId(), request.getOperateSiteCode(), new Date());
                result.setCode(200);
                result.setMessage("请求成功");
                result.setData(plan);
            } catch (Exception e) {
                this.log.error("获取当前分拣中心当前龙门架设备的发货方案异常，龙门架ID为：{}", request.getMachineId(), e);
            }
        }
        return result;
    }

    private AreaDestPlan requestToDomain(AreaDestPlanRequest request, ErpUserClient.ErpUser erpUser) {
        AreaDestPlan areaDestPlan = new AreaDestPlan();
        BeanUtils.copyProperties(request, areaDestPlan);
        areaDestPlan.setCreateUser(erpUser.getUserCode());
        areaDestPlan.setCreateUserCode(erpUser.getStaffNo());
        return areaDestPlan;
    }

    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JdResponse delete(Integer planId) {
        JdResponse response = new JdResponse();
        if (planId == null) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("参数错误：获取方案编号为null！");
            return response;
        }
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                if (areaDestPlanService.isUsing(planId)) {
                    response.setCode(JdResponse.CODE_SEE_OTHER);
                    response.setMessage("该方案正在处于启用状态，请释放后再操作！");
                } else {
                    if (areaDestPlanService.delete(planId, erpUser.getUserCode(), erpUser.getStaffNo())) {
                        areaDestService.disable(planId, erpUser.getUserCode(), erpUser.getStaffNo());
                        response.setCode(JdResponse.CODE_OK);
                        response.setMessage("删除成功！");
                    }
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null！");
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("删除龙门架发货关系方案发生异常", e);
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Model model, Integer id) {
        try {
            AreaDestPlan plan = areaDestPlanService.get(id);
            if (plan != null) {
                model.addAttribute("machineId", plan.getMachineId());
                model.addAttribute("planId", plan.getPlanId());
                model.addAttribute("planName", plan.getPlanName());
            }
        } catch (Exception e) {
            log.error("跳转查询方案详情页面发生异常", e);
        }
        return "areadest/detail";
    }

    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public String config(Model model, Integer id) {
        try {
            buildSiteViewParam(model);
            AreaDestPlan plan = areaDestPlanService.get(id);
            if (plan != null) {
                model.addAttribute("machineId", plan.getMachineId());
                model.addAttribute("planId", plan.getPlanId());
                model.addAttribute("planName", plan.getPlanName());
                model.addAttribute("allOrgs", baseService.getAllOrg());
            }
        } catch (Exception e) {
            log.error("跳转配置页面发生异常", e);
        }
        return "areadest/config";
    }

}
