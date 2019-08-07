package com.jd.bluedragon.core.base;

import com.jd.spm.bwcompany.response.BwCompanyInfo;

import java.util.List;

/**
 * @author lixin39
 * @Description B网商家查询接口
 * @ClassName SpmBWCompanyManager
 * @date 2019/8/7
 */
public interface SpmBWCompanyManager {

    /**
     * 根据操作用户pin码值获取商家信息
     *
     * @param pin
     * @return
     */
    List<BwCompanyInfo> getCompanyListByPin(String pin);

}
