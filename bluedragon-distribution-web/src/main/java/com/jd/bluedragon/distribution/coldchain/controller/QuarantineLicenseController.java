package com.jd.bluedragon.distribution.coldchain.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lixin39
 * @Description 冷链卡班检疫证票号管理
 * @ClassName QuarantineLicenseController
 * @date 2019/01/16
 */
@Controller
@RequestMapping("/coldchain/quarantinelicense")
public class QuarantineLicenseController {

    @Value("${DMS_BUSINESS_ADDRESS}")
    private String businessUrl;

    @RequestMapping("/index")
    public String index() {
        return "redirect:" + businessUrl + "/quarantineLicense/index";
    }

}
