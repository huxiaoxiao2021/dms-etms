package com.jd.bluedragon.distribution.merchantWeightAndVolume.controller;

import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class MerchantWeightAndVolumeWhiteListControllerTest {
    @Mock
    Logger log;
    @Mock
    MerchantWeightAndVolumeWhiteListService merchantWeightAndVolumeWhiteListService;
    @Mock
    WaybillPackageManager waybillPackageManager;
    @Mock
    ExportConcurrencyLimitService exportConcurrencyLimitService;
    @Mock
    BaseMajorManager baseMajorManager;
    @InjectMocks
    MerchantWeightAndVolumeWhiteListController merchantWeightAndVolumeWhiteListController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public  void testToIndex() {
        String result = merchantWeightAndVolumeWhiteListController.toIndex(null);
        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    public void testListData() {
        when(merchantWeightAndVolumeWhiteListService.queryByCondition(any())).thenReturn(new PagerResult<MerchantWeightAndVolumeDetail>());

        PagerResult<MerchantWeightAndVolumeDetail> result = merchantWeightAndVolumeWhiteListController.listData(new MerchantWeightAndVolumeCondition());
        Assert.assertEquals(new PagerResult<MerchantWeightAndVolumeDetail>(), result);
    }

    @Test
    public  void testDelete() {
        when(merchantWeightAndVolumeWhiteListService.delete(any())).thenReturn(new InvokeResult<Integer>(0, "message", Integer.valueOf(0)));

        InvokeResult<Integer> result = merchantWeightAndVolumeWhiteListController.delete(new MerchantWeightAndVolumeDetail());
        Assert.assertEquals(new InvokeResult<Integer>(0, "message", Integer.valueOf(0)), result);
    }

    @Test
    public  void testToImport() {
        when(merchantWeightAndVolumeWhiteListService.checkExportData(any(), anyString())).thenReturn("checkExportDataResponse");

        JdResponse result = merchantWeightAndVolumeWhiteListController.toImport(null);
        Assert.assertEquals(new JdResponse(Integer.valueOf(0), "message", "data"), result);
    }

    @Test
    public void testToImportVolume() throws Exception{
        File flie = new File("/Users/xumigen/Downloads/南京散货dws称重刷运单使用v3_20231027170712_xumigen_保密信息_请勿外传.csv");

        FileInputStream fileInputStream = new FileInputStream(flie);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("南京散货dws称重刷运单使用v3_20231027170712_xumigen_保密信息_请勿外传", "南京散货dws称重刷运单使用v3_20231027170712_xumigen_保密信息_请勿外传.csv", "content/type", fileInputStream);
        JdResponse result = merchantWeightAndVolumeWhiteListController.toImportVolume(mockMultipartFile);
        Assert.assertEquals(JdResponse.CODE_SUCCESS, result.getCode());
    }

    @Test
    public void testToExport() {
        InvokeResult result = merchantWeightAndVolumeWhiteListController.toExport(new MerchantWeightAndVolumeCondition(), null);
        Assert.assertEquals(new InvokeResult(0, "message", "data"), result);
    }

    @Test
    public void testGetLoginUser() {
        when(baseMajorManager.getBaseStaffByErpNoCache(anyString())).thenReturn(null);

        LoginUser result = merchantWeightAndVolumeWhiteListController.getLoginUser();
        Assert.assertEquals(new LoginUser(), result);
    }

    @Test
    public void testSetBaseModelInfo() {
        when(baseMajorManager.getBaseStaffByErpNoCache(anyString())).thenReturn(null);

        merchantWeightAndVolumeWhiteListController.setBaseModelInfo(null);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: https://weirddev.com/forum#!/testme