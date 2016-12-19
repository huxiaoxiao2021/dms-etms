package com.jd.bluedragon.distribution.web.gantryException;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceRequest;
import com.jd.bluedragon.distribution.api.request.GantryExceptionRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
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
    private GantryDeviceService gantryDeviceService;
    @Autowired
    private GantryExceptionService gantryExceptionService;

    @RequestMapping(value = "/gantryExceptionList", method = RequestMethod.GET)
    public String gantryExceptionPageList(GantryExceptionRequest request, Model model) {

        try {
            if (request.getSiteCode() != null) {
                List<GantryDevice> allDevice = gantryDeviceService.getGantryByDmsCode(request.getSiteCode());
                model.addAttribute("allDevice", allDevice);
            }
        } catch (Exception e){
            logger.error("获取分拣中心编号为" +request.getSiteCode() + "的龙门架失败",e);
        }
        model.addAttribute("queryParam", request);

        return "gantryException/gantryExceptionList";
    }


    private void checkAddParam(GantryExceptionRequest request) throws IllegalArgumentException{
        if(! StringHelper.isNotEmpty(request.getStartTime())) {
            throw new IllegalArgumentException("请选择开始时间");
        }
        if(! StringHelper.isNotEmpty(request.getEndTime())) {
            throw new IllegalArgumentException("请选择结束时间");
        }
    }


    @RequestMapping(value = "/doQuery", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<GantryException>>> queryGantryExceptionByParam(GantryExceptionRequest request
                                            , Pager<List<GantryException>> pager){
        InvokeResult<Pager<List<GantryException>>> result = new InvokeResult<Pager<List<GantryException>>>();
        try{
//            checkAddParam(request);
            Map<String, Object> params = this.buildParam(request);

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


    private Map<String, Object> buildParam(GantryExceptionRequest request){
        Map<String, Object> param = new HashMap<String, Object>();
        if (null == request) {
            return param;
        }
        param.put("machineId", request.getMachineId());

        param.put("startTime", request.getStartTime());
        param.put("endTime", request.getEndTime());
        param.put("isSend", request.getIsSend());
        Integer siteCodeInteger = new Integer(910);
        param.put("siteCode", siteCodeInteger);
        return param;
    }
}
