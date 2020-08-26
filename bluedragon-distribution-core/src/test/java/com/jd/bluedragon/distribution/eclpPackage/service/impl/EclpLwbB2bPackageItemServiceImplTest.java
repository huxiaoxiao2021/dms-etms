package com.jd.bluedragon.distribution.eclpPackage.service.impl;

import com.jd.bluedragon.distribution.eclpPackage.manager.EclpLwbB2bPackageItemManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class EclpLwbB2bPackageItemServiceImplTest {
    @Mock
    EclpLwbB2bPackageItemManager eclpLwbB2bPackageItemManager;
    @InjectMocks
    EclpLwbB2bPackageItemServiceImpl eclpLwbB2bPackageItemServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindSellerPackageCode() throws Exception {
        when(eclpLwbB2bPackageItemManager.findSellerPackageCode(anyString())).thenReturn("JDV000000058367-1-3-");

        String result = eclpLwbB2bPackageItemServiceImpl.findSellerPackageCode("11111");
        Assert.assertEquals("JDV000000058367-1-3-", result);
    }
}
