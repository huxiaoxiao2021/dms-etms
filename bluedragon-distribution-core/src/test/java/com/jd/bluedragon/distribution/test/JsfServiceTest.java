package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.distribution.external.jos.service.JosService;
import com.jd.bluedragon.distribution.jsf.domain.MixedPackageConfigResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by wangtingwei on 2015/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-jsf.xml"})
public class JsfServiceTest  {


    @Autowired(required = false)
    private JsfSortingResourceService jsfSortingResourceService;

    @Test
    public void testABC(){
       List<MixedPackageConfigResponse> a =  jsfSortingResourceService.getMixedConfigsBySitesAndTypes(482, 857, 1, 1);
        for(MixedPackageConfigResponse b : a){
            System.out.println(b.getMixedSiteName());
        }
    }
}
