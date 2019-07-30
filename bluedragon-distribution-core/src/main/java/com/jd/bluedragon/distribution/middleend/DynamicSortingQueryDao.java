package com.jd.bluedragon.distribution.middleend;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class DynamicSortingQueryDao implements ISortingDao{
    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private SortingDao sortingDao;

    @Autowired
    private MiddleEndSortingDao middleEndSortingDao;

    @Autowired
    private FailoverSortingDao failoverSortingDao;


    public ISortingDao selectDao(Integer createSiteCode) {
        String sortingQueryMode = uccPropertyConfiguration.getSortingQueryMode();
        if("DMS".equals(sortingQueryMode)){
            return sortingDao;
        }else if("MIDDLEEND".equals(sortingQueryMode)){
            return middleEndSortingDao;
        }else if("FAILOVER".equals(sortingQueryMode)){
            //配置列表里有

            return sortingDao;
        }
        return  sortingDao;
    }
    public List<Sorting> findByBoxCode(Sorting sorting) {
        return selectDao(sorting.getCreateSiteCode()).findByBoxCode(sorting);
    }


    /**
     * 根据包裹号判断是否存在分拣记录
     *
     * @param sorting
     * @return
     */
    public Boolean existSortingByPackageCode(Sorting sorting) {
        return selectDao(sorting.getCreateSiteCode()).existSortingByPackageCode(sorting);
    }


    /**
     * 获取分拣数量
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Integer findPackCount(Integer createSiteCode, String boxCode) {
        return selectDao(createSiteCode).findPackCount(createSiteCode,boxCode);
    }

    /**
     * 根据箱号和始发站点查询分拣明细
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Sorting findBoxDescSite(Integer createSiteCode, String boxCode) {
        return selectDao(createSiteCode).findBoxDescSite(createSiteCode,boxCode);
    }

    /**
     * 查询分拣明细
     * select box_code, package_code, waybill_code, create_time
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findBoxPackList(Sorting sorting) {
        return selectDao(sorting.getCreateSiteCode()).findBoxPackList(sorting);
    }

    /**
     * 根据运单号或包裹号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode(Sorting sorting) {
        return selectDao(sorting.getCreateSiteCode()).queryByCode(sorting);
    }

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sortingType条件
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode2(Sorting sorting){
        return selectDao(sorting.getCreateSiteCode()).queryByCode2(sorting);
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBsendCode(Sorting sorting){
        return selectDao(sorting.getCreateSiteCode()).findByBsendCode(sorting);
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByPackageCode(Sorting sorting){
        return selectDao(sorting.getCreateSiteCode()).findByBsendCode(sorting);
    }

    /**
     * 根据箱号，当前站点查询有限的分拣记录
     *
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    public List<Sorting> findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum){
        return selectDao(createSiteCode).findByBoxCodeAndFetchNum(boxCode,createSiteCode,fetchNum);
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting){
        return selectDao(sorting.getCreateSiteCode()).findByWaybillCodeOrPackageCode(sorting);
    }

    /**
     * 分页查询分拣记录
     */
    public List<Sorting> findPageSorting(Map<String, Object> params){
        return selectDao((Integer)params.get("createSiteCode")).findPageSorting(params);
    }

    public SortingDao getSortingDao() {
        return sortingDao;
    }

    public void setSortingDao(SortingDao sortingDao) {
        this.sortingDao = sortingDao;
    }

    public MiddleEndSortingDao getMiddleEndSortingDao() {
        return middleEndSortingDao;
    }

    public void setMiddleEndSortingDao(MiddleEndSortingDao middleEndSortingDao) {
        this.middleEndSortingDao = middleEndSortingDao;
    }

    public FailoverSortingDao getFailoverSortingDao() {
        return failoverSortingDao;
    }

    public void setFailoverSortingDao(FailoverSortingDao failoverSortingDao) {
        this.failoverSortingDao = failoverSortingDao;
    }
}

