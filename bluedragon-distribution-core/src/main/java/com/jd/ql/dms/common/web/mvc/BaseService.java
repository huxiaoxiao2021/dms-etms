package com.jd.ql.dms.common.web.mvc;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.Entity;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * @ClassName: BaseService
 * @Description: 基础Service实现
 * @author wuyoude
 * @date 2016年9月22日 下午4:27:45
 * 
 * @param <E>
 */
@SuppressWarnings("all")
public abstract class BaseService<E extends Entity> implements Service<E> {
	/**
	 * 公共logger
	 */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	/**
	 * 获取数据操作实体类
	 * 
	 * @return
	 */
	public abstract Dao<E> getDao();
	/**
	 * 是否包含id参数
	 * @param e
	 * @return
	 */
	protected boolean hasId(E e){
		return e.getId() != null && e.getId() > 0;
	}
	/**
	 * 是否包含唯一键
	 * @param e
	 * @return
	 */
	protected boolean hasUniqueKey(E e){
		return false;
	}
	@Transactional	
	@Override
	public boolean batchAdd(List<E> datas) {
		return this.getDao().batchInsert(datas);
	}
	@Transactional
	@Override
	public boolean saveOrUpdate(E e) {
		E oldData = this.find(e);
		if(oldData != null){
			e.setId(oldData.getId());
			return this.getDao().update(e);
		}else{
			return this.getDao().insert(e);
		}
	}
	@Transactional
	@Override
	public boolean deleteById(Long id) {
		if(id != null){
			return this.getDao().deleteById(id);
		}
		return false;
	}
	@Transactional
	@Override
	public int deleteByIds(List<Long> ids){
		return this.getDao().deleteByIds(ids);
	}
	@Transactional
	@Override
	public E findById(Long id) {
		if(id != null){
			return this.getDao().findById(id);
		}
		return null;
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public E find(E e) {
		if(hasId(e)){
			return this.getDao().findById(e.getId());
		}else if(hasUniqueKey(e)){
			return this.getDao().findByUniqueKey(e);
		}
		return null;
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagerResult<E> queryByPagerCondition(PagerCondition pagerCondition) {
		return this.getDao().queryByPagerCondition(pagerCondition);
	}
}
