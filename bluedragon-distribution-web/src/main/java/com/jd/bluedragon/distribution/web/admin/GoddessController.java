package com.jd.bluedragon.distribution.web.admin;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log logger= LogFactory.getLog(GoddessController.class);

    @Resource(name = "goddessService")
    private GoddessService goddessService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Pager<String> pager, Model model) {
        pager.init();
        try {
            model.addAttribute("model",goddessService.query(pager));
        }catch (Throwable throwable){
            logger.error("ERROR",throwable);
            Pager<List<Goddess>> result=new Pager<List<Goddess>>();
            result.setData(new ArrayList<Goddess>(1));
            result.setPageNo(pager.getPageNo());
            result.init();
            result.setTableName("执行异常"+throwable.getMessage());
            model.addAttribute("model",result);
            //throw throwable;
        }
        return "admin/goddess/index";
    }
}
