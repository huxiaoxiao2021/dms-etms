package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.unload.JyUnloadCarAggsEntity;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 16:49
 * @Description: 卸车进度汇总 service
 */
public interface JyUnloadCarAggsService {

    int insertOrUpdateJyUnloadCarAggs(JyUnloadCarAggsEntity entity);
}
