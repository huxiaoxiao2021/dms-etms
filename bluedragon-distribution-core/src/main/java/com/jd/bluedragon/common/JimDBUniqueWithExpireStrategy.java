package com.jd.bluedragon.common;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.coo.sa.sn.unique.UniqueStrategy;
import com.jd.coo.sa.utils.ByteUtils;
import com.jd.jim.cli.Cluster;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/7/11 17:24
 * @Description
 */
public class JimDBUniqueWithExpireStrategy implements UniqueStrategy {

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    private final Cluster jimClient;
    private final String prefix;
    private final Integer timeoutDays;


    public JimDBUniqueWithExpireStrategy(Cluster jimClient, String prefix, Integer timeoutDays) {
        this.jimClient = jimClient;
        this.prefix = prefix;
        this.timeoutDays = timeoutDays;
    }


    @Override
    public boolean isUnique(String sn) {
        System.out.println(sn);
        byte[] keyBytes = ByteUtils.toByteArray(this.prefix, sn);
        byte[] timeBytes = ByteUtils.toByteArray(System.currentTimeMillis());
        return this.jimClient.set(keyBytes, timeBytes, timeoutDays, TimeUnit.DAYS, false);
    }
}
