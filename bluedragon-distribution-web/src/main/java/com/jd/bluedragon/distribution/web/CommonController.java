package com.jd.bluedragon.distribution.web;

import com.google.common.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/common")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    /**
     * 基础资料接口
     */
    @Autowired
    private BaseService baseService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    RedisManager redisManager;

    private static final String ALL_SITES_KEY = "Q_ALL_SITES_KEY";

    /**
     * 获取机构
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_COMMON_R)
    @ResponseBody
    @RequestMapping("/getBaseOrg")
    public Object getBaseOrg() {
        return this.baseService.getAllOrg();
    }

    /**
     * 根据机构获取分拣中心
     *
     * @param orgCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_COMMON_R)
    @ResponseBody
    @RequestMapping("/getSiteByOrg")
    public Object getSiteByOrg(Integer orgCode) {
        List<BaseStaffSiteOrgDto> list = null;
        for (int m = 1; m < 4; m++) {
            try {
                list = this.baseMajorManager.getBaseSiteByOrgIdSiteType(orgCode,
                        Constants.DMS_SITE_TYPE);
                if (list != null) {
                    break;
                }
                CommonController.log.error("第{}次获取机构下站点信息失败",m);
            } catch (Exception e) {
                CommonController.log.error("调用基础资料发生异常,orgCode={}",orgCode, e);
            }
        }
        return list;
    }

    /**
     * 根据站点名称获取站点
     *
     * @param name
     * @return
     */
    @Authorization(Constants.DMS_WEB_COMMON_R)
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/getSiteByName")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.CommonController.getSiteByName", mState = {JProEnum.TP})
    public Object getSiteByName(String name) {
        List<BaseStaffSiteOrgDto> list = null;
        String temp = redisManager.get(CommonController.ALL_SITES_KEY);
        if (JsonHelper.isJsonString(temp)) {
            list = JsonHelper.fromJsonUseGson(temp, new TypeToken<List<BaseStaffSiteOrgDto>>() {
            }.getType());
        }
        if (list == null || list.size() == 0) {
            list = this.baseMajorManager.getBaseSiteAll();
            redisManager.setex(CommonController.ALL_SITES_KEY, 24 * 3600, JsonHelper.toJson(list));
        }
        //TO DO

        List<BaseStaffSiteOrgDto> data = new ArrayList<BaseStaffSiteOrgDto>();
        for (BaseStaffSiteOrgDto site : list) {
            if (site.getSiteName().startsWith(name)) {
                data.add(site);
            }
        }
        return data;
    }

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @RequestMapping("/getClickInterval")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.CommonController.getClickInterval", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public @ResponseBody JdResponse<Integer> getClickInterval() {
        JdResponse<Integer> response = new JdResponse<>();
        response.toSucceed();
        response.setData(uccPropertyConfiguration.getClickIntervalSecond());
        return response;
    }
}
