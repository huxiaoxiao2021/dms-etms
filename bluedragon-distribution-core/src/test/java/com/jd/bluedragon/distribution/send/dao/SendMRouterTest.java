package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.router.SendMRouter;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtingwei on 2016/9/27.
 */
public class SendMRouterTest extends AbstractDaoIntegrationTest {
    @Autowired
    private SendMRouter sendMRouter;

    @Autowired
    private KvIndexDao kvIndexDao;

//    @Test
//    public void testAdd() {
//        List<Integer> createSiteCode=new ArrayList<Integer>();
//        createSiteCode.add(Integer.valueOf(908));
//        createSiteCode.add(Integer.valueOf(910));
//        createSiteCode.add(Integer.valueOf(2015));
//        createSiteCode.add(Integer.valueOf(1609));
//        createSiteCode.add(Integer.valueOf(1610));
//        SendM domain=new SendM();
//        domain.setBoxCode("BC010F005027F00200000012");
//        for (Integer item :createSiteCode){
//            domain.setCreateSiteCode(item);
//            sendMRouter.add(StringUtils.EMPTY, domain);
//        }
//        List<Integer> list= kvIndexDao.queryCreateSiteCodesByKey("BC010F005027F00200000012");
//        Integer result=0;
//        for (Integer item:list){
//            for (Integer value:createSiteCode){
//                if(item.equals(value)){
//                    ++result;
//                }
//            }
//
//        }
//        Assert.assertEquals(result.intValue(),createSiteCode.size());
//    }


    //    @Override
//    public SendM get(String namespace, Long pk) {
//        return super.get(namespace, pk);
//    }
//
//    @Override
//    public Integer update(String namespace, SendM entity) {
//        return super.update(namespace, entity);
//    }
//
//    @Override
//    public List<SendM> selectBoxBySendCode(String sendCode) {
//        return super.selectBoxBySendCode(sendCode);
//    }
//
//    @Override
//    public List<String> batchQueryCancelSendMList(SendM sendM) {
//        return super.batchQueryCancelSendMList(sendM);
//    }
//
//    @Override
//    public List<String> batchQuerySendMList(SendM sendM) {
//        return super.batchQuerySendMList(sendM);
//    }
//
    @Test
    public void testAddBatch() {
        List<SendM> list=new ArrayList<SendM>();
        SendM domain=new SendM();
        domain.setBoxCode("BC010F001010F00300000008");
        domain.setCreateSiteCode(2015);
        list.add(domain);
        sendMRouter.addBatch(list);
    }
//
//    @Override
//    public List<SendM> querySendCodesByDepartue(Long shieldsCarId) {
//        return super.querySendCodesByDepartue(shieldsCarId);
//    }
//
//    @Override
//    public boolean checkSendByBox(SendM sendM) {
//        return super.checkSendByBox(sendM);
//    }
//
//    @Override
//    public boolean cancelSendM(SendM tSendM) {
//        return super.cancelSendM(tSendM);
//    }
//
    @Test
    public void testFindSendMByBoxCode2() {
        List<Integer> createSiteCode = new ArrayList<Integer>();
        createSiteCode.add(Integer.valueOf(908));
        createSiteCode.add(Integer.valueOf(910));
        createSiteCode.add(Integer.valueOf(2015));
        createSiteCode.add(Integer.valueOf(1609));
        createSiteCode.add(Integer.valueOf(1610));
        SendM domain = new SendM();
        domain.setYn(1);
        domain.setBoxCode("BC010F005027F00212341413");
        for (Integer item : createSiteCode) {
            domain.setCreateSiteCode(item);
            sendMRouter.insertSendM(domain);
        }
        SendM sendM=new SendM();
        sendM.setBoxCode(domain.getBoxCode());
        List<SendM> result= sendMRouter.findSendMByBoxCode2(sendM);
        Assert.assertEquals(result.size(),createSiteCode.size());
    }

