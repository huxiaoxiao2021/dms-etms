package com.jd.bluedragon.distribution.boxlimit.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

public interface BoxLimitService {

    /**
     *  PDA建箱包裹数 列表查询
     */
    PagerResult<BoxLimitVO> listData(BoxLimitQueryDTO dto);

    /**
     * 导入模板数据
     */
    JdResponse importData(List<BoxLimitTemplateVO> data, LoginUser operator);

    /**
     * PDA建箱包裹数 创建配置
     */
    JdResponse create(BoxLimitDTO dto, LoginUser operator);

    /**
     * PDA建箱包裹数 更新配置
     */
    JdResponse update(BoxLimitDTO dto, LoginUser operator);

    /**
     * 根据ID批量删除
     */
    JdResponse delete(List<Long> ids, String operatorErp);

    /**
     *  查询展示名称
     */
    JdResponse querySiteNameById(Integer siteId);

    /**
     *  根据站点ID 查询 包裹数量限制
     *  未配置 返回 null
     */
    Integer queryLimitNumBySiteId(Integer siteId);

}
