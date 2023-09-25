package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.basedata.request.GetFlowDirectionQuery;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jdl.basic.api.dto.site.AreaVO;
import com.jdl.basic.api.dto.site.BasicSiteVO;
import com.jdl.basic.api.dto.site.ProvinceAgencyVO;
import com.jdl.basic.api.dto.site.SiteQueryCondition;
import com.jdl.basic.common.utils.Pager;

import java.util.List;

/**
 * 拣运基础资料-站点查询接口包装类
 *
 * @author hujiping
 * @date 2023/5/31 11:10 AM
 */
public interface JyBasicSiteQueryManager {

    /**
     * 查询所有省区
     * 
     * @return
     */
    List<ProvinceAgencyVO> queryAllProvinceAgencyInfo();

    /**
     * 查询所有枢纽
     * 
     * @param provinceAgencyCode
     * @return
     */
    List<AreaVO> queryAllAreaInfo(String provinceAgencyCode);

    /**
     * 根据条件查询站点
     * @param siteQueryCondition
     * @param limit
     * @return
     */
    List<BasicSiteVO> querySiteByConditionFromBasicSite(SiteQueryCondition siteQueryCondition, Integer limit);

    /**
     * 分页查询
     * @param siteQueryPager 分页查询参数
     * @return 分页数据
     */
    Pager<BasicSiteVO> querySitePageByConditionFromBasicSite(Pager<SiteQueryCondition> siteQueryPager);

    /**
     * 获取流向
     *
     * @param request 请求参数
     * @return 返回结果
     */
    Result<com.jd.bluedragon.common.dto.base.request.Pager<BasicSiteVO>> getFlowDirection(com.jd.bluedragon.common.dto.base.request.Pager<GetFlowDirectionQuery> request);
}
