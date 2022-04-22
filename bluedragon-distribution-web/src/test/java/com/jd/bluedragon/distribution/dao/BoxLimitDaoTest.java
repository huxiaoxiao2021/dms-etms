package com.jd.bluedragon.distribution.dao;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.dao.BoxLimitConfigDao;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author chenyaguo
 * @date 2022/3/31 14:35
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class BoxLimitDaoTest {

    private static final Logger log = LoggerFactory.getLogger(BoxLimitDaoTest.class);

    @Autowired
    private BoxLimitConfigDao boxLimitConfigDao;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void insertTest() {
        BoxLimitConfig config = new BoxLimitConfig();
        config.setSiteName("test001231");
        //config.setSiteId(1);
        config.setLimitNum(110);
        config.setOperatorErp("testErp001");
        config.setOperatorSiteId(1);
        config.setOperatorSiteName("testErpSite");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(false);
        config.setConfigType(1);
        config.setBoxNumberType("BC");
        boxLimitConfigDao.insert(config);
    }

    @Test
    public void updateByPrimaryKeySelectiveTest(){
        BoxLimitConfig config = new BoxLimitConfig();
        config.setId(175L);
        config.setSiteName("test001");
        config.setSiteId(22);
        config.setLimitNum(222);
        config.setOperatorErp("testErp0011");
        config.setOperatorSiteId(1);
        config.setOperatorSiteName("testErpSit12e");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(true);
        config.setConfigType(1);
        config.setBoxNumberType("BC");
        boxLimitConfigDao.updateByPrimaryKeySelective(config);
    }

    @Test
    public void countByConditionTest() {
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        dto.setConfigType(2);
        dto.setSiteId(38);
        Integer countByCondition = boxLimitConfigDao.countByCondition(dto);
        log.info("===============>>>> COUNT:{}",countByCondition);
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryByCondition(dto);
        log.info("result :{}", JSON.toJSONString(boxLimitConfigs));
    }

    @Test
    public void deleteTest(){
        List<Long> integers = Arrays.asList(174L, 175L);
        boxLimitConfigDao.batchDelete(integers);
    }
}
