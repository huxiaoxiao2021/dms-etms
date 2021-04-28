package com.jd.bluedragon.distribution.web.popAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.LossProductResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalQuery;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.helper.AbnormalReason;
import com.jd.bluedragon.distribution.popAbnormal.helper.AbnormalReasonManager;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalDetailService;
import com.jd.bluedragon.distribution.popAbnormal.service.PopReceiveAbnormalService;
import com.jd.bluedragon.distribution.rest.product.LossProductResource;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.bluedragon.external.service.SortCenterServiceManager;
import com.jd.bluedragon.utils.*;
import com.jd.pop.sortcenter.ws.VenderOperInfoResult;
import com.jd.pop.sortcenter.ws.VenderOperateInfo;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
import java.util.*;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-27 下午05:30:06
 * 
 *             POP收货差异订单处理控制层New
 */
@Controller
@RequestMapping("/popReceiveAbnormal")
public class PopReceiveAbnormalController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseService baseService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private AbnormalReasonManager abnormalReasonManager;

	@Autowired
	private PopReceiveAbnormalService popReceiveAbnormalService;

	@Autowired
	private PopAbnormalDetailService popAbnormalDetailService;

	@Autowired
	private LossProductResource lossProductResource;

	@Autowired
	private SortCenterServiceManager sortCenterManager;

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

	/**
	 * 跳转到查询POP差异列表页面
	 * 
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage(Model model) {
		this.log.info("PopReceiveAbnormalController --> goListPage 跳转到查询平台订单差异列表页面");
		initSelectObject(null, model);
		initReasons(null, model);
		return "popReceiveAbnormal/pop_abnormal_list";
	}

	/**
	 * 按条件查询POP差异订单数据集合
	 * 
	 * @param popAbnormalQuery
	 * @param model
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    @JProfiler(jKey = "DMSWEB.PopReceiveAbnormalController.list", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
	public String list(PopAbnormalQuery popAbnormalQuery, Model model) {
		this.log.info("PopReceiveAbnormalController --> list 按条件查询平台订单差异订单数据集合");

		Boolean checkParamOK = Boolean.TRUE;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			if (popAbnormalQuery == null) {
				checkParamOK = Boolean.FALSE;
			}
			paramMap = ObjectMapHelper.makeObject2Map(popAbnormalQuery);

			initSelectObject(paramMap, model);
			initReasons(paramMap, model);

		} catch (Exception e) {
			checkParamOK = Boolean.FALSE;
		}

		if (checkParamOK) {

			try {
				Pager<List<PopReceiveAbnormal>> pager = popAbnormalQuery
						.getPager();

				// 设置分页参数
				if (pager == null) {
					pager = new Pager<List<PopReceiveAbnormal>>(
							Pager.DEFAULT_PAGE_NO, Pager.DEFAULT_PAGE_SIZE);
				} else {
					if (pager.getPageNo() == null || pager.getPageNo() <= 0) {
						pager.setPageNo(Pager.DEFAULT_PAGE_NO);
					}
					if (pager.getPageSize() == null || pager.getPageSize() <= 0
							|| pager.getPageSize() > Pager.DEFAULT_PAGE_SIZE) {
						pager.setPageSize(Pager.DEFAULT_PAGE_SIZE);
					}
				}

				pager = new Pager<List<PopReceiveAbnormal>>(pager.getPageNo(),
						pager.getPageSize());

				popAbnormalQuery.setPager(pager);

				paramMap.put("pageNo", pager.getPageNo());
				paramMap.put("pageSize", pager.getPageSize());
				paramMap.put("startIndex", pager.getStartIndex());
				paramMap.put("endIndex", pager.getEndIndex());

				List<PopReceiveAbnormal> popReceiveAbnormals = null;

				if (!paramMap.isEmpty()) {
					// 获取总数量
					int totalSize = this.popReceiveAbnormalService
							.findTotalCount(paramMap);
					this.log.info("PopReceiveAbnormalController list --> 获取总数量为：{}",totalSize);
					if (totalSize > 0) {
						pager.setTotalSize(totalSize);
						popReceiveAbnormals = this.popReceiveAbnormalService
								.findList(paramMap);
						pager.setData(popReceiveAbnormals);
					} else {
						this.log.info("PopReceiveAbnormalController list --> paramMap：{}, 调用服务成功，数据为空",paramMap);
					}
				}
			} catch (Exception e) {
				this.log.error("PopReceiveAbnormalController list SERVER ERROR:", e);
			}

		} else {
			this.log.warn("PopReceiveAbnormalController list PARAM ERROR --> 传入参数非法");
		}

		model.addAttribute("popAbnormalQuery", popAbnormalQuery);
		return "popReceiveAbnormal/pop_abnormal_list";
	}

	/**
	 * 进入反馈或显示页面
	 * 
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/addOrDetail", method = RequestMethod.GET)
	public String addOrDetail(Model model, Long abnormalId) {
		this.log.debug("PopReceiveAbnormalController addOrDetail --> 进入反馈或显示页面");
		initSelectObject(null, model);
		initReasons(null, model);

		if (abnormalId != null && abnormalId > 0) {
			PopReceiveAbnormal popReceiveAbnormal = new PopReceiveAbnormal();
			popReceiveAbnormal.setAbnormalId(abnormalId);

			popReceiveAbnormal = this.popReceiveAbnormalService
					.findByObj(popReceiveAbnormal);
			try {
				model.addAttribute("popReceiveAbnormal", popReceiveAbnormal);
				model.addAttribute("popAbnormalDetails",
						this.popAbnormalDetailService
								.findListByAbnormalId(abnormalId));
			} catch (Exception e) {
				this.log.error("PopReceiveAbnormalController detail --> 异常：", e);
			}
		}

		return "popReceiveAbnormal/pop_abnormal_addOrDetail";
	}

	/**
	 * 根据订单号获取运单信息
	 * 
	 * @param paramMap
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/getWaybill", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult getWaybillByOrderCode(
			@RequestBody Map<String, String> paramMap) {
		log.debug("PopReceiveAbnormalController getWaybillByOrderCode --> 根据订单号获取运单信息");
		String resultMsg = "";
		String waybillCode = "";
		// 验证传入参数
		if (paramMap == null || StringUtils.isBlank(paramMap.get("waybillCode"))) {
			if(log.isInfoEnabled()){
				log.info("PopReceiveAbnormalController getWaybillByOrderCode --> 传入参数:{}", JsonHelper.toJson(paramMap));
			}
			return new JsonResult(false, JdResponse.MESSAGE_PARAM_ERROR);
		} else {
			waybillCode = paramMap.get("waybillCode");
		}
		try {
			// 验证POP订单是否正在申请中
			PopReceiveAbnormal tempAbnormal = this.popReceiveAbnormalService
					.findByMap(paramMap);
			if (tempAbnormal != null) {
				log.info("根据订单号获取运单信息，{} 运单号正在处理中，返回",paramMap);
				resultMsg = "此订单号 " + waybillCode + " 正在处理中，未处理完成！";
			}
			if (StringUtils.isNotBlank(resultMsg)) {
				log.info("根据订单号获取运单信息，{} 返回",resultMsg);
				return new JsonResult(false, resultMsg);
			}

            // 如果是订单未发货、商家类型是SOP或者纯外单，只需验证订单是合法的并且没有在处理
            if ("4".equals(paramMap.get("mainType")) && StringUtils.isNotBlank(paramMap.get("subType"))
                && StringUtils.isNotBlank(paramMap.get("orderType")) && !"23".equals(paramMap.get("orderType"))
                && !"25".equals(paramMap.get("orderType"))){
                return new JsonResult(true, "订单号符合要求");
            }

			// 获取运单信息
			PopReceiveAbnormal popAbnormal = null;

			if ("4".equals(paramMap.get("mainType"))) {
				popAbnormal = new PopReceiveAbnormal();
				popAbnormal.setWaybillCode(waybillCode);
				try {
					VenderOperInfoResult venderOperInfoResult = null;
					if(NumberHelper.isNumber(waybillCode)){
						venderOperInfoResult = this.sortCenterManager.searchInfoForByOrderId(Long.valueOf(waybillCode));
					}else{
						this.log.warn("录入的订单号不是数字类型：【{}】 " , waybillCode);
					}
					if (venderOperInfoResult == null) {
						this.log.warn("根据订单号【{}】 获取POP相关信息为空", waybillCode);
					} else {
						if ("10100000".equals(venderOperInfoResult
								.getResultCode())
								&& venderOperInfoResult.getVenderOperateInfo() != null) {
							VenderOperateInfo venderOperateInfo = venderOperInfoResult
									.getVenderOperateInfo();
							popAbnormal.setPopSupNo(String
									.valueOf(venderOperateInfo.getVenderId()));
							popAbnormal.setPopSupName(venderOperateInfo
									.getCompanyName());
							popAbnormal
									.setAttr1(venderOperateInfo
											.getOperatorName()
											+ ", "
											+ venderOperateInfo
													.getOperatorPhone()
											+ ", "
											+ venderOperateInfo
													.getOperatorTel());
						} else {
							this.log.info("根据订单号【{}】 获取无POP相关信息：{}-{}",waybillCode, venderOperInfoResult.getResultCode(), venderOperInfoResult.getResultDescrib());
						}
					}
				} catch (Exception e) {
					this.log.error("根据订单号【{}】 获取POP相关信息异常：",waybillCode, e);
				}
				if (StringUtils.isBlank(popAbnormal.getAttr1())) {
					popAbnormal.setAttr1("无");
				}
			} else {
				popAbnormal = this.popReceiveAbnormalService
						.getWaybillByWaybillCode(waybillCode);
			}

			if (popAbnormal == null) {
				this.log.info("根据订单号：{} 获取运单信息为空",waybillCode);
				return new JsonResult(false, "不存在此订单号：" + waybillCode);
			} else {
				if ("6".equals(paramMap.get("mainType"))) {
					String attr1 = null;
					try {
						String orderCode = waybillCode;
						if (Constants.POP_SOP.equals(popAbnormal.getWaybillType())) {
							orderCode = popAbnormal.getOrderCode();
						}
						LossProductResponse lossProductResponse = this.lossProductResource
								.getOrderProductQuantity(orderCode);
						if (lossProductResponse != null
								&& lossProductResponse.getActualQuantity() != null) {
							attr1 = String.valueOf(lossProductResponse
									.getActualQuantity());
						}
					} catch (Exception e) {
						this.log.error("根据订单号：{} 获取商品数量异常：",waybillCode, e);
					}
					if (StringUtils.isBlank(attr1) || "null".equals(attr1)) {
						attr1 = "";
					}
					popAbnormal.setAttr1(attr1);
				}
				
				if (popAbnormal.getWaybillType() == null) {
					popAbnormal.setWaybillType(0);
				}
				
				if (StringUtils.isBlank(popAbnormal.getOrderCode())) {
					popAbnormal.setOrderCode(popAbnormal.getWaybillCode());
				}
				
				this.log.info("根据订单号：{} 获取运单信息为：{}",waybillCode, popAbnormal);
				return new JsonResult(true, popAbnormal);
			}
		} catch (Exception e) {
			this.log.error("根据订单号获取运单信息:{}",paramMap, e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 保存差异订单
	 * 
	 * @param popReceiveAbnormal
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/savePopAbnormal", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult savePopReceiveAbnormal(
			PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail) {
		this.log.info("savePopReceiveAbnormal --> 保存差异订单保存差异订单 开始");
		// 验证传入参数
		if (popReceiveAbnormal == null
				|| popReceiveAbnormal.getOrgCode() == null
				|| popReceiveAbnormal.getCreateSiteCode() == null) {
			this.log.info("savePopReceiveAbnormal --> 传入参数有误！");
			return new JsonResult(false, "传入参数有误！");
		}
		if (popAbnormalDetail != null
				&& StringUtils.isNotBlank(popAbnormalDetail.getRemark())
				&& popAbnormalDetail.getRemark().length() > 256) {
			this.log.info("savePopReceiveAbnormal --> 传入Remark字段太长！");
			return new JsonResult(false, "输入备注长度不能大于256个字符，请减短！");
		}
		try {
			if (popReceiveAbnormal.getAbnormalId() != null
					&& popReceiveAbnormal.getAbnormalId() > 0) {
				this.log.info("savePopReceiveAbnormal --> 第二次提交 [{}]，执行更新、插入",popReceiveAbnormal.getAbnormalId());
				popReceiveAbnormal
						.setAbnormalStatus(PopReceiveAbnormal.IS_FINISH);
				int addCount = this.popReceiveAbnormalService.update(
						popReceiveAbnormal, popAbnormalDetail, Boolean.TRUE);
				if (addCount == Constants.RESULT_SUCCESS) {
					return new JsonResult(true, "提交成功");
				} else {
					return new JsonResult(false, "提交失败，请稍后重试！");
				}
			} else {
				 if (popReceiveAbnormal.getMainType().equals(2)
						|| popReceiveAbnormal.getMainType().equals(3) 
						|| popReceiveAbnormal.getMainType().equals(4)) {
					if (popReceiveAbnormal.getSubType() == null) {
						this.log.info("savePopReceiveAbnormal --> 子类型不能为空，请选择！");
						return new JsonResult(false, "子类型不能为空，请选择！");
					}
				} else if (popReceiveAbnormal.getMainType().equals(5)
						|| popReceiveAbnormal.getMainType().equals(6)
						|| (popReceiveAbnormal.getMainType().equals(4) && popReceiveAbnormal
								.getSubType().equals(402))) {
					if (!BusinessHelper.checkIntNumRange(Integer
							.valueOf(popReceiveAbnormal.getAttr2()))) {
						this.log.info("savePopReceiveAbnormal --> 传入实际数量不大于0或大于2000！");
						return new JsonResult(false, "传入实际数量不大于0或大于2000");
					}
				}

				PopReceiveAbnormal tempAbnormal = null;

				if (StringUtils.isNotBlank(popReceiveAbnormal.getWaybillCode())) {
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("waybillCode", popReceiveAbnormal
							.getWaybillCode());
					if (popReceiveAbnormal.getMainType() != null) {
						paramMap.put("mainType", String
								.valueOf(popReceiveAbnormal.getMainType()));
					}
					if (popReceiveAbnormal.getSubType() != null) {
						paramMap.put("subType", String
								.valueOf(popReceiveAbnormal.getSubType()));
					}
					tempAbnormal = this.popReceiveAbnormalService
							.findByMap(paramMap);
				}
				if (tempAbnormal == null) {
					this.log.info("savePopReceiveAbnormal --> 根据运单号[{}]验证为空，执行插入",popReceiveAbnormal.getWaybillCode());

					if (StringUtils.isBlank(popReceiveAbnormal.getPopSupNo())) {
						popReceiveAbnormal.setPopSupNo(null);
					}
					if (StringUtils.isBlank(popReceiveAbnormal.getPopSupName())) {
						popReceiveAbnormal.setPopSupName(null);
					}
					if (StringUtils.isBlank(popReceiveAbnormal.getAttr1())) {
						popReceiveAbnormal.setAttr1(null);
					}
					if (StringUtils.isBlank(popReceiveAbnormal.getAttr2())) {
						popReceiveAbnormal.setAttr2(null);
					}
					try {
						if (StringUtils.isBlank(popReceiveAbnormal
								.getPopSupNo())
								|| "null".equals(popReceiveAbnormal
										.getPopSupNo())) {
							// 获取运单信息，补全POP商家信息
							tempAbnormal = this.popReceiveAbnormalService
									.getWaybillByWaybillCode(popReceiveAbnormal
											.getWaybillCode());
							if (tempAbnormal != null) {
								popReceiveAbnormal.setPopSupNo(tempAbnormal
										.getPopSupNo());
								popReceiveAbnormal.setPopSupName(tempAbnormal
										.getPopSupName());
							}
						}
					} catch (Exception e) {
						this.log.error("savePopReceiveAbnormal --> 运单号[{}] 补全POP商家信息异常：",popReceiveAbnormal.getWaybillCode(), e);
					}

					int addCount = this.popReceiveAbnormalService.add(
							popReceiveAbnormal, popAbnormalDetail);
					if (addCount == Constants.RESULT_SUCCESS) {
						this.log.info("savePopReceiveAbnormal --> 根据运单号[{}]成功",popReceiveAbnormal.getWaybillCode());
						return new JsonResult(true, "提交成功");
					} else {
						this.log.info("savePopReceiveAbnormal --> 根据运单号{}失败",popReceiveAbnormal.getWaybillCode());
						return new JsonResult(false, "提交失败，请稍后重试！");
					}
				} else {
					this.log.info("savePopReceiveAbnormal --> 根据运单号{}验证不为空，不执行插入",popReceiveAbnormal.getWaybillCode());
					return new JsonResult(false, "此订单号已提交过，且未审核通过！");
				}
			}
		} catch (Exception e) {
			this.log.error("savePopReceiveAbnormal --> 运单号[{}] 异常：",popReceiveAbnormal.getWaybillCode(), e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 取消差异订单
	 * @param paramMap
	 * @return
     */
    @Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/cancelPopReceiveAbnormal", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult cancelPopReceiveAbnormal(@RequestBody Map<String, String> paramMap) {
		this.log.info("cancelPopReceiveAbnormal --> 取消差异订单 开始");
		
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		if (erpUser == null || !erpUser.getUserId().equals(24779)) {
			this.log.info("cancelPopReceiveAbnormal --> 无权限！");
			return new JsonResult(false, "无权限，请联系相关人员进行操作！");
		}
		
		try {
			Boolean checkParamOK = Boolean.FALSE;
			Long abnormalId = null;
			if (paramMap == null || paramMap.isEmpty()) {
				checkParamOK = Boolean.TRUE;
			} else {
				abnormalId = Long.valueOf(paramMap.get("abnormalId"));
				if (abnormalId == null || abnormalId <= 0) {
					checkParamOK = Boolean.TRUE;
				}
			}
			
			if (checkParamOK) {
				this.log.info("cancelPopReceiveAbnormal --> 传入参数有误！");
				return new JsonResult(false, "传入参数有误！");
			}
		
			int resultCode = this.popReceiveAbnormalService.delete(abnormalId);
			
			if (resultCode == Constants.RESULT_SUCCESS) {
				this.log.info("cancelPopReceiveAbnormal --> 根据主键[{}]取消成功",abnormalId);
				return new JsonResult(true, "取消成功");
			} else {
				this.log.info("cancelPopReceiveAbnormal --> 根据主键【{}】取消失败",abnormalId);
				return new JsonResult(false, "取消失败，请确认是否存在相应数据！");
			}
			
		} catch (Exception e) {
			this.log.error("cancelPopReceiveAbnormal --> 取消差异订单 异常：", e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 导出POP差异订单数据
	 * @param popAbnormalQuery
	 * @param response
     * @return
     */
    @Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/exportPopAbnormal", method = RequestMethod.POST)
	@ResponseBody
	@JProfiler(jKey = "com.jd.bluedragon.distribution.web.popAbnormal.PopReceiveAbnormalController.exportPopReceiveAbnormal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult exportPopReceiveAbnormal(PopAbnormalQuery popAbnormalQuery, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		this.log.info("导出平台差异订单数据");

		if (popAbnormalQuery == null) {
			result.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,InvokeResult.PARAM_ERROR);
			this.log.warn("PopReceiveAbnormalController exportPopReceiveAbnormal PARAM ERROR --> 传入参数非法");
			return result;
		}

		BufferedWriter bfw = null;
		try {
			Map<String, Object> paramMap =  ObjectMapHelper.makeObject2Map(popAbnormalQuery);
			if(paramMap.isEmpty()){
				this.log.warn("PopReceiveAbnormalController exportPopReceiveAbnormal PARAM ERROR --> 查询参数为空");
				result.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,InvokeResult.PARAM_ERROR);
				return result;
			}

			exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.POP_RECEIVE_ABNORMAL_REPORT.getCode());

			String fileName = "平台收货差异订单数据";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);
			popReceiveAbnormalService.export(paramMap,bfw);
			exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.POP_RECEIVE_ABNORMAL_REPORT.getCode());
		} catch (Exception e) {
			this.log.error("根据条件查询平台差异订单导出数据异常：", e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
		} finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException e) {
				log.error("平台收货差异订单数据 流关闭异常", e);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
		return result;
	}

	@Authorization(Constants.DMS_WEB_PTORDER_DIFF_R)
	@RequestMapping(value = "/getSubReasons", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult getSubReasonsByMainType(
			@RequestBody Map<String, String> paramMap) {
		this.log.info("PopReceiveAbnormalController getSubReasons");
		String resultMsg = "";
		Integer mainType = 0;
		// 验证传入参数
		if (paramMap == null) {
			log.info("PopReceiveAbnormalController getSubReasons --> 传入参数为空!");
			resultMsg = "传入参数为空!";
		} else {
			mainType = Integer.parseInt(paramMap.get("mainType"));
			if (mainType == null || mainType.equals(0)) {
				log.info("PopReceiveAbnormalController getSubReasons --> 传入参数有误：{}",paramMap);
				resultMsg = "传入参数有误！";
			}
		}

		if ("".equals(resultMsg)) {
			return new JsonResult(true, this.abnormalReasonManager
					.getSubReasonsByMainType(mainType));
		} else {
			return new JsonResult(false, resultMsg);
		}
	}

	/**
	 * 初始化查询条件
	 * 
	 * @param paramMap
	 * @param model
	 */
	private void initSelectObject(Map<String, Object> paramMap, Model model) {
		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		List<BaseOrg> orgList = new ArrayList<BaseOrg>();
		
		model.addAttribute("erpUser", erpUser);

		try {
			this.log.info("初始化查询条件-->调用基础资料获取某个员工信息开始");
			BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseService
					.getBaseStaffByStaffId(erpUser.getUserId());

			Integer defaultSiteCode = null;
			Integer defaultOrgId = null;
			Integer defaultSiteType = null;

			if (baseStaffSiteOrgDto != null) {
				defaultOrgId = baseStaffSiteOrgDto.getOrgId();
				defaultSiteCode = baseStaffSiteOrgDto.getSiteCode();
				defaultSiteType = baseStaffSiteOrgDto.getSiteType();

				if (defaultSiteCode != null
						&& Constants.DMS_SITE_TYPE.equals(defaultSiteType)) {
					this.log.info("初始化查询条件-->员工信息 属于分拣中心");
					if (paramMap != null) {
						paramMap.put("createSiteCode", defaultSiteCode);
					}

					// 反馈页面需要参数
					model.addAttribute("userInfo", baseStaffSiteOrgDto);
				}
				if (defaultOrgId != null) {
					BaseOrg baseOrg = new BaseOrg();
					baseOrg.setOrgId(defaultOrgId);
					baseOrg.setOrgName(baseStaffSiteOrgDto.getOrgName());
					orgList.add(baseOrg);
				}
			} else {
				this.log.info("初始化查询条件-->调用基础资料获取某个员工信息 为空");
				orgList = baseService.getAllOrg();
			}

			model.addAttribute("orgList", orgList);

			List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();
			if (defaultSiteType != null
					&& defaultSiteType.equals(Constants.DMS_SITE_TYPE)) {
				siteList.add(baseStaffSiteOrgDto);
			} else if (defaultOrgId != null) {
				siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType(defaultOrgId,
						Constants.DMS_SITE_TYPE);
			} else if (paramMap != null && paramMap.get("orgCode") != null) {
				siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType(
						(Integer) paramMap.get("orgCode"),
						Constants.DMS_SITE_TYPE);
			}
			model.addAttribute("siteList", siteList);
		} catch (Exception e) {
			this.log.error("初始化查询条件异常", e);
		}
	}

	/**
	 * 初始化差异类型
	 * 
	 * @param paramMap
	 * @param model
	 */
	private void initReasons(Map<String, Object> paramMap, Model model) {
		List<AbnormalReason> mainReasons = this.abnormalReasonManager
				.getMainReasons();

		List<AbnormalReason> subReasons = null;
		if (paramMap != null && !paramMap.isEmpty()) {
			Integer mainType = (Integer) paramMap.get("mainType");
			if (mainType != null) {
				subReasons = this.abnormalReasonManager
						.getSubReasonsByMainType(mainType);
			}
		}

		model.addAttribute("subReasons", subReasons);
		model.addAttribute("mainReasons", mainReasons);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

}
