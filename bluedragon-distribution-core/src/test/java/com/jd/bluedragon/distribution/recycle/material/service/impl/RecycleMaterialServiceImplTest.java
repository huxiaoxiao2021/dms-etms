package com.jd.bluedragon.distribution.recycle.material.service.impl;

import com.jd.bluedragon.distribution.box.service.BoxServiceImpl;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecycleMaterialServiceImplTest {

    @InjectMocks
    private RecycleMaterialServiceImpl recycleMaterialService;
    @InjectMocks
    private BoxServiceImpl boxService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getPrintInfo() {
        RecycleBasketEntity recycleBasket = new RecycleBasketEntity();
        //首次 1 补打 2
        recycleBasket.setPrintType(1);
        recycleBasket.setQuantity(2);
        recycleMaterialService.getPrintInfo(recycleBasket);
    }
}
