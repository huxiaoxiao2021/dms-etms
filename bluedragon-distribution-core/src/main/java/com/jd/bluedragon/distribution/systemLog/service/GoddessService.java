package com.jd.bluedragon.distribution.systemLog.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

import java.util.List;

/**
 * Created by wangtingwei on 2017/2/16.
 */
public interface GoddessService {

    /**
     * 保存
     * @param domain
     */
    void save(Goddess domain);

    /**
     * 分页查询
     * @param pager
     * @return
     */
    Pager<List<Goddess>> query(Pager<String> pager);
}
