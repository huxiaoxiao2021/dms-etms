package com.jd.bluedragon.distribution.rest.sorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DynamicSortingQueryDaoResource {
    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;

    @Autowired
    private SortingDao sortingDao;

    /**
     * 根据箱号查询分拣记录
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/findByBoxCode")
    public List<Sorting> findByBoxCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByBoxCode(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.findByBoxCode(sorting);
        return middleResult;
    }


    /**
     * 根据包裹号判断是否存在分拣记录
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/existSortingByPackageCode")
    public Boolean existSortingByPackageCode(Sorting sorting) {
        Boolean dmsResult = sortingDao.existSortingByPackageCode(sorting);
        Boolean middleResult = dynamicSortingQueryDao.existSortingByPackageCode(sorting);
        return middleResult;
    }


    /**
     * 获取分拣数量
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    @GET
    @Path("/sorting/findPackCount")
    public Integer findPackCount(@QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("boxCode") String boxCode) {
        Integer dmsResult = sortingDao.findPackCount(createSiteCode, boxCode);
        Integer middleResult = dynamicSortingQueryDao.findPackCount(createSiteCode, boxCode);
        return middleResult;
    }

    /**
     * 根据箱号和始发站点查询分拣明细
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    @GET
    @Path("/sorting/findBoxDescSite")
    public Sorting findBoxDescSite(@QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("boxCode") String boxCode) {
        Sorting dmsResult = sortingDao.findBoxDescSite(createSiteCode, boxCode);
        Sorting middleResult = dynamicSortingQueryDao.findBoxDescSite(createSiteCode, boxCode);
        return middleResult;
    }

    /**
     * 查询分拣明细
     * select box_code, package_code, waybill_code, create_time
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/findBoxPackList")
    public List<Sorting> findBoxPackList(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findBoxPackList(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.findBoxPackList(sorting);
        return middleResult;
    }

    /**
     * 根据运单号或包裹号查询分拣明细
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/queryByCode")
    public List<Sorting> queryByCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.queryByCode(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.queryByCode(sorting);
        return middleResult;
    }

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sortingType条件
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/queryByCode2")
    public List<Sorting> queryByCode2(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.queryByCode2(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.queryByCode2(sorting);
        return middleResult;
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/findByBsendCode")
    public List<Sorting> findByBsendCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByBsendCode(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.findByBsendCode(sorting);
        return middleResult;
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    @POST
    @Path("/sorting/findByPackageCode")
    public List<Sorting> findByPackageCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByPackageCode(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.findByPackageCode(sorting);
        return middleResult;
    }

    /**
     * 根据箱号，当前站点查询有限的分拣记录
     *
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    @GET
    @Path("/sorting/findByBoxCodeAndFetchNum")
    public List<Sorting> findByBoxCodeAndFetchNum(@QueryParam("boxCode") String boxCode, @QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("fetchNum") int fetchNum) {
        List<Sorting> dmsResult = sortingDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);
        List<Sorting> middleResult = dynamicSortingQueryDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);
        return middleResult;
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    @POST
    @Path("/sorting/findByWaybillCodeOrPackageCode")
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByWaybillCodeOrPackageCode(sorting);
        List<Sorting> middleResult = dynamicSortingQueryDao.findByWaybillCodeOrPackageCode(sorting);
        return middleResult;
    }

    /**
     * 分页查询分拣记录
     */
    @POST
    @Path("/sorting/findPageSorting")
    public List<Sorting> findPageSorting(Map<String, Object> params) {
        List<Sorting> dmsResult = sortingDao.findPageSorting(params);
        List<Sorting> middleResult = dynamicSortingQueryDao.findPageSorting(params);
        return middleResult;
    }
}
