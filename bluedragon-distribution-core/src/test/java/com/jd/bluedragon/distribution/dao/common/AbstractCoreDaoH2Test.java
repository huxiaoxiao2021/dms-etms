package com.jd.bluedragon.distribution.dao.common;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = SpringAppContextConfigPath.H2_APP_DAO_H2_PATH)
public abstract class AbstractCoreDaoH2Test {

}
