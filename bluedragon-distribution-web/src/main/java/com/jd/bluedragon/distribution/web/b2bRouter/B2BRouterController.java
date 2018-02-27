package com.jd.bluedragon.distribution.web.b2bRouter;

import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.web.crossbox.CrossBoxController;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by xumei3 on 2018/2/24.
 */
@Controller
@RequestMapping("/b2bRouter")
public class B2BRouterController {
    private static final Logger logger = Logger.getLogger(CrossBoxController.class);

    @RequestMapping("/index")
    public String index() {
        return "b2bRouter/list";
    }

    public String doAdd(B2BRouter router){

        return null;
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        return "b2bRouter/add";
    }
}
