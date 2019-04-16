package com.jd.bluedragon.distribution.web.sortscheme;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeDetailService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeSyncService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wuzuxiang on 2017/1/11.
 */
@Controller
@RequestMapping("/sortSchemeSync")
public class SortSchemeSyncController {

    private static final Log logger = LogFactory.getLog(SortSchemeSyncController.class);

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";

    @Autowired
    SortSchemeSyncService sortSchemeSyncService;

    @Autowired
    SortSchemeService sortSchemeService;

    @Autowired
    SortSchemeDetailService sortSchemeDetailService;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Autowired
    BaseService baseService;

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/index" ,method = RequestMethod.GET)
    public String index(Model model){
        this.logger.debug("分拣计划同步数据 --> index");
        try{
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if(erpUser != null){
                String userCode = "";
                Integer siteCode = 0;
                String siteName = "";
                userCode = erpUser.getUserCode() == null ? "none":erpUser.getUserCode();
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if(bssod != null && bssod.getSiteType() == 64){/** 站点类型为64的时候为分拣中心 **/
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
                model.addAttribute("siteCode",String.valueOf(siteCode));
                model.addAttribute("siteName",siteName);
            }
        }catch(Exception e){
            logger.info("没有维护分拣中心，初始化加载失败");
        }
        return "sortscheme/sort-scheme-sync";
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/sync",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> sortSchemeSync(String siteCode){
        InvokeResult<String> result = new InvokeResult<String>();
        result.setCode(400);
        result.setMessage("请求成功无法响应");
        if("".equals(siteCode) || null == siteCode ){
            logger.error("正在同步的分拣中心ID为空");
            return  result;
        }
        logger.info("分拣计划配置同步导入开始-->分拣中心ID:" + siteCode);
        String url = PropertiesHelper.newInstance().getValue(prefixKey + siteCode);

        boolean bool = false;
        try{
            bool = sortSchemeSyncService.sync(HTTP + url,siteCode);
            if(bool){
                logger.info("分拣中心分拣计划执行同步成功");
                result.setCode(200);
                result.setMessage("分拣中心分拣计划执行同步成功");
            }else{
                logger.info("分拣中心分拣计划执行同步失败");
                result.setCode(10000);
                result.setMessage("分拣中心分拣计划执行同步失败");
            }
        }catch (Exception e){
            logger.error("程序执行异常:",e);
            result.setCode(500);
            result.setMessage("程序执行异常，稍后再试");
        }
        return result;
    }
}
