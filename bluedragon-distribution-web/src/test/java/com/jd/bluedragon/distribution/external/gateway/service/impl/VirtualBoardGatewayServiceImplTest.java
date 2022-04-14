package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.board.request.AutoBoardCompleteRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadScanDetailDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanRecordDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.external.gateway.service.LoadAndUnloadCarGatewayService;
import com.jd.bluedragon.external.gateway.service.VirtualBoardGatewayService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 卸车相关功能测试
 * @author lvyuan21
 * @date 2020-12-29 16:14
 */
//@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
//@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
//@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
//@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
//@ContextConfiguration(locations = "classpath:distribution-web-context.xml")

@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class VirtualBoardGatewayServiceImplTest {



    @Resource
    private VirtualBoardGatewayService virtualBoardGatewayService;



    @Test
    public void testStartUnloadTask() {

        AutoBoardCompleteRequest req = new AutoBoardCompleteRequest();
        req.setBarcode("JDVF00001658288-9-20-");
        req.setMachineCode("test006");
        req.setOperatorErp("wuyoude");
        req.setSiteCode(910);
        virtualBoardGatewayService.autoBoardDetail(req);


    }

}
