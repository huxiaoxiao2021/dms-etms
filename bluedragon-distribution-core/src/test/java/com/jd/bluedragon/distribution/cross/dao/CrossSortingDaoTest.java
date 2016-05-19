package com.jd.bluedragon.distribution.cross.dao;

import org.junit.Assert;
import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CrossSortingDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private CrossSortingDao crossSortingDao;
	
	//@Test
    public void testAdd() {
        CrossSorting parameter = new CrossSorting();
        parameter.setOrgId(6);
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
        parameter.setYn(1);
        Assert.assertEquals(new Integer(1), crossSortingDao.add(CrossSortingDao.namespace , parameter));
    }
	
	//@Test
    public void testFindCrossSorting() {
        CrossSorting parameter = new CrossSorting();
        parameter.setCreateDmsCode(864);
        parameter.setDestinationDmsCode(732);
        parameter.setMixDmsCode(129);
        Assert.assertNotNull(crossSortingDao.findCrossSorting(parameter));
    }
	
	//@Test
    public void testUpdateForDelete() {
        CrossSorting parameter = new CrossSorting();
        parameter.setDeleteUserCode(478);
        parameter.setDeleteUserName("Joe");
        parameter.setDeleteTime(new Date());
        parameter.setId((long)2);
        Assert.assertEquals(1, crossSortingDao.updateCrossSortingForDelete(parameter));
    }
	
	//@Test
    public void testAddBatch() {
		List<CrossSorting> csList = new ArrayList<CrossSorting>();
		
		CrossSorting parameter = new CrossSorting();
        parameter.setOrgId(6);
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
        parameter.setYn(1);
        csList.add(parameter);
        
        parameter = new CrossSorting();
        parameter.setOrgId(6);
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
        parameter.setYn(1);
        csList.add(parameter);
        
        parameter = new CrossSorting();
        parameter.setOrgId(6);
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
        parameter.setYn(1);
        csList.add(parameter);
        Assert.assertEquals(1, crossSortingDao.addBatchCrossSorting(csList));
    }
	
	//@Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testDelete() {
        Map parameter = new HashMap();
        parameter.put("id", (long)3);
        Assert.assertEquals(1, crossSortingDao.deleteCrossSorting(parameter));
    }
	
	//@Test
    public void testUpdateCrossSorting() {
        CrossSorting parameter = new CrossSorting();
        parameter.setOrgId(3);
        parameter.setCreateDmsName("Mary1");
        parameter.setDestinationDmsName("Joe1");
        parameter.setMixDmsName("James1");
        parameter.setCreateUserCode(993);
        parameter.setCreateUserName("Jone1");
        parameter.setDeleteUserCode(986);
        parameter.setDeleteUserName("Jone1");
        parameter.setCreateTime(new Date());
        parameter.setDeleteTime(new Date());
        parameter.setCreateDmsCode(864);
        parameter.setDestinationDmsCode(732);
        parameter.setMixDmsCode(129);
        Assert.assertEquals(1, crossSortingDao.updateCrossSorting(parameter));
    }
	
	//@Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void findPageCrossSorting(){
		Map parameter = new HashMap();
        parameter.put("orgId", 3);
        parameter.put("createUserName", "Stone");
        parameter.put("createDmsCode", 864);
        parameter.put("destinationDmsCode", 732);
        parameter.put("startIndex", 1);
        parameter.put("endIndex", 2);
        List<CrossSorting> list = crossSortingDao.findPageCrossSorting(parameter);
        Assert.assertNotNull(list);
	}
	
	//@Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void findCountCrossSorting(){
		Map parameter = new HashMap();
        parameter.put("orgId", 3);
        parameter.put("createUserName", "Stone");
        parameter.put("createDmsCode", 864);
        parameter.put("destinationDmsCode", 732);
        Integer i = crossSortingDao.findCountCrossSorting(parameter);
        Assert.assertEquals(new Integer(1), i);
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void findMixDms(){
		Map parameter = new HashMap();
        parameter.put("createDmsCode", 864);
        parameter.put("destinationDmsCode", 732);
        List<CrossSorting> list = crossSortingDao.findMixDms(parameter);
        Assert.assertNotNull(list);
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void findOne(){
		Map parameter = new HashMap();
        parameter.put("createDmsCode", 864);
        parameter.put("destinationDmsCode", 732);
        parameter.put("mixDmsCode", 129);
        List<CrossSorting> list = crossSortingDao.findOne(parameter);
        Assert.assertNotNull(list);
	}
}
