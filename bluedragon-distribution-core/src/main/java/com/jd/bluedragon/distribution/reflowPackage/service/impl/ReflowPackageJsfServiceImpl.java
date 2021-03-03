package com.jd.bluedragon.distribution.reflowPackage.service.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.request.ReflowPackageQuery;
import com.jd.bluedragon.distribution.reflowPackage.response.ReflowPackageVo;
import com.jd.bluedragon.distribution.reflowPackage.service.ReflowPackageJsfService;
import com.jd.bluedragon.distribution.reflowPackage.service.ReflowPackageService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 包裹回流扫描报表jsf服务实现层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-28 16:30:50 周日
 */
@Service("reflowPackageJsfService")
public class ReflowPackageJsfServiceImpl implements ReflowPackageJsfService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReflowPackageService reflowPackageService;

    /**
     * 按条件统计查询条数
     *
     * @param query 查询参数
     * @return 结果总条数
     * @author fanggang7
     * @time 2021-02-28 16:30:50 周日
     */
    @Override
    public Response<Long> selectCount(ReflowPackageQuery query) {
        return reflowPackageService.selectCount(query);
    }

    /**
     * 按条件查询列表
     *
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 16:30:50 周日
     */
    @Override
    public Response<List<ReflowPackage>> selectList(ReflowPackageQuery query) {
        return reflowPackageService.selectList(query);
    }

    /**
     * 按条件查询分页数据
     *
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 16:30:50 周日
     */
    @Override
    public Response<PageDto<ReflowPackageVo>> selectPageList(ReflowPackageQuery query) {
        return reflowPackageService.selectPageList(query);
    }
}
