package com.jd.bluedragon.distribution.popAbnormal.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:40:42
 *
 * POP差异订单Service
 */
public interface PopAbnormalService {
	
	/**
	 * 根据员工编号获取员工信息
	 * @param staffId 员工编号
	 */
	public Map<String, Object> getBaseStaffByStaffId(Integer staffId);
	
	/**
	 * 根据条件获取POP差异订单集合总数量
	 * @param paramMap
	 * @return
	 */
	public int findTotalCount(Map<String, Object> paramMap);

	/**
	 * 根据条件查询POP差异订单处理集合
	 * @param paramMap
	 * @return
	 */
	public List<PopAbnormal> findList(Map<String, Object> paramMap);
	
	/**
	 * 根据订单号获取运单信息（直接调用运单接口）
	 * @param orderCode
	 * @return
	 */
	public PopAbnormal getWaybillByOrderCode(String orderCode);
	
	/**
	 * 按条件（运单号、订单号或者流水号）查询POP差异明细
	 * @param waybillCode
	 * @return
	 */
	public PopAbnormal checkByMap(Map<String, String> paramMap);
	
	/**
	 * 增加POP差异反馈单
	 * @param popAbnormal
	 * @return
	 */
	public int add(PopAbnormal popAbnormal);
	
	/**
	 * 更新商家确认时间
	 * @param popAbnormal
	 * @return
	 */
	public int updateById(PopAbnormal popAbnormal);
	
	/**
	 * 更新商家确认时间New
	 * @param popAbnormal
	 * @return
	 */
	public int updatePopPackNum(PopAbnormal popAbnormal);
}
