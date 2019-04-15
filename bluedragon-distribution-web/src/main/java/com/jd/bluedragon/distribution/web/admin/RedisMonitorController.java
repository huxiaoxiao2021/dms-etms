package com.jd.bluedragon.distribution.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.admin.service.RedisMonitorService;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Controller
@RequestMapping("/admin/redis-monitor")
public class RedisMonitorController {

	private static final Logger logger = Logger.getLogger(RedisMonitorController.class);

	@Autowired
	private RedisMonitorService redisMonitorService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private RedisManager redisManager;

	@Authorization(Constants.DMS_WEB_DEVELOP_REDIS_R)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(Model model) {
		try {
			ErpUser erpUser = ErpUserClient.getCurrUser();
			model.addAttribute("erpUser", erpUser);
		} catch (Exception e) {
			logger.error("index error!", e);
		}
		return "admin/redis-monitor/redis-monitor-index";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_REDIS_R)
    @RequestMapping(value = "/queryValueByKey", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<List<Map<String, Object>>> queryValueByKey(@RequestParam(value = "key", required = false) String key) {

		CommonDto<List<Map<String, Object>>> cdto = new CommonDto<List<Map<String, Object>>>();
		try {
			logger.debug("doQueryRedisQueueCount begin...");

			if (key == null || key.length() < 1) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[key]不能为空！");
				return cdto;
			}

			String value = redisMonitorService.getValueByKey(key);

			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (value == null || value.length() < 1) {
				map.put("length", "查询结果为空！");
			} else {
				map.put("length", value.length());
			}
			map.put("key", key);
			list.add(map);
			cdto.setCode(CommonDto.CODE_NORMAL);
			cdto.setData(list);
		} catch (Exception e) {
			logger.error("doQueryValueByKey-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_REDIS_R)
	@RequestMapping(value = "/deleteByKey", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<String> deleteByKey(@RequestParam(value = "key", required = false) String key) {

		CommonDto<String> cdto = new CommonDto<String>();
		try {
			logger.debug("doQueryRedisQueueCount begin...");
			if (key == null || key.length() < 1) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[key]不能为空！");
				return cdto;
			}
			redisMonitorService.deleteByKey(key);
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("deleteByKey-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

	/**
	 * 添加数据,供测试用
	 * 
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_DEVELOP_REDIS_R)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<List<Map<String, Object>>> add() {
		CommonDto<List<Map<String, Object>>> cdto = new CommonDto<List<Map<String, Object>>>();
		try {
			BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(1);
			String value = JsonHelper.toJson(dto);
			redisManager.setex("basicMajorWSProxy.getBaseSiteBySiteId@1", 1000000, value);
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("doQueryValueByKey-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

}
