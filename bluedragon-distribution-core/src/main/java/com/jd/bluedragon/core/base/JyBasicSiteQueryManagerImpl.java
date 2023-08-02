package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.dto.site.AreaVO;
import com.jdl.basic.api.dto.site.BasicSiteVO;
import com.jdl.basic.api.dto.site.ProvinceAgencyVO;
import com.jdl.basic.api.dto.site.SiteQueryCondition;
import com.jdl.basic.api.service.site.SiteQueryService;
import com.jdl.basic.common.utils.Pager;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
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
}
