package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.distribution.weightVolume.handler.BoxWeightVolumeHandler;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetErrorRes;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BoxWeightVolumeHandlerTest {
    @Mock
    private BoxWeightVolumeHandler boxWeightVolumeHandler;

    @Test
    public void retryOnFailDoRestInterfaceTest() throws Exception{
        EconomicNetResult<EconomicNetErrorRes> result = null;
        Object request = null;
        long startTime = 0;
        Exception e = null;

//        boxWeightVolumeHandler.retryOnFailDoRestInterface(result, null,null, startTime, e);

    }
}
