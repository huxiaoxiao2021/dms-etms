package com.jd.ql.dms.common.web.mvc.api;

import java.util.List;

/**
 * 
 * @ClassName: Dao
 * @Description: 基础Dao，包含实体类的基本增删改查
 * @author wuyoude
 * @date 2016年9月22日 下午5:28:15
 * 
 * @param <E>
 */
public interface Dao<E> {
	/**
	 * 插入
	 * 
	 * @param e
	 * @return
	 */
	public boolean insert(E e);
	/**
	 * 批量插入
	 * 
	 * @param e
	 * @return
	 */
	public boolean batchInsert(List<E> datas);
	/**
	 * 更新
	 * 
	 * @param e
	 * @return
	 */
	public boolean update(E e);
	/**
	 * 根据id删除对象
	 * @param id
	 * @return
	 */
	public boolean deleteById(Long id);
	/**
	 * 根据主键删除多个对象
	 * @param ids
	 * @return
	 */
	public int deleteByIds(List<Long> ids);
	/**
	 * 根据id查找对象
	 * @param id
	 * @return
	 */
	public E findById(Long id);
	/**
	 * 根据业务唯一主键查询数据
	 * @param e
	 * @return
	 */
	public E findByUniqueKey(E e);
	/**
	 * 分页查询数据,返回数据列表并设置page的total值
	 * @param pagerCondition
	 * @return
	 */
	public PagerResult<E> queryByPagerCondition(PagerCondition pagerCondition);
}
