package com.jd.bluedragon.distribution.web.gantryException;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.authorization.RestAuthorization;
import com.jd.ql.basic.domain.BaseOrg;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/9
 */
@Controller
@RequestMapping("/gantryException")
public class GantryExceptionController {
    private static final Log logger = LogFactory.getLog(GantryExceptionController.class);
    @Autowired
    private BaseService baseService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private GantryExceptionService gantryExceptionService;

    @Autowired
    private RestAuthorization restAuthorization;

    @RequestMapping(value = "/gantryExceptionList", method = RequestMethod.GET)
    public String gantryExceptionPageList(Model model) {
        try {
            List<BaseOrg> allOrgs = baseService.getAllOrg();
            model.addAttribute("allOrgs", allOrgs);
        } catch (Exception e){
            logger.error("获取所有机构失败",e);
        }
        return "gantryException/gantryExceptionList";
    }


    private void checkAddParam(HttpServletRequest request) throws IllegalArgumentException{
        if(null == request.getParameter("machineId")) {
            throw new IllegalArgumentException("请选择龙门架");
        }
        if(null == request.getParameter("beginTime")) {
            throw new IllegalArgumentException("请选择开始时间");
        }
    }


    @RequestMapping(value = "/doQuery", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<GantryException>>> queryGantryExceptionByParam(HttpServletRequest request
                                            , Pager<List<GantryException>> pager){
        InvokeResult<Pager<List<GantryException>>> result = new InvokeResult<Pager<List<GantryException>>>();
        try{
            String machineId = request.getParameter("machineId");
            String beginTime = request.getParameter("beginTime");
            String endTime = request.getParameter("endTime");
            String isSend = request.getParameter("isSend");
            Map<String, Object> params = new HashedMap();
            params.put("machineId", machineId);
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
            params.put("isSend", isSend);

            if(null == pager){
                pager = new Pager<List<GantryException>>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<List<GantryException>>(pager.getPageNo(), pager.getPageSize());
            }
            params.putAll(ObjectMapHelper.makeObject2Map(pager));

            List<GantryException> gantryExceptionList = gantryExceptionService.getGantryExceptionPage(params);
            Integer totalSize = gantryExceptionService.getGantryExceptionCount(params);
            pager.setTotalSize(totalSize);
            pager.setData(gantryExceptionList);
            result.setData(pager);
            result.setCode(200);
            result.setMessage("查询龙门架异常信息成功");
        } catch (Exception e){
            result.setCode(10000);
            result.setMessage("查询龙门架异常信息失败");
            logger.error("查询龙门架异常信息失败", e);
        }

        return result;
    }


//    private Map<String, Object> buildParam(GantryExceptionRequest request){
//        Map<String, Object> param = new HashMap<String, Object>();
//        if (null == request) {
//            return param;
//        }
//        param.put("machineId", request.getMachineId());
//        param.put("e", request.getOrgCode());
//        param.put("siteCode", request.getSiteCode());
//        if (StringHelper.isNotEmpty(request.getSupplier())) {
//            param.put("supplier", request.getSupplier());
//        }
//        return param;
//    }
}
