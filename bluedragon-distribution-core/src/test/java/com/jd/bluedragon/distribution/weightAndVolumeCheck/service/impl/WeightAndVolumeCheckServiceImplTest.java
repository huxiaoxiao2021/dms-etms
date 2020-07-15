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
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.dms.receive.enums.VolumeFeeType;
import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.jss.JingdongStorageService;
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
import org.slf4j.Logger;

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
    Logger log;
    @Mock
    JingdongStorageService dmsWebJingdongStorageService;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInsertAndSendMq() throws Exception {
        when(dmsBaseDictService.queryListByParentId(anyInt())).thenReturn(Arrays.<DmsBaseDict>asList(new DmsBaseDict()));

        ResponseDTO<BizDutyDTO> responseDto = new ResponseDTO<>();
        BizDutyDTO bizDutyDTO = new BizDutyDTO();
        bizDutyDTO.setWeight(new BigDecimal(1.0));
        bizDutyDTO.setVolume(new BigDecimal(8000.0));
        responseDto.setStatusCode(0);
        responseDto.setData(bizDutyDTO);

        when(businessFinanceManager.queryDutyInfo(anyString())).thenReturn(responseDto);

        when(baseMajorManager.getBaseSiteBySiteId(anyInt())).thenReturn(null);
        when(baseMajorManager.getBaseStaffByErpNoCache(anyString())).thenReturn(null);
        when(waybillQueryManager.getDataByChoice(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(null);
        when(waybillQueryManager.getOnlyWaybillByWaybillCode(anyString())).thenReturn(null);
        when(dmsWeightVolumeAbnormal.getTopic()).thenReturn("dms_weightVolume_abnormal");
        when(dmsWeightVolumeExcess.getTopic()).thenReturn("ldop_abnormal_fail");


        QuoteCustomerDto quoteCustomerDto = new QuoteCustomerDto();
        quoteCustomerDto.setVolumeFeeType(8000);
        quoteCustomerDto.setVolumeFeeType(VolumeFeeType.volumeWeight.getType());
        when(quoteCustomerApiServiceManager.queryCustomerById(anyInt())).thenReturn(quoteCustomerDto);

        PackWeightVO packWeightVO = new PackWeightVO();
        packWeightVO.setWeight(10.0);
        packWeightVO.setHigh(20.0);
        packWeightVO.setLength(40.0);
        packWeightVO.setWidth(20.0);
        packWeightVO.setCodeStr("JDVC03992440423");
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        weightVolumeCollectDto.setBusiCode(123);
        InvokeResult<Boolean> result = weightAndVolumeCheckServiceImpl.insertAndSendMq(packWeightVO,weightVolumeCollectDto,new InvokeResult<Boolean>());
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