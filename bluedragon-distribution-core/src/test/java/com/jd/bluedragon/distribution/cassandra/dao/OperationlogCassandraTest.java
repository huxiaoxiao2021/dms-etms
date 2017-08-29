package com.jd.bluedragon.distribution.cassandra.dao;

import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.offline.service.OfflineSortingService;
import com.jd.bluedragon.distribution.operationLog.dao.OperationlogCassandra;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-cassandra.xml"})
public class OperationlogCassandraTest  extends AbstractJUnit4SpringContextTests{

    @Autowired
    protected ApplicationContext ctx;

    @Autowired
    private OperationlogCassandra logCassandra;


    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add() {
        String log = "{\"logId\":null,\"boxCode\":\"BC010F002010F00200002080\",\"waybillCode\":\"VA00010729040\",\"pickupCode\":null,\"packageCode\":\"VA00010729040-1-1-\",\"orgCode\":null,\"logType\":120,\"sendCode\":null,\"createUserCode\":10053,\"createUser\":\"邢松\",\"createSiteCode\":910,\"createSiteName\":\"北京马驹桥分拣中心\",\"receiveSiteCode\":10115,\"receiveSiteName\":\"北京98号库\",\"remark\":\"不是\",\"operateTime\":\"2017-08-14 16:50:42\",\"createTime\":\"2017-08-15 21:58:04\",\"updateTime\":null,\"yn\":null}";
        try {
            OperationLog operationLog = JsonHelper.fromJson(log, OperationLog.class);
            logCassandra.batchInsert(operationLog);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
