package rma;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SmsMessageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumer.send.SendDetailConsumer;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.distribution.sms.domain.SMSDto;
import com.jd.bluedragon.distribution.sms.service.SmsConfigService;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>
 * Created by lixin39 on 2018/9/24.
 */
@RunWith(MockitoJUnitRunner.class)
public class RmaConsumerTest {

    @InjectMocks
    private SendDetailConsumer sendDetailConsumer;

    @Mock
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private GantryExceptionService gantryExceptionService;

    @Mock
    private SmsMessageManager smsMessageManager;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private SmsConfigService smsConfigService;

    @Mock
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Mock
    @Qualifier("dmsToVendor")
    private DefaultJMQProducer dmsToVendor;

    @Mock
    @Qualifier("dmsColdChainSendWaybill")
    private DefaultJMQProducer dmsColdChainSendWaybill;

    @Before
    public void before() throws JMQException {
        DefaultJMQProducer mq1 = Mockito.mock(dmsToVendor.getClass());
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return "called with arguments: " + args;
            }
        }).when(mq1).sendOnFailPersistent(Mockito.anyString(),Mockito.anyString());

        DefaultJMQProducer mq2 = Mockito.mock(dmsColdChainSendWaybill.getClass());
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return "called with arguments: " + args;
            }
        }).when(mq2).send(Mockito.anyString(),Mockito.anyString());

    }

    @Test
    public void testConsumer(){
        try {
            Mockito.when(uccPropertyConfiguration.isColdChainStorageSmsSwitch())
                    .thenReturn(Boolean.TRUE);
            BaseEntity<BigWaybillDto> baseEntity = new BaseEntity<BigWaybillDto>();
            BigWaybillDto bigWaybillDto = new BigWaybillDto();
            Waybill waybill = new Waybill();
            waybill.setWaybillCode("JDV000050847830");
            waybill.setWaybillSign("30001000171000000000000000000002000000020002030000002202010000000000000000000027000000000000100000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
            waybill.setOldSiteId(910);
            waybill.setReceiverMobile("110");
            bigWaybillDto.setWaybill(waybill);
            baseEntity.setData(bigWaybillDto);
            Mockito.when(waybillQueryManager.getDataByChoice(Mockito.anyString(),Mockito.any(WChoice.class)))
                    .thenReturn(baseEntity);
            Mockito.when(rmaHandOverWaybillService.buildAndStorage(Mockito.any(SendDetailMessage.class),Mockito.any(Waybill.class),Mockito.<Goods>anyList()))
                    .thenReturn(Boolean.FALSE);
            BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
            dto.setDmsId(910);
            dto.setOrgId(3);
            Mockito.when(baseMajorManager.getBaseSiteBySiteId(Mockito.anyInt()))
                    .thenReturn(dto);
            Mockito.when(gantryExceptionService.updateSendStatus(Mockito.anyString(),Mockito.anyLong()))
                    .thenReturn(1);
            Mockito.when(smsMessageManager.sendSmsTemplateMessage(Mockito.anyString(),Mockito.anyLong(),Mockito.any(String[].class),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
                    .thenReturn(new InvokeResult());
            SMSDto sMSDto = new SMSDto();
            sMSDto.setAccount("test");
            sMSDto.setTemplateId(1l);
            sMSDto.setToken("test");
            Mockito.when(smsConfigService.getSMSConstantsByOrgId(Mockito.anyInt()))
                    .thenReturn(sMSDto);

            Message message = new Message();
            message.setText("{\"boxCode\":\"JDV000050847830-1-5-\",\"createSiteCode\":910,\"createUser\":\"邢松\",\"createUserCode\":100531,\"operateTime\":1582594542000,\"packageBarcode\":\"JDV000050847830-1-5-\",\"receiveSiteCode\":39,\"sendCode\":\"910-39-20200225094934011\",\"source\":\"DMS\"}");
            sendDetailConsumer.consume(message);
            Assert.assertTrue(Boolean.TRUE);
        }catch (Exception e){
            //异常直接断言失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }
}
