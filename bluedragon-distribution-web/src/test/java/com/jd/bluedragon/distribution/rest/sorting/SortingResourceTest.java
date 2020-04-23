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
        request.setPackageCode("JD0000000454734-1-3-");
        request.setUserCode(10053);
        request.setUserName("邢松");
        request.setSiteCode(910);
        request.setSiteName("北京马驹桥分拣中心");
        request.setBusinessType(10);
        request.setOperateTime("2019-08-07 13:48:29");

        sortingResource.cancelPackage(request);
    }
}
