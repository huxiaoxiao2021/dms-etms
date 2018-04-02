package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.ql.basic.domain.Assort;

import java.util.List;

/**
 * Created by xumei3 on 2017/5/31.
 */
public interface ProvinceAndCityService {
    List<ProvinceAndCity> getCityByProvince(Integer provinceId);

    List<ProvinceAndCity> getCityByProvince(List<Integer> provinceId);

    List<ProvinceNode> getProvinceByArea(Integer orgId);

    Assort getAssortById(Integer id);
}
