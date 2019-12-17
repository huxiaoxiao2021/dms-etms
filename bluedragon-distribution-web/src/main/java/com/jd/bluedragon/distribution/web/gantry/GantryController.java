package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.authorization.RestAuthorization;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.SimpleBaseSite;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/9
 */
@Controller
@RequestMapping("/gantry")
public class GantryController {
    private static final Logger log = LoggerFactory.getLogger(GantryController.class);
    @Autowired
    private BaseService baseService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private GantryDeviceService gantryDeviceService;

    @Autowired
    private RestAuthorization restAuthorization;

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String gantryPageList(Model model) {
        try {
            List<BaseOrg> allOrgs = baseService.getAllOrg();
            model.addAttribute("allOrgs", allOrgs);
        } catch (Exception e){
            log.error("获取所有机构失败",e);
        }
        return "gantry/gantryList";
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/dmsList", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleBaseSite> queryDmsListByOrg(Integer orgId){
        try {
            return baseMajorManager.getDmsListByOrgId(orgId);
        } catch (Exception e) {
            log.error("获取机构下的所有分拣中心失败",e);
            return null;
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/addShow", method = RequestMethod.GET)
    public String addGantryDevice(Model model){
        try {
            List<BaseOrg> allOrgs = baseService.getAllOrg();
            model.addAttribute("allOrgs", allOrgs);
        } catch (Exception e) {
            log.error("获取所有机构信息失败",e);
        }
        return "/gantry/gantryAdd";
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/modifyShow", method = RequestMethod.GET)
    public String modifyGantryDevice(Model model, Integer id){
        try {
            List<BaseOrg> allOrgs = baseService.getAllOrg();
            model.addAttribute("allOrgs", allOrgs);
            GantryDeviceRequest request = new GantryDeviceRequest();
            request.setMachineId((long)id);
            Map<String, Object> map = buildParam(request);
            List<GantryDevice> gantryDevices = gantryDeviceService.getGantry(map);
            model.addAttribute("gantryDevice", gantryDevices.get(0));
        } catch (Exception e) {
            log.error("获取所有机构信息失败",e);
        }
        return "/gantry/gantryModify";
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/doAdd", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult doAddGantry(GantryDeviceRequest request){
        InvokeResult result = new InvokeResult();
        try{
            checkAddParam(request);
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            request.setOperateName(erpUser.getUserName());
            gantryDeviceService.addGantry(GantryDevice.fromGantryRequest(request));
        } catch (Exception e){
            log.error("插入龙门架信息失败", e);
            if(e instanceof IllegalArgumentException){
                IllegalArgumentException exception = (IllegalArgumentException)e;
                result.setCode(10000);
                result.setMessage(exception.getMessage());
            }else{
                result.setCode(20000);
                result.setMessage("服务异常，请稍后再试");
            }
            return result;
        }
        result.setCode(200);
        result.setMessage("龙门架新增成功");
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/doDel", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult doDelGantry(Integer id){
        InvokeResult result = new InvokeResult();
        try{
            gantryDeviceService.delGantryById(id);
        } catch (Exception e){
            log.error("删除龙门架信息失败", e);
            result.setCode(20000);
            result.setMessage("服务异常，请稍后再试");
            return result;
        }
        result.setCode(200);
        result.setMessage("龙门架删除成功");
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/doModify", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult doModifyGantry(GantryDeviceRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            checkAddParam(request);
            if (null == request.getMachineId()) {
                throw new IllegalArgumentException("龙门架参数错误");
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            request.setOperateName(erpUser.getUserName());
            gantryDeviceService.updateGantryById(GantryDevice.fromGantryRequest(request));
        } catch (Exception e) {
            log.error("更新龙门架信息失败", e);
            if (e instanceof IllegalArgumentException) {
                IllegalArgumentException exception = (IllegalArgumentException)e;
                result.setCode(10000);
                result.setMessage(exception.getMessage());
            } else {
                result.setCode(20000);
                result.setMessage("服务异常，请稍后再试");
            }
            return result;
        }
        result.setCode(200);
        result.setMessage("龙门架更新成功");
        return result;
    }

    private void checkAddParam(GantryDeviceRequest request) throws IllegalArgumentException{
        if(null == request) {
            throw new IllegalArgumentException("龙门架参数错误");
        }
        if(null == request.getOrgCode()) {
            throw new IllegalArgumentException("请选择龙门架所属机构");
        }
        if(null == request.getSiteCode()) {
            throw new IllegalArgumentException("请选择龙门架所属分拣中心");
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRY_R)
    @RequestMapping(value = "/doQuery", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<GantryDevice>>> queryGantryByParam(GantryDeviceRequest request
                                            , Pager<List<GantryDevice>> pager){
        InvokeResult<Pager<List<GantryDevice>>> result = new InvokeResult<Pager<List<GantryDevice>>>();
        try{
            Map<String, Object> params = buildParam(request);
            if(null == pager){
                pager = new Pager<List<GantryDevice>>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<List<GantryDevice>>(pager.getPageNo(), pager.getPageSize());
            }
            params.putAll(ObjectMapHelper.makeObject2Map(pager));

            List<GantryDevice> deviceList = buildToken(gantryDeviceService.getGantryPage(params));
            Integer totalSize = gantryDeviceService.getGantryCount(params);
            pager.setTotalSize(totalSize);
            pager.setData(deviceList);
            result.setData(pager);
            result.setCode(200);
            result.setMessage("查询龙门架成功");
        } catch (Exception e){
            result.setCode(10000);
            result.setMessage("查询龙门架失败");
            log.error("查询龙门架失败", e);
        }

        return result;
    }


    private Map<String, Object> buildParam(GantryDeviceRequest request){
        Map<String, Object> param = new HashMap<String, Object>();
        if (null == request) {
            return param;
        }
        param.put("machineId", request.getMachineId());
        param.put("orgCode", request.getOrgCode());
        param.put("siteCode", request.getSiteCode());
        if (StringHelper.isNotEmpty(request.getSupplier())) {
            param.put("supplier", request.getSupplier());
        }
        return param;
    }

    private List<GantryDevice> buildToken(List<GantryDevice> gantryDevices){
        for(GantryDevice gantryDevice : gantryDevices){
            gantryDevice.setToken(restAuthorization.generateAuthorizationCode(String.valueOf(gantryDevice.getMachineId())));
        }
        return gantryDevices;
    }
}
