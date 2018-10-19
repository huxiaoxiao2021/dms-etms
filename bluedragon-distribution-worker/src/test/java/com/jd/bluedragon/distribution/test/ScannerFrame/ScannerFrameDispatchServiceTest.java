package com.jd.bluedragon.distribution.test.ScannerFrame;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.auto.dao.ScannerFrameBatchSendDao;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameDispatchService;
import com.jd.bluedragon.distribution.test.BaseJunitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzuxiang on 2018/10/17.
 */
public class ScannerFrameDispatchServiceTest extends BaseJunitTest {

    //分拣机自动发货任务消息体集合
    private List<String> sortMachineTaskBodys = new ArrayList<String>();

    @Autowired
    private ScannerFrameDispatchService service;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private ScannerFrameBatchSendDao scannerFrameBatchSendDao;

    @Before
    public void before() {

        //未开启封车自动换批次的开关
        String sortMachineTaskBody1 = "{ \"barCode\" : \"VA00040479084-1-8-24\", \"registerNo\" : \"test006\", \"scannerTime\" : " + (System.currentTimeMillis()+ 1000) + ", \"source\" : 2, \"packageCode\" : \"VA00040479084-1-8-24\", \"chuteCode\" : \"826\", \"distributeId\" : 910, \"boxSiteCode\" : 22, \"sendSiteCode\" : 22, \"operatorId\" : 20546396, \"operatorName\" : \"颜廷峰\" }\n";

        //开启封车自动换批次开关，但未封车 目的站点 142780
        String sortMachineTaskBody2 = "{ \"barCode\" : \"41017153337-1-1-24\", \"registerNo\" : \"JD-TZ-001\", \"scannerTime\" : " + (System.currentTimeMillis()+ 2000) + ", \"source\" : 2, \"packageCode\" : \"41017153337-1-1-24\", \"chuteCode\" : \"665\", \"distributeId\" : 364605, \"boxSiteCode\" : 142780, \"sendSiteCode\" : 142780, \"operatorId\" : 20546396, \"operatorName\" : \"颜廷峰\" }\n";

        //开启封车自动换批次开关，未操作封车 目的站点 121674
        String sortMachineTaskBody3 = "{ \"barCode\" : \"41017153219-1-1-3\", \"registerNo\" : \"JD-TZ-001\", \"scannerTime\" : " + (System.currentTimeMillis()+ 3000) + ", \"source\" : 2, \"packageCode\" : \"41017153219-1-1-3\", \"chuteCode\" : \"711\", \"distributeId\" : 364605, \"boxSiteCode\" : 121674, \"sendSiteCode\" : 121674, \"operatorId\" : 20546396, \"operatorName\" : \"颜廷峰\" }\n";

        //开启封车自动换批次开关，已操作封车 目的站点 121674
        String sortMachineTaskBody4 = "{ \"barCode\" : \"56290032034-1-1-3\", \"registerNo\" : \"JD-TZ-001\", \"scannerTime\" : " + (System.currentTimeMillis()+ 4000) + ", \"source\" : 2, \"packageCode\" : \"56290032034-1-1-3\", \"chuteCode\" : \"711\", \"distributeId\" : 364605, \"boxSiteCode\" : 121674, \"sendSiteCode\" : 121674, \"operatorId\" : 20546396, \"operatorName\" : \"颜廷峰\" }\n";


        sortMachineTaskBodys.add(sortMachineTaskBody1);
        sortMachineTaskBodys.add(sortMachineTaskBody2);
        sortMachineTaskBodys.add(sortMachineTaskBody3);
        sortMachineTaskBodys.add(sortMachineTaskBody4);
    }

    @Test
    public void dispatchTest() throws Exception {
        for (int i = 0;i < sortMachineTaskBodys.size();i++){
            System.out.println(sortMachineTaskBodys.get(i));

            UploadData data = JsonHelper.fromJson(sortMachineTaskBodys.get(i), UploadData.class);
            service.dispatch(data);

            if (i == sortMachineTaskBodys.size()-2) {
                //在最后一件之前操作模拟封车使其触发封车自动换批次动作
                ScannerFrameBatchSend batchSend = scannerFrameBatchSendDao.selectCurrentBatchSend(data.getRegisterNo(), data.getSendSiteCode(), data.getScannerTime());
                String sendCode = batchSend.getSendCode();
                redisManager.setex(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + sendCode, Constants.TIME_SECONDS_ONE_WEEK, String.valueOf(data.getScannerTime().getTime()+500));

            }
        }
    }
}
