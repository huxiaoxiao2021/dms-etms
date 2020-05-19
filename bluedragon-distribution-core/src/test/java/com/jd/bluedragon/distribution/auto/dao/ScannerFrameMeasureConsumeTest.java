package com.jd.bluedragon.distribution.auto.dao;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameMeasureConsume;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/5/7 13:52
 */
@RunWith(MockitoJUnitRunner.class)
public class ScannerFrameMeasureConsumeTest {

    @InjectMocks
    private ScannerFrameMeasureConsume scannerFrameMeasureConsume;

    @Mock
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private UccPropertyConfiguration uccPropertyConfiguration;

    private BaseStaffSiteOrgDto dto;

    private InvokeResult<Boolean> result;


    @Before
    public void before(){

        dto = new BaseStaffSiteOrgDto();
        dto.setAccountNumber("bjxings");

        result = new InvokeResult<Boolean>();
        result.setData(true);

        when(uccPropertyConfiguration.getAutomaticWeightVolumeExchangeSwitch()).thenReturn(true);
        when(baseMajorManager.getBaseStaffByStaffIdNoCache(anyInt())).thenReturn(dto);
        when(dmsWeightVolumeService.dealWeightAndVolume(any(WeightVolumeEntity.class))).thenReturn(result);
    }

    @Test
    public void onMessageTest(){
        UploadData uploadData = new UploadData();
        uploadData.setBarCode("JDV000075645467-1-5-");
        uploadData.setLength(20f);
        uploadData.setWidth(20f);
        uploadData.setHeight(20f);
        uploadData.setWeight(1.5f);
        uploadData.setScannerTime(new Date());
        GantryDeviceConfig config = new GantryDeviceConfig();
        config.setCreateSiteCode(910);
        config.setCreateSiteName("北京马驹桥分拣中心");
        config.setOperateUserId(123456);
        config.setUpdateUserName("邢松");
        scannerFrameMeasureConsume.onMessage(uploadData,config);
    }


}
