package com.jd.bluedragon.distribution.offline.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.offline.domain.OfflineLog;

public class OfflineDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private OfflineDao offlineDao;
	
	
	@Test
    public void testQueryByParams() {
        OfflineLog parameter = new OfflineLog();
        parameter.setOfflineLogId((long)5791);
        parameter.setBoxCode("Stone");
        parameter.setWaybillCode("Stone");
        parameter.setPackageCode("James");
        parameter.setReceiveSiteCode(491);
        parameter.setStatus(613);
        parameter.setCreateSiteCode(188);
        // parameter.getEndIndex(new Object());
        // parameter.getStartIndex(new Object());
        offlineDao.queryByParams(parameter);
    }
	
	@Test
    public void testUpdateById() {
        OfflineLog parameter = new OfflineLog();
        parameter.setBoxCode("Jone");
        parameter.setWaybillCode("Jax");
        parameter.setPackageCode("Joe");
        parameter.setReceiveSiteCode(339);
        parameter.setStatus(797);
        parameter.setOfflineLogId((long)5665);
        offlineDao.updateById(parameter);
    }
	
	@Test
    public void testTotalSizeByParams() {
        OfflineLog parameter = new OfflineLog();
        parameter.setOfflineLogId((long)7390);
        parameter.setBoxCode("Jim");
        parameter.setWaybillCode("Joe");
        parameter.setPackageCode("James");
        parameter.setReceiveSiteCode(401);
        parameter.setStatus(778);
        parameter.setCreateSiteCode(317);
        offlineDao.totalSizeByParams(parameter);
    }
	
	@Test
    public void testAdd() {
        OfflineLog parameter = new OfflineLog();
        parameter.setWaybillCode("Jim");
        parameter.setPackageCode("Jim");
        parameter.setBoxCode("Mary");
        parameter.setBusinessType(689);
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(339);
        parameter.setCreateSiteCode(846);
        parameter.setCreateSiteName("Jim");
        parameter.setReceiveSiteCode(410);
        parameter.setOperateTime(new Date());
        parameter.setTurnoverBoxCode("Stone");
        parameter.setWeight(0.7722556920408051);
        parameter.setVolume(0.6521414042886224);
        parameter.setExceptionType("Stone");
        parameter.setSendCode("Jone");
        parameter.setSealBoxCode("Jax");
        parameter.setShieldsCarCode("James");
        parameter.setVehicleCode("Jim");
        parameter.setTaskType(44);
        parameter.setSendUser("Jone");
        parameter.setSendUserCode(847);
        parameter.setStatus(933);
        offlineDao.add(parameter);
    }
	
	@Test
    public void testFindByObj() {
        OfflineLog parameter = new OfflineLog();
        parameter.setOfflineLogId((long)3090);
        parameter.setBoxCode("Joe");
        parameter.setWaybillCode("James");
        parameter.setPackageCode("Jim");
        parameter.setReceiveSiteCode(671);
        parameter.setStatus(698);
        parameter.setCreateSiteCode(798);
        offlineDao.findByObj(parameter);
    }
}
