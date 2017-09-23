package com.jd.bluedragon.utils;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;

import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.utils.canal.CanalEvent;
import com.jd.bluedragon.utils.canal.CanalHelper;

public class CanalHelperTest {
	
	BusinessHelper helper = new BusinessHelper();
	@Test
	public void testFields() {
		Class<?>[] clazzs = {int.class,Integer.class,Object.class,ClassP.class,ClassC.class,TransbillM.class};
		for(Class<?> c:clazzs){
			Map<String,Field> fields = ObjectHelper.getDeclaredFields(c);
			fields = ObjectHelper.getAllFields(c);
			System.err.println("======class:"+c.getName());
			for(String k:fields.keySet()){
				System.err.println(k+":"+fields.get(k).getClass());
			}
		}
		
	}
	@Test
	public void testTransbillM() throws Exception {
		String canalText = "{\"eventType\":\"UPDATE\",\"tableName\":\"transbill_m\",\"tableAlias\":null,\"database\":\"transbill\",\"executeTime\":1505802648000,\"beforeChangeOfColumns\":[{\"name\":\"m_id\",\"value\":\"14652\",\"update\":false,\"sqlType\":-5,\"index\":0,\"length\":0,\"null\":false,\"key\":true,\"mysqlType\":\"bigint(20)\"},{\"name\":\"transbill_code\",\"value\":\"61169925232\",\"update\":false,\"sqlType\":12,\"index\":1,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"waybill_code\",\"value\":\"61169925232\",\"update\":false,\"sqlType\":12,\"index\":2,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"order_flag\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":3,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"schedule_bill_code\",\"value\":\"DP17091800001983\",\"update\":false,\"sqlType\":12,\"index\":4,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"schedule_amount\",\"value\":\"30\",\"update\":false,\"sqlType\":4,\"index\":5,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"truck_spot\",\"value\":\"A01\",\"update\":false,\"sqlType\":12,\"index\":6,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"allocate_sequence\",\"value\":\"16-16\",\"update\":false,\"sqlType\":12,\"index\":7,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(50)\"},{\"name\":\"arrive_time\",\"value\":\"\",\"update\":false,\"sqlType\":93,\"index\":8,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"redelivery_time\",\"value\":\"\",\"update\":false,\"sqlType\":93,\"index\":9,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"redelivery_address\",\"value\":\"\",\"update\":false,\"sqlType\":12,\"index\":10,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"varchar(200)\"},{\"name\":\"operate_time\",\"value\":\"2017-09-19 09:43:16\",\"update\":false,\"sqlType\":93,\"index\":11,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"transbill_state\",\"value\":\"700\",\"update\":false,\"sqlType\":4,\"index\":12,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"site_id\",\"value\":\"394705\",\"update\":false,\"sqlType\":4,\"index\":13,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"site_name\",\"value\":\"沈阳沈北接货仓\",\"update\":false,\"sqlType\":12,\"index\":14,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(200)\"},{\"name\":\"site_code\",\"value\":\"024L008\",\"update\":false,\"sqlType\":12,\"index\":15,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(50)\"},{\"name\":\"generate_type\",\"value\":\"2\",\"update\":false,\"sqlType\":-6,\"index\":16,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"push_pre_flag\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":17,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"create_time\",\"value\":\"2017-09-18 10:20:37\",\"update\":false,\"sqlType\":93,\"index\":18,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"update_time\",\"value\":\"2017-09-19 09:43:16\",\"update\":false,\"sqlType\":93,\"index\":19,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"timestamp\"},{\"name\":\"create_user\",\"value\":\"system\",\"update\":false,\"sqlType\":12,\"index\":20,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(20)\"},{\"name\":\"update_user\",\"value\":\"yanghongquan\",\"update\":false,\"sqlType\":12,\"index\":21,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(20)\"},{\"name\":\"partition_time\",\"value\":\"2017-09-01 00:00:00\",\"update\":false,\"sqlType\":93,\"index\":22,\"length\":0,\"null\":false,\"key\":true,\"mysqlType\":\"datetime\"},{\"name\":\"ts_m\",\"value\":\"1505785396288\",\"update\":false,\"sqlType\":-5,\"index\":23,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"bigint(20)\"},{\"name\":\"yn\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":24,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(1)\"},{\"name\":\"transbill_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":25,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"actual_collection\",\"value\":\"\",\"update\":false,\"sqlType\":3,\"index\":26,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"decimal(18,4)\"},{\"name\":\"collection_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":27,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"transbill_org_id\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":28,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"carrier_name\",\"value\":\"\",\"update\":false,\"sqlType\":12,\"index\":29,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"carrier_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":30,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"}],\"afterChangeOfColumns\":[{\"name\":\"m_id\",\"value\":\"14652\",\"update\":false,\"sqlType\":-5,\"index\":0,\"length\":0,\"null\":false,\"key\":true,\"mysqlType\":\"bigint(20)\"},{\"name\":\"transbill_code\",\"value\":\"61169925232\",\"update\":false,\"sqlType\":12,\"index\":1,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"waybill_code\",\"value\":\"61169925232\",\"update\":false,\"sqlType\":12,\"index\":2,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"order_flag\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":3,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"schedule_bill_code\",\"value\":\"DP17091800001983\",\"update\":false,\"sqlType\":12,\"index\":4,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"schedule_amount\",\"value\":\"30\",\"update\":false,\"sqlType\":4,\"index\":5,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"truck_spot\",\"value\":\"A01\",\"update\":false,\"sqlType\":12,\"index\":6,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"allocate_sequence\",\"value\":\"16-16\",\"update\":false,\"sqlType\":12,\"index\":7,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(50)\"},{\"name\":\"arrive_time\",\"value\":\"\",\"update\":false,\"sqlType\":93,\"index\":8,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"redelivery_time\",\"value\":\"\",\"update\":false,\"sqlType\":93,\"index\":9,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"redelivery_address\",\"value\":\"\",\"update\":false,\"sqlType\":12,\"index\":10,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"varchar(200)\"},{\"name\":\"operate_time\",\"value\":\"2017-09-19 14:30:48\",\"update\":true,\"sqlType\":93,\"index\":11,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"transbill_state\",\"value\":\"700\",\"update\":false,\"sqlType\":4,\"index\":12,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"site_id\",\"value\":\"497\",\"update\":true,\"sqlType\":4,\"index\":13,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"site_name\",\"value\":\"沈阳沈北分拣中心\",\"update\":true,\"sqlType\":12,\"index\":14,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(200)\"},{\"name\":\"site_code\",\"value\":\"024L008\",\"update\":false,\"sqlType\":12,\"index\":15,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(50)\"},{\"name\":\"generate_type\",\"value\":\"2\",\"update\":false,\"sqlType\":-6,\"index\":16,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"push_pre_flag\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":17,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(4)\"},{\"name\":\"create_time\",\"value\":\"2017-09-18 10:20:37\",\"update\":false,\"sqlType\":93,\"index\":18,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"datetime\"},{\"name\":\"update_time\",\"value\":\"2017-09-19 14:30:48\",\"update\":true,\"sqlType\":93,\"index\":19,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"timestamp\"},{\"name\":\"create_user\",\"value\":\"system\",\"update\":false,\"sqlType\":12,\"index\":20,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(20)\"},{\"name\":\"update_user\",\"value\":\"liumengmeng5\",\"update\":true,\"sqlType\":12,\"index\":21,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"varchar(20)\"},{\"name\":\"partition_time\",\"value\":\"2017-09-01 00:00:00\",\"update\":false,\"sqlType\":93,\"index\":22,\"length\":0,\"null\":false,\"key\":true,\"mysqlType\":\"datetime\"},{\"name\":\"ts_m\",\"value\":\"1505802648336\",\"update\":true,\"sqlType\":-5,\"index\":23,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"bigint(20)\"},{\"name\":\"yn\",\"value\":\"1\",\"update\":false,\"sqlType\":-6,\"index\":24,\"length\":0,\"null\":false,\"key\":false,\"mysqlType\":\"tinyint(1)\"},{\"name\":\"transbill_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":25,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"actual_collection\",\"value\":\"\",\"update\":false,\"sqlType\":3,\"index\":26,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"decimal(18,4)\"},{\"name\":\"collection_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":27,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"transbill_org_id\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":28,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"},{\"name\":\"carrier_name\",\"value\":\"\",\"update\":false,\"sqlType\":12,\"index\":29,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"varchar(30)\"},{\"name\":\"carrier_type\",\"value\":\"\",\"update\":false,\"sqlType\":4,\"index\":30,\"length\":0,\"null\":true,\"key\":false,\"mysqlType\":\"int(11)\"}],\"businessId\":\"61169925232\"}";
		CanalEvent<TransbillM> obj = CanalHelper.parseCanalMsg(canalText, TransbillM.class);
		System.err.println(JsonHelper.toJson(obj.getDataAfter()));
	}
	
	static class ClassP{
		private String pid;

		/**
		 * @return the pid
		 */
		public String getPid() {
			return pid;
		}

		/**
		 * @param pid the pid to set
		 */
		public void setPid(String pid) {
			this.pid = pid;
		}
		
	}
	static class ClassC extends ClassP{
		private String cid;

		/**
		 * @return the cid
		 */
		public String getCid() {
			return cid;
		}

		/**
		 * @param cid the cid to set
		 */
		public void setCid(String cid) {
			this.cid = cid;
		}
		
	}
}
