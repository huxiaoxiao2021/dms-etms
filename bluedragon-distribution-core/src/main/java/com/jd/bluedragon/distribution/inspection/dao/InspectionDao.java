package com.jd.bluedragon.distribution.inspection.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;

public class InspectionDao extends BaseDao<Inspection>{

	private final static Logger logger = Logger.getLogger(InspectionDao.class);

	public static final String namespace = InspectionDao.class.getName();
	
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
		Object obj = this.getSqlSession().selectOne(namespace + ".findPopJoinTotalCount", paramMap);
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
		Object obj = this.getSqlSession().selectList(namespace + ".findPopJoinList", paramMap);
		List<Inspection> inspections = (List<Inspection>)((obj == null) ? null : obj);
		return inspections;
	}
	
	/**
	 * 按条件查询POP交接清单总数
	 * @param paramMap
	 * @return
	 */
	public int findBPopJoinTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findBPopJoinTotalCount", paramMap);
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
		Object obj = this.getSqlSession().selectList(namespace + ".findBPopJoinList", paramMap);
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
	
	/**
	 * 根据包裹号查询验货记录pop订单是否收货
	 * @param haveInspection
	 * @return
	 */
	public boolean havePOPInspection(Inspection inspection){
		Object o = this.getSqlSession().selectOne(namespace+".havePOPInspection", inspection);
		return  o==null?false:true;
	}

    /**
     * 按条件查询验货记录
     *
     * */
    public List<Inspection> queryByCondition(Inspection inspection) {
        return this.getSqlSession().selectList(namespace + ".selectSelective", inspection);
    }


    /**
     * 查询符合条件验货记录数量
     *
     * */
    public Integer queryCountByCondition(Inspection inspection) {
        return (Integer) this.getSqlSession().selectOne(namespace + ".selectCountSelective", inspection);
    }
}
