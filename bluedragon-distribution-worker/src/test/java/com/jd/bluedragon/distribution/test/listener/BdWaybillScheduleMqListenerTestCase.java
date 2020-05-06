package com.jd.bluedragon.distribution.test.listener;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumer.schedule.BdWaybillScheduleMqListener;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.jmq.common.message.Message;
/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandlerTestCase
 * @Description: 打印业务-收、寄件人信息处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BdWaybillScheduleMqListenerTestCase {
	@InjectMocks
	BdWaybillScheduleMqListener bdWaybillScheduleMqListener;
    @Mock
    private DmsScheduleInfoService dmsScheduleInfoService;
    @Mock
    private BaseMajorManager baseMajorManager;
	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testConsume() throws Exception{
    	Message message = new Message();
    	message.setText("{}");
    	bdWaybillScheduleMqListener.consume(message);
    	when(dmsScheduleInfoService.syncScheduleInfoToDb(Mockito.any(DmsScheduleInfo.class))).thenReturn(true);
	}
}
