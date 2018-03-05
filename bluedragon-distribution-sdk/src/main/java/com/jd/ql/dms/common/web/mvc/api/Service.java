package com.jd.ql.dms.common.web.mvc.api;

import java.util.List;

/**
 * 
 * @ClassName: Service
 * @Description: 基础service接口
 * @author: wuyoude
 * @date: 2017年12月28日 上午9:31:27
 * 
 * @param <E>
 */
public interface Service<E> {
	/**
	 * 批量插入对象
	 * @param datas
	 * @return
	 */
	public boolean batchAdd(List<E> datas);
	/**
	 * 保存对象-不存在则插入，存在则更新
	 * @param e
	 * @return
	 */
	public boolean saveOrUpdate(E e);
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
	 * 查询实体类
	 * @param e
	 * @return
	 */
	public E find(E e);
	/**
	 * 分页查询数据,返回当前实体的分页对象
	 * 
	 * @param paraMap
	 * @param page
	 * @return
	 */
	public PagerResult<E> queryByPagerCondition(PagerCondition pagerCondition);
}
