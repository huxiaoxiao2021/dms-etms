package com.jd.bluedragon.distribution.inspection.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;

public class InspectionDao extends BaseDao<Inspection>{

	private final static Logger logger = Logger.getLogger(InspectionDao.class);

	public static final String namespace = InspectionDao.class.getName();
	

	/**
	 * 根据条件查询一个Inspection对象
	 * @param inspection
	 * @return
	 */
	public Inspection queryForObject(Inspection inspection) throws Exception{
		Object o = this.getSqlSession().selectOne(namespace+".queryForObject", inspection);
		Inspection bean = o==null?null:(Inspection)o;
		return bean;
	}

	
	/**
	 * 根据条数限制
	 * @param limitNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Inspection> selectSelective( Inspection inspection ) throws Exception{
		return this.getSqlSession().selectList(namespace+ ".selectSelective", inspection);
	}

	public int updateByBoxPackageBarcode( String boxCode, String packageBarcode ) throws Exception{
		if( StringUtils.isBlank(boxCode) && StringUtils.isBlank(boxCode) ){
			logger.info("updateByBoxPackageBarcode fail : boxCode or packageBarcode is blank ");
			throw new InspectionException(" boxCode or packageBarcode is null ");
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("boxCode", boxCode);
		map.put("packageBarcode", packageBarcode);
		return this.getSqlSession().update(namespace+".updateByBoxPackageBarcode", map);
	}
	
	/**
	 * 更新POP验货信息
	 * @param inspection
	 * @return
	 * @throws Exception
	 */
	public int updatePop(Inspection inspection) {
		if(inspection == null || StringUtils.isBlank(inspection.getWaybillCode())){
			logger.info("更新POP验货 参数为空");
			return 0;
		}
		return this.getSqlSession().update(namespace + ".updatePop", inspection);
	}
	
	/**
	 * batch update status value of 1 that have been processed   
	 * @param list
	 * @return
	 */
	public int updateStatusBatchByPrimaryKey( List<Inspection> list ) throws Exception{
		if( null==list || list.isEmpty() ){
			logger.info(" updateBatchByPrimaryKey for inspection fail because of parameter list is null");
			throw new InspectionException(" list is empty ");
		}
		return this.getSqlSession().update(namespace+".updateStatusBatchByPrimaryKey", list);
	}

	@SuppressWarnings("unchecked")
	public List<Inspection> queryListByBox(Inspection inspection) {
		return this.getSqlSession().selectList(namespace+".queryListByBox",inspection);
	}
	
	public Integer inspectionCount( Inspection inspection ){
		return (Integer)this.getSqlSession().selectOne(namespace+".inspectionCount", inspection);
	}
	
	public int updateYnByPackage(Inspection inspection){
		return this.getSqlSession().update(namespace+".updateYnByPackage", inspection);
	}


	public int updateYnByPackageFuzzy(Inspection inspection) {
		return this.getSqlSession().update(namespace+".updateYnByPackageFuzzy", inspection);
	}

	/**
	 * 按条件查询POP交接清单总数
	 * @param paramMap
	 * @return
	 */
	public int findPopJoinTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSessionRead().selectOne(namespace + ".findPopJoinTotalCount", paramMap);
		int totalCount = (Integer)((obj == null) ? 0 : obj);
		return totalCount;
	}


	/**
	 * 按条件查询POP交接清单集合
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Inspection> findPopJoinList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSessionRead().selectList(namespace + ".findPopJoinList", paramMap);
		List<Inspection> inspections = (List<Inspection>)((obj == null) ? null : obj);
		return inspections;
	}
	
	/**
	 * 按条件查询POP交接清单总数
	 * @param paramMap
	 * @return
	 */
	public int findBPopJoinTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSessionRead().selectOne(namespace + ".findBPopJoinTotalCount", paramMap);
		int totalCount = (Integer)((obj == null) ? 0 : obj);
		return totalCount;
	}


	/**
	 * 按条件查询POP交接清单集合
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Inspection> findBPopJoinList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSessionRead().selectList(namespace + ".findBPopJoinList", paramMap);
		List<Inspection> inspections = (List<Inspection>)((obj == null) ? null : obj);
		return inspections;
	}

	@SuppressWarnings("unchecked")
	public List<Inspection> findPopByWaybillCodes(List<String> waybillCodes) {
		Object obj = this.getSqlSession().selectList(namespace + ".findPopByWaybillCodes", waybillCodes);
		List<Inspection> inspections = (List<Inspection>)((obj == null) ? null : obj);
		return inspections;
	}
	
	/**
	 * 根据包裹号查询验货记录
	 * @param haveInspection
	 * @return
	 */
	public boolean haveInspection(Inspection inspection){
		Object o = this.getSqlSession().selectOne(namespace+".haveInspection", inspection);
		return  o==null?false:true;
	}
}
