package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageFinishStatisticsDto;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.FinishSubmitDiscardedUo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.UnSubmitDiscardedListQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteWaybillTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 弃件暂存运单dao测试
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-08 18:36:09 周三
 */
public class DiscardedWaybillStorageTempDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private DiscardedWaybillStorageTempDao discardedWaybillStorageTempDao;

    @Test
    public void testSelectUnSubmitDiscardedWaybillList() {
        final UnSubmitDiscardedListQo query = new UnSubmitDiscardedListQo();
        query.setUnSubmitStatus(Constants.YN_NO);
        query.setOperatorErp("xumigen");
        query.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final List<DiscardedWaybillScanResultItemDto> result = discardedWaybillStorageTempDao.selectUnSubmitDiscardedWaybillList(query);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void testSelectDiscardedPackageFinishStatistics() {
        final UnSubmitDiscardedListQo query = new UnSubmitDiscardedListQo();
        query.setUnSubmitStatus(Constants.YN_NO);
        query.setOperatorErp("xumigen");
        query.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
        final DiscardedPackageFinishStatisticsDto result = discardedWaybillStorageTempDao.selectDiscardedPackageFinishStatistics(query);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void testFinishSubmitDiscarded() {
        final FinishSubmitDiscardedUo updateObj = new FinishSubmitDiscardedUo();
        updateObj.setUnSubmitStatus(Constants.YN_NO);
        updateObj.setOperatorErp("xumigen");
        updateObj.setSubmitStatus(Constants.YN_YES);
        updateObj.setOperateTime(new Date());
        final int result = discardedWaybillStorageTempDao.finishSubmitDiscarded(updateObj);
        System.out.println(JsonHelper.toJson(result));
    }
}