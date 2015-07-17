package com.jd.bluedragon.distribution.web.popAbnormal;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDTO;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.WorkBookObject;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-27 下午05:30:06
 * 
 *             POP差异订单处理控制层
 */
@Controller
@RequestMapping("/popAbnormal")
public class PopAbnormalController {
	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PopAbnormalService popAbnormalService;

	/**
	 * 跳转到查询POP差异列表页面
	 * 
	 * @return
	 */
//	@HrmPrivilege("TMS-REPORT-POPCY")
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage() {
		logger.info("跳转到查询POP差异列表页面");
		return "popAbnormal/pop_abnormal_list";
	}

	/**
	 * 按条件查询POP差异订单数据集合
	 * 
	 * @param paramMap
	 * @param model
	 * @return
	 */
//	@HrmPrivilege("TMS-REPORT-POPCY")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(PopAbnormalDTO popAbnormalDTO, Pager pager, Model model) {
		logger.info("按条件查询POP差异订单数据集合");
		Map<String, Object> paramMap = ObjectMapHelper.makeObject2Map(popAbnormalDTO);

		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

		// 设置分页对象
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager(pager.getPageNo(), pager.getPageSize());
		}
		paramMap.putAll(ObjectMapHelper.makeObject2Map(pager));

		List<PopAbnormal> popAbnormals = null;
		
		// 获取用户分拣中心相关信息
		Map<String, Object> dmsUserMap = getUserMap(erpUser.getUserId());
		if (!dmsUserMap.isEmpty()) {
			// 设置用户分拣中心信息
			paramMap.putAll(dmsUserMap);
			try {
				// 获取总数量
				int totalSize = popAbnormalService.findTotalCount(paramMap);
				logger.info("按条件查询POP差异订单数据集合 --> 获取总数量为：" + totalSize);
				if (totalSize > 0) {
					pager.setTotalSize(totalSize);
					popAbnormals = popAbnormalService.findList(paramMap);
				}
				
			} catch (Exception e) {
				logger.error("根据条件查询POP差异订单处理集合异常：", e);
			}
		}
		model.addAttribute("popAbnormals", popAbnormals);
		model.addAttribute("popAbnormalDTO", popAbnormalDTO);
		model.addAttribute("pager", pager);
		return "popAbnormal/pop_abnormal_list";
	}

	/**
	 * 进入反馈页面
	 * 
	 * @return
	 */
//	@HrmPrivilege("TMS-REPORT-POPCY")
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(Model model) {
		logger.info("进入反馈页面");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		paramMap.put("operatorCode", erpUser.getUserId());
		paramMap.put("erpUserCode", erpUser.getUserCode());
		paramMap.put("operatorName", erpUser.getUserName());
		
		// 获取用户分拣中心相关信息
		Map<String, Object> dmsUserMap = getUserMap(erpUser.getUserId());
		if (!dmsUserMap.isEmpty()) {
			// 设置用户分拣中心信息
			paramMap.putAll(dmsUserMap);
		}

		model.addAttribute("paramMap", paramMap);
		return "popAbnormal/pop_abnormal_detail";
	}

	/**
	 * 根据订单号获取运单信息
	 * 
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/getWaybillByOrderCode", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult getWaybillByOrderCode(
			@RequestBody Map<String, String> paramMap) {
		logger.info("根据订单号获取运单信息");
		String resultMsg = "";
		String orderCode = "";
		// 验证传入参数
		if (paramMap == null) {
			logger.info("根据订单号获取运单信息，传入参数为空!");
			resultMsg = "传入参数为空!";
		} else {
			orderCode = paramMap.get("orderCode");
			if (StringUtils.isBlank(orderCode)) {
				logger.info("根据订单号获取运单信息，传入参数有误：" + paramMap);
				resultMsg = "传入参数有误！";
			}
		}
		try {
			// 验证POP订单是否正在申请中
			PopAbnormal tempAbnormal = popAbnormalService.checkByMap(paramMap);
			if (tempAbnormal != null) {
				logger.info("根据订单号获取运单信息，" + paramMap + " 运单号正在申请中，返回");
				resultMsg = "此订单号 " + orderCode + " 正在申请中，未审核通过！";
			}
			if (StringUtils.isNotBlank(resultMsg)) {
				logger.info("根据订单号获取运单信息，" + resultMsg + " 返回");
				return new JsonResult(false, resultMsg);
			}
			// 获取运单信息
			PopAbnormal popAbnormal = popAbnormalService
					.getWaybillByOrderCode(orderCode);
			
			if (popAbnormal == null) {
				logger.info("根据订单号：" + orderCode + " 获取运单信息为空");
				return new JsonResult(false, "不存在此订单号：" + orderCode);
			} else {
				logger.info("根据订单号：" + orderCode + " 获取运单信息为：" + popAbnormal);
				return new JsonResult(true, popAbnormal);
			}
		} catch (Exception e) {
			logger.error("根据订单号获取运单信息:“" + paramMap + "” 异常：", e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 保存差异订单
	 * 
	 * @param popAbnormal
	 * @return
	 */
