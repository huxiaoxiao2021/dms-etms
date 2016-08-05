package com.jd.bluedragon.distribution.web.operatelog;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.uim.annotation.Authorization;

@Controller
@RequestMapping("/operateLog")
public class OperateLogController {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private OperationLogService operationLosService;

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG0")
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
		return "operateLog/operatelog";
	}

	@Authorization("DMS-WEB-QUERY-OPERATE-LOG1")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
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

		logger.info("查询符合条件的规则数量：" + totalsize);

		model.addAttribute("operatelogs", operationLosService.queryByParams(params));
		model.addAttribute("operationLogqueryDto", operationLog);
		model.addAttribute("pager", pager);

		return "operateLog/operatelog";
	}

	@RequestMapping(value = "/goListPage1", method = RequestMethod.GET)
	public String goListpage1(Model model) {
		return "operateLog/operatelog1";
	}

	@RequestMapping(value = "/list1", method = RequestMethod.GET)
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

			logger.info("查询符合条件的规则数量：" + totalsize);

			model.addAttribute("operatelogs", operationLosService.queryByParams(params));
			model.addAttribute("operationLogqueryDto", operationLog);
			model.addAttribute("pager", pager);
		} catch (Exception e) {
			logger.error("日志查询异常-读库",e);
		}

		return "operateLog/operatelog1";
	}
	
	@RequestMapping(value = "/goListPage2", method = RequestMethod.GET)
	public String goListpage2(Model model) {
		return "operateLog/operatelog2";
	}
	
	@RequestMapping(value = "/list2", method = RequestMethod.GET)
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
		} catch (Exception e) {
			logger.error("日志查询异常2-读库",e);
		}

		return "operateLog/operatelog2";
	}
}
