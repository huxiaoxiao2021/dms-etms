package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/24 16:59
 */
public class UnloadCarDistributionDaoTest  extends AbstractDaoIntegrationH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarDistributionDao unloadCarDistributionDao;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void add() {

    }
}