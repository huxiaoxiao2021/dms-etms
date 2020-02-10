package com.jd.bluedragon.external.crossbow.economicNet.manager;

import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeDto;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetEntity;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetErrorRes;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     经济网接口调用类
 *
 * @author wuzuxiang
 * @since 2020/1/17
 **/
public class EconomicNetBusinessManager extends AbstractCrossbowManager<EconomicNetEntity<EconomicNetBoxWeightVolumeDto>, EconomicNetResult<EconomicNetErrorRes>> {

    /**
     * 合作者ID
     */
    private String partnerId;

    /**
     * 消息签名 盐
     */
    private String secretKey;

    /**
     * 将 EconomicNetBoxWeightVolumeDto条件转化为经济网的请求对象
     * @param condition 相应的条件
     * @return
     */
    @Override
    protected Map<String, String> getMyUrlParams(Object condition) {
        String data = JsonHelper.toJson(Collections.singletonList((EconomicNetBoxWeightVolumeDto) condition));
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("partnerId", partnerId);
        urlParams.put("sign", Md5Helper.encode(data + secretKey));
        urlParams.put("data", data);
        return urlParams;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
