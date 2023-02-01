package com.jd.bluedragon.distribution.web;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ssa.oidc.client.interceptor.ErpSsoInterceptor;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-26 下午06:30:06 首页显示类
 */
@Controller
public class IndexController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    //获取一级域名的正则
    private static final String RE_DOMAIN = "[0-9a-zA-Z]+\\.((360buy.com)|(jd.com))";

    @Autowired
    private BaseMajorManager baseMajorManager;

    //    @Autowired
//    private CookieUtils cookieUtils;
    @Value("${mixedConfigUrl}")
    private String mixedConfigUrl;

    @Value("${zyWeightConfigUrl:}")
    private String zyWeightConfigUrl;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private ErpSsoInterceptor springSSOInterceptor;

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage() {
        this.log.debug("IndexController --> welcomePage");
        if (!checkBasicInfo()) {
            return "reject";
        }
        return "index";
    }

    /**
     * 检查有没有基础资料
     *
     * @return
     */
    private boolean checkBasicInfo() {
        LoginContext loginContext = LoginContext.getLoginContext();
        if (loginContext != null) {
            BaseStaffSiteOrgDto user = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            if (user == null) {
                return false;
            }
        }
        return true;
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        this.log.debug("IndexController --> index");
        if (!checkBasicInfo()) {
            return "reject";
        }
        return "index";
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public String top(Model model) {
        this.log.debug("IndexController --> top");
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
            this.log.error("首页显示类-->获取基础资料信息异常：", e);
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(roleName)) {
            roleName = "管理员";
        }
        model.addAttribute("username", erpUser.getUserName());
        model.addAttribute("roleName", roleName);
        model.addAttribute("usercode", erpUser.getUserCode());
        return "topFrame";
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/left", method = RequestMethod.GET)
    public String left(Model model) {
        this.log.debug("IndexController --> left");
        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
        try {
            erpUser = ErpUserClient.getCurrUser();

            model.addAttribute("mixedConfigUrl", mixedConfigUrl);
            model.addAttribute("zyWeightConfigUrl",zyWeightConfigUrl);
            model.addAttribute("userName", erpUser.getUserName());
            model.addAttribute("userCode", erpUser.getStaffNo());

            boolean flag = Objects.equals(uccPropertyConfiguration.getPackConsumableSwitch(),1)
                    || Objects.equals(uccPropertyConfiguration.getPackConsumableSwitch(), 3);

            model.addAttribute("banAddAndDeleteFlag", flag);

        } catch (Exception e) {
            //菜单不处理异常信息
            log.error("获取当前用户失败", e);
        }

        return "leftFrame";
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/quit")
    public void quit(HttpServletRequest request, HttpServletResponse response, Model model) {
        InputStream in = getClass().getResourceAsStream("/authen.properties");
        Properties pps = new Properties();
        try {
            pps.load(in);
            String logoutKey = "sso.logout.address";
            String logoutUrl = pps.getProperty(logoutKey);
            String domainName = "webapp.domain.name";
            String returnUrl = pps.getProperty(domainName);

            response.sendRedirect(logoutUrl + "?ReturnUrl=" + returnUrl);

            if (!returnUrl.contains(".jd.com")) {
                Pattern p = Pattern.compile(RE_DOMAIN);
                Matcher m = p.matcher(returnUrl);
                //获取一级域名
                while(m.find()){
                    springSSOInterceptor.toLogout(request, response);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
