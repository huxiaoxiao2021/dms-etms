package com.jd.bluedragon.distribution.test.waybill;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName WaybillQueryManagerTest
 * @date 2019/3/6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class WaybillQueryManagerTest {

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Test
    public void testGetDataByChoice() throws InterruptedException {
        final CountDownLatch count = new CountDownLatch(10);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String waybillCode = "JDVA00001529046";
                    WChoice wChoice = new WChoice();
                    wChoice.setQueryWaybillC(true);
                    wChoice.setQueryWaybillE(true);
                    wChoice.setQueryPackList(true);
                    wChoice.setQueryGoodList(true);
                    for (int i = 0, size = 1000; i < size; i++) {
                        String sign = String.valueOf(i);
                        if (sign.length() == 1) {
                            sign = "00" + sign;
                        } else if (sign.length() == 2) {
                            sign = "0" + sign;
                        }
                        BaseEntity<BigWaybillDto> baseEntity1 = waybillQueryManager.getDataByChoice(waybillCode + sign, wChoice);
                        if (baseEntity1.getData().getPackageList() != null) {
                            System.out.println(i + " --- 成功获取包裹信息，包裹大小为：" + baseEntity1.getData().getPackageList().size());
                        } else {
                            System.out.println(i + " --- 获取信息为null");
                        }
                    }
                    count.countDown();
                }
            });
        }
        count.await();
    }
}
