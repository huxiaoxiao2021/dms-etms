package com.jd.bluedragon.distribution.middleend.sorting.dao;

import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FailoverSortingDao implements ISortingDao {
    private final Logger logger = Logger.getLogger(FailoverSortingDao.class);

    @Autowired
    private SortingDao sortingDao;

    @Autowired
    private MiddleEndSortingDao middleEndSortingDao;

    /**
     * 根据箱号查询分拣记录
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBoxCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findByBoxCode(sorting);
        } catch (Exception e) {
            logger.error("中台findByBoxCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByBoxCode接口的返回值为空，调用分拣的接口查询");
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
        try {
            result = middleEndSortingDao.existSortingByPackageCode(sorting);
        } catch (Exception e) {
            logger.error("中台existSortingByPackageCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (result == null) {
            logger.info("调用中台existSortingByPackageCode接口的返回值为空，调用分拣的接口查询");
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
        try {
            result = middleEndSortingDao.findPackCount(createSiteCode, boxCode);
        } catch (Exception e) {
            logger.error("中台findPackCount接口调用异常.createSiteCode:" + createSiteCode + ",boxCode:" + boxCode, e);
        }
        if (result == null) {
            logger.info("调用中台findPackCount接口的返回值为空，调用分拣的接口查询");
            result = sortingDao.findPackCount(createSiteCode, boxCode);
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
        try {
            sorting = middleEndSortingDao.findBoxDescSite(createSiteCode, boxCode);
        } catch (Exception e) {
            logger.error("中台findBoxDescSite接口调用异常.createSiteCode:" + createSiteCode + ",boxCode:" + boxCode, e);
        }
        if (sorting == null) {
            logger.info("调用中台findBoxDescSite接口的返回值为空，调用分拣的接口查询");
            sorting = sortingDao.findBoxDescSite(createSiteCode, boxCode);
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
        try {
            sortingList = middleEndSortingDao.findBoxPackList(sorting);
        } catch (Exception e) {
            logger.error("中台findBoxPackList接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findBoxPackList接口的返回值为空，调用分拣的接口查询");
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
        try {
            sortingList = middleEndSortingDao.queryByCode(sorting);
        } catch (Exception e) {
            logger.error("中台queryByCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台queryByCode接口的返回值为空，调用分拣的接口查询");
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
    public List<Sorting> queryByCode2(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.queryByCode2(sorting);
        } catch (Exception e) {
            logger.error("中台queryByCode2接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台queryByCode2接口的返回值为空，调用分拣的接口查询");
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
    public List<Sorting> findByBsendCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findByBsendCode(sorting);
        } catch (Exception e) {
            logger.error("中台findByBsendCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByBsendCode接口的返回值为空，调用分拣的接口查询");
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
    public List<Sorting> findByPackageCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findByPackageCode(sorting);
        } catch (Exception e) {
            logger.error("中台findByPackageCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByPackageCode接口的返回值为空，调用分拣的接口查询");
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
    public List<Sorting> findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);
        } catch (Exception e) {
            logger.error("中台findByBoxCodeAndFetchNum接口调用异常.boxCode:" + boxCode + ",createSiteCode:" + createSiteCode + "fetchNum:" +fetchNum , e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByBoxCodeAndFetchNum接口的返回值为空，调用分拣的接口查询");
            sortingList = sortingDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);
        }
        return sortingList;
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findByWaybillCodeOrPackageCode(sorting);
        } catch (Exception e) {
            logger.error("中台findByWaybillCodeOrPackageCode接口调用异常.sorting:" + JSON.toJSONString(sorting), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByWaybillCodeOrPackageCode接口的返回值为空，调用分拣的接口查询");
            sortingList = sortingDao.findByWaybillCodeOrPackageCode(sorting);
        }
        return sortingList;
    }


    /**
     * 分页查询分拣记录
     */
    public List<Sorting> findPageSorting(Map<String, Object> params) {
        List<Sorting> sortingList = new ArrayList<>();
        try {
            sortingList = middleEndSortingDao.findPageSorting(params);
        } catch (Exception e) {
            logger.error("中台findByWaybillCodeOrPackageCode接口调用异常.params:" + JSON.toJSONString(params), e);
        }
        if (sortingList == null) {
            logger.info("调用中台findByWaybillCodeOrPackageCode接口的返回值为空，调用分拣的接口查询");
            sortingList = sortingDao.findPageSorting(params);
        }
        return sortingList;
    }
}
