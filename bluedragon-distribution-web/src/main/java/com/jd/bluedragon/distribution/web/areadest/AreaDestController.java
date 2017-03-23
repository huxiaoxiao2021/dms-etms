package com.jd.bluedragon.distribution.web.areadest;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.RouteType;
import com.jd.ql.basic.dto.SimpleBaseSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse getList(AreaDestRequest request, Pager<List<AreaDest>> pager) {
        AreaDestResponse<Pager<List<AreaDest>>> response = new AreaDestResponse<Pager<List<AreaDest>>>();
        try {
            Integer planId = request.getPlanId();
            Integer routeType = request.getRouteType();
            if (planId == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数错误：获取编方案编号为null");
                return response;
            }

            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<AreaDest>>(Pager.DEFAULT_PAGE_NO);
            }

            List<AreaDest> result;
            if (routeType != null) {
                result = areaDestService.getList(planId, RouteType.getEnum(request.getRouteType()), pager);
            } else {
                result = areaDestService.getList(planId, null, pager);
            }

            pager.setData(result);
            response.setData(pager);
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("获取区域批次目的地树形列表数据异常", e);
        }
        return response;
    }

    /**
     * 根据机构id获取对应分拣中心
     *
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/dmsList", method = RequestMethod.GET)
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
     * 根据方案编号和路线类型获取关系列表
     *
     * @param planId
     * @param routeType
     * @return
     */
    @RequestMapping(value = "/getSelected", method = RequestMethod.GET)
    @ResponseBody
    public List<AreaDest> getSelected(Integer planId, Integer routeType) {
        try {
            return areaDestService.getList(planId, RouteType.getEnum(routeType));
        } catch (Exception e) {
            logger.error("获取机构下的分拣中心失败", e);
        }
        return null;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse save(AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            erpUser.setUserId(320246);
            Integer count = areaDestService.getCount(request);
            if (count != null) {
                if (count > 0) {
                    response.setCode(JdResponse.CODE_SEE_OTHER);
                    response.setMessage("新增失败，该关系已经存在，请勿重复添加！");
                    return response;
                } else {
                    if (areaDestService.add(buildAreaDestDomain(request, erpUser))) {
                        response.setCode(JdResponse.CODE_OK);
                        response.setMessage(JdResponse.MESSAGE_OK);
                        return response;
                    }
                }
            }
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        } catch (Exception e) {
            logger.error("新增发货关系时发生异常", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private AreaDest buildAreaDestDomain(AreaDestRequest request, ErpUserClient.ErpUser erpUser) {
        AreaDest areaDest = new AreaDest();
        areaDest.setPlanId(request.getPlanId());
        areaDest.setRouteType(request.getRouteType());
        areaDest.setCreateSiteCode(request.getCreateSiteCode());
        areaDest.setCreateSiteName(request.getCreateSiteName());
        areaDest.setTransferSiteCode(request.getTransferSiteCode());
        areaDest.setTransferSiteName(request.getTransferSiteName());
        areaDest.setReceiveSiteCode(request.getReceiveSiteCode());
        areaDest.setReceiveSiteName(request.getReceiveSiteName());
        areaDest.setCreateUser(erpUser.getUserCode());
        areaDest.setCreateUserCode(erpUser.getUserId());
        return areaDest;
    }

    /**
     * 批量保存
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveBatch", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse saveBatch(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            erpUser.setUserId(00320246);
            if (areaDestService.addBatch(request, erpUser.getUserCode(), erpUser.getUserId()) > 0) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
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
    @RequestMapping(value = "/delBatch", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse delBatch(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            //ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("lixin456");
            if (areaDestService.disable(request, erpUser.getUserCode(), erpUser.getUserId())) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        } catch (Exception e) {
            logger.error("批量移除目的站点失败", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

}
