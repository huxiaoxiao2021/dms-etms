package com.jd.bluedragon.distribution.popAbnormal.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:10:00
 *
 * POP差异订单Dao
 */
public class PopReceiveAbnormalDao extends BaseDao<PopReceiveAbnormal> {

	public static final String NAME_SPACE = PopReceiveAbnormalDao.class.getName();
	
	/**
	 * 根据条件获取POP差异订单集合总数量
	 * @param paramMap
	 * @return
	 */
	public int findTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectOne(NAME_SPACE + ".findTotalCount", paramMap);
		int totalCount = (Integer)((obj == null) ? 0 : obj);
		return totalCount;
	}
	
	/**
	 * 根据条件查询POP差异订单处理集合
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PopReceiveAbnormal> findList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectList(NAME_SPACE + ".findList", paramMap);
		List<PopReceiveAbnormal> popReceiveAbnormals = (List<PopReceiveAbnormal>)((obj == null) ? null : obj);
		return popReceiveAbnormals;
	}
	
	/**
	 * 按条件（运单号、订单号）查询POP差异明细
	 * @param paramMap
	 * @return
	 */
	public PopReceiveAbnormal findByMap(Map<String, String> paramMap) {
		Object obj = this.getSqlSession().selectOne(NAME_SPACE + ".findByMap", paramMap);
		PopReceiveAbnormal popReceiveAbnormal = (obj == null) ? null : (PopReceiveAbnormal)obj;
		return popReceiveAbnormal;
	}
	
	/**
	 * 按条件查询POP差异明细
	 * @param popReceiveAbnormal
	 * @return
	 */
	public PopReceiveAbnormal findByObj(PopReceiveAbnormal popReceiveAbnormal) {
		Object obj = this.getSqlSession().selectOne(NAME_SPACE + ".findByObj", popReceiveAbnormal);
		popReceiveAbnormal = (obj == null) ? null : (PopReceiveAbnormal)obj;
		return popReceiveAbnormal;
	}
	
	/**
	 * 更新商家确认时间
	 * @param popAbnormal
	 * @return
	 */
	public int updateById(PopReceiveAbnormal popReceiveAbnormal) {
		return this.getSqlSession().update(NAME_SPACE + ".updateById", popReceiveAbnormal);
	}

	public int delete(Long abnormalId) {
		return this.getSqlSession().update(NAME_SPACE + ".delete", abnormalId);
	}
}
