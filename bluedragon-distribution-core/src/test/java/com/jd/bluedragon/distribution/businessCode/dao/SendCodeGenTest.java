package com.jd.bluedragon.distribution.businessCode.dao;

import com.jd.bluedragon.distribution.dao.common.SpringAppContextConfigPath;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.coo.sa.sequence.*;
import com.jd.coo.sa.sequence.ha.BitwiseLoadBalanceSequenceGen;
import com.jd.coo.sa.sequence.snowflake.SnowflakeSequenceGen;
import com.jd.coo.sa.sequence.snowflake.impl.PropertiesFileSnowflakeSequenceGen;
import com.jd.coo.sa.sn.GenContextItem;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.coo.sa.sn.expression.SmartInterpreter;
import com.jd.coo.sa.sn.expression.jexl.JexlInterpreter;
import com.jd.coo.sa.sn.unique.JimDBUniqueStrategy;
import com.jd.jim.cli.Cluster;
import com.jd.jim.cli.ReloadableJimClientFactoryBean;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * <p>
 *     <bean id="redisClient"  class="com.jd.jim.cli.ReloadableJimClientFactoryBean">
 *        <property name="jimUrl" value="jim://1803528818953446384/1"/>
 *     </bean>
 *
 * 	<bean id="jimDBUniqueStrategy" class="com.jd.coo.sa.sn.unique.JimDBUniqueStrategy">
 * 		<constructor-arg name="jimClient" ref="redisClient"/>
 * 		<constructor-arg name="prefix" value="sequence-unique-"/>
 * 	</bean>
 *
 * 	<bean id="smartSendCodeSNGen" class="com.jd.coo.sa.sn.SmartSNGen">
 * 		<constructor-arg name="expressionArg" value="createSiteCode + '-' + receiveSiteCode + '-' + currentTimeLong + u:leftPad(sequence,7)"/>
 * 		<constructor-arg name="uniqueStrategyArg" ref="jimDBUniqueStrategy"/>
 * 	</bean>
 *
 * @author zoothon
 * @since 2020/3/20
 **/
public class SendCodeGenTest {

    private SmartSNGen smartSendCodeSNGen;

    private SmartSNGen smartSNGen;

    @Before
    public void before() throws Exception {
        ReloadableJimClientFactoryBean reloadableJimClientFactoryBean = new ReloadableJimClientFactoryBean();
        reloadableJimClientFactoryBean.setJimUrl("jim://2581594387536019499/80000013");
        Cluster cluster = (Cluster) reloadableJimClientFactoryBean.getObject();

        JimDBUniqueStrategy jimDBUniqueStrategy = new JimDBUniqueStrategy(cluster, "send-code-gen-");

//        String regex = "@{var,createSiteCode}-@{var,receiveSiteCode}-@{var,currentTimeLong}u:leftPad(sequence,7)";
//        String regex = "createSiteCode + '-' + receiveSiteCode + '-' + currentTimeLong + u:leftPad(sequence,7)";


        String regex = "createSiteCode + '-' + receiveSiteCode + '-' + currentTimeLong + u:leftPad(sequence,6) + serial:getSendCodeMod(createSiteCode,receiveSiteCode,currentTimeLong,sequence)";

        JimdbSequenceGen jimdbSequenceGen = new JimdbSequenceGen(cluster,"send-code-gen-%s");
        JimdbSequenceGen jimdbSequenceGen1 = new JimdbSequenceGen(cluster,"send-code-gen-%s");
        List<SequenceGen> sequenceGens = new ArrayList<>();
        sequenceGens.add(jimdbSequenceGen);
        sequenceGens.add(jimdbSequenceGen1);
//        RowBasedSequenceGen rowBasedSequenceGen = new RowBasedSequenceGen();
//        rowBasedSequenceGen.setDataSource(new BasicDataSource());
//        rowBasedSequenceGen.setAutoCreateOwnerKey(true);
//        rowBasedSequenceGen.setSequenceTableFormat("sequence_0");
//        sequenceGens.add(rowBasedSequenceGen);
        BitwiseLoadBalanceSequenceGen bitwiseLoadBalanceSequenceGen = new BitwiseLoadBalanceSequenceGen(sequenceGens);
        bitwiseLoadBalanceSequenceGen.setTimeoutThresholdInMilliseconds(100);
        bitwiseLoadBalanceSequenceGen.setTimeoutEventCountThreshold(3);
        bitwiseLoadBalanceSequenceGen.setOnErrorRescueThresholdInSeconds(10);
        QueuedSequenceGen queuedSequenceGen = new QueuedSequenceGen(bitwiseLoadBalanceSequenceGen);
        queuedSequenceGen.setMemoryBitLength(4);
        BitSpacePaddingSequenceGen bitSpacePaddingSequenceGen = new BitSpacePaddingSequenceGen(queuedSequenceGen);
        DateTimeBasedSequenceGen dateTimeBasedSequenceGen = new DateTimeBasedSequenceGen(bitSpacePaddingSequenceGen,"yyyyMMddHH", 6);
        LoopCycleSequenceGen loopCycleSequenceGen = new LoopCycleSequenceGen(dateTimeBasedSequenceGen,1, 999999);

        Map<String,Object> namespace = new HashMap<String, Object>();
        namespace.put("serial","com.jd.bluedragon.utils.SerialRuleUtil");
        JexlInterpreter jexlInterpreter = new JexlInterpreter(loopCycleSequenceGen, namespace);

//        SmartInterpreter smartInterpreter = new SmartInterpreter();
        smartSendCodeSNGen = new SmartSNGen(regex,jimDBUniqueStrategy);
        smartSendCodeSNGen.setInterpreter(jexlInterpreter);

    }

    @Test
    public void sendCodeGen() {
        GenContextItem[] genContextItems = new GenContextItem[3];
        genContextItems[0] = GenContextItem.create("createSiteCode", 910);
        genContextItems[1] = GenContextItem.create("receiveSiteCode", 364605);
        genContextItems[2] = GenContextItem.create("currentTimeLong", DateHelper.formatDate(new Date(),"yyyyMMddHH"));
        String[] sendCodes = smartSendCodeSNGen.batchGen("SENDCODE",genContextItems, 1000);
//        smartSendCodeSNGen.gen("SENDCODE",genContextItems);
        for (String sendCode : sendCodes) {
            System.out.println(sendCode);
            if (!BusinessUtil.isSingleBatchNo(sendCode)) {
                throw  new RuntimeException("批次号生成规则异常");
            }
        }

    }

    @Test
    public void idGen() {
        PropertiesFileSnowflakeSequenceGen snowflakeSequenceGen = new PropertiesFileSnowflakeSequenceGen("/configured/snowflake.properties");
        int i = 0;
        while (i++ < 10000) {
            System.out.println(snowflakeSequenceGen.gen("BUSINESS_CODE"));
        }
    }

    @Test
    public void mod() {
        System.out.println(String.format("%02d", 5655 % 7));
    }

}
