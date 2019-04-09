package com.jd.bluedragon.distribution.web.sysconfig;

import java.util.*;
import java.util.regex.Pattern;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.SysConfigContentResponse;
import com.jd.bluedragon.distribution.api.response.SysConfigResponse;
import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.client.DefaultDataSubscriber;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SpringHelper;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Controller
@RequestMapping("/sysconfig")
public class SysconfigController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String MODIFY_USERS = "modifyUsers";
    private static final List<String> modifyUsers;

    @Autowired
    private BaseService baseService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private RedisManager redisManager;
    @Autowired
    private SysConfigDao sysConfigDao;

    static {
        modifyUsers = Arrays.asList(PropertiesHelper.newInstance()
                .getValue(SysconfigController.MODIFY_USERS).split(Constants.SEPARATOR_COMMA));
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String querySysconfig(SysConfig sysConfig, Pager<SysConfig> pager, Model model) {
        logger.debug("基础设置查询");
        try {

//			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
//			if(erpUser == null){
//				return "index";
//			}
//			
//			logger.info("访问sqlkit/toView用户erp账号：[" + erpUser.getUserCode() + "]");
//
//			if (!SysconfigController.modifyUsers.contains(erpUser.getUserCode().toLowerCase())) {
//				logger.info("用户erp账号：" + erpUser.getUserCode() + "不在查询用户列表中,跳转到/index");
//				return "index";
//			}

            Map<String, Object> params = ObjectMapHelper.makeObject2Map(sysConfig);

            // 设置分页对象
            if (pager == null) {
                pager = new Pager<SysConfig>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<SysConfig>(pager.getPageNo(), pager.getPageSize());
            }

            String configName = sysConfig != null && sysConfig.getConfigName() != null ? sysConfig.getConfigName() + "%" : "%";

            // 获取总数量
            int totalsize = baseService.totalSysconfigSizeByParams(configName);
            pager.setTotalSize(totalsize);

            params.put("configName", configName);
            params.putAll(ObjectMapHelper.makeObject2Map(pager));


            List<SysConfig> sysconfigal = baseService.queryConfigByKey(params);
            model.addAttribute("sysconfigal", sysconfigal);
            model.addAttribute("sysConfigDto", sysConfig);
            model.addAttribute("pager", pager);
        } catch (Exception e) {
            logger.error("基础设置查询异常：", e);
            model.addAttribute("errormsg", "基础设置查询异常：" + e.getMessage());
        }
        return "sysconfig/sysconfig_list";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/getSwitchList", method = RequestMethod.GET)
    public String getSwitchList(Model model) {
        List<SysConfig> dataList = this.sysConfigService.getSwitchList();
        for (SysConfig sysConfig : dataList) {
            String redisContent = redisManager.hget(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName());
            sysConfig.setRedisContent(redisContent);
        }
        model.addAttribute("dataList", dataList);
        return "sysconfig/switchList";

    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/syncRedis", method = RequestMethod.GET)
    public String syncRedis(Model model, Long id) {
        SysConfig sysConfig = this.baseService.getSysConfig(id);
        redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
        return getSwitchList(model);
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    public String open(Model model, Long id) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigId(id);
        sysConfig.setConfigContent("1");
        this.baseService.updateSysConfig(sysConfig);
        sysConfig = this.baseService.getSysConfig(id);
        redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
        return getSwitchList(model);


    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/close", method = RequestMethod.GET)
    public String close(Model model, Long id) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigId(id);
        sysConfig.setConfigContent("0");
        this.baseService.updateSysConfig(sysConfig);
        sysConfig = this.baseService.getSysConfig(id);
        redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
        return getSwitchList(model);


    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/toAdd")
    public String toAdd() {
        return "sysconfig/add";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/addSwitch")
    public String addSwitch(SysConfig sysConfig, Model model) {
        baseService.insertSysConfig(sysConfig);
        return getSwitchList(model);
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(SysConfig sysConfig, Pager pager, Model model) {
        try {
            baseService.insertSysConfig(sysConfig);
        } catch (Exception e) {
            logger.error("规则增加失败", e);
            model.addAttribute("errormsg", "规则增加失败：" + e.getMessage());
        }
        return querySysconfig(null, null, model);
    }

    /**
     * 分拣机有用到，暂时不加
     * @param configName
     * @return
     */
//    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @JProfiler( jKey = "DMS.WEB.SysconfigController.findConfigContentByConfigName", jAppName = Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    @RequestMapping(value = "/findConfigContentByConfigName/{configName}", method = RequestMethod.GET)
    @ResponseBody
    public SysConfigContentResponse findConfigContentByConfigName(@PathVariable String configName) {
        Assert.notNull(configName, "configName must not be null");
        this.logger.info("configName's " + configName);
        SysConfigContentResponse response = new SysConfigContentResponse();
        SysConfig config = sysConfigService.findConfigContentByConfigName(configName);
        if (config == null) {
            response.setCode(SysConfigContentResponse.CODE_OK_NULL);
            response.setMessage(SysConfigContentResponse.MESSAGE_OK_NULL);
            return response;
        }
        response.setCode(SysConfigContentResponse.CODE_OK);
        response.setMessage(SysConfigContentResponse.MESSAGE_OK);
        response.setConfigContent(config.getConfigContent());
        return response;
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/goAddPage", method = RequestMethod.GET)
    public String goAddPage() {
        logger.debug("跳转到增加规则页面");
        return "sysconfig/sysconfig_add";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(SysConfig sysConfig, Pager pager, Model model) {
        SysConfig sysconfig = baseService.getSysConfig(sysConfig.getConfigId());
        if (sysconfig != null) {
            model.addAttribute("sysconfigDto", sysconfig);
        }
        return "sysconfig/sysconfig_edit";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSCONFIG_R)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(SysConfig sysConfig, Pager pager, Model model) {
        try {
            baseService.updateSysConfig(sysConfig);
        } catch (Exception e) {
            logger.error("规则增加更新", e);
            model.addAttribute("errormsg", "规则增加更新：" + e.getMessage());
        }
        return querySysconfig(null, null, model);
    }

    @Authorization(Constants.DMS_WEB_TOOL_DAFU_R)
    @RequestMapping(value = "/dafu/index", method = RequestMethod.GET)
    public String dafuWhiteList() {
        return "sysconfig/dafuWhiteList";
    }

    @Authorization(Constants.DMS_WEB_TOOL_DAFU_R)
    @RequestMapping(value = "/dafu/whiteList", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse updateDafuWhiteList(String type, String content) {
        Integer listType = Integer.valueOf(type.trim());
        String listContent = content.trim();
        Pattern pattern = Pattern.compile("([0-9][,]?)+");
        if (!pattern.matcher(listContent).matches()) {
            return new JdResponse(300, "黑白名单内容不符合要求");
        }

        try {
            List<SysConfig> configs = sysConfigDao.getListByConName("auto.sorting.whitepaper.910");
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigId(configs.get(0).getConfigId());
            switch (listType) {
                case 0: //黑名单
                {
                    sysConfig.setConfigContent(getBlackList(configs.get(0).getConfigContent(),listContent));
                    break;
                }
                case 1: //白名单
                {
                    sysConfig.setConfigContent(getWhiteList(configs.get(0).getConfigContent(),listContent));
                    break;
                }
            }
            sysConfigDao.update(SysConfigDao.namespace, sysConfig);
        } catch (Exception e) {
            logger.error(e);
            return new JdResponse(500, "系统异常，请稍后重试");
        }

        return new JdResponse(200, "配置黑白名单成功");
    }

    private String getBlackList(String orgiContent, String toAddContent) {
        Set<String> orgiContentSet = new HashSet<String>();
        Collections.addAll(orgiContentSet, orgiContent.split(","));

        Set<String> toAddContentSet = new HashSet<String>();
        Collections.addAll(toAddContentSet, toAddContent.split(","));

        orgiContentSet.removeAll(toAddContentSet);

        return StringHelper.join(removeEmptyItem(orgiContentSet) , ",");
    }

    private String getWhiteList(String orgiContent, String toAddContent) {
        String[] orgiContentList = orgiContent.split(",");
        String[] toAddContentList = toAddContent.split(",");
        Set<String> newContentSet = new HashSet<String>();
        Collections.addAll(newContentSet, orgiContentList);
        Collections.addAll(newContentSet, toAddContentList);

        return StringHelper.join(removeEmptyItem(newContentSet) , ",");
    }

    private Set<String> removeEmptyItem(Set<String> stringSet) {
        for(Iterator<String> it = stringSet.iterator(); it.hasNext();) {
            String siteCode = it.next();
            if (StringHelper.isEmpty(siteCode)) {
                it.remove();
                stringSet.remove(siteCode);
            }
        }
        return stringSet;
    }

    public static void main(String[] args) {
        String orgiContent = "677,24,1410,316,3417,5212,4386, 4520,5071,5920";
        String blackContent = "";
        String whiteContent = "";
        SysconfigController manager = new SysconfigController();
        Set<String> newContentSet = new HashSet<String>();
        Collections.addAll(newContentSet, orgiContent.split(","));
        System.out.println("before distinct " + newContentSet.size());
        String result = manager.getWhiteList(manager.getBlackList(orgiContent,blackContent),whiteContent);
        System.out.println(orgiContent.split(",").length + "   VS   " + result.split(",").length);
        System.out.println(result);
    }
}
