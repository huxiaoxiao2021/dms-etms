package com.jd.bluedragon.distribution.testCore.inspection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class InspectionDaoTest {
	
	@Autowired
	private InspectionDao inspectionDao;
	
	@Test
	public void testInsert(){
		Inspection inspection = new Inspection();
		inspection.setCreateSiteCode(4);
		//inspection.setBoxCode("11b");
		inspection.setCreateUser("11cr");
		inspection.setCreateUserCode(1);
		inspection.setExceptionType("exce");
		inspection.setInspectionType(10);
		inspection.setPackageBarcode("113355N34packge");
		inspection.setReceiveSiteCode(1111);
		inspection.setSealBoxCode("11Seal");
		inspection.setUpdateUser("111update");
		inspection.setUpdateUserCode(1111);
		inspection.setCreateTime(new java.util.Date());
		inspection.setUpdateTime(new java.util.Date());
		try {
			int result = inspectionDao.add(InspectionDao.namespace, inspection);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testQuery(){
		Inspection inspection = new Inspection();
		inspection.setInspectionId(1001l);
		/*inspection.setCreateSiteCode(1111);
		inspection.setBoxCode("11b");
		inspection.setCreateUser("11cr");
		inspection.setCreateUserCode(1111);
		inspection.setExceptionType("exce");
		inspection.setInspectionType(1);
		inspection.setPackageBarcode("113355N34packge");
		inspection.setReceiveSiteCode(1111);
		inspection.setSealBoxCode("11Seal");
		inspection.setUpdateUser("111update");
		inspection.setUpdateUserCode(1111);*/
		//inspection.setCreateTime(new Date());
		Inspection bean;
		try {
			bean = inspectionDao.queryForObject(inspection);
			System.out.println("--------"+bean.getPackageBarcode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//@TestsortingRet/checkReDispatch
	public void testUpdate(){
		Inspection inspection = new Inspection();
		inspection.setInspectionId(1l);
		inspection.setCreateSiteCode(1111);
		inspection.setBoxCode("22b");
		inspection.setCreateTime(new Date());
		inspection.setCreateUser("22cr");
		inspection.setCreateUserCode(1111);
		inspection.setExceptionType("222ttttt");
		inspection.setInspectionType(1);
		inspection.setPackageBarcode("2222N34packge");
		inspection.setReceiveSiteCode(1111);
		inspection.setSealBoxCode("222Seal");
		inspection.setUpdateUser("22update");
		inspection.setUpdateUserCode(1111);
		inspection.setUpdateTime(new Date());
		inspection.setYn(0);
		try {
			int result = inspectionDao.update(InspectionDao.namespace, inspection);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	/*public void testBatchAdd(){
		try {
			List<Inspection> lists = new ArrayList<Inspection>();
			Inspection inspection = null;
			for( int i=1;i<1000;i++ ){
				inspection = new Inspection();
				inspection.setInspectionId((long)i);
				inspection.setCreateSiteCode(1111);
				inspection.setBoxCode("11b");
				inspection.setCreateUser("11cr");
				inspection.setCreateUserCode(1111);
				inspection.setExceptionType("exce");
				inspection.setInspectionType(1);
				inspection.setPackageBarcode("113355N34packge"+i);
				inspection.setReceiveSiteCode(1111);
				inspection.setSealBoxCode("11Seal"+i);
				inspection.setUpdateUser("111update");
				inspection.setUpdateUserCode(1111);
				lists.add(inspection);
			}
			int result = inspectionDao.addBatch(lists);
			System.out.println("add batch--"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	//@Test
	public void testSelectSelective(){
		Inspection inspection = new Inspection();
		inspection.setYn(1);
		List<Inspection> list;
		try {
			list = inspectionDao.selectSelective(inspection);
			for( Inspection bean:list ){
				System.out.println(bean.getInspectionId()+"---"+bean.getPackageBarcode()+"---"+bean.getBoxCode());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void testBatchStatusUpdateByPrimarykey(){
		List<Inspection> inspections = new ArrayList<Inspection>();
		Inspection inspection = null;
		inspection = new Inspection();
		inspection.setInspectionId(1l);
		inspections.add(inspection);
		
		inspection = new Inspection();
		inspection.setInspectionId(2l);
		inspections.add(inspection);
		
		inspection = new Inspection();
		inspection.setInspectionId(3l);
		inspections.add(inspection);

		inspection = new Inspection();
		inspection.setInspectionId(4l);
		inspections.add(inspection);
		
		inspection = new Inspection();
		inspection.setInspectionId(5l);
		inspections.add(inspection);
		
		try {
			int result = inspectionDao.updateStatusBatchByPrimaryKey(inspections);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdateYnByPackage(){
		Inspection inspection = new Inspection.Builder("179619456N1S2H17", 1006).receiveSiteCode(10).boxCode("BC010F001000003900004015").inspectionType(30).build();
		int result = inspectionDao.updateYnByPackage(inspection);
		System.out.println("========"+result);
	}
	
}
