package com.jd.bluedragon.distribution.jy.service.config;

import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * 拣运功能与工序配置接口
 *
 * @author hujiping
 * @date 2022/4/6 6:04 PM
 */
public interface JyWorkMapFuncConfigService {

    /**
     * 新增数据
     *
     * @param entity
     * @return
     */
    int addWorkMapFunConfig(JyWorkMapFuncConfigEntity entity);

    /**
     * 根据id更新
     *
     * @param entity
     * @return
     */
    int updateById(JyWorkMapFuncConfigEntity entity);

    /**
     * 根据id将数据置为无效
     *
     * @param entity
     * @return
     */
    int deleteById(JyWorkMapFuncConfigEntity entity);

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    PageDto<JyWorkMapFuncConfigEntity> queryPageList(JyWorkMapFuncQuery query);

    /**
     * 根据条件查询
     *
     * @param query
     * @return
     */
    List<JyWorkMapFuncConfigEntity> queryByCondition(JyWorkMapFuncConfigEntity query);
}
