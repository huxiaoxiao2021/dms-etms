package com.jd.bluedragon.distribution.base.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: PermissionControlController
 * @Description: 权限控制controller
 * @author: hujiping
 * @date: 2019/4/1 18:47
 */
@Controller
@RequestMapping("permission")
public class DmsBasePermissionControlController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(DmsBasePermissionControlController.class);

    @RequestMapping("/toIndex/{code}")
    public String permissionControl(@PathVariable("code") String code, Model model){

        model.addAttribute("code",code);
        return "/401";
    }

}
