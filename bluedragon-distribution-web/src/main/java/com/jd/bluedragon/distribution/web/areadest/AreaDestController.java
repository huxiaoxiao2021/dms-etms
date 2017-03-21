package com.jd.bluedragon.distribution.web.areadest;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.RouteType;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.SimpleBaseSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 区域批次目的地
 * <p>
 * Created by lixin39 on 2016/12/9.
 */
@Controller
@RequestMapping("/areaDest")
public class AreaDestController {

    private final static Log logger = LogFactory.getLog(AreaDestController.class);

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    /**
     * 获取区域批次目的地列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse getList(@RequestBody AreaDestRequest request, Pager<List<AreaDest>> pager) {
        AreaDestResponse<Pager<List<AreaDest>>> response = new AreaDestResponse<Pager<List<AreaDest>>>();
        try {
            Integer planId = request.getPlanId();
            if (planId == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数错误：获取编方案编号为null");
                return response;
            }
            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<AreaDest>>(Pager.DEFAULT_PAGE_NO);
            }

            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
            List<AreaDest> result = areaDestService.getList(request.getPlanId(), RouteType.getEnum(request.getRouteType()), pager);
            if (result != null && result.size() > 0) {
                pager.setData(result);
                response.setData(pager);
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
            } else {
                response.setCode(JdResponse.CODE_OK_NULL);
                response.setMessage(JdResponse.MESSAGE_OK_NULL);
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("获取区域批次目的地树形列表数据异常", e);
        }
        return response;
    }

    /**
     * 跳转新增页
     *
     * @param model
     * @return
     */
    /*@Authorization("DMS-WEB-ADD-AREA-DEST")*/
    @RequestMapping(value = "/addview", method = RequestMethod.GET)
    public String addView(Model model) {
        try {
            this.buildAddView(model);
        } catch (Exception e) {
            logger.error("跳转新增页面失败", e);
        }
        return "areadest/add";
    }

    /**
     * 无权限控制跳转新增页
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/addviewunauth", method = RequestMethod.GET)
    public String addViewUnAuth(Model model) {
        try {
            this.buildAddView(model);
        } catch (Exception e) {
            logger.error("跳转无权限新增页面失败", e);
        }
        return "areadest/add";
    }

    private void buildAddView(Model model) {
        //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
        erpUser.setUserCode("lixin456");
        if (erpUser != null) {
            //BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
            dto.setSiteCode(910);
            dto.setSiteName("北京马驹桥分拣中心");
            if (dto != null) {
                model.addAttribute("createSiteCode", dto.getSiteCode());
                model.addAttribute("createSiteName", dto.getSiteName());
            } else {
                logger.error("根据erp用户信息获取基础信息失败，结果为null");
            }
            model.addAttribute("allOrgs", removeInvalidOrg(baseService.getAllOrg()));
        } else {
            logger.error("获取erp用户信息失败，结果为null");
        }
    }

    /**
     * 移除无分拣中心的机构
     *
     * @param allOrg
     * @return
     */
    private List<BaseOrg> removeInvalidOrg(List<BaseOrg> allOrg) {
        if (allOrg != null && allOrg.size() > 0) {
            Iterator<BaseOrg> iterator = allOrg.iterator();
            while (iterator.hasNext()) {
                BaseOrg org = iterator.next();
                List<SimpleBaseSite> sites = baseMajorManager.getDmsListByOrgId(org.orgId);
                if (sites == null || sites.size() == 0) {
                    iterator.remove();
                }
            }
        }
        return allOrg;
    }

    /**
     * 根据机构id获取对应分拣中心
     *
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/dmslist", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleBaseSite> queryDmsListByOrg(Integer orgId) {
        try {
            return baseMajorManager.getDmsListByOrgId(orgId);
        } catch (Exception e) {
            logger.error("获取机构下的所有分拣中心失败", e);
            return null;
        }
    }

    /**
     * 根据始发分拣中心、中转分拣中心和目的分拣中心所属机构获取目的分拣中心列表
     *
     * @param createSiteCode
     * @param transferSiteCode
     * @return
     */
    @RequestMapping(value = "/dmsselected", method = RequestMethod.GET)
    @ResponseBody
    public List<AreaDest> queryDmsByTransfer(Integer createSiteCode, Integer transferSiteCode) {
        try {
            //return areaDestService.getList(createSiteCode, transferSiteCode, null);
        } catch (Exception e) {
            logger.error("获取机构下的分拣中心失败", e);
            return null;
        }
        return null;
    }

    /**
     * 批量保存
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse save(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            erpUser.setUserId(00320246);
            if (request.getCreateSiteCode() != null && request.getCreateSiteCode() > 0 && request.getTransferSiteCode() != null && request.getTransferSiteCode() > 0) {
                if (areaDestService.saveOrUpdate(request, erpUser.getUserCode(), erpUser.getUserId())) {
                    response.setCode(JdResponse.CODE_OK);
                    response.setMessage(JdResponse.MESSAGE_OK);
                } else {
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            logger.error("批量保存目的分拣中心失败", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 批量移除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse remove(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            if (request.getCreateSiteCode() != null && request.getCreateSiteCode() > 0 && request.getTransferSiteCode() != null && request.getTransferSiteCode() > 0) {
                if (doDisable(request, erpUser.getUserCode(), erpUser.getUserId())) {
                    response.setCode(JdResponse.CODE_OK);
                    response.setMessage(JdResponse.MESSAGE_OK);
                } else {
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            logger.error("批量移除目的分拣中心失败", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 设置目的地为无效
     *
     * @param request
     * @param updateUser
     * @param updateUserCode
     * @return
     */
    private boolean doDisable(AreaDestRequest request, String updateUser, Integer updateUserCode) {
        List<String> codeNameList = request.getReceiveSiteCodeName();
        if (codeNameList != null && codeNameList.size() > 0) {
            List<Integer> siteCodeList = new ArrayList<Integer>();
            for (String code : codeNameList) {
                siteCodeList.add(Integer.valueOf(code));
            }
            return areaDestService.disable(request.getCreateSiteCode(), request.getTransferSiteCode(), siteCodeList, updateUser, updateUserCode);
        }
        return true;
    }

}
