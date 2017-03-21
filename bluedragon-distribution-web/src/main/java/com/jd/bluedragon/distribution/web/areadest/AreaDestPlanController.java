package com.jd.bluedragon.distribution.web.areadest;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestPlanRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestPlanResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.fastjson.serializer.InetAddressCodec;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/18.
 */
@Controller
@RequestMapping("/areaDestPlan")
public class AreaDestPlanController {

    private final static Log logger = LogFactory.getLog(AreaDestPlanController.class);

    @Autowired
    private AreaDestPlanService areaDestPlanService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 功能首页
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            if (erpUser != null) {
                //BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
                dto.setSiteCode(910);
                dto.setSiteName("北京马驹桥分拣中心");
                model.addAttribute("currentSiteCode", dto.getSiteCode());
                model.addAttribute("currentSiteName", dto.getSiteName());
            } else {
                logger.error("获取erp用户信息失败，结果为null");
            }
        } catch (Exception e) {
            logger.error("获取始发分拣中心信息发生异常", e);
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
            if (data != null && data.size() > 0) {
                pager.setData(data);
                response.setData(pager);
                response.setCode(JdResponse.CODE_OK);
            } else {
                response.setCode(JdResponse.CODE_OK_NULL);
                response.setMessage(JdResponse.MESSAGE_OK_NULL);
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("查询龙门架发货关系方案列表失败", e);
        }
        return response;
    }

    @RequestMapping(value = "/addView", method = RequestMethod.GET)
    public String addView(Model model, Integer machineId) {
        try {
            if (machineId != null) {
                buildAddView(model, machineId);
            }
        } catch (Exception e) {
            logger.error("跳转新增方案页面发生异常", e);
        }
        return "areadest/add";
    }

    private void buildAddView(Model model, Integer machineId) {
        //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
        erpUser.setUserCode("lixin456");
        erpUser.setUserId(320246);
        if (erpUser != null) {
            //BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
            dto.setSiteCode(910);
            dto.setSiteName("北京马驹桥分拣中心");
            if (dto != null) {
                model.addAttribute("currentSiteCode", dto.getSiteCode());
                model.addAttribute("currentSiteName", dto.getSiteName());
                model.addAttribute("machineId", machineId);
            } else {
                logger.error("根据Erp用户信息获取基础信息失败，结果为null");
            }
        } else {
            logger.error("获取Erp用户信息失败，结果为null");
        }
    }

    /**
     * 新增方案
     *
     * @param request
     * @return
     */
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
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            erpUser.setUserId(320246);
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
            logger.error("新增龙门架发货关系方案发生异常", e);
        }
        return response;
    }

    private AreaDestPlan requestToDomain(AreaDestPlanRequest request, ErpUserClient.ErpUser erpUser) {
        AreaDestPlan areaDestPlan = new AreaDestPlan();
        BeanUtils.copyProperties(request, areaDestPlan);
        areaDestPlan.setCreateUser(erpUser.getUserCode());
        areaDestPlan.setCreateUserCode(erpUser.getUserId());
        return areaDestPlan;
    }

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
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            erpUser.setUserId(320246);
            if (erpUser != null) {
                if (areaDestPlanService.delete(planId, erpUser.getUserCode(), erpUser.getUserId())) {
                    response.setCode(JdResponse.CODE_OK);
                    response.setMessage("删除成功！");
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null！");
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("删除龙门架发货关系方案发生异常", e);
        }
        return response;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Model model, Integer planId) {
        try {
            AreaDestPlan plan = areaDestPlanService.get(planId);
            if (plan != null){
                model.addAttribute("machineId", plan.getMachineId());
                model.addAttribute("planId", plan.getPlanId());
                model.addAttribute("planName", plan.getPlanName());
            }
        } catch (Exception e) {
            logger.error("跳转查询方案详情页面发生异常", e);
        }
        return "areadest/detail";
    }

}
