package com.jd.bluedragon.distribution.inspection.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionCondition;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionInfo;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectionDao extends BaseDao<Inspection>{

	private final static Logger log = LoggerFactory.getLogger(InspectionDao.class);

	public static final String namespace = InspectionDao.class.getName();
	
	/**
	 * 更新POP验货信息
	 * @param inspection
	 * @return
	 * @throws Exception
	 */
	public int updatePop(Inspection inspection) {
		if(inspection == null || StringUtils.isBlank(inspection.getWaybillCode())){
			log.info("更新POP验货 参数为空");
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
	 * @param inspection
	 * @return
	 */
	public boolean haveInspection(Inspection inspection){
		Object o = this.getSqlSession().selectOne(namespace+".haveInspection", inspection);
		return  o==null?false:true;
	}
	
	/**
	 * 根据包裹号查询验货记录pop订单是否收货
	 * @param inspection
	 * @return
	 */
	public boolean havePOPInspection(Inspection inspection){
		Object o = this.getSqlSession().selectOne(namespace+".havePOPInspection", inspection);
		return  o==null?false:true;
	}

	/**
	 * 根据包裹号查询验货记录 是否验货
	 * @param inspection
	 * @return
	 */
	public boolean haveInspectionByPackageCode(Inspection inspection){
		Object o = this.getSqlSession().selectOne(namespace+".haveInspectionByPackageCode", inspection);
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

	/**
	 * 查询符合条件验货记录数量 - 按包裹号去重
	 *
	 * */
	public Integer queryCountByConditionOfSolePackage(Inspection inspection) {
		return (Integer) this.getSqlSession().selectOne(namespace + ".selectCountSelectiveOfSolePackage", inspection);
	}

	/**
	 * 根据运单号，查询所有包裹号
	 * @param inspectionQuery inspectionQuery
	 * @return
	 */
	public List<Inspection>  findPackageBoxCodesByWaybillCode(Inspection inspectionQuery){
		return this.getSqlSession().selectList(namespace + ".findPackageBoxCodesByWaybillCode", inspectionQuery);

	}
	/**
	 * 查某些运单的验货时间
	 * @param siteCode
	 * @param waybillCodeList
	 * @return
	 */
	public List<Inspection> findOperateTimeByWaybillCodes(Integer siteCode, List<String> waybillCodeList) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("createSiteCode", siteCode);
		param.put("waybillCodes", waybillCodeList);
		return super.getSqlSession().selectList(namespace + ".findOperateTimeByWaybillCodes", param);
	}

	/**分页查询验货记录*/
	public List<Inspection> findPageInspection(Map<String,Object> params){
		return this.getSqlSession().selectList(namespace + ".findPageInspection",params);
	}

	@JProfiler(jKey = "DMSWEB.InspectionDao.getWaybillNoCollectionInfo", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public List<WaybillNoCollectionInfo> getWaybillNoCollectionInfo(Integer createSiteCode, List<String> waybillCodeList, int dayCount) {
		if (createSiteCode == null || waybillCodeList == null || waybillCodeList.isEmpty() || dayCount <= 0) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("createSiteCode", createSiteCode);
		params.put("waybillCodeList", waybillCodeList);
		params.put("startTime", DateHelper.newTimeRangeHoursAgo(new Date(), 24 * dayCount));
		return this.getSqlSession().selectList(namespace + ".getWaybillNoCollectionInfo", params);
	}

	@JProfiler(jKey = "DMSWEB.InspectionDao.getScannedInfoPackageNumMoreThanOne", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public List<String> getInspectedPackageNumMoreThanOne(WaybillNoCollectionCondition waybillNoCollectionCondition) {
		if (waybillNoCollectionCondition.getCreateSiteCode() == null || waybillNoCollectionCondition.getWaybillCodeList() == null || waybillNoCollectionCondition.getWaybillCodeList().isEmpty()) {
			return null;
		}
		return this.getSqlSession().selectList(namespace + ".getInspectedPackageNumMoreThanOne", waybillNoCollectionCondition);
	}
}
