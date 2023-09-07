package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.basedata.request.GetFlowDirectionQuery;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.dto.site.AreaVO;
import com.jdl.basic.api.dto.site.BasicSiteVO;
import com.jdl.basic.api.dto.site.ProvinceAgencyVO;
import com.jdl.basic.api.dto.site.SiteQueryCondition;
import com.jdl.basic.api.service.site.SiteQueryService;
import com.jdl.basic.common.utils.Pager;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拣运基础资料-站点查询接口包装类
 *
 * @author hujiping
 * @date 2023/5/31 11:11 AM
 */
@Service("jyBasicSiteQueryManager")
public class JyBasicSiteQueryManagerImpl implements JyBasicSiteQueryManager {

    private final Logger logger = LoggerFactory.getLogger(JyBasicSiteQueryManagerImpl.class);
    
    @Autowired
    private SiteQueryService siteQueryService;

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private RouterService routerService;

    @Override
    public List<ProvinceAgencyVO> queryAllProvinceAgencyInfo() {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyBasicSiteQueryManager.queryAllProvinceAgencyInfo",
                false, true);
        List<ProvinceAgencyVO> list = Lists.newArrayList();
        try {
            Result<List<ProvinceAgencyVO>> result = siteQueryService.queryAllProvinceAgencyInfo();
            if(result != null && CollectionUtils.isNotEmpty(result.getData())){
                return result.getData();
            }
        }catch (Exception e){
            logger.error("查询拣运所有省区接口异常!", e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return list;
    }

    @Override
    public List<AreaVO> queryAllAreaInfo(String provinceAgencyCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyBasicSiteQueryManager.queryAllAreaInfo",
                false, true);
        List<AreaVO> list = Lists.newArrayList();
        try {
            Result<List<AreaVO>> result = siteQueryService.queryAllAreaInfo(provinceAgencyCode);
            if(result != null && CollectionUtils.isNotEmpty(result.getData())){
                return result.getData();
            }
        }catch (Exception e){
            logger.error("查询拣运所有枢纽接口异常!", e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return list;
    }

    @Override
    public List<BasicSiteVO> querySiteByConditionFromBasicSite(SiteQueryCondition siteQueryCondition, Integer limit) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyBasicSiteQueryManager.querySiteByConditionFromBasicSite",
                false, true);
        List<BasicSiteVO> list = Lists.newArrayList();
        try {
            Result<List<BasicSiteVO>> result = siteQueryService.querySiteByConditionFromBasicSite(siteQueryCondition, limit);
            if(result != null && CollectionUtils.isNotEmpty(result.getData())){
                return result.getData();
            }
        }catch (Exception e){
            logger.error("根据条件:{}查询拣运站点信息异常!", JsonHelper.toJson(siteQueryCondition), e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return list;
    }

    @Override
    public Pager<BasicSiteVO> querySitePageByConditionFromBasicSite(Pager<SiteQueryCondition> siteQueryPager) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyBasicSiteQueryManager.querySitePageByConditionFromBasicSite",
                false, true);
        Pager<BasicSiteVO> pagerResult = new Pager<>();
        pagerResult.setPageNo(siteQueryPager.getPageNo());
        pagerResult.setPageSize(siteQueryPager.getPageSize());
        pagerResult.setTotal(0L);
        pagerResult.setData(Lists.newArrayList());
        try {
            Result<Pager<BasicSiteVO>> result = siteQueryService.querySitePageByConditionFromBasicSite(siteQueryPager);
            if(result != null && result.getData() != null){
                return result.getData();
            }
        }catch (Exception e){
            logger.error("根据条件:{}查询拣运站点信息异常!", JsonHelper.toJson(siteQueryPager), e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return pagerResult;
    }

    /**
     * 获取流向
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-11 14:59:04 周二
     */
    @Override
    public com.jd.dms.workbench.utils.sdk.base.Result<com.jd.bluedragon.common.dto.base.request.Pager<BasicSiteVO>> getFlowDirection(com.jd.bluedragon.common.dto.base.request.Pager<GetFlowDirectionQuery> request) {
        com.jd.dms.workbench.utils.sdk.base.Result<com.jd.bluedragon.common.dto.base.request.Pager<BasicSiteVO>> pagerResult = new com.jd.dms.workbench.utils.sdk.base.Result<>();
        pagerResult.toSuccess();
        com.jd.bluedragon.common.dto.base.request.Pager<BasicSiteVO> basicSiteVOPagers = new com.jd.bluedragon.common.dto.base.request.Pager<>(request.getPageNo(), request.getPageSize(), 0L);
        pagerResult.setData(basicSiteVOPagers);
        GetFlowDirectionQuery getFlowDirectionQuery = JSON.parseObject(JSON.toJSONString(request.getSearchVo()), GetFlowDirectionQuery.class);
        //入参校验
        checkGetFlowDirectionQuery(getFlowDirectionQuery);
        //根据岗位编码获取当前的场地编码
        selectFlowDirection(getFlowDirectionQuery);
        Pager<BasicSiteVO> basicSiteVOPager = this.querySitePageByConditionFromBasicSite(convertStreamlinedBasicSiteQuery(getFlowDirectionQuery.getSearchStr(), request.getPageNo(), request.getPageSize()));
        if (basicSiteVOPager == null) {
            pagerResult.toFail("查询站点信息异常");
            return pagerResult;
        }
        if(ObjectHelper.isNotEmpty(basicSiteVOPager)){
            basicSiteVOPagers.setData(basicSiteVOPager.getData());
            basicSiteVOPagers.setTotal(basicSiteVOPager.getTotal());
            pagerResult.setData(basicSiteVOPagers);
        }
        return pagerResult;
    }

    /**
     * 校验入参
     *
     * @param getFlowDirectionQuery
     */
    private void checkGetFlowDirectionQuery(GetFlowDirectionQuery getFlowDirectionQuery) {
        if (ObjectHelper.isEmpty(getFlowDirectionQuery)) {
            throw new JyBizException("参数错误：请求参数为空！");
        }
        if (CollectionUtils.isEmpty(getFlowDirectionQuery.getSupportQueryType())) {
            throw new JyBizException("参数错误：查询类型为空！");
        }
        if (ObjectHelper.isEmpty(getFlowDirectionQuery.getCurrentOperate()) && ObjectHelper.isEmpty(getFlowDirectionQuery.getCurrentOperate().getSiteCode())) {
            throw new JyBizException("参数错误：操作单位编号为空！");
        }
    }

    /**
     * 查询流向
     *
     * @param getFlowDirectionQuery
     */
    private void selectFlowDirection(GetFlowDirectionQuery getFlowDirectionQuery) {
        if (getFlowDirectionQuery.getSupportQueryType().contains(GetFlowDirectionQuery.SupportQueryTypeEnum.PACKAGE_CODE.getCode())) {
            if (StringHelper.isNotEmpty(getFlowDirectionQuery.getSearchStr()) && WaybillUtil.isPackageCode(getFlowDirectionQuery.getSearchStr())) {
                String waybillCode = WaybillUtil.getWaybillCode(getFlowDirectionQuery.getSearchStr());
                RouteNextDto routeNextDto = RouteNextDto.NONE;
                if (StringUtils.isNotEmpty(waybillCode)) {
                    try {
                        routeNextDto = routerService.matchRouterNextNode(getFlowDirectionQuery.getCurrentOperate().getSiteCode(), waybillCode);
                    } catch (Exception e) {
                        logger.error("调用查询流向接口报错，入参：getFlowDirectionQuery:{},searchStr:{}", JSONObject.toJSONString(getFlowDirectionQuery), getFlowDirectionQuery.getSearchStr(), e);
                        throw new JyBizException("调用查询流向接口报错！");
                    }
                    if (ObjectHelper.isNotEmpty(routeNextDto) && ObjectHelper.isNotEmpty(routeNextDto.getFirstNextSiteId())) {
                        getFlowDirectionQuery.setSearchStr(String.valueOf(routeNextDto.getFirstNextSiteId()));
                    } else {
                        logger.error("该包裹未获取到流向，入参：getFlowDirectionQuery:{},routeNextDto:{}", JSONObject.toJSONString(getFlowDirectionQuery), routeNextDto);
                        throw new JyBizException("该包裹未获取到流向！");
                    }
                }
            }
        }
    }

    /**
     * 入参类型参数转换为Pager<StreamlinedBasicSiteQuery>
     *
     * @param searchStr
     * @return
     */
    private Pager<SiteQueryCondition> convertStreamlinedBasicSiteQuery(String searchStr, Integer pageNo, Integer pageSize) {
        Pager<SiteQueryCondition> streamlinedBasicSiteQueryPager = new Pager<SiteQueryCondition>();
        SiteQueryCondition siteQueryCondition = new SiteQueryCondition();
        siteQueryCondition.setSearchStr(searchStr);
        streamlinedBasicSiteQueryPager.setPageNo(pageNo);
        streamlinedBasicSiteQueryPager.setPageSize(pageSize);
        streamlinedBasicSiteQueryPager.setSearchVo(siteQueryCondition);
        return streamlinedBasicSiteQueryPager;
    }
}
