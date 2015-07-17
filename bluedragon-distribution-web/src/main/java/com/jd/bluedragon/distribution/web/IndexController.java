package com.jd.bluedragon.distribution.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.common.web.cookie.CookieUtils;
import com.jd.etms.basic.domain.BaseDataDict;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-26 下午06:30:06 首页显示类
 */
@Controller
public class IndexController {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
	private BaseMajorManager baseMajorManager;
    
    @Autowired
    private CookieUtils cookieUtils;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage() {
        return "index";
    }
    
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        this.logger.debug("IndexController --> index");
        return "index";
    }
    
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public String top(Model model) {
        this.logger.debug("IndexController --> top");
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        Integer userId = erpUser.getUserId();
        String roleName = null;
        try {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                    .getBaseStaffByStaffId(userId);
            if (baseStaffSiteOrgDto != null) {
                List<BaseDataDict> roleList = this.baseMajorManager.getBaseDataDictList(100, 2, 100);
                for (BaseDataDict dict : roleList) {
                    if (dict.getTypeCode().equals(baseStaffSiteOrgDto.getRole())) {
                        roleName = dict.getTypeName();
                    }
                }
            }
        } catch (Exception e) {
            this.logger.error("首页显示类-->获取基础资料信息异常：", e);
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(roleName)) {
            roleName = "管理员";
        }
        model.addAttribute("username", erpUser.getUserName());
        model.addAttribute("roleName", roleName);
        return "topFrame";
    }
    
    @RequestMapping(value = "/left", method = RequestMethod.GET)
    public String left() {
        this.logger.debug("IndexController --> left");
        return "leftFrame";
    }
    
    @RequestMapping("/quit")
    public ModelAndView quit(HttpServletRequest request, HttpServletResponse response, Model model) {
        this.cookieUtils.invalidate(request, response);
        ModelAndView mav = new ModelAndView();
        RedirectView view = new RedirectView("/index");
        mav.setView(view);
        return mav;// "index";
    }
}
