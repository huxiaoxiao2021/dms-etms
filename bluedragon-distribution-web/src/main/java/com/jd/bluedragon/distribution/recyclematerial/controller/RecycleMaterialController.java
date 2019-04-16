package com.jd.bluedragon.distribution.recyclematerial.controller;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lixin39
 * @Description 物资管理跳转
 * @ClassName RecycleMaterialController
 * @date 2018/12/10
 */
@Controller
@RequestMapping("/recyclematerial")
public class RecycleMaterialController {

    @Value("${DMS_BUSINESS_ADDRESS}")
    private String businessUrl;

    @Authorization(Constants.DMS_WEB_TOOL_RECYCLEMATERIAL_R)
    @RequestMapping("/index")
    public String index() {
        return "redirect:" + businessUrl + "/recycleMaterialManager/index";
    }

}
