package com.jd.bluedragon.distribution.reflowPackage.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.request.ReflowPackageQuery;
import com.jd.bluedragon.distribution.reflowPackage.response.ReflowPackageVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * 包裹回流服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-28 15:51:16 周日
 */
public interface ReflowPackageJsfService {

    /**
     * 按条件统计查询条数
     * @param query 查询参数
     * @return 结果总条数
     * @author fanggang7
     * @time 2019-12-20 18:41:01 周五
     */
    Response<Long> selectCount(ReflowPackageQuery query);

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2019-12-20 19:21:37 周五
     */
    Response<List<ReflowPackage>> selectList(ReflowPackageQuery query);

    /**
     * 按条件查询分页数据
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2019-12-20 19:21:37 周五
     */
    Response<PageDto<ReflowPackageVo>> selectPageList(ReflowPackageQuery query);
}
