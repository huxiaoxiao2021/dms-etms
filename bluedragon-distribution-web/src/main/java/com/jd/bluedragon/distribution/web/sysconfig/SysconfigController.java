package com.jd.bluedragon.distribution.web.sysconfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

	static {
		modifyUsers = Arrays.asList(PropertiesHelper.newInstance()
		        .getValue(SysconfigController.MODIFY_USERS).split(Constants.SEPARATOR_COMMA));
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String querySysconfig(SysConfig sysConfig, Pager<SysConfig> pager, Model model){
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

			String configName = sysConfig!=null&&sysConfig.getConfigName()!=null?sysConfig.getConfigName()+"%":"%";
			
			// 获取总数量
			int totalsize = baseService.totalSysconfigSizeByParams(configName);
			pager.setTotalSize(totalsize);
			
			params.put("configName", configName);
			params.putAll(ObjectMapHelper.makeObject2Map(pager));
			
			
			List<SysConfig> sysconfigal = baseService.queryConfigByKey(params);
			model.addAttribute("sysconfigal",sysconfigal);
			model.addAttribute("sysConfigDto",sysConfig);
			model.addAttribute("pager", pager);
		} catch (Exception e) {
			logger.error("基础设置查询异常：", e);
			model.addAttribute("errormsg","基础设置查询异常：" + e.getMessage());
		}
		return "sysconfig/sysconfig_list";
	}
	@RequestMapping(value = "/getSwitchList",method = RequestMethod.GET)
	public String getSwitchList(Model model){
		List<SysConfig> dataList = this.sysConfigService.getSwitchList();
		for(SysConfig sysConfig:dataList){
			String redisContent = redisManager.hget(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName());
			sysConfig.setRedisContent(redisContent);
		}
		model.addAttribute("dataList", dataList);
		return "sysconfig/switchList";
		
	}
	@RequestMapping(value = "/syncRedis",method = RequestMethod.GET)
	public String syncRedis(Model model,Long id){
		SysConfig sysConfig = this.baseService.getSysConfig(id);
		redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
		return getSwitchList(model);
	}
	
	@RequestMapping(value = "/open",method = RequestMethod.GET)
	public String open(Model model,Long id){
		SysConfig sysConfig = new SysConfig();
		sysConfig.setConfigId(id);
		sysConfig.setConfigContent("1");
		this.baseService.updateSysConfig(sysConfig);
	    sysConfig = this.baseService.getSysConfig(id);
	    redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
		return getSwitchList(model);
		 
		
	}
	
	@RequestMapping(value = "/close",method = RequestMethod.GET)
	public String close(Model model,Long id){
		SysConfig sysConfig = new SysConfig();
		sysConfig.setConfigId(id);
		sysConfig.setConfigContent("0");
		this.baseService.updateSysConfig(sysConfig);
	    sysConfig = this.baseService.getSysConfig(id);
	    redisManager.hset(DefaultDataSubscriber.CONFIGURATION_REDIS_QUEUE_NAME, sysConfig.getConfigName(), sysConfig.getConfigContent());
		return getSwitchList(model);
		 
		
	}
	
	@RequestMapping(value = "/toAdd")
	public String toAdd(){
		return "sysconfig/add";
	}
	@RequestMapping(value = "/addSwitch")
	public String addSwitch(SysConfig sysConfig, Model model){
		baseService.insertSysConfig(sysConfig);
		return getSwitchList(model);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(SysConfig sysConfig,Pager pager, Model model) {
		try{
			baseService.insertSysConfig(sysConfig);
		}catch (Exception e) {
			logger.error("规则增加失败",e);
			model.addAttribute("errormsg","规则增加失败：" + e.getMessage());
		}
		return querySysconfig(null,null,model);
	}


	@RequestMapping(value = "/goAddPage", method = RequestMethod.GET)
	public String goAddPage() {
		logger.debug("跳转到增加规则页面");
		return "sysconfig/sysconfig_add";
	}


	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(SysConfig sysConfig,Pager pager, Model model){
		SysConfig sysconfig = baseService.getSysConfig(sysConfig.getConfigId());
		if(sysconfig!=null){
			model.addAttribute("sysconfigDto",sysconfig);
		}
		return "sysconfig/sysconfig_edit";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(SysConfig sysConfig,Pager pager, Model model){
		try{
			baseService.updateSysConfig(sysConfig);
		}catch (Exception e) {
			logger.error("规则增加更新",e);
			model.addAttribute("errormsg","规则增加更新：" + e.getMessage());
		}
		return querySysconfig(null,null,model);
	}
}
