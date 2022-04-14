package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.service.DmsBoxService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsBoxService")
public class DmsBoxServiceImpl implements DmsBoxService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("boxResource")
    private BoxResource boxResource;

    @Autowired
    private BoxService boxService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxServiceImpl.get", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoxResponse get(String boxCode) {
        return boxResource.get(boxCode);
    }


    @Override
    public Response<List<String>> generateRecycleBasketCode(int quantity){
        Response<List<String>> response= new Response();
        response.toSucceed();
        try {
            response.setData(boxService.generateRecycleBasketCode(quantity));
        }catch (Exception e){
            logger.error("周转筐生成编码异常:", e);
            response.toError("周转筐生成编码异常");
        }
        return response;
    }

}
