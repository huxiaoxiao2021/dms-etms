package com.jd.bluedragon.distribution.businessCode.dao;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.coo.sa.sequence.*;
import com.jd.coo.sa.sequence.ha.BitwiseLoadBalanceSequenceGen;
import com.jd.coo.sa.sequence.snowflake.impl.PropertiesFileSnowflakeSequenceGen;
import com.jd.coo.sa.sn.GenContextItem;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.coo.sa.sn.expression.jexl.JexlInterpreter;
import com.jd.coo.sa.sn.unique.JimDBUniqueStrategy;
import com.jd.jim.cli.Cluster;
import com.jd.jim.cli.ReloadableJimClientFactoryBean;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.businessCode.dao
 * @ClassName: JQCodeGenTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 16:27
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class JQCodeGenTest {

    private SmartSNGen smartJQCodeSNGen;

    @Before
    public void before() throws Exception {
        ReloadableJimClientFactoryBean reloadableJimClientFactoryBean = new ReloadableJimClientFactoryBean();
        reloadableJimClientFactoryBean.setJimUrl("jim://2914173422341158041/110000259");

        Cluster cluster = (Cluster) reloadableJimClientFactoryBean.getObject();

        JimDBUniqueStrategy jimDBUniqueStrategy = new JimDBUniqueStrategy(cluster, "jq-code-gen-");

        String regex = "'JQ' + sequence";

        JimdbSequenceGen jimdbSequenceGen = new JimdbSequenceGen(cluster,"jq-code-gen-%s");
        JimdbSequenceGen jimdbSequenceGen1 = new JimdbSequenceGen(cluster,"jq-code-gen-%s");
        List<SequenceGen> sequenceGens = new ArrayList<>();
        sequenceGens.add(jimdbSequenceGen);
        sequenceGens.add(jimdbSequenceGen1);

        BitwiseLoadBalanceSequenceGen bitwiseLoadBalanceSequenceGen = new BitwiseLoadBalanceSequenceGen(sequenceGens);
        bitwiseLoadBalanceSequenceGen.setTimeoutThresholdInMilliseconds(100);
        bitwiseLoadBalanceSequenceGen.setTimeoutEventCountThreshold(3);
        bitwiseLoadBalanceSequenceGen.setOnErrorRescueThresholdInSeconds(10);
        QueuedSequenceGen queuedSequenceGen = new QueuedSequenceGen(bitwiseLoadBalanceSequenceGen);
        queuedSequenceGen.setMemoryBitLength(4);

        DateTimeBasedSequenceGen dateTimeBasedSequenceGen
            = new DateTimeBasedSequenceGen(queuedSequenceGen,"yyMMddHHmmss", 5);
        dateTimeBasedSequenceGen.setIgnoreDateTimePart(false);

        JexlInterpreter jexlInterpreter = new JexlInterpreter(dateTimeBasedSequenceGen);

        smartJQCodeSNGen = new SmartSNGen(regex,jimDBUniqueStrategy);
        smartJQCodeSNGen.setInterpreter(jexlInterpreter);

    }

    @Test
    public void JQCodeGen() {
        String[] sendCodes = smartJQCodeSNGen.batchGen("JQ_CODE", 1000);
        for (String sendCode : sendCodes) {
            System.out.println(sendCode);
        }

    }

}
