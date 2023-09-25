package ld;

import com.jd.bluedragon.distribution.coldChain.domain.ColdSendResult;
import com.jd.bluedragon.distribution.external.service.DmsSortingService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.domain.SortingRequestDto;
import com.jdl.basic.common.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package ld
 * @Description:
 * @date Date : 2023年09月22日 10:57
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class DmsSortingServiceTest {

    @Resource
    private DmsSortingService iDmsSortingService;
    @Test
    public void testFindSortingByPackageCode() {
        for(int i = 0;i<10;i++) {
            InvokeResult<List<SortingDto>> result = iDmsSortingService.findSortingByPackageCode("JDVC00003095700-1-3-", 910);
            System.out.println(JsonHelper.toJSONString(result));
        }
    }
    @Test
    public void testBindingBoxMaterialPackageRelation() {
        for(int i = 0;i<10;i++) {
            InvokeResult<String> result = iDmsSortingService.bindingBoxMaterialPackageRelation(JsonHelper.toObject("{\"boxCode\":\"MS1001221206290000100513\",\"businessType\":10,\"createSiteCode\":910,\"createSiteName\":\"北京马驹桥分拣中心\",\"createUser\":\"tms3f\",\"createUserCode\":1382444018,\"featureType\":0,\"isCancel\":0,\"isLoss\":0,\"materialCode\":\"AY0101010000599\",\"needBindMaterialFlag\":1,\"operateTime\":\"2023-09-21 21:02:46\",\"packageCode\":\"JDVC00003095700-1-3-\",\"receiveSiteCode\":267460,\"receiveSiteName\":\"站点所属分拣\",\"waybillCode\":\"JDVC00003095700\"}", SortingRequestDto.class));
            System.out.println(JsonHelper.toJSONString(result));
        }
    }
    @Test
    public void testCancelSorting() {
        for(int i = 0;i<10;i++) {
            InvokeResult<String> result = iDmsSortingService.cancelSorting(JsonHelper.toObject("{\"boxCode\":\"\",\"businessType\":0,\"createSiteCode\":910,\"createSiteName\":\"\",\"createUser\":\"tms3f\",\"createUserCode\":1382444018,\"featureType\":0,\"isCancel\":0,\"isLoss\":0,\"materialCode\":\"\",\"needBindMaterialFlag\":0,\"operateTime\":\"2023-09-22 09:54:28\",\"packageCode\":\"JDVC00003095700-1-3-\",\"receiveSiteCode\":0,\"receiveSiteName\":\"\",\"waybillCode\":\"\"}", SortingRequestDto.class));
            System.out.println(JsonHelper.toJSONString(result));
        }
    }
}
