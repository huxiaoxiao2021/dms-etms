package com.jd.bluedragon.core.base;

import com.github.pagehelper.PageInfo;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.Flow;
import com.jd.ql.shared.services.sorting.api.dto.PageBean;
import com.jd.ql.shared.services.sorting.api.dto.Sorting;
import com.jd.ql.shared.services.sorting.api.dto.SortingDirection;

import java.util.List;

public interface SharedSortingQueryManager {
    /**
     * 根据始发目的和容器号查询理货信息
     *
     * @param containerCode
     * @param flow
     * @param sortingDirection
     * @return
     */
    ApiResult<List<Sorting>> queryByContainerAndFlow(String containerCode, Flow flow, SortingDirection sortingDirection);

    /**
     * 查询包裹是否已理货
     *
     * @param operateSiteId
     * @param objectCode
     * @return
     */
    ApiResult<Boolean> existSortingByPackageCode(Integer operateSiteId, String objectCode);

    /**
     * 查询容器内已理货数量
     *
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    ApiResult<Integer> queryContainerPackNum(Integer operateSiteId, String containerCode);

    /**
     * 根据容器查询理货目的地信息（返回一条理货信息）
     *
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    ApiResult<Sorting> queryContainerDescSite(Integer operateSiteId, String containerCode);

    /**
     * 根据运单||包裹分页查询理货信息
     *
     * @param operateSiteId
     * @param waybillCode
     * @param objectCode
     * @param sortingDirection
     * @param pager
     * @return
     */
    ApiResult<PageBean<Sorting>> queryPackList(Integer operateSiteId, String waybillCode, String objectCode, SortingDirection sortingDirection, PageBean pager);

    /**
     * 根据包裹查询理货信息
     *
     * @param operateSiteId
     * @param waybillCode
     * @param objectCode
     * @param sortingDirection
     * @return
     */
    ApiResult<List<Sorting>> queryBySiteCodeAndPackage(Integer operateSiteId, String waybillCode, String objectCode, SortingDirection sortingDirection);

    /**
     * 根据批量发货批次号分页查询所有理货信息
     *
     * @param operateSiteId
     * @param batchCode
     * @param pager
     * @return
     */
    ApiResult<PageBean<Sorting>> queryByBatchCode(Integer operateSiteId, String batchCode, PageBean pager);

    /**
     * 根据容器号查询有限的理货信息
     *
     * @param operateSiteId
     * @param containerCode
     * @param fetchNum
     * @return
     */
    ApiResult<List<Sorting>> queryByContainerCodeAndFetchNum(Integer operateSiteId, String containerCode, Integer fetchNum);

    /**
     * 根据包裹号分页查询理货信息
     *
     * @param operateSiteId
     * @param packageCode
     * @param pager
     * @return
     */
    ApiResult<PageBean<Sorting>> queryByPage(Integer operateSiteId, String packageCode, PageBean pager);

}
