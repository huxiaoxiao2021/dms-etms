package com.jd.bluedragon.distribution.web.systemlog;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.systemLog.service.SystemLogService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;

@Controller
@RequestMapping("/systemLog")
public class SystemLogController {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SystemLogService systemLogService;

	@Authorization(Constants.DMS_WEB_DEVELOP_SYSTEMLOG_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
		return "systemLog/systemLog";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSTEMLOG_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String queryOperateLog(SystemLog systemLog, Pager<SystemLog> pager, Model model) {
		try{
		Map<String, Object> params = ObjectMapHelper.makeObject2Map(systemLog);

		// 设置分页对象
		if (pager == null) {
			pager = new Pager<SystemLog>(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager<SystemLog>(pager.getPageNo(), pager.getPageSize());
		}

		params.putAll(ObjectMapHelper.makeObject2Map(pager));

		// 获取总数量
		int totalsize = systemLogService.totalSizeByParams(params);
		pager.setTotalSize(totalsize);

		logger.info("查询符合条件的规则数量：" + totalsize);
		
		
		List<SystemLog> logList = systemLogService.queryByParams(params);
		for(SystemLog log:logList){
			log.setContent(StringHelper.html(log.getContent()));
		}
		model.addAttribute("systemlogs", logList);
		model.addAttribute("systemLogQueryDto", systemLog);
		model.addAttribute("pager", pager);
		}catch(Exception e){
			logger.error("查询SystemLog出错", e);
		}

		return "systemLog/systemLog";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSTEMLOG_R)
	@RequestMapping(value = "/goListPage1", method = RequestMethod.GET)
	public String goListpage1(Model model) {
		return "systemLog/systemLog1";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SYSTEMLOG_R)
	@RequestMapping(value = "/list1", method = RequestMethod.GET)
	public String queryOperateLog1(SystemLog systemLog, Pager<SystemLog> pager, Model model) {
		try{
		// 设置分页对象
		if (pager == null) {
			pager = new Pager<SystemLog>(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager<SystemLog>(pager.getPageNo(), pager.getPageSize());
		}
		
		// 获取总数量
		int totalsize =systemLogService.totalSize(systemLog.getKeyword1());
		pager.setTotalSize(totalsize);

		logger.info("查询符合条件的规则数量：" + totalsize);
		
		List<SystemLog> logList = systemLogService.queryByCassandra(systemLog.getKeyword1() ,pager);
		for(SystemLog log:logList){
			log.setContent(StringHelper.html(log.getContent()));
		}
		model.addAttribute("systemlogs", logList);
		model.addAttribute("systemLogQueryDto", systemLog);
		model.addAttribute("pager", pager);
		}catch(Exception e){
			logger.error("查询SystemLog出错", e);
		}
		return "systemLog/systemLog1";
	}
}
