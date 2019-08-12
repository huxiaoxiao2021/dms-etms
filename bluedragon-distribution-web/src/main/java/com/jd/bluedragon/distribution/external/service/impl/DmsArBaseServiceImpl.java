package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.service.DmsArBaseService;
import com.jd.bluedragon.distribution.rest.transport.ArBaseResource;
import com.jd.ql.dms.common.domain.DictionaryInfoModel;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsArBaseService")
public class DmsArBaseServiceImpl implements DmsArBaseService {

    @Autowired
    @Qualifier("arBaseResource")
    private ArBaseResource arBaseResource;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsArBaseServiceImpl.getARCommonDictionaryInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<DictionaryInfoModel> getARCommonDictionaryInfo(String arg) {
        return arBaseResource.getARCommonDictionaryInfo();
    }

}
