package com.jd.bluedragon.distribution.popAbnormal.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popAbnormal.dao.PopAbnormalDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.rest.client.UpdateCacheWaybillClient;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalService;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.AbnormalResult;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.PopAbnormalOrderPackageWebService;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.PopAbnormalOrderVo;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.PopOrderDto;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.WaybillService;
import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillUpdateApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.dto.WaybillPOPDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:43:35
 * 
 *             POP差异订单Service
 */
@Service("popAbnormalService")
public class PopAbnormalServiceImpl implements PopAbnormalService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	WaybillQueryApi waybillQueryApi;

	@Autowired
	private WaybillUpdateApi waybillUpdateApi;

	@Autowired
	private PopAbnormalDao popAbnormalDao;

	@Autowired
	private PopAbnormalOrderPackageWebService popAbnormalOrderPackageWebService;

	@Autowired
	private InspectionDao inspectionDao;

	@Autowired
	private PopPrintDao popPrintDao;
	
	@Autowired
	private WaybillService waybillService; 

	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.getBaseStaffByStaffId")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Map<String, Object> getBaseStaffByStaffId(Integer staffId) {
		logger.info("根据员工编号获取员工信息，staffId:" + staffId);
		if (staffId == null) {
			logger.info("根据员工编号获取员工信息 --> getBaseStaffByStaffId 传入参数为空");
			return Collections.emptyMap();
		}
		try {
			// 调用运单接口
			this.logger.info("根据员工编号获取员工信息（调用基础资料接口）开始，员工编号： " + staffId);
			BaseStaffSiteOrgDto user = baseMajorManager
					.getBaseStaffByStaffId(staffId);
			if (user == null) {
				logger.info("根据员工编号获取员工信息（调用基础资料接口）为空，staffId:" + staffId);
			} else {
				logger.info("根据员工编号获取员工信息（调用基础资料接口）为:" + user + "，staffId:"
						+ staffId);
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("createSiteCode", user.getSiteCode());
				paramMap.put("createSiteName", user.getSiteName());
				return paramMap;
			}
		} catch (Exception e) {
			logger.error("根据员工编号“" + staffId + "”获取员工信息（调用基础资料接口） 异常："
					+ e.getMessage());
		}
		return Collections.emptyMap();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public int findTotalCount(Map<String, Object> paramMap) {
		logger.info("按条件查询POP差异订单信息，paramMap:" + paramMap);
		return popAbnormalDao.findTotalCount(paramMap);
	}

	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.findList")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PopAbnormal> findList(Map<String, Object> paramMap) {
		logger.info("按条件查询POP差异订单信息，paramMap:" + paramMap);
		return popAbnormalDao.findList(paramMap);
	}

	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.getWaybillByOrderCode")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PopAbnormal getWaybillByOrderCode(String orderCode) {
		PopAbnormal popAbnormal = null;
		try {
			// 调用运单接口
			this.logger.info("调用运单接口, 订单号为： " + orderCode);
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(true);
			wChoice.setQueryWaybillE(true);
			// wChoice.setQueryWaybillM(true);
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryApi
					.getDataByChoice(orderCode, wChoice);
			if ((baseEntity != null) && (baseEntity.getData() != null)) {
				popAbnormal = this.convWaybill(baseEntity.getData());
				if (popAbnormal == null) {
					// 无数据
					this.logger.info("调用运单接口, 订单号为： " + orderCode
							+ " 调用运单WSS数据为空");
				}
			}
		} catch (Exception e) {
			this.logger
					.error(
							"PopAbnormalServiceImpl --> getWaybillByOrderCode, 调用运单接口异常：",
							e);
			return null;
		}
		return popAbnormal;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PopAbnormal checkByMap(Map<String, String> paramMap) {
		if (paramMap == null || paramMap.isEmpty()) {
			logger.info("POP差异订单Service --> checkByMap 传入验证参数为空");
			return null;
		}
		return popAbnormalDao.checkByMap(paramMap);
	}

	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(PopAbnormal popAbnormal) {
		int flag = 0;
		if (popAbnormal == null) {
			logger.info("POP差异订单Service --> add 传入对象为空");
			return (flag - 1);
		}
		// 调用POP插入数据接口,成功后 flag 为 1
		try {
			this.logger.info("POP差异订单Service，增加POP差异反馈单，调用POP接口 开始，运单号："
					+ popAbnormal.getWaybillCode());
			AbnormalResult abnormalResult = popAbnormalOrderPackageWebService
					.savePopAbnormalOrderPackage(convPopAbnormalVo(popAbnormal));
			if (abnormalResult.isSuccess()) {
				this.logger.info("POP差异订单Service，增加POP差异反馈单，调用POP接口 成功，运单号："
						+ popAbnormal.getWaybillCode());
				flag++;
			} else {
				this.logger.info("POP差异订单Service，增加POP差异反馈单，调用POP接口 失败，运单号："
						+ popAbnormal.getWaybillCode() + ", POP返回消息："
						+ abnormalResult.getMessage());
				return flag;
			}

		} catch (Exception e) {
			this.logger.error("POP差异订单Service，增加POP差异反馈单，调用POP接口 异常：", e);
			return flag;
		}

		// 插入本地数据库,成功后 flag 为 2
		try {
			popAbnormalDao.add(popAbnormal);
			this.logger.info("POP差异订单Service，增加POP差异反馈单 成功，ID："
					+ popAbnormal.getId());
			flag++;
		} catch (Exception e) {
			logger.error("POP差异订单Service，插入本地数据（运单号："
					+ popAbnormal.getWaybillCode() + "）异常：", e);
			return flag;
		}
		return flag;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.updatePopPackNum")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updatePopPackNum(PopAbnormal popAbnormal) {
		int flag = 0;
		if (popAbnormal == null || popAbnormal.getId() == null
				|| popAbnormal.getId() <= 0) {
			this.logger.info("updatePopPackNum，更新商家确认时间，传入参数有误！");
			return (flag - 1);
		}
		// 更新运单包裹数量,更新成功后 flag 为 1
		long startTime = System.currentTimeMillis();
		com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.BaseEntity baseEntity = this.waybillService.sendPopOrders(convPopOrderList(popAbnormal));
		long endTime = System.currentTimeMillis();
		
		this.logger.info("updatePopPackNum，更新商家确认时间，更新运单包裹数量 结束参数：运单号【"
				+ popAbnormal.getWaybillCode() + "】，调用时间【"
				+ (endTime - startTime) + "】");
		
		if (baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode()) {
			Map<String, Boolean> resultMap = JsonHelper.fromJson((String) baseEntity.getData(), Map.class);
			if (resultMap != null && resultMap.get(popAbnormal.getWaybillCode())) {
				flag++;
			}
		}
		
		if (flag < 1) {
			this.logger.info("updatePopPackNum，更新商家确认时间，更新运单包裹数量 失败，运单号："
					+ popAbnormal.getWaybillCode() + ", 包裹数："
					+ popAbnormal.getConfirmNum());
			return flag;
		}
		// 更新验货表包裹数量
		int resultInt = this.inspectionDao
				.updatePop(convToInsepection(popAbnormal));
		this.logger.info("updatePopPackNum，更新商家确认时间，更新申请单 成功，ID："
				+ popAbnormal.getId());
		if (resultInt <= 0) {
			// 更新包裹标签打印表数据
			this.popPrintDao.updateByWaybillCode(convToPopPrint(popAbnormal));
		}
		flag++;

		// 更新申请单状态,更新成功后 flag 为 3
		popAbnormalDao.updateById(popAbnormal);
		this.logger.info("updatePopPackNum，更新商家确认时间，更新申请单 成功，ID："
				+ popAbnormal.getId());
		flag++;
		return flag;
	}

	@Override
	@Profiled(tag = "PopAbnormalServiceImpl.updateById")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateById(PopAbnormal popAbnormal) {
		int flag = 0;
		if (popAbnormal == null || popAbnormal.getId() == null
				|| popAbnormal.getId() <= 0) {
			this.logger.info("POP差异订单Service，更新商家确认时间，传入参数有误！");
			return (flag - 1);
		}
		// 更新Cache缓存包裹数量,更新成功后 flag 为 1
		long startTime = System.currentTimeMillis();
		int resultCode = UpdateCacheWaybillClient.updateConfirmNum(popAbnormal
				.getWaybillCode(), popAbnormal.getConfirmNum());
		long endTime = System.currentTimeMillis();
		this.logger.info("POP差异订单Service，更新商家确认时间，更新Cache缓存包裹数量，参数：运单号【"
				+ popAbnormal.getWaybillCode() + "】，调用时间【"
				+ (endTime - startTime) + "】");
		if (resultCode == 200 || resultCode == 404) {
			flag++;
		} else {
			this.logger.error("POP差异订单Service，更新商家确认时间，更新Cache缓存包裹数量失败："
					+ resultCode);
			return flag;
		}
		// 更新运单包裹数量,更新成功后 flag 为 2
		this.logger.info("POP差异订单Service，更新商家确认时间，更新运单包裹数量 开始，运单号："
				+ popAbnormal.getWaybillCode());
		startTime = System.currentTimeMillis();
		BaseEntity<List<String>> baseEntity = waybillUpdateApi
				.batchUpdataForPOP(convPopList(popAbnormal));
		endTime = System.currentTimeMillis();
		this.logger.info("POP差异订单Service，更新商家确认时间，更新运单包裹数量 结束参数：运单号【"
				+ popAbnormal.getWaybillCode() + "】，调用时间【"
				+ (endTime - startTime) + "】");

		if (baseEntity != null) {
			List<String> resultStrList = baseEntity.getData();
			if (resultStrList != null && resultStrList.size() > 0
					&& resultStrList.contains(popAbnormal.getWaybillCode())) {
				this.logger.info("POP差异订单Service，更新商家确认时间，更新运单包裹数量 成功，运单号："
						+ popAbnormal.getWaybillCode() + ", 包裹数："
						+ popAbnormal.getConfirmNum());
				flag++;
			}
		}
		if (flag < 2) {
			this.logger.info("POP差异订单Service，更新商家确认时间，更新运单包裹数量 失败，运单号："
					+ popAbnormal.getWaybillCode() + ", 包裹数："
					+ popAbnormal.getConfirmNum());
			return flag;
		}

		// 更新验货表包裹数量
		int resultInt = this.inspectionDao
				.updatePop(convToInsepection(popAbnormal));
		this.logger.info("POP差异订单Service，更新商家确认时间，更新申请单 成功，ID："
				+ popAbnormal.getId());
		if (resultInt <= 0) {
			// 更新包裹标签打印表数据
			this.popPrintDao.updateByWaybillCode(convToPopPrint(popAbnormal));
		}
		flag++;

		// 更新申请单状态,更新成功后 flag 为 4
		popAbnormalDao.updateById(popAbnormal);
		this.logger.info("POP差异订单Service，更新商家确认时间，更新申请单 成功，ID："
				+ popAbnormal.getId());
		flag++;
		return flag;
	}

	/**
	 * 转换运单基本信息
	 * 
	 * @param waybillWS
	 * @return
	 */
	public PopAbnormal convWaybill(BigWaybillDto bigWaybillDto) {
		if (bigWaybillDto == null) {
			this.logger.info("转换运单基本信息 --> 原始运单数据bigWaybillDto为空");
			return null;
		}
		com.jd.etms.waybill.domain.Waybill waybillWS = bigWaybillDto
				.getWaybill();
		if (waybillWS == null) {
			this.logger.info("转换运单基本信息 --> 原始运单数据集waybillWS为空");
			return null;
		}
		/*
		 * WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
		 * if (manageDomain == null) {
		 * this.logger.info("转换运单基本信息 --> 原始运单数据集manageDomain为空"); return null;
		 * }
		 */
		PopAbnormal popAbnormal = new PopAbnormal();
		popAbnormal.setOrderCode(waybillWS.getWaybillCode());
		popAbnormal.setWaybillCode(waybillWS.getWaybillCode());
		popAbnormal.setPopSupNo(String.valueOf(waybillWS.getConsignerId()));
		popAbnormal.setPopSupName(waybillWS.getConsigner());
		popAbnormal.setCurrentNum(waybillWS.getGoodNumber());
		return popAbnormal;
	}

	/**
	 * 转换popAbnormal为JQ对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public PopAbnormalOrderVo convPopAbnormalVo(PopAbnormal popAbnormal) {
		if (popAbnormal == null) {
			this.logger.info("转换popAbnormal为JQ对象 --> 原始运单数据popAbnormal为空");
			return null;
		}
		PopAbnormalOrderVo vo = new PopAbnormalOrderVo();
		vo.setSerialNumber(popAbnormal.getSerialNumber());
		vo.setWayBill(popAbnormal.getWaybillCode());
		vo.setOrderId(popAbnormal.getOrderCode());
		vo.setVenderId(popAbnormal.getPopSupNo());
		vo.setCurrentNumber(String.valueOf(popAbnormal.getCurrentNum()));
		vo.setActualNumber(String.valueOf(popAbnormal.getActualNum()));
		vo.setType(String.valueOf(popAbnormal.getAbnormalType()));
		vo.setMemo(popAbnormal.getMemo());
		return vo;
	}

	/**
	 * 转换popAbnormal为更新运单对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public List<WaybillPOPDto> convPopList(PopAbnormal popAbnormal) {
		if (popAbnormal == null) {
			this.logger.info("转换popAbnormal为更新运单对象 --> 原始运单数据popAbnormal为空");
			return null;
		}
		List<WaybillPOPDto> waybillPOPDtoList = new ArrayList<WaybillPOPDto>();
		WaybillPOPDto dto = new WaybillPOPDto();
		dto.setConsignerId(Integer.valueOf(popAbnormal.getPopSupNo()));
		dto.setConsigner(popAbnormal.getPopSupName());
		dto.setWaybillCode(popAbnormal.getWaybillCode());
		dto.setGoodNumber(popAbnormal.getConfirmNum());
		waybillPOPDtoList.add(dto);
		return waybillPOPDtoList;
	}
	
	/**
	 * 转换popAbnormal为更新运单对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public List<PopOrderDto> convPopOrderList(PopAbnormal popAbnormal) {
		if (popAbnormal == null) {
			this.logger.info("转换popAbnormal为更新运单对象 --> 原始运单数据popAbnormal为空");
			return null;
		}
		List<PopOrderDto> popOrderDtos = new ArrayList<PopOrderDto>();
		PopOrderDto dto = new PopOrderDto();
		dto.setBusiId(Integer.valueOf(popAbnormal.getPopSupNo()));
		dto.setBusiName(popAbnormal.getPopSupName());
		dto.setOrderId(popAbnormal.getWaybillCode());
		dto.setPackNum(popAbnormal.getConfirmNum());
		popOrderDtos.add(dto);
		return popOrderDtos;
	}

	/**
	 * 转换 popAbnormal 为验货对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public Inspection convToInsepection(PopAbnormal popAbnormal) {
		if (popAbnormal == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| popAbnormal.getCreateSiteCode() == null
				|| popAbnormal.getCreateSiteCode().equals(0)
				|| !BusinessHelper
						.checkIntNumRange(popAbnormal.getConfirmNum())) {
			this.logger
					.info("转换popAbnormal 为 Inspection对象 --> 原始数据popAbnormal为空或必要参数有误");
			return null;
		}
		Inspection inspection = new Inspection();
		inspection.setCreateSiteCode(popAbnormal.getCreateSiteCode());
		inspection.setWaybillCode(popAbnormal.getWaybillCode());
		inspection.setQuantity(popAbnormal.getConfirmNum());
		return inspection;
	}

	/**
	 * 转换 popAbnormal 为打印对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public PopPrint convToPopPrint(PopAbnormal popAbnormal) {
		if (popAbnormal == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| !BusinessHelper
						.checkIntNumRange(popAbnormal.getConfirmNum())) {
			this.logger
					.info("转换popAbnormal 为打印对象 --> 原始数据popAbnormal为空或必要参数有误");
			return null;
		}
		PopPrint popPrint = new PopPrint();
		popPrint.setWaybillCode(popAbnormal.getWaybillCode());
		popPrint.setQuantity(popAbnormal.getConfirmNum());
		return popPrint;
	}
}
