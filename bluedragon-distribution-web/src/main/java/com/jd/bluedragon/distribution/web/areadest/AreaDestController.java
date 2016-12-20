package com.jd.bluedragon.distribution.web.areadest;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestResponse;
import com.jd.bluedragon.distribution.api.response.AreaDestTree;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
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
@RequestMapping("/areadest")
public class AreaDestController {

    private final static Log logger = LogFactory.getLog(AreaDestController.class);

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            model.addAttribute("createSiteCode", dto.getSiteCode());
            model.addAttribute("createSiteName", dto.getSiteName());
        } catch (Exception e) {
            logger.error("获取始发分拣中心信息失败", e);
        }
        return "areaDest/list";
    }

    /**
     * 获取区域批次目的地列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse getAreaDestList(@RequestBody AreaDestRequest request) {
        AreaDestResponse<List<AreaDestTree>> response = new AreaDestResponse<List<AreaDestTree>>();
        try {
            List<AreaDestTree> result = areaDestService.getTree(request.getCreateSiteCode(), request.getTransferSiteCode(), request.getReceiveSiteCode());
            if (result != null && result.size() > 0) {
                response.setData(result);
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
     * @return
     */
    @RequestMapping(value = "/addview", method = RequestMethod.GET)
    public String addView(Model model) {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        model.addAttribute("createSiteCode", dto.getSiteCode());
        model.addAttribute("createSiteName", dto.getSiteName());
        model.addAttribute("allOrgs", removeInvalidOrg(baseService.getAllOrg()));
        return "areaDest/add";
    }

    /**
     * 移除无效的机构
     *
     * @param allOrgs
     * @return
     */
    private List<BaseOrg> removeInvalidOrg(List<BaseOrg> allOrgs) {
        if (allOrgs != null && allOrgs.size() > 0) {
            Iterator<BaseOrg> iterator = allOrgs.iterator();
            while (iterator.hasNext()) {
                BaseOrg org = iterator.next();
                List<SimpleBaseSite> sites = baseMajorManager.getDmsListByOrgId(org.orgId);
                if (sites == null || sites.size() == 0) {
                    iterator.remove();
                }
            }
        }
        return allOrgs;
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
            return areaDestService.getList(createSiteCode, transferSiteCode, null);
        } catch (Exception e) {
            logger.error("获取机构下的分拣中心失败", e);
            return null;
        }
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
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (request.getCreateSiteCode() != null && request.getCreateSiteCode() > 0 && request.getTransferSiteCode() != null && request.getTransferSiteCode() > 0) {
                // 设置所有目的分拣中心站点无效
                boolean isSuccess = doDisable(request, erpUser.getUserCode(), erpUser.getUserId());
                if (isSuccess && this.doSave(request, erpUser)) {
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

    private boolean doDisable(AreaDestRequest request, String updateUser, Integer updateUserCode) {
        boolean isSuccess = true;
        if (request.getReceiveSiteOrg() != null && request.getReceiveSiteOrg() > 0) {
            List<SimpleBaseSite> siteList = baseMajorManager.getDmsListByOrgId(request.getReceiveSiteOrg());
            for (SimpleBaseSite site : siteList) {
                areaDestService.disable(request.getCreateSiteCode(), request.getTransferSiteCode(), site.getSiteCode(), updateUser, updateUserCode);
            }
        } else {
            isSuccess = areaDestService.disable(request.getCreateSiteCode(), request.getTransferSiteCode(), null, updateUser, updateUserCode);
        }
        return isSuccess;
    }

    /**
     * 保存
     *
     * @param request
     * @param erpUser
     * @return
     */
    private boolean doSave(AreaDestRequest request, ErpUserClient.ErpUser erpUser) {
        if (request.getReceiveSiteCodeName() != null && request.getReceiveSiteCodeName().size() > 0) {
            List<AreaDest> areaDests = new ArrayList<AreaDest>();
            for (String codeName : request.getReceiveSiteCodeName()) {
                if (codeName != null && !"".equals(codeName)) {
                    String[] arr = codeName.split(" ");
                    if (arr.length > 1) {
                        AreaDest area = new AreaDest();
                        area.setCreateSiteCode(request.getCreateSiteCode());
                        area.setCreateSiteName(request.getCreateSiteName());
                        area.setTransferSiteCode(request.getTransferSiteCode());
                        area.setTransferSiteName(request.getTransferSiteName());
                        area.setReceiveSiteCode(Integer.valueOf(arr[0]));
                        area.setReceiveSiteName(arr[1]);
                        area.setCreateUser(erpUser.getUserCode());
                        area.setCreateUserCode(erpUser.getUserId());
                        areaDests.add(area);
                    }
                }
            }
            return areaDestService.add(areaDests);
        }
        return true;
    }

}
