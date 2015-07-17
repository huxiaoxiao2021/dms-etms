package com.jd.bluedragon.distribution.popAbnormal.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:40:42
 * 
 *             POP收货差异订单Service New
 */
public interface PopReceiveAbnormalService {

	/**
	 * 根据条件获取POP差异订单集合总数量
	 * 
	 * @param paramMap
	 * @return
	 */
	public int findTotalCount(Map<String, Object> paramMap);

	/**
	 * 根据条件查询POP差异订单处理集合
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<PopReceiveAbnormal> findList(Map<String, Object> paramMap);

	/**
	 * 按条件（运单号、订单号）查询POP差异明细
	 * 
	 * @param paramMap
	 * @return
	 */
	public PopReceiveAbnormal findByMap(Map<String, String> paramMap);

	public PopReceiveAbnormal findByObj(PopReceiveAbnormal popReceiveAbnormal);

	/**
	 * 第一次增加POP差异反馈单
	 * 
	 * @param popReceiveAbnormal
	 * @param popAbnormalDetail
	 * @return
	 */
	public int add(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail);

	/**
	 * 根据运单号获取运单信息
	 * 
	 * @param waybillCode
	 * @return
	 */
	public PopReceiveAbnormal getWaybillByWaybillCode(String waybillCode);
	
	/**
	 * 第二次更新主表并插入明细
	 * 
	 * @param popReceiveAbnormal
	 * @param popAbnormalDetail
	 * @return
	 */
	public int update(PopReceiveAbnormal popReceiveAbnormal,
			PopAbnormalDetail popAbnormalDetail, Boolean isSendPop);
	
	/**
	 * 根据主键取消差异订单
	 * 
	 * @param abnormalId
	 * @return
	 */
	public int delete(Long abnormalId);

}
