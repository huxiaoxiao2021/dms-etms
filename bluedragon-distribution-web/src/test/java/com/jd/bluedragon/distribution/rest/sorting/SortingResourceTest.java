package com.jd.bluedragon.distribution.rest.sorting;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xumigen on 2019/4/11.
 */

@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SortingResourceTest {

    @Autowired
    private SortingResource sortingResource;

    @Test
    public void testcancelPackage(){
        SortingRequest request = new SortingRequest();
        request.setBoxCode("85434861526-1-1-1");
        request.setPackageCode("85434861526-1-1-1");
        request.setWaybillCode("85434861526");
        request.setIsCancel(0);
        request.setReceiveSiteCode(0);
        request.setReceiveSiteName("");
        request.setSealCode("");
        request.setIsLoss(0);
        request.setFeatureType(0);
        request.setWhReverse(0);
//        request.setPackages(Lists.newArrayList());
        request.setBsendCode("");
        request.setKey("");
        request.setUserCode(0);
        request.setUserName("");
        request.setSiteCode(0);
        request.setSiteName("");
        request.setBusinessType(0);
        request.setId(0);
        request.setOperateTime("");

        sortingResource.cancelPackage(request);
    }
}
