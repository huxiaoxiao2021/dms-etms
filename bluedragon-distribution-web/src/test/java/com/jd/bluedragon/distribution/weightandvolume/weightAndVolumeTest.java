package com.jd.bluedragon.distribution.weightandvolume;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BusinessFinanceManager;
import com.jd.bluedragon.core.base.QuoteCustomerApiServiceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl.WeightAndVolumeCheckServiceImpl;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2019/11/20 18:27
 */
@RunWith(MockitoJUnitRunner.class)
public class weightAndVolumeTest {

    @InjectMocks
    public WeightAndVolumeCheckServiceImpl weightAndVolumeCheckService;

    @Mock
    private BusinessFinanceManager businessFinanceManager;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private QuoteCustomerApiServiceManager quoteCustomerApiServiceManager;

    @Mock
    private ReportExternalService reportExternalService;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    private PackWeightVO packWeightVO;

    private final Integer number = new Integer(8200);

    @Before
    public void initMocks() throws Exception{
        packWeightVO = new PackWeightVO();
        packWeightVO.setHigh(19.2);
        packWeightVO.setLength(28.9);
        packWeightVO.setWidth(20.3);
        packWeightVO.setWeight(1.13);
        packWeightVO.setCodeStr("JDVB01759364382-1-1-");
        packWeightVO.setOperatorSiteCode(11111);
        packWeightVO.setOperatorSiteName("北京亚一分拣中心");
        packWeightVO.setOrganizationCode(22222);
        packWeightVO.setOrganizationName("武汉亚一分拣中心");
        packWeightVO.setErpCode("lijie123");

        ResponseDTO<BizDutyDTO> responseDto = new ResponseDTO<>();
        responseDto.setStatusCode(0);
        BizDutyDTO bizDutyDTO = new BizDutyDTO();
        responseDto.setData(bizDutyDTO);
        bizDutyDTO.setFirstLevelId("11111");
        bizDutyDTO.setFirstLevelName("第一级别名称");
        bizDutyDTO.setSecondLevelId("22222");
        bizDutyDTO.setSecondLevelName("第二级别名称");
        bizDutyDTO.setThreeLevelId("33333");
        bizDutyDTO.setThreeLevelId("第三级别名称");
        bizDutyDTO.setDutyErp("lijie");
        bizDutyDTO.setWeight(new BigDecimal(1.7));
        bizDutyDTO.setVolume(new BigDecimal(10000));
        bizDutyDTO.setDutyType(111);
        bizDutyDTO.setBusinessObjectId(111);
        bizDutyDTO.setBusinessObject("商家对象");

        BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
        dto.setSiteName("通州分拣中心");



        when(businessFinanceManager.queryDutyInfo(anyString())).thenReturn(responseDto);
        when(baseMajorManager.getBaseStaffByErpNoCache(anyString())).thenReturn(dto);
        when(quoteCustomerApiServiceManager.queryVolumeRateByCustomerId(anyInt())).thenReturn(number);
        when(quoteCustomerApiServiceManager.queryVolumeRateByCustomerId(null)).thenReturn(number);
        when(reportExternalService.insertOrUpdateForWeightVolume(any(WeightVolumeCollectDto.class))).thenReturn(new BaseEntity<String>());
    }

    @Test
    public void testInsertAndSendMQ(){
        weightAndVolumeCheckService.dealSportCheck(packWeightVO, SpotCheckSourceEnum.SPOT_CHECK_CLIENT_PLATE,new InvokeResult<Boolean>());
    }


}
