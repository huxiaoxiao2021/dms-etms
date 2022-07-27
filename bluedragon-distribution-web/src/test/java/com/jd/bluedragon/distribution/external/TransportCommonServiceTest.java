package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.TransportCommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.external
 * @Description:
 * @date Date : 2022年07月18日 14:56
 */
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TransportCommonServiceTest {

    @Autowired
    private TransportCommonService transportCommonService;

    @Test
    public void testBatchAddSealVehicle(){
        InvokeResult<Integer> baseEntity =  transportCommonService.getSumByBoxCode("BC1001190301100000001303");
        SortingPageRequest request = new SortingPageRequest();
        request.setBoxCode("BC1001190301100000001303");
        request.setPageNumber(1);
        request.setLimit(10);
        InvokeResult<List<String>> baseEntity1 = transportCommonService.getPagePackageNoByBoxCode(request);
        Integer result = baseEntity.getData();
    }
}
