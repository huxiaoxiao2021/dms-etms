package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class WaybillPackageManagerImplTest extends TestCase {

    @Autowired
    private WaybillPackageManagerImpl waybillPackageManager;

    private static final ExecutorService executorParalleGetPackage = Executors.newFixedThreadPool(8);

    @Test
    public void testGetPackageByWaybillCodeParalle() throws Exception{
        Future<BaseEntity<List<DeliveryPackageD>>> future1 = executorParalleGetPackage.submit(new Callable<BaseEntity<List<DeliveryPackageD>>>() {
            @Override
            public BaseEntity<List<DeliveryPackageD>> call() throws Exception {
                return waybillPackageManager.getPackageByWaybillCodeParallel("JDV000701837919");
            }
        });
        Future<BaseEntity<List<DeliveryPackageD>>> future2 = executorParalleGetPackage.submit(new Callable<BaseEntity<List<DeliveryPackageD>>>() {
            @Override
            public BaseEntity<List<DeliveryPackageD>> call() throws Exception {
                return waybillPackageManager.getPackageByWaybillCodeParallel("JDVA00193428907");
            }
        });
        Future<BaseEntity<List<DeliveryPackageD>>> future3 = executorParalleGetPackage.submit(new Callable<BaseEntity<List<DeliveryPackageD>>>() {
            @Override
            public BaseEntity<List<DeliveryPackageD>> call() throws Exception {
                return waybillPackageManager.getPackageByWaybillCodeParallel("JDVA00193433874");
            }
        });
        Future<BaseEntity<List<DeliveryPackageD>>> future4 = executorParalleGetPackage.submit(new Callable<BaseEntity<List<DeliveryPackageD>>>() {
            @Override
            public BaseEntity<List<DeliveryPackageD>> call() throws Exception {
                return waybillPackageManager.getPackageByWaybillCodeParallel("JDVA00193428941");
            }
        });

        Assert.assertEquals(future1.get().getData().size(),24);
        Assert.assertEquals(future2.get().getData().size(),40);
        Assert.assertEquals(future3.get().getData().size(),40);
        Assert.assertEquals(future4.get().getData().size(),40);

        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackageByWaybillCodeDefault("JDV000701837919");

    }

    @Test
    public void testgetPackageByWaybillCodeDefault() {
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackageByWaybillCodeDefault("JDV000701837919");
        Assert.assertEquals(baseEntity.getData().size(),24);
    }
}