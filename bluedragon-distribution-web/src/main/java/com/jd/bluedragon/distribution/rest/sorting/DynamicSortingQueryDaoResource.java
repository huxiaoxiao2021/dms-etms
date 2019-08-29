package com.jd.bluedragon.distribution.rest.sorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.middleend.sorting.dao.MiddleEndSortingDao;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DynamicSortingQueryDaoResource {
    @Autowired
    private MiddleEndSortingDao middleEndSortingDao;

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
    public Map<String,List<Sorting>> findByBoxCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByBoxCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findByBoxCode(sorting);
        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }


    /**
     * 根据包裹号判断是否存在分拣记录
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/existSortingByPackageCode")
    public Map<String,Boolean> existSortingByPackageCode(Sorting sorting) {
        Boolean dmsResult = sortingDao.existSortingByPackageCode(sorting);
        Boolean middleResult = middleEndSortingDao.existSortingByPackageCode(sorting);

        Map<String,Boolean> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
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
    public Map<String,Integer> findPackCount(@QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("boxCode") String boxCode) {
        Integer dmsResult = sortingDao.findPackCount(createSiteCode, boxCode);
        Integer middleResult = middleEndSortingDao.findPackCount(createSiteCode, boxCode);

        Map<String,Integer> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
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
    public Map<String,Sorting> findBoxDescSite(@QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("boxCode") String boxCode) {
        Sorting dmsResult = sortingDao.findBoxDescSite(createSiteCode, boxCode);
        Sorting middleResult = middleEndSortingDao.findBoxDescSite(createSiteCode, boxCode);

        Map<String,Sorting> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
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
    public Map<String,List<Sorting>> findBoxPackList(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findBoxPackList(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findBoxPackList(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }

    /**
     * 根据运单号或包裹号查询分拣明细
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/queryByCode")
    public Map<String,List<Sorting>> queryByCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.queryByCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.queryByCode(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
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
    public Map<String,List<Sorting>> queryByCode2(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.queryByCode2(sorting);
        List<Sorting> middleResult = middleEndSortingDao.queryByCode2(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    @POST
    @Path("/sorting/findByBsendCode")
    public Map<String,List<Sorting>> findByBsendCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByBsendCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findByBsendCode(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    @POST
    @Path("/sorting/findByPackageCode")
    public Map<String,List<Sorting>> findByPackageCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByPackageCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findByPackageCode(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
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
    public Map<String,List<Sorting>> findByBoxCodeAndFetchNum(@QueryParam("boxCode") String boxCode, @QueryParam("createSiteCode") Integer createSiteCode, @QueryParam("fetchNum") int fetchNum) {
        List<Sorting> dmsResult = sortingDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);
        List<Sorting> middleResult = middleEndSortingDao.findByBoxCodeAndFetchNum(boxCode, createSiteCode, fetchNum);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    @POST
    @Path("/sorting/findPackageCodesByWaybillCode")
    public  Map<String,List<Sorting>> findPackageCodesByWaybillCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findPackageCodesByWaybillCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findPackageCodesByWaybillCode(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }
    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    @POST
    @Path("/sorting/findByWaybillCodeOrPackageCode")
    public  Map<String,List<Sorting>> findByWaybillCodeOrPackageCode(Sorting sorting) {
        List<Sorting> dmsResult = sortingDao.findByWaybillCodeOrPackageCode(sorting);
        List<Sorting> middleResult = middleEndSortingDao.findByWaybillCodeOrPackageCode(sorting);

        Map<String,List<Sorting>> result = new HashMap<>();
        result.put("dmsQueryResult",dmsResult);
        result.put("middleEndResult",middleResult);

        return result;
    }
}
