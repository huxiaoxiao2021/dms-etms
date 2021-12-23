package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service;

import com.jd.bluedragon.common.dto.base.request.OperateUser;
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
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * 快递弃件暂存测试类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-08 18:06:51 周三
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class DiscardedPackageStorageTempServiceImplTest {

    @Autowired
    private DiscardedPackageStorageTempService discardedPackageStorageTempService;

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
        final Result<List<DiscardedWaybillScanResultItemDto>> result = discardedPackageStorageTempService.queryUnSubmitDiscardedList(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testScanDiscardedPackage() {
        final ScanDiscardedPackagePo paramObj = new ScanDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setBarCode("JDV000505854251-2-5-");
        paramObj.setStatus(DiscardedPackageStorageTempStatusEnum.STORAGE.getCode());
        paramObj.setOperateType(WasteOperateTypeEnum.STORAGE.getCode());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final Result<List<DiscardedWaybillScanResultItemDto>> result = discardedPackageStorageTempService.scanDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testSubmitDiscardedPackage() {
        final SubmitDiscardedPackagePo paramObj = new SubmitDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final Result<Boolean> result = discardedPackageStorageTempService.submitDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }

    public void testQueryUnScanDiscardedPackage() {
        final QueryUnScanDiscardedPackagePo paramObj = new QueryUnScanDiscardedPackagePo();
        paramObj.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode()).setOperateUser(genOperateUser());
        paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final Result<List<DiscardedPackageNotScanItemDto>> result = discardedPackageStorageTempService.queryUnScanDiscardedPackage(paramObj);
        System.out.println(JsonHelper.toJson(result));
    }
}
