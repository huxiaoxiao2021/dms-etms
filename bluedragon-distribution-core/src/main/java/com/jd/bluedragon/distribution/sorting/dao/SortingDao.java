package com.jd.bluedragon.distribution.sorting.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

public class SortingDao extends BaseDao<Sorting> {

    public static final String namespace = SortingDao.class.getName();

    @SuppressWarnings("unchecked")
    public List<Sorting> findSortingPackages(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".findSortingPackages", sorting);
    }

    @SuppressWarnings("unchecked")
    public int existSortingByPackageCode(Sorting sorting) {
    	Object obj =  this.getSqlSession().selectOne(namespace + ".existSortingByPackageCode", sorting);
    	return (obj == null) ? 0 : (Integer) obj;
    }
    
    @SuppressWarnings("unchecked")
    public List<Sorting> findByBoxCode(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".findByBoxCode", sorting);
    }

    public Boolean canCancel(Sorting sorting) {
        Integer count = this.getSqlSession().update(namespace + ".canCancel", sorting);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean canCancel2(Sorting sorting) {
        Integer count = this.getSqlSession().update(namespace + ".canCancel2", sorting);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean canCancelFuzzy(Sorting sorting) {
    	Integer count = this.getSqlSession().update(namespace + ".canCancelFuzzy", sorting);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
	}
    
    @SuppressWarnings("unchecked")
    public List<Sorting> findOrderDetail(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".findOrderDetail", sorting);
    }
    @SuppressWarnings("unchecked")
    public List<Sorting> findOrder(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".findOrder", sorting);
    }

    public int findPackCount(Integer createSiteCode, String boxCode) {
    	Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("createSiteCode", createSiteCode);
    	paramMap.put("boxCode", boxCode);
    	Object obj = this.getSqlSession().selectOne(namespace + ".findPackCount", paramMap);
    	return (obj == null) ? 0 : (Integer) obj;
    }
    
    public Sorting findBoxDescSite(Integer createSiteCode, String boxCode) {
    	Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("createSiteCode", createSiteCode);
    	paramMap.put("boxCode", boxCode);
    	Object obj = this.getSqlSession().selectOne(namespace + ".findBoxDescSite", paramMap);
    	return (obj == null) ? null : (Sorting) obj;
    }
    
    @SuppressWarnings("unchecked")
	public List<Sorting> findBoxPackList(Sorting sorting) {
    	Object obj = this.getSqlSession().selectList(namespace + ".findBoxPackList", sorting);
    	return (obj == null) ? null : (List<Sorting>) obj;
    }
    
    @SuppressWarnings("unchecked")
	public List<Sorting> queryByCode(Sorting sorting){
    	return this.getSqlSession().selectList(namespace + ".queryByCode", sorting);
    }

    @SuppressWarnings("unchecked")
    public List<Sorting> queryByCode2(Sorting sorting){
        return this.getSqlSession().selectList(namespace + ".queryByCode2", sorting);
    }
    
    @SuppressWarnings("unchecked")
	public List<Sorting> findByBsendCode(Sorting sorting) {
    	Object obj = this.getSqlSession().selectList(namespace + ".findByBsendCode", sorting);
    	return (obj == null) ? null : (List<Sorting>) obj;
    }

    /**
     * 根据运单号，查询所有包裹号
     * @param sorting 运单号
     * @return
     */
    public List<Sorting>  findPackageCodesByWaybillCode(Sorting sorting){
        return this.getSqlSession().selectList(namespace + ".findPackageBoxCodesByWaybillCode", sorting);
    }
    /**
     * 根据包裹号，当前站点查询所有分拣记录
     * @param sorting 运单号
     * @return
     */
    public List<Sorting>  findByPackageCode(Sorting sorting){
        return this.getSqlSession().selectList(namespace + ".findByPackageCode", sorting);
    }
}
