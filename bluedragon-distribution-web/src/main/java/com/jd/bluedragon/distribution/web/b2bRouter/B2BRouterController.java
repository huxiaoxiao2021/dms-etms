package com.jd.bluedragon.distribution.web.b2bRouter;

import com.jd.bluedragon.distribution.api.response.B2BRouterResponse;
import com.jd.bluedragon.distribution.api.response.CrossBoxResponse;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.service.B2BRouterService;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.web.crossbox.CrossBoxController;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by xumei3 on 2018/2/24.
 */
@Controller
@RequestMapping("/b2bRouter")
public class B2BRouterController {
    private static final Logger logger = Logger.getLogger(CrossBoxController.class);

    @Autowired
    B2BRouterService b2bRouterService;

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

    @ResponseBody
    @RequestMapping("/check")
    public B2BRouterResponse<String> check(B2BRouter router) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
        try {
            if (router != null) {
                // 路线校验
                if (b2bRouterService.isHasRouter(router)) {
                    b2bRouterResponse.setCode(CrossBoxResponse.CODE_WARN);
                    b2bRouterResponse.setMessage("已存在" + router.getSiteNameFullLine() + "的路由，是否更新?");
                    return b2bRouterResponse;
                }
            }
            b2bRouterResponse.setCode(CrossBoxResponse.CODE_NORMAL);
        } catch (Exception e) {
            b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("检查是否存在异常，请检查数据是否选择正确");
            logger.error("执行B网路由配置检查是否存在操作异常：", e);
        }
        return b2bRouterResponse;
    }
}
