package com.jd.bluedragon.distribution.web.popAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDTO;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.bluedragon.utils.*;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PopAbnormalService popAbnormalService;

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

	/**
	 * 跳转到查询POP差异列表页面
	 * 
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage() {
		log.debug("跳转到查询POP差异列表页面");
		return "popAbnormal/pop_abnormal_list";
	}

	/**
	 * 按条件查询POP差异订单数据集合
	 * 
	 * @param pager
	 * @param model
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(PopAbnormalDTO popAbnormalDTO, Pager pager, Model model) {
		log.debug("按条件查询POP差异订单数据集合");
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
		Map<String, Object> dmsUserMap = getUserMap(erpUser.getStaffNo());
		if (!dmsUserMap.isEmpty()) {
			// 设置用户分拣中心信息
			paramMap.putAll(dmsUserMap);
			try {
				// 获取总数量
				int totalSize = popAbnormalService.findTotalCount(paramMap);
				log.info("按条件查询POP差异订单数据集合 --> 获取总数量为：{}", totalSize);
				if (totalSize > 0) {
					pager.setTotalSize(totalSize);
					popAbnormals = popAbnormalService.findList(paramMap);
				}
				
			} catch (Exception e) {
				log.error("根据条件查询POP差异订单处理集合异常：", e);
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
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(Model model) {
		log.debug("进入反馈页面");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		paramMap.put("operatorCode", erpUser.getUserId());
		paramMap.put("erpUserCode", erpUser.getUserCode());
		paramMap.put("operatorName", erpUser.getUserName());
		
		// 获取用户分拣中心相关信息
		Map<String, Object> dmsUserMap = getUserMap(erpUser.getStaffNo());
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
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/getWaybillByOrderCode", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult getWaybillByOrderCode(
			@RequestBody Map<String, String> paramMap) {
		log.debug("根据订单号获取运单信息");
		String resultMsg = "";
		String orderCode = "";
		// 验证传入参数
		if (paramMap == null) {
			log.warn("根据订单号获取运单信息，传入参数为空!");
			resultMsg = "传入参数为空!";
		} else {
			orderCode = paramMap.get("orderCode");
			if (StringUtils.isBlank(orderCode)) {
				log.warn("根据订单号获取运单信息，传入参数有误：{}", paramMap);
				resultMsg = "传入参数有误！";
			}
		}
		try {
			// 验证POP订单是否正在申请中
			PopAbnormal tempAbnormal = popAbnormalService.checkByMap(paramMap);
			if (tempAbnormal != null) {
				log.warn("根据订单号获取运单信息，{} 运单号正在申请中，返回",paramMap);
				resultMsg = "此订单号 " + orderCode + " 正在申请中，未审核通过！";
			}
			if (StringUtils.isNotBlank(resultMsg)) {
				log.warn("根据订单号获取运单信息，{} 返回", resultMsg);
				return new JsonResult(false, resultMsg);
			}
			// 获取运单信息
			PopAbnormal popAbnormal = popAbnormalService
					.getWaybillByOrderCode(orderCode);
			
			if (popAbnormal == null) {
				log.info("根据订单号：{}获取运单信息为空",orderCode);
				return new JsonResult(false, "不存在此订单号：" + orderCode);
			} else {
				log.info("根据订单号：{} 获取运单信息为：{}",orderCode, popAbnormal);
				return new JsonResult(true, popAbnormal);
			}
		} catch (Exception e) {
			log.error("根据订单号获取运单信息:{}异常",paramMap, e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 保存差异订单
	 * 
	 * @param popAbnormal
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/savePopAbnormal", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult savePopAbnormal(PopAbnormal popAbnormal) {
		log.debug("保存差异订单保存差异订单 开始");
		// 验证传入参数
		if (popAbnormal == null || popAbnormal.getAbnormalType() == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| StringUtils.isBlank(popAbnormal.getOrderCode())
				|| StringUtils.isBlank(popAbnormal.getPopSupNo())
				|| StringUtils.isBlank(popAbnormal.getPopSupName())
				|| popAbnormal.getCurrentNum() == null
				|| popAbnormal.getActualNum() == null
				|| popAbnormal.getActualNum() <= 0) {
			log.info("保存差异订单 传入参数有误！");
			return new JsonResult(false, "传入参数有误！");
		}
		if (!BusinessHelper.checkIntNumRange(popAbnormal.getActualNum())) {
			this.log.info("保存差异订单 传入实际包裹数不大于0或大于2000！");
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
				log.info("保存差异订单根据运单号 {} 验证为空，执行插入", popAbnormal.getWaybillCode());
				int addCount = popAbnormalService.add(popAbnormal);
				if (addCount == 2) {
					log.info("保存差异订单根据运单号{}成功", popAbnormal.getWaybillCode());
					return new JsonResult(true, "申请成功");
				} else {
					log.info("保存差异订单根据运单号{}失败", popAbnormal.getWaybillCode());
					return new JsonResult(false, "申请失败，请稍后重试！");
				}
			} else {
				log.info("保存差异订单根据运单号 {} 验证不为空，不执行插入",popAbnormal.getWaybillCode());
				return new JsonResult(false, "此订单号已申请过，且未审核通过！");
			}
		} catch (Exception e) {
			log.error("保存差异订单，运单号 {} 异常：",popAbnormal.getWaybillCode(), e);
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
	@Authorization(Constants.DMS_WEB_POP_ABNORMAL_R)
	@RequestMapping(value = "/exportPopAbnormal", method = RequestMethod.POST)
	@ResponseBody
	@JProfiler(jKey = "com.jd.bluedragon.distribution.web.popAbnormal.PopAbnormalController.exportPopAbnormal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult exportPopAbnormal(PopAbnormalDTO popAbnormalDTO, Pager pager, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		log.debug("导出POP差异订单数据");
		Map<String, Object> paramMap = ObjectMapHelper.makeObject2Map(popAbnormalDTO);

		// 设置分页对象
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager(pager.getPageNo(), pager.getPageSize());
		}
		paramMap.putAll(ObjectMapHelper.makeObject2Map(pager));

		BufferedWriter bfw = null;
		try {
			exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.POP_ABNORMAL_REPORT.getCode());
			String fileName = "POP差异订单数据";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);

			Map<String, Object>  dmsUserMap = checkUser();
			if (MapUtils.isNotEmpty(dmsUserMap)) {
				// 设置用户分拣中心信息
				paramMap.putAll(dmsUserMap);
			}
			popAbnormalService.export(paramMap,bfw);
			exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.POP_ABNORMAL_REPORT.getCode());

		} catch (Exception e) {
			log.error("根据条件查询POP差异订单处理集合异常：", e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,Constants.DMS_WEB_PTORDER_DIFF_R+InvokeResult.RESULT_EXPORT_MESSAGE);
		} finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException e) {
				log.error("POP差异订单数据 流关闭异常", e);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
		return result;
	}

	/**
	 * 根据用户编号获取用户所属站点信息
	 * staffId 用户编号
	 * @return
	 */
	private Map<String, Object> checkUser(){
		Map<String, Object>  dmsUserMap = null;
		try {
			// 验证登陆信息
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			// 获取用户分拣中心相关信息
			 dmsUserMap = popAbnormalService.getBaseStaffByStaffId(erpUser.getStaffNo());
		}catch (Exception e){
			log.error("获取用户信息异常",e);
		}
		return dmsUserMap;
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
