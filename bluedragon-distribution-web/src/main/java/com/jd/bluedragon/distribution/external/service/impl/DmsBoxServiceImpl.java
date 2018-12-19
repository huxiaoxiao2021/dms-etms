package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.external.service.DmsBoxService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
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
    public BoxResponse get(String boxCode) {
        return boxResource.get(boxCode);
    }

}
