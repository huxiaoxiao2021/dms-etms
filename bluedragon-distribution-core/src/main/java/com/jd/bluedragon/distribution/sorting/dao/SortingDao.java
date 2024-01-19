package com.jd.bluedragon.distribution.sorting.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.middleend.sorting.dao.ISortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortingDao extends BaseDao<Sorting>  implements ISortingDao {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String namespace = SortingDao.class.getName();

    @SuppressWarnings("unchecked")
    public List<Sorting> findSortingPackages(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".findSortingPackages", sorting);
    }

    @SuppressWarnings("unchecked")
    public Boolean existSortingByPackageCode(Sorting sorting) {
    	Object obj =  this.getSqlSession().selectOne(namespace + ".existSortingByPackageCode", sorting);
        if(obj == null){
            return false;
        }
        return  (Integer)obj > 0;
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
    	CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingDao.canCancel2", false, true);
    	Integer count = 0;
    	Long sortingId = null;
    	List<Sorting> updateList = this.getSqlSession().selectList(namespace + ".querySortingForCanCancel2", sorting);
    	if(updateList != null && updateList.size() > 0) {
    		if(updateList.size() > 1) {
    			this.log.warn("sortingServiceImpl.canCancel2:查询到{}条数据,[{}]",updateList.size(),JsonHelper.toJson(sorting));
    		}
    		sortingId = BeanHelper.getLastOperateSortingId(updateList);
            count = this.getSqlSession().update(namespace + ".canCancel2", sorting);
    	}
    	if(sortingId != null && count > 0) {
    		sorting.setId(sortingId);
    	}
    	Profiler.registerInfoEnd(info);
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

    public Integer findPackCount(Integer createSiteCode, String boxCode) {
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

    /**
     * 根据箱号，当前站点查询有限的分拣记录
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    public List<Sorting>  findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum){
        if (boxCode == null || boxCode.isEmpty() || createSiteCode <= 0 || fetchNum <=0)
            return null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("boxCode", boxCode);
        paramMap.put("fetchNum",fetchNum);
        paramMap.put("createSiteCode", createSiteCode);
        return this.getSqlSession().selectList(namespace + ".findByBoxCodeAndFetchNum", paramMap);
    }
    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     * @param sorting 运单号
     * @return
     */
    public List<Sorting>  findByWaybillCodeOrPackageCode(Sorting sorting){
        return this.getSqlSession().selectList(namespace + ".findByWaybillCodeOrPackageCode", sorting);
    }

    /**分页查询分拣记录*/
    public List<Sorting> findPageSorting(Map<String,Object> params){
        return this.getSqlSession().selectList(namespace +".findPageSorting",params);
    }

    public int invaliSortRealtion(Sorting sorting){
        return this.getSqlSession().update(namespace +".invaliSortRealtion",sorting);
    }

    public Long findByPackageCodeAndBoxCode(Sorting sorting){
        return this.getSqlSession().selectOne(namespace +".findByPackageCodeAndBoxCode",sorting);
    }

    public List<Sorting> listSortingByBoxCode(Sorting sorting) {
        return this.getSqlSession().selectList(namespace + ".listSortingByBoxCode", sorting);
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    public List<Sorting> getPagePackageNoByBoxCode(SortingPageRequest request) {
        return this.getSqlSession().selectList(namespace + ".getPagePackageNoByBoxCode", request);
    }
    /**
     * 更新前查询
     * @param sorting
     * @return
     */
	public List<Sorting> querySortingForUpdate(Sorting sorting) {
		return this.getSqlSession().selectList(namespace + ".querySortingForUpdate", sorting);
	}

    /**
     * 查询场地最后一次分拣记录
     *
     * @param sorting
     */
    @Override
    public Sorting findLastSortingByPackageCode(Sorting sorting) {
        return this.getSqlSession().selectOne(namespace + ".findLastSortingByPackageCode", sorting);
    }

    public int batchDelete(List<Sorting> sortingList) {
        return this.getSqlSession().update(namespace +".batchDelete",sortingList);
    }

    public int batchAdd(List<Sorting> sortingList) {
        return this.getSqlSession().insert(namespace +".batchAdd",sortingList);
    }
}
