package com.jd.bluedragon.distribution.web.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.jd.bluedragon.core.redis.QueueKeyInfo;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.admin.service.SystemMonitorService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.etms.erp.service.dto.CommonDto;

/**
 * 系统监控
 *
 */
@Controller
@RequestMapping("/admin/system-monitor")
public class SysremMonitorController {
	
	private static final Logger logger = Logger.getLogger(SysremMonitorController.class);
	
	@Autowired
	private SystemMonitorService systemMonitorService;
	
	@Autowired
	private RedisManager redisManager;

	@Authorization(Constants.DMS_WEB_DEVELOP_TASK_R)
	@RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
		try {
			ErpUser erpUser = ErpUserClient.getCurrUser();
	    	model.addAttribute("erpUser", erpUser);
	    } catch (Exception e) {
			logger.error("index error!", e);
		}
        return "admin/system-monitor/system-monitor-index";
    }

    /**
     * 查询RedisQueue个数
     * @param model
     * @param qName
     * @param qSign
     * @param qSize
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_TASK_R)
    @RequestMapping(value = "/doQueryRedisQueueCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<List<Map<String,Object>>> doQueryRedisQueueCount(Model model, 
    		@RequestParam(value = "qName", required = false) String qName,
			@RequestParam(value = "qSign", required = false) String qSign,
			@RequestParam(value = "qSize", required = false) String qSize) {
    	
    	CommonDto<List<Map<String,Object>>> cdto = new CommonDto<List<Map<String,Object>>>();
    	try {
    		logger.debug("doQueryRedisQueueCount begin...");
    		if(qName == null || qName.length() < 1){
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列名[qName]不能为空！");
    			return cdto;
    		}
    		if(qSize != null && qSize.length() > 2){//限制qSize < 100
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列数[qSize]只能为小于100的数字！");
    			return cdto;
    		}
    		Map<String,Object> param = new HashMap<String,Object>();
    		param.put("qName", qName);
    		param.put("qSign", qSign);
    		param.put("qSize", qSize);
    		
    		Map<String,Long> map = systemMonitorService.queryRedisQueueCount(param);
    		if(map== null || map.size() < 1){
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("查询结果为空！");
    			return cdto;
    		}
    		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    		Set<String> keyset = map.keySet();
    		
    		//使变的有序FIXME:还可以从源头上进行改进
    		List<String> keyList = new ArrayList<String>();
    		keyList.addAll(keyset);
    		Collections.sort(keyList);
    		
    		for (String key : keyList) {
    			Map<String,Object> item = new HashMap<String,Object>();
    			item.put("queue", key);
    			item.put("count", map.get(key));
    			list.add(item);
			}
    		
        	cdto.setCode(CommonDto.CODE_NORMAL);
    		cdto.setData(list);
    	} catch (Exception e) {
			logger.error("doQueryRedisQueueCount-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
        return cdto;
    }

    /**
     * 查询RedisQueue明细
     * @param model
     * @param queueKey
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_TASK_R)
    @RequestMapping(value = "/doQueryRedisQueueDetail", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<List<String>> doQueryRedisQueueDetail(Model model,
    		@RequestParam(value = "queueKey", required = false) String queueKey,
			@RequestParam(value = "currentPage", required = false) String currentPage,
			@RequestParam(value = "pageSize", required = false) String pageSize) {
    	
    	CommonDto<List<String>> cdto = new CommonDto<List<String>>();
    	try {
    		logger.debug("doQueryRedisQueueDetail begin...");
    		if(queueKey == null || queueKey.length() < 1){
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列名[queueKey]不能为空！");
    			return cdto;
    		}
    		if(pageSize != null && pageSize.length() > 3){//限制pageSize < 1000
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列数[qSize]只能为小于100的数字！");
    			return cdto;
    		}
    		
    		int current_page = 1;
    		int page_size = 100;
    		
    		if(currentPage != null && currentPage.length() > 0){
    			current_page = Integer.parseInt(currentPage);
    		}
    		if(pageSize != null && pageSize.length() > 0){
    			page_size = Integer.parseInt(pageSize);
    		}
    		
    		List<String> list = systemMonitorService.queryRedisQueueDetail(queueKey, current_page, page_size);
        	cdto.setCode(CommonDto.CODE_NORMAL);
    		cdto.setData(list);
    	} catch (Exception e) {
			logger.error("doQueryRedisQueueCount-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
        return cdto;
    }


    /**
     * 将redis队列数据转移到数据库中
     * @param model
     * @param qName 队列类型
     * @param qSign ownSign
     * @param qSize 队列数量
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_TASK_R)
    @RequestMapping(value = "/doMove2DB", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<List<Map<String,Object>>> doMove2DB(Model model, 
    		@RequestParam(value = "qName", required = false) String qName,
			@RequestParam(value = "qSign", required = false) String qSign,
			@RequestParam(value = "qSize", required = false) String qSize) {
    	
    	CommonDto<List<Map<String,Object>>> cdto = new CommonDto<List<Map<String,Object>>>();
    	try {
    		logger.debug("doQueryRedisQueueCount begin...");
    		if(qName == null || qName.length() < 1){
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列名[qName]不能为空！");
    			return cdto;
    		}
    		if(qSize != null && qSize.length() > 2){//限制qSize < 100
    			cdto.setCode(CommonDto.CODE_WARN);
    			cdto.setMessage("参数队列数[qSize]只能为小于100的数字！");
    			return cdto;
    		}
    		QueueKeyInfo queueKeyInfo = new QueueKeyInfo();
    		queueKeyInfo.setTaskType(qName);
    		queueKeyInfo.setOwnSign(qSign);
    		
    		Long count = redisManager.moveTaskToDB(queueKeyInfo);
    		
    		cdto.setCode(CommonDto.CODE_NORMAL);
    		cdto.setMessage("将任务"+qName+"$"+qSign+"数据"+count+"条移动到DB!");
    	} catch (Exception e) {
			logger.error("doMoveRedisQueueDataToDB-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setMessage(e.getMessage());
		}
        return cdto;
    }
	public void setSystemMonitorService(SystemMonitorService systemMonitorService) {
		this.systemMonitorService = systemMonitorService;
	}
	
}
