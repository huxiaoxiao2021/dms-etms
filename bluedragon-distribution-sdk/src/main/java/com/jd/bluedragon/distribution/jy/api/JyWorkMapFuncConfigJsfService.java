package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigDetailVO;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 拣运功能配置对外jsf服务
 *
 * @author hujiping
 * @date 2022/4/6 6:13 PM
 */
public interface JyWorkMapFuncConfigJsfService {

    /**
     * 新增
     *
     * @param record
     * @return
     */
    Result<Integer> addWorkMapFunConfig(JyWorkMapFuncConfigDetailVO record);

    /**
     * 根据id更新
     *
     * @param entity
     * @return
     */
    Result<Integer> updateById(JyWorkMapFuncConfigDetailVO entity);

    /**
     * 根据id将数据置为无效
     *
     * @param entity
     * @return
     */
    Result<Integer> deleteById(JyWorkMapFuncConfigEntity entity);

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    Result<PageDto<JyWorkMapFuncConfigDetailVO>> queryPageList(JyWorkMapFuncQuery query);
}
