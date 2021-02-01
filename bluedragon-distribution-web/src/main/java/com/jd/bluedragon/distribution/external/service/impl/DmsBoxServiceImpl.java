package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.service.DmsBoxService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private BoxService boxService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxServiceImpl.get", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoxResponse get(String boxCode) {
        return boxResource.get(boxCode);
    }


    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    @Override
    public BoxDto getBoxByBoxCode(String boxCode) {
        Box box = boxService.findBoxByCode(boxCode);
        BoxDto result = new BoxDto();
        BeanUtils.copyProperties(box,result);
        return result;
    }

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    @Override
    public Boolean updateBoxStatus(BoxReq boxReq) {
        return boxService.updateBoxStatus(boxReq);
    }
}