    @Test
    public void testFindSendMByBoxCode( ) {
        List<Integer> createSiteCode = new ArrayList<Integer>();
        createSiteCode.add(Integer.valueOf(908));
        createSiteCode.add(Integer.valueOf(910));
        createSiteCode.add(Integer.valueOf(2015));
        createSiteCode.add(Integer.valueOf(1609));
        createSiteCode.add(Integer.valueOf(1610));
        createSiteCode.add(Integer.valueOf(908));
        createSiteCode.add(Integer.valueOf(910));
        createSiteCode.add(Integer.valueOf(2015));
        createSiteCode.add(Integer.valueOf(1609));
        createSiteCode.add(Integer.valueOf(1610));
        SendM domain = new SendM();
        domain.setYn(1);
        domain.setBoxCode("BC010F005027F04422341413");
        for (Integer item : createSiteCode) {
            domain.setCreateSiteCode(item);
            sendMRouter.insertSendM(domain);
        }
        SendM sendM=new SendM();
        sendM.setBoxCode(domain.getBoxCode());
        List<SendM> result= sendMRouter.findSendMByBoxCode(sendM);
        Assert.assertEquals(result.size(),createSiteCode.size());
    }

    @Test
    public void testInsertSendM() {
        List<Integer> createSiteCode = new ArrayList<Integer>();
        createSiteCode.add(Integer.valueOf(908));
        createSiteCode.add(Integer.valueOf(910));
        createSiteCode.add(Integer.valueOf(2015));
        createSiteCode.add(Integer.valueOf(1609));
        createSiteCode.add(Integer.valueOf(1610));
        SendM domain = new SendM();
        domain.setBoxCode("BC010F005027F00200123413");
        for (Integer item : createSiteCode) {
            domain.setCreateSiteCode(item);
            sendMRouter.insertSendM(domain);
        }
        List<Integer> list = kvIndexDao.queryCreateSiteCodesByKey("BC010F005027F00200123413");
        Integer result = 0;
        for (Integer item : list) {
            for (Integer value : createSiteCode) {
                if (item.equals(value)) {
                    ++result;
                }
            }

        }
        Assert.assertEquals(result.intValue(), createSiteCode.size());
    }

    @Test
    public void testInsertSendM2() {
        List<Integer> createSiteCode = new ArrayList<Integer>();
        createSiteCode.add(Integer.valueOf(908));
        createSiteCode.add(Integer.valueOf(910));
        createSiteCode.add(Integer.valueOf(2015));
        createSiteCode.add(Integer.valueOf(1609));
        createSiteCode.add(Integer.valueOf(1610));
        SendM domain = new SendM();
        domain.setBoxCode("   BC010F005027F002000000120000000000000  ");
        for (Integer item : createSiteCode) {
            domain.setCreateSiteCode(item);
            sendMRouter.insertSendM(domain);
        }
        List<Integer> list = kvIndexDao.queryCreateSiteCodesByKey("        BC010F005027F002000000120000000000000  ");
        Integer result = 0;
        for (Integer item : list) {
            for (Integer value : createSiteCode) {
                if (item.equals(value)) {
                    ++result;
                }
            }

        }
        Assert.assertEquals(result.intValue(), createSiteCode.size());
    }
//
//    @Override
//    public int updateBySendCodeSelective(SendM record) {
//        return super.updateBySendCodeSelective(record);
//    }
//
//    @Override
//    public List<SendM> selectBySendSiteCode(SendM sendM) {
//        return super.selectBySendSiteCode(sendM);
//    }
//
//    @Override
//    public SendM selectBySendCode(String sendCode) {
//        return super.selectBySendCode(sendCode);
//    }
//
//    @Override
//    public List<SendM> selectBySiteAndSendCodeBYtime(Integer createSiteCode, String sendCode) {
//        return super.selectBySiteAndSendCodeBYtime(createSiteCode, sendCode);
//    }
//
//    @Override
//    public List<SendM> selectBySiteAndSendCode(Integer createSiteCode, String sendCode) {
//        return super.selectBySiteAndSendCode(createSiteCode, sendCode);
//    }
//
//    @Override
//    public List<SendM> selectOneBySendCode(String sendCode) {
//        return super.selectOneBySendCode(sendCode);
//    }
//
//    @Override
//    public SendM selectOneBySiteAndSendCode(Integer createSiteCode, String sendCode) {
//        return super.selectOneBySiteAndSendCode(createSiteCode, sendCode);
//    }

}