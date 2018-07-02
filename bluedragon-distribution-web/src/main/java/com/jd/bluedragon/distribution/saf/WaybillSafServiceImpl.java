package com.jd.bluedragon.distribution.saf;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;

public class WaybillSafServiceImpl implements WaybillSafService {

    private final Log logger = LogFactory.getLog(this.getClass());
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
        logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---start!");
        if (StringHelper.isEmpty(packageCode)) packageCode = "";
        String url = DMSVER_ADDRESS + "/services/waybills/cancel?packageCode=" + packageCode;

        WaybillSafResponse response = this.template.getForObject(url, WaybillSafResponse.class);
        logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---end!");
        return response;
    }

    @JProfiler(jKey = "DMSWEB.WaybillSafServiceImpl.isCancelPost", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public WaybillSafResponse isCancelPost(PdaOperateRequest pdaOperateRequest) {
        logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancelPost---start!");
        WaybillSafResponse response = RestHelper.jsonPostForEntity(DMSVER_ADDRESS + URL_IS_CANCEL_POST, pdaOperateRequest, new TypeReference<WaybillSafResponse>() {
        });
        logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancelPost---end!");
        return response;
    }

}
