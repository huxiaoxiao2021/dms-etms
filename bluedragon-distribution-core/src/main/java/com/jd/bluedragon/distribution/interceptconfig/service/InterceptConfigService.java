package com.jd.bluedragon.distribution.interceptconfig.service;

import com.jd.bluedragon.distribution.interceptconfig.domain.InterceptConfigInfo;

public interface InterceptConfigService {

    InterceptConfigInfo getInterceptConfigInfoByCode(String code);
}
