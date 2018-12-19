package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.external.service.DmsReceiveService;
import com.jd.bluedragon.distribution.rest.receive.ReceiveResource;
import com.jd.bluedragon.distribution.rest.rollcontainer.RollContainerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsReceiveService")
public class DmsReceiveServiceImpl implements DmsReceiveService {

    @Autowired
    @Qualifier("receiveResource")
    private ReceiveResource receiveResource;

    @Autowired
    @Qualifier("rollContainerResource")
    private RollContainerResource rollContainerResource;

    @Override
    public DeparturePrintResponse queryArteryBillInfo(String type, String code, Integer dmsID, String dmsName, String userCode, String userName) {
        return receiveResource.queryArteryBillInfo(type, code, dmsID, dmsName, userCode, userName);
    }

    @Override
    public ContainerRelationResponse getBoxCodeByContainerCode(String containerCode, Integer siteCode) {
        return rollContainerResource.getBoxCodeByContainerCode(containerCode, siteCode);
    }

}
