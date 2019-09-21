package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.EcpQueryWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.tms.basic.dto.ConfNodeCarrierDto;
import com.jd.tms.ecp.dto.BasicRailTrainDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jd.bluedragon.distribution.transport.domain.ArTransportTypeEnum.RAILWAY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author : xumigen
 * @date : 2019/9/21
 */

@RunWith(MockitoJUnitRunner.class)
public class ArSendRegisterServiceImplTest {

    @InjectMocks
    private ArSendRegisterServiceImpl arSendRegisterService;

    @Mock
    private DmsBaseDictService dmsBaseDictService;

    @Mock
    private ArSendCodeService arSendCodeService;

    @Mock
    private EcpQueryWSManager ecpQueryWSManager;

    @Mock
    private BasicQueryWSManager basicQueryWSManager;

    @Mock
    private ArSendRegisterDao arSendRegisterDao;

    @Mock
    private TaskService taskService;

    @Mock
    private SendDatailDao sendDetailDao;

    @Mock
    private DefaultJMQProducer arSendReportMQ;

    private ArSendRegister arSendRegister;

    private String[] sendCodes = new String[2];

    @Before
    public void initMocks() throws Exception {
        //初始化当前测试类所有@Mock注解模拟对象  和 MockitoJUnitRunner 用一个就好
        //MockitoAnnotations.initMocks(this);

        arSendRegister = new ArSendRegister();
        arSendRegister.setSendRouterMqType(0);
        arSendRegister.setStatus(0);
        arSendRegister.setTransportType(0);
        arSendRegister.setOrderCode("");
        arSendRegister.setTransportName("");
        arSendRegister.setSiteOrder("");
        arSendRegister.setSendDate(new Date());
        arSendRegister.setTransCompanyCode("");
        arSendRegister.setTransCompany("");
        arSendRegister.setStartCityName("");
        arSendRegister.setStartCityId(0);
        arSendRegister.setEndCityName("");
        arSendRegister.setEndCityId(0);
        arSendRegister.setStartStationName("");
        arSendRegister.setStartStationId("");
        arSendRegister.setEndStationName("");
        arSendRegister.setEndStationId("");
        arSendRegister.setPlanStartTime(new Date());
        arSendRegister.setPlanEndTime(new Date());
        arSendRegister.setAging(0);
        arSendRegister.setSendNum(0);
        arSendRegister.setChargedWeight(new BigDecimal("0"));
        arSendRegister.setRemark("");
        arSendRegister.setShuttleBusType(0);
        arSendRegister.setShuttleBusNum("");
        arSendRegister.setOperatorErp("");
        arSendRegister.setOperatorId(0);
        arSendRegister.setOperatorName("");
        arSendRegister.setOperationDept("");
        arSendRegister.setOperationDeptCode(0);
        arSendRegister.setOperationTime(new Date());
        arSendRegister.setCreateUser("");
        arSendRegister.setUpdateUser("");
        arSendRegister.setSendCode("");
        arSendRegister.setOperateType(0);
        arSendRegister.setWaybillCode("");
        arSendRegister.setPackageCode("");
        arSendRegister.setGoodsType(0);
        arSendRegister.setGoodsTypeName("");
        arSendRegister.setId(0L);
        arSendRegister.setCreateTime(new Date());
        arSendRegister.setUpdateTime(new Date());
        arSendRegister.setIsDelete(0);
        arSendRegister.setTs(new Date());

        Map<Integer,String> goodsType = new HashMap<>();
        goodsType.put(1,"普货");
        goodsType.put(2,"生鲜");
        when(dmsBaseDictService.queryMapKeyTypeCodeByParentId(Constants.BASEDICT_GOODS_TYPE_PARENTID)).thenReturn(goodsType);
        when(arSendCodeService.batchAdd(Mockito.anyLong(),any(String[].class),Mockito.anyString())).thenReturn(Boolean.TRUE);
        BasicRailTrainDto railTrainDto = new BasicRailTrainDto();
        railTrainDto.setBeginNodeCode("12132");
        railTrainDto.setBeginNodeName("萨法");
        railTrainDto.setEndNodeCode("sfsdf");
        when(ecpQueryWSManager.getRailTrainListByCondition(anyString(),anyInt(),anyInt())).thenReturn(railTrainDto);
        ConfNodeCarrierDto confNodeCarrierDto = new ConfNodeCarrierDto();
        confNodeCarrierDto.setCarrierCode("1111111111111");
        when(basicQueryWSManager.getCarrierByNodeCode(anyString())).thenReturn(confNodeCarrierDto);
        when(arSendRegisterDao.insert(any(ArSendRegister.class))).thenReturn(true);
        when(taskService.add(any(Task.class),anyBoolean())).thenReturn(1);
        when(sendDetailDao.querySendDCountBySendCode(anyString())).thenReturn(1);
        arSendReportMQ = Mockito.mock(DefaultJMQProducer.class);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return "called with arguments: " + args;
            }
        }).when(arSendReportMQ).send(anyString(),anyString());
    }

    @Test
    public void testInsert(){

        arSendRegister.setTransportType(RAILWAY.getCode());
        arSendRegister.setGoodsType(1);
        arSendRegisterService.insert(arSendRegister,sendCodes);
    }

}
