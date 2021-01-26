package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BusinessFinanceManager;
import com.jd.bluedragon.core.base.QuoteCustomerApiServiceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.dms.receive.enums.VolumeFeeType;
import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/5/28 17:46
 */
public class WeightAndVolumeCheckServiceImplTest {
    @Mock
    ReportExternalService reportExternalService;
    @Mock
    DmsBaseDictService dmsBaseDictService;
    @Mock
    BusinessFinanceManager businessFinanceManager;
    @Mock
    BaseMajorManager baseMajorManager;
    @Mock
    WaybillQueryManager waybillQueryManager;
    @Mock
    DefaultJMQProducer dmsWeightVolumeAbnormal;
    @Mock
    DefaultJMQProducer dmsWeightVolumeExcess;
    @Mock
    QuoteCustomerApiServiceManager quoteCustomerApiServiceManager;

    @InjectMocks
    WeightAndVolumeCheckServiceImpl weightAndVolumeCheckServiceImpl;

    @InjectMocks
    WeightAndVolumeCheckAHandler weightAndVolumeCheckAHandler;

    @InjectMocks
    WeightAndVolumeCheckBHandler weightAndVolumeCheckBHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInsertAndSendMq() throws Exception {
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler, "firstThresholdWeight", 1.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"firstStage",0.5);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"secondThresholdWeight",20.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"secondStage",1.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"thirdThresholdWeight",50.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"thirdStage",0.02);
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"firstSumLWH","100");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"secondSumLWH","120");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"thirdSumLWH","200");
        ReflectionTestUtils.setField(weightAndVolumeCheckServiceImpl,"fourSumLWH","70");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"firstSumLWHStage","1");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"secondSumLWHStage","1.5");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"thirdSumLWHStage","2");
        ReflectionTestUtils.setField(weightAndVolumeCheckAHandler,"fourSumLWHStage","0.8");

        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler, "firstThresholdWeight", 1.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"firstStage",0.5);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"secondThresholdWeight",20.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"secondStage",1.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"thirdThresholdWeight",50.0);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"thirdStage",0.02);
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"firstSumLWH","100");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"secondSumLWH","120");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"thirdSumLWH","200");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"fourSumLWH","70");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"firstSumLWHStage","1");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"secondSumLWHStage","1.5");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"thirdSumLWHStage","2");
        ReflectionTestUtils.setField(weightAndVolumeCheckBHandler,"fourSumLWHStage","0.8");

        ReflectionTestUtils.setField(weightAndVolumeCheckServiceImpl,"weightAndVolumeCheckAHandler",weightAndVolumeCheckAHandler);
        ReflectionTestUtils.setField(weightAndVolumeCheckServiceImpl,"weightAndVolumeCheckBHandler",weightAndVolumeCheckBHandler);

        when(dmsBaseDictService.queryListByParentId(anyInt())).thenReturn(Arrays.<DmsBaseDict>asList(new DmsBaseDict()));

        ResponseDTO<BizDutyDTO> responseDto = new ResponseDTO<>();
        BizDutyDTO bizDutyDTO = new BizDutyDTO();
        bizDutyDTO.setWeight(new BigDecimal(1.0));
        bizDutyDTO.setVolume(new BigDecimal(8000.0));
        bizDutyDTO.setCalcWeight(new BigDecimal(10.1));
        responseDto.setStatusCode(0);
        responseDto.setData(bizDutyDTO);

        when(businessFinanceManager.queryDutyInfo(anyString())).thenReturn(responseDto);

        when(baseMajorManager.getBaseSiteBySiteId(anyInt())).thenReturn(null);
        when(baseMajorManager.getBaseStaffByErpNoCache(anyString())).thenReturn(null);
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = new com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto>();
        BigWaybillDto bigWaybillDto = new BigWaybillDto();
        Waybill waybill = new Waybill();
        waybill.setWaybillSign("300010020100000200000000202020020001000200020070110020000100000000000000000000120001000000301009000000000000");
        bigWaybillDto.setWaybill(waybill);
        baseEntity.setData(bigWaybillDto);
        when(waybillQueryManager.getDataByChoice(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(baseEntity);
        when(waybillQueryManager.getOnlyWaybillByWaybillCode(anyString())).thenReturn(null);
        when(dmsWeightVolumeAbnormal.getTopic()).thenReturn("dms_weightVolume_abnormal");
        when(dmsWeightVolumeExcess.getTopic()).thenReturn("ldop_abnormal_fail");

        PackWeightVO packWeightVO = new PackWeightVO();
        packWeightVO.setWeight(0.9);
        packWeightVO.setHigh(20.0);
        packWeightVO.setLength(30.0);
        packWeightVO.setWidth(20.0);
        packWeightVO.setCodeStr("JDVC03992440423");

        QuoteCustomerDto quoteCustomerDto = new QuoteCustomerDto();
        quoteCustomerDto.setVolumeFeeType(8000);
        quoteCustomerDto.setVolumeFeeType(VolumeFeeType.volumeWeight.getType());
        when(quoteCustomerApiServiceManager.queryCustomerById(anyInt())).thenReturn(quoteCustomerDto);

        when(waybillQueryManager.getOnlyWaybillByWaybillCode(anyString())).thenReturn(waybill);
        InvokeResult<Boolean> result = weightAndVolumeCheckServiceImpl.dealSportCheck(packWeightVO,
                SpotCheckSourceEnum.SPOT_CHECK_CLIENT_PLATE,new InvokeResult<Boolean>());
        Assert.assertTrue(result.getCode()!=200);
    }


    @Test
    public void testGetExportData() throws Exception {

        Pager<WeightVolumeQueryCondition> pager = new Pager<WeightVolumeQueryCondition>();
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        pager.setPageNo(1);
        pager.setPageSize(10);
        pager.setSearchVo(condition);
        BaseEntity<Pager<WeightVolumeCollectDto>> nextBaseEntity = new BaseEntity<Pager<WeightVolumeCollectDto>>();
        Pager<WeightVolumeCollectDto> resultPager = new Pager<WeightVolumeCollectDto>();
        List<WeightVolumeCollectDto> list = new ArrayList<>();
        WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
        dto.setWaybillCode("JDVC03992440423");
        list.add(dto);
        resultPager.setTotal(10l);
        resultPager.setData(list);
        nextBaseEntity.setCode(200);
        nextBaseEntity.setData(resultPager);
        when(reportExternalService.getPagerByConditionForWeightVolume(pager)).thenReturn(nextBaseEntity);

        WeightAndVolumeCheckCondition condition1 = new WeightAndVolumeCheckCondition();
        List<List<Object>> result = weightAndVolumeCheckServiceImpl.getExportData(condition1);
        Assert.assertTrue(true);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme