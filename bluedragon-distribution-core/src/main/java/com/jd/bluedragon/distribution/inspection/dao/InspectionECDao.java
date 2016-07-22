package com.jd.bluedragon.distribution.inspection.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;

public class InspectionECDao extends BaseDao<InspectionEC>{
	
	public static final String namespace = InspectionECDao.class.getName();
		
	@SuppressWarnings("unchecked")
	public List<InspectionEC> selectSelective(  InspectionEC inspectionEC ) {
		return this.getSqlSession().selectList(namespace+".selectSelective",inspectionEC );
	}
	
	@SuppressWarnings("unchecked")
	public List<InspectionEC> queryByThird(InspectionEC inspectionEC) {
		return this.getSqlSession().selectList(namespace+".queryByThird",inspectionEC );
	}
	
	/**
	 * update status --> 3:超区退回,4:多验退回,5:少验取消
	 * @param inspecgtionEC
	 * @return
	 * @Condition create_site_code & package_barcode & waybill_code 
	 */
	public int updateStatus(InspectionEC inspectionEC) {
		return this.getSqlSession().update(namespace+".updateStatus",inspectionEC);
	}

	public int updateYnByWaybillCode(InspectionEC inspectionEC){
		return this.getSqlSession().update(namespace+".updateYnByWaybillCode",inspectionEC);
	}
	/**
	 * update status & box --->6:多验直接配送
	 * @param inspectionEC
	 * @return
	 */
	public int updateForSorting(InspectionEC inspectionEC) {
		return this.getSqlSession().update(namespace+".updateForSorting",inspectionEC);
	}
	
	public int updateOne(InspectionEC inspectionEC) {
		return this.getSqlSession().update(namespace+".updateOne",inspectionEC);
	}

	public int batchUpdateInspectionECType(Set<InspectionEC> set) throws Exception{
		int result = 0;
		for( InspectionEC inspectionEC:set ){
			result += this.updateInspectionECType(inspectionEC);
		}
		return result;
	}

	public int updateInspectionECType(InspectionEC inspectionEC) throws Exception {
		return this.getSqlSession().update(namespace+".updateInspectionECType",inspectionEC);
	}

	/**
	 * 查询未处理的异常信息
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @param boxCode
	 * @return
	 */
	public int queryExceptionsCore( Integer createSiteCode, Integer receiveSiteCode, String boxCode ){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createSiteCode", createSiteCode);
		params.put("receiveSiteCode",receiveSiteCode);
		params.put("boxCode", boxCode);
		return (Integer) this.getSqlSession().selectOne(namespace+".queryExceptionsCore", params);
	}

	public Integer boxUnInspection(Integer createSiteCode, Integer receiveSiteCode,
			String boxCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createSiteCode", createSiteCode);
		params.put("receiveSiteCode",receiveSiteCode);
		params.put("boxCode", boxCode);
		return (Integer)this.getSqlSession().selectOne(namespace+".boxUnInspection",params);
	}

	public Integer exceptionCountByBox(Integer createSiteCode,
			Integer receiveSiteCode, String boxCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createSiteCode", createSiteCode);
		params.put("receiveSiteCode",receiveSiteCode);
		params.put("boxCode", boxCode);
		return (Integer)this.getSqlSession().selectOne(namespace+".exceptionCountByBox",params);
		
	}

	@SuppressWarnings("unchecked")
	public List<InspectionEC> checkDispose(InspectionEC inspectionEC) {
		return getSqlSession().selectList(namespace+".checkDispose",inspectionEC);
	}

	public Integer inspectionCount(InspectionEC inspectionEC) {
		return (Integer)this.getSqlSession().selectOne(namespace+".inspectionCount",inspectionEC);
	}

	@SuppressWarnings("unchecked")
	public List<InspectionEC> queryThirdByParams(Map<String, Object> paramMap) {
		return this.getSqlSession().selectList(namespace+".queryThirdByParams",paramMap );
	}

	public int totalThirdByParams(Map<String, Object> paramMap) {
		return (Integer)this.getSqlSession().selectOne(namespace + ".totalThirdByParams", paramMap);
	}
	
}