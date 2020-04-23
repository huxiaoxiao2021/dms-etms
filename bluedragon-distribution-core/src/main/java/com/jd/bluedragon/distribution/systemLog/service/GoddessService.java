package com.jd.bluedragon.distribution.systemLog.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;

import java.util.List;

/**
 * 不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 *
 * Created by wangtingwei on 2017/2/16.
 */
@Deprecated
public interface GoddessService {

    /**
     * 保存
     * @param domain
     */
    @Deprecated
    void save(Goddess domain);

    /**
     * 分页查询
     * @param pager
     * @return
     */
    Pager<List<Goddess>> query(Pager<String> pager);
}
