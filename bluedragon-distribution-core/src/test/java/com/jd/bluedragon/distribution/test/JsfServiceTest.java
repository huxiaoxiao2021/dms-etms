package com.jd.bluedragon.distribution.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.rd.wms.jsf.bin.service.RdWmsStoreExtendService;
import com.jd.rd.wms.jsf.common.ServiceResult;

/**
 * Created by wangtingwei on 2015/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-jsf.xml"})
public class JsfServiceTest  {

	@Qualifier("rdWmsStoreExtendService")
	@Autowired(required = false)
	private RdWmsStoreExtendService rdWmsStoreExtendService;
    
//    @Autowired(required = false)
//    private JsfSortingResourceService jsfSortingResourceService;

    @Test
    public void testABC(){
    	 ServiceResult<String> res = rdWmsStoreExtendService.getOrgStoreTag(6, 25016);
//       List<MixedPackageConfigResponse> a =  jsfSortingResourceService.getMixedConfigsBySitesAndTypes(482, 857, 1, 1);
//        for(MixedPackageConfigResponse b : a){
//            System.out.println(b.getMixedSiteName());
//        }
    }
}
