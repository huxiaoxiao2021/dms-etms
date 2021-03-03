package com.jd.bluedragon.distribution.reflowPackage.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.request.ReflowPackageQuery;
import com.jd.bluedragon.distribution.reflowPackage.response.ReflowPackageVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

public interface ReflowPackageService {

    /**
     * 新增
     * @param mode
     * @return
     */
    int add(ReflowPackage mode);

    /**
     * 更新
     * @param mode
     * @return
     */
    int update(ReflowPackage mode);

    /**
     * 判断数据是否存在
     * @param mode
     * @return
     */
    boolean isExist(ReflowPackage mode);

    /**
     * 包裹回流扫描提交
     * @param mode
     * @return
     */
    JdCResponse<Boolean> reflowPackageSubmit(ReflowPackage mode);

    /**
     * 根据对象查询符合条件的数据
     * @param mode
     * @return
     */
    List<ReflowPackage> getDataByBean(ReflowPackage mode);

    /**
     * 按条件统计查询条数
     * @param query 查询参数
     * @return 结果总条数
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    Response<Long> selectCount(ReflowPackageQuery query);

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    Response<List<ReflowPackage>> selectList(ReflowPackageQuery query);

    /**
     * 按条件查询分页数据
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    Response<PageDto<ReflowPackageVo>> selectPageList(ReflowPackageQuery query);
}
