package com.jd.bluedragon.core.jsf.easyFreezeSite;

import com.jd.bluedragon.common.dto.easyFreeze.EasyFreezeSiteDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/16 18:09
 * @Description:
 */
public interface EasyFreezeSiteManager {

    /**
     * 根据站点编码获取单个站点配置信息
     * @param siteCode
     * @return
     */
    EasyFreezeSiteDto selectOneBysiteCode(Integer siteCode);
}
