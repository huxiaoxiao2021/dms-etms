package com.jd.bluedragon.distribution.recyclematerial.controller;

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

    @RequestMapping("/index")
    public String index() {
        return "redirect:" + businessUrl + "/recyclematerial/manager/index";
    }

}
