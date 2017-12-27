package com.jd.ql.dms.common.web.mvc.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.Entity;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: BaseDAO
 * @Description: 基础Dao实现
 * @author wuyoude
 * @date 2016年9月22日 下午4:27:45
 * 
 * @param <E>
 */
@SuppressWarnings("all")
public class BaseDao<E extends Entity> implements Dao<E>{
	/**
	 * 公共logger
	 */
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	protected String nameSpace;
	
	protected SqlSession sqlSession;
	/**
	 * 初始化
	 */
	public BaseDao(){
		init();
	}
	/**
	 * 初始化逻辑
	 */
	public void init(){
		//没有设置nameSpace参数时，默认设置为实例
		if(this.nameSpace == null){
			this.nameSpace = this.getClass().getName().replace(".impl", "").replace("Impl", "");
		}
		 
	}
	@Override
	public boolean insert(E e) {
		return sqlSession.insert(this.nameSpace+".insert", e) == 1;
	}

	@Override
	public boolean batchInsert(List<E> datas) {
		if(datas != null && !datas.isEmpty()){
			return sqlSession.insert(this.nameSpace+".batchInsert", datas) >= 1;
		}
		logger.warn(this.nameSpace+".batchInsert unexecuted with empty params");
		return false;
	}

	@Override
	public boolean update(E e) {
		return sqlSession.update(this.nameSpace+".update", e) == 1;
	}
	
	@Override
	public boolean deleteById(Long id) {
		return sqlSession.delete(this.nameSpace+".deleteById", id) == 1;
	}
	
	@Override
	public int deleteByIds(List<Long> ids) {
		return sqlSession.delete(this.nameSpace+".deleteByIds", ids);
	}

	@Override
	public E findById(Long id) {
		return sqlSession.selectOne(this.nameSpace+".findById", id);
	}

	@Override
	public E findByUniqueKey(E e) {
		return sqlSession.selectOne(this.nameSpace+".findByUniqueKey", e);
	}

	@Override
	public PagerResult<E> queryByPagerCondition(PagerCondition pagerCondition) {
		return (PagerResult<E>)this.queryByPagerCondition("queryByPagerCondition", pagerCondition);
	}
	/**
	 * 指定查询sql的id，分页查询数据
	 * @param statementName
	 * @param pagerCondition
	 * @return
	 */
	protected <C,R> PagerResult<R> queryByPagerCondition(String statementName,PagerCondition pagerCondition) {
		PagerResult<R> pagerResult = new PagerResult<R>();
		int total =(Integer)this.sqlSession.selectOne(getNameSpace()+".pageNum_"+statementName, pagerCondition);
		if(total>0){
			pagerResult.setTotal(total);
			pagerResult.setRows((List<R>)this.sqlSession.selectList(getNameSpace()+"."+statementName, pagerCondition));
		}else{
			pagerResult.setRows(new ArrayList<R>());
		}
		return pagerResult;
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	/**
	 * @return the sqlSession
	 */
	public SqlSession getSqlSession() {
		return sqlSession;
	}
	/**
	 * @param sqlSession the sqlSession to set
	 */
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
}