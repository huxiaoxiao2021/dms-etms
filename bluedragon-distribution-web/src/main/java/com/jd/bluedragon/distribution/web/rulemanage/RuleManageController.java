package com.jd.bluedragon.distribution.web.rulemanage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/ruleManage")
public class RuleManageController {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private RuleService ruleService;

	/**
	 * 
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage() {
		logger.debug("跳转到查询规则列表页面");
		return "ruleManage/rule_list";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Rule rule, Pager pager, Model model){
		try{
			logger.info("编辑规则-验证主键");
			/**验证主键*/
			checkId(rule);
			logger.info("编辑规则-准备调用服务进行查询 id = " + rule.getRuleId());
			Rule result = ruleService.queryById(rule.getRuleId());
			logger.info("查询规则数据");
			model.addAttribute("rulemanagequeryDto",result);
		}catch (Exception e) {
			logger.error("查询规则数据失败",e);
			model.addAttribute("errormsg","规则删除失败：" + e.getMessage());
		}
		return "ruleManage/rule_edit";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Rule rule,Pager pager, Model model){
		try{
			logger.info("更新规则-验证必输项");
			/**验证必输项*/
			checkRule(rule);
			logger.info("更新规则-验证主键");
			/**验证主键*/
			checkId(rule);
			logger.info("更新规则-准备调用服务进行更新 id = " + rule.getRuleId());
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			rule.setUpdateUser(erpUser == null ? StringUtils.EMPTY : erpUser.getUserName());
			ruleService.update(rule);
			logger.info("更新规则成功");
			model.addAttribute("successmsg","规则更新成功，请重新查询");
			return "ruleManage/rule_list";
		}catch (Exception e) {
			logger.error("查询规则数据失败",e);
			model.addAttribute("errormsg","规则删除失败：" + e.getMessage());
			model.addAttribute("rulemanagequeryDto",rule);
			return "ruleManage/rule_list";
		}		
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String del(Rule rule,Pager pager, Model model){
		try{
			logger.info("删除规则-验证主键");
			/**验证主键*/
			checkId(rule);
			logger.info("删除规则-准备调用服务进行删除 id = " + rule.getRuleId());
			ruleService.del(rule.getRuleId());
			logger.info("规则删除成功");
			model.addAttribute("successmsg","规则删除成功，请重新查询");
		}catch (Exception e) {
			logger.error("规则删除失败",e);
			model.addAttribute("errormsg","规则删除失败：" + e.getMessage());
		}
		return "ruleManage/rule_list";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Rule rule,Pager pager, Model model){
		logger.debug("按条件查询规则页面");
	    Map<String, Object> paramMap = makeObject2Map(rule);

	    // 设置分页对象
	    if (pager == null) {
	      pager = new Pager(Pager.DEFAULT_PAGE_NO);
	    } else {
	      pager = new Pager(pager.getPageNo(), pager.getPageSize());
	    }
	    paramMap.putAll(makeObject2Map(pager));

	    List<Rule> ruleal = null;
		try {
			// 获取总数量
			int totalsize = ruleService.queryAllSize(paramMap);
			pager.setTotalSize(totalsize);
			logger.info("查询符合条件的规则数量：" + totalsize);
			ruleal = ruleService.select(paramMap);
		} catch (Exception e) {
			logger.error("根据条件查询规则数据异常：", e);
		}
		model.addAttribute("ruleal",ruleal);
		model.addAttribute("rulemanagequeryDto",rule);
		model.addAttribute("pager", pager);
		model.addAttribute("ts", System.currentTimeMillis());
		
		return "ruleManage/rule_list";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(Rule rule,Pager pager, Model model) {
		try{
			logger.info("增加规则-验证必输项");
			checkRule(rule);
			logger.info("增加规则-准备调用服务进行增加");
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userName = erpUser == null ? StringUtils.EMPTY : erpUser.getUserName();
			rule.setCreateUser(userName);
			rule.setUpdateUser(userName);
			ruleService.add(rule);
			logger.info("规则增加成功");
			model.addAttribute("successmsg","规则增加成功，请重新查询");
		}catch (Exception e) {
			logger.error("规则增加失败",e);
			model.addAttribute("errormsg","规则增加失败：" + e.getMessage());
		}
		return "ruleManage/rule_list";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/goAddPage", method = RequestMethod.GET)
	public String goAddPage() {
		logger.debug("跳转到增加规则页面");
		return "ruleManage/rule_add";
	}
	
	private void checkId(Rule rule)throws Exception{
		if(rule.getRuleId()==0){
			throw new Exception("规则主键不能为空");
		}
	}
	
	private void checkRule(Rule rule) throws Exception{
		String msg = "";
		if(rule.getSiteCode()==null){
			msg += "站点编码不能为空，";
		}
		if(rule.getInOut() == null||rule.getInOut().equals("")){
			msg += "包含关系不能为空，";
		}
		if(rule.getType() == null){
			msg += "规则类型不能为空，";
		}
		if(rule.getContent() == null||rule.getContent().equals("")){
			msg += "规则内容不能为空，";
		}
		
		if(msg.length()>0){
			throw new Exception(msg.substring(0, msg.length()-1));
		}
	}
	
	/**
	 * 将对象转换为Map
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> makeObject2Map(Object obj) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (obj == null) {
			logger.debug("转换对象为空");
			return paramMap;
		}
		try {
			Class objClass = obj.getClass();
			Method[] methods = objClass.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					Object targetValueObj = method.invoke(obj);
					if (targetValueObj != null) {
						if (targetValueObj instanceof Integer) {
							if ((Integer) targetValueObj < 0) {
								logger.debug("方法名：" + methodName + " 的值小于0");
								continue;
							}
						} else if (targetValueObj instanceof String &&
								"".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
								logger.debug("方法名：" + methodName + " 的值为空");
								continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			logger.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}
}
