package com.jd.bluedragon.distribution.web.operatelog;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/operateLog")
public class OperateLogController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OperationLogService operationLosService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG0")
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
        model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		return "operateLog/operatelog";
	}

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG1")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.OperateLogController.queryOperateLog", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String queryOperateLog(OperationLog operationLog, Pager<OperationLog> pager, Model model) {
		Map<String, Object> params = ObjectMapHelper.makeObject2Map(operationLog);

		if (operationLog.getWaybillCode() == null && operationLog.getPickupCode() == null
				&& operationLog.getPackageCode() == null && operationLog.getBoxCode() == null) {
			return "operateLog/operatelog";
		}
		
		if (operationLog.getWaybillCode().equals("") && operationLog.getPickupCode().equals("")
				&& operationLog.getPackageCode().equals("") && operationLog.getBoxCode().equals("")) {
			return "operateLog/operatelog";
		}
		
		// 设置分页对象
		if (pager == null) {
			pager = new Pager<OperationLog>(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager<OperationLog>(pager.getPageNo(), pager.getPageSize());
		}

		params.putAll(ObjectMapHelper.makeObject2Map(pager));

		// 获取总数量
		int totalsize = operationLosService.totalSizeByParams(params);
		pager.setTotalSize(totalsize);

		log.info("查询符合条件的规则数量：{}", totalsize);

		model.addAttribute("operatelogs", operationLosService.queryByParams(params));
		model.addAttribute("operationLogqueryDto", operationLog);
		model.addAttribute("pager", pager);
        model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		return "operateLog/operatelog";
	}

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG1")
	@RequestMapping(value = "/goListPage1", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.OperateLogController.goListPage1", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String goListpage1(Model model) {
		return "operateLog/operatelog1";
	}

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG1")
	@RequestMapping(value = "/list1", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.OperateLogController.queryOperateLog1", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String queryOperateLog1(OperationLog operationLog, Pager<OperationLog> pager, Model model) {
		try {
			Map<String, Object> params = ObjectMapHelper.makeObject2Map(operationLog);

			if (operationLog.getWaybillCode() == null && operationLog.getPickupCode() == null
					&& operationLog.getPackageCode() == null && operationLog.getBoxCode() == null) {
				return "operateLog/operatelog1";
			}
			
			if (operationLog.getWaybillCode().equals("") && operationLog.getPickupCode().equals("")
					&& operationLog.getPackageCode().equals("") && operationLog.getBoxCode().equals("")) {
				return "operateLog/operatelog1";
			}

			// 设置分页对象
			if (pager == null) {
				pager = new Pager<OperationLog>(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager<OperationLog>(pager.getPageNo(), pager.getPageSize());
			}

			params.putAll(ObjectMapHelper.makeObject2Map(pager));

			// 获取总数量
			int totalsize = operationLosService.totalSizeByParams(params);
			pager.setTotalSize(totalsize);

			log.info("查询符合条件的规则数量：{}", totalsize);

			model.addAttribute("operatelogs", operationLosService.queryByParams(params));
			model.addAttribute("operationLogqueryDto", operationLog);
			model.addAttribute("pager", pager);
            model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		} catch (Exception e) {
			log.error("日志查询异常-读库",e);
		}

		return "operateLog/operatelog1";
	}

	@Authorization(Constants.DMS_WEB_SORTING_OPERATELOG_R)
	@RequestMapping(value = "/goListPage2", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.OperateLogController.goListPage2", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String goListpage2(Model model) {

		ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
		BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
		businessLogProfiler.setSourceSys(1);
		businessLogProfiler.setBizType(Constants.BIZTYPE_URL_CLICK);
		businessLogProfiler.setOperateType(Constants.OPERATELOG_CLICK);
		JSONObject request=new JSONObject();
		request.put("operatorName",erpUser.getUserName());
		request.put("operatorCode",erpUser.getUserCode());
		businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));
		BusinessLogWriter.writeLog(businessLogProfiler);
        model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		return "operateLog/operatelog2";
	}

    @Authorization(Constants.DMS_WEB_SORTING_OPERATELOG_R)
	@RequestMapping(value = "/list2", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.OperateLogController.list2", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String queryOperateLog2(OperationLog operationLog, Pager<OperationLog> pager, Model model) {
		try {

			if (operationLog.getWaybillCode() == null && operationLog.getPickupCode() == null
					&& operationLog.getPackageCode() == null && operationLog.getBoxCode() == null) {
				return "operateLog/operatelog2";
			}
			
			if (operationLog.getWaybillCode().equals("") && operationLog.getPickupCode().equals("")
					&& operationLog.getPackageCode().equals("") && operationLog.getBoxCode().equals("")) {
				return "operateLog/operatelog2";
			}
			String code = operationLog.getWaybillCode();
			String type = "waybill";
			
			if(operationLog.getPickupCode()!= null && !operationLog.getPickupCode().equals("")){
				code = operationLog.getPickupCode();
				type = "pick";
			}
			
			if(operationLog.getPackageCode()!= null && !operationLog.getPackageCode().equals("")){
				code = operationLog.getPackageCode();
				type = "package";
			}
			if(operationLog.getBoxCode()!= null && !operationLog.getBoxCode().equals("")){
				code = operationLog.getBoxCode();
				type = "box";
			}
			
			// 设置分页对象
			if (pager == null) {
				pager = new Pager<OperationLog>(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager<OperationLog>(pager.getPageNo(), pager.getPageSize());
			}
			// 获取总数量
			pager.setTotalSize(operationLosService.totalSize(code ,type));
			model.addAttribute("operatelogs", operationLosService.queryByCassandra(code ,type,pager));
			model.addAttribute("operationLogqueryDto", operationLog);
			model.addAttribute("pager", pager);
            model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		} catch (Exception e) {
			log.error("日志查询异常2-读库",e);
		}

		return "operateLog/operatelog2";
	}
}
