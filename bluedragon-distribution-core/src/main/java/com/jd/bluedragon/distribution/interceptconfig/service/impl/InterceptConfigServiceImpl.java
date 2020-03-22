package com.jd.bluedragon.distribution.interceptconfig.service.impl;

import com.jd.bluedragon.distribution.interceptconfig.dao.InterceptConfigInfoDao;
import com.jd.bluedragon.distribution.interceptconfig.domain.InterceptConfigInfo;
import com.jd.bluedragon.distribution.interceptconfig.service.InterceptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("interceptConfigService")
public class InterceptConfigServiceImpl implements InterceptConfigService {

    @Autowired
    private InterceptConfigInfoDao interceptConfigInfoDao;

    @Override
    public InterceptConfigInfo getInterceptConfigInfoByCode(String code) {
        return interceptConfigInfoDao.queryByCode(code);
    }
}
