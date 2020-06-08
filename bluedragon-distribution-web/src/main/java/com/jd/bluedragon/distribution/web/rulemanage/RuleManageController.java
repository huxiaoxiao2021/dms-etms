package com.jd.bluedragon.distribution.web.rulemanage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.domain.RuleCondition;
import com.jd.bluedragon.distribution.rule.domain.RuleEnum;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.NullObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/ruleManage")
public class RuleManageController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RuleService ruleService;

	/**
	 * 
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage() {
		log.debug("跳转到查询规则列表页面");
		return "ruleManage/rule_list";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Rule rule, Pager pager, Model model){
		try{
			log.info("编辑规则-验证主键");
			/**验证主键*/
			checkId(rule);
			log.info("编辑规则-准备调用服务进行查询 id = {}", rule.getRuleId());
			Rule result = ruleService.queryById(rule.getRuleId());
			log.info("查询规则数据");
			model.addAttribute("rulemanagequeryDto",result);
		}catch (Exception e) {
			log.error("查询规则数据失败",e);
			model.addAttribute("errormsg","规则删除失败：" + e.getMessage());
		}
		return "ruleManage/rule_edit";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
	public InvokeResult<String> update(@RequestBody Rule rule){
		try{
			log.info("更新规则-验证必输项");
			/**验证必输项*/
			checkRule(rule);
			log.info("更新规则-验证主键");
			/**验证主键*/
			checkId(rule);
			log.info("更新规则-准备调用服务进行更新 id = {}", rule.getRuleId());
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			rule.setUpdateUser(erpUser == null ? StringUtils.EMPTY : erpUser.getUserName());
			ruleService.update(rule);
			log.info("更新规则成功");
			return new InvokeResult<>();
		}catch (Exception e) {
			log.error("更新规则数据失败",e);
			InvokeResult result = new InvokeResult();
			result.error("规则更新失败");
			return result;
		}		
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/del", method = RequestMethod.GET)
    @ResponseBody
	public InvokeResult del(Rule rule){
		try{
			log.info("删除规则-验证主键");
			/**验证主键*/
			checkId(rule);
			log.info("删除规则-准备调用服务进行删除 id = {}", rule.getRuleId());
			ruleService.del(rule.getRuleId());
			log.info("规则删除成功");
		}catch (Exception e) {
			log.error("规则删除失败:" + rule.getRuleId(),e);
			InvokeResult result = new InvokeResult();
			result.error("规则删除失败");
		}
		return new InvokeResult();
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public PagerResult<Rule> list(RuleCondition rule){
		log.debug("按条件查询规则页面");
		PagerResult<Rule> result = new PagerResult<>();

	    Map<String, Object> paramMap = makeObject2Map(rule);


	    List<Rule> ruleal = null;
		try {
			// 获取总数量
			int totalsize = ruleService.queryAllSize(paramMap);
			result.setTotal(totalsize);
			log.info("查询符合条件的规则数量：{}", totalsize);
			ruleal = ruleService.select(paramMap);
			result.setRows(ruleal);
		} catch (Exception e) {
			log.error("根据条件查询规则数据异常：", e);
		}

		return result;
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
	public InvokeResult add(@RequestBody Rule rule) {
		try{
			log.info("增加规则-验证必输项");
			checkRule(rule);
			log.info("增加规则-准备调用服务进行增加");
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userName = erpUser == null ? StringUtils.EMPTY : erpUser.getUserName();
			rule.setCreateUser(userName);
			rule.setUpdateUser(userName);
			ruleService.add(rule);
			log.info("规则增加成功");
			return new InvokeResult();
		}catch (Exception e) {
			log.error("规则增加失败",e);
			InvokeResult result = new InvokeResult();
			result.error("增加分拣规则成功");
			return result;
		}
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/goAddPage", method = RequestMethod.GET)
	public String goAddPage() {
		log.debug("跳转到增加规则页面");
		return "ruleManage/rule_add";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/getRuleTypeList", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> getRuleTypeList() {
		return RuleEnum.getRulesMaps();
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_RULE_CONFIG_R)
	@RequestMapping(value = "/getRuleTypeByTypeCode", method = RequestMethod.GET)
	@ResponseBody
	public Object getRuleTypeByTypeCode(@QueryParam("type")Integer typeCode) {
		for (Map<String ,Object> ruleType : RuleEnum.getRulesMaps()) {
			if (ruleType.get("ruleType").equals(typeCode)) {
				return ruleType;
			}
		}
		return new Object();
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
			log.debug("转换对象为空");
			return paramMap;
		}
		try {
			Class objClass = obj.getClass();
			Method[] methods = objClass.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					Object targetValueObj = method.invoke(obj);
					if (targetValueObj != null) {
						if (targetValueObj instanceof Integer) {
							if ((Integer) targetValueObj < 0) {
								log.debug("方法名：{} 的值小于0",methodName);
								continue;
							}
						} else if (targetValueObj instanceof String &&
								"".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
								log.debug("方法名：{} 的值为空", methodName);
								continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			log.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}
}
