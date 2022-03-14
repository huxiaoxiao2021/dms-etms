package com.jd.bluedragon.distribution.test.listener;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumer.schedule.B2bWaybillScheduleMqListener;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
* B2B-TMS企配仓运单调度结果
*@author wengguoqi
*@date 2022/3/11 10:45
*/
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class B2bWaybillScheduleMqListenerTestCase {
	@InjectMocks
	B2bWaybillScheduleMqListener b2bWaybillScheduleMqListener;
    @Mock
    private DmsScheduleInfoService dmsScheduleInfoService;
    @Mock
    private BaseMajorManager baseMajorManager;
	
	public static void main(String[] args) throws Exception{

	}


	/**
	 *
	 * B2B-TMS企配仓运单调度结果
	 * @throws Exception
	 */
    @Test
    public void testConsume() throws Exception{
    	Message message = new Message();
    	String text = "{\"beginNodeCode\":\"010A210\"," +
				"\"boxCount\":1," +
				"\"carrierName\":\"小松鼠合伙人\"," +
				"\"scheduleTime\":1627014451000," +
				"\"transJobCode\":\"TJ21072300404909\"," +
				"\"waybillCode\":\"JD0003359302748\"" +
				"}\t";
    	message.setText(text);
    	b2bWaybillScheduleMqListener.consume(message);
    	when(dmsScheduleInfoService.syncScheduleInfoToDb(Mockito.any(DmsScheduleInfo.class))).thenReturn(true);
	}


	@Test
	public void getBaseSiteBySiteId(){
		BaseStaffSiteOrgDto orgDto = baseMajorManager.getBaseSiteByDmsCode("010A210");
		log.info("orgDto:{}",JSONObject.toJSONString(orgDto));

		BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(1362614);
		log.info("baseStaffSiteOrgDto:{}", JSONObject.toJSONString(baseStaffSiteOrgDto));
	}


}
