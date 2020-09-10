package com.jd.bluedragon.distribution.boxlimit.dao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.Gson;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BoxLimitConfigDaoTest extends AbstractDaoIntegrationH2Test {
    private static final Logger log = LoggerFactory.getLogger(BoxLimitConfigDaoTest.class);
    @Autowired
    private BoxLimitConfigDao boxLimitConfigDao;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void insertTest() {
        BoxLimitConfig config = new BoxLimitConfig();
        config.setSiteName("test001");
        config.setSiteId(1);
        config.setLimitNum(110);
        config.setOperatorErp("testErp001");
        config.setOperatorSiteId(1);
        config.setOperatorSiteName("testErpSite");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(false);
        boxLimitConfigDao.insert(config);
    }
    @Test
    public void insertSelectiveTest() {
        BoxLimitConfig config = new BoxLimitConfig();
        config.setSiteName("test002");
        config.setSiteId(2);
        config.setLimitNum(220);
        config.setOperatorErp("testErp002");
        config.setOperatorSiteId(2);
//        config.setOperatorSiteName("testErpSite");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(false);
        boxLimitConfigDao.insertSelective(config);
    }
    @Test
    public void selectByPrimaryKeyTest() {
        insertTest();
        BoxLimitConfig config = boxLimitConfigDao.selectByPrimaryKey(1);
        log.info("=============>>>> 查询结果:{}", new Gson().toJson(config));
    }
    @Test
    public void updateByPrimaryKeySelectiveTest() {
        insertTest();
        selectByPrimaryKeyTest();
        BoxLimitConfig config = new BoxLimitConfig();
        config.setId(1);
        config.setOperatorErp("update - test0001");
        boxLimitConfigDao.updateByPrimaryKeySelective(config);
        selectByPrimaryKeyTest();
    }
    @Test
    public void updateByPrimaryKeyTest() {
        BoxLimitConfig config = new BoxLimitConfig();
        config.setSiteName("test001");
        config.setSiteId(1);
        config.setLimitNum(110);
        config.setOperatorErp("testErp001");
        config.setOperatorSiteId(1);
        config.setOperatorSiteName("testErpSite");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(false);

        config.setOperatorErp("updateByPrimaryKey ---  test");
        boxLimitConfigDao.updateByPrimaryKey(config);
    }
    @Test
    public void batchInsertTest() {
        List<BoxLimitConfig> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BoxLimitConfig config = new BoxLimitConfig();
            config.setSiteName("batch-insert-num-" + i);
            config.setSiteId(i + 1);
            config.setLimitNum(1000 + i);
            config.setOperatorErp("op-erp-num-" + i);
            config.setOperatorSiteId(i + 1);
            config.setOperatorSiteName("op-site-name" + i);
            config.setOperatingTime(new Date());
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            config.setYn(false);
            dataList.add(config);
        }
        boxLimitConfigDao.batchInsert(dataList);
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryByCondition(dto);
        log.info("===============>>>> queryByCondition:{}",new Gson().toJson(boxLimitConfigs));
    }

    @Test
    public void queryByConditionTest() {
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        dto.setSiteName("测试");
        dto.setSiteId(1);
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setOffset();

        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryByCondition(dto);
        log.info(new Gson().toJson(boxLimitConfigs));
    }
    @Test
    public void countByConditionTest() {
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        Integer countByCondition = boxLimitConfigDao.countByCondition(dto);
        log.info("===============>>>> COUNT:{}",countByCondition);
    }
    @Test
    public void queryBySiteIdsTest() {
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryBySiteIds(Arrays.asList(1));
        log.info("queryBySiteIds:{}" + new Gson().toJson(boxLimitConfigs));
    }

    @Test
    public void queryLimitNumBySiteIdTest() {
        Integer limitNum = boxLimitConfigDao.queryLimitNumBySiteId(1);
        log.info("=====> limitNum = {}", limitNum);
    }

    @Test
    public void deleteByPrimaryKeyTest() {
        boxLimitConfigDao.deleteByPrimaryKey(1);
    }

    @Test
    public void batchDeleteTest() {
        boxLimitConfigDao.batchDelete(Arrays.asList(1));
    }
}

