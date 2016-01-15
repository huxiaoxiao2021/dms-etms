package com.jd.bluedragon.distribution.cross.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import java.util.Map;
import java.util.List;

public class CrossSortingDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private CrossSortingDao crossSortingDao;
	
	
	@Test
    public void testAdd() {
        CrossSorting parameter = new CrossSorting();
        parameter.setOrgId(500);
        parameter.setCreateDmsCode(864);
        parameter.setCreateDmsName("Stone");
        parameter.setDestinationDmsCode(732);
        parameter.setDestinationDmsName("Mary");
        parameter.setMixDmsCode(129);
        parameter.setMixDmsName("James");
        parameter.setCreateUserCode(790);
        parameter.setCreateUserName("Stone");
        parameter.setDeleteUserCode(654);
        parameter.setDeleteUserName("Jax");
        parameter.setCreateTime(new Date());
        parameter.setDeleteTime(new Date());
        parameter.setYn(628);
        crossSortingDao.add(parameter);
    }
	
	@Test
    public void testFindCrossSorting() {
        CrossSorting parameter = new CrossSorting();
        parameter.setCreateDmsCode(837);
        parameter.setDestinationDmsCode(159);
        parameter.setMixDmsCode(984);
        crossSortingDao.findCrossSorting(parameter);
    }
	
	@Test
    public void testUpdateForDelete() {
        CrossSorting parameter = new CrossSorting();
        parameter.setDeleteUserCode(478);
        parameter.setDeleteUserName("Joe");
        parameter.setDeleteTime(new Date());
        parameter.setId((long)1145);
        crossSortingDao.updateForDelete(parameter);
    }
	
	@Test
    public void testAddBatch() {
        List parameter = new ArrayList();
        //set property for item.orgId
        //set property for item.createDmsCode
        //set property for item.createDmsName
        //set property for item.destinationDmsCode
        //set property for item.destinationDmsName
        //set property for item.mixDmsCode
        //set property for item.mixDmsName
        //set property for item.createUserCode
        //set property for item.createUserName
        //set property for item.deleteUserCode
        //set property for item.deleteUserName
        //set property for item.createTime
        //set property for item.deleteTime
        //set property for item.yn
        crossSortingDao.addBatch(parameter);
    }
	
	@Test
    public void testDelete() {
        Map parameter = new HashMap();
        // parameter.put("id", new Object());
        crossSortingDao.delete(parameter);
    }
	
	@Test
    public void testUpdateCrossSorting() {
        CrossSorting parameter = new CrossSorting();
        parameter.setOrgId(385);
        parameter.setCreateDmsName("Mary");
        parameter.setDestinationDmsName("Joe");
        parameter.setMixDmsName("James");
        parameter.setCreateUserCode(993);
        parameter.setCreateUserName("Jone");
        parameter.setDeleteUserCode(986);
        parameter.setDeleteUserName("Jone");
        parameter.setCreateTime(new Date());
        parameter.setDeleteTime(new Date());
        parameter.setCreateDmsCode(862);
        parameter.setDestinationDmsCode(469);
        parameter.setMixDmsCode(221);
        crossSortingDao.updateCrossSorting(parameter);
    }
}