//	@HrmPrivilege("TMS-REPORT-POPCY")
	@RequestMapping(value = "/savePopAbnormal", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult savePopAbnormal(PopAbnormal popAbnormal) {
		logger.info("保存差异订单保存差异订单 开始");
		// 验证传入参数
		if (popAbnormal == null || popAbnormal.getAbnormalType() == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| StringUtils.isBlank(popAbnormal.getOrderCode())
				|| StringUtils.isBlank(popAbnormal.getPopSupNo())
				|| StringUtils.isBlank(popAbnormal.getPopSupName())
				|| popAbnormal.getCurrentNum() == null
				|| popAbnormal.getActualNum() == null
				|| popAbnormal.getActualNum() <= 0) {
			logger.info("保存差异订单 传入参数有误！");
			return new JsonResult(false, "传入参数有误！");
		}
		if (!BusinessHelper.checkIntNumRange(popAbnormal.getActualNum())) {
			this.logger.info("保存差异订单 传入实际包裹数不大于0或大于2000！");
			return new JsonResult(false, "传入实际包裹数不大于0或大于2000");
		}
		// 生成流水号
		popAbnormal.setSerialNumber(popAbnormal.getCreateSiteCode() + "-"
				+ new Date().getTime());
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("waybillCode", popAbnormal.getWaybillCode());
			PopAbnormal tempAbnormal = popAbnormalService.checkByMap(paramMap);
			if (tempAbnormal == null) {
				logger.info("保存差异订单根据运单号“" + popAbnormal.getWaybillCode()
						+ "”验证为空，执行插入");
				int addCount = popAbnormalService.add(popAbnormal);
				if (addCount == 2) {
					logger.info("保存差异订单根据运单号“" + popAbnormal.getWaybillCode()
							+ "”成功");
					return new JsonResult(true, "申请成功");
				} else {
					logger.info("保存差异订单根据运单号“" + popAbnormal.getWaybillCode()
							+ "”失败");
					return new JsonResult(false, "申请失败，请稍后重试！");
				}
			} else {
				logger.info("保存差异订单根据运单号“" + popAbnormal.getWaybillCode()
						+ "”验证不为空，不执行插入");
				return new JsonResult(false, "此订单号已申请过，且未审核通过！");
			}
		} catch (Exception e) {
			logger.error(
					"保存差异订单，运单号“" + popAbnormal.getWaybillCode() + "” 异常：", e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 导出POP差异订单数据
	 * 
	 * @param popAbnormalDTO
	 * @param pager
	 * @param response
	 * @return
	 */
//	@HrmPrivilege("TMS-REPORT-POPCY")
	@RequestMapping(value = "/exportPopAbnormal", method = RequestMethod.POST)
	public String exportPopAbnormal(PopAbnormalDTO popAbnormalDTO, Pager pager,
			HttpServletResponse response) {
		logger.info("导出POP差异订单数据");
		Map<String, Object> paramMap = ObjectMapHelper.makeObject2Map(popAbnormalDTO);
		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

		// 设置分页对象
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager(pager.getPageNo(), pager.getPageSize());
		}
		paramMap.putAll(ObjectMapHelper.makeObject2Map(pager));

		OutputStream os = null;
		try {
			List<PopAbnormal> popAbnormals = null;
			// 获取用户分拣中心相关信息
			Map<String, Object> dmsUserMap = getUserMap(erpUser.getUserId());
			try {
				if (!dmsUserMap.isEmpty()) {
					// 设置用户分拣中心信息
					paramMap.putAll(dmsUserMap);
					popAbnormals = popAbnormalService.findList(paramMap);
				}
			} catch (Exception e) {
				this.logger.error("根据条件查询POP差异订单处理集合异常：", e);
			}
			os = response.getOutputStream();
			String filename = "POP差异订单数据" + DateHelper.formatDate(new Date())
					+ ".xls";
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ new String(filename.getBytes("GB2312"), "ISO8859-1")
					+ "\"");

			WorkBookObject wb = new WorkBookObject(filename);
			wb.addTitle(new String[] { "订单号", "运单号", "异常类型", "申请状态", "流水单号",
					"商家编号", "原包裹数", "实际包裹数", "确认包裹数", "申请时间", "申请人ID", "申请人姓名",
					"确认时间", "更新时间", "备注" });
			wb.saveTitle();

			if (popAbnormals != null && !popAbnormals.isEmpty()) {
				String[] tAttrs = new String[] { "orderCode", "waybillCode",
						"abnormalType", "abnormalState", "serialNumber",
						"popSupNo", "currentNum", "actualNum", "confirmNum",
						"createTime", "operatorCode", "operatorName",
						"confirmTime", "updateTime", "memo" };
				wb.saveDataList(popAbnormals, PopAbnormal.class, tAttrs);
			}

			wb.getWb().write(os);

			os.flush();
		} catch (IOException e) {
			logger.error("根据条件查询POP差异订单处理集合异常：", e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				logger.error("关闭数据流 ERROR", e);
			}
		}
		return null;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

	/**
	 * 根据用户编号获取用户所属站点信息
	 * @param staffId 用户编号
	 * @return
	 */
	private Map<String, Object> getUserMap(Integer staffId) {
		return popAbnormalService.getBaseStaffByStaffId(staffId);
	}
}
