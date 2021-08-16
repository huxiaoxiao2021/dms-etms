package com.jd.bluedragon.distribution.stock;

import com.jd.bluedragon.common.dto.stock.StockInventoryResult;
import com.jd.bluedragon.common.dto.stock.StockInventoryScanDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stock.dao.StockInventoryDao;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;
import com.jd.bluedragon.distribution.stock.service.StockInventoryService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/6/15 3:28 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
public class StockInventoryServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(StockInventoryServiceImplTest.class);

    @Autowired
    private StockInventoryService stockInventoryService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void stockInventoryScan() {
        try {
            StockInventoryScanDto stockInventoryScanDto = new StockInventoryScanDto();
            stockInventoryScanDto.setPackageCode("JDV000700265286-1-3-");
            stockInventoryScanDto.setWaveCode("010F002-20210616054600");
            stockInventoryScanDto.setWaveBeginTime(new Date(1623793560000L));
            stockInventoryScanDto.setWaveEndTime(new Date(1623828600000L));
            stockInventoryScanDto.setOperateSiteCode(910);
            stockInventoryScanDto.setOperateSiteName("北京马驹桥分拣中心");
            stockInventoryScanDto.setOperateUserErp("bjxings");
            stockInventoryScanDto.setOperateUserName("邢松");
            stockInventoryScanDto.setScanTime(new Date());

            String[] ss = new String[]{"JDV000700265286-1-3-","JDV000700263971-1-3-","JDV000700263980-1-3-","JDV000700263999-1-3-","JDV000700264000-1-3-"};
            for (String packageCode : ss) {
                stockInventoryScanDto.setPackageCode(packageCode);
                InvokeResult<StockInventoryResult> result = stockInventoryService.stockInventoryScan(stockInventoryScanDto);
                logger.info(JsonHelper.toJson(result.getData()));
                Assert.assertTrue(true);
            }
        }catch (Exception e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void queryInventoryUnSendPacks() {
    }

    @Test
    public void queryInventoryUnSendNum() {
    }

    @Test
    public void queryStockInventory() {
        try {
            InvokeResult<StockInventoryResult> result = stockInventoryService.queryStockInventory(910);
            logger.info(JsonHelper.toJson(result.getData()));
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Autowired
    private StockInventoryDao stockInventoryDao;

    @Test
    public void initDate() {
        try {

            StockInventory stockInventory = new StockInventory();
            // 波次 010F002-20210616003100
            stockInventory.setInventoryStatus(1);
            stockInventory.setWaveCode("010F002-20210616003100");
            stockInventory.setWaveBeginTime(new Date(1623774660000L));
            stockInventory.setWaveEndTime(new Date(1623793500000L));
            stockInventory.setInventoryTime(new Date(1623812501000L));
            stockInventory.setOperateSiteCode(910);
            stockInventory.setOperateSiteName("北京马驹桥分拣中心");
            stockInventory.setOperateUserId(10053);
            stockInventory.setOperateUserErp("bjxings");
            stockInventory.setOperateUserName("邢松");

            stockInventory.setUpdateUserErp("bjxings");
            stockInventory.setYn(1);

            String[] ss = new String[]{"JDV000700263971-1-3-","JDV000700263980-1-3-","JDV000700263999-1-3-","JDV000700264000-1-3-"};
            for (String packageCode : ss) {
                stockInventory.setPackageCode(packageCode);
                stockInventory.setCreateTime(new Date());
                stockInventory.setUpdateTime(new Date());
                stockInventoryDao.add(stockInventory);
            }

            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }
}