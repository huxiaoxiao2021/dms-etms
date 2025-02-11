package com.jd.bluedragon.distribution.reverse.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;

public class ReverseReceiveDao extends BaseDao<ReverseReceive> {

    public static final String namespace = ReverseReceiveDao.class.getName();

    public ReverseReceive findByOrderId(Long orderId) {
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByOrderId", orderId);
    }

    public ReverseReceive findByPackageCode(String packageCode) {
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByPackageCode", packageCode);
    }

    public ReverseReceive findByPackageCodeAndSendCode(String packageCode, String sendCode, Integer businessType) {
        ReverseReceive reverseReceive = new ReverseReceive();
        reverseReceive.setPackageCode(packageCode);
        reverseReceive.setSendCode(sendCode);
        reverseReceive.setReceiveType(businessType);
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByPackageCodeAndSendCode", reverseReceive);
    }

    public ReverseReceive findByWaybillCodeAndSendCode(String waybillCode, String sendCode) {
        ReverseReceive reverseReceive = new ReverseReceive();
        reverseReceive.setPackageCode(waybillCode);
        reverseReceive.setSendCode(sendCode);
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findByWaybillCodeAndSendCode", reverseReceive);
    }

    /**
     * 获得一个ReverseReceive对象
     *
     * @param packageCode  运单号
     * @param canReceive   大库、备件库收货标签0 拒收 1收货
     * @param businessType 操作单位类型 1大库 3备件库 2售后
     * @param sendCode     分拣逆向发货批次号
     * @param operatorId   大库、备件库操作拒收、收货的人
     * @param rejectCode   拒收编码，标识是什么原因大库、备件库拒收
     * @return
     */
    public ReverseReceive findOneReverseReceive(String packageCode, Integer canReceive, Integer businessType,
                                                String sendCode, String operatorId, Integer rejectCode) {
        ReverseReceive reverseReceive = new ReverseReceive();

        reverseReceive.setPackageCode(packageCode);
        reverseReceive.setCanReceive(canReceive);
        reverseReceive.setReceiveType(businessType);
        reverseReceive.setSendCode(sendCode);
        reverseReceive.setOperatorId(operatorId);
        reverseReceive.setRejectCode(rejectCode);
        return (ReverseReceive) super.getSqlSession()
                .selectOne(ReverseReceiveDao.namespace + ".findOneReverseReceive", reverseReceive);
    }

    /**
     * 查询维修外单
     * <p/>
     * 没有复用当前类的findByPackageCodeAndSendCode()方法,是因为:<br>
     * canReceive以前只有两个值,0/1,0表示拒收,1表示收货,两个值是平级关系,只会有一个值有效,所以数据库只会有一条数据;<br>
     * 但维修外单类型有新的值,2,表示交接,发生在收货之前,那么数据库会出现两条数据,所以查询条件加了新的字段,用于确定唯一数据.
     *
     * @param packageCode
     * @param sendCode
     * @param businessType
     * @param canReceive
     * @return
     */
    public ReverseReceive findMCS(String packageCode, String sendCode, Integer businessType, Integer canReceive) {
        ReverseReceive reverseReceive = new ReverseReceive();
        reverseReceive.setPackageCode(packageCode);
        reverseReceive.setSendCode(sendCode);
        reverseReceive.setReceiveType(businessType);
        reverseReceive.setCanReceive(canReceive);
        return (ReverseReceive) super.getSqlSession().selectOne(
                ReverseReceiveDao.namespace + ".findMCS", reverseReceive);
    }
}
