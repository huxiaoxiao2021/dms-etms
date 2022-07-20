package com.jd.bluedragon.distribution.middleend.sorting.dao;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

public class DynamicSortingQueryDao implements ISortingDao{
    private final Logger log = LoggerFactory.getLogger(DynamicSortingQueryDao.class);

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private SortingDao sortingDao;

    private static final String SYSTEM_CONFIG_KEY_SORTING_QUERY_OPEN = "failover.sorting.query.site.open";
    private static final String SYSTEM_CONFIG_KEY_SORTING_QUERY_CLOSE = "failover.sorting.query.site.close";


    private static final String SORTING_QUERY_MODE_DMS ="DMS";
    private static final String SORTING_QUERY_MODE_MIDDLEEND ="MIDDLEEND";
    private static final String SORTING_QUERY_MODE_FAILOVER ="FAILOVER";


    /**
     * 根据ucc和system_config的配置确定选择哪个dao
     * @param createSiteCode
     * @return
     */
    public ISortingDao selectDao(Integer createSiteCode) {
        return  sortingDao;
    }

    /**
     * 根据箱号查询分拣记录
     *
     * @param sorting
     * @return
     */
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
        return selectDao(sorting.getCreateSiteCode()).findByPackageCode(sorting);
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

    @Override
    public List<Sorting> findPackageCodesByWaybillCode(Sorting sorting) {
        return selectDao(sorting.getCreateSiteCode()).findPackageCodesByWaybillCode(sorting);
    }

    public SortingDao getSortingDao() {
        return sortingDao;
    }

    public void setSortingDao(SortingDao sortingDao) {
        this.sortingDao = sortingDao;
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @Override
    public List<Sorting> getPagePackageNoByBoxCode(SortingPageRequest request) {
        return selectDao(request.getCreateSiteCode()).getPagePackageNoByBoxCode(request);
    }
}

