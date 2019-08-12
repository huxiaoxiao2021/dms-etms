package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.external.service.DmsBoxService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsBoxService")
public class DmsBoxServiceImpl implements DmsBoxService {

    @Autowired
    @Qualifier("boxResource")
    private BoxResource boxResource;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxServiceImpl.get", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoxResponse get(String boxCode) {
        return boxResource.get(boxCode);
    }

}
