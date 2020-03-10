package com.jd.bluedragon.distribution.systemLog.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.dao.GoddessDao;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 *
 * Created by wangtingwei on 2017/2/17.
 */
@Service("goddessService")
@Deprecated
public class GoddessServiceImpl implements GoddessService {

    @Resource(name = "goddessDao")
    private GoddessDao goddessDao;

    @Override
    @Deprecated
    public void save(Goddess domain) {
        if(null!=domain&& StringUtils.isNotBlank(domain.getKey())){
            goddessDao.batchInsert(domain);
        }
    }

    @Override
    public Pager<List<Goddess>> query(Pager<String> pager) {
        return goddessDao.getSplitPage(pager);
    }
}
