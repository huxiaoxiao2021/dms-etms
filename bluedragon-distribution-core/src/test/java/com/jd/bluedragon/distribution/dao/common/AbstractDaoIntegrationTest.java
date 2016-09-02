package com.jd.bluedragon.distribution.dao.common;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = SpringAppContextConfigPath.MYSQL_APP_CONTEXT_PATH)
public abstract class AbstractDaoIntegrationTest {

}
