package com.jd.bluedragon.distribution.test.departure;

import com.jd.bluedragon.distribution.api.request.DepartureTmpRequest;
import com.jd.bluedragon.distribution.api.response.DepartureTmpResponse;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Random;

/**
 * Created by dudong on 2015/1/12.
 */
public class DepartureTmpTest {
    private static final String SEED = "0123456789";
    public static void main(String args[]) {
        testDepartureTmp();
    }

    public static void testDepartureTmp(){
        RestTemplate template = new RestTemplate();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String url = "http://localhost:8081/web/services/departure/departuresendtemp";
            template.postForEntity(url, generateTmp(), Object.class);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - startTime) / 1000);
    }

    public static DepartureTmpRequest generateTmp(){
        DepartureTmpRequest request = new DepartureTmpRequest();
        request.setBatchCode(geneCode(24));
        request.setCreateTime(new Date());
        request.setSendCode(geneCode(20));
        request.setOperateTime(new Date());
        return request;
    }

    public static String geneCode(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(SEED.charAt(new Random().nextInt(SEED.length() - 1)));
        }
        return sb.toString();
    }

}
