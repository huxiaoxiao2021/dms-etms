package com.jd.bluedragon.distribution.web.admin;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtingwei on 2017/2/17.
 */
@Controller
@RequestMapping("/admin/goddess")
public class GoddessController {

    private static final Logger log = LoggerFactory.getLogger(GoddessController.class);

    @Resource(name = "goddessService")
    private GoddessService goddessService;

    @Authorization(Constants.DMS_WEB_SORTING_GODDESS_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Pager<String> pager, Model model) {
        pager.init();
        try {
            model.addAttribute("model",goddessService.query(pager));
        }catch (Throwable throwable){
            log.error("ERROR",throwable);
            Pager<List<Goddess>> result=new Pager<List<Goddess>>();
            result.setData(new ArrayList<Goddess>(1));
            result.setPageNo(pager.getPageNo());
            result.init();
            result.setTableName("执行异常"+throwable.getMessage());
            model.addAttribute("model",result);
            //throw throwable;
        }

        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(1);
        businessLogProfiler.setBizType(Constants.BIZTYPE_URL_CLICK);
        businessLogProfiler.setOperateType(Constants.GODDESSLOG_CLICK);
        JSONObject request=new JSONObject();
        request.put("operatorName",erpUser.getUserName());
        request.put("operatorCode",erpUser.getUserCode());
        businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));
        BusinessLogWriter.writeLog(businessLogProfiler);

        return "admin/goddess/index";
    }
}
