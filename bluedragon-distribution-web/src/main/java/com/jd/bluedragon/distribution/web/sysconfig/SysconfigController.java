package com.jd.bluedragon.distribution.web.sysconfig;

import java.util.*;
import java.util.regex.Pattern;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = "/syncRedis", method = RequestMethod.GET)
    public String syncRedis(Model model, Long id) {
        SysConfig sysConfig = this.baseService.getSysConfig(id);
        redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
        return getSwitchList(model);
    }

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

    @RequestMapping(value = "/toAdd")
    public String toAdd() {
        return "sysconfig/add";
    }

    @RequestMapping(value = "/addSwitch")
    public String addSwitch(SysConfig sysConfig, Model model) {
        baseService.insertSysConfig(sysConfig);
        return getSwitchList(model);
    }

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


    @RequestMapping(value = "/goAddPage", method = RequestMethod.GET)
    public String goAddPage() {
        logger.debug("跳转到增加规则页面");
        return "sysconfig/sysconfig_add";
    }


    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(SysConfig sysConfig, Pager pager, Model model) {
        SysConfig sysconfig = baseService.getSysConfig(sysConfig.getConfigId());
        if (sysconfig != null) {
            model.addAttribute("sysconfigDto", sysconfig);
        }
        return "sysconfig/sysconfig_edit";
    }

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

    @RequestMapping(value = "/dafu/index", method = RequestMethod.GET)
    public String dafuWhiteList() {
        return "sysconfig/dafuWhiteList";
    }

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
        String orgiContent = "677,24,1410,316,3417,5212,4386,4520,5071,5920,873,1282,1335,1361,573,1341,5209,5074,5187,5205,5076,4837,4108,3696,5422,5970,5991,1170,672,1194,610,2704,2705,5206,5184,4494,5081,4443,5186,5713,4442,5073,5221,5140,5220,4441,4495,5082,5219,5432,3610,3611,3932,5961,1045,5992,5718,5717,5978,4444,5973,1213,829,38,5185,5204,5069,5711,3253,4984,4230,3954,3925,5988,5969,154,4165,5208,5687,2990,2655,2741,3205,3679,2382,5678,5670,4211,4260,3930,3923,5990,989,518,1163,3695,5710,1372,317,2588,1323,1325,1944,1463,5072,3966,4100,5959,4544,5778,3926,685,1150,42,575,36,1665,572,3486,3957,4983,3485,5182,5431,5816,5972,6055,1393,1382,623,1133,1381,1470,1324,1791,3477,3928,4944,5188,5207,5197,5770,4241,6077,1932,2740,4,1933,411,3090,3091,5203,5624,5643,21,517,3672,4163,3931,2706,3403,5191,5200,5201,5202,5211,5198,5424,5796,6017,1471,2587,2906,2954,3959,3034,136,3481,3483,5423,5948,5974,1154,996,529,1804,653,944,4204,3691,4543,3689,4243,4242,4244,5189,5199,5607,5430,5612,717,5960,6018,2556,2434,2576,2541,2540,1199,2035,1645,1063,2435,2997,3023,3288,3410,2387,3639,3441,3638,4171,1982,1649,3270,2468,54105,3640,1650,6084,122968,146905,157028,158130,152317,159137,159143,159145,157029,149628,157031,168400,168402,206834,208734,206840,208765,197684,221652,2158,2474,20,5030,4989,5084,139088,159153,1280,3962,242636,233451,242636,233451,242630,208750,251288,251291,252341,252332,252357,251242,251298,270760,271718,271744,271733,271749,270723,270728,270834,270815,270842,270785,270760,270752, 271744,273422,273409,257317,281159,284737,281135,281155,281141,284696,281253,284708,288406,281258,5553,251253,251261,3820,160714,160563,2223,700,22,295640,295659,295687,289221,294060,317977,153494,1358,3618,161,365810,341024,4757,572,1410,289210,5247,289163,289117,288571,282015,284727,289191,3471,284757,288565,288685,281176,288411,284795,288556,281205,284766,330821,425921,340967,341404,317977,341130,428152,389451,410422,439925,398075,398080,398084,610,398061,572,288558,395314,330821,341437,473818,474899,477755,479090,479121,483705,488591,485502,492820,492812,490729";
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
