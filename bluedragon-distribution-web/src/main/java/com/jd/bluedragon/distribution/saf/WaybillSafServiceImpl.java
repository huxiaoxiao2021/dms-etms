package com.jd.bluedragon.distribution.saf;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class WaybillSafServiceImpl implements WaybillSafService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * DMSVER_ADDRESS,查询取消订单地址
     */
    private final static String DMSVER_ADDRESS = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS");

    private final static String URL_IS_CANCEL_POST = "/services/waybills/post/cancel";

    private final RestTemplate template = new RestTemplate();

    /* (non-Javadoc)
     * @see com.jd.bluedragon.distribution.saf.WaybillSafService#isCancel(java.lang.String)
     */
    @Override
    public WaybillSafResponse isCancel(String packageCode) {
        log.debug("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---start!");
        if (StringHelper.isEmpty(packageCode)) packageCode = "";
        String url = DMSVER_ADDRESS + "/services/waybills/cancel?packageCode=" + packageCode;

        WaybillSafResponse response = this.template.getForObject(url, WaybillSafResponse.class);
        log.debug("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---end!");
        return response;
    }

    @JProfiler(jKey = "DMSWEB.WaybillSafServiceImpl.isCancelPost", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public WaybillSafResponse isCancelPost(PdaOperateRequest pdaOperateRequest) {
        log.debug("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancelPost---start!");
        WaybillSafResponse response = RestHelper.jsonPostForEntity(DMSVER_ADDRESS + URL_IS_CANCEL_POST, pdaOperateRequest, new TypeReference<WaybillSafResponse>() {
        });
        log.debug("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancelPost---end!");
        return response;
    }

}
