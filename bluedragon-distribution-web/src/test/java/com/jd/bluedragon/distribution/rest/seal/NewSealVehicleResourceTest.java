package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.seal.service.CarLicenseChangeUtil;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/2/13 18:32
 */
@RunWith(MockitoJUnitRunner.class)
public class NewSealVehicleResourceTest {

    @InjectMocks
    private NewSealVehicleResource newSealVehicleResource;

    @Mock
    private NewSealVehicleService newsealVehicleService;

    @Mock
    private CarLicenseChangeUtil carLicenseChangeUtil;


    private NewSealVehicleRequest request;

    @Before
    public void before(){
        request = new NewSealVehicleRequest();
        request.setStartSiteId("910");
        request.setEndSiteId("39");
        request.setVehicleNumber("551AD35622");
        request.setBatchCode("910-39-20200123110648024");
        request.setStatus(20);
        request.setSealCode("2002258072C");
        request.setTransportCode("R1905151949054");
        request.setPageNums(20);
    }

    @Test
    public void findSealInfo(){
        try {
            CommonDto<PageDto<SealCarDto>> returnCommonDto =new CommonDto<PageDto<SealCarDto>>();
            returnCommonDto.setCode(InvokeResult.SERVER_ERROR_CODE);
            returnCommonDto.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            Mockito.when(carLicenseChangeUtil.formateLicense2Chinese(Mockito.anyString())).thenReturn("551AD35622");
            Mockito.when(newsealVehicleService.findSealInfo(Matchers.<SealCarDto>any(), Matchers.<PageDto<SealCarDto>>any()))
                    .thenReturn(new CommonDto<PageDto<SealCarDto>>());
            NewSealVehicleResponse response = newSealVehicleResource.findSealInfo(request);
            Assert.assertEquals(NewSealVehicleResponse.CODE_EXCUTE_ERROR,response.getCode());
        }catch (Exception e){
            Assert.assertTrue(Boolean.FALSE);
        }
    }
}
