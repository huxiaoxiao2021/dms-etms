package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.PackageWeightVolumeHandler;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/5/7 16:00
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageWeightVolumeHandlerTest {

    @InjectMocks
    private PackageWeightVolumeHandler packageWeightVolumeHandler;

    @Mock
    private SiteService siteService;

    @Mock
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Mock
    WaybillTraceManager waybillTraceManager;

    @Mock
    @Qualifier("waybillCommonService")
    private WaybillCommonService waybillCommonService;

    private InvokeResult<Boolean> booleanResult;

    private InvokeResult<Integer> intResult;

    private List<PackageStateDto> packageStateDtos;

    private BaseStaffSiteOrgDto site;

    @Before
    public void before(){

        booleanResult = new InvokeResult<Boolean>();
        booleanResult.setData(false);

        intResult = new InvokeResult<Integer>();
        intResult.setData(1);

        packageStateDtos = new ArrayList<>();
        packageStateDtos.add(new PackageStateDto());
        packageStateDtos.add(new PackageStateDto());

        site = new BaseStaffSiteOrgDto();
        site.setOrgId(6);
        site.setOrgName("华北区");

        when(weightAndVolumeCheckService.dealSportCheck(any(PackWeightVO.class),
                SpotCheckSourceEnum.SPOT_CHECK_CLIENT_PLATE,any(InvokeResult.class))).thenReturn(booleanResult);

        when(waybillCommonService.getPackNum(any(String.class))).thenReturn(intResult);

        when(waybillTraceManager.getPkStateDtoByWCodeAndState(any(String.class),any(String.class))).thenReturn(packageStateDtos);

        when(siteService.getSite(anyInt())).thenReturn(site);
    }

    //私有方法测试
    @Test
    public void handlerWeighVolumeTest(){
        WeightVolumeEntity entity = new WeightVolumeEntity();
        entity.setBarCode("JDV000075645467-1-5-");
        entity.setWeight(1.5);
        entity.setLength(20d);
        entity.setWidth(20d);
        entity.setHeight(20d);
        entity.setVolume(8000d);
        entity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE);
        entity.setSourceCode(FromSourceEnum.DMS_AUTOMATIC_MEASURE);
        entity.setOperateSiteCode(910);
        entity.setOperateSiteName("北京马驹桥分拣中心");
        entity.setOperatorCode("bjxings");
        entity.setOperatorId(123456);
        entity.setOperatorName("邢松");
        entity.setOperateTime(new Date());
        PackageWeightVolumeHandler handler = packageWeightVolumeHandler;
        Class c = handler.getClass();
        try{
            Method handlerWeighVolume = c.getDeclaredMethod("handlerWeighVolume",WeightVolumeEntity.class);
            handlerWeighVolume.setAccessible(true);
            handlerWeighVolume.invoke(handler,entity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
