package com.jd.bluedragon.distribution.popAbnormal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popAbnormal.dao.PopReceiveAbnormalDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalSendVO;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalDetailService;
import com.jd.bluedragon.distribution.popAbnormal.service.PopReceiveAbnormalService;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.PopOrderDto;
import com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.WaybillService;
import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:43:35
 * 
 *             POP收货差异订单Service New
 */
@Service("popReceiveAbnormalService")
public class PopReceiveAbnormalServiceImpl implements PopReceiveAbnormalService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("dmsAbnormalSendMQ")
    private DefaultJMQProducer dmsAbnormalSendMQ;

    @Qualifier("receiveBdAbnormalSendMQ")
    @Autowired
    private DefaultJMQProducer receiveBdAbnormalSendMQ;

	@Autowired
	private PopReceiveAbnormalDao popReceiveAbnormalDao;

	@Autowired
	private PopAbnormalDetailService popAbnormalDetailService;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private InspectionDao inspectionDao;

	@Autowired
	private PopPrintDao popPrintDao;

	@Override
	public int findTotalCount(Map<String, Object> paramMap) {
		log.info("按条件查询POP差异订单信息，paramMap:{}" , paramMap);
		return popReceiveAbnormalDao.findTotalCount(paramMap);
	}

	@Override
	public List<PopReceiveAbnormal> findList(Map<String, Object> paramMap) {
		log.info("按条件查询POP差异订单信息，paramMap:{}" , paramMap);
		return this.popReceiveAbnormalDao.findList(paramMap);
	}

	@Override
	public PopReceiveAbnormal findByMap(Map<String, String> paramMap) {
		if (paramMap == null || paramMap.isEmpty()) {
			log.info("POP差异订单Service --> findByMap 传入验证参数为空");
			return null;
		}
		return this.popReceiveAbnormalDao.findByMap(paramMap);
	}

	@Override
	public PopReceiveAbnormal findByObj(PopReceiveAbnormal popReceiveAbnormal) {
		if (popReceiveAbnormal == null) {
			log.info("POP差异订单Service --> findByObj 传入验证参数为空");
			return null;
		}
		return this.popReceiveAbnormalDao.findByObj(popReceiveAbnormal);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail) {
		Integer mainType = popReceiveAbnormal.getMainType();
		Integer subType = popReceiveAbnormal.getSubType();
		if ((mainType.equals(2) && (subType != null && (subType.equals(201)
				|| subType.equals(202) || subType.equals(203))))
				|| mainType.equals(3)) {
			popReceiveAbnormal.setAbnormalStatus(PopReceiveAbnormal.IS_FINISH);
		} else {
			popReceiveAbnormal.setAbnormalStatus(PopReceiveAbnormal.IS_SUBMIT);
		}

		int result = this.popReceiveAbnormalDao.add(
				PopReceiveAbnormalDao.NAME_SPACE, popReceiveAbnormal);
		if (result == Constants.RESULT_SUCCESS) {
			popAbnormalDetail.setAbnormalId(popReceiveAbnormal.getAbnormalId());
			popAbnormalDetail.setOperatorTime(DateHelper.getSeverTime(null));
			this.initFingerPrint(popReceiveAbnormal, popAbnormalDetail);
			PopAbnormalDetail tempAbnormalDetail = this.popAbnormalDetailService
					.findByObj(popAbnormalDetail);
			if (tempAbnormalDetail == null) {
				this.popAbnormalDetailService.add(popAbnormalDetail);
			}
		} else {
			this.log.info("popReceiveAbnormalService add 主表插入不成功，直接返回");
			return Constants.RESULT_FAIL;
		}

		if (subType != null && (subType.equals(304) || subType.equals(307))) {
			// 不回传给POP信息
			return Constants.RESULT_SUCCESS;
		}

//		if (mainType != null && mainType.equals(4)) {
//			// 订单未发货的去掉 attr1 的值
//			popReceiveAbnormal.setAttr1(null);
//			String remark = popAbnormalDetail.getRemark();
//			if (StringUtils.isNotBlank(remark)) {
//				popAbnormalDetail.setRemark(remark.substring(remark
//						.indexOf(">") + 1));
//			}
//		}
		//存在运单为空的时候，订单类型为null走else
		//之前逻辑POP组会收到多余类型的订单信息无法处理
		//添加类型判断
		if (Waybill.isOrderTypeBWaybill(popReceiveAbnormal.getWaybillType())) {
			this.pushMqToReceive(popReceiveAbnormal, popAbnormalDetail,
					PopAbnormalSendVO.FIRST_SEND);
			//SOPL LBP订单
		} else if (Waybill.isPopType(popReceiveAbnormal.getWaybillType())){
			this.pushMqToPop(popReceiveAbnormal, popAbnormalDetail,
					PopAbnormalSendVO.FIRST_SEND);
		}else{
			log.warn("POP差异订单处理，订单的类型不符合:{}",JsonHelper.toJson(popReceiveAbnormal));
		}

		return Constants.RESULT_SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int update(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail, Boolean isSendPop) {
		int result = this.popReceiveAbnormalDao.updateById(popReceiveAbnormal);
		if (result == Constants.RESULT_SUCCESS) {
			if (popAbnormalDetail.getOperatorTime() == null) {
				popAbnormalDetail
						.setOperatorTime(DateHelper.getSeverTime(null));
			}
			this.initFingerPrint(popReceiveAbnormal, popAbnormalDetail);
			PopAbnormalDetail tempAbnormalDetail = this.popAbnormalDetailService
					.findByObj(popAbnormalDetail);
			if (tempAbnormalDetail == null) {
				this.popAbnormalDetailService.add(popAbnormalDetail);
			}
		} else {
			this.log.info("popReceiveAbnormalService update 主表无记录，直接返回");
			return Constants.RESULT_FAIL;
		}

		//存在运单为空的时候，订单类型为null走else
		//之前逻辑POP组会收到多余类型的订单信息无法处理
		//添加类型判断
		if (isSendPop) {
			this.log.info("popReceiveAbnormalService --> 第二次给POP或B商家发送信息");
			if (Waybill.isOrderTypeBWaybill(popReceiveAbnormal.getWaybillType())) {
				this.pushMqToReceive(popReceiveAbnormal, popAbnormalDetail, PopAbnormalSendVO.SECOND_SEND);
			} else if (Waybill.isPopType(popReceiveAbnormal.getWaybillType())){
				this.pushMqToPop(popReceiveAbnormal, popAbnormalDetail,
						PopAbnormalSendVO.SECOND_SEND);
			}else{
				log.warn("POP差异订单处理，订单的类型不符合:{}"+JsonHelper.toJson(popReceiveAbnormal));
			}
		} else {
			if (popReceiveAbnormal.getMainType().equals(5)
					|| (popReceiveAbnormal.getMainType().equals(4) && popReceiveAbnormal
							.getSubType().equals(402))) {
				// 根据ID查询POP商家信息
				popReceiveAbnormal = this.popReceiveAbnormalDao
						.findByObj(popReceiveAbnormal);

				// 更新运单包裹数量,更新成功后 flag 为 1
				long startTime = System.currentTimeMillis();
				com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill.BaseEntity baseEntity = this.waybillService
						.sendPopOrders(convPopOrderList(popReceiveAbnormal));
				long endTime = System.currentTimeMillis();

				this.log.info("update，更新商家确认时间，更新运单包裹数量 结束参数：运单号【{}】，调用时间【{}】",popReceiveAbnormal.getWaybillCode(), (endTime - startTime));

				if (baseEntity != null
						&& Constants.RESULT_SUCCESS == baseEntity
								.getResultCode()) {
					Map<String, Boolean> resultMap = JsonHelper.fromJson(
							(String) baseEntity.getData(), Map.class);
					if (resultMap != null
							&& resultMap.get(popReceiveAbnormal
									.getWaybillCode())) {
						this.log.info("update，更新商家确认时间，更新运单包裹数量 成功，ID：{}",popReceiveAbnormal.getAbnormalId());
					}
				}

				// 更新验货表包裹数量
				int resultInt = this.inspectionDao
						.updatePop(convToInsepection(popReceiveAbnormal));
				if (resultInt > 0) {
					this.log.info("update，更新商家确认时间，更新验货表包裹数量 成功，ID：{}",popReceiveAbnormal.getAbnormalId());
				}

				// 更新包裹标签打印表数据
				resultInt = this.popPrintDao
						.updateByWaybillCode(convToPopPrint(popReceiveAbnormal));
				if (resultInt > 0) {
					this.log.info("update，更新商家确认时间，更新打印表包裹数量、打印次数 成功，ID：{}",popReceiveAbnormal.getAbnormalId());
				}
			}
		}

		return Constants.RESULT_SUCCESS;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PopReceiveAbnormal getWaybillByWaybillCode(String waybillCode) {
		PopReceiveAbnormal popReceiveAbnormal = null;
		try {
			// 调用运单接口
			this.log.info("调用运单接口, 订单号为：{} " , waybillCode);
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(true);
			wChoice.setQueryWaybillE(true);
			// wChoice.setQueryWaybillM(true);
			BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager
					.getDataByChoice(waybillCode, wChoice);
			if ((baseEntity != null) && (baseEntity.getData() != null)) {
				popReceiveAbnormal = this.convWaybill(baseEntity.getData());
				if (popReceiveAbnormal == null) {
					// 无数据
					this.log.info("调用运单接口, 订单号为： {} 调用运单WSS数据为空", waybillCode);
				}
			}
		} catch (Exception e) {
			this.log.error("PopReceiveAbnormalServiceImpl --> getWaybillByWaybillCode, 调用运单接口异常：{}",waybillCode,e);
			return null;
		}
		return popReceiveAbnormal;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int delete(Long abnormalId) {
		if (abnormalId == null || abnormalId <= 0) {
			return 0;
		}
		return this.popReceiveAbnormalDao.delete(abnormalId);
	}

	private void pushMqToPop(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail, Integer retType) {

		PopAbnormalSendVO popAbnormalSend = new PopAbnormalSendVO();
		popAbnormalSend.setSerialNumber(popReceiveAbnormal.getAbnormalId());
		if (StringUtils.isNotBlank(popReceiveAbnormal.getOrderCode())) {
			popAbnormalSend.setOrderCode(popReceiveAbnormal.getOrderCode());
		} else {
			popAbnormalSend.setOrderCode(popReceiveAbnormal.getWaybillCode());
		}
		popAbnormalSend.setMainType(popReceiveAbnormal.getMainType());
		popAbnormalSend.setSubType(popReceiveAbnormal.getSubType());
		popAbnormalSend.setComment(popAbnormalDetail.getRemark());
		popAbnormalSend.setOperateTime(DateHelper.formatDate(DateHelper
				.getSeverTime(null), Constants.DATE_TIME_FORMAT));
		popAbnormalSend.setAttr1(popReceiveAbnormal.getAttr1());
		popAbnormalSend.setAttr2(popReceiveAbnormal.getAttr2());
		popAbnormalSend.setRetType(retType);
		
		this.log.info("pushMqToPop.OrderCode={}" , popAbnormalSend.getOrderCode());
		this.dmsAbnormalSendMQ.sendOnFailPersistent(String.valueOf(popAbnormalSend
                .getSerialNumber())
                + "_" + String.valueOf(popAbnormalSend.getRetType()),JsonHelper
                .toJson(popAbnormalSend));
	}

	private void pushMqToReceive(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail, Integer retType) {
		
		PopAbnormalSendVO popAbnormalSend = new PopAbnormalSendVO();
		popAbnormalSend.setSerialNumber(popReceiveAbnormal.getAbnormalId());
		popAbnormalSend.setWaybillCode(popReceiveAbnormal.getWaybillCode());
		popAbnormalSend.setOrderCode(popReceiveAbnormal.getOrderCode());
		popAbnormalSend.setPopSupNo(popReceiveAbnormal.getPopSupNo());
		popAbnormalSend.setPopSupName(popReceiveAbnormal.getPopSupName());
		popAbnormalSend.setMainType(popReceiveAbnormal.getMainType());
		popAbnormalSend.setSubType(popReceiveAbnormal.getSubType());
		popAbnormalSend.setComment(popAbnormalDetail.getRemark());
		popAbnormalSend.setOperateTime(DateHelper.formatDate(DateHelper
				.getSeverTime(null), Constants.DATE_TIME_FORMAT));
		popAbnormalSend.setAttr1(popReceiveAbnormal.getAttr1());
		popAbnormalSend.setAttr2(popReceiveAbnormal.getAttr2());
		popAbnormalSend.setRetType(retType);
		
		
		this.log.info("pushMqToReceive:OrderCode={},WaybillCode={}" ,popAbnormalSend.getOrderCode(),popAbnormalSend.getWaybillCode());

        this.receiveBdAbnormalSendMQ.sendOnFailPersistent(String.valueOf(popAbnormalSend
.getSerialNumber())
+ "_" + String.valueOf(popAbnormalSend.getRetType()), JsonHelper
.toJson(popAbnormalSend));

    }
	
	/**
	 * 转换运单基本信息
	 * 
	 * @param waybillWS
	 * @return
	 */
	public PopReceiveAbnormal convWaybill(BigWaybillDto bigWaybillDto) {
		if (bigWaybillDto == null) {
			this.log.info("转换运单基本信息 --> 原始运单数据bigWaybillDto为空");
			return null;
		}
		com.jd.etms.waybill.domain.Waybill waybillWS = bigWaybillDto
				.getWaybill();
		if (waybillWS == null) {
			this.log.info("转换运单基本信息 --> 原始运单数据集waybillWS为空");
			return null;
		}
		/*
		 * WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
		 * if (manageDomain == null) {
		 * this.log.info("转换运单基本信息 --> 原始运单数据集manageDomain为空"); return null;
		 * }
		 */

		PopReceiveAbnormal popReceiveAbnormal = new PopReceiveAbnormal();
		popReceiveAbnormal.setWaybillCode(waybillWS.getWaybillCode());
		popReceiveAbnormal.setWaybillType(waybillWS.getWaybillType());
		popReceiveAbnormal.setOrderCode(waybillWS.getVendorId());
		if (waybillWS.getConsignerId() != null) {
			popReceiveAbnormal.setPopSupNo(String.valueOf(waybillWS
					.getConsignerId()));
		}
		popReceiveAbnormal.setPopSupName(waybillWS.getConsigner());
		if (Waybill.isOrderTypeBWaybill(waybillWS.getWaybillType())) {
			if (waybillWS.getBusiId() != null) {
				popReceiveAbnormal.setPopSupNo(String.valueOf(waybillWS.getBusiId()));
			} else {
				popReceiveAbnormal.setPopSupNo(null);
			}
			popReceiveAbnormal.setPopSupName(waybillWS.getBusiName());
			
//			popReceiveAbnormal.setOrderCode(popReceiveAbnormal.getWaybillCode());
		}
		if (waybillWS.getGoodNumber() != null) {
			popReceiveAbnormal.setAttr1(String.valueOf(waybillWS
					.getGoodNumber()));
		}
		return popReceiveAbnormal;
	}

	/**
	 * 转换popReceiveAbnormal为更新运单对象
	 * 
	 * @param popReceiveAbnormal
	 * @return
	 */
	public List<PopOrderDto> convPopOrderList(
			PopReceiveAbnormal popReceiveAbnormal) {
		if (popReceiveAbnormal == null) {
			this.log.info("转换popReceiveAbnormal为更新运单对象 --> 原始运单数据popReceiveAbnormal为空");
			return null;
		}
		List<PopOrderDto> popOrderDtos = new ArrayList<PopOrderDto>();
		PopOrderDto dto = new PopOrderDto();
		if (StringUtils.isNotBlank(popReceiveAbnormal.getPopSupNo())
				&& !"null".equals(popReceiveAbnormal.getPopSupNo())) {
			dto.setBusiId(Integer.valueOf(popReceiveAbnormal.getPopSupNo()));
		}
		if (StringUtils.isNotBlank(popReceiveAbnormal.getPopSupName())
				&& !"null".equals(popReceiveAbnormal.getPopSupName())) {
			dto.setBusiName(popReceiveAbnormal.getPopSupName());
		}
		dto.setOrderId(popReceiveAbnormal.getWaybillCode());
		if (StringUtils.isNotBlank(popReceiveAbnormal.getAttr3())) {
			dto.setPackNum(Integer.valueOf(popReceiveAbnormal.getAttr3()));
		}
		popOrderDtos.add(dto);
		return popOrderDtos;
	}

	/**
	 * 转换 popAbnormal 为验货对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public Inspection convToInsepection(PopReceiveAbnormal popAbnormal) {
		if (popAbnormal == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| StringUtils.isBlank(popAbnormal.getAttr3())
				|| BusinessHelper.checkIntNumNotInRange(Integer
						.parseInt(popAbnormal.getAttr3()))) {
			this.log.info("转换popAbnormal 为 Inspection对象 --> 原始数据popAbnormal为空或必要参数有误");
			return null;
		}
		Inspection inspection = new Inspection();
		inspection.setCreateSiteCode(popAbnormal.getCreateSiteCode());
		inspection.setWaybillCode(popAbnormal.getWaybillCode());
		inspection.setQuantity(Integer.parseInt(popAbnormal.getAttr3()));
		return inspection;
	}

	/**
	 * 转换 popAbnormal 为打印对象
	 * 
	 * @param popAbnormal
	 * @return
	 */
	public PopPrint convToPopPrint(PopReceiveAbnormal popAbnormal) {
		if (popAbnormal == null
				|| StringUtils.isBlank(popAbnormal.getWaybillCode())
				|| StringUtils.isBlank(popAbnormal.getAttr3())
				|| BusinessHelper.checkIntNumNotInRange(Integer
						.parseInt(popAbnormal.getAttr3()))) {
			this.log.info("转换popAbnormal 为打印对象 --> 原始数据popAbnormal为空或必要参数有误");
			return null;
		}
		PopPrint popPrint = new PopPrint();
		popPrint.setWaybillCode(popAbnormal.getWaybillCode());
		popPrint.setQuantity(Integer.parseInt(popAbnormal.getAttr3()));
		popPrint.setPrintCount(0);
		return popPrint;
	}

	private void initFingerPrint(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail) {
		StringBuilder fingerprint = new StringBuilder("");

		fingerprint.append(popReceiveAbnormal.getOrgCode()).append("_").append(
				popReceiveAbnormal.getCreateSiteCode()).append("_").append(
				popReceiveAbnormal.getMainType()).append("_").append(
				popReceiveAbnormal.getSubType()).append("_").append(
				popReceiveAbnormal.getWaybillCode()).append("_").append(
				popReceiveAbnormal.getAbnormalStatus()).append("_").append(
				popReceiveAbnormal.getAttr1()).append("_").append(
				popReceiveAbnormal.getAttr2()).append("_").append(
				popReceiveAbnormal.getAttr3()).append("_").append(
				popReceiveAbnormal.getAttr4()).append("_").append(
				popAbnormalDetail.getAbnormalId()).append("_").append(
				popAbnormalDetail.getOperatorName()).append("_").append(
				popAbnormalDetail.getOperatorName()).append("_").append(
				popAbnormalDetail.getRemark());
		popAbnormalDetail.setFingerPrint(Md5Helper.encode(fingerprint
				.toString()));
	}
}
