package com.jd.bluedragon.distribution.dao.common;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/mysql/distribution-core-datasource-test.xml", "classpath:spring/distribution-core-dao.xml"})
public abstract class AbstractMySQLDaoIntegrationTest {

}
