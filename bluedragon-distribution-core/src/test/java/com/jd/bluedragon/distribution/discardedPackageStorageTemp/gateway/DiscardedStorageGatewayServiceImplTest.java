package com.jd.bluedragon.distribution.discardedPackageStorageTemp.gateway;

import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageNotScanItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.QueryUnScanDiscardedPackagePo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.QueryUnSubmitDiscardedListPo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.SubmitDiscardedPackagePo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageStorageTempStatusEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteOperateTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteWaybillTypeEnum;
import com.jd.bluedragon.external.gateway.service.DiscardedStorageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * 网关
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-08 19:02:06 周三
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class DiscardedStorageGatewayServiceImplTest {

    @Autowired
    private DiscardedStorageGatewayService discardedStorageGatewayService;

    private OperateUser genOperateUser(){
        OperateUser operateUser = new OperateUser();
        operateUser.setUserCode("xumigen");
        operateUser.setUserName("徐迷根");
        operateUser.setSiteCode(10186);
        operateUser.setSiteName("名称");
        return operateUser;
    }

    @Test
    public void testQueryUnSubmitDiscardedList() {
        final QueryUnSubmitDiscardedListPo paramObj = new QueryUnSubmitDiscardedListPo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        final JdCResponse<List<DiscardedWaybillScanResultItemDto>> result = discardedStorageGatewayService.queryUnSubmitDiscardedList(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testScanDiscardedPackage() {
        final ScanDiscardedPackagePo paramObj = new ScanDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setBarCode("JDV000505854251-2-5-");
        paramObj.setStatus(DiscardedPackageStorageTempStatusEnum.STORAGE.getCode());
        paramObj.setOperateType(WasteOperateTypeEnum.STORAGE.getCode());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final JdCResponse<List<DiscardedWaybillScanResultItemDto>> result = discardedStorageGatewayService.scanDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testSubmitDiscardedPackage() {
        final SubmitDiscardedPackagePo paramObj = new SubmitDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final JdCResponse<Boolean> result = discardedStorageGatewayService.submitDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testQueryUnScanDiscardedPackage() {
        final QueryUnScanDiscardedPackagePo paramObj = new QueryUnScanDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final JdCResponse<List<DiscardedPackageNotScanItemDto>> result = discardedStorageGatewayService.queryUnScanDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }
}
