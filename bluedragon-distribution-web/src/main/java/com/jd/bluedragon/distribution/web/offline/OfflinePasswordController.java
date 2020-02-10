package com.jd.bluedragon.distribution.web.offline;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.RandomUtils;
import com.jd.bluedragon.utils.SendMailUtil;
import com.jd.uim.annotation.Authorization;

@Controller
@RequestMapping("/offlinePwd")
public class OfflinePasswordController {
	@Autowired
	private BaseService baseService;
	@Autowired
	private SysConfigService sysConfigService;
	@RequestMapping(value = "/list")
	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	public String list( Model model){
		SysConfig sysConfig= new SysConfig();
		sysConfig.setConfigType(-100);
		List<SysConfig> dataList =this.sysConfigService.getList(sysConfig);
		model.addAttribute("dataList", dataList);
		SysConfig querySysConfig = new SysConfig();
		querySysConfig.setConfigName("offline.password");
		List<SysConfig> passwordList =this.sysConfigService.getList(querySysConfig);
		if(passwordList!=null&&passwordList.size()>0){
			model.addAttribute("offlinePassword", passwordList.get(0).getConfigContent());
		}
		
		querySysConfig.setConfigName("special.password");
		List<SysConfig> specialList =this.sysConfigService.getList(querySysConfig);
		
		if(specialList!=null&&specialList.size()>0){
			model.addAttribute("specialPassword", specialList.get(0).getConfigContent());
		}
		return "offlinePwd/list";
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/addMail")
	public String addMail(){
		
		return "offlinePwd/addMail";
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/save")
	public String save(SysConfig sysConfig,Model model){
		sysConfig.setConfigType(-100);
		if(sysConfig.getConfigId()==null){
			baseService.insertSysConfig(sysConfig);
		}else{
			baseService.updateSysConfig(sysConfig);
		}
		return list(model);
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/deleteMail")
	public String deleteMail(Long id,Model model){
		this.sysConfigService.del(id);
		return list(model);
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/updateMail")
	public String updateMail(Model model,Long id){
		SysConfig sysConfig =this.baseService.getSysConfig(id);
		model.addAttribute("sysConfig", sysConfig);
		return "offlinePwd/addMail";
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/sendOfflinePwdMail")
	@ResponseBody
	public String sendOfflinePwdMail(Integer type){
		SysConfig sysConfig= new SysConfig();
		sysConfig.setConfigType(-100);
		List<SysConfig> dataList =this.sysConfigService.getList(sysConfig);
		List<String> address = new ArrayList<String>();
		for(SysConfig o:dataList){
			address.add(o.getConfigName());
		}
		SysConfig querySysConfig = new SysConfig();
		StringBuilder content=new StringBuilder();
		String subject= null;
		if(type.equals(1)){
			subject="离线分拣密码变更";
			querySysConfig.setConfigName("offline.password");
			List<SysConfig> passwordList =this.sysConfigService.getList(querySysConfig);
			if(passwordList!=null&&passwordList.size()>0){
				content.append("离线密码变更为：").append(passwordList.get(0).getConfigContent()).append("。");
			}
			
		}else if(type.equals(2)){
			subject="一单多件不齐发货密码变更";
			querySysConfig.setConfigName("special.password");
			List<SysConfig> passwordList =this.sysConfigService.getList(querySysConfig);
			if(passwordList!=null&&passwordList.size()>0){
				content.append("一单多件不齐发货密码变更为：").append(passwordList.get(0).getConfigContent()).append("。");
			}
		}
		SendMailUtil.sendSimpleEmail(subject, content.toString(), address);
		return null;
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/sendDeliverPwdMail")
	public String sendDeliverPwdMail(){
		return null;
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@RequestMapping(value = "/generatePassword")
	public String generatePassword(Integer type,Model model){
		SysConfig querySysConfig = new SysConfig();
		SysConfig sysConfig= new SysConfig();
		if (type.equals(1)) {//离线密码
			querySysConfig.setConfigName("offline.password");
        }
		else {
            querySysConfig.setConfigName("special.password");
        }
		List<SysConfig> passwordList = this.sysConfigService.getList(querySysConfig);
		if(passwordList!=null&&passwordList.size()>0){
			sysConfig=passwordList.get(0);
			if (type.equals(1)) {
			    sysConfig.setConfigContent(RandomUtils.generateRandomNumByCurDate());
            }
			else {
                sysConfig.setConfigContent(RandomUtils.generateString(6));
            }
		}
		this.baseService.updateSysConfig(sysConfig);
		return list(model);
	}

	@Authorization(Constants.DMS_WEB_OFFLINE_MANAGER_R)
	@ResponseBody
	@RequestMapping(value = "/checkMail")
	public int checkMail(String email,Long configId){
		SysConfig sysConfig= new SysConfig();
		sysConfig.setConfigType(-100);
		sysConfig.setConfigName(email);
		List<SysConfig> dataList =this.sysConfigService.getList(sysConfig);
		if(configId==null){
		if(dataList!=null&&dataList.size()>0){
			return 1;
		}else{
			return 0;
		}
		}else{
			SysConfig o =	dataList.get(0);
			if(o.getConfigId().equals(configId)){
				return 0;
			}else {
				return 1;
			}
		}
		
	}
}
