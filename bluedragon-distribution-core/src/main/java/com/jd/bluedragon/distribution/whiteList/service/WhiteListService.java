package com.jd.bluedragon.distribution.whiteList.service;

import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.distribution.whitelist.WhiteListCondition;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @author lijie
 * @date 2020/3/11 23:10
 */
public interface WhiteListService {

    /**
     * 按条件查询验货记录
     *
     * */
    public PagerResult<WhiteList> queryByCondition(WhiteListCondition condition);

    /**
     * 保存新增白名单
     *
     * */
    public JdResponse<Boolean> save(WhiteList whiteList);

    /**
     * 根据id删除白名单
     *
     * */
    public int deleteByIds(List<Long> ids);
}
