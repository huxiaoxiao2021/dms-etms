package com.jd.bluedragon.core.base;

import com.github.pagehelper.PageInfo;
import com.jd.bluedragon.Constants;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.SortingQueryService;
import com.jd.ql.shared.services.sorting.api.dto.Flow;
import com.jd.ql.shared.services.sorting.api.dto.PageBean;
import com.jd.ql.shared.services.sorting.api.dto.Sorting;
import com.jd.ql.shared.services.sorting.api.dto.SortingDirection;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryByContainerAndFlow",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<List<Sorting>> queryByContainerAndFlow(String containerCode,Flow flow,SortingDirection sortingDirection){
        return sortingQueryService.queryByContainerAndFlow(tenantCode,containerCode,flow,sortingDirection);
    }

    /**
     * 查询包裹是否已理货
     * @param operateSiteId
     * @param objectCode
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.existSortingByPackageCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<Boolean> existSortingByPackageCode(Integer operateSiteId,String objectCode){
        return sortingQueryService.existSortingByPackageCode(tenantCode,operateSiteId,objectCode);
    }

    /**
     * 查询容器内已理货数量
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryContainerPackNum",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<Integer> queryContainerPackNum(Integer operateSiteId,String containerCode){
        return sortingQueryService.queryContainerPackNum(tenantCode,operateSiteId,containerCode);
    }

    /**
     * 根据容器查询理货目的地信息（返回一条理货信息）
     * @param operateSiteId
     * @param containerCode
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryContainerDescSite",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<Sorting> queryContainerDescSite(Integer operateSiteId,String containerCode){
        return sortingQueryService.queryContainerDescSite(tenantCode,operateSiteId,containerCode);
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryPackList",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<PageBean<Sorting>> queryPackList(Integer operateSiteId,String waybillCode,String objectCode,SortingDirection sortingDirection,PageBean pager){
        return sortingQueryService.queryPackList(tenantCode,operateSiteId,waybillCode,objectCode,sortingDirection,pager);
    }

    /**
     * 根据包裹查询理货信息
     * @param operateSiteId
     * @param waybillCode
     * @param objectCode
     * @param sortingDirection
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryBySiteCodeAndPackage",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<List<Sorting>> queryBySiteCodeAndPackage(Integer operateSiteId,String waybillCode,String objectCode,SortingDirection sortingDirection){
        return sortingQueryService.queryBySiteCodeAndPackage(tenantCode,operateSiteId,waybillCode,objectCode,sortingDirection);
    }

    /**
     * 根据批量发货批次号分页查询所有理货信息
     * @param operateSiteId
     * @param batchCode
     * @param pager
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryByBatchCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<PageBean<Sorting>> queryByBatchCode(Integer operateSiteId,String batchCode,PageBean pager){
        return sortingQueryService.queryByBatchCode(tenantCode,operateSiteId,batchCode,pager);
    }

    /**
     * 根据容器号查询有限的理货信息
     * @param operateSiteId
     * @param containerCode
     * @param fetchNum
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryByContainerCodeAndFetchNum",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<List<Sorting>> queryByContainerCodeAndFetchNum(Integer operateSiteId,String containerCode,Integer fetchNum){
        return sortingQueryService.queryByContainerCodeAndFetchNum(tenantCode,operateSiteId,containerCode,fetchNum);
    }


    /**
     * 根据包裹号分页查询理货信息
     * @param operateSiteId
     * @param packageCode
     * @param pager
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.SharedSortingQueryManagerImpl.queryByPage",mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<PageBean<Sorting>> queryByPage(Integer operateSiteId,String packageCode,PageBean pager){
        return sortingQueryService.queryByPage(tenantCode,operateSiteId,packageCode,pager);
    }
}
