package com.jd.bluedragon.distribution.testCore.inspection;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class InspectionECDaoTest {

	@Autowired
	private InspectionECDao inspectionECDao;
	
	public static final String namespace = InspectionECDao.class.getName();
	
	//@Test
	public void testInsert(){
		try {
			InspectionEC inspectionEC =null;
			/*for( int i=1;i<100;i++){
				inspectionEC = new InspectionEC();
				inspectionEC.setIecId((long)i);
				inspectionEC.setBoxCode("boxCode"+i);
				inspectionEC.setCreateSiteCode("createSiteCode"+i);
				inspectionEC.setExceptionType("exceptionType"+i);
				inspectionEC.setInspectionType(2);
				inspectionEC.setPackageBarcode("packageBarcode"+i);
				int result = inspectionECDao.add(namespace, inspectionEC);
				System.out.println(result);
			}*/
			inspectionEC = new InspectionEC();
			inspectionEC.setCheckId(2l);
			inspectionEC.setBoxCode("boxCode");
			inspectionEC.setCreateSiteCode(1111);
			inspectionEC.setReceiveSiteCode(4);
			inspectionEC.setExceptionType("exceptionType");
			inspectionEC.setInspectionType(2);
			inspectionEC.setPackageBarcode("packageBarcode");
			int result = inspectionECDao.add(namespace, inspectionEC);
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	/*public void testBatchInsert(){
		List<InspectionEC> list = new ArrayList<InspectionEC>();
		InspectionEC inspectionEC =null;
		inspectionEC = new InspectionEC();
		inspectionEC.setCheckId(1l);
		inspectionEC.setBoxCode("boxCode");
		inspectionEC.setCreateSiteCode(1111);
		inspectionEC.setExceptionType("exceptionType");
		inspectionEC.setInspectionType(1);
		inspectionEC.setPackageBarcode("packageBarcode");
		list.add(inspectionEC);
		InspectionEC inspectionEC2 = new InspectionEC();
		inspectionEC2.setCheckId(2l);
		inspectionEC2.setBoxCode("boxCode");
		inspectionEC2.setCreateSiteCode(1111);
		inspectionEC2.setExceptionType("exceptionType");
		inspectionEC2.setInspectionType(2);
		inspectionEC2.setPackageBarcode("packageBarcode");
		list.add(inspectionEC2);
		int result;
		try {
			result = inspectionECDao.addBatch(list);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	//@Test
	public void testSelect(){
		try{
			InspectionEC inspectionEC = new InspectionEC();
			inspectionEC.setStartNo(0);
			//inspectionEC.setLimitNo(10);
			List<InspectionEC> list = inspectionECDao.selectSelective(inspectionEC);
//			List<InspectionEC> list = inspectionECDao.selectInspECList(inspectionEC);
			for( InspectionEC bean:list ){
				System.out.println(bean.getCheckId()+"--"+bean.getBoxCode()+"---"+bean.getPackageBarcode());
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testUpdate(){
		InspectionEC inspectionEC = new InspectionEC();
		inspectionEC.setCreateSiteCode(1111);
		inspectionEC.setInspectionECType(1);
		inspectionEC.setWaybillCode("111");
		inspectionEC.setPackageBarcode("111");
		try {
			int result = inspectionECDao.updateStatus(inspectionEC);
			System.out.println("---"+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testQueryExceptions(){
		try {
			int res = inspectionECDao.queryExceptionsCore(1111, 4, "boxCode");
			System.out.println("======="+res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStatusFuzzy(){
		try {
			InspectionEC inspectionEC = new InspectionEC.Builder("179619455N%", 1006)
				.waybillCode("179619455").receiveSiteCode(10).boxCode("BC010F001000003900004013")
				.status(1).updateUser("limei").updateUserCode(943).updateTime(new java.util.Date())
				.build();
			inspectionEC.setCreateSiteCode(1006);
			int result = inspectionECDao.updateStatusFuzzy(inspectionEC);
			System.out.println("==========="+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
