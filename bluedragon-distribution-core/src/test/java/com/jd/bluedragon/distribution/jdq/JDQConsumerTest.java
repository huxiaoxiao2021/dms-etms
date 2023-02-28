package com.jd.bluedragon.distribution.jdq;

import com.jd.bdp.jdw.avro.JdwData;
import com.jd.bluedragon.distribution.jdq4.consume.BoardChuteConsumer;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/10/16 15:30
 */
@RunWith(MockitoJUnitRunner.class)
public class JDQConsumerTest {

    @InjectMocks
    private BoardChuteConsumer boardChuteConsumer;



    @Test
    public  void test11(){

        Map<CharSequence, CharSequence> ss1 = new HashMap<>();
        Map<CharSequence, CharSequence> ss2 = new HashMap<>();
        ss1.put("p_ts","1677575576466");
        ss1.put("ft","1677575576466");
        ss1.put("ip","10.170.77.54:3057527");

        ss2.put("create_erp","wangmengmeng38");
        ss2.put("create_time","2023-02-28 17:12:56");
        ss2.put("end_type",null);
        ss2.put("end_erp",null);
        ss2.put("first_barcode","JD0098723831702-1-1-");
        ss2.put("end_time",null);
        ss2.put("board_code","B23022800219768");
        ss2.put("create_site_code","1357562");
        ss2.put("machine_code","ZZWL-FJJ-JL-003");
        ss2.put("package_count","1");
        ss2.put("last_barcode",null);
        ss2.put("create_site_name","郑州前程散货分拣中心");
        ss2.put("send_code","");
        ss2.put("chute_code","3");
        ss2.put("receive_site_name","1");
        ss2.put("status","0");

        JdwData jd = new JdwData();
        jd.setMid(200002607159L);
        jd.setDb("bd_dmsauto_dev");
        jd.setSch("bd_dmsauto_dev");
        jd.setTab("board_chute");
        jd.setOpt("INSERT");
        jd.setTs(1677575576000L);
        jd.setDdl(null);
        jd.setErr(null);
        jd.setSrc(new HashMap<>());
        jd.setCus(ss1);
        jd.setCur(ss2);

        ConsumerRecord<String, JdwData> t = new ConsumerRecord<String, JdwData>("jrdw-bz-bd_dmsauto_dev-board_chute",1,1,"1",jd);
        boardChuteConsumer.onMessage(t);
    }
}
    
