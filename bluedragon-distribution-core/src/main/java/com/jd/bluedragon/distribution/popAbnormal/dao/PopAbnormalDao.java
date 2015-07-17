package com.jd.bluedragon.distribution.popAbnormal.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:10:00
 *
 * POP差异订单Dao
 */
public class PopAbnormalDao extends BaseDao<PopAbnormal> {

	public static final String NAME_SPACE = PopAbnormalDao.class.getName();
	
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
	public List<PopAbnormal> findList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectList(NAME_SPACE + ".findList", paramMap);
		List<PopAbnormal> popAbnormals = (List<PopAbnormal>)((obj == null) ? null : obj);
		return popAbnormals;
	}
	
	/**
	 * 按条件（运单号、订单号或者流水号）查询POP差异明细
	 * @param paramMap
	 * @return
	 */
	public PopAbnormal checkByMap(Map<String, String> paramMap) {
		Object obj = this.getSqlSession().selectOne(NAME_SPACE + ".checkByMap", paramMap);
		PopAbnormal popAbnormal = (obj == null) ? null : (PopAbnormal)obj;
		return popAbnormal;
	}
	
	/**
	 * 增加POP差异反馈单
	 * @param popAbnormal
	 * @return
	 */
	public int add(PopAbnormal popAbnormal) {
		return this.getSqlSession().insert(NAME_SPACE + ".add", popAbnormal);
	}
	
	/**
	 * 更新商家确认时间
	 * @param popAbnormal
	 * @return
	 */
	public int updateById(PopAbnormal popAbnormal) {
		return this.getSqlSession().update(NAME_SPACE + ".updateById", popAbnormal);
	}
}
