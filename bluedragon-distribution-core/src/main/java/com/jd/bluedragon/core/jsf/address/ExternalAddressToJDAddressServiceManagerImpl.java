package com.jd.bluedragon.core.jsf.address;

import com.alibaba.fastjson.JSON;
import com.jd.addresstranslation.api.address.ExternalAddressToJDAddressService;
import com.jd.addresstranslation.api.base.BaseResponse;
import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.addresstranslation.api.base.ExternalJDAddress;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/1 11:25
 * @Description:
 */
@Service("externalAddressToJDAddressServiceManager")
public class ExternalAddressToJDAddressServiceManagerImpl implements ExternalAddressToJDAddressServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ExternalAddressToJDAddressServiceManagerImpl.class);


    @Autowired
    private ExternalAddressToJDAddressService externalAddressToJDAddressService;

    /**
     * 秘钥
     */
    @Value("${beans.externalAddressToJDAddressServiceManager.getJDDistrict.appKey}")
    private String appKey;



    @Cache(key = "externalAddressToJDAddressServiceManager.getJDDistrict@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = false)
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "ExternalAddressToJDAddressServiceManagerImpl.getJDDistrict",mState={JProEnum.TP,JProEnum.FunctionError})
    public DmsExternalJDAddressResponse getJDDistrict(ExternalAddressRequest request) {

        try{
            BaseResponse<ExternalJDAddress> response = externalAddressToJDAddressService.getJDDistrict(appKey,request);
            if(response.getStatus().equals(200) && response.getResult() != null){
                DmsExternalJDAddressResponse jdAddressResponse = coverToDmsExternalJDAddress(response.getResult());
                return jdAddressResponse;
            }
        }catch (Exception e){
            log.error("git 四级地址解析异常-request-{},error-{}", JSON.toJSONString(request),e.getMessage(),e);
        }
        return null;
    }

    private DmsExternalJDAddressResponse coverToDmsExternalJDAddress(ExternalJDAddress address){
        DmsExternalJDAddressResponse result = new DmsExternalJDAddressResponse();
        result.setProvinceCode(address.getProvinceCode());
        result.setProvinceName(address.getProvinceName());
        result.setCityCode(address.getCityCode());
        result.setCityName(address.getCityName());
        result.setDistrictCode(address.getDistrictCode());
        result.setDistrictName(address.getDistrictName());
        result.setTownCode(address.getTownCode());
        result.setTownName(address.getTownName());
        result.setDetailAddress(address.getDetailAddress());
        result.setFullAddress(address.getFullAddress());
        result.setLatitude(address.getLatitude());
        result.setLongitude(address.getLongitude());
        result.setPrecise(address.getPrecise());
        result.setOrderId(address.getOrderId());
        result.setAdminCode(address.getAdminCode());
        result.setWrongLevel(address.getWrongLevel());
        return result;
    }
}
