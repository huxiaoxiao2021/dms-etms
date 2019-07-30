package com.jd.bluedragon.distribution.middleend;

import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FailoverSortingDao implements ISortingDao{
    @Autowired
    private SortingDao sortingDao;

    @Autowired
    private MiddleEndSortingDao middleEndSortingDao;

    public List<Sorting> findByBoxCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findByBoxCode(sorting);
            if(sortingList == null){
                sortingList = sortingDao.findByBoxCode(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.findByBoxCode(sorting);
        }
        return sortingList;
    }


    /**
     * 根据包裹号判断是否存在分拣记录
     *
     * @param sorting
     * @return
     */
    public Boolean existSortingByPackageCode(Sorting sorting) {
        Boolean result = null;
        try{
            result = middleEndSortingDao.existSortingByPackageCode(sorting);
            if(result == null){
                result = sortingDao.existSortingByPackageCode(sorting);
            }
        }catch (Exception e){
            result = sortingDao.existSortingByPackageCode(sorting);
        }
        return result;
    }


    /**
     * 获取分拣数量
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Integer findPackCount(Integer createSiteCode, String boxCode) {
        Integer result = null;
        try{
            result = middleEndSortingDao.findPackCount(createSiteCode,boxCode);
            if(result == null){
                result = sortingDao.findPackCount(createSiteCode,boxCode);
            }
        }catch (Exception e){
            result = sortingDao.findPackCount(createSiteCode,boxCode);
        }
        return result;
    }

    /**
     * 根据箱号和始发站点查询分拣明细
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Sorting findBoxDescSite(Integer createSiteCode, String boxCode) {
        Sorting sorting = null;
        try{
            sorting = middleEndSortingDao.findBoxDescSite(createSiteCode,boxCode);
            if(sorting == null){
                sorting = sortingDao.findBoxDescSite(createSiteCode,boxCode);
            }
        }catch (Exception e){
            sorting = sortingDao.findBoxDescSite(createSiteCode,boxCode);
        }
        return sorting;
    }

    /**
     * 查询分拣明细
     * select box_code, package_code, waybill_code, create_time
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findBoxPackList(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findBoxPackList(sorting);
            if(sortingList == null){
                sortingList = sortingDao.findBoxPackList(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.findBoxPackList(sorting);
        }
        return sortingList;
    }

    /**
     * 根据运单号或包裹号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.queryByCode(sorting);
            if(sortingList == null){
                sortingList = sortingDao.queryByCode(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.queryByCode(sorting);
        }
        return sortingList;
    }

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sortingType条件
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode2(Sorting sorting){
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.queryByCode2(sorting);
            if(sortingList == null){
                sortingList = sortingDao.queryByCode2(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.queryByCode2(sorting);
        }
        return sortingList;
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBsendCode(Sorting sorting){
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findByBsendCode(sorting);
            if(sortingList == null){
                sortingList = sortingDao.findByBsendCode(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.findByBsendCode(sorting);
        }
        return sortingList;
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByPackageCode(Sorting sorting){
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findByPackageCode(sorting);
            if(sortingList == null){
                sortingList = sortingDao.findByPackageCode(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.findByPackageCode(sorting);
        }
        return sortingList;
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
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findByBoxCodeAndFetchNum(boxCode,createSiteCode,fetchNum );
            if(sortingList == null){
                sortingList = sortingDao.findByBoxCodeAndFetchNum(boxCode,createSiteCode,fetchNum );
            }
        }catch (Exception e){
            sortingList = sortingDao.findByBoxCodeAndFetchNum(boxCode,createSiteCode,fetchNum);
        }
        return sortingList;
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting){
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findByWaybillCodeOrPackageCode(sorting);
            if(sortingList == null){
                sortingList = sortingDao.findByWaybillCodeOrPackageCode(sorting);
            }
        }catch (Exception e){
            sortingList = sortingDao.findByWaybillCodeOrPackageCode(sorting);
        }
        return sortingList;
    }


    /**
     * 分页查询分拣记录
     */
    public List<Sorting> findPageSorting(Map<String, Object> params){
        List<Sorting> sortingList = new ArrayList<>();
        try{
            sortingList = middleEndSortingDao.findPageSorting(params);
            if(sortingList == null){
                sortingList = sortingDao.findPageSorting(params);
            }
        }catch (Exception e){
            sortingList = sortingDao.findPageSorting(params);
        }
        return sortingList;
    }
}
