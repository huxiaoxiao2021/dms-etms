package com.jd.bluedragon.distribution.middleend.sorting.dao;


import com.jd.bluedragon.distribution.sorting.domain.Sorting;

import java.util.List;

public interface ISortingDao {

    /**
     * 根据箱号查询分拣记录
     * @param sorting
     * @return
     */
    List<Sorting> findByBoxCode(Sorting sorting);

    /**
     * 根据包裹号判断是否存在分拣记录
     * @param sorting
     * @return
     */
    Boolean existSortingByPackageCode(Sorting sorting);

    /**
     * 获取分拣数量
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    Integer findPackCount(Integer createSiteCode, String boxCode);

    /**
     * 根据箱号和始发站点查询分拣明细
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    Sorting findBoxDescSite(Integer createSiteCode, String boxCode);

    /**
     * 查询分拣明细
     * select box_code, package_code, waybill_code, create_time
     * @param sorting
     * @return
     */
    List<Sorting> findBoxPackList(Sorting sorting);

    /**
     * 根据运单号或包裹号查询分拣明细
     * @param sorting
     * @return
     */
    List<Sorting> queryByCode(Sorting sorting);

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sendType条件
     * @param sorting
     * @return
     */
    List<Sorting> queryByCode2(Sorting sorting);

    /**
     * 根据批次号查询分拣明细
     * @param sorting
     * @return
     */
    List<Sorting> findByBsendCode(Sorting sorting);

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     * @param sorting 运单号
     * @return
     */
    List<Sorting>  findByPackageCode(Sorting sorting);

    /**
     * 根据箱号，当前站点查询有限的分拣记录
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    List<Sorting>  findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum);
    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     * @param sorting 运单号
     * @return
     */
    List<Sorting>  findByWaybillCodeOrPackageCode(Sorting sorting);
}
