package com.jd.bluedragon.distribution.carSchedule.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.carSchedule.domain.SendCodeToCarCode;

import java.util.List;

/**
 * 保存发车条码和批次号之间的映射关系
 * Created by wuzuxiang on 2017/3/13.
 */
public class SendCodeToCarNoDao extends BaseDao<SendCodeToCarCode> {

    private static final String NAMESPACE = SendCodeToCarNoDao.class.getName();

    /**
     * 发车记录批次号和发车条码之间的联系
     */
    public Integer add(SendCodeToCarCode domain){
        return super.add(NAMESPACE,domain);
    }

    public Integer cancelSendCar(SendCodeToCarCode domain){
        return super.getSqlSession().update(NAMESPACE + ".cancelSendCar" , domain);
    }

    public List<String> sendCodeBySendCarCode(String sendCarCode){
        return super.getSqlSession().selectList(SendCodeToCarNoDao.NAMESPACE + ".sendCodeBySendCarCode" ,sendCarCode);
    }

}
