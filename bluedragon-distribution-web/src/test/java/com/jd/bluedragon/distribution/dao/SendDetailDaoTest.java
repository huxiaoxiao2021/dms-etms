package com.jd.bluedragon.distribution.dao;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.StringHelper;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by dudong on 2014/11/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/distribution-web-context.xml"})
public class SendDetailDaoTest {

    private Log log  = LogFactory.getLog(this.getClass());

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Test
    public void testRedisExpire(){
        String key = "key1111111111";
        Long result = redisManager.lpush(key,"1111");
        try{
            Assert.assertEquals(redisManager.lrange(key,0,-1).size(),1);
            redisManager.expire(key, 5);
            Thread.sleep(10 * 1000);
            Assert.assertEquals(redisManager.lrange(key,0,-1).size(),0);
        } catch(Exception ex){

        }

    }

    @Test
    public void testGetPacksByWaybill(){
        String waybills = "000000002763-1-1-\n" +
                "000000003124-1-3-\n" +
                "000000017776-1-1-\n" +
                "000000017861-1-1-\n" +
                "000000018126-1-4-\n" +
                "000000018126-3-4-\n" +
                "000000018255-1-13-\n" +
                "000000020790-5-5-\n" +
                "311001003N1S3H22\n" +
                "654844487-1-2-\n" +
                "170003978N3S3H11\n" +
                "171099620-2-3-\n" +
                "201206274N1S1H22\n" +
                "170001131N2S2H33\n" +
                "90400950963702-1-1-\n" +
                "90400950963945-1-1-\n" +
                "170371502-1-3-\n" +
                "170171648-16-22-\n";
        for(String waybill : waybills.split("\n")){
            List<SendDetail> sendDetails = sendDatailDao.queryWaybillsByPackCode(waybill);
            log.info(StringHelper.join(sendDetails, "getWaybillCode", ","));
        }
    }

    @Test
    public void testGetPacksByBox(){
        String boxs = "BC010F001010F00200002008\n" +
                "BC010F001010F00200003002\n" +
                "BC010F001010F00200003003\n" +
                "BC010F511010Y04200000110\n" +
                "BC010F511010Y04200000127\n" +
                "BC010F511010Y04200000172\n" +
                "BC010F511010Y04200000190\n" +
                "BC010F511010Y04200000195\n" +
                "BC010F511010Y04200000197\n" +
                "BC010F511010Y04200000216\n" +
                "BC010F511010Y04200000218\n" +
                "BC010F511010Y04200000222\n" +
                "BC010F511010Y04200000232\n" +
                "BC010F511010Y04200000564\n" +
                "BC010F511010Y04200000566\n" +
                "BC010F511010Y04200000571\n" +
                "BC010F511010Y04200000572\n" +
                "BC010F511010Y04200000574\n" +
                "BC010F511010Y04200000578\n" +
                "BC010F511010Y04200000581\n" +
                "BC010F511010Y04200000587\n" +
                "BC010F511010Y04200000590\n" +
                "BC010F511010Y04200000596\n" +
                "BC010F511010Y04200000606\n" +
                "BC010F511010Y04200000634";

        for(String box : boxs.split("\n")){
            List<SendDetail> sendDetails = sendDatailDao.queryWaybillsByBoxCode(box);
            log.info(StringHelper.join(sendDetails, "getWaybillCode", ","));
        }
    }


    @Test
    public void testGetPacksBySendCode(){
        String sendCodes = "1609-480-201304061418020\n" +
                "511201203280756360\n" +
                "511201203300447000\n" +
                "511201203300458240\n" +
                "1006-39-20120405064836236236\n" +
                "511-39-20120406161228231\n" +
                "392012040511251218\n" +
                "1006-511-20120406140628654\n" +
                "511-10089-20120411172327000\n" +
                "1006-909-20120412151900909\n" +
                "1006-39-20120717141504509\n" +
                "511-20400-20120720031130640\n" +
                "1006-39-20120720054450173\n" +
                "1006-20400-20120709043946929\n" +
                "1006-20400-20120709050354850\n" +
                "1006-1-20120720060249765\n" +
                "511-20400-20120716020405221\n" +
                "511-20400-20120716070034481\n" +
                "1006-161-20120627034140291\n" +
                "1006-161-20120627051519644\n" +
                "1006-10089-20120806084438248\n" +
                "1006-39-20120616140006158\n" +
                "1006-39-20120813070439286\n" +
                "1006-39-20120804100112379\n" +
                "910-1029-201208150340020\n" +
                "1006-1-20120816143158722\n" +
                "1006-1-20120816144638846\n" +
                "1006-39-20120712150235081\n" +
                "511-20400-2012071004103348";

        for(String sc : sendCodes.split("\n")){
            List<SendDetail> sendDetails = sendDatailDao.queryWaybillsBySendCode(sc);
            log.info(StringHelper.join(sendDetails, "getWaybillCode", ","));
        }
    }

    @Test
    public void testGetPacksByDepartureID(){
        String departures = "2093\n" +
                "6892\n" +
                "6809\n" +
                "7362\n" +
                "7351\n" +
                "7348\n" +
                "6857\n" +
                "6858\n" +
                "6597\n" +
                "6815\n" +
                "6787\n" +
                "6831\n" +
                "7981\n" +
                "8111\n" +
                "7917\n" +
                "7967\n" +
                "8072\n" +
                "7626\n" +
                "2090\n" +
                "6784\n" +
                "7108\n" +
                "6891\n" +
                "6612\n" +
                "6929";

        for(String dr : departures.split("\n")){
            List<String> sendCodess = sendDatailDao.querySendCodesByDepartID(Long.parseLong(dr));
            List<SendDetail> sendDetails=null;
            if (sendCodess != null && sendCodess.size() > 0) {
                List<SendDetail> temp = sendDatailDao.queryWaybillsBySendCode(sendCodess.get(0));
                if(temp!=null&&temp.size()>0){
                    sendDetails.addAll(temp);
                }
            }
            log.info(StringHelper.join(sendDetails,"getWaybillCode",","));
        }
    }
}
