package com.jd.bluedragon.distribution.base.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.basic.ws.BasicSecondaryWS;

/**
 * Created by xumei3 on 2017/5/31.
 */

@Service("provinceAndCityService")
public class ProvinceAndCityServiceImpl implements ProvinceAndCityService {
    private Logger logger = LoggerFactory.getLogger(ProvinceAndCityServiceImpl.class);

    @Autowired
    private BasicSecondaryWS basicSecondaryWS;

    /**
     * 根据省的ID调用基础资料接口获取所属城市信息，并缓存到Redis中
     * @param provinceId 省ID
     * @return
     */
    @Cache(key = "ProvinceAndCityServiceImpl.getCityByProvince@args0",memoryEnable = true,
            memoryExpiredTime = 10 * 60 * 1000,redisEnable = true,redisExpiredTime = 30 * 60 * 1000)
    public List<ProvinceAndCity> getCityByProvince(Integer provinceId){
        List<ProvinceAndCity> cityList = new ArrayList<ProvinceAndCity>();

        try {
            List<Assort> assortList = basicSecondaryWS.getAssortByFid(provinceId);
            if(null != assortList && assortList.size() > 0){
                for(Assort assort : assortList){
                    cityList.add(new ProvinceAndCity(assort.getAssId().toString(),assort.getAssDis()));
                }
            }else{
                logger.info("ProvinceAndCityServiceImpl.getCityByProvince根据省ID："
                        +provinceId+"调用基础资料接口获取的城市信息为空");
            }

        }catch (Exception e){
            logger.error("ProvinceAndCityServiceImpl.getCityByProvince根据省ID调用基础资料接口获取城市错误，错误信息为："+e.getMessage(),e);
        }

        return cityList;
    }

    /**
     * 批量获取城市信息
     * @param provinceIdList 省ID列表
     * @return
     */
    public List<ProvinceAndCity> getCityByProvince(List<Integer> provinceIdList){
        List<ProvinceAndCity> cityList = new ArrayList<ProvinceAndCity>();

        for(Integer provinceId: provinceIdList){
            cityList.addAll(getCityByProvince(provinceId));
        }
        return cityList;
    }

    /**
     * 根据大区ID获取所属省信息
     * @param orgId 大区ID
     * @return
     */
    public List<ProvinceNode> getProvinceByArea(Integer orgId){
        return AreaHelper.getProvincesByAreaId(orgId);
    }

    /**
     * 调用基础资料接口获取对应的省/市的信息
     * @param id 省/市 id
     * @return
     */
    public Assort getAssortById(Integer id){
        return basicSecondaryWS.getAssortById(id);
    }
}
