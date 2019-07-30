package com.jd.bluedragon.core.base;

import com.github.pagehelper.PageInfo;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.SortingQueryService;
import com.jd.ql.shared.services.sorting.api.dto.Flow;
import com.jd.ql.shared.services.sorting.api.dto.Sorting;
import com.jd.ql.shared.services.sorting.api.dto.SortingDirection;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SharedSortingQueryManagerImpl implements SharedSortingQueryManager{
    @Autowired
    private SortingQueryService sortingQueryService;
    /**
     * 租户编码
     */
    String tenantCode = "cn";

    /**
     *  /**
     * 根据始发目的和容器号查询理货信息
     * @param containerCode
     * @param flow
     * @param sortingDirection
     * @return
     */
    public ApiResult<List<Sorting>> queryByContainerAndFlow(String containerCode,Flow flow,SortingDirection sortingDirection){
        sortingQueryService.queryByContainerAndFlow(tenantCode,containerCode,flow,sortingDirection);
        return null;
    }

    /**
     * 查询包裹是否已理货
     * @param operateSiteId
     * @param objectCode
     * @return
     */
    public ApiResult<Boolean> existSortingByPackageCode(Integer operateSiteId,String objectCode){
        sortingQueryService.existSortingByPackageCode(tenantCode,operateSiteId,objectCode);
    }

    /**
     * 查询容器内已理货数量
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    public ApiResult<Integer> queryContainerPackNum(Integer operateSiteId,String containerCode){
        sortingQueryService.queryContainerPackNum(tenantCode,operateSiteId,containerCode);
        return null;

    }

    /**
     * 根据容器查询理货目的地信息（返回一条理货信息）
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    public ApiResult<Sorting> queryContainerDescSite(Integer operateSiteId,String containerCode){
        sortingQueryService.queryContainerDescSite(tenantCode,operateSiteId,containerCode);

        return null;
    }

    /**
     * 根据运单||包裹分页查询理货信息
     * @param operateSiteId
     * @param waybillCode
     * @param objectCode
     * @param sortingDirection
     * @param pager
     * @return
     */
    public ApiResult<PageInfo<Sorting>> queryPackList(Integer operateSiteId,String waybillCode,String objectCode,SortingDirection sortingDirection,PageInfo pager){
        sortingQueryService.queryPackList(tenantCode,operateSiteId,waybillCode,objectCode,sortingDirection,pager);
        return null;
    }

    /**
     * 根据包裹查询理货信息
     * @param operateSiteId
     * @param waybillCode
     * @param objectCode
     * @param sortingDirection
     * @return
     */
    public ApiResult<List<SortingObject>> queryBySiteCodeAndPackage(Integer operateSiteId,String waybillCode,String objectCode,SortingDirection sortingDirection){
        sortingQueryService.queryBySiteCodeAndPackage(tenantCode,operateSiteId,waybillCode,objectCode,sortingDirection);
        return null;
    }

    /**
     * 根据批量发货批次号分页查询所有理货信息
     * @param operateSiteId
     * @param batchCode
     * @param pager
     * @return
     */
    public ApiResult<PageInfo<SortingObject>> queryByBatchCode(Integer operateSiteId,String batchCode,PageInfo pager){
        sortingQueryService.queryByBatchCode(tenantCode,operateSiteId,batchCode,pager);
        return null;
    }

    /**
     * 根据容器号查询有限的理货信息
     * @param operateSiteId
     * @param containerCode
     * @param fetchNum
     * @return
     */
    public ApiResult<List<SortingObject>> queryByContainerCodeAndFetchNum(Integer operateSiteId,String containerCode,Integer fetchNum){
        sortingQueryService.queryByContainerCodeAndFetchNum(tenantCode,operateSiteId,containerCode,fetchNum);
        return null;
    }


    /**
     * 根据包裹号分页查询理货信息
     * @param operateSiteId
     * @param packageCode
     * @param pager
     * @return
     */
    public ApiResult<PageInfo<Sorting>> queryByPage(Integer operateSiteId,String packageCode,PageInfo pager){
        sortingQueryService.queryByPage(tenantCode,operateSiteId,packageCode,pager);
        return null;
    }
}
